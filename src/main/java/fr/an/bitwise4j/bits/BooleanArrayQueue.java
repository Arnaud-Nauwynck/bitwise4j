package fr.an.bitwise4j.bits;

import fr.an.bitwise4j.util.EOFRuntimeException;

/**
 * a simple FIFO queue of boolean, with BitInputStream/BitOutputStream end points
 */
public class BooleanArrayQueue {

	private BooleanArray buffer; 
	
	private BitInputStream inputEndPoint = new InnerBitInputStream();
	private BitOutputStream outputEndPoint = new InnerBitOutputStream();
	
	// ------------------------------------------------------------------------
	
	public BooleanArrayQueue() {
	    this(new BooleanArray());
	}
	
	public BooleanArrayQueue(BooleanArray buffer) {
	    this.buffer = buffer;
    }
	
	// ------------------------------------------------------------------------
	
	public BooleanArray getBuffer() {
		return buffer;
	}
	
	public BitInputStream getInputEndPoint() {
		return inputEndPoint;
	}

	public BitOutputStream getOutputEndPoint() {
		return outputEndPoint;
	}

	// override java.lang.Object
	// ------------------------------------------------------------------------
	
	public String toString() {
		return "BooleanArrayQueue[" + buffer + "]";
	}
	
	// internal inner classes
	// ------------------------------------------------------------------------
	
	private class InnerBitInputStream extends BitInputStream {

		@Override
		public void close() {
			// do nothing?
		}
		
		@Override
        public boolean hasMoreBit() {
            return ! buffer.isEmpty();
        }

        @Override
		public boolean readBit() {
			return buffer.remove(0);
		}

		@Override
		public int readBits(int readBitsCounts) {
			int index = buffer.size() - 1;
			if (index < 0) throw new EOFRuntimeException();
			int res = 0;
			for (int i = 0; i < readBitsCounts; i++) {
				boolean bit = buffer.remove(0);
				res = (res << 1) | ((bit) ? 1 : 0);
			}
			return res;
		}
		
		@Override
		public void readSkipPaddingTo8() {
		    int size = buffer.size();
		    if (size > 0) {
		        int skipCount = buffer.size() % 8;
		        if (skipCount != 0) {
		            for (int i = 0; i < skipCount; i++) {
		                buffer.remove(0);
		            }
		        }
		    }
		}

		  
	}

	private class InnerBitOutputStream extends BitOutputStream {

		@Override
		public void close() {
			// do nothing?
		}

		@Override
		public void flush() {
			// do nothing?
		}

		@Override
		public void writeBit(boolean p) {
			buffer.add(p);
		}

		@Override
		public void writeNBits(int count, int bitsValue) {
			for (int i = count-1; i >= 0; i--) {
				boolean bit = 0 != (bitsValue & (1 << i));
				buffer.add(bit);
			}
		}
		
	}

}
