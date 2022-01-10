package mx.kenzie.autodoc.api.note;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Description("""
    Generates an automatic example for this element.
    These examples are quite arbitrary and may not be particularly useful.
    
    They are designed to provide some guidance as to how a method might look when used,
    or as something to copy and paste.
    """)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface GenerateExample {
    
    @Ignore
    boolean value() default true;
    
}
