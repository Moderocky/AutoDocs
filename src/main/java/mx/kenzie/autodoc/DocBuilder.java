package mx.kenzie.autodoc;

import mx.kenzie.autodoc.api.note.Ignore;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * A utility class for generating documentation.
 * This resource has the parameters in a builder pattern for easier management.
 * <p>
 * This is equivalent to using the long-form methods from [this class](AutoDocs.html).
 */
public class DocBuilder implements Closeable, AutoCloseable {
    
    final List<Class<?>> classes = new ArrayList<>();
    final String title;
    final File output;
    String description, body;
    File source, jar;
    
    public DocBuilder(String title, File outputFolder) {
        this.title = title;
        this.output = outputFolder;
    }
    
    public DocBuilder setDescription(String description) {
        this.description = description;
        return this;
    }
    
    /**
     * This page body accepts markdown.
     */
    public DocBuilder setBody(String body) {
        this.body = body;
        return this;
    }
    
    /**
     * Set the Jar file, if the protectiondomain is unavailable in the environment.
     */
    public DocBuilder setJar(File jar) {
        this.jar = jar;
        return this;
    }
    
    /**
     * Adds the source root, which allows JavaDoc comments to be scraped.
     */
    public DocBuilder setSourceRoot(File source) {
        this.source = source;
        return this;
    }
    
    public DocBuilder addClasses(Class<?>... classes) {
        this.classes.addAll(Arrays.asList(classes));
        return this;
    }
    
    public DocBuilder addClasses(List<Class<?>> classes) {
        this.classes.addAll(classes);
        return this;
    }
    
    public DocBuilder addClassesFrom(String namespace) throws IOException {
        this.classes.addAll(this.getClasses(namespace));
        return this;
    }
    
    private List<Class<?>> getClasses(String namespace) throws IOException {
        final List<Class<?>> list = new ArrayList<>();
        final URL jar = this.source();
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
        return list;
    }
    
    private URL source() throws MalformedURLException {
        if (jar != null) {
            return jar.toURI().toURL();
        } else {
            final CodeSource source = AutoDocs.class.getProtectionDomain().getCodeSource();
            return source.getLocation();
        }
    }
    
    /**
     * Generates the documentation.
     *
     * @throws IOException
     */
    public void build() throws IOException {
        AutoDocs.generateDocumentation(title, description, body, output, source, classes.toArray(new Class[0]));
    }
    
    @Ignore
    @Override
    public void close() {
        this.classes.clear();
    }
}
