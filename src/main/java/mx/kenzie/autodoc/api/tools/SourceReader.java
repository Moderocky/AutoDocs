package mx.kenzie.autodoc.api.tools;

import mx.kenzie.autodoc.api.note.Ignore;
import org.objectweb.asm.*;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

@Ignore
public class SourceReader {
    
    public static boolean isAvailable() {
        try {
            Class.forName("org.objectweb.asm.ClassReader");
            return true;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }
    
    public static String[] getParameterNames(final Method method) {
        final String[] parameters = new String[method.getParameterCount()];
        if (!isAvailable()) return parameters;
        final ClassReader reader = new ClassReader(getSource(method.getDeclaringClass()));
        final int offset;
        if (Modifier.isStatic(method.getModifiers())) offset = 0;
        else offset = 1;
        reader.accept(new MethodFinder(parameters, method.getName(), Type.getMethodDescriptor(method), offset), ClassReader.SKIP_FRAMES);
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
    
    static class MethodFinder extends ClassVisitor {
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
    
    static class MethodReader extends MethodVisitor {
        
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
            if (variables.length > (index - offset) && variables[index - offset] == null) variables[index - offset] = name;
        }
        
    }
    
}
