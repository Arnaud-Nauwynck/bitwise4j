package fr.an.util.encoder.structio;

import fr.an.util.bits.BitInputStream;
import fr.an.util.encoder.huffman.HuffmanBitsCode;
import fr.an.util.encoder.huffman.HuffmanTable;

/**
 * similar to java.io.DataInput but for structured input using underlying bit stream (instead of bytes)
 * with dedicated methods for compressing
 * 
 * implementation note: use abstract class instead of interface ... for performance
 */
public abstract class StructDataInput extends BitInputStream {

    public abstract void close();
    public abstract boolean hasMoreBit();
    
    public abstract boolean readBit();
    public abstract int readBits(int count);
    
    public abstract int readUInt0N(int maxN);
    public abstract int readIntMinMax(int fromMin, int toMax);

    public abstract byte readByte();
    public abstract void readBytes(byte[] dest, int len);
    public abstract void readBytes(byte[] dest, int offset, int len);
    public abstract int readInt();
    public abstract void readInts(int[] dest, int offset, int len);
    public abstract float readFloat();
    public abstract double readDouble();

    public abstract String readUTF();
    
    public abstract <T> T readDecodeHuffmanCode(HuffmanTable<T> table);

    public abstract int readUIntLtMinElseMax(int min, int max);
    public abstract int readUIntLt16ElseMax(int max);
    public abstract int readUIntLt2048ElseMax(int max);
    public abstract int readUInt0ElseMax(int max);
        
    // TODO
    // public abstract int readIntHuffman(HuffmanTable table);
    
}
