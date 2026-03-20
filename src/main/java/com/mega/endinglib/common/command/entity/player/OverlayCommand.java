package com.mega.endinglib.common.command.entity.player;

import com.mega.endinglib.api.client.cmc.LoreHelper;
import com.mega.endinglib.common.command.argument.GuiOverlayArgument;
import com.mega.endinglib.common.command.argument.PoseArgument;
import com.mega.endinglib.common.config.CommandConfig;
import com.mega.endinglib.common.data.EndingLibrarySavedData;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.Set;

public class OverlayCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return LiteralArgumentBuilder.<CommandSourceStack>literal("overlay")
                .requires(stack -> stack.hasPermission(CommandConfig.COMMAND_OVERLAY.get()))
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.literal("display")
                                .then(Commands.literal("enable")
                                        .then(Commands.argument("overlay", GuiOverlayArgument.overlay())
                                                .executes(context -> enable(context.getSource(), EntityArgument.getPlayer(context, "player"), GuiOverlayArgument.getOverlayId(context, "overlay")))
                                        )
                                )
                                .then(Commands.literal("disable")
                                        .then(Commands.argument("overlay", GuiOverlayArgument.overlay())
                                                .executes(context -> disable(context.getSource(), EntityArgument.getPlayer(context, "player"), GuiOverlayArgument.getOverlayId(context, "overlay")))
                                        )
                                )
                                .then(Commands.literal("list")
                                        .executes(context -> list(context.getSource(), EntityArgument.getPlayer(context, "player")))
                                )
                        )
                );
    }
    private static int enable(CommandSourceStack sourceStack, ServerPlayer player, ResourceLocation id) {
        MinecraftServer server = sourceStack.getServer();
        EndingLibrarySavedData savedData = EndingLibrarySavedData.getInstance(server);
        int result = savedData.removeDisabledOverlay(player, id) ? 1 : 0;
        if (result == 1) {
            sourceStack.sendSuccess(()-> Component.translatable("commands.endinglib.message.overlay.display.enable", player.getDisplayName(), Component.literal(id.toString()).withStyle(ChatFormatting.GREEN)), false);
        }
        return result;
    }
    private static int disable(CommandSourceStack sourceStack, ServerPlayer player, ResourceLocation id) {
        MinecraftServer server = sourceStack.getServer();
        EndingLibrarySavedData savedData = EndingLibrarySavedData.getInstance(server);
        int result = savedData.addDisabledOverlay(player, id) ? 1 : 0;
        if (result == 1) {
            sourceStack.sendSuccess(()-> Component.translatable("commands.endinglib.message.overlay.display.disable", player.getDisplayName(), Component.literal(id.toString()).withStyle(ChatFormatting.GREEN)), false);
        }
        return result;
    }
    private static int list(CommandSourceStack sourceStack, ServerPlayer player) {
        MinecraftServer server = sourceStack.getServer();
        EndingLibrarySavedData savedData = EndingLibrarySavedData.getInstance(server);
        Set<ResourceLocation> set = savedData.getOrPutPlayerDisabledOverlays(player);
        if (!set.isEmpty())
            sourceStack.sendSuccess(()-> Component.translatable("commands.endinglib.message.overlay.display.list", player.getDisplayName()), false);
        sourceStack.sendSuccess(()-> LoreHelper.array(set, id -> {
            String langKey = "tooltip.endinglib.hud.%s.%s".formatted(id.getNamespace(), id.getPath());
            return Component.literal(id.toString()).withStyle(ChatFormatting.GREEN)
                    .withStyle(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable(langKey))));
        }, ChatFormatting.GRAY), false);
        return set.size();
    }
}
