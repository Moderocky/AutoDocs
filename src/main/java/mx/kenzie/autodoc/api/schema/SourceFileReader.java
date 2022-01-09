package mx.kenzie.autodoc.api.schema;

import java.io.IOException;
import java.io.InputStream;

@FunctionalInterface
public interface SourceFileReader {
    
    void read(InputStream stream) throws IOException;
    
}
