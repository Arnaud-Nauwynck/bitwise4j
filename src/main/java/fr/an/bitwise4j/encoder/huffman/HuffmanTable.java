package fr.an.bitwise4j.encoder.huffman;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;

import fr.an.bitwise4j.bits.BitInputStream;
import fr.an.bitwise4j.bits.BitOutputStream;
import fr.an.bitwise4j.encoder.structio.StructDataOutput;
import fr.an.bitwise4j.encoder.structio.Value2StructDataOutputWriter;

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

    public void clear() {
        symbolLeafMap.clear();
        mergedNodes.clear();
    }

	// ------------------------------------------------------------------------

	public int getSymbolCount() {
		return symbolLeafMap.size();
	}
	
	public Set<T> getSymbols() {
	    Set<T> res = new HashSet<T>();
	    for(HuffmanTreeLeaf<T> node : symbolLeafMap.values()) {
	        res.add(node.getSymbol());
	    }
	    return res;
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

    public HuffmanTreeLeaf<T> incrSymbolFreq(T symbol, int freq) {
        HuffmanTreeLeaf<T> leaf = symbolLeafMap.get(symbol);
        if (leaf == null) {
            int seqNumber = symbolLeafMap.size();
            leaf = new HuffmanTreeLeaf<T>(this, 0, seqNumber, symbol);
            symbolLeafMap.put(symbol, leaf);
        }
        leaf.frequency += freq;
        return leaf;
    }
    
	public HuffmanTreeLeaf<T> addSymbol(T symbol, int freq) {
	    return incrSymbolFreq(symbol, freq);
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

	// idem, using StructDataOutput  (useless as StructDataOutput now extends from BitOutputStream) 
	public void writeEncodeSymbol(StructDataOutput out, T symbol) {
        HuffmanBitsCode code = getSymbolCode(symbol);
        out.writeHuffmanCode(code);
    }

	// cf StructDataInput instead ... 
	public T readBitsDecodeSymbol(BitInputStream in) {
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

   public T readDecodeSymbol(boolean[] bits) {
        T res;
        HuffmanTreeNode<T> node = getRootNode();
        int i = 0;
        for(;;) {
            boolean bit = bits[i++];
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
	    if (symbolLeafMap.size() <= 1) {
	        // degenerated table
	        if (! symbolLeafMap.isEmpty()) {
	            HuffmanTreeLeaf<T> uniqueLeaf = symbolLeafMap.values().iterator().next();
	            uniqueLeaf.resultCode = HuffmanBitsCode.getRootEmptyCode(); 
	        }
	        return;
	    }
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

    
    public void writeEncode(StructDataOutput bitsStructOutput, final int maxSymbolCount,
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

    public void dump(StringBuilder sb, Function<T,String> symbolToString) {
        sb.append("HuffmanTable [symbolsCount=" + symbolLeafMap.size() + "\n");
        int maxSymbolFreq = 0;
        HuffmanTreeLeaf<T> mostFrequentLeaf = null;
        for(HuffmanTreeLeaf<T> p : symbolLeafMap.values()) {
            if (p.getFrequency() > maxSymbolFreq) {
                maxSymbolFreq = p.getFrequency();
                mostFrequentLeaf = p;
            }
        }
        sb.append("most frequent symbol: '" + symbolToString.apply(mostFrequentLeaf.getSymbol()) + "' " + mostFrequentLeaf.getResultCode().toString() + " freq:" + mostFrequentLeaf.getFrequency() + "\n");
        
        getRootNode().accept(new HuffmanNodeVisitor<T>() {
            private int indent = 0;
            public void caseNode(HuffmanTreeNode<T> p) {
                printIndent(sb, indent);
                sb.append("intermediateNode: " + p.getResultCode().toString() + " freq:" + p.getFrequency() + "\n");
                indent++;
                p.visitChildren(this);
                indent--;
            }
            public void caseLeaf(HuffmanTreeLeaf<T> p) {
                printIndent(sb, indent);
                sb.append("Node: '" + symbolToString.apply(p.getSymbol()) + "' " + p.getResultCode().toString() + " freq:" + p.getFrequency() + "\n");
            }
        });
        sb.append("]");
    }

    public String toStringDump(Function<T,String> symbolToString) {
        final StringBuilder sb = new StringBuilder();
        dump(sb, symbolToString);
        return sb.toString();        
    }
    
    @Override
    public String toString() {
        return toStringDump(x -> x.toString());
    }

    private static void printIndent(StringBuilder sb, int indent) {
        for(int i = 0; i < indent; i++) {
            sb.append(' ');
        }
    }
    
    
}
