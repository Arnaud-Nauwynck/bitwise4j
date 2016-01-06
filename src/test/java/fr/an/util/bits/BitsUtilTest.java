package fr.an.util.bits;

import org.junit.Assert;
import org.junit.Test;

public class BitsUtilTest {

    @Test
    public void testStringToBits() {
        int bits = BitsUtil.stringToBits("011001");
        Assert.assertEquals(0b011001, bits);
    }

    @Test
    public void testBitsToString() {
        String res = BitsUtil.bitsToString(6, 0b11001);
        Assert.assertEquals("011001", res);
    }

}
