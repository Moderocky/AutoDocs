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
@GenerateExample
public @interface Example {
    
    @Ignore
    String value();
    
    @Description("""
        The `highlight.js` language key name to use in generation.
        This is typically used by markdown parsers, but should be transferrable to other formats.
        """)
    String language() default "java";
    
    @Ignore
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR})
    @interface Multiple {
        
        @Ignore
        Example[] value();
        
    }
    
}
