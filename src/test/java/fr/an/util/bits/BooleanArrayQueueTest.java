package fr.an.util.bits;

import org.junit.Assert;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * JUnit test for BooleanArrayQueue
 */
public class BooleanArrayQueueTest extends TestCase {

	public BooleanArrayQueueTest(String name) {
		super(name);
	}

	public void test1() throws Exception {
		BooleanArrayQueue q = new BooleanArrayQueue();
		BooleanArray qbuffer = q.getBuffer();
		BitInputStream qin = q.getInputEndPoint();
		BitOutputStream qout = q.getOutputEndPoint();

		boolean writtenB;
		boolean readB;

		for (int i = 0; i < 10; i++) {
			writtenB = (i % 2) == 0; 
			qout.writeBit(writtenB);
			BitTestHelper.assertEquals(new boolean[] { writtenB }, qbuffer);
			readB = qin.readBit();
			assertEquals(writtenB, readB);
	
			writtenB = (i % 3) == 0; 
			qout.writeBit(writtenB);
			BitTestHelper.assertEquals(new boolean[] { writtenB }, qbuffer);
			readB = qin.readBit();
			assertEquals(writtenB, readB);
		}
		
		boolean writtenB2;
		boolean readB2;
		for (int i = 0; i < 10; i++) {
			writtenB = (i % 2) == 0;
			writtenB2 = (i % 3) == 0;
			
			qout.writeBit(writtenB);
			BitTestHelper.assertEquals(new boolean[] { writtenB }, qbuffer);
			qout.writeBit(writtenB2);
			BitTestHelper.assertEquals(new boolean[] { writtenB, writtenB2}, qbuffer);
						
			readB = qin.readBit();
			assertEquals(writtenB, readB);
			BitTestHelper.assertEquals(new boolean[] { writtenB2 }, qbuffer);
			readB2 = qin.readBit();
			assertEquals(writtenB2, readB2);
			BitTestHelper.assertEquals(new boolean[] { }, qbuffer);
		}

	}

	@Test
	public void testGetOutputEndPoint() {
	    // Prepare
        BooleanArrayQueue bitArrayQueue = new BooleanArrayQueue();
        // Perform
        BitOutputStream bOut = bitArrayQueue.getOutputEndPoint();
        bOut.writeBit(false);
        bOut.writeBit(true);
        
        
        Assert.assertEquals("01", bitArrayQueue.getBuffer().toString());
        BitInputStream bIn = bitArrayQueue.getInputEndPoint();
        Assert.assertFalse(bIn.readBit());
        Assert.assertTrue(bIn.readBit());

        Assert.assertFalse(bIn.hasMoreBit());
        // Post-check
	}
	
    
    
}
