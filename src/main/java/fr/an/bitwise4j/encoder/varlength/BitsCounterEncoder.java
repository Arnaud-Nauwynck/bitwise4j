package fr.an.bitwise4j.encoder.varlength;

import fr.an.bitwise4j.bits.CounterBitOuputStream;
import fr.an.bitwise4j.encoder.huffman.HuffmanBitsCode;
import fr.an.bitwise4j.encoder.huffman.HuffmanTreeLeaf;

/**
 * sub-class of VarLengthEncoder, combining a CounterBitOutputStream as underlying BitOutputStream
 * 
 * override for facility/optim... roughly equivalent to "VarLengthEncoder(new CounterBitOuputStream())"
 */
public class BitsCounterEncoder extends VarLengthEncoder {

	private CounterBitOuputStream counter;
	
	// ------------------------------------------------------------------------
	
	public BitsCounterEncoder() {
		super(new CounterBitOuputStream());
		this.counter = (CounterBitOuputStream) super.getOutput();
	}
	
	// ------------------------------------------------------------------------
	
	public CounterBitOuputStream getCounter() {
		return counter;
	}
	
	public int getCount() {
		return counter.getCount();
	}

	private void incrCount(int p) {
		counter.incrCount(p);
	}
	
	public void resetCount() {
		counter.setCount(0);
	}


	// override VarLengthEncoder for optim
	// ------------------------------------------------------------------------

	@Override
	public void writeBit(boolean value) {
		incrCount(1);
	}

	@Override
	public void writeBoolean(boolean value) {
		incrCount(1);
	}

	@Override
	public void writeNBits(int bits, int bitsLength) {
		incrCount(bitsLength);
	}
	
	@Override
	public void writeHuffmanSymbol(HuffmanTreeLeaf<?> symNode) {
		HuffmanBitsCode code = symNode.getResultCode();
		int bitsCount = code.getBitsCount();
		incrCount(bitsCount);
	}

	// override java.lang.Object
	// ------------------------------------------------------------------------
	
	@Override
	public String toString() {
		return "BitsCounterEncoder[count:" + getCount() + "]";
	}
	
}
 