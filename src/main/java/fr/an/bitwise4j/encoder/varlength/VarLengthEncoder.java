package fr.an.bitwise4j.encoder.varlength;

import fr.an.bitwise4j.bits.BitOutputStream;
import fr.an.bitwise4j.encoder.huffman.HuffmanBitsCode;
import fr.an.bitwise4j.encoder.huffman.HuffmanTreeLeaf;

/**
 * 
 */
public class VarLengthEncoder {

	private BitOutputStream output;
	
	private DivideRounding divideRounding = new DivideRounding();
	
	// ------------------------------------------------------------------------
	
	public VarLengthEncoder(BitOutputStream output) {
		super();
		this.output = output;
	}

	// ------------------------------------------------------------------------
	
	protected BitOutputStream getOutput() {
		return output; 
	}
	
	public void setDivideRounding(DivideRounding src) {
		this.divideRounding = new DivideRounding(src);
	}
	
	public DivideRounding getDivideRounding() {
		return divideRounding;
	}
	
	public void flush() {
		try {
			output.flush();
		} catch(Exception ioex) {
			throw new RuntimeException(ioex);
		}
	}

	public void close() {
		try {
			output.close();
		} catch(Exception ioex) {
			throw new RuntimeException(ioex);
		}
		output = null;
	}

	public void writeBit(boolean value) {
	    output.writeBit(value);
	}
	
	public void writeNBits(int bits, int bitsLength) {
		output.writeNBits(bitsLength, bits);
	}
	
	public void writeUInt(int value, int maxValue) {
		if (value < 0 || value >= maxValue) throw new IllegalArgumentException();
		if (maxValue == Integer.MAX_VALUE) throw new UnsupportedOperationException();
		
		// recursive divide in approximatly 2 equals part to encode most significant bits first
		// see divideRoundingMode & currentDivideRoundingLower
		while(maxValue > 1) {
			int maxValueDiv2 = divideRounding.roundDiv2(maxValue);
			boolean mostSignificantBit = (value >= maxValueDiv2);
			writeBit(mostSignificantBit);
			if (mostSignificantBit) {
				// value >= maxValueDiv2
				value = value - maxValueDiv2;
				maxValue = maxValue - maxValueDiv2;
			} else {
				// value < maxValueDiv2
				// value = value; .. unchanged
				maxValue = maxValueDiv2;
			}
		}
	}
	
	
	// helper methods
	// ------------------------------------------------------------------------
	
	public void writeBoolean(boolean value) {
		writeUInt((value)?1:0, 2);
	}

	public void writeUIntAny(int value) {
		writeUInt(value, Integer.MAX_VALUE);
	}

	public void writeNUInts(int[] values, int maxValue) {
		final int len = values.length; 
		for (int i = 0; i < len; i++) {
			writeUInt(values[i], maxValue);
		}
	}

	public void writeNUInts(int[] values, int[] valuesWeight) {
		final int len = values.length; 
		for (int i = 0; i < len; i++) {
			writeUInt(values[i], valuesWeight[i]);
		}
	}

	public void writeNUIntsWithConstrainedSum(int[] values, int maxSumValuesExclusive) {
		int len = values.length;
		int[] cumulValues = new int[len];
		cumulValues[0] = values[0]; 
		for (int i = 1; i < len; i++) {
			cumulValues[i] = cumulValues[i - 1] + values[i]; 
		}
		writeNOrderedUInts(cumulValues, maxSumValuesExclusive);
	}

	public void writeNUIntsWithConstrainedSum(int[] values, int[] maxEachValue) { //??? TODO add maxSumValuesExclusive
		int len = values.length;
		int[] cumulValues = new int[len];
		cumulValues[0] = values[0]; 
		int maxCumulValue = maxEachValue[0];
		for (int i = 1; i < len; i++) {
			cumulValues[i] = cumulValues[i - 1] + values[i];
			maxCumulValue += maxEachValue[i];
		}
		writeNOrderedUInts(cumulValues, maxCumulValue);
	}
	
	public void writeNOrderedUInts(int[] values, int maxLastValue) {
		recWriteNOrderedUInts(values, 0, values.length, 0, maxLastValue-1, 0);
	}

	public void writeNOrderedUInts(int[] values, 
									int fromMinValueInclusive, int toMaxValueInclusive, int minIncrInclusive) {
		recWriteNOrderedUInts(values, 0, values.length, 
				fromMinValueInclusive, toMaxValueInclusive, minIncrInclusive);
	}

	public void writeNOrderedUInts(int[] values, 
			int fromMinValueInclusive, int toMaxValueInclusive) {
		recWriteNOrderedUInts(values, 0, values.length, 
				fromMinValueInclusive, toMaxValueInclusive, 0);
	}

	/**
	 * toMaxValue is inclusive!
	 */
	private void recWriteNOrderedUInts(int[] values, int fromIndex, int toIndex, 
				int fromMinValueInclusive, int toMaxValueInclusive, int minIncrInclusive
				) {
		int tmpDiffMax = toMaxValueInclusive - fromMinValueInclusive + 1;
		
		// TODO .... minIncrInclusive is not used yet !!! 
		
		if (fromIndex == (toIndex - 1)) {
			int tmpDiff = values[fromIndex] - fromMinValueInclusive;
			writeUInt(tmpDiff, tmpDiffMax);
		} else if (fromIndex > toIndex) {
			throw new IllegalArgumentException();
		} else {
			int midIndex = (fromIndex + toIndex) / 2;
			int midValue = values[midIndex]; 
			// write mid point, then recurse
			int tmpMidDiff = midValue - fromMinValueInclusive;
			writeUInt(tmpMidDiff, tmpDiffMax);
			// recurse left
			if (fromIndex < midIndex) {
				recWriteNOrderedUInts(values, fromIndex, midIndex, fromMinValueInclusive, midValue, minIncrInclusive);
			}
			// recurse right
			if (midIndex+1 < toIndex) {
				recWriteNOrderedUInts(values, midIndex+1, toIndex, midValue, toMaxValueInclusive, minIncrInclusive);
			}
		}		
	}

	public void writeHuffmanSymbol(HuffmanTreeLeaf<?> symNode) {
		HuffmanBitsCode code = symNode.getResultCode();
		int bitsCount = code.getBitsCount();
		writeUInt(code.getBits(), bitsCount);
	}
	
}
