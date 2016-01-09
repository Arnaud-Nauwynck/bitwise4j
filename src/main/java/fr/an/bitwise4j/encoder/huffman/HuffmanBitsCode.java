package fr.an.bitwise4j.encoder.huffman;

import java.io.Serializable;

import fr.an.bitwise4j.bits.BitOutputStream;
import fr.an.bitwise4j.encoder.structio.StructDataOutput;

/**
 * 
 */
public class HuffmanBitsCode implements Serializable {

    /** */
    private static final long serialVersionUID = 1L;
    
	private static HuffmanBitsCode rootEmptyCode = new HuffmanBitsCode(0, 0);
	
	private final int bits;
	private final int bitsCount;

	// ------------------------------------------------------------------------
	
	private HuffmanBitsCode(int bits, int bitCount) {
		this.bits = bits;
		this.bitsCount = bitCount;
	}
	
	public static HuffmanBitsCode getRootEmptyCode() {
		return rootEmptyCode;
	}

	public static HuffmanBitsCode childLeftCode(HuffmanBitsCode p) {
		int newbits = p.bits << 1;
		return new HuffmanBitsCode(newbits, p.bitsCount + 1);
	}

	public static HuffmanBitsCode childRightCode(HuffmanBitsCode p) {
		int newbits = (p.bits << 1) + 1;
		return new HuffmanBitsCode(newbits, p.bitsCount + 1);
	}

	// ------------------------------------------------------------------------
	

	public int getBits() {
		return bits;
	}

	public int getBitsCount() {
		return bitsCount;
	}

	public int length() {
		return bitsCount;
	}
	
	public boolean getBit(int index) {
		return 0 != (bits & (1 << index));
	}
	
	public void writeCodeTo(StructDataOutput out) {
	    if (bitsCount <= 32) {
	        // bits are read one by one ... so encode them also one by one for debugging... 
	        // out.writeNBits(bitsCount, bits);
	        
	    } else {
	        throw new UnsupportedOperationException("TODO unsupported huffman code > 32 bits");
	    }
	}

	// idem using 
	public void writeCodeTo(BitOutputStream out) {
        if (bitsCount <= 32) {
            out.writeNBits(bitsCount, bits);
        } else {
            throw new UnsupportedOperationException("TODO unsupported huffman code > 32 bits");
        }
    }

	public void codeToString(StringBuilder sb) {
		for (int i = bitsCount - 1; i >= 0; i--) {
			boolean bit = getBit(i);
			sb.append((bit)? "1" : "0");
		}
	}

	/** override java.lang.Object */
	public String codeToString() {
		StringBuilder sb = new StringBuilder();
		codeToString(sb);
		return sb.toString();
	}
	
	public void toReverseString(StringBuilder sb) {
		for (int i = 0; i < bitsCount; i++) {
			boolean bit = getBit(i);
			sb.append((bit)? "1" : "0");
		}
	}

	public String toReverseString() {
		StringBuilder sb = new StringBuilder();
		toReverseString(sb);
		return sb.toString();
	}
	
	// ------------------------------------------------------------------------
	
	/** override java.lang.Object */
	public String toString() {
		return codeToString();
	}

}
