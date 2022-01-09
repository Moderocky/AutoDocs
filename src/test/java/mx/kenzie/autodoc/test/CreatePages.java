package mx.kenzie.autodoc.test;

import mx.kenzie.autodoc.AutoDocs;

import java.io.File;
import java.io.IOException;

public class CreatePages {
    
    public static void main(String[] args) throws IOException {
        AutoDocs.generateDocumentation("AutoDocs", new File("docs/"), new File("target/AutoDocs.jar"), "mx.kenzie.autodoc.api");
    }
    
}
