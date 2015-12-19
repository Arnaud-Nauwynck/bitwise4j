package fr.an.util.bits;

import it.unimi.dsi.fastutil.booleans.BooleanArrayList;

/**
 * an array of boolean/bits, using internally  <code>boolean[]</code>
 * (not efficient in memory, but easier for debugging / converting) 
 */
public class BooleanArray extends BooleanArrayList {

    /** */
    private static final long serialVersionUID = 1L;

    // ------------------------------------------------------------------------
	
    public BooleanArray() {
	}

	public BooleanArray(boolean[] src) {
	    super(src);
	}

	// ------------------------------------------------------------------------
	
	public boolean[] getBitsCopy() {
		return toBooleanArray(null);
	}
	
//	public void or(BooleanArray p) {
//		int resLen = Math.max(bits.length, p.bits.length);
//		boolean[] resBits = new boolean[resLen];
//		int minLen = Math.max(bits.length, p.bits.length);
//		for (int i = 0; i < minLen; i++) {
//			resBits[i] = bits[i] || p.bits[i];
//		}
//		if (resLen != minLen) {
//			boolean[] remainingSrc = (bits.length > p.bits.length) ? bits : p.bits;
//			System.arraycopy(remainingSrc, minLen, resBits, minLen, resLen);
//		}
//		this.bits = resBits;
//	}
//	
//	public void and(BooleanArray p) {
//		int resLen = Math.max(bits.length, p.bits.length);
//		boolean[] resBits = new boolean[resLen];
//		int minLen = Math.max(bits.length, p.bits.length);
//		for (int i = 0; i < minLen; i++) {
//			resBits[i] = bits[i] && p.bits[i];
//		}
//		this.bits = resBits;
//	}
	
	public void setAt(int index, boolean value) {
		super.set(index, value);
	}

	public boolean getAt(int index) {
		return super.getBoolean(index);
	}

	public void shiftLeft(int shiftIndex) {
	    for (int i = 0; i < shiftIndex; i++) {
	        super.add(super.size(), false);
	    }
	}

	public void shiftRight(int p) {
	    super.remove(super.size()-1);
	}
	
	public void removeNbitsLeft(int truncateLen) {
	    for (int i = 0; i < truncateLen; i++) {
            super.remove(0);
        }
	}
	
	// override java.lang.Object
	// ------------------------------------------------------------------------

	@Override
	public String toString() {
		final int len = super.size();
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
		// for (int i = len-1; i >= 0; i--) {
			boolean b = get(i);
			sb.append((b) ? '1' : '0');
		}
		return sb.toString();
	}	
	
	public static BooleanArray parse(String str) {
	    boolean[] tmpres = BitsUtil.strBitsToBooleans(str);
	    return new BooleanArray(tmpres);
	}

}
