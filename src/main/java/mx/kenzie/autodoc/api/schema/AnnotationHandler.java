package mx.kenzie.autodoc.api.schema;

import mx.kenzie.autodoc.api.note.Description;
import mx.kenzie.autodoc.api.note.Example;

import java.io.OutputStream;
import java.lang.annotation.Annotation;

@Description("""
    A customisable handler for parsing annotations.
    This can be used to add extra details to the output.
    
    These handlers are specific to an output schema.
    """)
@Example("""
    class MyHandler implements AnnotationHandler {
        @Override
        public boolean accepts(Annotation annotation) {
            return annotation instanceof Example;
        }
        
        @Override
        public void parse(Annotation annotation, OutputStream target) {
            // pretty print the data here
        }
    }
    """)
public interface AnnotationHandler {
    
    @Description("""
        Determines whether this handler can process the given annotation.
        This can be used as a type (or multi-type check.)
        
        If this passes, the [parse](#method:parse) method will be called.
        """)
    @Example("""
        public boolean accepts(Annotation annotation) {
            return annotation instanceof MyAnnotation;
        }
        """)
    boolean accepts(Annotation annotation);
    
    @Description("""
        Pretty-writes the content of this annotation to the output stream.
        This will not differentiate between the single and multiple form of the annotation.
        """)
    void parse(Annotation annotation, OutputStream target);
    
    @Description("""
        The state where this is to be printed.
        This is designed for use with the website schema.
        """)
    default State state() {
        return State.IN_BLOCK;
    }
    
    enum State {
        BEFORE_ELEMENT,
        IN_BLOCK
    }
    
}
