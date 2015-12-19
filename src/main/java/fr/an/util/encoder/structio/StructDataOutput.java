package fr.an.util.encoder.structio;

public interface StructDataOutput {

    public void writeBit(boolean value);

    public void writeNBits(int count, int bitsValue);
    
    public void writeUInt0N(int maxN, int value);
    public void writeIntMinMax(int fromMin, int toMax, int value);

    public void writeByte(byte value);
    public void writeBytes(byte[] dest, int len);
    public void writeBytes(byte[] dest, int offset, int len);
    
    public void writeInt(int value);
    public void writeFloat(float value);
    public void writeDouble(double value);

    public void writeUTF(String value);
    
    // TODO
    // void writeIntHuffman(HuffmanTable table, int value);

}
