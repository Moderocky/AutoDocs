package mx.kenzie.autodoc.impl.site;

import mx.kenzie.autodoc.api.controller.Element;
import mx.kenzie.autodoc.api.schema.WritableElement;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.Map;

public class FieldWriter implements WritableElement, Element, ElementWriter {
    
    protected final Field target;
    
    public FieldWriter(Field target) {
        this.target = target;
    }
    
    @Override
    public String name() {
        return target.getName();
    }
    
    @Override
    public Map<String, Object> getDetails() {
        return null;
    }
    
    @Override
    public void write(OutputStream stream) throws IOException {
        if (Utils.ignore(target)) return;
        this.write(stream, "<section id=\"" + Utils.getId(target) + "\">");
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
        this.write(stream, "<h3 class=\"mb-0\">" + target.getName());
        this.write(stream, "</h3>");
        this.write(stream, "<strong class=\"d-inline-block mb-2 text-primary\">Field</strong>");
        this.write(stream, Utils.getDescription(target));
        this.write(stream, "</div>");
        // side block
        this.write(stream, "<div class=\"col-md-4 d-none d-lg-block\">");
        new RightTextDetail("Type", Utils.hierarchyLabel(target.getType()))
            .printTo(stream);
        new RightTextDetail("Modifiers", Utils.createModifiers(target.getModifiers()))
            .printTo(stream);
        this.write(stream, "</div>");
        // end side block
        this.write(stream, "</div>");
        this.endBlock(stream);
        this.write(stream, Utils.getExamples(target));
        this.write(stream, "</div>");
        this.write(stream, "</section>");
        
    }
    
}
