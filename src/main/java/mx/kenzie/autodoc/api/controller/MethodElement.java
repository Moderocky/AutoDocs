package mx.kenzie.autodoc.api.controller;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public abstract class MethodElement implements Element {
    
    protected final Method method;
    protected final Map<String, Object> details = new HashMap<>();
    
    public MethodElement(Method method) {
        this.method = method;
        if (method == null) return;
        details.put("name", method.getName());
        details.put("parameter_count", method.getParameterCount());
        details.put("synthetic", method.isSynthetic());
        details.put("static", Modifier.isStatic(method.getModifiers()));
        details.put("public", Modifier.isPublic(method.getModifiers()));
    }
    
    public MethodElement() {
        this.method = null;
    }
    
    @Override
    public String name() {
        return "Method";
    }
    
    @Override
    public Map<String, Object> getDetails() {
        return details;
    }
    
}
