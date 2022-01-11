package mx.kenzie.autodoc.impl.site;

import mx.kenzie.autodoc.api.controller.Element;
import mx.kenzie.autodoc.api.schema.WritableElement;
import mx.kenzie.autodoc.api.tools.SourceReader;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
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
        this.createSection(target, stream);
        this.startBlock(stream);
        ((TitleArea) thing -> {
            this.write(stream, "<h3 class=\"mb-0\">" + target.getName());
            this.write(stream, "<span class=\"text-secondary\">" + this.getHeader());
            this.write(stream, "</span>");
            this.write(stream, "</h3>");
            if (target.getDeclaringClass().isAnnotation())
                this.write(stream, "<strong class=\"d-inline-block mb-2 text-primary\"" + Utils.toolTip("An annotation parameter.") + ">Input</strong>");
            else if (Modifier.isAbstract(target.getModifiers()))
                this.write(stream, "<strong class=\"d-inline-block mb-2 text-primary\"" + Utils.toolTip("A method that needs an implementation.") + ">Abstract Method</strong>");
            else
                this.write(stream, "<strong class=\"d-inline-block mb-2 text-primary\"" + Utils.toolTip("A callable code trigger.") + ">Method</strong>");
        }).printTo(stream);
        this.startMainArea(stream);
        this.write(stream, Utils.getDescription(target));
        // start params
        if (target.getParameterCount() > 0) {
            this.write(stream, "<div class=\"pt-2 col-sm-12\">");
            final String id = "params" + System.identityHashCode(target);
            this.write(stream, "<button class=\"d-inline btn btn-outline-primary\" type=\"button\" data-bs-toggle=\"collapse\" data-bs-target=\"#" + id + "\" aria-expanded=\"false\" aria-controls=\"" + id + "\">");
            this.write(stream, "Parameters</button>");
            this.write(stream, "<div class=\"collapse\" id=\"" + id + "\">");
            this.addParameterTable(stream);
            this.write(stream, "</div>");
            this.write(stream, "</div>");
        }
        // end params
        this.write(stream, "</div>");
        // side block
        new RightBox() {
            @Override
            public void write(OutputStream stream) throws IOException {
                new RightTextDetail("Return Type", Utils.hierarchyLabel(target.getReturnType()))
                    .printTo(stream);
                new RightTextDetail("Modifiers", Utils.createModifiers(target.getModifiers(), true))
                    .printTo(stream);
                if (target.getDeclaringClass().isAnnotation() && target.getDefaultValue() != null) {
                    new RightTextDetail("Default Value", "<code>" + target.getDefaultValue() + "</code>")
                        .printTo(stream);
                }
            }
        }.printTo(stream);
        // end side block
        this.write(stream, "</div>");
        this.endBlock(stream);
        this.write(stream, Utils.getExamples(target));
        this.write(stream, "</div>");
        this.write(stream, "</section>");
    }
    
    private String getHeader() {
        if (target.getDeclaringClass().isAnnotation()) return " = ...";
        final StringBuilder builder = new StringBuilder();
        builder.append(" (");
        final List<String> list = new ArrayList<>();
        for (final Class<?> type : target.getParameterTypes()) {
            list.add(type.getSimpleName());
        }
        builder.append(String.join(", ", list));
        if (target.isVarArgs()) {
            final String result = builder.toString();
            return result.substring(0, result.length() - 2) + "...)";
        } else {
            builder.append(")");
            return builder.toString();
        }
    }
    
    private void addParameterTable(OutputStream stream) throws IOException {
        if (target.getParameterCount() < 1) return;
        this.write(stream, "\n<table class=\"table table-borderless\">");
        this.write(stream, "\n<thead><tr>");
        this.write(stream, "<th scope=\"col\">Index</th>");
        this.write(stream, "<th scope=\"col\">Type</th>");
        this.write(stream, "<th scope=\"col\">Name</th>");
        this.write(stream, "\n</tr></thead>");
        this.write(stream, "\n<tbody>");
        int i = 0;
        final String[] names = SourceReader.getParameterNames(target);
        for (final Parameter parameter : target.getParameters()) {
            final String name = names[i];
            i++;
            this.write(stream, "\n<tr>");
            this.write(stream, "<th scope=\"row\">" + i + "</th>");
            this.write(stream, "<td>" + parameter.getType().getSimpleName() + "</td>");
            this.write(stream, "<td>" + (name != null ? name : Utils.createVarName(parameter.getType())) + "</td>");
            this.write(stream, "\n</tr>");
        }
        this.write(stream, "\n</tbody>");
        this.write(stream, "\n</table>");
    }
    
}
