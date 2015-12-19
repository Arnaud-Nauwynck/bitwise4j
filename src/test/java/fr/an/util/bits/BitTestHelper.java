package fr.an.util.bits;

import org.junit.Assert;

public class BitTestHelper {

	public static void assertEquals(boolean[] expected, BooleanArray actual) {
		BitTestHelper.assertEquals(expected, actual.getBitsCopy());
	}

	public static void assertEquals(boolean[] expected, boolean[] actual) {
		Assert.assertEquals(expected.length, actual.length);
		for (int i = 0; i < expected.length; i++) {
		    Assert.assertEquals(expected[i], actual[i]);
		}
	}

}
