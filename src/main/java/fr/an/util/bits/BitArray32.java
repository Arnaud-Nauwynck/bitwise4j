package fr.an.util.bits;

/**
 * a "bool[32]" bit array, limited to 32 and using an int as underlying backing store 
 */
public final class BitArray32 {

	private int bits;
	private int bitsCount;
	
	// ------------------------------------------------------------------------
	
	public BitArray32() {
	}

	public BitArray32(int bits, int bitCount) {
		this.bits = bits;
		this.bitsCount = bitCount;
	}

	// ------------------------------------------------------------------------
	
	public void or(BitArray32 p) {
		this.bitsCount = Math.max(bitsCount, p.bitsCount);
		this.bits = bits | p.bits;
	}
	
	public void set(int index) {
		this.bits = (bits | (1 << index));
		this.bitsCount = Math.max(bitsCount, index);
	}

	public boolean get(int index) {
		return (bits & (1 << index)) != 0;
	}

	public void shift(int shiftIndex) {
		this.bits = bits << shiftIndex;
		this.bitsCount += shiftIndex;
	}

	// override java.lang.Object
	// ------------------------------------------------------------------------
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new BitArray32(bits, bitsCount);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof BitArray32)) return false;
		BitArray32 p = (BitArray32) obj;
		return bits == p.bits && bitsCount == p.bitsCount;
	}

	@Override
	public int hashCode() {
		return bits ^ bitsCount;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(bitsCount);
		for (int i = 0; i < bitsCount; i++) {
			boolean b = ((bits & (1 << i))) != 0;
			sb.append((b) ? '1' : '0');
		}
		return sb.toString();
	}	
	
}
