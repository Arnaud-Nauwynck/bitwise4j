package fr.an.bitwise4j.encoder.structio;

import java.io.DataInputStream;
import java.io.IOException;

import fr.an.bitwise4j.bits.BitInputStream;
import fr.an.bitwise4j.encoder.huffman.AbstractHuffmanNode;
import fr.an.bitwise4j.encoder.huffman.HuffmanTable;
import fr.an.bitwise4j.encoder.huffman.HuffmanTreeLeaf;
import fr.an.bitwise4j.encoder.huffman.HuffmanTreeNode;
import fr.an.bitwise4j.util.RuntimeIOException;

/**
 * implements StructDataInput using underlying BitInputStream
 * 
 * cf similar java.io.DataInputStream (where underlying stream is a stream of bytes, instead of bits)
 */
public class BitStreamStructDataInput extends StructDataInput {

    private BitInputStream in;
    
    // ------------------------------------------------------------------------

    public BitStreamStructDataInput(BitInputStream target) {
        this.in = target;
    }

    @Override
    public void close() {
        if (in != null) {
            in.close();
            this.in = null;
        }
    }
    
    // ------------------------------------------------------------------------

    @Override
    public String getCurrStream() {
        if (in instanceof IStreamMultiplexerSupport) {
            return ((IStreamMultiplexerSupport) in).getCurrStream();
        }
        return null;
    }
    
    @Override
    public String setCurrStream(String name) {
        if (in instanceof IStreamMultiplexerSupport) {
            return ((IStreamMultiplexerSupport) in).setCurrStream(name);
        }
        return null;
    }
    
    @Override
    public boolean hasMoreBit() {
        return in.hasMoreBit();
    }

    @Override
    public void readSkipPaddingTo8() {
        in.readSkipPaddingTo8();
    }

    @Override
    public boolean readBit() {
        return in.readBit();
    }

    @Override
    public int readBits(int readBitsCounts) {
        return in.readBits(readBitsCounts);
    }

    @Deprecated
    public int readNBits(int count) {
        return in.readBits(count);
    }
    
    @Override
    public int readUInt0N(int maxNExclusive) {
        int nBits = Pow2Utils.valueToUpperLog2(maxNExclusive);
        return readBits(nBits);
    }
    
    @Override
    public int readIntMinMax(int fromMin, int toMax) {
        int maxAmplitude = toMax - fromMin;
        int nBits = Pow2Utils.valueToUpperLog2(maxAmplitude);
        return fromMin + readBits(nBits);
    }

    @Override
    public byte readByte() {
        return (byte) in.read();
    }
    
    @Override
    public void readBytes(byte[] dest, int len) {
        readBytes(dest, 0, len);
    }
    
    @Override
    public void readBytes(byte[] dest, int offset, int len) {
        in.readBytes(dest, offset, len);
    }
    
    @Override
    public int readInt() {
        return readBits(32);
    }
    
    @Override
    public void readInts(int[] dest, int offset, int len) {
        final int maxI = offset + len;
        for (int i = offset; i < maxI; i++) {
            dest[i] = readInt();
        }
    }
    
    @Override
    public float readFloat() {
        int tmpBits32 = readBits(32);
        return Float.intBitsToFloat(tmpBits32);
    }
    
    @Override
    public double readDouble() {
        int tmp1 = readBits(32);
        int tmp2 = readBits(32);
        long bits64 = (((long) tmp1) << 32) + tmp2;
        return Double.longBitsToDouble(bits64);
    }

    // cf java.io.DataInputStream
    @Override
    public String readUTF() {
        DataInputStream din = new DataInputStream(in);
        try {
            return din.readUTF();
        } catch (IOException e) {
            throw new RuntimeIOException("readUTF", e);
        }
    }

    @Override
    public <T> T readDecodeHuffmanCode(HuffmanTable<T> table) {
        T res;
        HuffmanTreeNode<T> node = table.getRootNode();
        for(;;) {
            boolean bit = in.readBit();
            AbstractHuffmanNode<T> childNode = node.getChildLeftRight(bit);
            if (childNode instanceof HuffmanTreeLeaf) {
                res = ((HuffmanTreeLeaf<T>) childNode).getSymbol();
                break;
            } else {
                node = (HuffmanTreeNode<T>) childNode;
            }
        }
        return res;
    }

    @Override
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

    @Override
    public int readUIntLt16ElseMax(int max) {
        return readUIntLtMinElseMax(16, max);
    }

    @Override
    public int readUIntLt2048ElseMax(int max) {
        return readUIntLtMinElseMax(2048, max);
    }

    @Override
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

    @Override
    public void readIntsSorted(int min, int max, boolean distincts, int[] dest, int fromIndex, int toIndex) {
        recursiveReadIntsSorted(min, max, distincts, dest, fromIndex, toIndex);
    }

    private void recursiveReadIntsSorted(int min, int max, boolean distincts, int[] dest, int fromIndex, int toIndex) {
        // divide&conquer ... first encode mid, then recursive left, then recursive right
        int midIndex = (toIndex + fromIndex) >>> 1;
        int midValue = readIntMinMax(min, max);
        dest[midIndex] = midValue; 
        if (fromIndex < midIndex) {
            recursiveReadIntsSorted(min, (distincts)? midValue-1:midValue, distincts, dest, fromIndex, midIndex);  
        }
        if (midIndex+1 < toIndex) {
            recursiveReadIntsSorted((distincts)? midValue+1:midValue, max, distincts, dest, midIndex+1, toIndex);  
        }
    }
 
}
