package com.mega.endinglib.util;

import net.minecraftforge.common.IExtensibleEnum;
import net.minecraftforge.fml.loading.FMLLoader;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public enum MCMapping implements IExtensibleEnum {
    Entity$METHOD$isPickable("isPickable", "m_6087_", "()Z"),
    Options$FIELD$keyMappings("keyMappings", "f_92059_", "[Lnet/minecraft/client/KeyMapping;"),
    Entity$METHOD$isPushable("isPushable", "m_6094_", "()Z"),
    Entity$METHOD$canBeCollidedWith("canBeCollidedWith", "m_5829_", "()Z"),
    AbstractArrow$FIELD$pickup("pickup", "f_36705_", "Lnet/minecraft/world/entity/projectile/AbstractArrow$Pickup;"),
    Entity$METHOD$makeBoundingBox("makeBoundingBox", "m_142242_", "()Lnet/minecraft/world/phys/AABB;"),
    EntityRenderer$METHOD$getTextureLocation("getTextureLocation", "m_5478_", "(Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/resources/ResourceLocation;"),
    Entity$METHOD$getDimensions("getDimensions", "m_6972_", "(Lnet/minecraft/world/entity/Pose;)Lnet/minecraft/world/entity/EntityDimensions;"),
    EntityDimensions$METHOD$scale("scale", "m_20388_", "(F)Lnet/minecraft/world/entity/EntityDimensions;"),
    LivingEntity$METHOD$getScale("getScale", "m_6134_", "()F"),
    Inventory$FIELD$selected("selected", "f_35977_", "I"),
    KeyMapping$METHOD$consumeClick("consumeClick", "m_90859_", "()Z"),
    Options$FIELD$keyHotbarSlots("keyHotbarSlots", "f_92056_", "[Lnet/minecraft/client/KeyMapping;"),
    Minecraft$FIELD$options("options", "f_91066_", "Lnet/minecraft/client/Options;"),
    Minecraft$METHOD$handleKeybinds("handleKeybinds", "m_91279_", "()V"),
    AbstractContainerMenu$METHOD$quickMoveStack("quickMoveStack", "m_7648_", "(Lnet/minecraft/world/entity/player/Player;I)Lnet/minecraft/world/item/ItemStack;"),
    Inventory$METHOD$hurtArmor("hurtArmor", "m_150072_", "(Lnet/minecraft/world/damagesource/DamageSource;F[I)V"),
    Equipable$METHOD$get("get", "m_269088_", "(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/Equipable;"),
    Scoreboard$FIELD$DISPLAY_SLOT_TEAMS_SIDEBAR_END("DISPLAY_SLOT_TEAMS_SIDEBAR_END", "f_166091_", "I"),
    Scoreboard$FIELD$DISPLAY_SLOTS("DISPLAY_SLOTS", "f_166092_", "I"),
    GlyphInfo$METHOD$getBoldOffset("getBoldOffset", "getBoldOffset", "()F"),
    Util$METHOD$getMillis("getMillis", "m_137550_", "()J");
    public static int isWorkingspace = 0;
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

    public static boolean isWorkingspaceMode() {
        if (isWorkingspace == 0) {
            if (isDevelopmentEnvironment()) {
                isWorkingspace = 1;
            } else isWorkingspace = 2;
        }
        return isWorkingspace == 1;
    }


    public boolean equalsFieldNode(FieldNode node) {
        return this.get().equals(node.name) && this.desc.equals(node.desc);
    }

    public boolean equalsFieldNode(FieldInsnNode node) {
        return this.get().equals(node.name) && this.desc.equals(node.desc);
    }

    public boolean equalsMethodNode(MethodNode node) {
        return this.get().equals(node.name) && this.desc.equals(node.desc);
    }

    public boolean equalsMethodNode(MethodInsnNode node) {
        return this.get().equals(node.name) && this.desc.equals(node.desc);
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
    public FieldInsnNode makeFIN(int opcode, String owner) {
        return new FieldInsnNode(opcode, owner, this.get(), this.desc);
    }
    public MethodInsnNode makeMIN(int opcode, String owner) {
        return new MethodInsnNode(opcode, owner, this.get(), this.desc);
    }
    public static boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    public String get() {
        return isWorkingspaceMode() ? workspace : normal;
    }
}
