package mx.kenzie.autodoc.test;

import mx.kenzie.autodoc.internal.ScratchReader;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.util.Map;

/**
 * blob blob :)
 */
public class ReaderTest {
    
    /**
     * thingy :)
     */
    final File root = new File("src/test/java/");
    
    /**
     * box box
     *
     * @throws IOException
     */
    @Test
    public void test() throws IOException {
        final ScratchReader reader = ScratchReader.reader(ReaderTest.class, root);
        final Map<AnnotatedElement, String> map = reader.read();
        assert map.size() == 3;
    }
    
}
