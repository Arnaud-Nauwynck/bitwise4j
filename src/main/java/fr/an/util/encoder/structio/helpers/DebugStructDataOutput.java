package fr.an.util.encoder.structio.helpers;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import fr.an.util.bits.BitsUtil;
import fr.an.util.bits.CounterOuputStream;
import fr.an.util.bits.RuntimeIOException;
import fr.an.util.encoder.huffman.HuffmanBitsCode;
import fr.an.util.encoder.structio.Pow2Utils;
import fr.an.util.encoder.structio.StructDataOutput;

/**
 * implements StructDataOutput using underlying Debug text OutputStream, with 1 instruction per line
 * 
 */
public class DebugStructDataOutput extends StructDataOutput {

    private PrintStream out;
    
    private String currStream = "";
    
    private CounterPerStream counters = new CounterPerStream();
    
    // ------------------------------------------------------------------------

    public DebugStructDataOutput(PrintStream out) {
        this.out = out;
    }

    @Override
    public void close() {
        out.close();
    }

    // ------------------------------------------------------------------------
    
    public CounterPerStream getCounters() {
        return counters;
    }
        
    protected void printlnInstr(int incrCountBits, String instr, int value) {
        printlnInstr(incrCountBits, instr, Integer.toString(value));
    }

    protected void printlnInstr(int incrCountBits, String instr, String value) {
        int incrValueLen = (value != null && !instr.equals("comment"))? value.length() : 0;
        counters.incr(incrCountBits, incrValueLen);
        out.print(currStream + ":" + incrCountBits
            + ":" + counters.toColumnsString()
            + ":" + instr + ": " + value + "\n");
    }

    // ------------------------------------------------------------------------
    
    @Override
    public void debugComment(String msg) {
        // printlnInstr(0, "comment", msg);
        out.print("# " + msg + "\n");
    }
    
    @Override
    public String getCurrStream() {
        return currStream;
    }
    
    @Override
    public String setCurrStream(String name) {
        String prev = currStream;
        counters.setCurrStream(name);
        this.currStream = name;
        return prev;
    }
    
    @Override
    public void writeBit(boolean value) {
        printlnInstr(1, "bit", ((value)? "1" : "0"));
    }

    @Override
    public void writeNBits(int count, int bitsValue) {
        printlnInstr(count, "NBits", BitsUtil.bitsToString(count, bitsValue));
    }
    
    @Override
    public void writeUInt0N(int maxNExclusive, int value) {
        int nBits = Pow2Utils.valueToUpperLog2(maxNExclusive);
        printlnInstr(nBits, "uint0N(" + maxNExclusive + ")", value);
    }
    
    @Override
    public void writeIntMinMax(int fromMin, int toMax, int value) {
        if (fromMin > toMax || value < fromMin || value >= toMax) {
            throw new IllegalArgumentException("writeIntMinMax(" + fromMin + ", " + toMax + ", " + value + ") expecting constraint: min <= value < max");
        }
        int nBits = countBitsIntMinMax(fromMin, toMax);
        printlnInstr(nBits, "intMinMax(" + fromMin + ", " + toMax + ")", value);
    }

    private int countBitsIntMinMax(int fromMin, int toMax) {
        int maxAmplitude = toMax - fromMin;
        int nBits = Pow2Utils.valueToUpperLog2(maxAmplitude);
        return nBits;
    }

    @Override
    public void writeByte(byte value) {
        printlnInstr(8, "byte", (int) value);
    }
    
    @Override
    public void writeBytes(byte[] dest, int len) {
        writeBytes(dest, 0, len);
    }
    
    @Override
    public void writeBytes(byte[] dest, int offset, int len) {
        final int maxI = offset+len;
        StringBuilder sb = new StringBuilder();
        for(int i = offset; i < maxI; i++) {
            sb.append((int) dest[i]);
            if ((i + 1) < maxI) {
                sb.append(' ');
            }
        }
        printlnInstr(8*len, "bytes", sb.toString());
    }
    
    @Override
    public void writeInt(int value) {
        printlnInstr(32, "int", value);
    }

    @Override
    public void writeInts(int[] values, int offset, int len) {
        final int maxI = offset + len;
        StringBuilder sb = new StringBuilder();
        for(int i = offset; i < maxI; i++) {
            sb.append(values[i]);
            if ((i + 1) < maxI) {
                sb.append(" ");
            }
        }
        printlnInstr(32*len, "ints", sb.toString());
    }

    @Override
    public void writeFloat(float value) {
        printlnInstr(32, "float", Float.toString(value));
    }
    
    @Override
    public void writeDouble(double value) {
        printlnInstr(64, "double", Double.toString(value));
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
        printlnInstr(nBits, "UTF", value.replace("\n", "\\n"));
    }
    
    @Override
    public void writeHuffmanCode(HuffmanBitsCode code) {
        int nBits = code.getBitsCount();
        printlnInstr(nBits, "huffmanCode", code.codeToString()); 
    }

    @Override
    public void writeUIntLtMinElseMax(int min, int max, int value) {
        int nBits = 1;
        if (value < min) {
            nBits += countBitsIntMinMax(0, min);
        } else {
            nBits += countBitsIntMinMax(min, max);
        }
        printlnInstr(nBits, "uintLtMinElseMax(" + min + ", " + max + ")", value);
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
        printlnInstr(nBits, "uint0ElseMax(" + max + ")", value);
    }

}
