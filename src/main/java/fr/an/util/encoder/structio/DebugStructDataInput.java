package fr.an.util.encoder.structio;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.an.util.bits.BitsUtil;
import fr.an.util.bits.RuntimeIOException;
import fr.an.util.encoder.huffman.HuffmanTable;

/**
 * implements StructDataInput using underlying Debug text InputStream, with 1 instruction per line
 * 
 */
public class DebugStructDataInput extends StructDataInput {

    private static final Pattern INSTRUCTION_LINE_PATTERN = Pattern.compile("\\[(\\d+) : (\\d+)\\] (\\w+)(\\(([^:]*)\\))?: (.*)"); 

    private BufferedReader in;
    private String readAheadLine;
    
    private int count;
    
    private int instrLineCount;
    
    // ------------------------------------------------------------------------

    public DebugStructDataInput(BufferedReader target) {
        this.in = target;
    }

    // ------------------------------------------------------------------------

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

    @Override
    public boolean hasMoreBit() {
        if (readAheadLine != null) {
            return true;
        }
        try {
            this.readAheadLine = in.readLine();
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
            } catch (IOException e) {
                throw new RuntimeIOException("Failed", e);
            }
        }
        instrLineCount++;
        return line;
    }
    
    protected RuntimeException failEx(String line, String text) {
        throw new IllegalStateException("Failed to read at line " + instrLineCount + " '" + line + "', " + text);
    }
    
    protected String readlnIncrInstructionValue(int expectedNBits, String expectedInstructionName, String expectedParams) {
        String line = doReadLine();
        Matcher matcher = INSTRUCTION_LINE_PATTERN.matcher(line);
        if (!matcher.matches()) {
            throw failEx(line, "expected line pattern " + INSTRUCTION_LINE_PATTERN + ", got '" + line + "'");
        }
        int nBits = Integer.parseInt(matcher.group(1));
        if (expectedNBits > 0) {
            if (expectedNBits != nBits) {
                throw failEx(line, "expected bits count " + expectedNBits + ", got '" + nBits + "'");
            }
        }
        count += nBits;
        int checkCount = Integer.parseInt(matcher.group(2));
        if (checkCount != count) {
            throw failEx(line, "expecting count: "+ checkCount + ", got: " + count);
        }
        String instr = matcher.group(3);
        if (!expectedInstructionName.equals(instr)) {
            throw failEx(line, "expecting instr '" + expectedInstructionName + ", got: " + instr);
        }
        
        String params = matcher.group(5);
        if (expectedParams != null && !expectedParams.equals(params)) {
            throw failEx(line, "expecting instr " + instr + " params ("+ expectedParams + "), got: (" + params + ")");
        }
        String value = matcher.group(6);
        return value;
    }

    
    private void checkIncr(int prevCount, int nBits) {
        int checkCount = prevCount + nBits;
        if (checkCount != count) {
            throw failEx(null, "expecting bits count: "+ count  + ", got " + checkCount + "=" + prevCount + "+" + nBits);
        }
    }

    private int countBitsIntMinMax(int fromMin, int toMax) {
        int maxAmplitude = toMax - fromMin;
        int nBits = Pow2Utils.valueToUpperLog2(maxAmplitude);
        return nBits;
    }

    
    @Override
    public int readBits(int readBitsCounts) {
        String tmpValue = readlnIncrInstructionValue(readBitsCounts, "bits", null);
        return BitsUtil.stringToBits(tmpValue);
    }
    
    @Override
    public boolean readBit() {
        String tmpValue = readlnIncrInstructionValue(1, "bit", null);
        return tmpValue.equals("1");
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
            dest[i] = (byte) scanner.nextInt();
        }
        scanner.close();
    }
    
    @Override
    public int readInt() {
        String tmpValue = readlnIncrInstructionValue(32, "int", null);
        return Integer.parseInt(tmpValue);
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
        int prevCount = count;
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
        int prevCount = count;
        String tmpValue = readlnIncrInstructionValue(-1, "uint0ElseMax", "" + max); // -1: size unknown while reading
        int res = Integer.parseInt(tmpValue);
        int nBits = 1;
        if (res != 0) {
            nBits += countBitsIntMinMax(1, max);
        }
        checkIncr(prevCount, nBits);
        return res;
    }

}
