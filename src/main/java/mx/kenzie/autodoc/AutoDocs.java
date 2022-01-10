package mx.kenzie.autodoc;

import mx.kenzie.autodoc.api.context.Context;
import mx.kenzie.autodoc.api.note.Ignore;
import mx.kenzie.autodoc.impl.site.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.security.CodeSource;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class AutoDocs {
    
    public static void main(String... args) {
        if (args.length < 2) exit("Correct usage: <source> <target>");
        final File jar = new File(args[0]);
        final File target = new File(args[1]);
        if (!jar.isFile()) exit("Jar must be a file.");
        if (target.isFile()) exit("Target must be the output directory.");
    }
    
    static void exit(String message) {
        System.out.println(message);
        System.exit(0);
    }
    
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
                        if (data.isAnonymousClass() || data.isLocalClass() || data.isHidden() || data.isSynthetic()) continue;
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
                        if (data.isAnonymousClass() || data.isLocalClass() || data.isHidden() || data.isSynthetic()) continue;
                        list.add(data);
                    } catch (ClassNotFoundException ignored) {}
                }
            }
        final Class<?>[] classes = list.toArray(new Class[0]);
        generateDocumentation(title, description, body, output, classes);
    }
    
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
    
    private static List<Class<?>> filterClasses(String namespace, Class<?>... classes) {
        final List<Class<?>> list = new ArrayList<>();
        for (final Class<?> type : classes) {
            if (type.isAnnotationPresent(Ignore.class)) continue;
            if (type.getPackageName().startsWith(namespace)) list.add(type);
        }
        return list;
    }
    
    private static void addPackages(Class<?> type, Set<String> directories) {
        String string = type.getName();
        int index;
        while ((index = string.lastIndexOf('.')) > -1) {
            string = string.substring(0, index);
            directories.add(string);
        }
    }
    
}
