package mx.kenzie.autodoc.test;

import mx.kenzie.autodoc.AutoDocs;
import org.junit.Test;

public class ExtractionTest {
    
    @Test
    public void readThis() {
        AutoDocs.main("target/AutoDocs.jar", "src/main/java/");
    }
    
}
