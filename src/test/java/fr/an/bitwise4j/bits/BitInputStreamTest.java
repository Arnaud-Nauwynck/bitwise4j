package fr.an.bitwise4j.bits;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import fr.an.bitwise4j.bits.BitsUtil;
import fr.an.bitwise4j.bits.InputStreamToBitInputStream;

/**
 * JUnit test for InputStreamToBitInputStream
 */
public class BitInputStreamTest {

    @Test
    public void test() throws IOException {
		doTest("00000000");
		doTest("00000001");
		doTest("10000000");
	}

	private void doTest(String str) throws IOException {
		boolean[] boolArray = BitsUtil.strBitsToBooleans(str);
		
		{ // read 1 by 1
			byte[] byteArray= BitsUtil.booleansToBytes(boolArray);
			InputStreamToBitInputStream bin = new InputStreamToBitInputStream(new ByteArrayInputStream(byteArray));
			int len = boolArray.length;
			for (int i = 0; i < len; i++) {
				boolean actual = bin.readBit(); // read 1 by 1 ... very restrictive !!
				Assert.assertEquals(boolArray[i], actual);
			}
			bin.close();
		}
		
		doTestReadNByN(boolArray, 2);
		doTestReadNByN(boolArray, 3);
		doTestReadNByN(boolArray, 8);
		doTestReadNByN(boolArray, 16);
		doTestReadNByN(boolArray, 32);
		for (int i = 1; i <= 32; i++) {
			doTestReadNByN(boolArray, i);
		}
		
	}
	
	private void doTestReadNByN(boolean[] boolArray, int n) throws IOException {
		byte[] byteArray= BitsUtil.booleansToBytes(boolArray);
		InputStreamToBitInputStream bin = new InputStreamToBitInputStream(new ByteArrayInputStream(byteArray));
		int len = boolArray.length;
		int i = 0;
		for(; i + n < len; i+= n) {
			int actual = bin.readBits(n); // read 1 by 1 ... very restrictive !!
			int expected = 0;
			for (int j = 0; j < n; j++) {
				expected = (expected << 1) + (boolArray[i+j] ? 1 : 0);
			}
			Assert.assertEquals(expected, actual);
		}
		// read remaing for modulo N to end
		for(; i < len; i++) {
			boolean actual = bin.readBit();
			Assert.assertEquals(boolArray[i], actual);
		}
		
		bin.close();
	}
	
}
