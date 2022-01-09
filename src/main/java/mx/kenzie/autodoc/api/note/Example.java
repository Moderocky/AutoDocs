package mx.kenzie.autodoc.api.note;

import java.lang.annotation.*;

@Description("""
    A collapsible example to be attached to this element.
    The `language` setting selects the example's language.
    This is intended to be a highlight.js markdown language.
    """)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR})
@Repeatable(Example.Multiple.class)
public @interface Example {
    
    String value();
    
    String language() default "java";
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR})
    @interface Multiple {
        
        Example[] value();
        
    }
    
}
