package com.mega.endinglib.util.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.*;


public final class AsmMethodPrinter {

    private AsmMethodPrinter() {}

    public static String toJavaLikeMethod(ClassNode cn, MethodNode mn) {
        String ownerSimpleName = simpleNameFromInternal(cn.name);
        boolean ownerIsInterface = (cn.access & Opcodes.ACC_INTERFACE) != 0;
        boolean ownerIsAnnotation = (cn.access & Opcodes.ACC_ANNOTATION) != 0;

        StringBuilder sb = new StringBuilder();

        appendAnnotations(sb, mn.visibleAnnotations, "");
        appendAnnotations(sb, mn.invisibleAnnotations, "");

        String modifiers = buildMethodModifiers(mn.access, ownerIsInterface, ownerIsAnnotation, "<init>".equals(mn.name));
        if (!modifiers.isEmpty()) {
            sb.append(modifiers).append(' ');
        }

        if (mn.signature != null) {
            sb.append("/*signature=").append(mn.signature).append("*/ ");
        }

        Type mt = Type.getMethodType(mn.desc);
        Type ret = mt.getReturnType();
        Type[] args = mt.getArgumentTypes();

        boolean isCtor = "<init>".equals(mn.name);
        boolean isClinit = "<clinit>".equals(mn.name);

        if (isClinit) {
            sb.append("static");
        } else if (isCtor) {
            sb.append(ownerSimpleName)
                    .append('(')
                    .append(buildParams(cn, mn, args))
                    .append(')');
        } else {
            sb.append(toJavaType(ret))
                    .append(' ')
                    .append(mn.name)
                    .append('(')
                    .append(buildParams(cn, mn, args))
                    .append(')');
        }

        if (mn.exceptions != null && !mn.exceptions.isEmpty()) {
            StringJoiner tj = new StringJoiner(", ");
            for (String ex : mn.exceptions) {
                tj.add(ex.replace('/', '.'));
            }
            sb.append(" throws ").append(tj);
        }

        if (mn.annotationDefault != null) {
            sb.append(" default ").append(annotationValueToString(mn.annotationDefault));
        }

        sb.append(';');

        return sb.toString();
    }

    private static String buildMethodModifiers(int access, boolean ownerIsInterface, boolean ownerIsAnnotation, boolean isCtor) {
        List<String> out = new ArrayList<>();

        if ((access & Opcodes.ACC_PUBLIC) != 0) out.add("public");
        else if ((access & Opcodes.ACC_PROTECTED) != 0) out.add("protected");
        else if ((access & Opcodes.ACC_PRIVATE) != 0) out.add("private");

        boolean isAbstract = (access & Opcodes.ACC_ABSTRACT) != 0;
        boolean isStatic = (access & Opcodes.ACC_STATIC) != 0;
        boolean isFinal = (access & Opcodes.ACC_FINAL) != 0;

        if (!isCtor && isAbstract && !(ownerIsInterface || ownerIsAnnotation)) out.add("abstract");
        if (isStatic) out.add("static");
        if (isFinal) out.add("final");

        if ((access & Opcodes.ACC_SYNCHRONIZED) != 0) out.add("synchronized");
        if ((access & Opcodes.ACC_NATIVE) != 0) out.add("native");
        if ((access & Opcodes.ACC_STRICT) != 0) out.add("strictfp");

        if ((access & Opcodes.ACC_BRIDGE) != 0) out.add("/*bridge*/");
        if ((access & Opcodes.ACC_SYNTHETIC) != 0) out.add("/*synthetic*/");

        return String.join(" ", out);
    }

    private static String buildParams(ClassNode cn, MethodNode mn, Type[] argTypes) {
        boolean isStatic = (mn.access & Opcodes.ACC_STATIC) != 0;
        boolean isVarArgs = (mn.access & Opcodes.ACC_VARARGS) != 0;

        int slot = isStatic ? 0 : 1;
        Map<Integer, String> lvtNameBySlot = buildLvtParamNameMap(mn);

        StringJoiner sj = new StringJoiner(", ");
        for (int i = 0; i < argTypes.length; i++) {
            Type t = argTypes[i];
            String typeName = toJavaType(t);

            if (isVarArgs && i == argTypes.length - 1 && t.getSort() == Type.ARRAY) {
                Type elem = t.getElementType();
                typeName = toJavaType(elem) + "[]".repeat(Math.max(0, t.getDimensions() - 1)) + "...";
            }

            String pName = lvtNameBySlot.getOrDefault(slot, "arg" + i);
            sj.add(typeName + " " + pName);

            slot += t.getSize(); // long/double 占2槽
        }
        return sj.toString();
    }

    private static Map<Integer, String> buildLvtParamNameMap(MethodNode mn) {
        Map<Integer, String> map = new HashMap<>();
        if (mn.localVariables == null) return map;

        for (LocalVariableNode lv : mn.localVariables) {
            map.putIfAbsent(lv.index, lv.name);
        }
        return map;
    }

    private static void appendAnnotations(StringBuilder sb, List<AnnotationNode> anns, String prefix) {
        if (anns == null) return;
        for (AnnotationNode an : anns) {
            sb.append(prefix)
                    .append('@')
                    .append(descToSimpleName(an.desc, an.values))
                    .append('\n');
        }
    }

    private static String descToSimpleName(String desc, List<Object> values) {
        // Ljava/lang/Deprecated; -> Deprecated
        if (desc == null || desc.length() < 2) return String.valueOf(desc);
        String internal = desc.substring(1, desc.length() - 1);
        int i = internal.lastIndexOf('/');
        StringBuilder name = new StringBuilder(i >= 0 ? internal.substring(i + 1) : internal);
        if (!values.isEmpty()) {
            name.append("(");
            for (int j = 0;j < values.size();j++) {
                if ((j + 1) % 2 != 0) {
                    name.append(values.get(j).toString()).append(j < values.size()-1 ? "=" : ")");
                } else {
                    name.append(values.get(j).toString()).append(j < values.size()-1 ? ", " : ")");
                }
            }
        }
        return name.toString();
    }

    private static String toJavaType(Type t) {
        return switch (t.getSort()) {
            case Type.VOID -> "void";
            case Type.BOOLEAN -> "boolean";
            case Type.CHAR -> "char";
            case Type.BYTE -> "byte";
            case Type.SHORT -> "short";
            case Type.INT -> "int";
            case Type.FLOAT -> "float";
            case Type.LONG -> "long";
            case Type.DOUBLE -> "double";
            case Type.ARRAY -> toJavaType(t.getElementType()) + "[]".repeat(t.getDimensions());
            case Type.OBJECT -> t.getClassName();
            default -> t.toString();
        };
    }

    private static String annotationValueToString(Object v) {
        if (v == null) return "null";
        if (v instanceof String s) return "\"" + s + "\"";
        if (v instanceof Character c) return "'" + c + "'";
        return String.valueOf(v);
    }

    private static String simpleNameFromInternal(String internalName) {
        int slash = internalName.lastIndexOf('/');
        String s = slash >= 0 ? internalName.substring(slash + 1) : internalName;
        int dollar = s.lastIndexOf('$');
        return dollar >= 0 ? s.substring(dollar + 1) : s;
    }
}
