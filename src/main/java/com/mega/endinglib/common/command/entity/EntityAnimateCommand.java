package com.mega.endinglib.common.command.entity;

import com.mega.endinglib.api.client.cmc.LoreHelper;
import com.mega.endinglib.common.command.argument.VanillaAnimation;
import com.mega.endinglib.common.command.argument.VanillaAnimationArgument;
import com.mega.endinglib.common.config.CommandConfig;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.entity.Entity;

import java.util.Collection;
import java.util.Locale;

public class EntityAnimateCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return LiteralArgumentBuilder.<CommandSourceStack>literal("entityAnimate")
                .requires(stack -> stack.hasPermission(CommandConfig.COMMAND_ENTITY_ANIMATE.get()))
                .then(Commands.argument("entities", EntityArgument.entities())
                        .then(Commands.argument("animation", VanillaAnimationArgument.animation())
                                .executes(context -> play(context.getSource(), EntityArgument.getEntities(context, "entities"), VanillaAnimationArgument.getAnimation(context, "animation")))
                        )
                );
    }
    public static int play(CommandSourceStack sourceStack, Collection<? extends Entity> entities, VanillaAnimation animation) {
        for(Entity entity : entities) {
            ClientboundAnimatePacket clientboundanimatepacket = new ClientboundAnimatePacket(entity, animation.id);
            ServerChunkCache serverchunkcache = sourceStack.getLevel().getChunkSource();
            serverchunkcache.broadcastAndSend(entity, clientboundanimatepacket);
        }

        if (entities.size() == 1) {
            sourceStack.sendSuccess(() -> Component.translatable("commands.endinglib.message.vanilla_animation.success.single", entities.iterator().next().getDisplayName(), Component.translatable("commands.endinglib.message.vanilla_animation."+animation.name().toUpperCase(Locale.ROOT))), true);
        } else {
            sourceStack.sendSuccess(() -> Component.translatable("commands.endinglib.message.vanilla_animation.success.multiple", LoreHelper.number(entities.size(), ChatFormatting.GOLD), Component.translatable("commands.endinglib.message.vanilla_animation."+animation.name().toUpperCase(Locale.ROOT))), true);
        }
        return entities.size();
    }
}
