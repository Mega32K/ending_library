package com.mega.endinglib.common.command.entity;

import com.mega.endinglib.api.client.cmc.LoreHelper;
import com.mega.endinglib.common.capability.EndingLibraryEntityCapability;
import com.mega.endinglib.common.command.argument.FloatArrayArgument;
import com.mega.endinglib.common.command.argument.scehdule.MobTypeArgument;
import com.mega.endinglib.common.command.entity.mob.MobControlCommand;
import com.mega.endinglib.common.config.ServerConfig;
import com.mega.endinglib.mixin.accessor.AccessorEntity;
import com.mega.endinglib.proxy.CommonProxy;
import com.mega.endinglib.util.mc.CommandFunction;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DataCommand {
    public static final BiFunction<DataType<Boolean>, EndingLibraryEntityCapability, Integer> BOOL_COMMAND_RESULT = (type, cap) -> type.getCapValue(cap) ? 1 : 0;
    public static final BiFunction<DataType<Integer>, EndingLibraryEntityCapability, Integer> INT_COMMAND_RESULT = DataType::getCapValue;
    public static final BiFunction<DataType<Float>, EndingLibraryEntityCapability, Integer> FLOAT_COMMAND_RESULT = (type, cap) -> (int) (type.getCapValue(cap) * 100.0F);
    public static <T> BiFunction<DataType<Optional<T>>, EndingLibraryEntityCapability, Integer> createOptionalUnitResult() {
        return (type, cap) -> type.getCapValue(cap).isPresent() ? 1 : 0;
    }
    static Entity getTarget(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return EntityArgument.getEntity(context, "target");
    }
    public static final CommandFunction<CommandContext<CommandSourceStack>, DataType<?>, Integer> NORMAL_COMMAND_GET_RULE = (context, personalRule) -> get(context.getSource(), getTarget(context), personalRule);
    public static final DataType<Optional<EntityDimensions>> DIMENSIONS = build("dimensions", (command, personalRule) ->
                    command.then(Commands.argument("width", FloatArgumentType.floatArg())
                                    .executes(context -> {
                                        float width = FloatArgumentType.getFloat(context, "width");
                                        return set(context.getSource(), getTarget(context), personalRule, Optional.of(EntityDimensions.scalable(width, width)));
                                    })
                                    .then(Commands.literal("fixed")
                                            .executes(context -> {
                                                float width = FloatArgumentType.getFloat(context, "width");
                                                return set(context.getSource(), getTarget(context), personalRule, Optional.of(EntityDimensions.fixed(width, width)));
                                            })
                                    )
                                    .then(Commands.argument("height", FloatArgumentType.floatArg())
                                            .executes(context -> set(context.getSource(), getTarget(context), personalRule, Optional.of(EntityDimensions.scalable(FloatArgumentType.getFloat(context, "width"), FloatArgumentType.getFloat(context, "height")))))
                                            .then(Commands.literal("fixed")
                                                    .executes(context -> set(context.getSource(), getTarget(context), personalRule, Optional.of(EntityDimensions.fixed(FloatArgumentType.getFloat(context, "width"), FloatArgumentType.getFloat(context, "height")))))
                                            )
                                    )
                            )
                            .executes(context -> {
                                Entity entity = getTarget(context);
                                int[] returnValue = new int[] {-1};
                                CommonProxy.getEntityCapOptional(entity).ifPresent(cap ->
                                        cap.getCustomEntityDimensions().ifPresent(ed -> {
                                            sendGetMessage(context.getSource(), entity, personalRule.getName(), personalRule.asComponent(Optional.of(ed)));
                                            returnValue[0] = (int) (new Vec2(ed.width, ed.height).length() * 100F);
                                        })
                                );
                                if (returnValue[0] == -1) {
                                    EntityDimensions dimensions = ((AccessorEntity) entity).getDimensions();
                                    returnValue[0] = (int) (new Vec2(dimensions.width, dimensions.height).length() * 100F);
                                    sendGetMessage(context.getSource(), entity, personalRule.getName(), personalRule.asComponent(Optional.of(dimensions)));
                                }
                                return returnValue[0];
                            }),
            EndingLibraryEntityCapability::setCustomEntityDimensions,
            EndingLibraryEntityCapability::getCustomEntityDimensions,
            (type, cap) -> {
                EntityDimensions ed = type.getCapValue(cap).orElse(EntityDimensions.scalable(0,0));
                return (int) (new Vec2(ed.width, ed.height).length() * 100F);
            },
            Optional.empty(),
            LoreHelper.OPT_ENTITY_DIMENSIONAL_COMPONENT_OPERATION
    );
    public static final DataType<Optional<AABB>> CULLING_BOX = build("culling_box", (command, personalRule) ->
                    command.then(Commands.argument("start", Vec3Argument.vec3(false))
                                    .executes(context -> {
                                        Vec3 v = Vec3Argument.getVec3(context, "start");
                                        return set(context.getSource(), getTarget(context), personalRule, Optional.of(new AABB(v,v)));
                                    })
                                    .then(Commands.argument("end", Vec3Argument.vec3(false))
                                            .executes(context -> set(context.getSource(), getTarget(context), personalRule, Optional.of(new AABB(Vec3Argument.getVec3(context, "start"), Vec3Argument.getVec3(context, "end")))))
                                    )
                            )
                            .executes(context -> NORMAL_COMMAND_GET_RULE.apply(context, personalRule)),
            EndingLibraryEntityCapability::setCustomCullingBox,
            EndingLibraryEntityCapability::getCustomCullingBox,
            (type, cap) -> type.getCapValue(cap).map(AABB::hashCode).orElse(0),
            Optional.empty(),
            LoreHelper.OPT_AABB_COMPONENT_OPERATION
    );
    public static final DataType<Optional<AABB>> HITBOX = build("hitbox", (command, personalRule) ->
                    command.then(Commands.argument("start", Vec3Argument.vec3(false))
                                    .executes(context -> {
                                        Vec3 v = Vec3Argument.getVec3(context, "start");
                                        return set(context.getSource(), getTarget(context), personalRule, Optional.of(new AABB(v,v)));
                                    })
                                    .then(Commands.argument("end", Vec3Argument.vec3(false))
                                            .executes(context -> set(context.getSource(), getTarget(context), personalRule, Optional.of(new AABB(Vec3Argument.getVec3(context, "start"), Vec3Argument.getVec3(context, "end")))))
                                    )
                            )
                            .executes(context -> NORMAL_COMMAND_GET_RULE.apply(context, personalRule)),
            EndingLibraryEntityCapability::setCustomHitbox,
            EndingLibraryEntityCapability::getCustomHitbox,
            (type, cap) -> type.getCapValue(cap).map(AABB::hashCode).orElse(0),
            Optional.empty(),
            LoreHelper.OPT_AABB_COMPONENT_OPERATION
    );
    public static final DataType<Optional<Vector3f>> RENDER_SCALE = build("render_scale", (command, personalRule) ->
                    command.then(Commands.argument("scale", FloatArrayArgument.floats(3))
                                    .executes(context -> set(context.getSource(), getTarget(context), personalRule, Optional.of(new Vector3f(FloatArrayArgument.getFloats(context,"scale")))))
                            )
                            .executes(context -> NORMAL_COMMAND_GET_RULE.apply(context, personalRule)),
            EndingLibraryEntityCapability::setRenderScale,
            EndingLibraryEntityCapability::getRenderScale,
            (type, cap) -> (int) (type.getCapValue(cap).orElse(new Vector3f()).length() * 100F),
            Optional.empty(),
            LoreHelper.OPT_VEC3F_OPERATION
    );
    public static final DataType<Optional<String>> CUSTOM_MOB_TYPE = build("mob_type", (command, personalRule) ->
                    command.then(Commands.argument("mobType", MobTypeArgument.mobType())
                                    .executes(context -> {
                                        if (getTarget(context) instanceof Mob mob)
                                            return set(context.getSource(), mob, personalRule, MobTypeArgument.getMobType(context, "mobType"));
                                        throw MobControlCommand.NO_MOBS_FOUND.create();
                                    })
                            )
                            .executes(context -> {
                                if (getTarget(context) instanceof Mob entity) {
                                    int[] returnValue = new int[] {0};
                                    CommonProxy.getEntityCapOptional(entity).ifPresent(cap ->
                                            cap.getMobType().ifPresent(str -> {
                                                sendGetMessage(context.getSource(), entity, personalRule.getName(), personalRule.asComponent(Optional.of(str)));
                                                returnValue[0] = 1;
                                            })
                                    );
                                    if (returnValue[0] == 0) {
                                        sendGetMessage(context.getSource(), entity, personalRule.getName(), personalRule.asComponent(Optional.of(MobTypeArgument.getName(entity.getMobType()))));
                                    }
                                    return returnValue[0];
                                }
                                throw MobControlCommand.NO_MOBS_FOUND.create();
                            }),
            EndingLibraryEntityCapability::setMobType,
            EndingLibraryEntityCapability::getMobType,
            createOptionalUnitResult(),
            Optional.empty(),
            LoreHelper.OPT_STRING_OPERATION
    );
    public static final DataType<String> CUSTOM_MODEL_TEXTURE = build("model_texture", (command, personalRule) ->
                    command.then(Commands.argument("value", ResourceLocationArgument.id())
                                    .executes(context -> set(context.getSource(), getTarget(context), personalRule, ResourceLocationArgument.getId(context, "value").toString()))
                            )
                            .executes(context -> NORMAL_COMMAND_GET_RULE.apply(context, personalRule)),
            EndingLibraryEntityCapability::setCustomModelTexture,
            EndingLibraryEntityCapability::getCustomModelTexture,
            (type, cap) -> type.getCapValue(cap).isEmpty() ? 0 : 1,
            ""
    );
    public static final DataType<Boolean> LOCKED_X_ROT = build("lockedXRot", (command, personalRule) ->
                    command.then(Commands.argument("value", BoolArgumentType.bool())
                                    .executes(context -> set(context.getSource(), getTarget(context), personalRule, BoolArgumentType.getBool(context, "value")))
                            )
                            .executes(context -> NORMAL_COMMAND_GET_RULE.apply(context, personalRule)),
            EndingLibraryEntityCapability::lockRotX,
            EndingLibraryEntityCapability::isXRotLocked,
            BOOL_COMMAND_RESULT,
            false
    );
    public static final DataType<Boolean> LOCKED_Y_ROT = build("lockedYRot", (command, personalRule) ->
                    command.then(Commands.argument("value", BoolArgumentType.bool())
                                    .executes(context -> set(context.getSource(), getTarget(context), personalRule, BoolArgumentType.getBool(context, "value")))
                            )
                            .executes(context -> NORMAL_COMMAND_GET_RULE.apply(context, personalRule)),
            EndingLibraryEntityCapability::lockRotY,
            EndingLibraryEntityCapability::isYRotLocked,
            BOOL_COMMAND_RESULT,
            false
    );
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return LiteralArgumentBuilder.<CommandSourceStack>literal("data")
                .requires(stack -> stack.hasPermission(ServerConfig.COMMAND_DATA.get()))
                .then(buildAllCommands(Commands.argument("target", EntityArgument.entity())));
    }
    static <T> DataType<T> build(String serializerName, BiFunction<LiteralArgumentBuilder<CommandSourceStack>, DataType<T>, LiteralArgumentBuilder<CommandSourceStack>> commandBuilder, BiConsumer<EndingLibraryEntityCapability, T> capValueSetter, Function<EndingLibraryEntityCapability, T> capValueGetter, BiFunction<DataType<T>, EndingLibraryEntityCapability, Integer> commandResult, T defaultValue) {
        return new DataType<>(capValueSetter, capValueGetter, serializerName, commandBuilder, defaultValue, commandResult);
    }
    static <T> DataType<T> build(String serializerName, BiFunction<LiteralArgumentBuilder<CommandSourceStack>, DataType<T>, LiteralArgumentBuilder<CommandSourceStack>> commandBuilder, BiConsumer<EndingLibraryEntityCapability, T> capValueSetter, Function<EndingLibraryEntityCapability, T> capValueGetter, BiFunction<DataType<T>, EndingLibraryEntityCapability, Integer> commandResult, T defaultValue, Function<T, Component> asComponent) {
        return new DataType<>(capValueSetter, capValueGetter, serializerName, commandBuilder, defaultValue, commandResult, asComponent);
    }
    public static RequiredArgumentBuilder<CommandSourceStack, EntitySelector> buildAllCommands(RequiredArgumentBuilder<CommandSourceStack, EntitySelector> p) {
        for (DataType<?> rule : DataType.RULES) {
            p.then(rule.command(Commands.literal(rule.getName())));
            p.then(Commands.literal(rule.getName())
                    .then(Commands.literal("default")
                            .executes(context -> set(context.getSource(), getTarget(context), rule, rule.defaultValue, true))
                    )
            );
        }
        return p;
    }
    private static void sendGetMessage(CommandSourceStack stack, Entity entity, String rule, Component component) {
        stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.data.get", entity.getDisplayName(), Component.translatable("commands.endinglib.message.data." + rule), component), false);
    }

    private static void sendSetDefaultMessage(CommandSourceStack stack, Entity entity, String rule, Component component) {
        stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.data.default", entity.getDisplayName(), Component.translatable("commands.endinglib.message.data." + rule), component), false);
    }

    private static void sendModifyMessage(CommandSourceStack stack, Entity entity, String rule, Component valueToString) {
        stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.data.set", entity.getDisplayName(), Component.translatable("commands.endinglib.message.data." + rule), valueToString), false);
    }
    private static <T> int set(CommandSourceStack stack, Entity entity, DataType<T> rule, T value) {
        return set(stack, entity, rule, value, false);
    }
    private static <T> int set(CommandSourceStack stack, Entity entity, DataType<T> rule, Object value, boolean isSetToDefault) {
        CommonProxy.getEntityCapOptional(entity).ifPresent(cap -> {
            rule.setCapValue(cap, value);
            if (isSetToDefault) {
                sendSetDefaultMessage(stack, entity, rule.getName(), rule.asComponent(rule.getCapValue(cap)));
            } else {
                sendModifyMessage(stack, entity, rule.getName(), rule.asComponent(rule.getCapValue(cap)));
            }
        });
        return 0;
    }
    private static <T> int get(CommandSourceStack stack, Entity entity, DataType<T> rule) {
        AtomicInteger result = new AtomicInteger(-1);
        CommonProxy.getEntityCapOptional(entity).ifPresent(cap -> {
            T capValue = rule.getCapValue(cap);
            if (result.get() != 0)
                sendGetMessage(stack, entity, rule.getName(), rule.asComponent(capValue));
            if (result.get() < 0)
                result.set(rule.getCommandResult(cap));
        });

        return result.get();
    }
    public static class DataType<T> {
        public static final Set<DataType<?>> RULES = new ObjectOpenHashSet<>();
        private final BiConsumer<EndingLibraryEntityCapability, T> capValueSetter;
        private final Function<EndingLibraryEntityCapability, T> capValueGetter;
        private final String serializerName;
        private final BiFunction<LiteralArgumentBuilder<CommandSourceStack>, DataType<T>, LiteralArgumentBuilder<CommandSourceStack>> commandBuilder;
        private final BiFunction<DataType<T>, EndingLibraryEntityCapability, Integer> commandResult;
        private final Function<T, Component> asComponent;
        private final T defaultValue;
        public DataType(BiConsumer<EndingLibraryEntityCapability, T> capValueSetter,
                        Function<EndingLibraryEntityCapability, T> capValueGetter,
                        String serializerName,
                        BiFunction<LiteralArgumentBuilder<CommandSourceStack>, DataType<T>, LiteralArgumentBuilder<CommandSourceStack>> commandBuilder,
                        T defaultValue,
                        BiFunction<DataType<T>, EndingLibraryEntityCapability, Integer> commandResult,
                        Function<T, Component> asComponent) {
            this.capValueSetter = capValueSetter;
            this.capValueGetter = capValueGetter;
            this.serializerName = serializerName;
            this.commandBuilder = commandBuilder;
            this.defaultValue = defaultValue;
            this.commandResult = commandResult;
            this.asComponent = asComponent;
            RULES.add(this);
        }
        public DataType(BiConsumer<EndingLibraryEntityCapability, T> capValueSetter,
                        Function<EndingLibraryEntityCapability, T> capValueGetter,
                        String serializerName,
                        BiFunction<LiteralArgumentBuilder<CommandSourceStack>, DataType<T>, LiteralArgumentBuilder<CommandSourceStack>> commandBuilder,
                        T defaultValue,
                        BiFunction<DataType<T>, EndingLibraryEntityCapability, Integer> commandResult) {
            this(capValueSetter, capValueGetter, serializerName, commandBuilder, defaultValue, commandResult, null);
        }

        @Override
        public int hashCode() {
            return serializerName.hashCode();
        }

        public LiteralArgumentBuilder<CommandSourceStack> command(LiteralArgumentBuilder<CommandSourceStack> parent) {
            return commandBuilder.apply(parent, this);
        }

        public T getDefaultValue() {
            return defaultValue;
        }

        public String getName() {
            return serializerName;
        }

        public T getCapValue(EndingLibraryEntityCapability cap) {
            return capValueGetter.apply(cap);
        }

        public void setCapValue(EndingLibraryEntityCapability cap, Object value) {
            capValueSetter.accept(cap, (T) value);
        }
        public Component asComponent(T value) {
            if (this.asComponent != null)
                return this.asComponent.apply(value);
            else {
                if (value instanceof Optional<?> optional) {
                    return optionalAsComponent(optional, LoreHelper::optionalWrap);
                } else {
                    String result = value.toString();
                    return Component.literal(result).withStyle(ChatFormatting.GOLD).withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, result)).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click"))));
                }
            }
        }
        private Component optionalAsComponent(Optional<?> optional, Function<Component, MutableComponent> function) {
            if (optional.isEmpty()) {
                return function.apply(LoreHelper.empty());
            } else if (optional.get() instanceof Component c) {
                return function.apply(c);
            } else if (optional.get() instanceof Optional<?> o2)
                return function.apply(optionalAsComponent(o2, LoreHelper::optionalWrap));
            else {
                String result = optional.get().toString();
                return function.apply(Component.literal(result).withStyle(ChatFormatting.GOLD).withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, result)).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click")))));
            }
        }
        public int getCommandResult(EndingLibraryEntityCapability cap) {
            return this.commandResult.apply(this, cap);
        }
    }
}
