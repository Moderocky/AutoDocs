package mx.kenzie.autodoc.impl.site;

import mx.kenzie.autodoc.api.controller.Element;
import mx.kenzie.autodoc.api.note.Description;
import mx.kenzie.autodoc.api.schema.WritableElement;
import mx.kenzie.autodoc.internal.ScratchReader;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;

@Description("""
    Processes a class element page.
    """)
public class ClassWriter implements WritableElement, Element, ElementWriter {
    
    protected final Class<?> target;
    protected final Map<AnnotatedElement, String> javadocs;
    
    public ClassWriter(Class<?> target) {
        this.target = target;
        this.javadocs = new HashMap<>();
    }
    
    public ClassWriter(Class<?> target, File root) {
        this.target = target;
        this.javadocs = ScratchReader.find(target, root);
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
        this.createSection(target, stream);
        this.startBlock(stream);
        ((TitleArea) thing -> {
            this.write(stream, "\n<h3 class=\"mb-0\">");
            this.write(stream, this.createTitle(target));
            this.write(stream, "</h3>");
            this.writeType(stream);
        }).printTo(stream);
        this.startMainArea(stream);
        this.write(stream, Utils.getWarnings(target));
        this.write(stream, Utils.getDescription(target, javadocs));
        this.write(stream, "\n</div>");
        new RightBox() {
            @Override
            public void write(OutputStream stream) throws IOException {
                
                writeSupers(stream);
                new RightTextDetail("Modifiers", Utils.createModifiers(target.getModifiers(), false))
                    .printTo(stream);
            }
        }.printTo(stream);
        this.write(stream, "\n</div>");
        this.endBlock(stream);
        this.write(stream, Utils.getExamples(target));
        this.write(stream, "\n</div>");
        this.write(stream, "\n</section>");
        this.writeConstructors(stream);
        this.writeFields(stream);
        this.writeMethods(stream);
    }
    
    protected void writeSupers(OutputStream stream) throws IOException {
        final Class<?>[] interfaces = target.getInterfaces();
        if (target.getSuperclass() != null && !target.isInterface() && !target.isAnnotation()) {
            new RightTextDetail("Extends", Utils.hierarchyLabel(target.getSuperclass()))
                .printTo(stream);
        }
        if (interfaces.length > 0) {
            final StringBuilder builder = new StringBuilder();
            for (final Class<?> type : interfaces) {
                builder.append(Utils.hierarchyLabel(type));
            }
            new RightTextDetail("Implements", builder.toString())
                .printTo(stream);
        }
    }
    
    protected void writeFields(OutputStream stream) throws IOException {
        final Field[] fields = Utils.getFields(target).toArray(new Field[0]);
        if (fields.length > 0) {
            this.write(stream, "<h2 class=\"border-bottom pb-2 mb-0\">Fields</h2>");
            this.write(stream, "<br />");
            for (final Field field : fields) {
                final FieldWriter writer = new FieldWriter(field, javadocs);
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
                final MethodWriter writer = new MethodWriter(method, javadocs);
                writer.write(stream);
            }
            this.write(stream, "<br />");
        }
    }
    
    protected void writeConstructors(OutputStream stream) throws IOException {
        final Constructor<?>[] constructors = Utils.getConstructors(target).toArray(new Constructor[0]);
        if (constructors.length > 0) {
            this.write(stream, "<h2 class=\"border-bottom pb-2 mb-0\">Constructors</h2>");
            this.write(stream, "<br />");
            for (final Constructor<?> constructor : constructors) {
                final ConstructorWriter writer = new ConstructorWriter(constructor, javadocs);
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
            this.write(stream, "<span class=\"badge bg-warning text-dark\"" + Utils.toolTip("Marked as unsafe to use.") + ">Deprecated</span> ");
        if (target.isRecord())
            this.write(stream, "<strong class=\"d-inline-block mb-2 text-primary\"" + Utils.toolTip("A final, data-holding class.") + ">Record</strong>");
        else if (target.isEnum())
            this.write(stream, "<strong class=\"d-inline-block mb-2 text-primary\"" + Utils.toolTip("A set of 'flag' value fields.") + ">Enum Class</strong>");
        else if (target.isPrimitive())
            this.write(stream, "<strong class=\"d-inline-block mb-2 text-info\"" + Utils.toolTip("A raw data type with no methods.") + ">Primitive</strong>");
        else if (target.isAnnotation())
            this.write(stream, "<strong class=\"d-inline-block mb-2 text-warning\"" + Utils.toolTip("A tag to be placed on elements.") + ">Annotation</strong>");
        else if (target.isInterface())
            this.write(stream, "<strong class=\"d-inline-block mb-2 text-success\"" + Utils.toolTip("An abstract template to be implemented.") + ">Interface</strong>");
        else if (Modifier.isAbstract(target.getModifiers()))
            this.write(stream, "<strong class=\"d-inline-block mb-2 text-primary\"" + Utils.toolTip("An abstract class to be extended.") + ">Abstract Class</strong>");
        else
            this.write(stream, "<strong class=\"d-inline-block mb-2 text-primary\"" + Utils.toolTip("A regular class.") + ">Class</strong>");
        this.write(stream, "</p>");
    }
    
}
