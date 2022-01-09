package mx.kenzie.autodoc.test;

import mx.kenzie.autodoc.api.note.Description;
import mx.kenzie.autodoc.api.note.Example;
import mx.kenzie.autodoc.api.note.Warning;
import mx.kenzie.autodoc.impl.site.ClassWriter;
import mx.kenzie.autodoc.impl.site.PageWriter;
import mx.kenzie.autodoc.impl.site.WebsiteDetails;
import org.junit.Test;

import java.io.FileOutputStream;
import java.util.ArrayList;

public class WebsiteTest {
    
    @Test
    public void generate() throws Throwable {
        final PageWriter writer = new PageWriter(Blob.class, new WebsiteDetails("hello", new ArrayList<>()), "My Title", "Cool description", "hello", "there");
        final FileOutputStream stream = new FileOutputStream("target/test.html");
        writer.write(stream, new ClassWriter(Blob.class), new ClassWriter(Bean.class), new ClassWriter(Blob.Foo.class));
    }
    
    @Example("""
        public String test() {
            return "hello " + 6;
        }
        """)
    
    @Example("""
        public void bean() {
            System.out.println("jhello");
        }
        """)
    interface Bean {
    
    }
    
    @Description("""
        This is a cool class.
        
        It supports `markdown` stuff! :)
        ```java
        public void test(String blob) {
            System.out.println(blob);
        }
        ```
        """)
    @Warning("This is an example warning.")
    @Example("""
        public void bean() {
            System.out.println("jhello");
        }
        """)
    @Example(language = "php", value = """
        $x = 6;
        
        do {
          echo "The number is: $x <br>";
          $x++;
        } while ($x <= 5);
        """
    
    )
    public static class Blob implements Bean {
        
        @Description("""
            My cool method. :)""")
        @Example("""
            void test() {
                blob.beanMethod("hello", 10, 'c');
            }
            """)
        public native void beanMethod(String s, int i, Character c);
        
        @Description("""
            This is a nested class :o
            
            it doesn't do anything tbh
            """)
        @Deprecated
        protected final class Foo extends Blob {
        
        }
    }
    
}
