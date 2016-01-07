package fr.an.util.encoder.structio.helpers;

import java.io.IOException;
import java.util.Arrays;

import fr.an.util.encoder.huffman.HuffmanTable;
import fr.an.util.encoder.structio.StructDataInput;

/**
 * debug helper class implementation of StructDataInput for reading twice from both inputs, and check compare them
 * 
 */
public class DebugTeeStructDataInput extends StructDataInput {

    private StructDataInput in1;

    private StructDataInput in2;
    
    private int countInstr;
    
    // ------------------------------------------------------------------------

    public DebugTeeStructDataInput(StructDataInput in1, StructDataInput in2) {
        this.in1 = in1;
        this.in2 = in2;
    }
    
    // ------------------------------------------------------------------------

    private RuntimeException failEx(String msg) {
        return new RuntimeException("Failed at line " + countInstr + ": " + msg);
    }

    @Override
    public int read() {
        countInstr++;
        int res1 = in1.read();
        int res2 = in2.read();
        if (res1 != res2) throw failEx("read() " + res1 + " != " + res2);
        return res1;
    }

    @Override
    public void close() {
        in1.close();
        in2.close();
    }

    @Override
    public boolean hasMoreBit() {
        boolean res1 = in1.hasMoreBit();
        boolean res2 = in2.hasMoreBit();
        if (res1 != res2) throw failEx("hasMoreBit() " + res1 + " != " + res2);
        return res1;
    }

    @Override
    public void readSkipPaddingTo8() {
        in1.readSkipPaddingTo8();
        in2.readSkipPaddingTo8();
    }
    
    @Override
    public boolean readBit() {
        countInstr++;
        boolean res1 = in1.readBit();
        boolean res2 = in2.readBit();
        if (res1 != res2) throw failEx("readBit() " + res1 + " != " + res2);
        return res1;
    }

    @Override
    public int readBits(int count) {
        countInstr++;
        int res1 = in1.readBits(count);
        int res2 = in2.readBits(count);
        if (res1 != res2) throw failEx("readBit() " + res1 + " != " + res2);
        return res1;
    }

    @Override
    public int readUInt0N(int maxN) {
        countInstr++;
        int res1 = in1.readUInt0N(maxN);
        int res2 = in2.readUInt0N(maxN);
        if (res1 != res2) throw failEx("readUInt0N(" + maxN + ") " + res1 + " != " + res2);
        return res1;
    }

    @Override
    public int readIntMinMax(int fromMin, int toMax) {
        countInstr++;
        int res1 = in1.readIntMinMax(fromMin, toMax);
        int res2 = in2.readIntMinMax(fromMin, toMax);
        if (res1 != res2) throw failEx("readIntMinMax(" + fromMin +", " + toMax + ") " + res1 + " != " + res2);
        return res1;
    }

    @Override
    public byte readByte() {
        countInstr++;
        byte res1 = in1.readByte();
        byte res2 = in2.readByte();
        if (res1 != res2) throw failEx("readByte() " + res1 + " != " + res2);
        return res1;
    }

    @Override
    public void readBytes(byte[] dest, int len) {
        countInstr++;
        readBytes(dest, 0, len);
    }

    @Override
    public void readBytes(byte[] dest, int offset, int len) {
        countInstr++;
        in1.readBytes(dest, offset, len);
        byte[] b2 = new byte[len];
        in2.readBytes(b2, 0, len);
        int foundDiffI1 = arrayFindFirstDiff(dest, offset, b2, 0, len);
        if (foundDiffI1 != -1) {
            byte elt1 = dest[foundDiffI1];
            byte elt2 = b2[foundDiffI1-offset];
            throw failEx("readByte() " + " [" + foundDiffI1 + "] " + elt1 + " != " + elt2 
                    + ", detailed arrays: " + Arrays.toString(dest) + " != " + Arrays.toString(b2));
        }
    }

    @Override
    public int readInt() {
        countInstr++;
        int res1 = in1.readInt();
        int res2 = in2.readInt();
        if (res1 != res2) throw failEx("readInt() " + res1 + " != " + res2);
        return res1;
    }

    @Override
    public void readInts(int[] dest, int offset, int len) {
        countInstr++;
        in1.readInts(dest, offset, len);
        int[] dest2 = new int[len];
        in2.readInts(dest2, 0, len);
        int foundDiff1 = arrayFindFirstDiff(dest, offset, dest2, 0, len);
        if (foundDiff1 != -1) {
            int elt1 = dest[foundDiff1];
            int elt2 = dest2[foundDiff1-offset];
            throw failEx("readInts(" + offset + ", len) [" + foundDiff1 + "] "+ elt1 + " != " + elt2);
        }
    }

    @Override
    public float readFloat() {
        countInstr++;
        float res1 = in1.readFloat();
        float res2 = in2.readFloat();
        if (res1 != res2) throw failEx("readFloat() " + res1 + " != " + res2);
        return res1;
    }

    @Override
    public double readDouble() {
        countInstr++;
        double res1 = in1.readDouble();
        double res2 = in2.readDouble();
        if (res1 != res2) throw failEx("readDouble() " + res1 + " != " + res2);
        return res1;
    }

    @Override
    public String readUTF() {
        countInstr++;
        String res1 = in1.readUTF();
        String res2 = in2.readUTF();
        if (! res1.equals(res2)) throw failEx("readUTF() '" + res1 + "' != '" + res2 + "'");
        return res1;
    }

    @Override
    public <T> T readDecodeHuffmanCode(HuffmanTable<T> table) {
        countInstr++;
        T res1 = in1.readDecodeHuffmanCode(table);
        T res2 = in2.readDecodeHuffmanCode(table);
        if (res1 != res2) throw failEx("readDecodeHuffmanCode() " + res1 + " != " + res2);
        return res1;
    }

    @Override
    public int readUIntLtMinElseMax(int min, int max) {
        countInstr++;
        int res1 = in1.readUIntLtMinElseMax(min, max);
        int res2 = in2.readUIntLtMinElseMax(min, max);
        if (res1 != res2) throw failEx("readUIntLtMinElseMax(" + min + ", " + max + ") " + res1 + " != " + res2);
        return res1;
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
        countInstr++;
        int res1 = in1.readUInt0ElseMax(max);
        int res2 = in2.readUInt0ElseMax(max);
        if (res1 != res2) throw failEx("readUInt0ElseMax(" + max + ") " + res1 + " != " + res2);
        return res1;
    }

    @Override
    public int read(byte[] b) {
        countInstr++;
        return read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) {
        readBytes(b, off, len);
        return len;
    }

    @Override
    public long skip(long n) throws IOException {
        countInstr++;
        long res1 = in1.skip(n);
        long res2 = in2.skip(n);
        if (res1 != res2) throw failEx("skip(" + n + ") " + res1 + " != " + res2);
        return res1;
    }

    @Override
    public int available() throws IOException {
        // DO NOT check?
        return in1.available();
    }

    @Override
    public void mark(int readlimit) {
        in1.mark(readlimit);
        in2.mark(readlimit);
    }

    @Override
    public void reset() throws IOException {
        in1.reset();
        in2.reset();
    }

    @Override
    public boolean markSupported() {
        return in1.markSupported() && in2.markSupported();
    }

    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        return "DebugTeeStructDataInput [countInstr=" + countInstr + "]";
    }
    
    private static int arrayFindFirstDiff(byte[] src1, int offset1, byte[] src2, int offset2, int len) {
        final int maxI1 = offset1 + len;
        for(int i1 = offset1, i2 = offset2; i1 < maxI1; i1++,i2++) {
            if (src1[i1] != src2[i2]) {
                return i1;
            }
        }
        return -1;
    }

    private static int arrayFindFirstDiff(int[] src1, int offset1, int[] src2, int offset2, int len) {
        final int maxI1 = offset1 + len;
        for(int i1 = offset1, i2 = offset2; i1 < maxI1; i1++,i2++) {
            if (src1[i1] != src2[i2]) {
                return i1;
            }
        }
        return -1;
    }

}
