package fr.an.util.bits;

import java.io.IOException;
import java.io.OutputStream;

/**
 * implementation of OutputStream for counting bytes
 * (no IO writes are involved!)
 */
public class CounterOuputStream extends OutputStream {

	public int count;
	
	// ------------------------------------------------------------------------
	
	public CounterOuputStream() {
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
	
	// implements OutputStream
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
    public void write(int b) throws IOException {
        incrCount(1);        
    }

    @Override
    public void write(byte[] b) throws IOException {
        incrCount(b.length);        
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        incrCount(len);        
    }
	
	// override java.lang.Object
	// ------------------------------------------------------------------------
	
    @Override
	public String toString() {
		return "CounterOutputStream[" + count + "]";
	}
	
}
