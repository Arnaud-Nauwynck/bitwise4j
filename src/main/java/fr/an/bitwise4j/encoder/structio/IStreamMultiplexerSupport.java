package fr.an.bitwise4j.encoder.structio;

import java.io.Closeable;

public interface IStreamMultiplexerSupport {

    public abstract String getCurrStream();
    
    public abstract String setCurrStream(String name);


    public static class StreamPopper implements Closeable {
        final IStreamMultiplexerSupport owner;
        final String prev;
        public StreamPopper(IStreamMultiplexerSupport owner, String prev) {
            this.owner = owner;
            this.prev = prev;
        }
        public void close() {
            owner.setCurrStream(prev);
        }
    }

    default public StreamPopper pushSetCurrStream(String name) {
        final String prev = setCurrStream(name);
        return new StreamPopper(this, prev);
    }

}
