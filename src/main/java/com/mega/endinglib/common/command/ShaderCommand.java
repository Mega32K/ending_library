package com.mega.endinglib.common.command;

import com.mega.endinglib.api.client.cmc.LoreHelper;
import com.mega.endinglib.client.ClientWrapped;
import com.mega.endinglib.common.command.argument.FloatArrayArgument;
import com.mega.endinglib.common.config.ServerConfig;
import com.mega.endinglib.common.network.PacketHandler;
import com.mega.endinglib.common.network.s2c.shader.S2CScreenEffectCreatePacket;
import com.mega.endinglib.common.network.s2c.shader.S2CScreenEffectRemovePacket;
import com.mega.endinglib.common.network.s2c.shader.S2CScreenEffectStatusPacket;
import com.mega.endinglib.common.network.s2c.shader.S2CScreenEffectUniformPacket;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Arrays;

public class ShaderCommand {
    private static final SuggestionProvider<CommandSourceStack> SHADER_NAMES_SUGGESTION = (context, builder) -> {
        CommandsEvent.suggestFromExamples(ClientWrapped.keysOfCommandScreenEffects(), builder);
        return builder.buildFuture();
    };

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return LiteralArgumentBuilder.<CommandSourceStack>literal("shader")
                .requires(stack -> stack.hasPermission(ServerConfig.COMMAND_SHADER.get()))
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.literal("create")
                                .then(Commands.argument("name", StringArgumentType.word())
                                        .then(Commands.argument("effectLocation", ResourceLocationArgument.id())
                                                .executes(context -> create(context.getSource(), getPlayer(context), getEffectName(context), ResourceLocationArgument.getId(context, "effectLocation")))
                                        )
                                )
                        )
                        .then(Commands.literal("remove")
                                .then(Commands.argument("name", StringArgumentType.word())
                                        .suggests(SHADER_NAMES_SUGGESTION)
                                        .executes(context -> remove(context.getSource(), getPlayer(context), getEffectName(context)))
                                )
                        )
                        .then(Commands.literal("enable")
                                .then(Commands.argument("name", StringArgumentType.word())
                                        .suggests(SHADER_NAMES_SUGGESTION)
                                        .executes(context -> status(context.getSource(), getPlayer(context), getEffectName(context), true))
                                )
                        )
                        .then(Commands.literal("disable")
                                .then(Commands.argument("name", StringArgumentType.word())
                                        .suggests(SHADER_NAMES_SUGGESTION)
                                        .executes(context -> status(context.getSource(), getPlayer(context), getEffectName(context), false))
                                )
                        )
                        .then(Commands.literal("uniform")
                                .then(Commands.argument("name", StringArgumentType.word())
                                        .suggests(SHADER_NAMES_SUGGESTION)
                                        .then(Commands.literal("singlePass")
                                                .then(Commands.argument("pass", StringArgumentType.string())
                                                        .suggests(ClientWrapped::suggestCurrentPasses)
                                                        .then(Commands.argument("uniform", StringArgumentType.string())
                                                                .suggests(ClientWrapped::suggestSinglePassUniforms)
                                                                .then(Commands.literal("set")
                                                                        .then(Commands.argument("values", FloatArrayArgument.floats())
                                                                                .executes(context -> setUniforms(context.getSource(), getPlayer(context), getEffectName(context), getPassName(context), StringArgumentType.getString(context, "uniform"), FloatArrayArgument.getFloats(context, "values")))
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                        .then(Commands.literal("allPasses")
                                                .then(Commands.argument("uniform", StringArgumentType.string())
                                                        .suggests(ClientWrapped::suggestAllUniforms)
                                                        .then(Commands.literal("set")
                                                                .then(Commands.argument("values", FloatArrayArgument.floats())
                                                                        .executes(context -> setUniforms(context.getSource(), getPlayer(context), getEffectName(context), StringArgumentType.getString(context, "uniform"), FloatArrayArgument.getFloats(context, "values")))
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                );
    }
    public static ServerPlayer getPlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return EntityArgument.getPlayer(context, "player");
    }
    public static String getEffectName(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return StringArgumentType.getString(context, "name");
    }
    public static String getPassName(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return StringArgumentType.getString(context, "pass");
    }
    private static int create(CommandSourceStack sourceStack, ServerPlayer player, String name, ResourceLocation location) {
        if (location.getPath().endsWith(".json")) {
            PacketHandler.sendToPlayer(new S2CScreenEffectCreatePacket(name, location), player);
            sourceStack.sendSuccess(()-> Component.translatable("commands.endinglib.message.shader.create", player.getDisplayName(), LoreHelper.withCopy(Component.literal(name), name)), false);
            return 1;
        } else sourceStack.sendFailure(Component.translatable("commands.endinglib.message.shader.invalid.effect_location", location));
        return 0;
    }
    private static int remove(CommandSourceStack sourceStack, ServerPlayer player, String name) {
        PacketHandler.sendToPlayer(new S2CScreenEffectRemovePacket(name), player);
        sourceStack.sendSuccess(()-> Component.translatable("commands.endinglib.message.shader.remove", player.getDisplayName(), LoreHelper.withCopy(Component.literal(name), name)), false);
        return 1;
    }
    private static int status(CommandSourceStack sourceStack, ServerPlayer player, String name, boolean using) {
        PacketHandler.sendToPlayer(new S2CScreenEffectStatusPacket(name, using), player);
        sourceStack.sendSuccess(()-> Component.translatable("commands.endinglib.message.shader." + (using ? "enable" : "disable"), player.getDisplayName(), name), false);
        return 1;
    }
    private static int setUniforms(CommandSourceStack sourceStack, ServerPlayer player, String name, String pass, String uniform, float... values) {

        PacketHandler.sendToPlayer(new S2CScreenEffectUniformPacket.SinglePass(name, pass, uniform, (short) values.length, values), player);
        sourceStack.sendSuccess(()-> Component.translatable("commands.endinglib.message.shader.uniform.set"), false);
        return 1;
    }
    private static int setUniforms(CommandSourceStack sourceStack, ServerPlayer player, String name, String uniform, float... values) {
        PacketHandler.sendToPlayer(new S2CScreenEffectUniformPacket.AllPasses(name, uniform, (short) values.length, values), player);
        sourceStack.sendSuccess(()-> Component.translatable("commands.endinglib.message.shader.uniform.set"), false);
        return 1;
    }
}
