package com.mega.endinglib.common.command;

import com.mega.endinglib.api.client.cmc.LoreHelper;
import com.mega.endinglib.common.command.argument.FloatArrayArgument;
import com.mega.endinglib.common.command.argument.PostEffectArgument;
import com.mega.endinglib.common.command.argument.PostEffectPassArgument;
import com.mega.endinglib.common.command.argument.PostEffectUniformArgument;
import com.mega.endinglib.common.config.ServerConfig;
import com.mega.endinglib.common.network.PacketHandler;
import com.mega.endinglib.common.network.s2c.shader.S2CScreenEffectCreatePacket;
import com.mega.endinglib.common.network.s2c.shader.S2CScreenEffectRemovePacket;
import com.mega.endinglib.common.network.s2c.shader.S2CScreenEffectStatusPacket;
import com.mega.endinglib.common.network.s2c.shader.S2CScreenEffectUniformPacket;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class ShaderCommand {

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
                                .then(Commands.argument("name", PostEffectArgument.postEffect())
                                        .executes(context -> remove(context.getSource(), getPlayer(context), getEffectName(context)))
                                )
                        )
                        .then(Commands.literal("enable")
                                .then(Commands.argument("name", PostEffectArgument.postEffect())
                                        .executes(context -> status(context.getSource(), getPlayer(context), getEffectName(context), true))
                                )
                        )
                        .then(Commands.literal("disable")
                                .then(Commands.argument("name", PostEffectArgument.postEffect())
                                        .executes(context -> status(context.getSource(), getPlayer(context), getEffectName(context), false))
                                )
                        )
                        .then(Commands.literal("uniform")
                                .then(Commands.argument("name", PostEffectArgument.postEffect())
                                        .then(Commands.literal("singlePass")
                                                .then(Commands.argument("pass", PostEffectPassArgument.pass())
                                                        .then(Commands.argument("ordinalOfPass", IntegerArgumentType.integer(0, Short.MAX_VALUE))
                                                                .then(Commands.argument("uniform", PostEffectUniformArgument.singlePassUniforms())
                                                                        .then(Commands.literal("set")
                                                                                .then(Commands.argument("values", FloatArrayArgument.floats())
                                                                                        .executes(context -> setUniforms(context.getSource(), getPlayer(context), getEffectName(context), getPassName(context), (short) IntegerArgumentType.getInteger(context, "ordinalOfPass"), getUniform(context), FloatArrayArgument.getFloats(context, "values")))
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                        .then(Commands.argument("uniform", PostEffectUniformArgument.singlePassUniforms())
                                                                .then(Commands.literal("set")
                                                                        .then(Commands.argument("values", FloatArrayArgument.floats())
                                                                                .executes(context -> setUniforms(context.getSource(), getPlayer(context), getEffectName(context), getPassName(context), (short) 0, getUniform(context), FloatArrayArgument.getFloats(context, "values")))
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                        .then(Commands.literal("allPasses")
                                                .then(Commands.argument("uniform", PostEffectUniformArgument.allPassesUniforms())
                                                        .then(Commands.literal("set")
                                                                .then(Commands.argument("values", FloatArrayArgument.floats())
                                                                        .executes(context -> setUniforms(context.getSource(), getPlayer(context), getEffectName(context), getUniform(context), FloatArrayArgument.getFloats(context, "values")))
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
    public static String getEffectName(CommandContext<?> context) {
        return PostEffectArgument.getEffectName(context, "name");
    }
    public static String getPassName(CommandContext<?> context) {
        return PostEffectPassArgument.getPassName(context, "pass");
    }
    public static String getUniform(CommandContext<?> context) {
        return PostEffectUniformArgument.getUniformName(context, "uniform");
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
    private static int setUniforms(CommandSourceStack sourceStack, ServerPlayer player, String name, String pass, short ordinalOfPass, String uniform, float... values) {

        PacketHandler.sendToPlayer(new S2CScreenEffectUniformPacket.SinglePass(name, pass, ordinalOfPass, uniform, (short) values.length, values), player);
        sourceStack.sendSuccess(()-> Component.translatable("commands.endinglib.message.shader.uniform.set"), false);
        return 1;
    }
    private static int setUniforms(CommandSourceStack sourceStack, ServerPlayer player, String name, String uniform, float... values) {
        PacketHandler.sendToPlayer(new S2CScreenEffectUniformPacket.AllPasses(name, uniform, (short) values.length, values), player);
        sourceStack.sendSuccess(()-> Component.translatable("commands.endinglib.message.shader.uniform.set"), false);
        return 1;
    }
}
