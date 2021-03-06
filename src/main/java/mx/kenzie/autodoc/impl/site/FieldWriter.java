package mx.kenzie.autodoc.impl.site;

import mx.kenzie.autodoc.api.controller.Element;
import mx.kenzie.autodoc.api.schema.WritableElement;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

public class FieldWriter implements WritableElement, Element, ElementWriter {
    
    protected final Field target;
    protected final Map<AnnotatedElement, String> javadocs;
    
    public FieldWriter(Field target, Map<AnnotatedElement, String> javadocs) {
        this.target = target;
        this.javadocs = javadocs;
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
        this.createSection(target, stream);
        this.startBlock(stream);
        ((TitleArea) thing -> {
            this.write(stream, "<h3 class=\"mb-0\">");
            this.write(stream, target.getName());
            this.write(stream, "</h3>");
            if (target.isEnumConstant()) {
                this.write(stream, "<strong class=\"d-inline-block mb-2 text-primary\"" + Utils.toolTip("A flag.") + ">Enum</strong>");
            } else if (Modifier.isStatic(target.getModifiers()) && Modifier.isFinal(target.getModifiers())) {
                this.write(stream, "<strong class=\"d-inline-block mb-2 text-primary\"" + Utils.toolTip("An immutable value-holding member.") + ">Constant</strong>");
            } else {
                this.write(stream, "<strong class=\"d-inline-block mb-2 text-primary\"" + Utils.toolTip("A value-holding member.") + ">Field</strong>");
            }
        }).printTo(stream);
        this.startMainArea(stream);
        this.write(stream, Utils.getDescription(target, javadocs));
        this.write(stream, "</div>");
        // side block
        new RightBox() {
            @Override
            public void write(OutputStream stream) throws IOException {
                new RightTextDetail("Type", Utils.hierarchyLabel(target.getType()))
                    .printTo(stream);
                new RightTextDetail("Modifiers", Utils.createModifiers(target.getModifiers(), false))
                    .printTo(stream);
            }
        }.printTo(stream);
        // end side block
        this.write(stream, "</div>");
        this.endBlock(stream);
        this.write(stream, Utils.getExamples(target));
        this.write(stream, "</div>");
        this.write(stream, "</section>");
        
    }
    
}
