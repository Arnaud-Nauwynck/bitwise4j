package fr.an.util.encoder.huffman;

/**
 * 
 */
public class HuffmanTreeLeaf<T> extends AbstractHuffmanNode<T> {

	private T symbol;
	
	private Object userData;

	
	// ------------------------------------------------------------------------

	public HuffmanTreeLeaf(HuffmanTable<T> owner, int frequency, int insertSequenceNumber, T symbol) {
		super(owner, frequency, insertSequenceNumber);
		this.symbol = symbol;
	}

	// ------------------------------------------------------------------------

	/** Visitor design pattern */
	public void accept(HuffmanNodeVisitor<T> v) {
		v.caseLeaf(this);
	}

	public boolean isLeaf() { 
		return true;
	}
	
	public Object getUserData() {
		return userData;
	}

	public void setUserData(Object userData) {
		this.userData = userData;
	}

	public T getSymbol() {
		return symbol;
	}

    @Override
    public String toString() {
        return "Leaf [" + frequency + ":" + symbol + " "  + " #" + super.insertSequenceNumber + "]";
    }

}
