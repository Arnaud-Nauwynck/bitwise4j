package fr.an.util.encoder.structio;

public interface StructDataInput {

    public boolean readBit();

    public int readNBits(int count);
    
    public int readUInt0N(int maxN);
    public int readIntMinMax(int fromMin, int toMax);

    public byte readByte();
    public void readBytes(byte[] dest, int len);
    public void readBytes(byte[] dest, int offset, int len);
    public int readInt();
    public float readFloat();
    public double readDouble();

    public String readUTF();
    
    // TODO
    // int readIntHuffman(HuffmanTable table);
    
}
