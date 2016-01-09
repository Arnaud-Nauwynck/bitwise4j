package fr.an.bitwise4j.encoder.structio.helpers;

import java.io.IOException;

import fr.an.bitwise4j.encoder.huffman.HuffmanBitsCode;
import fr.an.bitwise4j.encoder.structio.StructDataOutput;

/**
 * debug helper class implementation of StructDataOutput for writing twice from
 * both outputs
 * 
 */
public class DebugTeeStructDataOutput extends StructDataOutput {

    private StructDataOutput out1;

    private StructDataOutput out2;

    // ------------------------------------------------------------------------

    public DebugTeeStructDataOutput(StructDataOutput out1, StructDataOutput out2) {
        super();
        this.out1 = out1;
        this.out2 = out2;
    }

    // ------------------------------------------------------------------------

    @Override
    public void close() {
        if (out1 != null) {
            out1.close();
            out1 = null;
        }
        if (out2 != null) {
            out2.close();
            out2 = null;
        }
    }

    @Override
    public void flush() throws IOException {
        out1.flush();
        out2.flush();
    }
    
    @Override
    public void debugComment(String msg) {
        out1.debugComment(msg);
        out2.debugComment(msg);
    }

    @Override
    public String getCurrStream() {
        return out1.getCurrStream();
    }

    @Override
    public String setCurrStream(String name) {
        String res = out1.setCurrStream(name);
        out2.setCurrStream(name);
        return res;
    }

    @Override
    public void writeBit(boolean value) {
        out1.writeBit(value);
        out2.writeBit(value);
    }

    @Override
    public void writeNBits(int count, int bitsValue) {
        out1.writeNBits(count, bitsValue);
        out2.writeNBits(count, bitsValue);
    }

    @Override
    public void writeUInt0N(int maxN, int value) {
        out1.writeUInt0N(maxN, value);
        out2.writeUInt0N(maxN, value);
    }

    @Override
    public void writeIntMinMax(int fromMin, int toMax, int value) {
        out1.writeIntMinMax(fromMin, toMax, value);
        out2.writeIntMinMax(fromMin, toMax, value);
    }

    @Override
    public void write(int value) {
        out1.write(value);
        out2.write(value);
    }

    @Override
    public void writeByte(byte value) {
        out1.writeByte(value);
        out2.writeByte(value);
    }

    @Override
    public void writeBytes(byte[] dest, int len) {
        out1.writeBytes(dest, len);
        out2.writeBytes(dest, len);
    }

    @Override
    public void writeBytes(byte[] dest, int offset, int len) {
        out1.writeBytes(dest, offset, len);
        out2.writeBytes(dest, offset, len);
    }

    @Override
    public void writeInt(int value) {
        out1.writeInt(value);
        out2.writeInt(value);
    }

    @Override
    public void writeInts(int[] values, int offset, int len) {
        out1.writeInts(values, offset, len);
        out2.writeInts(values, offset, len);
    }

    @Override
    public void writeFloat(float value) {
        out1.writeFloat(value);
        out2.writeFloat(value);
    }

    @Override
    public void writeDouble(double value) {
        out1.writeDouble(value);
        out2.writeDouble(value);
    }

    @Override
    public void writeUTF(String value) {
        out1.writeUTF(value);
        out2.writeUTF(value);
    }

    @Override
    public void writeHuffmanCode(HuffmanBitsCode code) {
        out1.writeHuffmanCode(code);
        out2.writeHuffmanCode(code);
    }

    @Override
    public void writeUIntLtMinElseMax(int min, int max, int value) {
        out1.writeUIntLtMinElseMax(min, max, value);
        out2.writeUIntLtMinElseMax(min, max, value);
    }

    @Override
    public void writeUIntLt16ElseMax(int max, int value) {
        out1.writeUIntLt16ElseMax(max, value);
        out2.writeUIntLt16ElseMax(max, value);
    }

    @Override
    public void writeUIntLt2048ElseMax(int max, int value) {
        out1.writeUIntLt2048ElseMax(max, value);
        out2.writeUIntLt2048ElseMax(max, value);
    }

    @Override
    public void writeUInt0ElseMax(int max, int value) {
        out1.writeUInt0ElseMax(max, value);
        out2.writeUInt0ElseMax(max, value);
    }

    @Override
    public void write(byte[] b) throws IOException {
        out1.write(b);
        out2.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        out1.write(b, off, len);
        out2.write(b, off, len);
    }

    // ------------------------------------------------------------------------
    
    @Override
    public String toString() {
        return "DebugTeeStructDataOutput[]";
    }

}
