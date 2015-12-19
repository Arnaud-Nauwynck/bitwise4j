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
public class BitStreamStructDataInput implements StructDataInput {

    private BitInputStream in;
    
    // ------------------------------------------------------------------------

    public BitStreamStructDataInput(BitInputStream target) {
        this.in = target;
    }

    // ------------------------------------------------------------------------
    
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
    
}
