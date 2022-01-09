package mx.kenzie.autodoc.api.schema;

import mx.kenzie.autodoc.api.controller.Element;

@FunctionalInterface
public interface ConversionSchema<Record> {
    
    Element convert(Record record);
    
}
