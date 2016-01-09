package fr.an.bitwise4j.util;

import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * missing from jdk?  adapter for java.nio.ByteBuffer to java.io.InputStream
 */
public class ByteBufferInputStream extends InputStream {

    private ByteBuffer byteBuffer;

    // ------------------------------------------------------------------------

    public ByteBufferInputStream(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

    // ------------------------------------------------------------------------

    public ByteBuffer getByteBuffer() {
        return byteBuffer;
    }

    public void setByteBuffer(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

    @Override
    public int read() {
        if (!byteBuffer.hasRemaining()) {
            return -1;
        }
        return byteBuffer.get();
    }

    @Override
    public int read(byte[] bytes, int offset, int length) {
        int count = Math.min(byteBuffer.remaining(), length);
        if (count == 0) {
            return -1;
        }
        byteBuffer.get(bytes, offset, length);
        return count;
    }

    @Override
    public int available() {
        return byteBuffer.remaining();
    }

    // ------------------------------------------------------------------------
    
    @Override
    public String toString() {
        return "ByteBufferInputStream [byteBuffer=" + byteBuffer + "]";
    }
    
}
