package com.mega.endinglib.common.command.entity;

import com.mega.endinglib.common.config.CommandConfig;
import com.mega.endinglib.common.network.PacketHandler;
import com.mega.endinglib.common.network.s2c.S2CCompletelySoundPacket;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;

import java.util.Collection;

public class SoundCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        RequiredArgumentBuilder<CommandSourceStack, ResourceLocation> requiredargumentbuilder = Commands.argument("sound", ResourceLocationArgument.id()).suggests(SuggestionProviders.AVAILABLE_SOUNDS);

        for(SoundSource soundsource : SoundSource.values()) {
            requiredargumentbuilder.then(source(soundsource));
        }
        return LiteralArgumentBuilder.<CommandSourceStack>literal("sound")
                .requires(stack -> stack.hasPermission(CommandConfig.COMMAND_SOUND.get()))
                .then(Commands.argument("players", EntityArgument.player())
                        .then(requiredargumentbuilder)
                );
    }
    static Collection<ServerPlayer> getPlayers(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return EntityArgument.getPlayers(context, "players");
    }
    static ResourceLocation getSound(CommandContext<CommandSourceStack> context) {
        return ResourceLocationArgument.getId(context, "sound");
    }
    private static LiteralArgumentBuilder<CommandSourceStack> source(SoundSource source) {
        return Commands.literal(source.getName())
                .executes(context -> playStatic(context.getSource(), getPlayers(context), getSound(context), source, 1F, 1F, false, 0))
                .then(Commands.literal("static")
                        .executes(context -> playStatic(context.getSource(), getPlayers(context), getSound(context), source, 1F, 1F, false, 0))
                        .then(Commands.argument("volume", FloatArgumentType.floatArg(0F, 16F))
                                .executes(context -> playStatic(context.getSource(), getPlayers(context), getSound(context), source, FloatArgumentType.getFloat(context, "volume"), 1F, false, 0))
                                .then(Commands.argument("pitch", FloatArgumentType.floatArg(0F, 16F))
                                        .executes(context -> playStatic(context.getSource(), getPlayers(context), getSound(context), source, FloatArgumentType.getFloat(context, "volume"), FloatArgumentType.getFloat(context, "pitch"), false, 0))
                                        .then(Commands.argument("repeat", BoolArgumentType.bool())
                                                .executes(context -> playStatic(context.getSource(), getPlayers(context), getSound(context), source, FloatArgumentType.getFloat(context, "volume"), FloatArgumentType.getFloat(context, "pitch"), BoolArgumentType.getBool(context, "repeat"), 0))
                                                .then(Commands.argument("repeatDelay", IntegerArgumentType.integer(0))
                                                        .executes(context -> playStatic(context.getSource(), getPlayers(context), getSound(context), source, FloatArgumentType.getFloat(context, "volume"), FloatArgumentType.getFloat(context, "pitch"), BoolArgumentType.getBool(context, "repeat"), IntegerArgumentType.getInteger(context, "repeatDelay")))
                                                )
                                        )
                                )
                        )
                )
                .then(Commands.literal("stereo")
                        .executes(context -> {
                            ServerPlayer player = getPlayers(context).iterator().next();
                            BlockPos pos = BlockPos.ZERO;
                            if (player != null) pos = player.blockPosition();
                            return playStereo(context.getSource(), getPlayers(context), getSound(context), source, 1F, 1F, false, 0, pos, false);
                        })
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .executes(context -> playStereo(context.getSource(), getPlayers(context), getSound(context), source, 1F, 1F, false, 0, BlockPosArgument.getBlockPos(context, "pos"), true))
                                .then(Commands.argument("useDistance", BoolArgumentType.bool())
                                        .executes(context -> playStereo(context.getSource(), getPlayers(context), getSound(context), source, 1F, 1F, false, 0, BlockPosArgument.getBlockPos(context, "pos"), BoolArgumentType.getBool(context, "useDistance")))
                                        .then(Commands.argument("volume", FloatArgumentType.floatArg(0F, 16F))
                                                .executes(context -> playStereo(context.getSource(), getPlayers(context), getSound(context), source, FloatArgumentType.getFloat(context, "volume"), 1F, false, 0, BlockPosArgument.getBlockPos(context, "pos"), BoolArgumentType.getBool(context, "useDistance")))
                                                .then(Commands.argument("pitch", FloatArgumentType.floatArg(0F, 16F))
                                                        .executes(context -> playStereo(context.getSource(), getPlayers(context), getSound(context), source, FloatArgumentType.getFloat(context, "volume"), FloatArgumentType.getFloat(context, "pitch"), false, 0, BlockPosArgument.getBlockPos(context, "pos"), BoolArgumentType.getBool(context, "useDistance")))
                                                        .then(Commands.argument("repeat", BoolArgumentType.bool())
                                                                .executes(context -> playStereo(context.getSource(), getPlayers(context), getSound(context), source, FloatArgumentType.getFloat(context, "volume"), FloatArgumentType.getFloat(context, "pitch"), BoolArgumentType.getBool(context, "repeat"), 0, BlockPosArgument.getBlockPos(context, "pos"), BoolArgumentType.getBool(context, "useDistance")))
                                                                .then(Commands.argument("repeatDelay", IntegerArgumentType.integer(0))
                                                                        .executes(context -> playStereo(context.getSource(), getPlayers(context), getSound(context), source, FloatArgumentType.getFloat(context, "volume"), FloatArgumentType.getFloat(context, "pitch"), BoolArgumentType.getBool(context, "repeat"), IntegerArgumentType.getInteger(context, "repeatDelay"), BlockPosArgument.getBlockPos(context, "pos"), BoolArgumentType.getBool(context, "useDistance")))
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                );
    }
    private static int playStatic(CommandSourceStack sourceStack, Collection<ServerPlayer> targets, ResourceLocation sound, SoundSource source, float volume, float pitch, boolean repeat, int repeatDelay) {
        if (targets.size() == 1) {
            ServerPlayer target = targets.iterator().next();
            PacketHandler.sendToPlayer(new S2CCompletelySoundPacket.Static(sound, source, volume, pitch, sourceStack.getLevel().getRandom().nextLong(), repeat, repeatDelay), target);
            sourceStack.sendSuccess(()-> Component.translatable("commands.endinglib.message.sound.static.single", target.getDisplayName(), sound.toString()), false);
        } else {
            for (ServerPlayer target: targets)
                PacketHandler.sendToPlayer(new S2CCompletelySoundPacket.Static(sound, source, volume, pitch, sourceStack.getLevel().getRandom().nextLong(), repeat, repeatDelay), target);
            sourceStack.sendSuccess(()-> Component.translatable("commands.endinglib.message.sound.static.multiple", targets.size(), sound.toString()), false);
            return targets.size();
        }
        return 1;
    }
    private static int playStereo(CommandSourceStack sourceStack, Collection<ServerPlayer> targets, ResourceLocation sound, SoundSource source, float volume, float pitch, boolean repeat, int repeatDelay, BlockPos pos, boolean useDistance) {
        if (targets.size() == 1) {
            ServerPlayer target = targets.iterator().next();
            PacketHandler.sendToPlayer(new S2CCompletelySoundPacket.Stereo(sound, source, volume, pitch, sourceStack.getLevel().getRandom().nextLong(), repeat, repeatDelay, pos, useDistance), target);
            sourceStack.sendSuccess(()-> Component.translatable("commands.endinglib.message.sound.stereo.single", target.getDisplayName(), sound.toString()), false);
        } else {
            for (ServerPlayer target: targets)
                PacketHandler.sendToPlayer(new S2CCompletelySoundPacket.Stereo(sound, source, volume, pitch, sourceStack.getLevel().getRandom().nextLong(), repeat, repeatDelay, pos, useDistance), target);
            sourceStack.sendSuccess(()-> Component.translatable("commands.endinglib.message.sound.stereo.multiple", targets.size(), sound.toString()), false);
            return targets.size();
        }
        return 1;
    }
}
