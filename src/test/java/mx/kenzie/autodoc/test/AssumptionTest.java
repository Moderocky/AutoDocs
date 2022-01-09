package mx.kenzie.autodoc.test;

import mx.kenzie.autodoc.api.note.Example;
import org.junit.Test;

import java.lang.annotation.Annotation;

public class AssumptionTest {
    
    @Test
    @SuppressWarnings("all")
    public void annotationSuperclass() {
        assert new Example() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }
            
            @Override
            public String value() {
                return null;
            }
            
            @Override
            public String language() {
                return null;
            }
        } instanceof Annotation;
    }
    
}
