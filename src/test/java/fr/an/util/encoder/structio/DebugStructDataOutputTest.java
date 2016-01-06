package fr.an.util.encoder.structio;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Assert;
import org.junit.Test;

public class DebugStructDataOutputTest {

    @Test
    public void testWriteUInt0ElseMax() {
        // Prepare
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DebugStructDataOutput sut = new DebugStructDataOutput(new PrintStream(buffer));
        StringBuilder checkRes = new StringBuilder();
        // Perform
        sut.writeUInt0ElseMax(8, 0); // => test bit: "0"
        checkRes.append("[1: 1] uint0ElseMax(8): 0\n");
        Assert.assertEquals(checkRes.toString(), buffer.toString());
        sut.writeUInt0ElseMax(8, 1); // => test bit: "1" + value "111"
        checkRes.append("[4: 5] uint0ElseMax(8): 1\n");
        Assert.assertEquals(checkRes.toString(), buffer.toString());
        // Post-check
        sut.close();
    }

    
}