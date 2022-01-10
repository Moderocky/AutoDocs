package mx.kenzie.autodoc.impl.site;

import mx.kenzie.autodoc.api.controller.Element;
import mx.kenzie.autodoc.api.schema.WritableElement;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

public class ClassIndexWriter implements WritableElement, Element, ElementWriter {
    
    protected final List<Class<?>> classes;
    protected final String namespace;
    
    public ClassIndexWriter(String namespace, List<Class<?>> classes) {
        this.classes = classes;
        this.namespace = namespace;
    }
    
    @Override
    public String name() {
        return "writer";
    }
    
    @Override
    public Map<String, Object> getDetails() {
        return null;
    }
    
    @Override
    public void write(OutputStream stream) throws IOException {
        this.write(stream, "\n<section id=\"list\">");
        this.write(stream, """
            <div class="row mb-2">
            <div class="col col-lg-12 col-sm-12">""");
        this.startBlock(stream);
        this.write(stream, "\n<h3 class=\"mb-0\">");
        this.write(stream, "Index of " + namespace);
        this.write(stream, "</h3>");
        this.writeType(stream);
        this.writeIndices(stream);
        this.write(stream, "\n</div>");
        this.write(stream, "\n<div class=\"col-lg-4 col-sm-12\">");
        // side bit
        this.write(stream, "\n</div>");
        this.write(stream, "\n</div>");
        this.endBlock(stream);
        this.write(stream, "\n</div>");
        this.write(stream, "\n</section>");
    }
    
    protected void writeIndices(OutputStream stream) throws IOException {
        for (final Class<?> type : classes) {
            this.write(stream, "<h5><a class=\"text-decoration-none text-dark\" href=\"" + Utils.getTopPath(namespace) + Utils.getFilePath(type) + "\"><span class=\"fa fa-link\"></span> " + type.getCanonicalName() + "</a></h5>");
            this.writeType(type, stream);
        }
    }
    protected void writeType(Class<?> target, OutputStream stream) throws IOException {
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
    
    protected void writeType(OutputStream stream) throws IOException {
        this.write(stream, "<p>");
        this.write(stream, "<strong class=\"d-inline-block mb-2 text-info\">Package</strong>");
        this.write(stream, "</p>");
    }
}
