package fr.an.util.bits;

import java.io.IOException;
import java.io.InputStream;



/**
 * class for reading individual bits from a general Java InputStream.
 *
 */
public class InputStreamToBitInputStream extends BitInputStream {

	/**
	 * The Java InputStream this class is working on.
	 */
	private InputStream targetInputStream;

	/**
	 * 
	 */
	private int bitsBuffer;
	
	/**
	 * 
	 */
	private int bitsBufferLen;
	
	
	/**
	 * precomputed bit mask for binary values "1", "11", "111", "1111", ..., "111..(x32)" 
	 */
	private static final int BITS_MASKS[/*32*/] = {
        0x00, 0x01, 0x03, 0x07, 0x0f, 0x1f, 0x3f, 0x7f, 0xff,
        0x1ff,0x3ff,0x7ff,0xfff,0x1fff,0x3fff,0x7fff,0xffff,
        0x1ffff,0x3ffff,0x7ffff,0xfffff,0x1fffff,0x3fffff,
        0x7fffff,0xffffff,0x1ffffff,0x3ffffff,0x7ffffff,
        0xfffffff,0x1fffffff,0x3fffffff,0x7fffffff,0xffffffff
    };
	
	// ------------------------------------------------------------------------
	
	public InputStreamToBitInputStream(InputStream p) {
		targetInputStream = p;
	}

	// ------------------------------------------------------------------------

	public void close() {
	    try {
	        targetInputStream.close();
	    } catch(IOException ex) {
	        throw new RuntimeIOException("Failed close()", ex);
	    }
		targetInputStream = null;      
	}

	private int readTargetByte() {
		try {
            return targetInputStream.read();
        } catch (IOException e) {
            throw new RuntimeIOException("Failed readTargetByte()", e);
        }
	}
	
	/**
	 * Read a specified number of bits and return them as an int value.
	 */
	public int readBits(int readBitsCounts) {
		int res = 0;
		
		while (readBitsCounts > bitsBufferLen){
            res |= ( bitsBuffer << (readBitsCounts - bitsBufferLen) );
            readBitsCounts -= bitsBufferLen;
            // cf inlined fetchReadByte();
            bitsBuffer = readTargetByte();
            if (bitsBuffer == -1) {
                throw new EOFRuntimeException();
            }
            bitsBufferLen = 8;
        }

        if (readBitsCounts > 0){
            res |= bitsBuffer >> (bitsBufferLen - readBitsCounts);
            bitsBuffer &= BITS_MASKS[bitsBufferLen - readBitsCounts];
            bitsBufferLen -= readBitsCounts;
        }
        
		return res;
		
	}

	@Override
	public void readSkipPaddingTo8() {
	    bitsBuffer = 0;
	    bitsBufferLen = 0;
	}

    private void fetchReadByte() {
        if (bitsBufferLen == 0) {
            bitsBuffer = readTargetByte();
            if (bitsBuffer == -1) {
                throw new EOFRuntimeException();
            }
            bitsBufferLen = 8;
        }
    }
    
//	/**
//	 * Read the next bit from the stream.
//	 */
//	public boolean readBit() throws IOException {
//		if (bitsBufferLen == 0) {
//			bitsBuffer = readTargetByte();
//			if (bitsBuffer == -1)
//				throw new EOFException();
//			bitsBufferLen = 8;            
//		}
//		int bit = bitsBuffer & (1 << bitsBufferLen);
//		bitsBufferLen++;
//		return  (bit != 0);
//	}
	
	/**
	 * Read the next bit from the stream.
	 */
	public boolean readBit() {
		int tmp = readBits(1);
		return (tmp != 0);
	}

    @Override
    public int read() {
        return readBits(8);
    }

    @Override
    public int read(byte[] b) {
        return read(b, 0, b.length);
    }

    public void readBytes(byte[] dest, int len) {
        read(dest, 0, len);
    }
    
    @Override
    public int read(byte[] b, int off, int len) {
        // TODO does not implements EOF same as java.io !!! 
        for (int i = 0, pos = off; i < len; i++,pos++) {
            b[pos] = (byte) read();
        }
        return len;
    }

    public boolean hasMoreBit() {
        if (bitsBufferLen > 0) {
            return true;
        }
        try {
            if (super.available() > 0) {
                return true;
            }
        } catch(IOException ex) {
            throw new RuntimeIOException("hasMoreBit", ex);
        }
        // fetchReadByte();
        bitsBuffer = readTargetByte();
        if (bitsBuffer == -1) {
            return false;
        } else {
            bitsBufferLen = 8;
            return true;
        }
    }
    
    @Override
    public int available() throws IOException {
        return super.available();
    }

    @Override
    public void mark(int readlimit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void reset() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean markSupported() {
        return false;
    }
	
}

