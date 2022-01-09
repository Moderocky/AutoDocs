package mx.kenzie.autodoc.api.schema;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public interface WritableElement {
    
    void write(OutputStream target) throws IOException;
    
    default void write(final OutputStream stream, final String content) throws IOException {
        stream.write(content.getBytes(StandardCharsets.UTF_8));
    }
    
}
