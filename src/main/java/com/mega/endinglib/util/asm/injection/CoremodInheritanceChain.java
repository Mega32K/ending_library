package com.mega.endinglib.util.asm.injection;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.io.InputStream;

public class CoremodInheritanceChain {
    public final String start;
    public final String startBinary;
    @Nullable
    public final ClassNode startClassNode;
    public CoremodInheritanceChain(String start, @Nullable ClassNode startClassNode) {
        this.start = start.replace('.', '/');
        this.startBinary = start.replace('/', '.');
        this.startClassNode = startClassNode;
    }

    public final ObjectSet<String> inheritanceChain = new ObjectOpenHashSet<>();
    public final ObjectSet<String> rejected = new ObjectOpenHashSet<>();
    public boolean isSubOf(String toCheckParent) {
        toCheckParent = toCheckParent.replace('.', '/');
        if (inheritanceChain.contains(toCheckParent))
            return true;
        else if (rejected.contains(toCheckParent))
            return false;
        try {
            return checkAndCacheTargetClass(this.start, toCheckParent);
        } catch (IOException ioe) {
            rejected.add(toCheckParent);
            return false;
        }
    }
    private boolean checkAndCacheTargetClass(String nextSuper, String toCheckParent) throws IOException {
        if (nextSuper == null) {
            rejected.add(toCheckParent);
            return false;
        }
        inheritanceChain.add(nextSuper);
        if (toCheckParent == null || "java/lang/Object".equals(toCheckParent)) {
            rejected.add(toCheckParent);
            return false;
        }
        if (nextSuper.equals(toCheckParent)) {
            inheritanceChain.add(toCheckParent);
            return true;
        }
        try (InputStream is = getResourceAsStream(nextSuper + ".class")) {
            if (is == null) throw new IOException("Class not found");
            return checkAndCacheTargetClass(new SuperClassReader(is).getSimpleSuperName(), toCheckParent);
        }
    }
    private static InputStream getResourceAsStream(String path) {
        //maybe TransformingClassLoader
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        if (inputStream == null) {
            inputStream = CoremodInheritanceChain.class.getClassLoader().getResourceAsStream(path);
            if (inputStream == null) {
                inputStream = ClassLoader.getSystemResourceAsStream(path);
            }
        }
        return inputStream;
    }
    private static class SuperClassVisitor extends ClassVisitor {
        public String superName;
        public SuperClassVisitor() {
            super(Opcodes.ASM9);
        }
        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            this.superName = superName;
        }
    }
    private static class SuperClassReader extends ClassReader {
        public SuperClassReader(byte[] classFile) {
            super(classFile);
        }

        public SuperClassReader(byte[] classFileBuffer, int classFileOffset, int classFileLength) {
            super(classFileBuffer, classFileOffset, classFileLength);
        }

        public SuperClassReader(InputStream inputStream) throws IOException {
            super(inputStream);
        }

        public SuperClassReader(String className) throws IOException {
            super(className);
        }

        public String getSimpleSuperName() {
            char[] charBuffer = new char[getMaxStringLength()];
            int currentOffset = header;
            readUnsignedShort(currentOffset);
            readClass(currentOffset + 2, charBuffer);
            String s = readClass(currentOffset + 4, charBuffer);
            System.out.println(s);
            return s;
        }
    }
}
