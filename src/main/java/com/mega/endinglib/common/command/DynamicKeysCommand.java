package com.mega.endinglib.common.command;

import com.mega.endinglib.api.client.cmc.LoreHelper;
import com.mega.endinglib.common.config.CommandConfig;
import com.mega.endinglib.common.data.DynamicKeyMapping;
import com.mega.endinglib.common.data.EndingLibrarySavedData;
import com.mega.endinglib.common.eventhandler.CommonEventHandler;
import com.mega.endinglib.server.resource.DynamicKeyMappingReloadListener;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;

public class DynamicKeysCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return LiteralArgumentBuilder.<CommandSourceStack>literal("dynamicKeys")
                .requires(stack -> stack.hasPermission(CommandConfig.COMMAND_DYNAMIC_KEYS.get()))
                .then(Commands.literal("enable")
                        .then(Commands.argument("id", ResourceLocationArgument.id())
                                .suggests((context, builder) -> {
                                    EndingLibrarySavedData savedData = EndingLibrarySavedData.getInstance(context.getSource().getServer());
                                    return SharedSuggestionProvider.suggest(DynamicKeyMappingReloadListener.DYNAMIC_KEYS.values()
                                            .stream()
                                            .filter(savedData::isKeyMappingDisabled)
                                            .map(DynamicKeyMapping::getKeyId)
                                            .map(ResourceLocation::toString), builder);
                                })
                                .executes(context -> enable(context.getSource(), ResourceLocationArgument.getId(context, "id")))
                        )
                )
                .then(Commands.literal("disable")
                        .then(Commands.argument("id", ResourceLocationArgument.id())
                                .suggests((context, builder) -> {
                                    EndingLibrarySavedData savedData = EndingLibrarySavedData.getInstance(context.getSource().getServer());
                                    return SharedSuggestionProvider.suggest(DynamicKeyMappingReloadListener.DYNAMIC_KEYS.values()
                                            .stream()
                                            .filter(savedData::isKeyMappingEnabled)
                                            .map(DynamicKeyMapping::getKeyId)
                                            .map(ResourceLocation::toString), builder);
                                })
                                .executes(context -> disable(context.getSource(), ResourceLocationArgument.getId(context, "id")))
                        )
                )
                .then(Commands.literal("sync")
                        .executes(context -> sync(context.getSource()))
                );
    }
    private static int enable(CommandSourceStack sourceStack, ResourceLocation id) {
        MinecraftServer server = sourceStack.getServer();
        int result = EndingLibrarySavedData.getInstance(server).enableDynamicKeyMapping(id) ? 1 : 0;
        if (result == 1)
            sourceStack.sendSuccess(()-> Component.translatable("commands.endinglib.message.dynamic_key.enable", LoreHelper.wrap(LoreHelper.withCopy(Component.literal(id.toString()).withStyle(ChatFormatting.GREEN), id.toString()))), false);
        return result;
    }
    private static int disable(CommandSourceStack sourceStack, ResourceLocation id) {
        MinecraftServer server = sourceStack.getServer();
        int result = EndingLibrarySavedData.getInstance(server).disableDynamicKeyMapping(id) ? 1 : 0;
        if (result == 1)
            sourceStack.sendSuccess(()-> Component.translatable("commands.endinglib.message.dynamic_key.disable", LoreHelper.wrap(LoreHelper.withCopy(Component.literal(id.toString()).withStyle(ChatFormatting.GREEN), id.toString()))), false);
        return result;
    }
    private static int sync(CommandSourceStack sourceStack) {
        EndingLibrarySavedData savedData = EndingLibrarySavedData.getInstance(sourceStack.getServer());
        int r = CommonEventHandler.syncDynamicKeyMappings(savedData, sourceStack.getServer().getPlayerList().getPlayers());
        sourceStack.sendSuccess(()-> Component.translatable("commands.endinglib.message.dynamic_key.sync"), false);
        return r;
    }
}
