package fr.an.util.bits;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;

public class InputStreamToBitInputStreamTest {

    @Test
    public void testReadBit_read() {
        // encode bits->bytes ... 
        byte[] bytes;
        {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            BitOutputStream bitOut = new OutputStreamToBitOutputStream(byteOut);
            
            bitOut.writeBit(true);
            bitOut.writeBit(false);
            bitOut.writeBit(true);
            for(int i = 0; i < 256; i++) {
                byte bi = (byte) i;
                bitOut.write(bi);
            }
            bitOut.writeBit(true);
            bitOut.writeBit(false);
            
            bitOut.close(); // padding: 8-3-2=3 bits
            bytes = byteOut.toByteArray(); 
        }
        
        // then re-decode  bytes->bits
        {
            InputStream byteIn = new ByteArrayInputStream(bytes);
            BitInputStream bitIn = new InputStreamToBitInputStream(byteIn);
            
            Assert.assertEquals(true, bitIn.readBit());
            Assert.assertEquals(false, bitIn.readBit());
            Assert.assertEquals(true, bitIn.readBit());
            for(int i = 0; i < 256; i++) {
                byte bi = (byte) bitIn.read();
                Assert.assertEquals((byte)i, bi);
            }
            Assert.assertEquals(true, bitIn.readBit());
            Assert.assertEquals(false, bitIn.readBit());
    
//            Assert.assertFalse(bitIn.hasMoreBit()); // TODO ... fails!!!
            
            // read padding!!
            Assert.assertEquals(false, bitIn.readBit());
            Assert.assertEquals(false, bitIn.readBit());
            Assert.assertEquals(false, bitIn.readBit());
            
            Assert.assertFalse(bitIn.hasMoreBit());
            Assert.assertFalse(bitIn.hasMoreBit()); //can repeat
            
            bitIn.close();
        }
    }
    
}
