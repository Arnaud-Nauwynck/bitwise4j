package fr.an.bitwise4j.encoder.varlength;

import fr.an.bitwise4j.bits.BitsUtil;
import fr.an.bitwise4j.bits.BooleanArrayQueue;
import fr.an.bitwise4j.encoder.varlength.VarLengthDecoderTest.DecoderTestStruct;
import fr.an.bitwise4j.encoder.varlength.VarLengthEncoderTest.EncoderTestStruct;
import junit.framework.TestCase;

public class VarLengthEncoderDecoderTest extends TestCase {

	public static class EncoderDecoderTestStruct {
		EncoderTestStruct encoderTs = new EncoderTestStruct();
		DecoderTestStruct decoderTs = new DecoderTestStruct();
		
	}
	
	public static class EncodeThenDecodeTestStruct {
		BooleanArrayQueue queue = new BooleanArrayQueue(); 
		VarLengthEncoder encoder = new VarLengthEncoder(queue.getOutputEndPoint());
		VarLengthDecoder decoder = new VarLengthDecoder(queue.getInputEndPoint());
		
		public void assertSync() {
			assertEquals(0, queue.getBuffer().size());
		}

		public void assertNbBits(int expected) {
			assertEquals(expected, queue.getBuffer().size());
		}
		public void assertBits(String expectedBitsStr) {
			boolean[] expectedBits = BitsUtil.strBitsToBooleans(expectedBitsStr);
			boolean[] actualBits = queue.getBuffer().getBitsCopy();
			if (actualBits.length != expectedBits.length) {
				fail("expected bits " + BitsUtil.booleansToStrBits(expectedBits) + " , but bits ends at length " + actualBits.length);
			}
			for (int i = 0; i < expectedBits.length; i++) {
				if (expectedBits[i] != actualBits[i]) {
					fail("expected bits " + BitsUtil.booleansToStrBits(expectedBits) + " , but bit differs at " + i);
				}
			}
		}
		
	}
	
	// ------------------------------------------------------------------------
	
	public VarLengthEncoderDecoderTest(String name) {
		super(name);
	}
	
	// ------------------------------------------------------------------------
	
	public void testReadWrite1Bit() {
		doTestReadWriteNBits("0");
		doTestReadWriteNBits("1");
	}

	public void testReadWrite2Bits() {
		doTestReadWriteNBits("00");
		doTestReadWriteNBits("01");
		doTestReadWriteNBits("10");
		doTestReadWriteNBits("11");
	}

	public void testReadWrite8Bits() {
		doTestReadWriteNBits("00000000");
		doTestReadWriteNBits("10000000");
		doTestReadWriteNBits("00000001");
		doTestReadWriteNBits("10000001");
		doTestReadWriteNBits("11111111");
	}

	public void testReadWrite9Bits() {
		doTestReadWriteNBits("000000000");
		doTestReadWriteNBits("100000000");
		doTestReadWriteNBits("110000000");
		doTestReadWriteNBits("000000011");
		doTestReadWriteNBits("000000001");
		doTestReadWriteNBits("111111111");
	}
	
	private void doTestReadWriteNBits(String bitsStr) {
		boolean[] bits = BitsUtil.strBitsToBooleans(bitsStr);

		{ // test encoder 
			EncoderTestStruct encoderTs = new EncoderTestStruct();
			for (int i = 0; i < bits.length; i++) {
				encoderTs.encoder.writeBit(bits[i]);
			}
			encoderTs.assertDecodeSync(bitsStr);
		}
		{ // test decoder 
			DecoderTestStruct decoderTs = new DecoderTestStruct();
			decoderTs.putToDecode(bits);
			for (int i = 0; i < bits.length; i++) {
				boolean actualBit = decoderTs.decoder.readBit();
				assertEquals(bits[i], actualBit);
			}
			decoderTs.assertSync();
		}
		
	}

	public void testReadWriteUInt1() {
		DivideRounding rounding = new DivideRounding();
		doTestReadWriteUInt("0", 0, 2, rounding);
		doTestReadWriteUInt("1", 1, 2, rounding);
	}
	
	public void testReadWriteUInt3() {
		DivideRounding rounding = new DivideRounding();
		
		// [0,3( = 1~2 bits
		//       0-2(3)
		//      /    \
		//     0    1-2
		doTestReadWriteUInt("0", 0, 3, rounding);  // 1 MSB only!
		doTestReadWriteUInt("10", 1, 3, rounding);
		doTestReadWriteUInt("11", 2, 3, rounding);
	}

	public void testReadWriteUInt_exactsNBits() {
		DivideRounding rounding = new DivideRounding();
		
		// [0-4( = 2 bits (no round)
		doTestReadWriteUInt("00", 0, 4, rounding);
		doTestReadWriteUInt("01", 1, 4, rounding);
		doTestReadWriteUInt("10", 2, 4, rounding);
		doTestReadWriteUInt("11", 3, 4, rounding);

		// [0,8( = 3 bits no round
		doTestReadWriteUInt("000", 0, 8, rounding);
		doTestReadWriteUInt("001", 1, 8, rounding);
		doTestReadWriteUInt("010", 2, 8, rounding);
		doTestReadWriteUInt("011", 3, 8, rounding);
		doTestReadWriteUInt("100", 4, 8, rounding);
		doTestReadWriteUInt("101", 5, 8, rounding);
		doTestReadWriteUInt("110", 6, 8, rounding);
		doTestReadWriteUInt("111", 7, 8, rounding);
		
		// ....
		
		
		
		// [2,256( = 8 bits (no round)
		doTestReadWriteUInt("00000000", 0, 256, rounding);
		doTestReadWriteUInt("00000001", 1, 256, rounding);
		doTestReadWriteUInt("11111111", (256-1), 256, rounding);

		// [0,512( = 9 bits (no round)
		doTestReadWriteUInt("000000000", 0, 512, rounding);
		doTestReadWriteUInt("011111111", (256-1), 512, rounding);
		doTestReadWriteUInt("100000000", 256, 512, rounding);
		doTestReadWriteUInt("111111111", (512-1), 512, rounding);
	}


	public void testReadWriteUInt5() {
		// [0,5( = 2~3 bits
		//       0-4(5)      round lower
		//     /      \
		//   0-1(2)    2-4(3)   round upper  
		//  / \        /   \
		// 0  1       2-3   4
		//             /\
		//            2  3
		DivideRounding rounding = new DivideRounding();
 		doTestReadWriteUInt("00", 0, 5, rounding);
		doTestReadWriteUInt("01", 1, 5, rounding);
		doTestReadWriteUInt("100", 2, 5, rounding);
		doTestReadWriteUInt("101", 3, 5, rounding);
		doTestReadWriteUInt("11", 4, 5, rounding);
		
		// [0,5( = 2~3 bits
		//        0-4(5)      round upper
		//      /      \
		//    0-2(3)    3-4(2)   round lower  
		//   /   \        / \
		//  0    1-2      3   4
		//       / \
		//       1 2
		rounding = new DivideRounding();
		rounding.setCurrentDivideRoundingUpper(true);
 		doTestReadWriteUInt("00", 0, 5, rounding);
		doTestReadWriteUInt("010", 1, 5, rounding);
		doTestReadWriteUInt("011", 2, 5, rounding);
		doTestReadWriteUInt("10", 3, 5, rounding);
		doTestReadWriteUInt("11", 4, 5, rounding);
	}
	
	public void testReadWriteUInt6() {
		// [0,6( = 2~3 bits
		//         0-5(6)      round lower
		//        /      \
		//    0-2(3)     3-5(3)   round upper  
		//   /     \      /   \
		//  0-1     2    3-4   5    round lower
		//  /\           /\
		// 0  1         3  4
		DivideRounding rounding = new DivideRounding();
		doTestReadWriteUInt("000", 0, 6, rounding);
		doTestReadWriteUInt("001", 1, 6, rounding);
		doTestReadWriteUInt("01", 2, 6, rounding);
		doTestReadWriteUInt("100", 3, 6, rounding);
		doTestReadWriteUInt("101", 4, 6, rounding);
		doTestReadWriteUInt("11", 5, 6, rounding);
		
		// [0,6( = 2~3 bits
		//        0-5(6)      round upper
		//      /       \
		//    0-2(2)     3-5(3)   round lower  
		//   /  \         /  \
		//  0   1-2(2)  3    4-5(2)    round upper
		//       /\           /\
		//      1  2         4  5
		rounding = new DivideRounding();
		rounding.setCurrentDivideRoundingUpper(true);
		doTestReadWriteUInt("00", 0, 6, rounding);
		doTestReadWriteUInt("010", 1, 6, rounding);
		doTestReadWriteUInt("011", 2, 6, rounding);
		doTestReadWriteUInt("10", 3, 6, rounding);
		doTestReadWriteUInt("110", 4, 6, rounding);
		doTestReadWriteUInt("111", 5, 6, rounding);
	}
	
	public void testReadWriteUInt7() {
		// [0,7( = 2~3 bits
		//         0-6(7)      round lower
		//        /      \
		//     0-2(3)     3-6(4)   round upper  
		//    /     \      /   \
		//  0-1(2)   3   3-4   5-6    round lower
		//  /\           /\    / \
		// 0  1         3  4   5  6
		DivideRounding rounding = new DivideRounding();
		doTestReadWriteUInt("000", 0, 7, rounding);
		doTestReadWriteUInt("001", 1, 7, rounding);
		doTestReadWriteUInt("01",  2, 7, rounding);
		doTestReadWriteUInt("100", 3, 7, rounding);
		doTestReadWriteUInt("101", 4, 7, rounding);
		doTestReadWriteUInt("110", 5, 7, rounding);
		doTestReadWriteUInt("111", 6, 7, rounding);

		// [0,7( = 2~3 bits
		//         0-6(7)      round upper
		//        /      \
		//     0-3(4)     4-6(3)   round lower  
		//    /   \      /  \
		//  0-1   2-3   4   5-6    round upper
		//  /\    /\        /\
		// 0  1  2  3      5  6
		rounding = new DivideRounding();
		rounding.setCurrentDivideRoundingUpper(true);
		doTestReadWriteUInt("000", 0, 7, rounding);
		doTestReadWriteUInt("001", 1, 7, rounding);
		doTestReadWriteUInt("010",  2, 7, rounding);
		doTestReadWriteUInt("011", 3, 7, rounding);
		doTestReadWriteUInt("10", 4, 7, rounding);
		doTestReadWriteUInt("110", 5, 7, rounding);
		doTestReadWriteUInt("111", 6, 7, rounding);
	}
	

	private void doTestReadWriteUInt(String expectedBitsStr, int value, int maxValue, DivideRounding divideRounding) {
		boolean[] bits = BitsUtil.strBitsToBooleans(expectedBitsStr);

		{ // test encoder
			EncoderTestStruct encoderTs = new EncoderTestStruct();
			encoderTs.encoder.setDivideRounding(divideRounding);
			
			encoderTs.encoder.writeUInt(value, maxValue);
			encoderTs.assertDecodeSync(bits);
		}
		{ // test decoder
			DecoderTestStruct decoderTs = new DecoderTestStruct();
			decoderTs.decoder.setDivideRounding(divideRounding);

			decoderTs.putToDecode(bits);
			int actualValue = decoderTs.decoder.readUInt(maxValue);
			assertEquals(value, actualValue);
			decoderTs.assertSync();
		}
	}
	

	public void testReadWriteUInt_chained() {
		EncodeThenDecodeTestStruct ts = new EncodeThenDecodeTestStruct();

		doEncodeThenDecodeUInt(ts, 0, 2);
		doEncodeThenDecodeUInt(ts, 1, 2);
		
		doEncodeThenDecodeUInt(ts, 0, 3);
		doEncodeThenDecodeUInt(ts, 1, 3);
		doEncodeThenDecodeUInt(ts, 2, 3);

		doEncodeThenDecodeUInt(ts, 0, 4);
		doEncodeThenDecodeUInt(ts, 1, 4);
		doEncodeThenDecodeUInt(ts, 2, 4);
		doEncodeThenDecodeUInt(ts, 3, 4);

		for (int i = 0; i < 1000; i+= 51) {
			for (int j = 0; j < i; j+=5) {
				doEncodeThenDecodeUInt(ts, j, i);
			}
		}
		
		ts.assertSync();
	}
	
	private void doEncodeThenDecodeUInt(EncodeThenDecodeTestStruct ts, int value, int maxValue) {
		ts.encoder.writeUInt(value, maxValue);
		int actualValue = ts.decoder.readUInt(maxValue);
		assertEquals(value, actualValue);
		ts.assertSync();
	}
	
	public void testReadWriteNOrderedInts() {
		EncodeThenDecodeTestStruct ts = new EncodeThenDecodeTestStruct();
		
		int[] sortedValues;
		int maxValue;
		int[] actualSortedValues;
		
		sortedValues = new int[] { 2 };
		for (maxValue = 3; maxValue < 10; maxValue++) {
			ts.encoder.writeNOrderedUInts(sortedValues, maxValue);
			actualSortedValues = ts.decoder.readNOrderedUInts(sortedValues.length, maxValue);
			ts.assertSync();
			assertEquals(sortedValues, actualSortedValues);
		}
		
		sortedValues = new int[] { 2, 4 };
		for (maxValue = 5; maxValue < 10; maxValue++) {
			ts.encoder.writeNOrderedUInts(sortedValues, maxValue);
			actualSortedValues = ts.decoder.readNOrderedUInts(sortedValues.length, maxValue);
			ts.assertSync();
			assertEquals(sortedValues, actualSortedValues);
		}
		
		sortedValues = new int[] { 2, 3, 5 };
		for (maxValue = 6; maxValue < 10; maxValue++) {
			ts.encoder.writeNOrderedUInts(sortedValues, maxValue);
			actualSortedValues = ts.decoder.readNOrderedUInts(sortedValues.length, maxValue);
			ts.assertSync();
			assertEquals(sortedValues, actualSortedValues);
		}		

		sortedValues = new int[] { 2, 4, 5, 7 };
		for (maxValue = 8; maxValue < 10; maxValue++) {
			ts.encoder.writeNOrderedUInts(sortedValues, maxValue);
			actualSortedValues = ts.decoder.readNOrderedUInts(sortedValues.length, maxValue);
			ts.assertSync();
			assertEquals(sortedValues, actualSortedValues);
		}
		
		ts.assertSync();
	}
	
	
	public void testWriteNUIntsWithConstrainedSum() {
		EncodeThenDecodeTestStruct ts = new EncodeThenDecodeTestStruct();
		int[] values;
		int maxValue;
		int[] actualValues;
		
		values = new int[] { 5 };
		for (maxValue = sumOf(values)+1; maxValue < 20; maxValue++) {
			ts.encoder.writeNUIntsWithConstrainedSum(values, maxValue);
			actualValues = ts.decoder.readNUIntsWithConstrainedSum(values.length, maxValue);
			ts.assertSync();
			assertEquals(values, actualValues);
		}
		
		values = new int[] { 2, 5 };
		for (maxValue = sumOf(values)+1; maxValue < 20; maxValue++) {
			ts.encoder.writeNUIntsWithConstrainedSum(values, maxValue);
			actualValues = ts.decoder.readNUIntsWithConstrainedSum(values.length, maxValue);
			ts.assertSync();
			assertEquals(values, actualValues);
		}

		values = new int[] { 5, 2 };
		for (maxValue = sumOf(values)+1; maxValue < 20; maxValue++) {
			ts.encoder.writeNUIntsWithConstrainedSum(values, maxValue);
			actualValues = ts.decoder.readNUIntsWithConstrainedSum(values.length, maxValue);
			ts.assertSync();
			assertEquals(values, actualValues);
		}
		
		values = new int[] { 2, 0 };
		for (maxValue = sumOf(values)+1; maxValue < 20; maxValue++) {
			ts.encoder.writeNUIntsWithConstrainedSum(values, maxValue);
			actualValues = ts.decoder.readNUIntsWithConstrainedSum(values.length, maxValue);
			ts.assertSync();
			assertEquals(values, actualValues);
		}

		values = new int[] { 0, 2 };
		for (maxValue = sumOf(values)+1; maxValue < 20; maxValue++) {
			ts.encoder.writeNUIntsWithConstrainedSum(values, maxValue);
			actualValues = ts.decoder.readNUIntsWithConstrainedSum(values.length, maxValue);
			ts.assertSync();
			assertEquals(values, actualValues);
		}

		values = new int[] { 2, 7, 5 };
		for (maxValue = sumOf(values)+1; maxValue < 20; maxValue++) {
			ts.encoder.writeNUIntsWithConstrainedSum(values, maxValue);
			actualValues = ts.decoder.readNUIntsWithConstrainedSum(values.length, maxValue);
			ts.assertSync();
			assertEquals(values, actualValues);
		}

		values = new int[] { 2, 7, 0 };
		for (maxValue = sumOf(values)+1; maxValue < 20; maxValue++) {
			ts.encoder.writeNUIntsWithConstrainedSum(values, maxValue);
			actualValues = ts.decoder.readNUIntsWithConstrainedSum(values.length, maxValue);
			ts.assertSync();
			assertEquals(values, actualValues);
		}
		
		values = new int[] { 5, 2, 10, 6 };
		for (maxValue = sumOf(values)+1; maxValue < 30; maxValue++) {
			ts.encoder.writeNUIntsWithConstrainedSum(values, maxValue);
			actualValues = ts.decoder.readNUIntsWithConstrainedSum(values.length, maxValue);
			ts.assertSync();
			assertEquals(values, actualValues);
		}
		ts.assertSync();
	}
	
	private static int sumOf(int[] p) {
		int res = 0;
		for (int i = 0; i < p.length; i++) {
			res += p[i];
		}
		return res;
	}
	// ------------------------------------------------------------------------ 
	
	public static void assertEquals(int[] expected, int[] actual) {
		assertEquals(expected.length, actual.length);
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], actual[i]);
		}
	}
	
}
