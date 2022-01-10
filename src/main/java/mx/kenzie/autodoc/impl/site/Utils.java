package mx.kenzie.autodoc.impl.site;

import mx.kenzie.autodoc.api.note.Description;
import mx.kenzie.autodoc.api.note.Example;
import mx.kenzie.autodoc.api.note.Ignore;
import mx.kenzie.autodoc.api.note.Warning;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.io.File;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Ignore
class Utils {
    
    static String hierarchyLabel(Class<?> type) {
        final StringBuilder builder = new StringBuilder();
        builder.append("<strong class=\"d-block text-");
        if (type.isAnnotation()) builder.append("warning");
        else if (type.isInterface()) builder.append("success");
        else if (type.isPrimitive()) builder.append("info");
        else builder.append("primary");
        builder.append("\">");
        builder.append(type.getSimpleName());
        builder.append("</strong>");
        return builder.toString();
    }
    
    static String createModifiers(final int modifiers) {
        final StringBuilder builder = new StringBuilder();
        if (Modifier.isPublic(modifiers))
            builder.append("<span class=\"badge bg-primary\">public</span> ");
        else if (Modifier.isPrivate(modifiers))
            builder.append("<span class=\"badge bg-primary\">private</span> ");
        else if (Modifier.isProtected(modifiers))
            builder.append("<span class=\"badge bg-primary\">protected</span> ");
        if (Modifier.isStatic(modifiers))
            builder.append("<span class=\"badge bg-info\">static</span> ");
        if (Modifier.isAbstract(modifiers))
            builder.append("<span class=\"badge bg-info\">abstract</span> ");
        if (Modifier.isFinal(modifiers))
            builder.append("<span class=\"badge bg-info\">final</span> ");
        if (Modifier.isStrict(modifiers))
            builder.append("<span class=\"badge bg-success\">strict</span> ");
        if (Modifier.isSynchronized(modifiers))
            builder.append("<span class=\"badge bg-success\">synchronized</span> ");
        if (Modifier.isVolatile(modifiers))
            builder.append("<span class=\"badge bg-success\">volatile</span> ");
        if (Modifier.isTransient(modifiers))
            builder.append("<span class=\"badge bg-danger\">transient</span> ");
        if (Modifier.isNative(modifiers))
            builder.append("<span class=\"badge bg-danger\">native</span> ");
        if ((modifiers & 0x00001000) != 0)
            builder.append("<span class=\"badge bg-danger\">synthetic</span> ");
        if ((modifiers & 0x00000040) != 0)
            builder.append("<span class=\"badge bg-danger\">bridge</span> ");
        return builder.toString();
    }
    
    static boolean ignore(AnnotatedElement target) {
        return target.isAnnotationPresent(Ignore.class);
    }
    
    static String getDescription(AnnotatedElement target) {
        if (!target.isAnnotationPresent(Description.class)) return "No description is available for this element.";
        final Description description = target.getDeclaredAnnotation(Description.class);
        if (description == null) return "No description is available for this element.";
        return switch (description.mode()) {
            case MARKDOWN -> Utils.markDown(description.value());
            case HTML -> description.value();
            case OTHER -> Utils.escapeHTML(description.value());
        };
    }
    
    static String markDown(String content) {
        final Parser parser = Parser.builder().build();
        final Node node = parser.parse(content);
        final HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(node);
    }
    
    static String escapeHTML(String string) {
        final StringBuilder builder = new StringBuilder(string.length());
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (c > 127 || c == '"' || c == '\'' || c == '<' || c == '>' || c == '&') {
                builder.append("&#");
                builder.append((int) c);
                builder.append(';');
            } else {
                builder.append(c);
            }
        }
        return builder.toString();
    }
    
    private static boolean isLong(Example example) {
        for (final String line : example.value().lines().toList()) {
            if (line.length() < 50) continue;
            return true;
        }
        return false;
    }
    
    static boolean hasLongExamples(AnnotatedElement target) {
        if (!target.isAnnotationPresent(Example.class) && !target.isAnnotationPresent(Example.Multiple.class)) return false;
        final Example example = target.getDeclaredAnnotation(Example.class);
        if (example != null) return isLong(example);
        final Example.Multiple multiple = target.getDeclaredAnnotation(Example.Multiple.class);
        if (multiple != null) {
            for (final Example sub : multiple.value()) {
                if (isLong(sub)) return true;
            }
        }
        return false;
    }
    
    static String getExamples(AnnotatedElement target) {
        final StringBuilder builder = new StringBuilder();
        if (hasLongExamples(target)) builder.append("<div class=\"col col-lg-6 col-sm-12\">");
        else builder.append("<div class=\"col col-lg-4 col-sm-12\">");
        final Example example = target.getDeclaredAnnotation(Example.class);
        if (example != null)
            builder.append(getExample(example));
        final Example.Multiple multiple = target.getDeclaredAnnotation(Example.Multiple.class);
        if (multiple != null) {
            for (final Example sub : multiple.value()) {
                builder.append(getExample(sub));
            }
        }
        builder.append("</div>");
        return builder.toString();
    }
    
    private static String getExample(Example example) {
        return "<div class=\"rounded bg-dark text-light\">" + markDown("```" + example.language() + "\n" + example.value() + "\n```") + "</div>";
    }
    
    static String getWarnings(AnnotatedElement target) {
        final StringBuilder builder = new StringBuilder();
        builder.append("<div class=\"row p-2\">");
        final Warning warning = target.getDeclaredAnnotation(Warning.class);
        if (warning != null)
            builder.append(getWarning(warning));
        final Warning.Multiple multiple = target.getDeclaredAnnotation(Warning.Multiple.class);
        if (multiple != null) {
            for (final Warning sub : multiple.value()) {
                builder.append(getWarning(sub));
            }
        }
        builder.append("</div>");
        return builder.toString();
    }
    
    static String getId(AnnotatedElement element) {
        if (element == null) return "";
        if (element instanceof Method method) {
            return "method:" + method.getName() + "(" + method.getParameterCount() + ")";
        } else if (element instanceof Field field) {
            return "field:" + field.getName();
        } else if (element instanceof Class<?> thing) {
            return "class:" + thing.getSimpleName();
        } else {
            return "unknown:" + element.hashCode();
        }
    }
    
    static List<Field> getFields(Class<?> type) {
        if (type == null) return new ArrayList<>();
        final List<Field> list = new ArrayList<>(Arrays.asList(type.getDeclaredFields()));
        list.removeIf(field -> Modifier.isPrivate(field.getModifiers()));
        list.removeIf(field -> field.isAnnotationPresent(Ignore.class));
        list.removeIf(Field::isSynthetic);
        return list;
    }
    
    static List<Method> getMethods(Class<?> type) {
        if (type == null) return new ArrayList<>();
        final List<Method> list = new ArrayList<>(Arrays.asList(type.getDeclaredMethods()));
        list.removeIf(method -> Modifier.isPrivate(method.getModifiers()));
        list.removeIf(method -> method.isAnnotationPresent(Ignore.class));
        return list;
    }
    
    private static String getWarning(Warning warning) {
        return "<div class=\"alert bg-danger text-light\">" +
            switch (warning.mode()) {
                case MARKDOWN -> Utils.markDown(warning.value());
                case HTML -> warning.value();
                case OTHER -> Utils.escapeHTML(warning.value());
            } + "</div>";
    }
    
    static String getTopURL(Class<?> from) {
        return getTopPath(from) + "index.html";
    }
    
    static String getTopPath(Class<?> from) {
        final StringBuilder builder = new StringBuilder();
        String string = from.getName();
        int index;
        while ((index = string.lastIndexOf('.')) > -1) {
            string = string.substring(0, index);
            builder.append("../");
        }
        return builder.toString();
    }
    
    static String getTopPath(String string) {
        final StringBuilder builder = new StringBuilder();
        int index;
        builder.append("../");
        while ((index = string.lastIndexOf('.')) > -1) {
            string = string.substring(0, index);
            builder.append("../");
        }
        return builder.toString();
    }
    
    static String getURL(Class<?> from, Class<?> to) {
        if (to.getPackageName().equals(from.getPackageName())) return getFileName(to);
        return getTopPath(from) + getFilePath(to);
    }
    
    static String getFilePath(Class<?> type) {
        return type.getName().replace('.', File.separatorChar) + ".html";
    }
    
    static String getFileName(Class<?> type) {
        final String name = type.getName();
        return name.substring(name.lastIndexOf('.')+1) + ".html";
    }
    
    static String getPrettyName(Class<?> type) {
        final String name = type.getName();
        return name.substring(name.lastIndexOf('.')+1).replace('$', '.');
    }
    
}
