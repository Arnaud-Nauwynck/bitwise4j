package fr.an.util.encoder.structio;

@FunctionalInterface
public interface Value2StructDataOutputWriter<T> {

    public void writeTo(StructDataOutput output, T value);
    
}
