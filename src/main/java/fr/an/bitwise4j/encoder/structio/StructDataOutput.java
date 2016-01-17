package fr.an.bitwise4j.encoder.structio;

import fr.an.bitwise4j.bits.BitOutputStream;
import fr.an.bitwise4j.encoder.huffman.HuffmanBitsCode;

/**
 * similar to java.io.DataOutput but for structured input using underlying bit stream (instead of bytes)
 * with dedicated methods for compressing
 * 
 * implementation note: use abstract class instead of interface ... for performance
 */
public abstract class StructDataOutput extends BitOutputStream implements IStreamMultiplexerSupport {

    public abstract void writeBit(boolean value);

    public abstract void writeNBits(int count, int bitsValue);
    
    public abstract void writeUInt0N(int maxN, int value);
    public abstract void writeIntMinMax(int fromMin, int toMax, int value);

    public abstract void writeByte(byte value);
    public abstract void writeBytes(byte[] dest, int len);
    public abstract void writeBytes(byte[] dest, int offset, int len);
    
    public abstract void writeInt(int value);
    public abstract void writeInts(int[] values, int offset, int len);
    public abstract void writeFloat(float value);
    public abstract void writeDouble(double value);

    public abstract void writeUTF(String value);
    
    public abstract void writeHuffmanCode(HuffmanBitsCode code);

    public abstract void writeUIntLtMinElseMax(int min, int max, int value);
    public abstract void writeUIntLt16ElseMax(int max, int value);
    public abstract void writeUIntLt2048ElseMax(int max, int value);
    public abstract void writeUInt0ElseMax(int max, int value);


    public abstract void debugComment(String msg); 

    public abstract void writeIntsSorted(int min, int max, boolean distincts, int[] values, int fromIndex, int toIndex);
//    public abstract void writeIntsSortedSegments(int min, int max, int[] values, int fromIndex, int toIndex);
    
}
