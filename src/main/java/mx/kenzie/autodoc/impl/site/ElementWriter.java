package mx.kenzie.autodoc.impl.site;

import mx.kenzie.autodoc.api.schema.WritableElement;

import java.io.IOException;
import java.io.OutputStream;

public interface ElementWriter extends WritableElement {
    
    default void startBlock(OutputStream stream) throws IOException {
        this.write(stream, """
            <div class="row g-0 border rounded flex-md-row mb-4 shadow-sm h-md-250 position-relative">
              <div class="col col-lg-8 col-sm-12 p-4 d-flex flex-column position-static">""");
    }
    
    default void startSidebar(OutputStream stream) throws IOException {
    
    }
    
    default void endBlock(OutputStream stream) throws IOException {
        this.write(stream, "\n</div>");
        
    }
    
    default void endSidebar(OutputStream stream) throws IOException {
    
    }
    
}
