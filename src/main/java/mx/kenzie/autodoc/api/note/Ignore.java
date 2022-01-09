package mx.kenzie.autodoc.api.note;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Description("""
    Whether to ignore this element when documenting the class.
    Ignored elements will not have an entry stub generated for them.
    They will also not be listed in the member list.
    """)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface Ignore {
    
    @Ignore
    boolean value() default true;
    
}
