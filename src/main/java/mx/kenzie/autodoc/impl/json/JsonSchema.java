package mx.kenzie.autodoc.impl.json;

import mx.kenzie.autodoc.api.controller.Element;
import mx.kenzie.autodoc.api.schema.OutputSchema;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class JsonSchema implements OutputSchema {
    @Override
    public String name() {
        return "JSON";
    }
    
    @Override
    public String description() {
        return "Converts documentation to a JSON format.";
    }
    
    @Override
    public boolean write(OutputStream target, Element element)
        throws IOException {
        target.write('{');
        boolean join = false;
        for (final Map.Entry<String, Object> entry : element.getDetails().entrySet()) {
            if (join) target.write(',');
            target.write('"');
            target.write(entry.getKey().getBytes(StandardCharsets.UTF_8));
            target.write("\": ".getBytes(StandardCharsets.UTF_8));
            if (entry.getValue() == null) {
                target.write("null".getBytes(StandardCharsets.UTF_8));
            } else if (entry.getValue() instanceof Number number) {
                target.write(number.toString().getBytes(StandardCharsets.UTF_8));
            } else if (entry.getValue() instanceof Boolean boo) {
                target.write(boo.toString().getBytes(StandardCharsets.UTF_8));
            } else if (entry.getValue() instanceof Element child) {
                if (!this.write(target, child)) target.write("null".getBytes(StandardCharsets.UTF_8));
            } else {
                target.write('"');
                target.write(entry.getValue().toString().getBytes(StandardCharsets.UTF_8));
                target.write('"');
            }
            join = true;
        }
        target.write('}');
        return join;
    }
}
