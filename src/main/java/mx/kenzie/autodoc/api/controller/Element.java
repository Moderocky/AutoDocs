package mx.kenzie.autodoc.api.controller;

import mx.kenzie.autodoc.api.schema.WritableElement;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public interface Element extends WritableElement {
    
    String name();
    
    Map<String, Object> getDetails();
    
    default @Override
    void write(OutputStream target) throws IOException {
    
    }
}
