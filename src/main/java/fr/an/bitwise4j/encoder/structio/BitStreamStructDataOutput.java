package fr.an.bitwise4j.encoder.structio;

import java.io.DataOutputStream;
import java.io.IOException;

import fr.an.bitwise4j.bits.BitOutputStream;
import fr.an.bitwise4j.encoder.huffman.HuffmanBitsCode;
import fr.an.bitwise4j.util.RuntimeIOException;

/**
 * implements StructDataOutput using underlying BitOutputStream
 * 
 * cf similar java.io.DataOutputStream (where underlying stream is a stream of bytes, instead of bits)
 */
public class BitStreamStructDataOutput extends StructDataOutput {

    private BitOutputStream out;
    
    // ------------------------------------------------------------------------

    public BitStreamStructDataOutput(BitOutputStream out) {
        this.out = out;
    }

    // ------------------------------------------------------------------------
    
    @Override
    public void close() {
        if (out != null) {
            out.close();
            out = null;
        }
    }

    @Override
    public void debugComment(String msg) {
        // ignore!
    }

    @Override
    public String getCurrStream() {
        if (out instanceof IStreamMultiplexerSupport) {
            return ((IStreamMultiplexerSupport) out).getCurrStream();
        }
        return null;
    }
    
    @Override
    public String setCurrStream(String name) {
        if (out instanceof IStreamMultiplexerSupport) {
            return ((IStreamMultiplexerSupport) out).setCurrStream(name);
        }
        return null;
    }
    
    @Override
    public void writeBit(boolean value) {
        out.writeBit(value);
    }

    @Override
    public void writeNBits(int count, int bitsValue) {
        out.writeNBits(count, bitsValue); // warn: swap param order
    }
    
    @Override
    public void writeUInt0N(int maxNExclusive, int value) {
        int nBits = Pow2Utils.valueToUpperLog2(maxNExclusive);
        writeNBits(nBits, value);
    }
    
    @Override
    public void writeIntMinMax(int fromMin, int toMax, int value) {
        int offsetValue = value - fromMin;
        int maxAmplitude = toMax - fromMin;
        int nBits = Pow2Utils.valueToUpperLog2(maxAmplitude);
        writeNBits(nBits, offsetValue);
    }

    @Override
    public void writeByte(byte value) {
        out.write(value);
    }
    
    @Override
    public void writeBytes(byte[] dest, int len) {
        writeBytes(dest, 0, len);
    }
    
    @Override
    public void writeBytes(byte[] dest, int offset, int len) {
        out.writeBytes(dest, offset, len);
    }
    
    @Override
    public void writeInt(int value) {
        writeNBits(32, value);
    }
    
    @Override
    public void writeInts(int[] values, int offset, int len) {
        final int maxI = offset + len;
        for(int i = offset; i < maxI; i++) {
            writeInt(values[i]);
        }
    }
    
    @Override
    public void writeFloat(float value) {
        int tmpBits32 = Float.floatToIntBits(value);
        writeNBits(32, tmpBits32);
    }
    
    @Override
    public void writeDouble(double value) {
        long bits64 = Double.doubleToLongBits(value);
        int tmp1 = (int) (bits64 >>> 32);
        int tmp2 = (int) (bits64 & 0xFFFF);
        writeInt(tmp1);
        writeInt(tmp2);
    }

    // cf java.io.DataOutputStream
    @Override
    public void writeUTF(String value) {
        DataOutputStream din = new DataOutputStream(out);
        try {
            din.writeUTF(value);
        } catch (IOException e) {
            throw new RuntimeIOException("writeUTF", e);
        }
    }
    
    @Override
    public void writeHuffmanCode(HuffmanBitsCode code) {
        writeNBits(code.getBitsCount(), code.getBits());
    }

    @Override
    public void writeUIntLtMinElseMax(int min, int max, int value) {
        if (value < min) {
            writeBit(true);
            writeIntMinMax(0, min, value);
        } else {
            writeBit(false);
            writeIntMinMax(min, max, value);
        }
    }

    @Override
    public void writeUIntLt16ElseMax(int max, int value) {
        writeUIntLtMinElseMax(16, max, value);
    }

    @Override
    public void writeUIntLt2048ElseMax(int max, int value) {
        writeUIntLtMinElseMax(2048, max, value);
    }

    @Override
    public void writeUInt0ElseMax(int max, int value) {
        if (value == 0) {
            writeBit(true);
        } else {
            writeBit(false);
            writeIntMinMax(1, max, value);
        }
    }

    @Override
    public void writeIntsSorted(int min, int max, boolean distincts, int[] values, int fromIndex, int toIndex) {
        recursiveWriteIntsSorted(min, max, distincts, values, fromIndex, toIndex);
    }

    private void recursiveWriteIntsSorted(int min, int max, boolean distincts, int[] values, int fromIndex, int toIndex) {
        // divide&conquer ... first encode mid, then recursive left, then recursive right
        int midIndex = (toIndex + fromIndex) >>> 1;
        int midValue = values[midIndex];
        writeIntMinMax(min, max, midValue);
        if (fromIndex < midIndex) {
            recursiveWriteIntsSorted(min, (distincts)? midValue-1:midValue, distincts, values, fromIndex, midIndex);  
        }
        if (midIndex+1 < toIndex) {
            recursiveWriteIntsSorted((distincts)? midValue+1:midValue, max, distincts, values, midIndex+1, toIndex);  
        }
    }
    
}
