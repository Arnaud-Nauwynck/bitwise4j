package fr.an.bitwise4j.encoder.varlength;

import org.junit.Assert;
import org.junit.Test;

import fr.an.bitwise4j.bits.BitsUtil;
import fr.an.bitwise4j.bits.BooleanArrayQueue;
import fr.an.bitwise4j.encoder.varlength.VarLengthEncoder;
import fr.an.bitwise4j.util.EOFRuntimeException;

/**
 * JUnit test for VarLengthEncoder
 */
public class VarLengthEncoderTest {

	public static class EncoderTestStruct {
		// use Bit queue for testing, where inputStream is bound to the decoder, and outputStream is provided by test data putToDecode()" 
		BooleanArrayQueue queue = new BooleanArrayQueue(); 
		VarLengthEncoder encoder = new VarLengthEncoder(queue.getOutputEndPoint());
		
		public void assertDecode(String expectedBitsStr) {
			boolean[] expectedBits = BitsUtil.strBitsToBooleans(expectedBitsStr);
			assertDecode(expectedBits);
		}

		public void assertDecode(boolean[] expectedBits) {
			for (int i = 0; i < expectedBits.length; i++) {
				boolean actualBit;
				try {
					actualBit = queue.getInputEndPoint().readBit();
				} catch(EOFRuntimeException eof) {
				    Assert.fail("expected bits " + BitsUtil.booleansToStrBits(expectedBits) + " , but bits ends at length " + i);
					throw new RuntimeException(eof);// does not occur, fail()=>throw
				}
				if (expectedBits[i] != actualBit) {
				    Assert.fail("expected bits " + BitsUtil.booleansToStrBits(expectedBits) + " , but bit differs at " + i);
				}
			}
		}
		
		public void assertDecodeSync(String expectedBitsStr) {
			assertDecode(expectedBitsStr);
			assertSync();
		}

		public void assertDecodeSync(boolean[] expectedBits) {
			assertDecode(expectedBits);
			assertSync();
		}

		public void assertSync() {
			int bufferLen = queue.getBuffer().size();
			if (bufferLen != 0) {
				Assert.fail("expected sync bits, but got " + bufferLen + " more bit(s) : '" + queue.getBuffer().toString() + "'");
			}
		}
	}
	
	// assert utility methods
	// ------------------------------------------------------------------------
	
	public static void assertEquals(String expected, byte[] actual) {
		boolean[] expectedBoolArray = BitsUtil.strBitsToBooleans(expected);
		byte[] expectedByteArray = BitsUtil.booleansToBytes(expectedBoolArray);
		assertEquals(expectedByteArray, actual);
	}
	
	public static void assertEquals(byte[] expected, byte[] actual) {
		Assert.assertEquals(expected.length, actual.length);
		for (int i = 0; i < expected.length; i++) {
			Assert.assertEquals(expected[i], actual[i]);
		}
	}
	

	// TODO ...
	@Test
	public void TODO() {
	}
	
}

