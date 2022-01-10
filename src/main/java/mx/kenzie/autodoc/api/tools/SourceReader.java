package mx.kenzie.autodoc.api.tools;

import mx.kenzie.autodoc.api.note.Description;
import mx.kenzie.autodoc.api.note.GenerateExample;
import mx.kenzie.autodoc.api.note.Ignore;
import mx.kenzie.autodoc.impl.site.PublicUtils;
import org.objectweb.asm.*;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

@Description("""
    This tool is for reading the bytecode of a class-file to gain special insights about elements.
    It is currently used only for scouring variable and parameter names, to make method documentation clearer.
    
    In the future, this will also be used to identify simple getter/setter methods for fields
    and link them accordingly.
    
    This may not be available in all distributions. ASM is not packaged by default.
    """)
@GenerateExample
public class SourceReader {
    
    @Description("""
        Scours the compiled source debug information for the pre-compile parameter names.
        
        While these are supposed to be available to the JVM,
        the class-file structure changed after Java 1.6 and the JVM implementation was never updated.
        
        This is a relatively slow operation.
        """)
    @GenerateExample
    public static String[] getParameterNames(final Constructor<?> method) {
        final String[] parameters = new String[method.getParameterCount()];
        final Class<?>[] types = method.getParameterTypes();
        if (!isAvailable()) return sanitiseParameters(parameters, types);
        final ClassReader reader = new ClassReader(getSource(method.getDeclaringClass()));
        final int offset = 1;
        reader.accept(new MethodFinder(parameters, method.getName(), Type.getConstructorDescriptor(method), offset), ClassReader.SKIP_FRAMES);
        return sanitiseParameters(parameters, types);
    }
    
    @Description("""
        Whether the source-reader tool is available.
        
        Most of the tool methods are designed to check this and
        return some default value before an exception is thrown,
        but it may be worth checking here before using the class at all to avoid an exception being thrown.
        """)
    @GenerateExample
    public static boolean isAvailable() {
        try {
            Class.forName("org.objectweb.asm.ClassReader");
            return true;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }
    
    private static String[] sanitiseParameters(String[] parameters, Class<?>[] types) {
        for (int i = 0; i < parameters.length; i++)
            if (parameters[i] == null) parameters[i] = PublicUtils.createVarName(types[i]);
        return parameters;
    }
    
    private static byte[] getSource(final Class<?> thing) {
        try (final InputStream stream = ClassLoader.getSystemResourceAsStream(Type.getInternalName(thing) + ".class")) {
            assert stream != null;
            return stream.readAllBytes();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    @Description("""
        Scours the compiled source debug information for the pre-compile parameter names.
        
        While these are supposed to be available to the JVM,
        the class-file structure changed after Java 1.6 and the JVM implementation was never updated.
        
        This is a relatively slow operation.
        """)
    @GenerateExample
    public static String[] getParameterNames(final Method method) {
        final String[] parameters = new String[method.getParameterCount()];
        final Class<?>[] types = method.getParameterTypes();
        if (!isAvailable()) return sanitiseParameters(parameters, types);
        final ClassReader reader = new ClassReader(getSource(method.getDeclaringClass()));
        final int offset;
        if (Modifier.isStatic(method.getModifiers())) offset = 0;
        else offset = 1;
        reader.accept(new MethodFinder(parameters, method.getName(), Type.getMethodDescriptor(method), offset), ClassReader.SKIP_FRAMES);
        return sanitiseParameters(parameters, types);
    }
    
    @Ignore
    private static class MethodFinder extends ClassVisitor {
        final String signature;
        final String name;
        final String[] variables;
        final int offset;
        
        public MethodFinder(final String[] variables, final String name, String signature, int offset) {
            super(Opcodes.ASM9);
            this.variables = variables;
            this.name = name;
            this.signature = signature;
            this.offset = offset;
        }
        
        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if (name.equals(this.name) && this.signature.equals(desc)) {
                return new MethodReader(Opcodes.ASM9, variables, offset);
            }
            return super.visitMethod(access, name, desc, signature, exceptions);
        }
    }
    
    @Ignore
    private static class MethodReader extends MethodVisitor {
        
        final String[] variables;
        final int offset;
        
        public MethodReader(int api, String[] variables, int offset) {
            super(api);
            this.variables = variables;
            this.offset = offset;
        }
        
        @Override
        public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
            super.visitLocalVariable(name, descriptor, signature, start, end, index);
            if (offset > 0 && index == 0) return;
            if (variables.length > (index - offset) && variables[index - offset] == null)
                variables[index - offset] = name;
        }
        
    }
    
}
