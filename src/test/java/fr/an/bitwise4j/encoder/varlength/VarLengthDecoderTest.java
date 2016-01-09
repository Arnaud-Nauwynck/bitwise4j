package fr.an.bitwise4j.encoder.varlength;

import org.junit.Assert;
import org.junit.Test;

import fr.an.bitwise4j.bits.BitsUtil;
import fr.an.bitwise4j.bits.BooleanArrayQueue;
import fr.an.bitwise4j.encoder.varlength.VarLengthDecoder;

/**
 * JUnit test for VarLengthDecoder
 */
public class VarLengthDecoderTest {

	public static class DecoderTestStruct {
		// use Bit queue for testing, where inputStream is bound to the decoder, and outputStream is provided by test data putToDecode()" 
		BooleanArrayQueue queue = new BooleanArrayQueue(); 
		VarLengthDecoder decoder = new VarLengthDecoder(queue.getInputEndPoint());
		
		public void putToDecode(String bitsStr) {
			boolean[] bits = BitsUtil.strBitsToBooleans(bitsStr);
			putToDecode(bits);
		}

		public void putToDecode(boolean[] bits) {
			for (int i = 0; i < bits.length; i++) {
				try {
					queue.getOutputEndPoint().writeBit(bits[i]);
				} catch(Exception ex) {
					throw new RuntimeException(ex);
				}
			}
		}
		
		public void assertSync() {
			Assert.assertEquals(0, queue.getBuffer().size());
		}
		
	}

	// TODO ...
	@Test
	public void TODO() {
	}
	
//	public void testWrite1Bit() {
//		doTestReadNBits(0, "0", 2);
//		doTestReadNBits(1, "1", 2);
//	}
//
//	public void testRead2Bits() {
//		doTestReadNBits(0, "00", 3);
//		doTestReadNBits(1, "01", 3);
//		doTestReadNBits(2, "10", 3);
//
//		doTestReadNBits(0, "00", 4);
//		doTestReadNBits(1, "01", 4);
//		doTestReadNBits(2, "10", 4);
//		doTestReadNBits(3, "11", 4);
//	}
//
//	public void testRead8Bits() {
//		doTestReadNBits(0, 		"00000000", 256);
//		doTestReadNBits(128, 	"10000000", 256);
//		doTestReadNBits(1,		"00000001", 256);
//		doTestReadNBits(128+1, 	"10000001", 256);
//		doTestReadNBits(256-1, 	"11111111", 256);
//	}
//
//	public void testRead9Bits() {
//		doTestReadNBits(0, 			"000000000", 512);
//		doTestReadNBits(256, 		"100000000", 512);
//		doTestReadNBits((256+128), 	"110000000", 512);
//		doTestReadNBits(3, 			"000000011", 512);
//		doTestReadNBits(1, 			"000000001", 512);
//		doTestReadNBits(512-1, 		"111111111", 512);
//	}
//
//	public void testRead30Bits() {
//		int bitsLength = 30;
//		int pow2 = 1 << bitsLength;
//		doTestReadNBits(0, "000000000000000000000000000000", bitsLength);
//		doTestReadNBits(1, "000000000000000000000000000001", bitsLength);
//		doTestReadNBits(pow2 - 1, "111111111111111111111111111111", bitsLength);
//		doTestReadNBits(pow2>>1, "100000000000000000000000000000", bitsLength);
//	}
//	
//	private void doTestReadNBits(int expected, String bitsStrToDecode, int bitsLength) {
//		TestStruct ts = new TestStruct();
//		ts.putToDecode(bitsStrToDecode);
//		int actual = ts.decoder.readNBits(bitsLength);
//		assertEquals(expected, actual);
//	}

	
}

