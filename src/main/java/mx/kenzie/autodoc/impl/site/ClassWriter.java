package mx.kenzie.autodoc.impl.site;

import mx.kenzie.autodoc.api.controller.Element;
import mx.kenzie.autodoc.api.schema.WritableElement;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

public class ClassWriter implements WritableElement, Element, ElementWriter {
    
    protected final Class<?> target;
    
    public ClassWriter(Class<?> target) {
        this.target = target;
    }
    
    @Override
    public String name() {
        return target.getSimpleName();
    }
    
    @Override
    public Map<String, Object> getDetails() {
        return null;
    }
    
    @Override
    public void write(OutputStream stream) throws IOException {
        if (Utils.ignore(target)) return;
        this.write(stream, "\n<section id=\"");
        this.write(stream, Utils.getId(target));
        this.write(stream, "\">");
        if (Utils.hasLongExamples(target)) {
            this.write(stream, """
            <div class="row mb-2">
            <div class="col col-lg-6 col-sm-12">""");
        } else {
            this.write(stream, """
            <div class="row mb-2">
            <div class="col col-lg-8 col-sm-12">""");
        }
        this.startBlock(stream);
        this.write(stream, "\n<h3 class=\"mb-0\">");
        this.write(stream, this.createTitle(target));
        this.write(stream, "</h3>");
        this.writeType(stream);
        this.write(stream, Utils.getWarnings(target));
        this.write(stream, Utils.getDescription(target));
        this.write(stream, "\n</div>");
        this.write(stream, "\n<div class=\"col-lg-4 col-sm-12\">");
        this.writeSupers(stream);
        new RightTextDetail("Modifiers", Utils.createModifiers(target.getModifiers()))
            .printTo(stream);
        this.write(stream, "\n</div>");
        this.write(stream, "\n</div>");
        this.endBlock(stream);
        this.write(stream, Utils.getExamples(target));
        this.write(stream, "\n</div>");
        this.write(stream, "\n</section>");
        this.writeFields(stream);
        this.writeMethods(stream);
    }
    
    protected void writeSupers(OutputStream stream) throws IOException {
        final Class<?>[] interfaces = target.getInterfaces();
        if (interfaces.length > 0 || target.getSuperclass() != null) {
            this.write(stream, "<div class=\"col-md-6 col-lg-12 my-3 p-3 bg-body rounded shadow-sm\">");
            if (!target.isInterface() && !target.isAnnotation()) {
                this.write(stream, "<h6 class=\"border-bottom pb-2 mb-0\">Extends</h6>");
                this.write(stream, "<p class=\"pb-3 mb-0 small lh-sm\">");
                this.write(stream, Utils.hierarchyLabel(target.getSuperclass()));
                this.write(stream, "</p>");
            }
            if (interfaces.length > 0) {
                this.write(stream, "<h6 class=\"border-bottom pb-2 mb-0\">Implements</h6>");
                for (final Class<?> type : interfaces) {
                    this.write(stream, "<p class=\"pb-3 mb-0 small lh-sm\">");
                    this.write(stream, Utils.hierarchyLabel(type));
                    this.write(stream, "</p>");
                }
            }
            this.write(stream, "</div>");
        }
    }
    
    protected void writeFields(OutputStream stream) throws IOException {
        final Field[] fields = Utils.getFields(target).toArray(new Field[0]);
        if (fields.length > 0) {
            this.write(stream, "<h2 class=\"border-bottom pb-2 mb-0\">Fields</h2>");
            this.write(stream, "<br />");
            for (final Field field : fields) {
                final FieldWriter writer = new FieldWriter(field);
                writer.write(stream);
            }
            this.write(stream, "<hr />");
            this.write(stream, "<br />");
        }
    }
    
    protected void writeMethods(OutputStream stream) throws IOException {
        final Method[] methods = Utils.getMethods(target).toArray(new Method[0]);
        if (methods.length > 0) {
            this.write(stream, "<h2 class=\"border-bottom pb-2 mb-0\">Methods</h2>");
            this.write(stream, "<br />");
            for (final Method method : methods) {
                final MethodWriter writer = new MethodWriter(method);
                writer.write(stream);
            }
            this.write(stream, "<br />");
        }
    }
    
    protected String createTitle(Class<?> target) {
        final StringBuilder builder = new StringBuilder();
        if (target.getDeclaringClass() != null)
            builder.append("<span class=\"text-secondary\">")
                .append(createTitle(target.getDeclaringClass()))
                .append(" { ")
                .append("</span>");
        builder.append(target.getSimpleName());
        return builder.toString();
    }
    
    protected void writeType(OutputStream stream) throws IOException {
        this.write(stream, "<p>");
        if (target.isAnnotationPresent(Deprecated.class))
            this.write(stream, "<span class=\"badge bg-warning text-dark\">Deprecated</span> ");
        if (target.isRecord()) this.write(stream, "<strong class=\"d-inline-block mb-2 text-primary\">Record</strong>");
        else if (target.isEnum())
            this.write(stream, "<strong class=\"d-inline-block mb-2 text-primary\">Enum</strong>");
        else if (target.isPrimitive())
            this.write(stream, "<strong class=\"d-inline-block mb-2 text-info\">Primitive</strong>");
        else if (target.isAnnotation())
            this.write(stream, "<strong class=\"d-inline-block mb-2 text-warning\">Annotation</strong>");
        else if (target.isInterface())
            this.write(stream, "<strong class=\"d-inline-block mb-2 text-success\">Interface</strong>");
        else if (Modifier.isAbstract(target.getModifiers()))
            this.write(stream, "<strong class=\"d-inline-block mb-2 text-primary\">Abstract Class</strong>");
        else this.write(stream, "<strong class=\"d-inline-block mb-2 text-primary\">Class</strong>");
        this.write(stream, "</p>");
    }
    
}
