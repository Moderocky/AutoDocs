package mx.kenzie.autodoc.impl.site;

import mx.kenzie.autodoc.api.note.Ignore;
import mx.kenzie.autodoc.api.schema.WritableElement;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.AnnotatedElement;

@Ignore
public interface ElementWriter extends WritableElement {
    
    default void createSection(AnnotatedElement target, OutputStream stream) throws IOException {
        if (Utils.hasLongExamples(target)) {
            this.write(stream, """
                <div class="row mb-2">
                <div class="col-lg-8 col-md-12 col-sm-12">""");
        } else {
            this.write(stream, """
                <div class="row mb-2">
                <div class="col-lg-8 col-md-12 col-sm-12">""");
        }
    }
    
    default void startBlock(OutputStream stream) throws IOException {
        this.write(stream, "\n<div class=\"row g-0 border rounded flex-md-row mb-4 shadow-sm h-md-250 position-relative\">");
    }
    
    default void startMainArea(OutputStream stream) throws IOException {
        this.write(stream, "\n<div class=\"col col-lg-8 col-sm-12 pt-1 pb-4 px-4 d-flex flex-column position-static\">");
    }
    
    default void startSidebar(OutputStream stream) throws IOException {
    
    }
    
    default void endBlock(OutputStream stream) throws IOException {
        this.write(stream, "\n</div>");
        
    }
    
    default void endSidebar(OutputStream stream) throws IOException {
    
    }
    
}
