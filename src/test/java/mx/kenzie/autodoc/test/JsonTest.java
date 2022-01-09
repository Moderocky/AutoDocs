package mx.kenzie.autodoc.test;

import mx.kenzie.autodoc.api.controller.MethodElement;
import mx.kenzie.autodoc.impl.json.JsonSchema;
import org.junit.Test;

public class JsonTest {
    
    @Test
    public void jsonSimple() throws Throwable {
        final JsonSchema schema = new JsonSchema();
        final MethodElement element = new MethodElement(JsonTest.class.getMethod("jsonSimple")) {};
        schema.write(System.out, element);
        
    }
    
}
