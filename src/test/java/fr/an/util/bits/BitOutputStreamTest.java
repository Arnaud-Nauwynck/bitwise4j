package fr.an.util.bits;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit test for InputStreamToBitInputStream
 */
public class BitOutputStreamTest {

    @Test
	public void test() throws IOException {
		doTest("00000000");
		doTest("00000001");
		doTest("10000000");
		doTest("11111111");
	}

	private void doTest(String str) throws IOException {
		boolean[] boolArray = BitsUtil.strBitsToBooleans(str);
		byte[] byteArray= BitsUtil.booleansToBytes(boolArray);
		ByteArrayOutputStream boutArray = new ByteArrayOutputStream();
		BitOutputStream sut = new OutputStreamToBitOutputStream(boutArray);
		
		for (int i = 0; i < boolArray.length; i++) {
			sut.writeBit(boolArray[i]); // write 1 by 1 ... very restrictive !!
		}
		
		byte[] resByteArray = boutArray.toByteArray();
		assertEquals(byteArray, resByteArray);

		sut.close();
	}
	
	public static void assertEquals(byte[] expected, byte[] actual) {
		Assert.assertEquals(expected.length, actual.length);
		for (int i = 0; i < expected.length; i++) {
		    Assert.assertEquals(expected[i], actual[i]);			
		}
	}
	
}
