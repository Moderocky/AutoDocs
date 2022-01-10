package mx.kenzie.autodoc.api.tools;

import mx.kenzie.autodoc.api.note.Description;
import mx.kenzie.autodoc.api.note.GenerateExample;
import mx.kenzie.autodoc.impl.site.PublicUtils;

import java.lang.reflect.*;
import java.util.concurrent.ThreadLocalRandom;

@Description("""
    This tool is able to generate examples for using elements.
    """)
@GenerateExample
public class ExampleGenerator {
    
    @Description("""
        The element for generation.
        """)
    @GenerateExample
    protected final AnnotatedElement element;
    @Description("""
        The current builder being written to.
        This is replaced before every synchronised [generate](#method:generateLong(0)) call.
        """)
    @GenerateExample
    protected volatile StringBuilder builder;
    
    @GenerateExample
    public ExampleGenerator(AnnotatedElement element) {
        this.element = element;
    }
    
    
    @Description("""
        Generates an example of the element being used.
        
        The result is not guaranteed to be valid (or even usable) but may
        provide some indication of how to use it.
        
        This will always produce a short 1-to-2 line example.
        """)
    @GenerateExample
    public synchronized String generateShort() {
        builder = new StringBuilder();
        builder.append("// Randomly-generated example").append(System.lineSeparator());
        if (element instanceof Method method) this.generateMethod(method, false);
        else if (element instanceof Field field) this.generateField(field, false);
        else if (element instanceof Constructor constructor) this.generateConstructor(constructor, false);
        return builder.toString();
    }
    
    @Description("""
        Generates an example of the element being used.
        
        The result is not guaranteed to be valid (or even usable) but may
        provide some indication of how to use it.
        
        The long example will try and include some uses of the result, such as
        storing and using the result of a method call, or creating a new instance
        with a constructor and then calling some methods from the class.
        """)
    @GenerateExample
    public synchronized String generateLong() {
        builder = new StringBuilder();
        builder.append("// Randomly-generated example").append(System.lineSeparator());
        if (element instanceof Method method) this.generateMethod(method, true);
        else if (element instanceof Field field) this.generateField(field, true);
        else if (element instanceof Constructor constructor) this.generateConstructor(constructor, true);
        return builder.toString();
    }
    
    @Description("""
        Generates an example field access and uses the result in some way.
        All of this is generated based on what is available for the field.
        """)
    @GenerateExample
    protected void generateConstructor(Constructor<?> constructor, boolean extras) {
        final Class<?> type = constructor.getDeclaringClass();
        final String variable;
        this.builder.append("final ")
            .append(this.getTypeName(type))
            .append(" ")
            .append(variable = PublicUtils.createVarName(type))
            .append(" = ");
        this.writeConstructorUse(constructor);
        this.builder.append(';');
        if (!extras) return;
        int count = 0;
        for (final Method second : type.getDeclaredMethods()) {
            if (second.isBridge() || second.isSynthetic()) continue;
            if (!Modifier.isPublic(second.getModifiers())) continue;
            final Class<?> thing = second.getReturnType();
            builder.append(System.lineSeparator());
            if (thing.isPrimitive() || thing == Boolean.class)
                builder.append("assert ");
            builder.append(variable);
            this.writeMethodUse(second);
            if (thing.isPrimitive() && thing != boolean.class) {
                builder.append(" == ");
                this.writePrimitiveUse(thing);
            }
            builder.append(';');
            count++;
            if (count >= 3) break;
        }
    }
    
    @Description("""
        Generates an example field access and uses the result in some way.
        All of this is generated based on what is available for the field.
        """)
    @GenerateExample
    protected void generateField(Field field, boolean extras) {
        final Class<?> type = field.getType();
        final String variable;
        if (!Modifier.isFinal(field.getModifiers())) {
            this.writeFieldUse(field);
            this.builder.append(" = ");
            this.writeObjectUse(type);
            this.builder.append(';');
            this.builder.append(System.lineSeparator());
        }
        if (type.isPrimitive()) builder.append(variable = "assert ");
        else builder.append("final ")
            .append(this.getTypeName(type))
            .append(" ")
            .append(variable = PublicUtils.createVarName(type))
            .append(" = ");
        this.writeFieldUse(field);
        if (type.isPrimitive() && type != boolean.class) {
            builder.append(" == ");
            this.writePrimitiveUse(type);
        }
        builder.append(';');
        if (type.isPrimitive() || !extras) return;
        int count = 0;
        for (final Method second : type.getDeclaredMethods()) {
            if (second.isBridge() || second.isSynthetic()) continue;
            if (!Modifier.isPublic(second.getModifiers())) continue;
            final Class<?> thing = second.getReturnType();
            builder.append(System.lineSeparator());
            if (thing.isPrimitive() || thing == Boolean.class)
                builder.append("assert ");
            builder.append(variable);
            this.writeMethodUse(second);
            if (thing.isPrimitive() && thing != boolean.class) {
                builder.append(" == ");
                this.writePrimitiveUse(thing);
            }
            builder.append(';');
            count++;
            if (count >= 3) break;
        }
    }
    
    private void writeConstructorUse(Constructor<?> constructor) {
        final Class<?> type = constructor.getDeclaringClass();
        this.builder.append("new ");
        this.builder.append(this.getTypeName(type));
        this.builder.append('(');
        if (constructor.getParameterCount() > 0) {
            final String[] names = SourceReader.getParameterNames(constructor);
            this.builder.append(String.join(", ", names));
        }
        this.builder.append(')');
        
    }
    
    private void writeFieldUse(Field field) {
        final Class<?> source = field.getDeclaringClass();
        if (Modifier.isStatic(field.getModifiers())) builder.append(this.getTypeName(source));
        else builder.append(PublicUtils.createVarName(source));
        this.builder.append('.').append(field.getName());
    }
    
    @Description("""
        Generates an example method call and uses the result in some way.
        All of this is generated based on what is available for the method.
        """)
    @GenerateExample
    protected void generateMethod(Method method, boolean extras) {
        final Class<?> result = method.getReturnType();
        final Class<?> source = method.getDeclaringClass();
        final boolean use = result != void.class && result != Void.class;
        final String variable;
        if (result == boolean.class || result == Boolean.class) {
            builder.append("assert ");
            variable = "null";
        } else if (use) {
            builder.append("final ")
                .append(this.getTypeName(method.getReturnType()))
                .append(" ")
                .append(variable = PublicUtils.createVarName(method.getReturnType()))
                .append(" = ");
        } else variable = "null";
        if (Modifier.isStatic(method.getModifiers()))
            builder.append(this.getTypeName(source));
        else builder.append(PublicUtils.createVarName(source));
        this.writeMethodUse(method);
        builder.append(';');
        if (!use || result.isPrimitive() || !extras) return;
        int count = 0;
        for (final Method second : result.getDeclaredMethods()) {
            if (second.isBridge() || second.isSynthetic()) continue;
            if (!Modifier.isPublic(second.getModifiers())) continue;
            final Class<?> thing = second.getReturnType();
            builder.append(System.lineSeparator());
            if (thing.isPrimitive() || thing == Boolean.class)
                builder.append("assert ");
            builder.append(variable);
            this.writeMethodUse(second);
            if (thing.isPrimitive() && thing != boolean.class) {
                builder.append(" == ");
                this.writePrimitiveUse(thing);
            }
            builder.append(';');
            count++;
            if (count >= 3) break;
        }
    }
    
    private void writeMethodUse(Method method) {
        builder.append('.').append(method.getName()).append('(');
        if (method.getParameterCount() > 0) {
            final String[] names = SourceReader.getParameterNames(method);
            builder.append(String.join(", ", names));
        }
        builder.append(')');
    }
    
    @Description("""
        Gets the usable name of this type in source code.
        
        For example, `java.lang.String` would be converted to `String`
        and `my.thing.MyClass$Nested` would be converted to `Nested`.
        """)
    @GenerateExample
    protected String getTypeName(Class<?> type) {
        final String name = type.getCanonicalName();
        if (name.indexOf('.') < 0) return name;
        return name.substring(name.lastIndexOf('.') + 1);
    }
    
    private void writeObjectUse(Class<?> type) {
        if (type.isPrimitive()) {
            this.writePrimitiveUse(type);
            return;
        } else if (type == String.class) {
            this.builder.append("\"hello\"");
            return;
        }
        final Constructor<?>[] constructors = type.getDeclaredConstructors();
        for (final Constructor<?> constructor : constructors) {
            if (!Modifier.isPublic(constructor.getModifiers())) return;
            this.writeConstructorUse(constructor);
            return;
        }
        System.out.println(type.getSimpleName()); // todo
        this.builder.append(PublicUtils.createVarName(type));
    }
    
    private void writePrimitiveUse(Class<?> type) {
        if (!type.isPrimitive()) {
            this.writeObjectUse(type);
            return;
        }
        if (type == int.class || type == byte.class || type == short.class || type == long.class)
            builder.append(ThreadLocalRandom.current().nextInt(3, 92));
        else if (type == float.class || type == double.class)
            builder.append(ThreadLocalRandom.current().nextFloat(3, 92));
        else if (type == char.class) builder.append('c');
        else if (type == boolean.class) builder.append(false);
        else {
            System.out.println(type.getSimpleName()); // todo
            builder.append("null");
        }
    }
    
}
