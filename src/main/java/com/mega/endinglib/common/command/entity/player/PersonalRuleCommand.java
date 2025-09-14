package com.mega.endinglib.common.command.entity.player;

import com.mega.endinglib.common.capability.EndingLibraryPlayerCapability;
import com.mega.endinglib.proxy.CommonProxy;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class PersonalRuleCommand {
    public static final CommandFunction<CommandContext<CommandSourceStack>, PersonalRule<?>, Integer> NORMAL_COMMAND_GET_RULE = (context, personalRule) -> get(context.getSource(), EntityArgument.getPlayer(context, "player"), personalRule);
    public static final PersonalRule<Boolean> OTHER_SPECTOR_RENDERING = build("othrSpectorRender", (command, personalRule) ->
                    command.then(Commands.argument("value", BoolArgumentType.bool())
                                    .executes(context -> set(context.getSource(), EntityArgument.getPlayer(context, "player"), personalRule, BoolArgumentType.getBool(context, "value")))
                            )
                            .executes(context -> NORMAL_COMMAND_GET_RULE.apply(context, personalRule)),
            EndingLibraryPlayerCapability::setOtherSpectorRendering,
            EndingLibraryPlayerCapability::otherSpectorRendering,
            true
    );
    public static final PersonalRule<Boolean> OTHER_PLAYERS_RENDERING = build("othrPlayersRender", (command, personalRule) ->
                    command.then(Commands.argument("value", BoolArgumentType.bool())
                                    .executes(context -> set(context.getSource(), EntityArgument.getPlayer(context, "player"), personalRule, BoolArgumentType.getBool(context, "value")))
                            )
                            .executes(context -> NORMAL_COMMAND_GET_RULE.apply(context, personalRule)),
            EndingLibraryPlayerCapability::setOtherPlayerRendering,
            EndingLibraryPlayerCapability::otherPlayerRendering,
            true
    );

    public static final PersonalRule<Boolean> OTHER_PLAYER_NAMES_RENDERER = build("othrPNameRender", (command, personalRule) ->
                    command.then(Commands.argument("value", BoolArgumentType.bool())
                                    .executes(context -> set(context.getSource(), EntityArgument.getPlayer(context, "player"), personalRule, BoolArgumentType.getBool(context, "value")))
                            )
                            .executes(context -> NORMAL_COMMAND_GET_RULE.apply(context, personalRule)),
            EndingLibraryPlayerCapability::setOtherPlayerRenderingName,
            EndingLibraryPlayerCapability::otherPlayerRenderingName,
            true
    );
    public static final PersonalRule<Float> WALKING_VIEW_MULTIPLIER = build("walkingViewMultiplier", (command, personalRule) ->
                    command.then(Commands.argument("value", FloatArgumentType.floatArg(0, 8F))
                                    .executes(context -> set(context.getSource(), EntityArgument.getPlayer(context, "player"), personalRule, FloatArgumentType.getFloat(context, "value")))
                            )
                            .executes(context -> NORMAL_COMMAND_GET_RULE.apply(context, personalRule)),
            EndingLibraryPlayerCapability::setWalkingViewMultiplier,
            EndingLibraryPlayerCapability::getWalkingViewMultiplier,
            1.0F
    );
    public static final PersonalRule<Float> HURT_VIEW_MULTIPLIER = build("hurtViewMultiplier", (command, personalRule) ->
                    command.then(Commands.argument("value", FloatArgumentType.floatArg(0, 8F))
                                    .executes(context -> set(context.getSource(), EntityArgument.getPlayer(context, "player"), personalRule, FloatArgumentType.getFloat(context, "value")))
                            )
                            .executes(context -> NORMAL_COMMAND_GET_RULE.apply(context, personalRule)),
            EndingLibraryPlayerCapability::setHurtViewMultiplier,
            EndingLibraryPlayerCapability::getHurtViewMultiplier,
            1.0F
    );
    public static final PersonalRule<Boolean> LOCKED_GAME_MODE = build("lockedGameMode", (command, personalRule) ->
                    command.then(Commands.argument("value", BoolArgumentType.bool())
                                    .executes(context -> set(context.getSource(), EntityArgument.getPlayer(context, "player"), personalRule, BoolArgumentType.getBool(context, "value")))
                            )
                            .executes(context -> NORMAL_COMMAND_GET_RULE.apply(context, personalRule)),
            EndingLibraryPlayerCapability::setLockedGameMode,
            EndingLibraryPlayerCapability::isGameModeLocked,
            false
    );
    public static final PersonalRule<Boolean> OTHER_TEAM_PLAYERS_NAMES_RENDER = build("othrTeamPNameRender", (command, personalRule) ->
                    command.then(Commands.argument("value", BoolArgumentType.bool())
                                    .executes(context -> set(context.getSource(), EntityArgument.getPlayer(context, "player"), personalRule, BoolArgumentType.getBool(context, "value")))
                            )
                            .executes(context -> NORMAL_COMMAND_GET_RULE.apply(context, personalRule)),
            EndingLibraryPlayerCapability::setOtherTeamsPlayerRenderingName,
            EndingLibraryPlayerCapability::otherTeamsPlayerRenderingName,
            true
    );

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("personal")
                .requires((p_138087_) -> p_138087_.hasPermission(2))
                .then(buildAllCommands(Commands.argument("player", EntityArgument.player())));
    }

    private static void sendGetMessage(CommandSourceStack stack, ServerPlayer player, String rule, String valueToString) {
        stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.personal.rule.get", player.getDisplayName(), Component.translatable("commands.endinglib.message.personal_rule." + rule), Component.literal(valueToString).withStyle(ChatFormatting.GOLD).withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, valueToString)).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click"))))), false);
    }

    private static void sendModifyMessage(CommandSourceStack stack, ServerPlayer player, String rule, String valueToString) {
        stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.personal.rule.set", player.getDisplayName(), Component.translatable("commands.endinglib.message.personal_rule." + rule), Component.literal(valueToString).withStyle(ChatFormatting.GOLD).withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, valueToString)).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click"))))), false);
    }

    private static <T> int set(CommandSourceStack stack, ServerPlayer player, PersonalRule<T> rule, T value) {
        rule.setCapValue(CommonProxy.getCameraCap(player), value);
        sendModifyMessage(stack, player, rule.getName(), String.valueOf(value));
        return 0;
    }

    private static <T> int get(CommandSourceStack stack, ServerPlayer player, PersonalRule<T> rule) {
        sendGetMessage(stack, player, rule.getName(), String.valueOf(rule.getCapValue(CommonProxy.getCameraCap(player))));
        return 0;
    }

    static <T> PersonalRule<T> build(String serializerName, BiFunction<LiteralArgumentBuilder<CommandSourceStack>, PersonalRule<T>, LiteralArgumentBuilder<CommandSourceStack>> commandBuilder, BiConsumer<EndingLibraryPlayerCapability, T> capValueSetter, Function<EndingLibraryPlayerCapability, T> capValueGetter, T defaultValue) {
        return new PersonalRule<>(capValueSetter, capValueGetter, serializerName, commandBuilder, defaultValue);
    }

    public static RequiredArgumentBuilder<CommandSourceStack, EntitySelector> buildAllCommands(RequiredArgumentBuilder<CommandSourceStack, EntitySelector> p) {
        for (PersonalRule<?> rule : PersonalRule.RULES) {
            p.then(rule.command(Commands.literal(rule.getName())));
        }
        return p;
    }

    @FunctionalInterface
    public interface CommandFunction<T, U, R> {

        /**
         * Applies this function to the given arguments.
         *
         * @param t the first function argument
         * @param u the second function argument
         * @return the function result
         */
        R apply(T t, U u) throws CommandSyntaxException;
    }

    public static class PersonalRule<T> {
        public static final Set<PersonalRule<?>> RULES = new ObjectOpenHashSet<>();
        private final BiConsumer<EndingLibraryPlayerCapability, T> capValueSetter;
        private final Function<EndingLibraryPlayerCapability, T> capValueGetter;
        private final String serializerName;
        private final BiFunction<LiteralArgumentBuilder<CommandSourceStack>, PersonalRule<T>, LiteralArgumentBuilder<CommandSourceStack>> commandBuilder;
        private final T defaultValue;
        public PersonalRule(BiConsumer<EndingLibraryPlayerCapability, T> capValueSetter, Function<EndingLibraryPlayerCapability, T> capValueGetter, String serializerName, BiFunction<LiteralArgumentBuilder<CommandSourceStack>, PersonalRule<T>, LiteralArgumentBuilder<CommandSourceStack>> commandBuilder, T defaultValue) {
            this.capValueSetter = capValueSetter;
            this.capValueGetter = capValueGetter;
            this.serializerName = serializerName;
            this.commandBuilder = commandBuilder;
            this.defaultValue = defaultValue;
            RULES.add(this);
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

        public void setCapValue(EndingLibraryPlayerCapability cap, T value) {
            capValueSetter.accept(cap, value);
        }

    }
}
