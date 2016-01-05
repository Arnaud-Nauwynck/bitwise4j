package fr.an.util.encoder.structio;

import java.io.DataInputStream;
import java.io.IOException;

import fr.an.util.bits.BitInputStream;
import fr.an.util.bits.RuntimeIOException;

/**
 * implements StructDataInput using underlying BitInputStream
 * 
 * cf similar java.io.DataInputStream (where underlying stream is a stream of bytes, instead of bits)
 */
public class BitStreamStructDataInput extends BitInputStream implements StructDataInput {

    private BitInputStream in;
    
    // ------------------------------------------------------------------------

    public BitStreamStructDataInput(BitInputStream target) {
        this.in = target;
    }

    // ------------------------------------------------------------------------


    @Override
    public void close() {
        if (in != null) {
            in.close();
            this.in = null;
        }
    }

    @Override
    public boolean hasMoreBit() {
        return in.hasMoreBit();
    }

    @Override
    public int readBits(int readBitsCounts) {
        return in.readBits(readBitsCounts);
    }
    
    public boolean readBit() {
        return in.readBit();
    }

    public int readNBits(int count) {
        return in.readBits(count);
    }
    
    public int readUInt0N(int maxNExclusive) {
        int nBits = Pow2Utils.valueToUpperLog2(maxNExclusive);
        return readNBits(nBits);
    }
    
    public int readIntMinMax(int fromMin, int toMax) {
        int maxAmplitude = toMax - fromMin;
        int nBits = Pow2Utils.valueToUpperLog2(maxAmplitude);
        return fromMin + readNBits(nBits);
    }

    public byte readByte() {
        return (byte) in.read();
    }
    
    public void readBytes(byte[] dest, int len) {
        readBytes(dest, 0, len);
    }
    
    public void readBytes(byte[] dest, int offset, int len) {
        in.readBytes(dest, offset, len);
    }
    
    public int readInt() {
        return readNBits(32);
    }
    
    public float readFloat() {
        int tmpBits32 = readNBits(32);
        return Float.intBitsToFloat(tmpBits32);
    }
    
    public double readDouble() {
        int tmp1 = readNBits(32);
        int tmp2 = readNBits(32);
        long bits64 = (((long) tmp1) << 32) + tmp2;
        return Double.longBitsToDouble(bits64);
    }

    // cf java.io.DataInputStream
    public String readUTF() {
        DataInputStream din = new DataInputStream(in);
        try {
            return din.readUTF();
        } catch (IOException e) {
            throw new RuntimeIOException("readUTF", e);
        }
    }

    public int readUIntLtMinElseMax(int min, int max) {
        int res;
        boolean ltMin = readBit();
        if (ltMin) {
            res = readIntMinMax(0, min);
        } else {
            res = readIntMinMax(min, max);
        }
        return res;
    }

    public int readUIntLt16ElseMax(int max) {
        return readUIntLtMinElseMax(16, max);
    }

    public int readUIntLt2048ElseMax(int max) {
        return readUIntLtMinElseMax(2048, max);
    }

    public int readUInt0ElseMax(int max) {
        int res;
        boolean is0 = readBit();
        if (is0) {
            res = 0;
        } else {
            res = readIntMinMax(1, max);
        }
        return res;
    }

}
