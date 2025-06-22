package com.mega.endinglib.util;

import net.minecraft.world.item.Rarity;
import net.minecraftforge.common.IExtensibleEnum;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public enum MCMapping implements IExtensibleEnum {
    Util$METHOD$getMillis("getMillis", "m_137550_", "()J");
    public final String workspace;
    public final String normal;
    public final String desc;

    MCMapping(String workspace, String normal, String desc) {
        this.workspace = workspace;
        this.normal = normal;
        this.desc = desc;
    }
    public static MCMapping create(String name, String workspace, String normal, String desc) {
        throw new IllegalStateException("Enum not extended");
    }
    public String get() {
        return isWorkingspaceMode() ? workspace : normal;
    }
    public static int isWorkingspace = 0;
    public static boolean isWorkingspaceMode() {
        if (isWorkingspace == 0) {
            if (isDevelopmentEnvironment()) {
                isWorkingspace = 1;
            } else isWorkingspace = 2;
        }
        return isWorkingspace == 1;
    }
    public static boolean equalsFieldNode(FieldNode node, MCMapping o) {
        return o.get().equals(node.name) && o.desc.equals(node.desc);
    }
    public static boolean equalsFieldNode(FieldInsnNode node, MCMapping o) {
        return o.get().equals(node.name) && o.desc.equals(node.desc);
    }
    public static boolean equalsMethodNode(MethodNode node, MCMapping o) {
        return o.get().equals(node.name) && o.desc.equals(node.desc);
    }
    public static boolean equalsMethodNode(MethodInsnNode node, MCMapping o) {
        return o.get().equals(node.name) && o.desc.equals(node.desc);
    }
    public static boolean isDevelopmentEnvironment() {
        Path projectDir = Paths.get(System.getProperty("user.dir")).getParent();
        return Files.exists(projectDir.resolve(".gradle")) &&
                Files.exists(projectDir.resolve("build"));
    }
}
