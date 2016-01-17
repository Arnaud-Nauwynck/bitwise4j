package fr.an.bitwise4j.encoder.structio.helpers;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;

import org.junit.Assert;
import org.junit.Test;

import fr.an.bitwise4j.util.AssertArrayUtils;

public class DebugStructDataInputTest {

    @Test
    public void testReadBit() throws IOException {
        // Prepare
        ByteArrayOutputStream dataOutBuffer = new ByteArrayOutputStream();
        DebugStructDataOutput dataOut = new DebugStructDataOutput(new PrintStream(dataOutBuffer));
        dataOut.writeBit(true);
        dataOut.writeBit(false);
        dataOut.writeNBits(4, 0b0101);
        dataOut.flush();
        dataOut.close();
        String txt = dataOutBuffer.toString();

        DebugStructDataInput sut = new DebugStructDataInput(new BufferedReader(new StringReader(txt)));
        // Perform & Post-check
        Assert.assertTrue(sut.readBit());
        Assert.assertFalse(sut.readBit());
        Assert.assertEquals(0b0101, sut.readBits(4));
        // Post-check
        Assert.assertFalse(sut.hasMoreBit());
        sut.close();
    }

    @Test
    public void testReadBits() throws IOException {
        // Prepare
        ByteArrayOutputStream dataOutBuffer = new ByteArrayOutputStream();
        DebugStructDataOutput dataOut = new DebugStructDataOutput(new PrintStream(dataOutBuffer));
        dataOut.writeNBits(4, 0b0101);
        dataOut.writeNBits(1, 0);
        dataOut.writeNBits(1, 1);
        dataOut.flush();
        dataOut.close();
        String txt = dataOutBuffer.toString();

        DebugStructDataInput sut = new DebugStructDataInput(new BufferedReader(new StringReader(txt)));
        // Perform
        Assert.assertEquals(0b0101, sut.readBits(4));
        Assert.assertEquals(0, sut.readBits(1));
        Assert.assertEquals(1, sut.readBits(1));
        // Post-check
        Assert.assertFalse(sut.hasMoreBit());
        sut.close();
    }
    
    @Test
    public void testReadUInt0N() throws IOException {
        // Prepare
        ByteArrayOutputStream dataOutBuffer = new ByteArrayOutputStream();
        DebugStructDataOutput dataOut = new DebugStructDataOutput(new PrintStream(dataOutBuffer));
        dataOut.writeUInt0N(4, 1);
        dataOut.writeUInt0N(4, 3);
        dataOut.writeUInt0N(5, 4);
        dataOut.writeUInt0N(7, 6);
        dataOut.flush();
        dataOut.close();
        String txt = dataOutBuffer.toString();

        DebugStructDataInput sut = new DebugStructDataInput(new BufferedReader(new StringReader(txt)));
        // Perform
        Assert.assertEquals(1, sut.readUInt0N(4));
        Assert.assertEquals(3, sut.readUInt0N(4));
        Assert.assertEquals(4, sut.readUInt0N(5));
        Assert.assertEquals(6, sut.readUInt0N(7));
        // Post-check
        Assert.assertFalse(sut.hasMoreBit());
        sut.close();
    }
    
    @Test
    public void testReadIntMinMax() throws IOException {
        // Prepare
        ByteArrayOutputStream dataOutBuffer = new ByteArrayOutputStream();
        DebugStructDataOutput dataOut = new DebugStructDataOutput(new PrintStream(dataOutBuffer));
        dataOut.writeIntMinMax(10, 200, 190);
        dataOut.writeIntMinMax(10, 300, 290);
        dataOut.flush();
        dataOut.close();
        String txt = dataOutBuffer.toString();

        DebugStructDataInput sut = new DebugStructDataInput(new BufferedReader(new StringReader(txt)));
        // Perform
        Assert.assertEquals(190, sut.readIntMinMax(10, 200));
        Assert.assertEquals(290, sut.readIntMinMax(10, 300));
        // Post-check
        Assert.assertFalse(sut.hasMoreBit());
        sut.close();
    }

    @Test
    public void testReadByte() throws IOException {
        // Prepare
        ByteArrayOutputStream dataOutBuffer = new ByteArrayOutputStream();
        DebugStructDataOutput dataOut = new DebugStructDataOutput(new PrintStream(dataOutBuffer));
        dataOut.writeByte((byte) 2);
        dataOut.writeByte((byte) 127);
        dataOut.writeByte((byte) 128);
        dataOut.writeByte((byte) 255);
        dataOut.flush();
        dataOut.close();
        String txt = dataOutBuffer.toString();

        DebugStructDataInput sut = new DebugStructDataInput(new BufferedReader(new StringReader(txt)));
        // Perform
        Assert.assertEquals(2, sut.readByte());
        Assert.assertEquals(127, sut.readByte());
        Assert.assertEquals((byte)128, sut.readByte());
        Assert.assertEquals((byte)255, sut.readByte());
        // Post-check
        Assert.assertFalse(sut.hasMoreBit());
        sut.close();
    }
    
    @Test
    public void testReadBytes() throws IOException {
        // Prepare
        ByteArrayOutputStream dataOutBuffer = new ByteArrayOutputStream();
        DebugStructDataOutput dataOut = new DebugStructDataOutput(new PrintStream(dataOutBuffer));
        byte[] in = new byte[] { (byte)0, (byte)1, (byte) 254 };
        dataOut.writeBytes(in, 0, 3);
        dataOut.flush();
        dataOut.close();
        String txt = dataOutBuffer.toString();

        DebugStructDataInput sut = new DebugStructDataInput(new BufferedReader(new StringReader(txt)));
        // Perform
        byte[] res = new byte[3];
        sut.readBytes(res, 3);
        Assert.assertEquals(0, res[0]);
        Assert.assertEquals(1, res[1]);
        Assert.assertEquals((byte)254, res[2]);
        // Post-check
        Assert.assertFalse(sut.hasMoreBit());
        sut.close();
    }
    
    @Test
    public void testReadInt() throws IOException {
        // Prepare
        ByteArrayOutputStream dataOutBuffer = new ByteArrayOutputStream();
        DebugStructDataOutput dataOut = new DebugStructDataOutput(new PrintStream(dataOutBuffer));
        int val = 134567;
        dataOut.writeInt(val);
        dataOut.writeInt(-val);
        dataOut.flush();
        dataOut.close();
        String txt = dataOutBuffer.toString();

        DebugStructDataInput sut = new DebugStructDataInput(new BufferedReader(new StringReader(txt)));
        // Perform
        Assert.assertEquals(val, sut.readInt());
        Assert.assertEquals(-val, sut.readInt());
        // Post-check
        Assert.assertFalse(sut.hasMoreBit());
        sut.close();
    }
    
    @Test
    public void testReadInts() throws IOException {
        // Prepare
        ByteArrayOutputStream dataOutBuffer = new ByteArrayOutputStream();
        DebugStructDataOutput dataOut = new DebugStructDataOutput(new PrintStream(dataOutBuffer));
        int[] in1 = new int[] { 5, -12, 12, -27, 35, 56, 56, 79, 99 };
        dataOut.writeInts(in1, 0, in1.length);
        dataOut.writeInts(in1, 3, 5);
        dataOut.flush();
        dataOut.close();
        String txt = dataOutBuffer.toString();

        DebugStructDataInput sut = new DebugStructDataInput(new BufferedReader(new StringReader(txt)));
        // Perform
        int[] res1 = new int[in1.length];
        sut.readInts(res1, 0, in1.length);
        int[] subres1 = new int[5];
        sut.readInts(subres1, 0, 5);
        AssertArrayUtils.assertEquals(in1, res1);
        AssertArrayUtils.assertEquals(in1, 3, subres1, 0, 5);
        // Post-check
        Assert.assertFalse(sut.hasMoreBit());
        sut.close();
    }
    

    
    @Test
    public void testReadFloat() throws IOException {
        // Prepare
        float f1 = 12.34e5f;
        ByteArrayOutputStream dataOutBuffer = new ByteArrayOutputStream();
        DebugStructDataOutput dataOut = new DebugStructDataOutput(new PrintStream(dataOutBuffer));
        dataOut.writeFloat(f1);
        dataOut.writeFloat(-f1);
        dataOut.flush();
        dataOut.close();
        String txt = dataOutBuffer.toString();

        DebugStructDataInput sut = new DebugStructDataInput(new BufferedReader(new StringReader(txt)));
        // Perform
        Assert.assertEquals(f1, sut.readFloat(), 1e-9);
        Assert.assertEquals(-f1, sut.readFloat(), 1e-9);
        // Post-check
        Assert.assertFalse(sut.hasMoreBit());
        sut.close();
    }
    
    @Test
    public void testReadDouble() throws IOException {
        // Prepare
        double d1 = 12.34e5;
        ByteArrayOutputStream dataOutBuffer = new ByteArrayOutputStream();
        DebugStructDataOutput dataOut = new DebugStructDataOutput(new PrintStream(dataOutBuffer));
        dataOut.writeDouble(d1);
        dataOut.writeDouble(-d1);
        dataOut.flush();
        dataOut.close();
        String txt = dataOutBuffer.toString();

        DebugStructDataInput sut = new DebugStructDataInput(new BufferedReader(new StringReader(txt)));
        // Perform
        Assert.assertEquals(d1, sut.readDouble(), 1e-9);
        Assert.assertEquals(-d1, sut.readDouble(), 1e-9);
        // Post-check
        Assert.assertFalse(sut.hasMoreBit());
        sut.close();
    }
    
    @Test
    public void testReadUTF() throws IOException {
        // Prepare
        String val = "abcABC123#àâéè";
        ByteArrayOutputStream dataOutBuffer = new ByteArrayOutputStream();
        DebugStructDataOutput dataOut = new DebugStructDataOutput(new PrintStream(dataOutBuffer));
        dataOut.writeUTF(val);
        dataOut.writeUTF("!" + val);
        dataOut.flush();
        dataOut.close();
        String txt = dataOutBuffer.toString();

        DebugStructDataInput sut = new DebugStructDataInput(new BufferedReader(new StringReader(txt)));
        // Perform
        Assert.assertEquals(val, sut.readUTF());
        Assert.assertEquals("!" + val, sut.readUTF());
        // Post-check
        Assert.assertFalse(sut.hasMoreBit());
        sut.close();
    }
    
    @Test
    public void testReadUIntLtMinElseMax() throws IOException {
        // Prepare
        ByteArrayOutputStream dataOutBuffer = new ByteArrayOutputStream();
        DebugStructDataOutput dataOut = new DebugStructDataOutput(new PrintStream(dataOutBuffer));
        dataOut.writeUIntLtMinElseMax(5, 20, 2);
        dataOut.writeUIntLtMinElseMax(5, 20, 4);
        dataOut.writeUIntLtMinElseMax(5, 20, 5);
        dataOut.writeUIntLtMinElseMax(5, 20, 6);
        dataOut.writeUIntLtMinElseMax(5, 20, 19);
        dataOut.flush();
        dataOut.close();
        String txt = dataOutBuffer.toString();

        DebugStructDataInput sut = new DebugStructDataInput(new BufferedReader(new StringReader(txt)));
        // Perform
        Assert.assertEquals(2, sut.readUIntLtMinElseMax(5, 20));
        Assert.assertEquals(4, sut.readUIntLtMinElseMax(5, 20));
        Assert.assertEquals(5, sut.readUIntLtMinElseMax(5, 20));
        Assert.assertEquals(6, sut.readUIntLtMinElseMax(5, 20));
        Assert.assertEquals(19, sut.readUIntLtMinElseMax(5, 20));
        // Post-check
        Assert.assertFalse(sut.hasMoreBit());
        sut.close();
    }

    @Test
    public void testReadUInt0ElseMax() throws IOException {
        // Prepare
        ByteArrayOutputStream dataOutBuffer = new ByteArrayOutputStream();
        DebugStructDataOutput dataOut = new DebugStructDataOutput(new PrintStream(dataOutBuffer));
        dataOut.writeUInt0ElseMax(8, 0);
        dataOut.writeUInt0ElseMax(8, 1);
        dataOut.flush();
        dataOut.close();
        String txt = dataOutBuffer.toString();

        DebugStructDataInput sut = new DebugStructDataInput(new BufferedReader(new StringReader(txt)));
        // Perform
        Assert.assertEquals(0, sut.readUInt0ElseMax(8));
        Assert.assertEquals(1, sut.readUInt0ElseMax(8));
        // Post-check
        Assert.assertFalse(sut.hasMoreBit());
        sut.close();
    }

    @Test
    public void testReadIntsSorted() throws IOException {
        // Prepare
        ByteArrayOutputStream dataOutBuffer = new ByteArrayOutputStream();
        DebugStructDataOutput dataOut = new DebugStructDataOutput(new PrintStream(dataOutBuffer));
        int[] in1 = new int[] { 5, 12, 12, 27, 35, 56, 56, 79, 99 };
        int[] in2 = new int[] { 5, 12, 13, 14, 27, 35, 56, 79, 99 };
        dataOut.writeIntsSorted(5, 100, false, in1, 0, in1.length);
        dataOut.writeIntsSorted(5, 100, true, in2, 0, in2.length);
        dataOut.flush();
        dataOut.close();
        String txt = dataOutBuffer.toString();

        DebugStructDataInput sut = new DebugStructDataInput(new BufferedReader(new StringReader(txt)));
        // Perform
        int[] res1 = new int[in1.length];
        sut.readIntsSorted(5, 100, false, res1, 0, in1.length);
        int[] res2 = new int[in2.length];
        sut.readIntsSorted(5, 100, true, res2, 0, in2.length);
        AssertArrayUtils.assertEquals(in1, res1);
        AssertArrayUtils.assertEquals(in2, res2);
        // Post-check
        Assert.assertFalse(sut.hasMoreBit());
        sut.close();
    }
    

}
