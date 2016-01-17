package fr.an.bitwise4j.encoder.structio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.Consumer;

import org.junit.Assert;
import org.junit.Test;

import fr.an.bitwise4j.bits.BitOutputStream;
import fr.an.bitwise4j.bits.InputStreamToBitInputStream;
import fr.an.bitwise4j.bits.OutputStreamToBitOutputStream;
import fr.an.bitwise4j.util.AssertArrayUtils;

public class BitStreamStructDataInputTest {

    protected static BitStreamStructDataInput prepareReplay(Consumer<StructDataOutput> replayOut) {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        BitOutputStream bitOut = new OutputStreamToBitOutputStream(byteBuffer);
        try (StructDataOutput dataOut = new BitStreamStructDataOutput(bitOut)) {
            replayOut.accept(dataOut);
        }
        
        InputStreamToBitInputStream bitInput = new InputStreamToBitInputStream(new ByteArrayInputStream(byteBuffer.toByteArray()));
        BitStreamStructDataInput sut = new BitStreamStructDataInput(bitInput);
        return sut;
    }
    
    @Test
    public void testReadBit() throws IOException {
        // Prepare
        BitStreamStructDataInput sut = prepareReplay(out -> {
            out.writeBit(true);
            out.writeBit(false);
            out.writeNBits(4, 0b0101);
        });
        // Perform & Post-check
        Assert.assertTrue(sut.readBit());
        Assert.assertFalse(sut.readBit());
        Assert.assertEquals(0b0101, sut.readBits(4));
        // Post-check
        sut.close();
    }

    @Test
    public void testReadBits() throws IOException {
        // Prepare
        BitStreamStructDataInput sut = prepareReplay(out -> {
            out.writeNBits(4, 0b0101);
            out.writeNBits(1, 0);
            out.writeNBits(1, 1);
        });
        // Perform
        Assert.assertEquals(0b0101, sut.readBits(4));
        Assert.assertEquals(0, sut.readBits(1));
        Assert.assertEquals(1, sut.readBits(1));
        // Post-check
        sut.close();
    }
    
    @Test
    public void testReadUInt0N() throws IOException {
        // Prepare
        BitStreamStructDataInput sut = prepareReplay(out -> {
            out.writeUInt0N(4, 1);
            out.writeUInt0N(4, 3);
            out.writeUInt0N(5, 4);
            out.writeUInt0N(7, 6);
        });
        // Perform
        Assert.assertEquals(1, sut.readUInt0N(4));
        Assert.assertEquals(3, sut.readUInt0N(4));
        Assert.assertEquals(4, sut.readUInt0N(5));
        Assert.assertEquals(6, sut.readUInt0N(7));
        // Post-check
        sut.close();
    }
    
    @Test
    public void testReadIntMinMax() throws IOException {
        // Prepare
        BitStreamStructDataInput sut = prepareReplay(out -> {
            out.writeIntMinMax(10, 200, 190);
            out.writeIntMinMax(10, 300, 290);
            out.flush();
            out.close();
        });
        // Perform
        Assert.assertEquals(190, sut.readIntMinMax(10, 200));
        Assert.assertEquals(290, sut.readIntMinMax(10, 300));
        // Post-check
        sut.close();
    }

    @Test
    public void testReadByte() throws IOException {
        // Prepare
        BitStreamStructDataInput sut = prepareReplay(out -> {
            out.writeByte((byte) 2);
            out.writeByte((byte) 127);
            out.writeByte((byte) 128);
            out.writeByte((byte) 255);
        });
        // Perform
        Assert.assertEquals(2, sut.readByte());
        Assert.assertEquals(127, sut.readByte());
        Assert.assertEquals((byte)128, sut.readByte());
        Assert.assertEquals((byte)255, sut.readByte());
        // Post-check
        sut.close();
    }
    
    @Test
    public void testReadBytes() throws IOException {
        // Prepare
        byte[] in = new byte[] { (byte)0, (byte)1, (byte) 254 };
        BitStreamStructDataInput sut = prepareReplay(out -> {
            out.writeBytes(in, 0, 3);
            out.flush();
            out.close();
        });
        // Perform
        byte[] res = new byte[3];
        sut.readBytes(res, 3);
        Assert.assertEquals(0, res[0]);
        Assert.assertEquals(1, res[1]);
        Assert.assertEquals((byte)254, res[2]);
        // Post-check
        sut.close();
    }
    
    @Test
    public void testReadInt() throws IOException {
        // Prepare
        int val = 134567;
        BitStreamStructDataInput sut = prepareReplay(out -> {
            out.writeInt(val);
            out.writeInt(-val);
        });
        // Perform
        Assert.assertEquals(val, sut.readInt());
        Assert.assertEquals(-val, sut.readInt());
        // Post-check
        sut.close();
    }
    
    @Test
    public void testReadInts() throws IOException {
        // Prepare
        int[] in1 = new int[] { 5, -12, 12, -27, 35, 56, 56, 79, 99 };
        BitStreamStructDataInput sut = prepareReplay(out -> {
            out.writeInts(in1, 0, in1.length);
            out.writeInts(in1, 3, 5);
        });
        // Perform
        int[] res1 = new int[in1.length];
        sut.readInts(res1, 0, in1.length);
        int[] subres1 = new int[5];
        sut.readInts(subres1, 0, 5);
        AssertArrayUtils.assertEquals(in1, res1);
        AssertArrayUtils.assertEquals(in1, 3, subres1, 0, 5);
        // Post-check
        sut.close();
    }
    

    
    @Test
    public void testReadFloat() throws IOException {
        // Prepare
        float f1 = 12.34e5f;
        BitStreamStructDataInput sut = prepareReplay(out -> {
            out.writeFloat(f1);
            out.writeFloat(-f1);
        });
        // Perform
        Assert.assertEquals(f1, sut.readFloat(), 1e-9);
        Assert.assertEquals(-f1, sut.readFloat(), 1e-9);
        // Post-check
        sut.close();
    }
    
    @Test
    public void testReadDouble() throws IOException {
        // Prepare
        double d1 = 12.34e5;
        BitStreamStructDataInput sut = prepareReplay(out -> {
            out.writeDouble(d1);
            out.writeDouble(-d1);
        });
        // Perform
        Assert.assertEquals(d1, sut.readDouble(), 1e-9);
        Assert.assertEquals(-d1, sut.readDouble(), 1e-9);
        // Post-check
        sut.close();
    }
    
    @Test
    public void testReadUTF() throws IOException {
        // Prepare
        String val = "abcABC123#àâéè";
        BitStreamStructDataInput sut = prepareReplay(out -> {
            out.writeUTF(val);
            out.writeUTF("!" + val);
            out.flush();
            out.close();
        });
        // Perform
        Assert.assertEquals(val, sut.readUTF());
        Assert.assertEquals("!" + val, sut.readUTF());
        // Post-check
        sut.close();
    }
    
    @Test
    public void testReadUIntLtMinElseMax() throws IOException {
        // Prepare
        BitStreamStructDataInput sut = prepareReplay(out -> {
            out.writeUIntLtMinElseMax(5, 20, 2);
            out.writeUIntLtMinElseMax(5, 20, 4);
            out.writeUIntLtMinElseMax(5, 20, 5);
            out.writeUIntLtMinElseMax(5, 20, 6);
            out.writeUIntLtMinElseMax(5, 20, 19);
        });
        // Perform
        Assert.assertEquals(2, sut.readUIntLtMinElseMax(5, 20));
        Assert.assertEquals(4, sut.readUIntLtMinElseMax(5, 20));
        Assert.assertEquals(5, sut.readUIntLtMinElseMax(5, 20));
        Assert.assertEquals(6, sut.readUIntLtMinElseMax(5, 20));
        Assert.assertEquals(19, sut.readUIntLtMinElseMax(5, 20));
        // Post-check
        sut.close();
    }

    @Test
    public void testReadUInt0ElseMax() throws IOException {
        // Prepare
        BitStreamStructDataInput sut = prepareReplay(out -> {
            out.writeUInt0ElseMax(8, 0);
            out.writeUInt0ElseMax(8, 1);
        });
        // Perform
        Assert.assertEquals(0, sut.readUInt0ElseMax(8));
        Assert.assertEquals(1, sut.readUInt0ElseMax(8));
        // Post-check
        sut.close();
    }

    @Test
    public void testReadIntsSorted() throws IOException {
        // Prepare
        int[] in1 = new int[] { 5, 12, 12, 27, 35, 56, 56, 79, 99 };
        int[] in2 = new int[] { 5, 12, 13, 14, 27, 35, 56, 79, 99 };
        BitStreamStructDataInput sut = prepareReplay(out -> {
            out.writeIntsSorted(5, 100, false, in1, 0, in1.length);
            out.writeIntsSorted(5, 100, true, in2, 0, in2.length);
        });
        // Perform
        int[] res1 = new int[in1.length];
        sut.readIntsSorted(5, 100, false, res1, 0, in1.length);
        int[] res2 = new int[in2.length];
        sut.readIntsSorted(5, 100, true, res2, 0, in2.length);
        AssertArrayUtils.assertEquals(in1, res1);
        AssertArrayUtils.assertEquals(in2, res2);
        // Post-check
        sut.close();
    }

}
