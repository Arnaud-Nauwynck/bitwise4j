package fr.an.util.bits;

import java.io.IOException;

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
	    int tmpres;
	    try {
            tmpres = read(dest, offset, len);
        } catch (IOException ex) {
            throw new RuntimeIOException("readFully", ex);
        }
	    if (tmpres != len) {
	        throw new EOFRuntimeException();
	    }
	}
}