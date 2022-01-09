package mx.kenzie.autodoc;

import mx.kenzie.autodoc.api.context.Context;
import mx.kenzie.autodoc.internal.DocReader;

import java.io.File;
import java.io.OutputStream;

public class AutoDocs {
    
    public static void main(String... args) {
        if (args.length < 2) exit("Correct usage: <jar> <source>");
        final File jar = new File(args[0]);
        final File source = new File(args[1]);
        if (!jar.isFile()) exit("Jar must be a file.");
        if (source.isFile()) exit("Source must be the root directory.");
        
    }
    
    static void exit(String message) {
        System.out.println(message);
        System.exit(0);
    }
    
    public static DocReader createReader() {
        return null;
    }
    
    public void generate(final OutputStream stream, final Context context) {
    
    }
    
}
