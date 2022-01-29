package mx.kenzie.autodoc.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A reading utility designed for extracting JavaDoc comments from source code,
 * and attaching them to the correct element in memory.
 */
public class ScratchReader {
    
    protected final Class<?> owner;
    protected final String source;
    protected final List<AnnotatedElement> members;
    protected TypeMatcher matcher;
    protected StringReader reader;
    protected String comment;
    protected AnnotatedElement object;
    
    public ScratchReader(Class<?> owner, String source) {
        this.owner = owner;
        this.source = source;
        this.reader = new StringReader(source);
        this.members = new ArrayList<>();
        this.members.add(owner);
        this.members.addAll(Arrays.asList(owner.getDeclaredMethods()));
        this.members.addAll(Arrays.asList(owner.getDeclaredFields()));
        this.members.addAll(Arrays.asList(owner.getDeclaredConstructors()));
    }
    
    public static Map<AnnotatedElement, String> find(Class<?> owner, File root) {
        try {
            return reader(owner, root).read();
        } catch (IOException ex) {
            return new HashMap<>();
        }
    }
    
    public Map<AnnotatedElement, String> read() {
        final Map<AnnotatedElement, String> map = new HashMap<>();
        while (reader.canRead()) {
            this.readNext();
            if (comment == null && object == null) continue;
            if (comment != null && object != null) map.put(object, comment);
            this.comment = null;
            this.object = null;
        }
        return map;
    }
    
    public static ScratchReader reader(Class<?> owner, File root) throws IOException {
        final File source = new File(root, owner.getName().replace('.', File.separatorChar) + ".java");
        if (!source.exists()) throw new IOException("Unable to find source file.");
        try (final FileInputStream stream = new FileInputStream(source)) {
            final String string = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            return new ScratchReader(owner, string);
        }
    }
    
    protected void readNext() {
        this.reader.readUntil("/**");
        this.reader.read(3);
        this.comment = this.handleComment(reader.readUntil("*/").trim());
        this.reader.read(2);
        while ((matcher = TypeMatcher.matcher(reader)) == null) {
            if (!reader.canRead()) return;
            this.reader.trim();
            if (this.reader.remainingString().startsWith("/**")) return;
            this.reader.readWord();
            this.reader.trim();
        }
        this.object = matcher.find(members);
    }
    
    protected String handleComment(String comment) {
        final StringBuilder builder = new StringBuilder();
        for (final String line : comment.lines().toList()) {
            try {
                String thing = line.replaceFirst("^\\s*\\*\\s*", "").trim();
                if (thing.equals("<p>")) continue;
                builder.append(thing);
            } catch (Throwable ignore) {
                builder.append(line);
            }
            builder.append(System.lineSeparator());
        }
        return builder.toString();
    }
    
    enum TypeMatcher {
        
        CLASS(Pattern.compile("^(?:class|interface|record)\\s+(?<name>\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*)\\b")) {
            @Override
            public AnnotatedElement find(List<AnnotatedElement> list) {
                final String name = matcher.group("name");
                for (final AnnotatedElement element : list) {
                    if (element instanceof Class<?> type && type.getSimpleName().equals(name)) return element;
                }
                return null;
            }
        },
        METHOD(Pattern.compile("^(?<type>\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*(?:\\s*<.+>)?)\\s+(?<name>\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*)\\s*\\(" +
            "(?<params>[^)]*)" +
            "\\)")) {
            @Override
            public AnnotatedElement find(List<AnnotatedElement> list) {
                final List<Method> methods = new ArrayList<>();
                for (final AnnotatedElement element : list) {
                    if (element instanceof Method method) methods.add(method);
                }
                final String type = matcher.group("type");
                final String name = matcher.group("name");
                final String params = matcher.group("params");
                methods.removeIf(method -> !method.getName().equals(name));
                if (methods.size() == 1) return methods.get(0);
                methods.removeIf(method -> !method.getReturnType().getName().endsWith(type));
                if (methods.size() == 1) return methods.get(0);
                if (!params.isEmpty()) {
                    final String[] parameters = params.trim().split(",\\w*");
                    methods.removeIf(method -> method.getParameterCount() != parameters.length);
                    if (methods.size() == 1) return methods.get(0);
                    final String[] things = new String[parameters.length];
                    for (int i = 0; i < parameters.length; i++) {
                        final String parameter = parameters[i];
                        final String[] parts = parameter.split("\\w+");
                        final String thing = parts[parts.length - 2];
                        things[i] = thing;
                    }
                    methods.removeIf(method -> {
                        final Class<?>[] classes = method.getParameterTypes();
                        for (int i = 0; i < classes.length; i++) {
                            if (!classes[i].getName().endsWith(things[i])) return false;
                        }
                        return true;
                    });
                }
                if (methods.size() > 0) return methods.get(0);
                return null;
            }
        },
        FIELD(Pattern.compile("^(?<type>\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*)\\s+(?<name>\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*)\\s*(?=[=;])")) {
            @Override
            public AnnotatedElement find(List<AnnotatedElement> list) {
                final String type = matcher.group("type");
                final String name = matcher.group("name");
                for (final AnnotatedElement element : list) {
                    if (!(element instanceof Field field)) continue;
                    if (field.getName().equals(name)) return field;
                }
                return null;
            }
        },
        CONSTRUCTOR(Pattern.compile("hello"));
        
        public final Pattern pattern;
        
        protected Matcher matcher;
        
        TypeMatcher(Pattern pattern) {
            this.pattern = pattern;
        }
        
        static TypeMatcher matcher(StringReader reader) {
            final String string = reader.remainingString();
            for (final TypeMatcher value : values()) {
                if ((value.matcher = value.pattern.matcher(string)).find()) return value;
            }
            return null;
        }
        
        public AnnotatedElement find(List<AnnotatedElement> list) {
            return null;
        }
        
    }
    
}
