package fr.an.util.bits;


/**
 * implementation of BitOutputStream for counting bits
 * (no IO writes are involved!)
 */
public class CounterBitOuputStream extends BitOutputStream {

	public int count;
	
	// ------------------------------------------------------------------------
	
	public CounterBitOuputStream() {
	}
	
	// ------------------------------------------------------------------------

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	public void incrCount(int p) {
		this.count += p;
	}
	
	// implements BitOutputStream
	// ------------------------------------------------------------------------
	
	@Override
	public void close() {
		// do nothing
	}

	@Override
	public void flush() {
		// do nothing		
	}

	@Override
	public void writeBit(boolean p) {
		incrCount(1);
	}

	@Override
	public void writeNBits(int count, int bitsValue) {
		incrCount(count);
	}

    @Override
	public void write(int value) {
	    incrCount(8);
	}
	
    @Override
	public void writeBytes(byte[] src, int offset, int len) {
	    incrCount(len * 8);
	}
	
	// override java.lang.Object
	// ------------------------------------------------------------------------
	
	@Override
	public String toString() {
		return "CounterBitOutputStream[" + count + "]";
	}
	
}
