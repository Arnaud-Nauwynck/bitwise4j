package fr.an.bitwise4j.encoder.structio.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.an.bitwise4j.bits.BitsUtil;
import fr.an.bitwise4j.encoder.huffman.HuffmanTable;
import fr.an.bitwise4j.encoder.structio.Pow2Utils;
import fr.an.bitwise4j.encoder.structio.StructDataInput;
import fr.an.bitwise4j.util.RuntimeIOException;

/**
 * implements StructDataInput using underlying Debug text InputStream, with 1 instruction per line
 * 
 */
public class DebugStructDataInput extends StructDataInput {

    private static final Pattern INSTRUCTION_LINE_PATTERN = 
            Pattern.compile("[^:]*:(\\d+):(\\d+)/\\d+:\\d+/\\d+:(\\w+)(\\(([^:]*)\\))?: (.*)"); 

    private BufferedReader in;
    private String readAheadLine;
    
    private String currStream = "";

    private int lineCount;
    private CounterPerStream counters = new CounterPerStream();
    
    // ------------------------------------------------------------------------

    public DebugStructDataInput(BufferedReader target) {
        this.in = target;
    }

    @Override
    public void close() {
        if (in != null) {
            try {
                in.close();
            } catch(IOException ex) {
            }
            this.in = null;
        }
    }
    
    // ------------------------------------------------------------------------

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
    public boolean hasMoreBit() {
        if (readAheadLine != null) {
            return true;
        }
        try {
            this.readAheadLine = in.readLine();
            lineCount++;
        } catch (IOException e) {
            throw new RuntimeIOException("hasMoreBit", e);
        }
        return readAheadLine != null;
    }

    private String doReadLine() {
        String line;
        if (readAheadLine != null) {
            line = readAheadLine;
            this.readAheadLine = null;
        } else {
            try {
                line = in.readLine();
                lineCount++;
            } catch (IOException e) {
                throw new RuntimeIOException("Failed", e);
            }
            while(line.startsWith("#")) {
                try {
                    line = in.readLine();
                    lineCount++;
                } catch (IOException e) {
                    throw new RuntimeIOException("Failed", e);
                }
            }
        }
        return line;
    }
    
    protected RuntimeException failEx(String line, String text) {
        throw new IllegalStateException("Failed to read at line " + lineCount + " '" + line + "', " + text);
    }
    
    protected String readlnIncrInstructionValue(int expectedNBits, String expectedInstructionName, String expectedParams) {
        String line = doReadLine();
        Matcher matcher = INSTRUCTION_LINE_PATTERN.matcher(line);
        if (!matcher.matches()) {
            throw failEx(line, "expected line pattern " + INSTRUCTION_LINE_PATTERN + ", got '" + line + "'");
        }
        int nBits = Integer.parseInt(matcher.group(1));
        int checkCount = Integer.parseInt(matcher.group(2));
        String instr = matcher.group(3);
        String params = matcher.group(5);
        String value = matcher.group(6);

        if (expectedNBits > 0) {
            if (expectedNBits != nBits) {
                throw failEx(line, "expected bits count " + expectedNBits + ", got '" + nBits + "'");
            }
        }
        int incrValueLen = (value != null && !instr.equals("comment"))? value.length() : 0; 
        counters.incr(nBits, incrValueLen);

        if (checkCount != counters.getCurrBitsCount()) {
            throw failEx(line, "expecting count: "+ checkCount + ", got: " + counters.getCurrBitsCount());
        }
        if (!expectedInstructionName.equals(instr)) {
            throw failEx(line, "expecting instr '" + expectedInstructionName + ", got: " + instr);
        }
        
        if (expectedParams != null && !expectedParams.equals(params)) {
            throw failEx(line, "expecting instr " + instr + " params ("+ expectedParams + "), got: (" + params + ")");
        }
        return value;
    }

    
    private void checkIncr(int prevCount, int nBits) {
        int checkCount = prevCount + nBits;
        if (checkCount != counters.getCurrBitsCount()) {
            throw failEx(null, "expecting bits count: "+ counters.getCurrBitsCount() + ", got " + checkCount + "=" + prevCount + "+" + nBits);
        }
    }

    private int countBitsIntMinMax(int fromMin, int toMax) {
        int maxAmplitude = toMax - fromMin;
        int nBits = Pow2Utils.valueToUpperLog2(maxAmplitude);
        return nBits;
    }

    
    @Override
    public int readBits(int readBitsCounts) {
        String tmpValue = readlnIncrInstructionValue(readBitsCounts, "NBits", null);
        return BitsUtil.stringToBits(tmpValue);
    }
    
    @Override
    public boolean readBit() {
        String tmpValue = readlnIncrInstructionValue(1, "bit", null);
        return tmpValue.equals("1");
    }
    
    @Override
    public void readSkipPaddingTo8() {
        int skipModulo = 8 - counters.getCurrBitsCount() % 8;
        readlnIncrInstructionValue(skipModulo, "skipPaddingTo8", null);
    }
    
    @Override
    public int readUInt0N(int maxNExclusive) {
        int nBits = Pow2Utils.valueToUpperLog2(maxNExclusive);
        String tmpValue = readlnIncrInstructionValue(nBits, "uint0N", "" + maxNExclusive);
        return Integer.parseInt(tmpValue);
    }
    
    @Override
    public int readIntMinMax(int fromMin, int toMax) {
        int nBits = Pow2Utils.valueToUpperLog2(toMax - fromMin);
        String tmpValue = readlnIncrInstructionValue(nBits, "intMinMax", "" + fromMin + ", " + toMax);
        return Integer.parseInt(tmpValue);
    }

    @Override
    public byte readByte() {
        String tmpValue = readlnIncrInstructionValue(8, "byte", null);
        return (byte) Integer.parseInt(tmpValue);
    }
    
    @Override
    public void readBytes(byte[] dest, int len) {
        readBytes(dest, 0, len);
    }
    
    @Override
    public void readBytes(byte[] dest, int offset, int len) {
        int nBits = 8 * len;
        String tmpValue = readlnIncrInstructionValue(nBits, "bytes", null);
        Scanner scanner = new Scanner(tmpValue);
        for(int i = 0; i < len; i++) {
            dest[offset+i] = (byte) scanner.nextInt();
        }
        scanner.close();
    }
    
    @Override
    public int readInt() {
        String tmpValue = readlnIncrInstructionValue(32, "int", null);
        return Integer.parseInt(tmpValue);
    }

    @Override
    public void readInts(int[] dest, int offset, int len) {
        int nBits = 32 * len;
        String tmpValue = readlnIncrInstructionValue(nBits, "ints", null);
        parseInts(dest, offset, offset+len, tmpValue);
    }

    private static void parseInts(int[] dest, int fromIndex, int toIndex, String valuesStr) {
        Scanner scanner = new Scanner(valuesStr);
        for(int i = fromIndex; i < toIndex; i++) {
            dest[i] = scanner.nextInt();
        }
        scanner.close();
    }

    @Override
    public float readFloat() {
        String tmpValue = readlnIncrInstructionValue(32, "float", null);
        return Float.parseFloat(tmpValue);
    }
    
    @Override
    public double readDouble() {
        String tmpValue = readlnIncrInstructionValue(64, "double", null);
        return Double.parseDouble(tmpValue);
    }

    // cf java.io.DataInputStream
    @Override
    public String readUTF() {
        // int prevCount = count;
        String tmpValue = readlnIncrInstructionValue(-1, "UTF", null); // -1: size unknown while reading
        String res = tmpValue.replace("\\n", "\n");
        // may recheck?..
        return res;
    }
    
    @Override
    public <T> T readDecodeHuffmanCode(HuffmanTable<T> table) {
        String tmpValue = readlnIncrInstructionValue(-1, "huffmanCode", null); // -1: size unknown while reading
        boolean[] bits = BitsUtil.strBitsToBooleans(tmpValue);
        return table.readDecodeSymbol(bits);
    }

    @Override
    public int readUIntLtMinElseMax(int min, int max) {
        int prevCount = counters.getCurrBitsCount();
        String tmpValue = readlnIncrInstructionValue(-1, "uintLtMinElseMax", "" + min + ", " + max); // -1: size unknown while reading
        int res = Integer.parseInt(tmpValue);
        int nBits = 1;
        if (res < min) {
            nBits += countBitsIntMinMax(0, min);
        } else {
            nBits += countBitsIntMinMax(min, max);
        }
        checkIncr(prevCount, nBits);
        return res;
    }

    @Override
    public int readUIntLt16ElseMax(int max) {
        return readUIntLtMinElseMax(16, max);
    }

    @Override
    public int readUIntLt2048ElseMax(int max) {
        return readUIntLtMinElseMax(2048, max);
    }

    @Override
    public int readUInt0ElseMax(int max) {
        int prevCount = counters.getCurrBitsCount();
        String tmpValue = readlnIncrInstructionValue(-1, "uint0ElseMax", "" + max); // -1: size unknown while reading
        int res = Integer.parseInt(tmpValue);
        int nBits = 1;
        if (res != 0) {
            nBits += countBitsIntMinMax(1, max);
        }
        checkIncr(prevCount, nBits);
        return res;
    }

    @Override
    public void readIntsSorted(int min, int max, boolean distincts, int[] dest, int fromIndex, int toIndex) {
        String tmpValue = readlnIncrInstructionValue(-1, "intsSorted", "" + (toIndex - fromIndex)); // -1: size unknown while reading
        parseInts(dest, fromIndex, toIndex, tmpValue);
        // checkIncr(prevCount, nBits);
    }

}
