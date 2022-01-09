package mx.kenzie.autodoc.api.note;

import mx.kenzie.autodoc.api.context.DisplayMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Description("""
    The description value of an element.
    The `mode` method selects the content-type this supports.
    """)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface Description {
    
    String value();
    
    DisplayMode mode() default DisplayMode.MARKDOWN;
    
}
