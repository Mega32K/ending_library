package com.mega.endinglib.util.asm;

import com.mega.endinglib.coremod.forge.IClassProcessor;
import com.mega.endinglib.util.MCMapping;
import com.mega.endinglib.util.asm.injection.InjectionFinder;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class NormalCoremodProcessor implements IClassProcessor {
    public static final NormalCoremodProcessor INSTANCE = new NormalCoremodProcessor();
    static final String EVENT_UTIL_CLASS = "com/mega/endinglib/util/asm/EventUtil";
    static final String CLIENT_EVENT_UTIL_CLASS = "com/mega/endinglib/util/asm/ClientEventUtil";
    static final String EVENT_CLASS = "net/minecraftforge/eventbus/api/Event";
    static final String MINECRAFT_CLASS = "net/minecraft/client/Minecraft";
    static final String OPTIONS_CLASS = "net/minecraft/client/Options";
    public static String KEYMAPPING_CLASS = "net/minecraft/client/KeyMapping";
    public static final String EVENT_FIELD$el_isUnCancelable = "el_isUnCancelable";
    public static final String EVENT_FIELD$el_isUnCancelable$desc = "Z";
    public static final int SCOREBOARD_MAX_DISPLAY_OBJECTIVE_COUNT_EXPAND = 16;

    @Override
    public void processClass(ILaunchPluginService.Phase phase, ClassNode classNode, Type classType, AtomicBoolean shouldWrite) {
        if (phase == ILaunchPluginService.Phase.AFTER) {
            String name = classNode.name;
            if (isUnsupportModifyingClass(name))
                return;
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
                    switch (methodNode.name) {
                        case "canElytraFly", "elytraFlightTick" -> methodNode.instructions.forEach(insnNode -> {
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
                        case "getFoodProperties" -> methodNode.instructions.forEach(insnNode -> {
                            if (insnNode instanceof InsnNode node && node.getOpcode() == Opcodes.ARETURN) {
                                InsnList insnNodes = new InsnList();
                                insnNodes.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                insnNodes.add(new MethodInsnNode(Opcodes.INVOKESTATIC, EVENT_UTIL_CLASS, "getFoodProperties", "(Lnet/minecraft/world/food/FoodProperties;Lnet/minecraftforge/common/extensions/IForgeItemStack;)Lnet/minecraft/world/food/FoodProperties;", false));
                                methodNode.instructions.insertBefore(node, insnNodes);
                                shouldWrite.set(true);
                            }
                        });
                        case "canPerformAction" -> {
                            InsnList insnNodes = new InsnList();
                            insnNodes.add(new VarInsnNode(Opcodes.ALOAD, 0));
                            insnNodes.add(new VarInsnNode(Opcodes.ALOAD, 1));
                            insnNodes.add(new MethodInsnNode(Opcodes.INVOKESTATIC, EVENT_UTIL_CLASS, "componentCanPerformAction", "(Lnet/minecraftforge/common/extensions/IForgeItemStack;Lnet/minecraftforge/common/ToolAction;)Z", false));
                            LabelNode elseNode = new LabelNode();
                            insnNodes.add(new JumpInsnNode(Opcodes.IFEQ, elseNode));
                            insnNodes.add(new InsnNode(Opcodes.ICONST_1));
                            insnNodes.add(new InsnNode(Opcodes.IRETURN));
                            insnNodes.add(elseNode);
                            methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), insnNodes);
                            shouldWrite.set(true);
                        }
                        case "canDisableShield" -> methodNode.instructions.forEach(insnNode -> {
                            if (insnNode instanceof InsnNode node && node.getOpcode() == Opcodes.IRETURN) {
                                InsnList insnNodes = new InsnList();
                                insnNodes.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                insnNodes.add(new MethodInsnNode(Opcodes.INVOKESTATIC, EVENT_UTIL_CLASS, "canDisableShield", "(ZLnet/minecraftforge/common/extensions/IForgeItemStack;)Z", false));
                                methodNode.instructions.insertBefore(node, insnNodes);
                                shouldWrite.set(true);
                            }
                        });
                        case "getEnchantmentValue" -> methodNode.instructions.forEach(insnNode -> {
                            if (insnNode instanceof InsnNode node && node.getOpcode() == Opcodes.IRETURN) {
                                InsnList insnNodes = new InsnList();
                                insnNodes.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                insnNodes.add(new MethodInsnNode(Opcodes.INVOKESTATIC, EVENT_UTIL_CLASS, "getEnchantmentValue", "(ILnet/minecraftforge/common/extensions/IForgeItemStack;)I", false));
                                methodNode.instructions.insertBefore(node, insnNodes);
                                shouldWrite.set(true);
                            }
                        });
                        case "canEquip" -> methodNode.instructions.forEach(insnNode -> {
                            if (insnNode instanceof InsnNode node && node.getOpcode() == Opcodes.IRETURN) {
                                InsnList insnNodes = new InsnList();
                                insnNodes.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                insnNodes.add(new VarInsnNode(Opcodes.ALOAD, 1));
                                insnNodes.add(new VarInsnNode(Opcodes.ALOAD, 2));
                                insnNodes.add(new MethodInsnNode(Opcodes.INVOKESTATIC, EVENT_UTIL_CLASS, "canEquip", "(ZLnet/minecraftforge/common/extensions/IForgeItemStack;Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/entity/Entity;)Z", false));
                                methodNode.instructions.insertBefore(node, insnNodes);
                                shouldWrite.set(true);
                            }
                        });
                    }
                });
            } else if ("net/minecraftforge/common/extensions/IForgeItem".equals(classNode.name)) {
                classNode.methods.forEach(methodNode -> {
                    switch (methodNode.name) {
                        case "getMaxStackSize" -> methodNode.instructions.forEach(insnNode -> {
                            if (insnNode instanceof InsnNode node && node.getOpcode() == Opcodes.IRETURN) {
                                InsnList insnNodes = new InsnList();
                                insnNodes.add(new VarInsnNode(Opcodes.ALOAD, 1));
                                insnNodes.add(new MethodInsnNode(Opcodes.INVOKESTATIC, EVENT_UTIL_CLASS, "getMaxStackSize", "(ILnet/minecraft/world/item/ItemStack;)I", false));
                                methodNode.instructions.insertBefore(insnNode, insnNodes);
                                shouldWrite.set(true);
                            }
                        });
                        case "getArmorTexture" -> {
                            InsnList insnNodes = new InsnList();
                            insnNodes.add(new VarInsnNode(Opcodes.ALOAD, 1));
                            insnNodes.add(new VarInsnNode(Opcodes.ALOAD, 3));
                            insnNodes.add(new VarInsnNode(Opcodes.ALOAD, 4));
                            insnNodes.add(new MethodInsnNode(Opcodes.INVOKESTATIC, EVENT_UTIL_CLASS, "componentArmorTexture", "(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlot;Ljava/lang/String;)Ljava/lang/String;", false));
                            insnNodes.add(new InsnNode(Opcodes.ARETURN));
                            InjectionFinder.injectHead(methodNode, insnNodes);
                            shouldWrite.set(true);
                        }
                        case "getMaxDamage" -> methodNode.instructions.forEach(insnNode -> {
                            if (insnNode instanceof InsnNode node && node.getOpcode() == Opcodes.IRETURN) {
                                InsnList insnNodes = new InsnList();
                                insnNodes.add(new VarInsnNode(Opcodes.ALOAD, 1));
                                insnNodes.add(new MethodInsnNode(Opcodes.INVOKESTATIC, EVENT_UTIL_CLASS, "getComponentMaxDamage", "(ILnet/minecraft/world/item/ItemStack;)I", false));
                                methodNode.instructions.insertBefore(insnNode, insnNodes);
                                shouldWrite.set(true);
                            }
                        });
                        case "isDamageable" -> methodNode.instructions.forEach(insnNode -> {
                            if (insnNode instanceof InsnNode node && node.getOpcode() == Opcodes.IRETURN) {
                                InsnList insnNodes = new InsnList();
                                insnNodes.add(new VarInsnNode(Opcodes.ALOAD, 1));
                                insnNodes.add(new MethodInsnNode(Opcodes.INVOKESTATIC, EVENT_UTIL_CLASS, "isComponentItemDamageable", "(ZLnet/minecraft/world/item/ItemStack;)Z", false));
                                methodNode.instructions.insertBefore(insnNode, insnNodes);
                                shouldWrite.set(true);
                            }
                        });
                    }
                });
            } else if ("net/minecraft/world/item/Equipable".equals(classNode.name)) {
                classNode.methods.forEach(methodNode -> {
                    if (MCMapping.Equipable$METHOD$get.equalsMethodNode(methodNode)) {
                       InsnList insnNodes = new InsnList();
                       LabelNode jumpNode = new LabelNode();
                       insnNodes.add(new VarInsnNode(Opcodes.ALOAD, 0));
                       insnNodes.add(new MethodInsnNode(Opcodes.INVOKESTATIC, EVENT_UTIL_CLASS, "getEquippableComponentEquipable", "(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/Equipable;", false));
                       insnNodes.add(new InsnNode(Opcodes.DUP));
                       insnNodes.add(new JumpInsnNode(Opcodes.IFNULL, jumpNode));
                       insnNodes.add(new InsnNode(Opcodes.ARETURN));
                       insnNodes.add(jumpNode);
                       insnNodes.add(new InsnNode(Opcodes.POP));
                       InjectionFinder.injectHead(methodNode, insnNodes);
                       shouldWrite.set(true);
                    }
                });
            } else if ("net/minecraft/world/entity/player/Inventory".equals(classNode.name)) {
                classNode.methods.forEach(methodNode -> {
                    if (MCMapping.Inventory$METHOD$hurtArmor.equalsMethodNode(methodNode)) {
                        AtomicBoolean visited = new AtomicBoolean(false);
                        methodNode.instructions.forEach(insnNode -> {
                            if (!visited.get()) {
                                if (insnNode instanceof TypeInsnNode instanceofNode && instanceofNode.getOpcode() == Opcodes.INSTANCEOF) {
                                    InsnList instructions = methodNode.instructions;
                                    if (instructions.get(instructions.indexOf(instanceofNode)-1) instanceof MethodInsnNode getItemNode && getItemNode.desc.equals("()Lnet/minecraft/world/item/Item;")) {
                                        InsnList insnNodes = new InsnList();
                                        insnNodes.add(new MethodInsnNode(Opcodes.INVOKESTATIC, EVENT_UTIL_CLASS, "isArmorOrEquippableComponentStack", "(Lnet/minecraft/world/item/ItemStack;)Z", false));
                                        instructions.insertBefore(getItemNode, insnNodes);
                                        instructions.remove(getItemNode);
                                        instructions.remove(instanceofNode);
                                        shouldWrite.set(true);
                                        visited.set(true);
                                    }
                                }
                            }
                        });
                    }
                });
            } else if ("net/minecraft/world/inventory/LoomMenu".equals(classNode.name)) {
                classNode.methods.forEach(methodNode -> {
                    if (MCMapping.AbstractContainerMenu$METHOD$quickMoveStack.equalsMethodNode(methodNode)) {
                        methodNode.instructions.forEach(insnNode -> {
                            if (insnNode instanceof TypeInsnNode instanceofNode && instanceofNode.getOpcode() == Opcodes.INSTANCEOF) {
                                InsnList instructions = methodNode.instructions;
                                if (instructions.get(instructions.indexOf(instanceofNode)-1) instanceof MethodInsnNode getItemNode && getItemNode.desc.equals("()Lnet/minecraft/world/item/Item;")) {
                                    InsnList insnNodes = new InsnList();
                                    insnNodes.add(new MethodInsnNode(Opcodes.INVOKESTATIC, EVENT_UTIL_CLASS, "isBannerPatternOrComponentStack", "(Lnet/minecraft/world/item/ItemStack;)Z", false));
                                    instructions.insertBefore(getItemNode, insnNodes);
                                    instructions.remove(getItemNode);
                                    instructions.remove(instanceofNode);
                                    shouldWrite.set(true);
                                }
                            }
                        });
                    }
                });
            } else if (MINECRAFT_CLASS.equals(classNode.name)) {
                classNode.methods.forEach(methodNode -> {
                    if (MCMapping.Minecraft$METHOD$handleKeybinds.equalsMethodNode(methodNode)) {
                        InsnList instructions = methodNode.instructions;
                        AtomicBoolean finished = new AtomicBoolean(false);
                        AtomicInteger ordinalOf_vin_index_of_array = new AtomicInteger(-1);
                        instructions.forEach(insnNode -> {
                            if (!finished.get()) {
                                if ((insnNode instanceof FieldInsnNode fin_keyHotbarSlots
                                        && fin_keyHotbarSlots.owner.equals(OPTIONS_CLASS)
                                        && MCMapping.Options$FIELD$keyHotbarSlots.equalsFieldNode(fin_keyHotbarSlots))) {
                                    //查找下一个insn node 是否是对象数值的索引
                                    if (instructions.get(instructions.indexOf(fin_keyHotbarSlots) + 1) instanceof VarInsnNode vin_index_of_array) {
                                        ordinalOf_vin_index_of_array.set(vin_index_of_array.var);
                                    }
                                } else {
                                    if (ordinalOf_vin_index_of_array.get() > 0) {
                                        if (insnNode instanceof MethodInsnNode min_consumeClick && min_consumeClick.owner.equals(KEYMAPPING_CLASS) && MCMapping.KeyMapping$METHOD$consumeClick.equalsMethodNode(min_consumeClick)) {
                                            InsnList insnNodes = new InsnList();
                                            insnNodes.add(new VarInsnNode(Opcodes.ILOAD, ordinalOf_vin_index_of_array.get()));
                                            insnNodes.add(new MethodInsnNode(Opcodes.INVOKESTATIC, CLIENT_EVENT_UTIL_CLASS, "hotbarKeyConsumeClickAndCanUse", "(ZI)Z"));
                                            instructions.insert(min_consumeClick, insnNodes);
                                            shouldWrite.set(true);
                                            finished.set(true);
                                        }
                                    }
                                }
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
            classNode.methods.forEach(m -> {
                m.instructions.forEach(n -> {
                    if (n instanceof FieldInsnNode fin) {
                        if (fin.getOpcode() == Opcodes.PUTFIELD) {
                            if (fin.owner.equals("net/minecraft/world/entity/player/Inventory")) {
                                if (MCMapping.Inventory$FIELD$selected.equalsFieldNode(fin)) {
                                    InsnList insnNodes = new InsnList();
                                    insnNodes.add(new InsnNode(Opcodes.DUP2));
                                    insnNodes.add(new FieldInsnNode(fin.getOpcode(), fin.owner, fin.name, fin.desc));
                                    insnNodes.add(new MethodInsnNode(Opcodes.INVOKESTATIC, EVENT_UTIL_CLASS, "onInventorySelectedSet", "(Lnet/minecraft/world/entity/player/Inventory;I)V"));
                                    m.instructions.insertBefore(fin, insnNodes);
                                    m.instructions.remove(fin);
                                    shouldWrite.set(true);
                                }
                            }
                        }
                    }
                });
            });
        }
    }


    static boolean isUnsupportModifyingClass(String name) {
        return name.startsWith("com/mega/endinglib/util/");
    }
}
