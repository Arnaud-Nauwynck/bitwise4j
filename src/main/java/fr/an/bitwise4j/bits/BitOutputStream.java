package fr.an.bitwise4j.bits;

import java.io.IOException;
import java.io.OutputStream;

import fr.an.bitwise4j.util.RuntimeIOException;

/**
 * 
 */
public abstract class BitOutputStream extends OutputStream {

    // override without the annoying "throws IOException" 
    public abstract void close();
    
	/**
	 * Write a single bit to the stream
	 */
	public abstract void writeBit(boolean p);

	/**
	 * Write the specified number of bits (<=32) from the int value to the stream.
	 * @param bitsFlag the int containing the bits that should be written to the stream.
	 * @param numBits how many bits of the integer should be written to the stream.
	 */
	public abstract void writeNBits(int count, int bitsValue);

	@Override
	public void write(int value) {
	    writeNBits(8, value);
	}
	
	/**
     * @param dest
     * @param offset
     * @param len
     */
    public void writeBytes(byte[] src, int offset, int len) {
        try {
            super.write(src, offset, len);
        } catch (IOException ex) {
            throw new RuntimeIOException("write", ex);
        }
    }
    
}