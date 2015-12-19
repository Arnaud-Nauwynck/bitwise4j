package fr.an.util.encode.huffman;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import fr.an.util.bits.BitInputStream;
import fr.an.util.bits.BitOutputStream;
import fr.an.util.bits.BooleanArray;
import fr.an.util.bits.BooleanArrayQueue;
import fr.an.util.encoder.huffman.HuffmanBitsCode;
import fr.an.util.encoder.huffman.HuffmanTable;
import fr.an.util.encoder.huffman.util.HuffmanTreePrettyPrinter;

/**
 * 
 */
public class HuffmanTableTest {

    private static final boolean DEBUG = false;
    
    
	@Test
	public void testComputeTable() {
	    // Prepare

	    // see https://www.siggraph.org/education/materials/HyperGraph/video/mpeg/mpegfaq/huffman_tutorial.html
	    // (but example was wrong!! inverted 45,57!! so corrected...)
	    
        //        A quick tutorial on generating a huffman tree
        //        Lets say you have a set of numbers and their frequency of use and want to create a huffman encoding for them:
        //
        //            FREQUENCY   VALUE
        //                ---------       -----
        //                     5            1
        //                     7            2
        //                    10            3
        //                    15            4
        //                    20            5
        //                    45            6
        //
        //        Creating a huffman tree is simple. Sort this list by frequency and make the two-lowest elements into leaves, creating a parent node with a frequency that is the sum of the two lower element's frequencies:
        //
        //                12:*
        //                /  \
        //              5:1   7:2
        //
        //        The two elements are removed from the list and the new parent node, with frequency 12, is inserted into the list by frequency. So now the list, sorted by frequency, is:
        //
        //                10:3
        //                12:*
        //                15:4
        //                20:5
        //                45:6
        //
        //        You then repeat the loop, combining the two lowest elements. This results in:
        //
        //                22:*
        //                /   \
        //             10:3   12:*
        //                    /   \
        //              5:1   7:2
        //
        //        and the list is now:
        //
        //            15:4
        //            20:5
        //            22:*
        //            45:6
        //
        //        You repeat until there is only one element left in the list.
        //
        //                35:*
        //                /   \
        //              15:4  20:5
        //
        //                22:*
        //                35:*
        //                45:6
        //
        //                    57:*
        //                ___/    \___
        //               /            \
        //             22:*          35:*
        //            /   \          /   \
        //         10:3   12:*     15:4   20:5
        //                /   \
        //              5:1   7:2
        //
        //            45:6
        //            57:*
        //
        //                   102:*
        //            ______/    \____
	    //          /                 \
        //         45:6               57:*                         
        //                        ___/    \___
        //                       /            \
        //                      22:*          35:*
        //                     /   \          /   \
        //                   10:3   12:*     15:4   20:5
        //                         /   \
        //                        5:1   7:2
        //
	    HuffmanTable<Integer> sut = new HuffmanTable<Integer>();
	    doAddSymbolsCountsTest0(sut);
        // Perform
	    sut.compute();
        // Post-check
	    if (DEBUG) {
    	    sut.getRootNode().accept(new HuffmanTreePrettyPrinter<Integer>(System.out));
	    }
	    Assert.assertEquals("0", sut.getSymbolCode(6).codeToString());
	    Assert.assertEquals("100", sut.getSymbolCode(3).codeToString());
	    Assert.assertEquals("1011", sut.getSymbolCode(2).codeToString());
	    Assert.assertEquals("110", sut.getSymbolCode(4).codeToString());
	    Assert.assertEquals("1010", sut.getSymbolCode(1).codeToString());
	    Assert.assertEquals("111", sut.getSymbolCode(5).codeToString());
	}

    private void doAddSymbolsCountsTest0(HuffmanTable<Integer> sut) {
        Map<Integer,Integer> counts = new HashMap<Integer,Integer>();
	    counts.put(1, 5);
	    counts.put(2, 7);
	    counts.put(3, 10);
	    counts.put(4, 15);
	    counts.put(5, 20);
	    counts.put(6, 45);
	    
	    sut.addSymbols(counts);
    }
	
	@Test
	public void testWriteEncodeSymbol() {
	    // Prepare
	    HuffmanTable<Integer> sut = new HuffmanTable<Integer>();
	    doAddSymbolsCountsTest0(sut);
	    sut.compute();
	    BooleanArrayQueue bitArrayQueue = new BooleanArrayQueue();
	    BitOutputStream bOut = bitArrayQueue.getOutputEndPoint();
	    int[] msg = new int[] { 1, 
	            2, 3, 4, 5, 6 
	    };
        // Perform
	    for(int e : msg) {
	        sut.writeEncodeSymbol(bOut, e);
	    }
        // Post-check
	    String res = bitArrayQueue.getBuffer().toString();
	    Assert.assertEquals("1010" 
	            + "1011" + "100" + "110" + "111" + "0"
	            , res);
	}
	
	@Test
    public void testReadDecodeSymbol() {
        // Prepare
	    HuffmanTable<Integer> sut = new HuffmanTable<Integer>();
        doAddSymbolsCountsTest0(sut);
        sut.compute();
        BooleanArray bitArray = BooleanArray.parse("1010" + "1011" + "100" + "110" + "111" + "0");
        BooleanArrayQueue bitArrayQueue = new BooleanArrayQueue(bitArray);
        BitInputStream bIn = bitArrayQueue.getInputEndPoint();
        List<Integer> res = new ArrayList<Integer>();
        // Perform
        while(bIn.hasMoreBit()) {
            Integer symbol = sut.readDecodeSymbol(bIn);
            res.add(symbol);
        }
        // Post-check
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6), res);
	}
	
	@Test
	public void testHuffmanBitsCode() {
		HuffmanBitsCode b = HuffmanBitsCode.getRootEmptyCode();
		Assert.assertEquals(0, b.getBitsCount());
		Assert.assertEquals("", b.codeToString());

		HuffmanBitsCode b0 = HuffmanBitsCode.childLeftCode(b);
		Assert.assertEquals(1, b0.length());
		Assert.assertEquals("0", b0.codeToString());
		
		HuffmanBitsCode b1 = HuffmanBitsCode.childRightCode(b);
		Assert.assertEquals(1, b1.length());
		Assert.assertEquals("1", b1.codeToString());

		HuffmanBitsCode b00 = HuffmanBitsCode.childLeftCode(b0);
		Assert.assertEquals(2, b00.length());
		Assert.assertEquals("00", b00.codeToString());

		HuffmanBitsCode b01 = HuffmanBitsCode.childRightCode(b0);
		Assert.assertEquals(2, b01.length());
		Assert.assertEquals("01", b01.codeToString());

		HuffmanBitsCode b10 = HuffmanBitsCode.childLeftCode(b1);
		Assert.assertEquals(2, b10.length());
		Assert.assertEquals("10", b10.codeToString());

		HuffmanBitsCode b11 = HuffmanBitsCode.childRightCode(b1);
		Assert.assertEquals(2, b11.length());
		Assert.assertEquals("11", b11.codeToString());
	}
	
}
