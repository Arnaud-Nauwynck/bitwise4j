package fr.an.bitwise4j.bits;

import java.io.ByteArrayOutputStream;

import org.junit.Assert;
import org.junit.Test;

import fr.an.bitwise4j.bits.OutputStreamToBitOutputStream;

public class OutputStreamToBitOutputStreamTest {

    @Test
    public void testWriteBit() {
        // Prepare
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        OutputStreamToBitOutputStream sut = new OutputStreamToBitOutputStream(bout);
        // Perform
        sut.writeBit(true);
        Assert.assertEquals(0, bout.size());
        for(int i = 0; i < 6; i++) {
            sut.writeBit(false);
        }
        Assert.assertEquals(0, bout.size());
        sut.writeBit(false);
        Assert.assertEquals(1, bout.size());
        // Post-check
        byte[] res = bout.toByteArray();
        Assert.assertEquals(1, res.length);
        Assert.assertEquals((byte) (1 << 7), res[0]);
        bout.reset();
        
        // Perform
        for(int i = 0; i < 15; i++) {
            sut.writeBit(true);
        }
        sut.writeBit(false);
        // 4 more..
        for(int i = 0; i < 4; i++) {
            sut.writeBit(true);
        }
        // Post-check
        res = bout.toByteArray();
        Assert.assertEquals(2, res.length);
        Assert.assertEquals((byte) 0b11111111, res[0]);
        Assert.assertEquals((byte) 0b11111110, res[1]);
        bout.reset();
        
        // 4 more..
        for(int i = 0; i < 4; i++) {
            sut.writeBit(false);
        }
        res = bout.toByteArray();
        Assert.assertEquals(1, res.length);
        Assert.assertEquals((byte) 0b11110000, res[0]);
        
        sut.close();
    }
}
