package fr.an.util.bits;

import java.io.IOException;
import java.io.OutputStream;

/**
 * class for writing individual bits from bytes OutputStream
 */
public class OutputStreamToBitOutputStream extends BitOutputStream {

    /**
     * precomputed bit mask for binary values "1", "11", "111", "1111", ...,
     * "111..(x32)"
     */
    private static final int BITS_MASKS[/* 32 */] = { 0x00, 0x01, 0x03, 0x07, 0x0f, 0x1f, 0x3f, 0x7f, 0xff, 0x1ff, 0x3ff, 0x7ff, 0xfff, 0x1fff,
        0x3fff, 0x7fff, 0xffff, 0x1ffff, 0x3ffff, 0x7ffff, 0xfffff, 0x1fffff, 0x3fffff, 0x7fffff, 0xffffff, 0x1ffffff, 0x3ffffff, 0x7ffffff,
        0xfffffff, 0x1fffffff, 0x3fffffff, 0x7fffffff, 0xffffffff };

    /**
     * The Java OutputStream that is used to write completed bytes.
     */
    private OutputStream targetOutputStream;

    /**
     * The temporary buffer containing the individual bits until a byte has been
     * completed and can be commited to the output stream.
     */
    private int bitsBuffer;

    /**
     * Counts how many bits have been cached up to now.
     */
    private int bitsBufferLen;

    private int debugCountTotalBytes;

    // ------------------------------------------------------------------------

    public OutputStreamToBitOutputStream(OutputStream aOs) {
        this.targetOutputStream = aOs;
    }

    // ------------------------------------------------------------------------

    private void writeTargetByte(int p) {
        try {
            targetOutputStream.write(p);
        } catch (IOException ex) {
            throw new RuntimeIOException("Failed writeTargetByte", ex);
        }
        debugCountTotalBytes++;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.an.util.bits.BitOutputStream#flush()
     */
    public void flush() {
        if (bitsBufferLen > 0) {
            writeTargetByte(bitsBuffer);
            bitsBufferLen = 0;
            bitsBuffer = 0;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.an.util.bits.BitOutputStream#close()
     */
    public void close() {
        try {
            flush();
        } catch (Exception ex) {
            // close anyway! .. rethrow?
        }
        try {
            targetOutputStream.close();
        } catch (IOException e) {
            // rethrow?
        }
        targetOutputStream = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.an.util.bits.BitOutputStream#writeBit(boolean)
     */
    public void writeBit(boolean p) {
        writeNBits(1, (p) ? 1 : 0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.an.util.bits.BitOutputStream#writeBits(int, int)
     */
    public void writeNBits(int count, int bitsValue) {
        bitsValue &= BITS_MASKS[count]; // only right most bits valid

        if (count + bitsBufferLen > 8) {
            while (count + bitsBufferLen > 8) {
                int paddingTo8 = 8 - bitsBufferLen;
                int bitsToComplete = bitsValue >> (count - paddingTo8);
                int valueToFlush = (bitsBuffer << paddingTo8) | bitsToComplete;

                writeTargetByte(valueToFlush);

                count -= paddingTo8;
                bitsValue = bitsValue & BITS_MASKS[count];
                bitsBufferLen = 0;
                bitsBuffer = 0;
            }
        }

        if (count > 0) {
            bitsBuffer = (bitsBuffer << count) | bitsValue;
            bitsBufferLen += count;
        }
        if (bitsBufferLen == 8) {
            writeTargetByte(bitsBuffer);
            bitsBufferLen = 0;
            bitsBuffer = 0;
        }
    }

    
    public String toString() {
        return "BitOutputSteam[" + "writtenBytes:" + debugCountTotalBytes + ", bits buffer len:" + bitsBufferLen + " : '"
            + new BitArray32(bitsBuffer, bitsBufferLen).toString() + "'" + "]";
    }
}
