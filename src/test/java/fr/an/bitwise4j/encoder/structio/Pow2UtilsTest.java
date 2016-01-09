package fr.an.bitwise4j.encoder.structio;

import org.junit.Assert;
import org.junit.Test;

import fr.an.bitwise4j.encoder.structio.Pow2Utils;

public class Pow2UtilsTest {

    @Test
    public void testValueToUpperLog2() {
        // Prepare
        // Perform
        Assert.assertEquals(1, Pow2Utils.valueToUpperLog2(2)); // for value in [0, 2(  => 0, 1 : 1 bit
        Assert.assertEquals(2, Pow2Utils.valueToUpperLog2(3)); // for value in [0, 3(  => 0, 1, 2=0b10 : 2 bits
        Assert.assertEquals(2, Pow2Utils.valueToUpperLog2(4)); // ...
        Assert.assertEquals(3, Pow2Utils.valueToUpperLog2(5));
        Assert.assertEquals(3, Pow2Utils.valueToUpperLog2(8));
        Assert.assertEquals(4, Pow2Utils.valueToUpperLog2(9));
        Assert.assertEquals(4, Pow2Utils.valueToUpperLog2(16));
        Assert.assertEquals(5, Pow2Utils.valueToUpperLog2(17));
        Assert.assertEquals(10, Pow2Utils.valueToUpperLog2(1023));
        Assert.assertEquals(10, Pow2Utils.valueToUpperLog2(1024));
        Assert.assertEquals(11, Pow2Utils.valueToUpperLog2(1025));
        Assert.assertEquals(11, Pow2Utils.valueToUpperLog2(2048));
        Assert.assertEquals(12, Pow2Utils.valueToUpperLog2(2049));

        // Post-check
    }

}
