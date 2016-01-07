package fr.an.util.encoder.structio.helpers;

import java.io.IOException;

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

    public int read() {
        countInstr++;
        int res1 = in1.read();
        int res2 = in2.read();
        if (res1 != res2) throw failEx("read() " + res1 + " != " + res2);
        return res1;
    }


    public void close() {
        in1.close();
        in2.close();
    }

    public boolean hasMoreBit() {
        boolean res1 = in1.hasMoreBit();
        boolean res2 = in2.hasMoreBit();
        if (res1 != res2) throw failEx("hasMoreBit() " + res1 + " != " + res2);
        return res1;
    }

    public boolean readBit() {
        countInstr++;
        boolean res1 = in1.readBit();
        boolean res2 = in2.readBit();
        if (res1 != res2) throw failEx("readBit() " + res1 + " != " + res2);
        return res1;
    }

    public int readBits(int count) {
        countInstr++;
        int res1 = in1.readBits(count);
        int res2 = in2.readBits(count);
        if (res1 != res2) throw failEx("readBit() " + res1 + " != " + res2);
        return res1;
    }

    public int readUInt0N(int maxN) {
        countInstr++;
        int res1 = in1.readUInt0N(maxN);
        int res2 = in2.readUInt0N(maxN);
        if (res1 != res2) throw failEx("readUInt0N(" + maxN + ") " + res1 + " != " + res2);
        return res1;
    }

    public int readIntMinMax(int fromMin, int toMax) {
        countInstr++;
        int res1 = in1.readIntMinMax(fromMin, toMax);
        int res2 = in2.readIntMinMax(fromMin, toMax);
        if (res1 != res2) throw failEx("readIntMinMax(" + fromMin +", " + toMax + ") " + res1 + " != " + res2);
        return res1;
    }

    public byte readByte() {
        countInstr++;
        byte res1 = in1.readByte();
        byte res2 = in2.readByte();
        if (res1 != res2) throw failEx("readByte() " + res1 + " != " + res2);
        return res1;
    }

    public void readBytes(byte[] dest, int len) {
        countInstr++;
        readBytes(dest, 0, len);
    }

    public void readBytes(byte[] dest, int offset, int len) {
        countInstr++;
        in1.readBytes(dest, offset, len);
        byte[] b2 = new byte[len];
        in2.readBytes(b2, 0, len);
        if (arrayCompare(dest, offset, b2, 0, len)) {
            throw failEx("readByte() " + dest + " != " + b2);
        }
    }

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

    public float readFloat() {
        countInstr++;
        float res1 = in1.readFloat();
        float res2 = in2.readFloat();
        if (res1 != res2) throw failEx("readFloat() " + res1 + " != " + res2);
        return res1;
    }

    public double readDouble() {
        countInstr++;
        double res1 = in1.readDouble();
        double res2 = in2.readDouble();
        if (res1 != res2) throw failEx("readDouble() " + res1 + " != " + res2);
        return res1;
    }

    public String readUTF() {
        countInstr++;
        String res1 = in1.readUTF();
        String res2 = in2.readUTF();
        if (! res1.equals(res2)) throw failEx("readUTF() '" + res1 + "' != '" + res2 + "'");
        return res1;
    }

    public <T> T readDecodeHuffmanCode(HuffmanTable<T> table) {
        countInstr++;
        T res1 = in1.readDecodeHuffmanCode(table);
        T res2 = in2.readDecodeHuffmanCode(table);
        if (res1 != res2) throw failEx("readDecodeHuffmanCode() " + res1 + " != " + res2);
        return res1;
    }

    public int readUIntLtMinElseMax(int min, int max) {
        countInstr++;
        int res1 = in1.readUIntLtMinElseMax(min, max);
        int res2 = in2.readUIntLtMinElseMax(min, max);
        if (res1 != res2) throw failEx("readUIntLtMinElseMax(" + min + ", " + max + ") " + res1 + " != " + res2);
        return res1;
    }

    public int readUIntLt16ElseMax(int max) {
        return readUIntLtMinElseMax(16, max);
    }

    public int readUIntLt2048ElseMax(int max) {
        return readUIntLtMinElseMax(2048, max);
    }

    public int readUInt0ElseMax(int max) {
        countInstr++;
        int res1 = in1.readUInt0ElseMax(max);
        int res2 = in2.readUInt0ElseMax(max);
        if (res1 != res2) throw failEx("readUInt0ElseMax(" + max + ") " + res1 + " != " + res2);
        return res1;
    }

    public int read(byte[] b) throws IOException {
        countInstr++;
        return read(b, 0, b.length);
    }

    public int read(byte[] b, int off, int len) throws IOException {
        countInstr++;
        int res1 = in1.read(b, off, len);
        byte[] b2 = new byte[len];
        int res2 = in2.read(b, 0, len);
        if (res1 != res2) throw failEx("read(" + off + ", " + len + ") " + res1 + " != " + res2);
        if (! arrayCompare(b, off, b2, 0, len)) {
            throw failEx("read(" + off + ", " + len + ") " + res1 +  "!= " + res2);
        }
        return res1;
    }

    public long skip(long n) throws IOException {
        countInstr++;
        long res1 = in1.skip(n);
        long res2 = in2.skip(n);
        if (res1 != res2) throw failEx("skip(" + n + ") " + res1 + " != " + res2);
        return res1;
    }

    public int available() throws IOException {
        // DO NOT check?
        return in1.available();
    }

    public void mark(int readlimit) {
        in1.mark(readlimit);
        in2.mark(readlimit);
    }

    public void reset() throws IOException {
        in1.reset();
        in2.reset();
    }

    public boolean markSupported() {
        return in1.markSupported() && in2.markSupported();
    }

    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        return "DebugTeeStructDataInput [countInstr=" + countInstr + "]";
    }

    
    
    private static boolean arrayCompare(byte[] src1, int offset1, byte[] src2, int offset2, int len) {
        final int maxI1 = offset1 + len;
        for(int i1 = offset1, i2 = offset2; i1 < maxI1; i1++,i2++) {
            if (src1[i1] != src2[i2]) {
                return false;
            }
        }
        return true;
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
