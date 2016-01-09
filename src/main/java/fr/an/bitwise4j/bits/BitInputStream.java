package fr.an.bitwise4j.bits;

import java.io.IOException;

import fr.an.bitwise4j.util.RuntimeIOException;

/**
 * 
 */
public abstract class BitInputStream extends java.io.InputStream {

	public abstract void close();

	// cf java.io.InputStream.available()
	public abstract boolean hasMoreBit();
	
	/**
	 * Read the next bit from the stream.
	 */
	public abstract boolean readBit();
	
	public abstract void readSkipPaddingTo8();
	
	/**
	 * Read a specified number of bits (<=32) and return them as an int value.
	 */
	public abstract int readBits(int readBitsCounts);

	@Override
	public int read() {
	    return readBits(8);
	}
	
	/**
	 * may rename "readFully" ?
	 * @param dest
	 * @param offset
	 * @param len
	 */
	public void readBytes(byte[] dest, int offset, int len) {
	    int remainLen = len;
	    int pos = offset;
	    while(remainLen > 0) {
    	    int tmpres;
    	    try {
                tmpres = read(dest, pos, remainLen);
            } catch (IOException ex) {
                throw new RuntimeIOException("readFully", ex);
            }
    	    remainLen -= tmpres;
    	    pos += tmpres;
	    }
	}
}