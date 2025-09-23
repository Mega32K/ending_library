package com.mega.endinglib.util.asm;

import com.mega.endinglib.coremod.forge.IClassProcessor;
import com.mega.endinglib.util.MCMapping;
import com.mega.endinglib.util.asm.injection.InjectionFinder;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.concurrent.atomic.AtomicBoolean;

public class NormalCoremodProcessor implements IClassProcessor {
    public static final NormalCoremodProcessor INSTANCE = new NormalCoremodProcessor();
    static final String EVENT_UTIL_CLASS = "com/mega/endinglib/util/asm/EventUtil";
    public static final String EVENT_CLASS = "net/minecraftforge/eventbus/api/Event";
    public static final String EVENT_FIELD$el_isUnCancelable = "el_isUnCancelable";
    public static final String EVENT_FIELD$el_isUnCancelable$desc = "Z";
    public static final int SCOREBOARD_MAX_DISPLAY_OBJECTIVE_COUNT_EXPAND = 16;

    @Override
    public void processClass(ILaunchPluginService.Phase phase, ClassNode classNode, Type classType, AtomicBoolean shouldWrite) {
        if (phase == ILaunchPluginService.Phase.AFTER) {
            String name = classNode.name;
            /*
            if (name.equals(SCOREBOARD_CLASS)) {
                classNode.methods.forEach(methodNode -> methodNode.instructions.forEach(insnNode -> {
                    if (insnNode instanceof IntInsnNode intInsn && intInsn.getOpcode() == Opcodes.BIPUSH) {
                        if (intInsn.operand == 19) {
                            InsnList list = new InsnList();
                            list.add(new IntInsnNode(Opcodes.BIPUSH, SCOREBOARD_MAX_DISPLAY_OBJECTIVE_COUNT_EXPAND));
                            list.add(new InsnNode(Opcodes.IADD));
                            methodNode.instructions.insert(intInsn, list);
                        } else if (intInsn.operand == 18) {
                            InsnList list = new InsnList();
                            list.add(new IntInsnNode(Opcodes.BIPUSH, SCOREBOARD_MAX_DISPLAY_OBJECTIVE_COUNT_EXPAND));
                            list.add(new InsnNode(Opcodes.IADD));
                            methodNode.instructions.insert(intInsn, list);
                        }
                    }
                }));
                classNode.fields.forEach(fieldNode -> {
                    if (MCMapping.equalsFieldNode(fieldNode, MCMapping.Scoreboard$FIELD$DISPLAY_SLOTS)) {
                        if (fieldNode.value instanceof Integer integer && integer.compareTo(19) == 0) {
                            fieldNode.value = integer + SCOREBOARD_MAX_DISPLAY_OBJECTIVE_COUNT_EXPAND;
                        }
                    } else if (MCMapping.equalsFieldNode(fieldNode, MCMapping.Scoreboard$FIELD$DISPLAY_SLOT_TEAMS_SIDEBAR_END)) {
                        if (fieldNode.value instanceof Integer integer && integer.compareTo(18) == 0) {
                            fieldNode.value = integer + SCOREBOARD_MAX_DISPLAY_OBJECTIVE_COUNT_EXPAND;
                        }
                    }
                });
                shouldWrite.set(true);
            } else
             */
            if ("com/mojang/blaze3d/font/GlyphInfo".equals(name)) {
                classNode.methods.forEach(methodNode -> {
                    if (MCMapping.GlyphInfo$METHOD$getBoldOffset.equalsMethodNode(methodNode)) {
                        InsnList insnNodes = new InsnList();
                        insnNodes.add(new LdcInsnNode(0.5F));
                        insnNodes.add(new InsnNode(Opcodes.FRETURN));
                        methodNode.instructions.insert(methodNode.instructions.get(0), insnNodes);
                        shouldWrite.set(true);
                    }
                });
            } else if ("net/minecraftforge/common/extensions/IForgeItemStack".equals(name)) {
                classNode.methods.forEach(methodNode -> {
                    if (methodNode.name.equals("canElytraFly") || methodNode.name.equals("elytraFlightTick")) {
                        methodNode.instructions.forEach(insnNode -> {
                            if (insnNode instanceof InsnNode node && node.getOpcode() == Opcodes.IRETURN) {
                                InsnList insnNodes = new InsnList();
                                LabelNode returnNode = new LabelNode();
                                insnNodes.add(new InsnNode(Opcodes.DUP));
                                insnNodes.add(new JumpInsnNode(Opcodes.IFNE, returnNode));
                                insnNodes.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                insnNodes.add(new MethodInsnNode(Opcodes.INVOKESTATIC, EVENT_UTIL_CLASS, "canElytraFly", "(Lnet/minecraftforge/common/extensions/IForgeItemStack;)Z", false));
                                insnNodes.add(new InsnNode(Opcodes.IRETURN));
                                insnNodes.add(returnNode);
                                methodNode.instructions.insertBefore(node, insnNodes);
                                shouldWrite.set(true);
                            }
                        });
                    } else if (methodNode.name.equals("getFoodProperties")) {
                        methodNode.instructions.forEach(insnNode -> {
                            if (insnNode instanceof InsnNode node && node.getOpcode() == Opcodes.ARETURN) {
                                InsnList insnNodes = new InsnList();
                                insnNodes.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                insnNodes.add(new MethodInsnNode(Opcodes.INVOKESTATIC, EVENT_UTIL_CLASS, "getFoodProperties", "(Lnet/minecraft/world/food/FoodProperties;Lnet/minecraftforge/common/extensions/IForgeItemStack;)Lnet/minecraft/world/food/FoodProperties;", false));
                                methodNode.instructions.insertBefore(node, insnNodes);
                                shouldWrite.set(true);
                            }
                        });
                    } else if (methodNode.name.equals("canPerformAction")) {
                        InsnList insnNodes = new InsnList();
                        insnNodes.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        insnNodes.add(new VarInsnNode(Opcodes.ALOAD, 1));
                        insnNodes.add(new MethodInsnNode(Opcodes.INVOKESTATIC, EVENT_UTIL_CLASS, "canPerformAction", "(Lnet/minecraftforge/common/extensions/IForgeItemStack;Lnet/minecraftforge/common/ToolAction;)Z", false));
                        LabelNode elseNode = new LabelNode();
                        insnNodes.add(new JumpInsnNode(Opcodes.IFNE, elseNode));
                        insnNodes.add(new InsnNode(Opcodes.ICONST_1));
                        insnNodes.add(new InsnNode(Opcodes.IRETURN));
                        insnNodes.add(elseNode);
                        methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), insnNodes);
                        shouldWrite.set(true);
                    } else if (methodNode.name.equals("canDisableShield")) {
                        methodNode.instructions.forEach(insnNode -> {
                            if (insnNode instanceof InsnNode node && node.getOpcode() == Opcodes.IRETURN) {
                                InsnList insnNodes = new InsnList();
                                insnNodes.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                insnNodes.add(new MethodInsnNode(Opcodes.INVOKESTATIC, EVENT_UTIL_CLASS, "canDisableShield", "(ZLnet/minecraftforge/common/extensions/IForgeItemStack;)Z", false));
                                methodNode.instructions.insertBefore(node, insnNodes);
                                shouldWrite.set(true);
                            }
                        });
                    } else if (methodNode.name.equals("getEnchantmentValue")) {
                        methodNode.instructions.forEach(insnNode -> {
                            if (insnNode instanceof InsnNode node && node.getOpcode() == Opcodes.IRETURN) {
                                InsnList insnNodes = new InsnList();
                                insnNodes.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                insnNodes.add(new MethodInsnNode(Opcodes.INVOKESTATIC, EVENT_UTIL_CLASS, "getEnchantmentValue", "(ILnet/minecraftforge/common/extensions/IForgeItemStack;)Z", false));
                                methodNode.instructions.insertBefore(node, insnNodes);
                                shouldWrite.set(true);
                            }
                        });
                    }
                });
            } else if ("net/minecraftforge/common/extensions/IForgeItem".equals(classNode.name)) {
                classNode.methods.forEach(methodNode -> {
                    if (methodNode.name.equals("getMaxStackSize")) {
                        methodNode.instructions.forEach(insnNode -> {
                            if (insnNode instanceof InsnNode node && node.getOpcode() == Opcodes.IRETURN) {
                                InsnList insnNodes = new InsnList();
                                insnNodes.add(new VarInsnNode(Opcodes.ALOAD, 1));
                                insnNodes.add(new MethodInsnNode(Opcodes.INVOKESTATIC, EVENT_UTIL_CLASS, "getMaxStackSize", "(ILnet/minecraft/world/item/ItemStack;)I", false));
                                methodNode.instructions.insertBefore(insnNode, insnNodes);
                                shouldWrite.set(true);
                            }
                        });
                    }
                });
            }
            if (classNode.superName.equals(EVENT_CLASS)) {
                classNode.interfaces.add("com/mega/endinglib/api/event/EventItf");
                classNode.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, EVENT_FIELD$el_isUnCancelable, EVENT_FIELD$el_isUnCancelable$desc, null, false));
                AtomicBoolean hasIsCanceled = new AtomicBoolean(false);
                classNode.methods.forEach(methodNode -> {
                    if ("isCanceled".equals(methodNode.name) && "()Z".equals(methodNode.desc)) {
                        InsnList insnNodes = new InsnList();
                        LabelNode elseLabel = new LabelNode();
                        insnNodes.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        insnNodes.add(new FieldInsnNode(Opcodes.GETFIELD, name, EVENT_FIELD$el_isUnCancelable, EVENT_FIELD$el_isUnCancelable$desc));
                        insnNodes.add(new JumpInsnNode(Opcodes.IFEQ, elseLabel));
                        insnNodes.add(new InsnNode(Opcodes.ICONST_0));
                        insnNodes.add(new InsnNode(Opcodes.IRETURN));
                        insnNodes.add(elseLabel);
                        InjectionFinder.injectHead(methodNode, insnNodes);
                        hasIsCanceled.set(true);
                    }
                });
                if (!hasIsCanceled.get()) {
                    MethodNode isCanceled = new MethodNode(Opcodes.ASM9, Opcodes.ACC_PUBLIC, "isCanceled", "()Z", null, null);
                    InsnList insnNodes = new InsnList();
                    LabelNode elseLabel = new LabelNode();
                    insnNodes.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    insnNodes.add(new FieldInsnNode(Opcodes.GETFIELD, name, EVENT_FIELD$el_isUnCancelable, EVENT_FIELD$el_isUnCancelable$desc));
                    insnNodes.add(new JumpInsnNode(Opcodes.IFEQ, elseLabel));
                    insnNodes.add(new InsnNode(Opcodes.ICONST_0));
                    insnNodes.add(new InsnNode(Opcodes.IRETURN));
                    insnNodes.add(elseLabel);
                    insnNodes.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    insnNodes.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, classNode.superName, "isCanceled", "()Z", false));
                    insnNodes.add(new InsnNode(Opcodes.IRETURN));
                    isCanceled.instructions.add(insnNodes);
                    isCanceled.maxLocals = 1;
                    isCanceled.maxStack = 3;
                    classNode.methods.add(isCanceled);
                }
                {
                    MethodNode el_setEventUnCancelable = new MethodNode(Opcodes.ASM9, Opcodes.ACC_PUBLIC, "el_setEventUnCancelable", "(Z)V", null, null);
                    InsnList insnNodes = new InsnList();
                    insnNodes.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    insnNodes.add(new VarInsnNode(Opcodes.ILOAD, 1));
                    insnNodes.add(new FieldInsnNode(Opcodes.PUTFIELD, name, EVENT_FIELD$el_isUnCancelable, EVENT_FIELD$el_isUnCancelable$desc));
                    insnNodes.add(new InsnNode(Opcodes.RETURN));
                    el_setEventUnCancelable.maxLocals = 2;
                    el_setEventUnCancelable.maxStack = 2;
                    el_setEventUnCancelable.instructions.add(insnNodes);
                    classNode.methods.add(el_setEventUnCancelable);
                }
                {
                    MethodNode el_isEventUnCancelable = new MethodNode(Opcodes.ASM9, Opcodes.ACC_PUBLIC, "el_isEventUnCancelable", "()Z", null, null);
                    InsnList insnNodes = new InsnList();
                    insnNodes.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    insnNodes.add(new FieldInsnNode(Opcodes.GETFIELD, name, EVENT_FIELD$el_isUnCancelable, EVENT_FIELD$el_isUnCancelable$desc));
                    insnNodes.add(new InsnNode(Opcodes.IRETURN));
                    el_isEventUnCancelable.maxLocals = 1;
                    el_isEventUnCancelable.maxStack = 1;
                    el_isEventUnCancelable.instructions.add(insnNodes);
                    classNode.methods.add(el_isEventUnCancelable);
                }
                shouldWrite.set(true);
            }
        }
    }
}
