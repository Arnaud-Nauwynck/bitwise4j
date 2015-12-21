package fr.an.util.encoder.huffman;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import fr.an.util.bits.BitInputStream;
import fr.an.util.bits.BitOutputStream;
import fr.an.util.encoder.structio.BitStreamStructDataOutput;
import fr.an.util.encoder.structio.Value2StructDataOutputWriter;

/**
 * 
 */
public class HuffmanTable<T> {
	
	private Map<T,HuffmanTreeLeaf<T>> symbolLeafMap = new LinkedHashMap<T,HuffmanTreeLeaf<T>>();
	
	/**
	 * intermediate nodes added to build huffman tree
	 */
	private List<HuffmanTreeNode<T>> mergedNodes = new ArrayList<HuffmanTreeNode<T>>();

	// ------------------------------------------------------------------------

	public HuffmanTable() {
	}
	
	public HuffmanTable(SymbolsCounter<T> symbolCounts) {
	    addSymbols(symbolCounts);
	    compute();
    }
	
	public HuffmanTable(Map<T,Integer> symbolCounts) {
        addSymbols(symbolCounts);
        compute();
    }
    	
	// ------------------------------------------------------------------------

	public int getSymbolCount() {
		return symbolLeafMap.size();
	}
	

    public static class MutableInt {
        public int value;
    }
    
    public static class SymbolsCounter<T> {
        private Map<T,MutableInt> res = new HashMap<T,MutableInt>();
        
        public void incr(T symbol) {
            MutableInt count = res.get(symbol);
            if (count == null) {
                count = new MutableInt();
                res.put(symbol, count);
            }
            count.value++;
        }
        
        public Map<T,MutableInt> getRes() {
            return res;
        }
    }
    
	public HuffmanTreeLeaf<T> addSymbol(T symbol, int freq) {
		int seqNumber = symbolLeafMap.size();
		HuffmanTreeLeaf<T> p = new HuffmanTreeLeaf<T>(this, freq, seqNumber, symbol);
		symbolLeafMap.put(symbol, p);
		return p;
	}

	public void addSymbols(SymbolsCounter<T> symbolCounts) {
	    addSymbolsMapMutableInt(symbolCounts.getRes());
	}
	
    public void addSymbolsMapMutableInt(Map<T, MutableInt> symbolCounts) {
        for(Map.Entry<T,MutableInt> e : symbolCounts.entrySet()) {
            addSymbol(e.getKey(), e.getValue().value);
        }
    }

    public void addSymbols(Map<T, Integer> symbolCounts) {
        for(Map.Entry<T,Integer> e : symbolCounts.entrySet()) {
            addSymbol(e.getKey(), e.getValue());
        }
    }
    
	public HuffmanTreeLeaf<T> getSymbolLeaf(T symbol) {
		return symbolLeafMap.get(symbol);
	}

	public Collection<HuffmanTreeLeaf<T>> getSymbolLeafs() {
        return symbolLeafMap.values();
	}

	public HuffmanBitsCode getSymbolCode(T symbol) {
	    HuffmanTreeLeaf<T> leaf = getSymbolLeaf(symbol);
	    if (leaf == null) {
	        return null;
	    }
	    return leaf.getResultCode();
	}

	public void writeEncodeSymbol(BitOutputStream out, T symbol) {
	    HuffmanBitsCode code = getSymbolCode(symbol);
	    code.writeCodeTo(out);
	}

	// idem, using BitStreamStructDataOutput
	public void writeEncodeSymbol(BitStreamStructDataOutput out, T symbol) {
        HuffmanBitsCode code = getSymbolCode(symbol);
        code.writeCodeTo(out);
    }

	public T readDecodeSymbol(BitInputStream in) {
	    T res;
	    HuffmanTreeNode<T> node = getRootNode();
	    for(;;) {
	        boolean bit = in.readBit();
	        AbstractHuffmanNode<T> childNode = node.getChildLeftRight(bit);
	        if (childNode instanceof HuffmanTreeLeaf) {
	            res = ((HuffmanTreeLeaf<T>) childNode).getSymbol();
	            break;
	        } else {
	            node = (HuffmanTreeNode<T>) childNode;
	        }
	    }
	    return res;
	}
	
	public void compute() {
		mergedNodes.clear();
		Queue<AbstractHuffmanNode<T>> queue = new PriorityQueue<AbstractHuffmanNode<T>>(symbolLeafMap.size());
		queue.addAll(symbolLeafMap.values());
		
		int seqNumberGenerator = symbolLeafMap.size();
		while(queue.size() >= 2) {
		    AbstractHuffmanNode<T> left = queue.poll();
		    AbstractHuffmanNode<T> right = queue.poll();
			int seqNumber = seqNumberGenerator++;
			HuffmanTreeNode<T> newNode = new HuffmanTreeNode<T>(this, seqNumber, left, right);
			queue.add(newNode);
			mergedNodes.add(newNode);
		}
		
		assignCodes();
	}
	
	private void assignCodes() {
		HuffmanTreeNode<T> root = getRootNode();
		HuffmanBitsCode rootCode = HuffmanBitsCode.getRootEmptyCode();
		root.setResultCode(rootCode);
		
		root.accept(new HuffmanNodeVisitor<T>() {
			public void caseNode(HuffmanTreeNode<T> p) {
				HuffmanBitsCode code = p.getResultCode();
				HuffmanBitsCode leftCode = HuffmanBitsCode.childLeftCode(code);
				HuffmanBitsCode rightCode = HuffmanBitsCode.childRightCode(code);
				p.getLeft().setResultCode(leftCode);
				p.getRight().setResultCode(rightCode);
				p.visitChildren(this);
			}
			public void caseLeaf(HuffmanTreeLeaf<T> p) {
				// do nothing
			}
		});
	}

	public HuffmanTreeNode<T> getRootNode() {
		return mergedNodes.get(mergedNodes.size() - 1);
	}

    public int getMaxCodeBitLen() {
        int res = 0;
        for(HuffmanTreeLeaf<T> node : symbolLeafMap.values()) {
            res = Math.max(res, node.getResultCode().getBitsCount());
        }
        return res;
    }

    
    public void writeEncode(BitStreamStructDataOutput bitsStructOutput, final int maxSymbolCount,
            Value2StructDataOutputWriter<T> symbolWriter) {
        bitsStructOutput.writeUIntLt16ElseMax(maxSymbolCount, getSymbolCount());
        final int maxCodeBitLen = getMaxCodeBitLen();
        bitsStructOutput.writeIntMinMax(1, getSymbolCount(), maxCodeBitLen);
        for (HuffmanTreeLeaf<T> symbolNode : symbolLeafMap.values()) {
            HuffmanBitsCode symbolCode = symbolNode.getResultCode();
            int symbolBitsCount = symbolCode.getBitsCount();
            bitsStructOutput.writeIntMinMax(1, maxCodeBitLen, symbolBitsCount);
            bitsStructOutput.writeNBits(symbolBitsCount, symbolCode.getBits());
            symbolWriter.writeTo(bitsStructOutput, symbolNode.getSymbol());
        }
    }

}
