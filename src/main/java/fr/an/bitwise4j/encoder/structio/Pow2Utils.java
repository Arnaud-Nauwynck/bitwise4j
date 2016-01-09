package fr.an.bitwise4j.encoder.structio;

public final class Pow2Utils {

    /**
     * 
     */
    private static final int[] INTS1024_TO_UPPERLOG2;
    static {
        int[] tmpres = new int[1024];
        for(int i = 0; i < 1024; i++) {
            tmpres[i] = doValueToUpperLog2(i);
        }
        INTS1024_TO_UPPERLOG2 = tmpres;
    }
    
    /*private to force all static */
    private Pow2Utils() {
    }

    public static int valueToUpperLog2(int value) {
        if (value < 0) {
            throw new IllegalArgumentException();
        }
        if (value < 1024) {
            return INTS1024_TO_UPPERLOG2[value];
        } else {
            return doValueToUpperLog2(value);
        }
    }

    private static int doValueToUpperLog2(int value) {
        int pow = 0;
        for(int remain = value-1; remain != 0; remain = remain >>> 1) {
            pow++;
        }
        return pow;
    }
    
}
