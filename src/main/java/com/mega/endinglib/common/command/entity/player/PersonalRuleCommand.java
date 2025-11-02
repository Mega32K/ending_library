package com.mega.endinglib.common.command.entity.player;

import com.mega.endinglib.api.client.cmc.LoreHelper;
import com.mega.endinglib.common.capability.EndingLibraryPlayerCapability;
import com.mega.endinglib.common.config.ServerConfig;
import com.mega.endinglib.proxy.CommonProxy;
import com.mega.endinglib.util.mc.CommandFunction;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class PersonalRuleCommand {
    public static final BiFunction<PersonalRule<Boolean>, EndingLibraryPlayerCapability, Integer> BOOL_COMMAND_RESULT = (rule, cap) -> rule.getCapValue(cap) ? 1 : 0;
    public static final BiFunction<PersonalRule<Integer>, EndingLibraryPlayerCapability, Integer> INT_COMMAND_RESULT = PersonalRule::getCapValue;
    public static final BiFunction<PersonalRule<Float>, EndingLibraryPlayerCapability, Integer> FLOAT_COMMAND_RESULT = (rule, cap) -> (int) (rule.getCapValue(cap) * 100.0F);
    public static final CommandFunction<CommandContext<CommandSourceStack>, PersonalRule<?>, Integer> NORMAL_COMMAND_GET_RULE = (context, personalRule) -> get(context.getSource(), EntityArgument.getPlayer(context, "player"), personalRule);
    public static final PersonalRule<Boolean> OTHER_SPECTOR_RENDERING = build("othrSpectorRender", (command, personalRule) ->
                    command.then(Commands.argument("value", BoolArgumentType.bool())
                                    .executes(context -> set(context.getSource(), EntityArgument.getPlayer(context, "player"), personalRule, BoolArgumentType.getBool(context, "value")))
                            )
                            .executes(context -> NORMAL_COMMAND_GET_RULE.apply(context, personalRule)),
            EndingLibraryPlayerCapability::setOtherSpectorRendering,
            EndingLibraryPlayerCapability::otherSpectorRendering,
            BOOL_COMMAND_RESULT,
            true
    );
    public static final PersonalRule<Boolean> OTHER_PLAYERS_RENDERING = build("othrPlayersRender", (command, personalRule) ->
                    command.then(Commands.argument("value", BoolArgumentType.bool())
                                    .executes(context -> set(context.getSource(), EntityArgument.getPlayer(context, "player"), personalRule, BoolArgumentType.getBool(context, "value")))
                            )
                            .executes(context -> NORMAL_COMMAND_GET_RULE.apply(context, personalRule)),
            EndingLibraryPlayerCapability::setOtherPlayerRendering,
            EndingLibraryPlayerCapability::otherPlayerRendering,
            BOOL_COMMAND_RESULT,
            true
    );
    public static final PersonalRule<Float> WALKING_VIEW_MULTIPLIER = build("walkingViewMultiplier", (command, personalRule) ->
                    command.then(Commands.argument("value", FloatArgumentType.floatArg(0, 8F))
                                    .executes(context -> set(context.getSource(), EntityArgument.getPlayer(context, "player"), personalRule, FloatArgumentType.getFloat(context, "value")))
                            )
                            .executes(context -> NORMAL_COMMAND_GET_RULE.apply(context, personalRule)),
            EndingLibraryPlayerCapability::setWalkingViewMultiplier,
            EndingLibraryPlayerCapability::getWalkingViewMultiplier,
            FLOAT_COMMAND_RESULT,
            1.0F
    );
    public static final PersonalRule<Float> HURT_VIEW_MULTIPLIER = build("hurtViewMultiplier", (command, personalRule) ->
                    command.then(Commands.argument("value", FloatArgumentType.floatArg(0, 8F))
                                    .executes(context -> set(context.getSource(), EntityArgument.getPlayer(context, "player"), personalRule, FloatArgumentType.getFloat(context, "value")))
                            )
                            .executes(context -> NORMAL_COMMAND_GET_RULE.apply(context, personalRule)),
            EndingLibraryPlayerCapability::setHurtViewMultiplier,
            EndingLibraryPlayerCapability::getHurtViewMultiplier,
            FLOAT_COMMAND_RESULT,
            1.0F
    );
    public static final PersonalRule<Boolean> LOCKED_GAME_MODE = build("lockedGameMode", (command, personalRule) ->
                    command.then(Commands.argument("value", BoolArgumentType.bool())
                                    .executes(context -> set(context.getSource(), EntityArgument.getPlayer(context, "player"), personalRule, BoolArgumentType.getBool(context, "value")))
                            )
                            .executes(context -> NORMAL_COMMAND_GET_RULE.apply(context, personalRule)),
            EndingLibraryPlayerCapability::setLockedGameMode,
            EndingLibraryPlayerCapability::isGameModeLocked,
            BOOL_COMMAND_RESULT,
            false
    );
    public static final PersonalRule<Boolean> HIDE_SCOREBOARD_NUMBERS = build("hideScoreboardNum", (command, personalRule) ->
                    command.then(Commands.argument("value", BoolArgumentType.bool())
                                    .executes(context -> set(context.getSource(), EntityArgument.getPlayer(context, "player"), personalRule, BoolArgumentType.getBool(context, "value")))
                            )
                            .executes(context -> NORMAL_COMMAND_GET_RULE.apply(context, personalRule)),
            EndingLibraryPlayerCapability::setScoreboardNumDisplay,
            EndingLibraryPlayerCapability::isScoreboardNumDisplay,
            BOOL_COMMAND_RESULT,
            false
    );
    public static final PersonalRule<String> CUSTOM_SKIN = build("customSkin", (command, personalRule) ->
                    command.then(Commands.argument("value", ResourceLocationArgument.id())
                                    .executes(context -> set(context.getSource(), EntityArgument.getPlayer(context, "player"), personalRule, ResourceLocationArgument.getId(context, "value").toString()))
                            )
                            .executes(context -> NORMAL_COMMAND_GET_RULE.apply(context, personalRule)),
            EndingLibraryPlayerCapability::setCustomSkin,
            EndingLibraryPlayerCapability::getCustomSkin,
            (a,b) -> a.getCapValue(b).isEmpty() ? 0 : 1,
            ""
    );
    public static final PersonalRule<Optional<Component>> NAME = build("name", (command, personalRule) ->
                    command.then(Commands.argument("value", ComponentArgument.textComponent())
                                    .executes(context -> set(context.getSource(), EntityArgument.getPlayer(context, "player"), personalRule, Optional.of(ComponentArgument.getComponent(context, "value"))))
                            )
                            .executes(context -> NORMAL_COMMAND_GET_RULE.apply(context, personalRule)),
            EndingLibraryPlayerCapability::setDisplayNameOpt,
            EndingLibraryPlayerCapability::getDisplayNameOpt,
            (a,b) -> 0,
            Optional.empty()
    );

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("personal")
                .requires(stack -> stack.hasPermission(ServerConfig.COMMAND_PERMISSION_PERSONAL_RULE.get()))
                .then(buildAllCommands(Commands.argument("player", EntityArgument.player())));
    }
    private static void sendGetMessage(CommandSourceStack stack, ServerPlayer player, String rule, Component valueToString) {
        stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.personal.rule.get", player.getDisplayName(), Component.translatable("commands.endinglib.message.personal_rule." + rule), valueToString), false);
    }
    private static void sendSetDefaultMessage(CommandSourceStack stack, ServerPlayer player, String rule, Component valueToString) {
        stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.personal.rule.default", player.getDisplayName(), Component.translatable("commands.endinglib.message.personal_rule." + rule), valueToString), false);
    }
    private static void sendModifyMessage(CommandSourceStack stack, ServerPlayer player, String rule, Component valueToString) {
        stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.personal.rule.set", player.getDisplayName(), Component.translatable("commands.endinglib.message.personal_rule." + rule), valueToString), false);
    }
    private static <T> int set(CommandSourceStack stack, ServerPlayer player, PersonalRule<T> rule, Object value) {
        return set(stack, player, rule, value, false);
    }
    private static <T> int set(CommandSourceStack stack, ServerPlayer player, PersonalRule<T> rule, Object value, boolean isSetToDefault) {
        CommonProxy.getCameraCapOptional(player).ifPresent(capability -> {
            rule.setCapValue(capability, value);
            if (isSetToDefault) {
                sendSetDefaultMessage(stack, player, rule.getName(), rule.asComponent((T) value));
            } else {
                sendModifyMessage(stack, player, rule.getName(), rule.asComponent((T) value));
            }
        });
        return 0;
    }
    private static <T> int get(CommandSourceStack stack, ServerPlayer player, PersonalRule<T> rule) {
        EndingLibraryPlayerCapability cap = CommonProxy.getCameraCap(player);
        T capValue = rule.getCapValue(cap);
        sendGetMessage(stack, player, rule.getName(), rule.asComponent(capValue));
        return rule.getCommandResult(cap);
    }

    static <T> PersonalRule<T> build(String serializerName, BiFunction<LiteralArgumentBuilder<CommandSourceStack>, PersonalRule<T>, LiteralArgumentBuilder<CommandSourceStack>> commandBuilder, BiConsumer<EndingLibraryPlayerCapability, T> capValueSetter, Function<EndingLibraryPlayerCapability, T> capValueGetter, BiFunction<PersonalRule<T>, EndingLibraryPlayerCapability, Integer> commandResult, T defaultValue) {
        return new PersonalRule<>(capValueSetter, capValueGetter, serializerName, commandBuilder, defaultValue, commandResult);
    }

    static <T> PersonalRule<T> build(String serializerName, BiFunction<LiteralArgumentBuilder<CommandSourceStack>, PersonalRule<T>, LiteralArgumentBuilder<CommandSourceStack>> commandBuilder, BiConsumer<EndingLibraryPlayerCapability, T> capValueSetter, Function<EndingLibraryPlayerCapability, T> capValueGetter, BiFunction<PersonalRule<T>, EndingLibraryPlayerCapability, Integer> commandResult, T defaultValue, Function<T, Component> asComponent) {
        return new PersonalRule<>(capValueSetter, capValueGetter, serializerName, commandBuilder, defaultValue, commandResult, asComponent);
    }
    public static RequiredArgumentBuilder<CommandSourceStack, EntitySelector> buildAllCommands(RequiredArgumentBuilder<CommandSourceStack, EntitySelector> p) {
        for (PersonalRule<?> rule : PersonalRule.RULES) {
            p.then(rule.command(Commands.literal(rule.getName())));
            p.then(Commands.literal(rule.getName())
                    .then(Commands.literal("default")
                            .executes(context -> set(context.getSource(), EntityArgument.getPlayer(context, "player"), rule, rule.defaultValue, true))
                    )
            );
        }
        return p;
    }

    public static class PersonalRule<T> {
        public static final Set<PersonalRule<?>> RULES = new ObjectOpenHashSet<>();
        private final BiConsumer<EndingLibraryPlayerCapability, T> capValueSetter;
        private final Function<EndingLibraryPlayerCapability, T> capValueGetter;
        private final String serializerName;
        private final BiFunction<LiteralArgumentBuilder<CommandSourceStack>, PersonalRule<T>, LiteralArgumentBuilder<CommandSourceStack>> commandBuilder;
        private final BiFunction<PersonalRule<T>, EndingLibraryPlayerCapability, Integer> commandResult;
        private final T defaultValue;
        private final Function<T, Component> asComponent;
        public PersonalRule(BiConsumer<EndingLibraryPlayerCapability, T> capValueSetter, Function<EndingLibraryPlayerCapability, T> capValueGetter, String serializerName, BiFunction<LiteralArgumentBuilder<CommandSourceStack>, PersonalRule<T>, LiteralArgumentBuilder<CommandSourceStack>> commandBuilder, T defaultValue, BiFunction<PersonalRule<T>, EndingLibraryPlayerCapability, Integer> commandResult, Function<T, Component> asComponent) {
            this.capValueSetter = capValueSetter;
            this.capValueGetter = capValueGetter;
            this.serializerName = serializerName;
            this.commandBuilder = commandBuilder;
            this.defaultValue = defaultValue;
            this.commandResult = commandResult;
            this.asComponent = asComponent;
            RULES.add(this);
        }
        public PersonalRule(BiConsumer<EndingLibraryPlayerCapability, T> capValueSetter, Function<EndingLibraryPlayerCapability, T> capValueGetter, String serializerName, BiFunction<LiteralArgumentBuilder<CommandSourceStack>, PersonalRule<T>, LiteralArgumentBuilder<CommandSourceStack>> commandBuilder, T defaultValue, BiFunction<PersonalRule<T>, EndingLibraryPlayerCapability, Integer> commandResult) {
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

        public T getCapValue(EndingLibraryPlayerCapability cap) {
            return capValueGetter.apply(cap);
        }

        public void setCapValue(EndingLibraryPlayerCapability cap, Object value) {
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
        public int getCommandResult(EndingLibraryPlayerCapability cap) {
            return this.commandResult.apply(this, cap);
        }
    }
}
