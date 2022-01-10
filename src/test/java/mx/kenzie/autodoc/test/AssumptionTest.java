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
    
    @Test
    public void naming() {
        System.out.println(Blob.class.getName());
        System.out.println(Blob.class.getPackageName());
        System.out.println(Blob.class.getSimpleName());
        System.out.println(Blob.class.getCanonicalName());
        System.out.println(Blob.class.getTypeName());
    }
    
    @Test
    public void casing() {
        System.out.println("a: " + ('a' - 'A'));
        System.out.println("b: " + ('b' - 'B'));
        System.out.println("c: " + ('c' - 'C'));
        System.out.println("d: " + ('d' - 'D'));
        System.out.println("z: " + ('z' - 'Z'));
    }
    
    public class Blob {
    
    }
    
}
