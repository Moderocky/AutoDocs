package mx.kenzie.autodoc.api.schema;

import java.lang.annotation.Annotation;

public interface AnnotationHandler {
    
    boolean matches(Annotation annotation);
    
}
