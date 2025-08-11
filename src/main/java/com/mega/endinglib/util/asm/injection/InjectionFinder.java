package com.mega.endinglib.util.asm.injection;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

public class InjectionFinder {
    public static AbstractInsnNode tail(String desc, InsnList insnNodes) {
        AbstractInsnNode ret = null;

        // RETURN opcode varies based on return type, thus we calculate what opcode we're actually looking for by inspecting the target method
        int returnOpcode = Type.getReturnType(desc).getOpcode(Opcodes.IRETURN);

        for (AbstractInsnNode insn : insnNodes) {
            if (insn instanceof InsnNode && insn.getOpcode() == returnOpcode) {
                ret = insn;
            }
        }

        // WAT?
        if (ret == null) {
            throw new AssertionError("TAIL could not locate a valid RETURN in the target method!");
        }
        return ret;
    }

    public static AbstractInsnNode tail(MethodNode methodNode) {
        return tail(methodNode.desc, methodNode.instructions);
    }

    public static AbstractInsnNode head(InsnList insnNodes) {
        return insnNodes.getFirst();
    }

    public static AbstractInsnNode head(MethodNode methodNode) {
        return methodNode.instructions.getFirst();
    }

    public static void injectHead(MethodNode methodNode, InsnList insnNodes) {
        methodNode.instructions.insertBefore(head(methodNode), insnNodes);
    }

    public static void injectTail(MethodNode methodNode, InsnList insnNodes) {
        methodNode.instructions.insertBefore(tail(methodNode), insnNodes);
    }
}
