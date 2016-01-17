package fr.an.bitwise4j.util;

import org.junit.Assert;

public class AssertArrayUtils {


    public static void assertEquals(int[] expected, int[] actual) {
        Assert.assertEquals(expected.length, actual.length);
        for(int i= 0; i < expected.length; i++) {
            Assert.assertEquals(expected[i], actual[i]);
        }
    }

    public static void assertEquals(int[] expected, int offsetExpected, int[] actual, int offsetActual, int len) {
        for(int i= 0; i < len; i++) {
            Assert.assertEquals(expected[i+offsetExpected], actual[i+offsetActual]);
        }
    }

    
    public static void assertEquals(byte[] expected, byte[] actual) {
        Assert.assertEquals(expected.length, actual.length);
        for(int i= 0; i < expected.length; i++) {
            Assert.assertEquals(expected[i], actual[i]);
        }
    }
}
