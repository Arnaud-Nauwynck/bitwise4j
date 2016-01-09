package fr.an.bitwise4j.encoder.huffman;

/**
 * 
 */
public class HuffmanTreeNode<T> extends AbstractHuffmanNode<T> {

	private AbstractHuffmanNode<T> left;
	private AbstractHuffmanNode<T> right;
	
	// ------------------------------------------------------------------------

	public HuffmanTreeNode(HuffmanTable<T> owner, int seqNumber, AbstractHuffmanNode<T> left, AbstractHuffmanNode<T> right) {
		super(owner, left.frequency + right.frequency, seqNumber);
		this.left = left;
		this.right = right;
		
		left.setParent(this);
		right.setParent(this);
	}
	
	// ------------------------------------------------------------------------

	/** Visitor design pattern */
	public void accept(HuffmanNodeVisitor<T> v) {
		v.caseNode(this);
	}

	public void visitChildren(HuffmanNodeVisitor<T> v) {
		if (left != null) {
			left.accept(v);
		}
		if (right != null) {
			right.accept(v);
		}
	}

	public boolean isLeaf() { 
		return false;
	}

	public AbstractHuffmanNode<T> getLeft() {
		return left;
	}

	public AbstractHuffmanNode<T> getRight() {
		return right;
	}
	
	public AbstractHuffmanNode<T> getChildLeftRight(boolean bit) {
		return (bit)? right : left;
	}

    @Override
    public String toString() {
        return "MergeNode[" + frequency + ":* #" + super.insertSequenceNumber + "]";
    }

}
