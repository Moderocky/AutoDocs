package mx.kenzie.autodoc.api.schema;

import mx.kenzie.autodoc.api.controller.Element;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public interface OutputSchema {
    
    String name();
    
    String description();
    
    boolean write(final OutputStream target, final Element element) throws IOException;
    
    default void write(final OutputStream stream, final String content) throws IOException {
        stream.write(content.getBytes(StandardCharsets.UTF_8));
    }
    
}
