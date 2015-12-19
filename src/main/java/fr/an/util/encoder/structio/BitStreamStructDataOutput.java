package fr.an.util.encoder.structio;

import java.io.DataOutputStream;
import java.io.IOException;

import fr.an.util.bits.BitOutputStream;
import fr.an.util.bits.RuntimeIOException;

/**
 * implements StructDataOutput using underlying BitOutputStream
 * 
 * cf similar java.io.DataOutputStream (where underlying stream is a stream of bytes, instead of bits)
 */
public class BitStreamStructDataOutput implements StructDataOutput {

    private BitOutputStream out;
    
    // ------------------------------------------------------------------------

    public BitStreamStructDataOutput(BitOutputStream out) {
        this.out = out;
    }

    // ------------------------------------------------------------------------
    
    public void writeBit(boolean value) {
        out.writeBit(value);
    }

    public void writeNBits(int count, int bitsValue) {
        out.writeNBits(count, bitsValue); // warn: swap param order
    }
    
    public void writeUInt0N(int maxNExclusive, int value) {
        int nBits = Pow2Utils.valueToUpperLog2(maxNExclusive);
        writeNBits(nBits, value);
    }
    
    public void writeIntMinMax(int fromMin, int toMax, int value) {
        int offsetValue = value - fromMin;
        int maxAmplitude = toMax - fromMin;
        int nBits = Pow2Utils.valueToUpperLog2(maxAmplitude);
        writeNBits(nBits, offsetValue);
    }

    public void writeByte(byte value) {
        out.write(value);
    }
    
    public void writeBytes(byte[] dest, int len) {
        writeBytes(dest, 0, len);
    }
    
    public void writeBytes(byte[] dest, int offset, int len) {
        out.writeBytes(dest, offset, len);
    }
    
    public void writeInt(int value) {
        writeNBits(32, value);
    }
    
    public void writeFloat(float value) {
        int tmpBits32 = Float.floatToIntBits(value);
        writeNBits(32, tmpBits32);
    }
    
    public void writeDouble(double value) {
        long bits64 = Double.doubleToLongBits(value);
        int tmp1 = (int) (bits64 >>> 32);
        int tmp2 = (int) (bits64 & 0xFFFF);
        writeInt(tmp1);
        writeInt(tmp2);
    }

    // cf java.io.DataOutputStream
    public void writeUTF(String value) {
        DataOutputStream din = new DataOutputStream(out);
        try {
            din.writeUTF(value);
        } catch (IOException e) {
            throw new RuntimeIOException("writeUTF", e);
        }
    }
    
}
