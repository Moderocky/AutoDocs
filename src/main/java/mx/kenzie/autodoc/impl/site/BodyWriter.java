package mx.kenzie.autodoc.impl.site;

import mx.kenzie.autodoc.api.controller.Element;
import mx.kenzie.autodoc.api.schema.WritableElement;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

public class BodyWriter implements WritableElement, Element, ElementWriter {
    
    protected final Class<?>[] classes;
    protected final String body;
    
    public BodyWriter(String body, Class<?>[] classes) {
        this.classes = classes;
        this.body = body;
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
        this.write(stream, "\n<section id=\"description\">");
        this.write(stream, """
            <div class="row mb-2">
            <div class="col col-lg-12 col-sm-12">""");
        this.startBlock(stream);
        this.write(stream, Utils.markDown(body));
        this.endBlock(stream);
        this.write(stream, "\n</div>");
        this.write(stream, "\n</div>");
        this.write(stream, "\n</section>");
    }
    
}