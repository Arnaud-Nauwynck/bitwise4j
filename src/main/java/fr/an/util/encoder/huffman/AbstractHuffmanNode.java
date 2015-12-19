package fr.an.util.encoder.huffman;


/**
 * 
 */
public abstract class AbstractHuffmanNode<T> implements Comparable<AbstractHuffmanNode<T>> {

	private HuffmanTable<T> owner;
	
	protected AbstractHuffmanNode<T> parent;
	
	protected int frequency;

	protected int insertSequenceNumber;
	
	protected HuffmanBitsCode resultCode;

	// ------------------------------------------------------------------------

	public AbstractHuffmanNode(HuffmanTable<T> owner, int frequency, int insertSequenceNumber) {
		this.owner = owner;
		this.frequency = frequency;
		this.insertSequenceNumber = insertSequenceNumber;
	}
	
	// ------------------------------------------------------------------------

	/** Visitor design pattern */
	public abstract void accept(HuffmanNodeVisitor<T> v);

	public void visitParent(HuffmanNodeVisitor<T> v) {
		if (parent != null) {
			parent.accept(v);
		}
	}

	public abstract boolean isLeaf();
	
	public HuffmanTable<T> getOwner() {
		return owner;
	}

	public AbstractHuffmanNode<T> getParent() {
		return parent;
	}

	/*pp*/ void setParent(HuffmanTreeNode<T> p) {
		this.parent = p;
	}

	public int getFrequency() {
		return frequency;
	}

	public HuffmanBitsCode getResultCode() {
		return resultCode;
	}

	/*pp*/ void setResultCode(HuffmanBitsCode p) {
		this.resultCode = p;
	}

	public int getSequenceNumber() {
		return insertSequenceNumber;
	}
	
	// ------------------------------------------------------------------------

	/** implements Comparable */
	public int compareTo(AbstractHuffmanNode<T> p) {
		int res;
		int p_frequency = p.frequency;
		if (frequency < p_frequency) {
			res = -1;
		} else if (frequency > p_frequency) {
			res = +1;
		} else {
			int p_insertSequenceNumber = p.insertSequenceNumber;
			if (insertSequenceNumber < p_insertSequenceNumber) {
				res = -1;
			} else if (insertSequenceNumber > p_insertSequenceNumber) {
			    res = +1;
			} else {
				res = 0; // should not occur, except for comparing to itself! (insertSequenceNumber is uniq)
			}
		}
		return res;
	}
}
