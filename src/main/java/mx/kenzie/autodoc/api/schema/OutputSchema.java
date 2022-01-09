package mx.kenzie.autodoc.api.schema;

import mx.kenzie.autodoc.api.note.Description;
import mx.kenzie.autodoc.api.note.Ignore;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@Description("""
    A schema for formatting and writing documentation.
    The writable elements in this should be specific to this schema's writing format.
    """)
public interface OutputSchema {
    
    @Ignore
    String name();
    
    @Ignore
    String description();
    
    @Description("""
        Write elements to the target output stream, according to this schema.
        The schema may define specific formatting, find resources or add writing controls here.
        This should return true only if the write was successful.
        """)
    boolean write(final OutputStream target, final WritableElement... element) throws IOException;
    
    default void write(final OutputStream stream, final String content) throws IOException {
        stream.write(content.getBytes(StandardCharsets.UTF_8));
    }
    
}
