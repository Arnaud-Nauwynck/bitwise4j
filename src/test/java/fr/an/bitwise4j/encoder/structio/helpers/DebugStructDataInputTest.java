package fr.an.bitwise4j.encoder.structio.helpers;

import java.io.BufferedReader;
import java.io.StringReader;

import org.junit.Assert;
import org.junit.Test;

import fr.an.bitwise4j.encoder.structio.helpers.DebugStructDataInput;

public class DebugStructDataInputTest {

    @Test
    public void testReadBit() {
        // Prepare
        StringReader buffer = new StringReader(
            "[1 : 1] bit: 1\n" //
            + "[1 : 2] bit: 0\n" //
            + "[4 : 6] bits: 0101\n" //
            + "");
        DebugStructDataInput sut = new DebugStructDataInput(new BufferedReader(buffer));
        // Perform & Post-check
        Assert.assertTrue(sut.readBit());
        Assert.assertFalse(sut.readBit());
        Assert.assertEquals(0b0101, sut.readBits(4));
        // Post-check
        Assert.assertFalse(sut.hasMoreBit());
        sut.close();
    }

    @Test
    public void testReadBits() {
        // Prepare
        StringReader buffer = new StringReader(
            "[4 : 4] bits: 0101\n" //
            + "[1 : 5] bits: 0\n" //
            + "[1 : 6] bits: 1\n" //
            );
        DebugStructDataInput sut = new DebugStructDataInput(new BufferedReader(buffer));
        // Perform
        Assert.assertEquals(0b0101, sut.readBits(4));
        Assert.assertEquals(0, sut.readBits(1));
        Assert.assertEquals(1, sut.readBits(1));
        // Post-check
        Assert.assertFalse(sut.hasMoreBit());
        sut.close();
    }
    
    @Test
    public void testReadUInt0N() {
        // Prepare
        StringReader buffer = new StringReader(
            "[2 : 2] uint0N(4): 1\n" //
            + "[2 : 4] uint0N(4): 3\n" //
            + "[3 : 7] uint0N(5): 4\n" //
            + "[3 : 10] uint0N(7): 6\n" //
            );
        DebugStructDataInput sut = new DebugStructDataInput(new BufferedReader(buffer));
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
    public void testReadIntMinMax() {
        // Prepare
        StringReader buffer = new StringReader(
            "[8 : 8] intMinMax(10, 200): 190\n"
            + "[9 : 17] intMinMax(10, 300): 290\n"
            );
        DebugStructDataInput sut = new DebugStructDataInput(new BufferedReader(buffer));
        // Perform
        Assert.assertEquals(190, sut.readIntMinMax(10, 200));
        Assert.assertEquals(290, sut.readIntMinMax(10, 300));
        // Post-check
        Assert.assertFalse(sut.hasMoreBit());
        sut.close();
    }

    @Test
    public void testReadByte() {
        // Prepare
        StringReader buffer = new StringReader(
            "[8 : 8] byte: 2\n" 
            + "[8 : 16] byte: 127\n" 
            + "[8 : 24] byte: 128\n" 
            + "[8 : 32] byte: 255\n" 
            );
        DebugStructDataInput sut = new DebugStructDataInput(new BufferedReader(buffer));
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
    public void testReadBytes() {
        // Prepare
        StringReader buffer = new StringReader(
            "[24 : 24] bytes: 0 1 254\n"
            );
        DebugStructDataInput sut = new DebugStructDataInput(new BufferedReader(buffer));
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
    public void testReadInt() {
        // Prepare
        StringReader buffer = new StringReader(
            "[32 : 32] int: -134567\n"
            + "[32 : 64] int: 134567\n"
            );
        DebugStructDataInput sut = new DebugStructDataInput(new BufferedReader(buffer));
        // Perform
        Assert.assertEquals(-134567, sut.readInt());
        Assert.assertEquals(134567, sut.readInt());
        // Post-check
        Assert.assertFalse(sut.hasMoreBit());
        sut.close();
    }
    
    @Test
    public void testReadFloat() {
        // Prepare
        float f1 = 12.34e5f;
        StringReader buffer = new StringReader(
            "[32 : 32] float: " + f1 + "\n"
            );
        DebugStructDataInput sut = new DebugStructDataInput(new BufferedReader(buffer));
        // Perform
        Assert.assertEquals(f1, sut.readFloat(), 1e-9);
        // Post-check
        Assert.assertFalse(sut.hasMoreBit());
        sut.close();
    }
    
    @Test
    public void testReadDouble() {
        // Prepare
        double d1 = 12.34e5;
        StringReader buffer = new StringReader(
            "[64 : 64] double: " + d1 + "\n"
            );
        DebugStructDataInput sut = new DebugStructDataInput(new BufferedReader(buffer));
        // Perform
        Assert.assertEquals(d1, sut.readDouble(), 1e-9);
        // Post-check
        Assert.assertFalse(sut.hasMoreBit());
        sut.close();
    }
    
//    public String readUTF() {
//        // int prevCount = count;
//        String tmpValue = readlnIncrInstructionValue(-1, "UTF", null); // -1: size unknown while reading
//        String res = tmpValue.replace("\\n", "\n");
//        // may recheck?..
//        return res;
//    }
//
//    public int readUIntLtMinElseMax(int min, int max) {
//        int prevCount = count;
//        String tmpValue = readlnIncrInstructionValue(-1, "uintLtMinElseMax", "" + min + ", " + max); // -1: size unknown while reading
//        int res = Integer.parseInt(tmpValue);
//        int nBits = 1;
//        if (res < min) {
//            nBits += countBitsIntMinMax(0, min);
//        } else {
//            nBits += countBitsIntMinMax(min, max);
//        }
//        checkIncr(prevCount, nBits);
//        return res;
//    }
//
//    public int readUIntLt16ElseMax(int max) {
//        return readUIntLtMinElseMax(16, max);
//    }
//
//    public int readUIntLt2048ElseMax(int max) {
//        return readUIntLtMinElseMax(2048, max);
//    }

    @Test
    public void testReadUInt0ElseMax() {
        // Prepare
        StringReader buffer = new StringReader(
            "[1 : 1] uint0ElseMax(8): 0\n"
            + "[4 : 5] uint0ElseMax(8): 1\n"
            );
        DebugStructDataInput sut = new DebugStructDataInput(new BufferedReader(buffer));
        // Perform
        Assert.assertEquals(0, sut.readUInt0ElseMax(8));
        Assert.assertEquals(1, sut.readUInt0ElseMax(8));
        // Post-check
        Assert.assertFalse(sut.hasMoreBit());
        sut.close();
    }

    
}
