package fr.an.util.encoder.structio;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import fr.an.util.bits.BitsUtil;
import fr.an.util.bits.CounterOuputStream;
import fr.an.util.bits.RuntimeIOException;
import fr.an.util.encoder.huffman.HuffmanBitsCode;

/**
 * implements StructDataOutput using underlying Debug text OutputStream, with 1 instruction per line
 * 
 */
public class DebugStructDataOutput extends StructDataOutput {

    private PrintStream out;
    
    private int count = 0;
    
    private int countInstrLine;
    
    // ------------------------------------------------------------------------

    public DebugStructDataOutput(PrintStream out) {
        this.out = out;
    }

    // ------------------------------------------------------------------------

    protected void print(String text) {
        out.print(text);
    }
    
    protected void println(String line) {
        out.println(line);
    }
    
    protected void println() {
        out.println();
    }

    protected void printIncr(int incr, String text) {
        count += incr;
        countInstrLine++;
        print("[" + incr + " : " + count + "] " + text);
    }

    protected void printlnIncr(int incr, String text) {
        printIncr(incr, text);
        println();
    }

    @Override
    public void close() {
        out.close();
    }

    @Override
    public void writeBit(boolean value) {
        printlnIncr(1, "bit: " + ((value)? "1" : "0"));
    }

    @Override
    public void writeNBits(int count, int bitsValue) {
        printlnIncr(count, "NBits: " + BitsUtil.bitsToString(count, bitsValue));
    }
    
    @Override
    public void writeUInt0N(int maxNExclusive, int value) {
        int nBits = Pow2Utils.valueToUpperLog2(maxNExclusive);
        printlnIncr(nBits, "uint0N(" + maxNExclusive + "): " + value);
    }
    
    @Override
    public void writeIntMinMax(int fromMin, int toMax, int value) {
        int nBits = countBitsIntMinMax(fromMin, toMax);
        printlnIncr(nBits, "intMinMax(" + fromMin + ", " + toMax + "): " + value);
    }

    private int countBitsIntMinMax(int fromMin, int toMax) {
        int maxAmplitude = toMax - fromMin;
        int nBits = Pow2Utils.valueToUpperLog2(maxAmplitude);
        return nBits;
    }

    @Override
    public void writeByte(byte value) {
        printlnIncr(8, "byte: " + (int) value);
    }
    
    @Override
    public void writeBytes(byte[] dest, int len) {
        writeBytes(dest, 0, len);
    }
    
    @Override
    public void writeBytes(byte[] dest, int offset, int len) {
        printIncr(8*len, "bytes: ");
        final int maxI = offset+len;
        for(int i = offset; i < maxI; i++) {
            out.print((int) dest[i]);
            if ((i + 1) < maxI) {
                print(" ");
            }
        }
        println();
    }
    
    @Override
    public void writeInt(int value) {
        printlnIncr(32, "int: " + value);
    }

    @Override
    public void writeFloat(float value) {
        printlnIncr(32, "float: " + value);
    }
    
    @Override
    public void writeDouble(double value) {
        printlnIncr(64, "double: " + value);
    }

    // cf java.io.DataOutputStream
    @Override
    public void writeUTF(String value) {
        CounterOuputStream tmpCount = new CounterOuputStream();
        DataOutputStream din = new DataOutputStream(tmpCount);
        try {
            din.writeUTF(value);
        } catch (IOException e) {
            throw new RuntimeIOException("writeUTF", e);
        } finally {
            try { din.close(); } catch(IOException ex) {}
        }
        int nBits = 8 * tmpCount.getCount();
        printlnIncr(nBits, "UTF: " + value.replace("\n", "\\n"));
    }
    
    @Override
    public void writeHuffmanCode(HuffmanBitsCode code) {
        int nBits = code.getBitsCount();
        printlnIncr(nBits, "huffmanCode: " + code.codeToString()); 
    }

    @Override
    public void writeUIntLtMinElseMax(int min, int max, int value) {
        int nBits = 1;
        if (value < min) {
            nBits += countBitsIntMinMax(0, min);
        } else {
            nBits += countBitsIntMinMax(min, max);
        }
        printlnIncr(nBits, "uintLtMinElseMax(" + min + ", " + max + "): " + value);
    }

    @Override
    public void writeUIntLt16ElseMax(int max, int value) {
        writeUIntLtMinElseMax(16, max, value);
    }

    @Override
    public void writeUIntLt2048ElseMax(int max, int value) {
        writeUIntLtMinElseMax(2048, max, value);
    }

    @Override
    public void writeUInt0ElseMax(int max, int value) {
        int nBits = 1;
        if (value == 0) {
            // nothing
        } else {
            nBits += countBitsIntMinMax(1, max);
        }
        printlnIncr(nBits, "uint0ElseMax(" + max + "): " + value);
    }

}
