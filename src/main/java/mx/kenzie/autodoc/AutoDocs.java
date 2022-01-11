package mx.kenzie.autodoc;

import mx.kenzie.autodoc.api.note.Description;
import mx.kenzie.autodoc.api.note.Example;
import mx.kenzie.autodoc.api.note.Ignore;
import mx.kenzie.autodoc.impl.site.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Description("""
    The main class and best entry-point to generate documentation.
    Currently, this supports generating from loaded classes to an output directory.
    
    This is for webpage documentation. If another format is required, the schema must be run directly.
    """)
public final class AutoDocs {
    
    @Ignore
    public static void main(String... args) {
        if (args.length < 2) exit("Correct usage: <source> <target>");
        final File jar = new File(args[0]);
        final File target = new File(args[1]);
        if (!jar.isFile()) exit("Jar must be a file.");
        if (target.isFile()) exit("Target must be the output directory.");
        // todo
    }
    
    @Ignore
    static void exit(String message) {
        System.out.println(message);
        System.exit(0);
    }
    
    @Description("""
        Generates documentation from loaded classes with their provided source Jar.
        This method was designed for building documentation in IntelliJ run suite, since the code-source of the loaded classes would be unavailable.
        The source file is used to identify all classes under the namespace.
        
        The title and page description are used in the HTML meta tags.
        The body is used in the first element of the index page, above the class list.
        """)
    @Example("""
        // The body is displayed on the index page and rendered with markdown.
        // You could stream a .md file to this.
        AutoDocs.generateDocumentation(title, pageDescription, markdownBody, outputFolder, sourceJar, "org.example");
        """)
    @Example("""
        // Multiple packages can be used for filtering. Specify an empty "" string to catch everything.
        AutoDocs.generateDocumentation(title, pageDescription, markdownBody, outputFolder, sourceJar, "org.example", "my.package");
        """)
    public static void generateDocumentation(String title, String description, String body, File output, File source, String... namespaces) throws IOException {
        final List<Class<?>> list = new ArrayList<>();
        final URL jar = source.toURI().toURL();
        for (final String namespace : namespaces) {
            try (final ZipInputStream zip = new ZipInputStream(jar.openStream())) {
                while (true) {
                    final ZipEntry entry = zip.getNextEntry();
                    if (entry == null) break;
                    if (entry.isDirectory()) continue;
                    final String name = entry.getName().replace('/', '.');
                    if (name.startsWith(namespace)) try {
                        final Class<?> data = Class.forName(name
                            .substring(0, name.length() - 6), false, AutoDocs.class.getClassLoader());
                        if (data.isAnonymousClass() || data.isLocalClass() || data.isHidden() || data.isSynthetic())
                            continue;
                        if (data.isAnnotationPresent(Ignore.class)) continue;
                        list.add(data);
                    } catch (ClassNotFoundException ignored) {}
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                break;
            }
        }
        final Class<?>[] classes = list.toArray(new Class[0]);
        generateDocumentation(title, description, body, output, classes);
    }
    
    @Description("""
        Generates documentation from loaded classes.
        This method cannot be used in the IntelliJ run suite or similar, since the class code-source is unavailable.
        
        The title and page description are used in the HTML meta tags.
        The body is used in the first element of the index page, above the class list.
        """)
    @Example("""
        // The body is displayed on the index page and rendered with markdown.
        // You could stream a .md file to this.
        AutoDocs.generateDocumentation(title, pageDescription, markdownBody, outputFolder, MyClass.class);
        """)
    @Example("""
        // Multiple packages can be used for filtering. Specify an empty "" string to catch everything.
        AutoDocs.generateDocumentation(title, pageDescription, markdownBody, outputFolder, MyClass.class, YourClass.class);
        """)
    public static void generateDocumentation(String title, String description, String body, File output, Class<?>... classes) throws IOException {
        for (final Class<?> type : classes) {
            final String name = type.getName().replace('.', File.separatorChar);
            final File file = new File(output, name + ".html");
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            final PageWriter writer = new PageWriter(
                type,
                new WebsiteDetails(title, Arrays.asList(classes)),
                type.getSimpleName(),
                "Documentation for this class and its members.",
                type.getSimpleName(),
                type.getName(),
                type.getPackageName());
            try (final FileOutputStream stream = new FileOutputStream(file)) {
                writer.write(stream, new ClassWriter(type));
            }
        }
        final Set<String> directories = new HashSet<>();
        for (final Class<?> type : classes) {
            addPackages(type, directories);
        }
        for (final String directory : directories) {
            final File folder = new File(output, directory.replace('.', File.separatorChar));
            if (!folder.exists() || !folder.isDirectory()) continue;
            final File index = new File(folder, "index.html");
            if (!index.exists()) index.createNewFile();
            final List<Class<?>> list = filterClasses(directory, classes);
            final IndexPageWriter writer = new IndexPageWriter(directory,
                new WebsiteDetails(title, Arrays.asList(classes)),
                "Index of " + directory,
                "Documentation for this package.",
                directory);
            try (final FileOutputStream stream = new FileOutputStream(index)) {
                writer.write(stream, new ClassIndexWriter(directory, list));
            }
        }
        final File index = new File(output, "index.html");
        final RootIndexPageWriter writer = new RootIndexPageWriter(new WebsiteDetails(title, Arrays.asList(classes)),
            title,
            description != null ? description : "Procedurally-generated documentation, examples and insights about " + classes.length + " unique classes.",
            title.split(" "));
        try (final FileOutputStream stream = new FileOutputStream(index)) {
            final List<Class<?>> list = filterClasses("", classes);
            writer.write(stream, new BodyWriter(body, classes), new ClassIndexWriter("", list));
        }
    }
    
    private static void addPackages(Class<?> type, Set<String> directories) {
        String string = type.getName();
        int index;
        while ((index = string.lastIndexOf('.')) > -1) {
            string = string.substring(0, index);
            directories.add(string);
        }
    }
    
    private static List<Class<?>> filterClasses(String namespace, Class<?>... classes) {
        final List<Class<?>> list = new ArrayList<>();
        for (final Class<?> type : classes) {
            if (type.isAnnotationPresent(Ignore.class)) continue;
            if (type.getPackageName().startsWith(namespace)) list.add(type);
        }
        final Comparator<Class<?>> first = Comparator.comparing(Class::getPackageName);
        final Comparator<Class<?>> second = Comparator.comparing(Class::getModifiers);
        final Comparator<Class<?>> third = Comparator.comparing(Class::getSimpleName);
        list.sort(first.thenComparing(second).thenComparing(third));
        return list;
    }
    
    @Description("""
        Generates documentation from the given packages.
        This will search all sub-packages.
        This method cannot be used in the IntelliJ run suite or similar, since the class code-source is unavailable.
        
        The title and page description are used in the HTML meta tags.
        The body is used in the first element of the index page, above the class list.
        """)
    @Example("""
        // The body is displayed on the index page and rendered with markdown.
        // You could stream a .md file to this.
        AutoDocs.generateDocumentation(title, pageDescription, markdownBody, outputFolder, "org.example");
        """)
    @Example("""
        // Multiple packages can be used for filtering. Specify an empty "" string to catch everything.
        AutoDocs.generateDocumentation(title, pageDescription, markdownBody, outputFolder, "org.example", "org.thing");
        """)
    public static void generateDocumentation(String title, String description, String body, File output, String... namespaces) throws IOException {
        final List<Class<?>> list = new ArrayList<>();
        final CodeSource source = AutoDocs.class.getProtectionDomain().getCodeSource();
        if (source == null) return;
        final URL jar = source.getLocation();
        for (final String namespace : namespaces)
            try (final ZipInputStream zip = new ZipInputStream(jar.openStream())) {
                while (true) {
                    final ZipEntry entry = zip.getNextEntry();
                    if (entry == null) break;
                    if (entry.isDirectory()) continue;
                    final String name = entry.getName();
                    if (name.startsWith(namespace)) try {
                        final Class<?> data = Class.forName(name
                            .substring(0, name.length() - 6)
                            .replace('/', '.'), false, AutoDocs.class.getClassLoader());
                        if (data.isAnonymousClass() || data.isLocalClass() || data.isHidden() || data.isSynthetic())
                            continue;
                        if (data.isAnnotationPresent(Ignore.class)) continue;
                        list.add(data);
                    } catch (ClassNotFoundException ignored) {}
                }
            }
        final Class<?>[] classes = list.toArray(new Class[0]);
        generateDocumentation(title, description, body, output, classes);
    }
    
}
