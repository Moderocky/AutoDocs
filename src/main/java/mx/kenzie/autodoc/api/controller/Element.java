package mx.kenzie.autodoc.api.controller;

import java.util.Map;

public interface Element {
    
    String name();
    
    Map<String, Object> getDetails();
    
}
