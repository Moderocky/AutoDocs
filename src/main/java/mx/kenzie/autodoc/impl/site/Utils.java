package mx.kenzie.autodoc.impl.site;

import mx.kenzie.autodoc.api.note.Description;
import mx.kenzie.autodoc.api.note.Example;
import mx.kenzie.autodoc.api.note.Warning;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Modifier;

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
    
    static String getExamples(AnnotatedElement target) {
        final StringBuilder builder = new StringBuilder();
        builder.append("<div class=\"col col-lg-4 col-sm-12\">");
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
    
    private static String getWarning(Warning warning) {
        return "<div class=\"alert bg-danger text-light\">" +
            switch (warning.mode()) {
                case MARKDOWN -> Utils.markDown(warning.value());
                case HTML -> warning.value();
                case OTHER -> Utils.escapeHTML(warning.value());
            } + "</div>";
    }
    
}
