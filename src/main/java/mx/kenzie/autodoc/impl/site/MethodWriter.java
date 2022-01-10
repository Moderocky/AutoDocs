package mx.kenzie.autodoc.impl.site;

import mx.kenzie.autodoc.api.controller.Element;
import mx.kenzie.autodoc.api.schema.WritableElement;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MethodWriter implements WritableElement, Element, ElementWriter {
    
    protected final Method target;
    
    public MethodWriter(Method target) {
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
        this.write(stream, "<span class=\"text-secondary\">" + this.getHeader());
        this.write(stream, "</span>");
        this.write(stream, "</h3>");
        this.write(stream, "<strong class=\"d-inline-block mb-2 text-primary\">Method</strong>");
        this.write(stream, Utils.getDescription(target));
        this.write(stream, "</div>");
        // side block
        this.write(stream, "<div class=\"col-md-4 d-none d-lg-block\">");
        new RightTextDetail("Return Type", Utils.hierarchyLabel(target.getReturnType()))
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
    
    private String getHeader() {
        final StringBuilder builder = new StringBuilder();
        builder.append(" (");
        final List<String> list = new ArrayList<>();
        for (final Class<?> type : target.getParameterTypes()) {
            list.add(type.getSimpleName());
        }
        builder.append(String.join(", ", list));
        builder.append(")");
        return builder.toString();
    }
    
}
