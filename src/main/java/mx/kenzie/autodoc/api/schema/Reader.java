package mx.kenzie.autodoc.api.schema;

import mx.kenzie.autodoc.api.controller.Element;

public interface Reader {
    
    Element read(Class<?> type);
    
}
