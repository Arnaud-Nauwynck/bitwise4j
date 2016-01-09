package fr.an.bitwise4j.encoder.structio;

@FunctionalInterface
public interface Value2StructDataOutputWriter<T> {

    public void writeTo(StructDataOutput output, T value);
    
}
