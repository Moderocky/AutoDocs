package mx.kenzie.autodoc.impl.site;

import mx.kenzie.autodoc.api.controller.Element;
import mx.kenzie.autodoc.api.schema.WritableElement;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MethodWriter implements WritableElement, Element {
    
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
        this.write(stream, "<section id=\"method:" + this.target.getName() + "\">");
        this.write(stream, """
            <div class="row mb-2">
            <div class="col-md-8">
              <div class="row g-0 border rounded overflow-hidden flex-md-row mb-4 shadow-sm h-md-250 position-relative">
                <div class="col col-md-8 p-4 d-flex flex-column position-static">""");
        this.write(stream, "<h3 class=\"mb-0\">" + target.getName());
        this.write(stream, "<span class=\"text-secondary\">" + this.getHeader());
        this.write(stream, "</span>");
        this.write(stream, "</h3>");
        this.write(stream, "<strong class=\"d-inline-block mb-2 text-primary\">Method</strong>");
        this.write(stream, "<p class=\"card-text mb-auto\">");
        this.write(stream, Utils.getDescription(target));
        this.write(stream, "</p>");
        this.write(stream, "</div>");
        this.write(stream, "<div class=\"col-md-4 d-none d-lg-block\">");
        this.write(stream, "<div class=\"my-3 p-3 bg-body rounded shadow-sm\">");
        this.write(stream, "<h6 class=\"border-bottom pb-2 mb-0\">Return Type</h6>");
        this.write(stream, "<p class=\"pb-3 mb-0 small lh-sm\">");
        this.write(stream, Utils.hierarchyLabel(target.getReturnType()));
        this.write(stream, "</p>");
        this.write(stream, "</div>");
        this.write(stream, "<div class=\"my-3 p-3 bg-body rounded shadow-sm\">");
        this.write(stream, "<h6 class=\"border-bottom pb-2 mb-0\">Modifiers</h6>");
        this.write(stream, "<p class=\"pb-3 mb-0 small lh-sm\">");
        this.write(stream, Utils.createModifiers(target.getModifiers()));
        this.write(stream, "</p>");
        this.write(stream, "</div>");
        this.write(stream, "</div>");
        this.write(stream, "</div>");
        this.write(stream, "</div>");
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
