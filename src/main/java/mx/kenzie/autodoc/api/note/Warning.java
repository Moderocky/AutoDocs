package mx.kenzie.autodoc.api.note;

import mx.kenzie.autodoc.api.context.DisplayMode;

import java.lang.annotation.*;

@Description("""
    A warning label to be attached to this element.
    The `mode` method selects the content-type this supports.
    Multiple warnings may be attached to a single element.
    """)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR})
@Repeatable(Warning.Multiple.class)
public @interface Warning {
    
    String value();
    
    DisplayMode mode() default DisplayMode.MARKDOWN;
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR})
    @interface Multiple {
        
        Warning[] value();
        
    }
    
}
