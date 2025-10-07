package com.mega.endinglib.common.command.entity.player;

import com.mega.endinglib.api.client.cmc.LoreHelper;
import com.mega.endinglib.common.command.argument.InteractionHandArgument;
import com.mega.endinglib.common.config.ServerConfig;
import com.mega.endinglib.mixin.accessor.AccessorCooldownsInstance;
import com.mega.endinglib.mixin.accessor.AccessorItemCooldowns;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public class CooldownCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandBuildContext buildContext) {
        return LiteralArgumentBuilder.<CommandSourceStack>literal("cooldown")
                .requires(stack -> stack.hasPermission(ServerConfig.COMMAND_COOLDOWN.get()))
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.literal("increase")
                                .then(Commands.literal("hand")
                                        .then(Commands.argument("hand", InteractionHandArgument.hand())
                                                .then(Commands.argument("ticks", IntegerArgumentType.integer(0))
                                                        .executes(context -> increase(
                                                                context.getSource(),
                                                                EntityArgument.getPlayer(context, "player"),
                                                                InteractionHandArgument.getHand(context, "hand"),
                                                                IntegerArgumentType.getInteger(context, "ticks")
                                                        ))
                                                )
                                        )
                                )
                                .then(Commands.literal("item")
                                        .then(Commands.argument("item", ItemArgument.item(buildContext))
                                                .then(Commands.argument("ticks", IntegerArgumentType.integer(0))
                                                        .executes(context -> increase(
                                                                context.getSource(),
                                                                EntityArgument.getPlayer(context, "player"),
                                                                ItemArgument.getItem(context, "item"),
                                                                IntegerArgumentType.getInteger(context, "ticks")
                                                        ))
                                                )
                                        )
                                )
                        )
                        .then(Commands.literal("decrease")
                                .then(Commands.literal("hand")
                                        .then(Commands.argument("hand", InteractionHandArgument.hand())
                                                .then(Commands.argument("ticks", IntegerArgumentType.integer(0))
                                                        .executes(context -> decrease(
                                                                context.getSource(),
                                                                EntityArgument.getPlayer(context, "player"),
                                                                InteractionHandArgument.getHand(context, "hand"),
                                                                IntegerArgumentType.getInteger(context, "ticks")
                                                        ))
                                                )
                                        )
                                )
                                .then(Commands.literal("item")
                                        .then(Commands.argument("item", ItemArgument.item(buildContext))
                                                .then(Commands.argument("ticks", IntegerArgumentType.integer(0))
                                                        .executes(context -> decrease(
                                                                context.getSource(),
                                                                EntityArgument.getPlayer(context, "player"),
                                                                ItemArgument.getItem(context, "item"),
                                                                IntegerArgumentType.getInteger(context, "ticks")
                                                        ))
                                                )
                                        )
                                )
                        )
                        .then(Commands.literal("remove")
                                .executes(context -> remove(
                                        context.getSource(),
                                        EntityArgument.getPlayer(context, "player"))
                                )
                                .then(Commands.literal("hand")
                                        .then(Commands.argument("hand", InteractionHandArgument.hand())
                                                .executes(context -> remove(
                                                        context.getSource(),
                                                        EntityArgument.getPlayer(context, "player"),
                                                        InteractionHandArgument.getHand(context, "hand")
                                                ))

                                        )
                                )
                                .then(Commands.literal("item")
                                        .then(Commands.argument("item", ItemArgument.item(buildContext))
                                                .executes(context -> remove(
                                                        context.getSource(),
                                                        EntityArgument.getPlayer(context, "player"),
                                                        ItemArgument.getItem(context, "item")
                                                ))

                                        )
                                )
                        )
                        .then(Commands.literal("get")
                                .then(Commands.literal("hand")
                                        .then(Commands.argument("hand", InteractionHandArgument.hand())
                                                .executes(context -> get(
                                                        context.getSource(),
                                                        EntityArgument.getPlayer(context, "player"),
                                                        InteractionHandArgument.getHand(context, "hand")
                                                ))

                                        )
                                )
                                .then(Commands.literal("item")
                                        .then(Commands.argument("item", ItemArgument.item(buildContext))
                                                .executes(context -> get(
                                                        context.getSource(),
                                                        EntityArgument.getPlayer(context, "player"),
                                                        ItemArgument.getItem(context, "item")
                                                ))

                                        )
                                )
                        )
                );
    }

    private static int increase(CommandSourceStack sourceStack, ServerPlayer player, InteractionHand hand, int ticks) {
        ItemStack itemStack;
        if (!(itemStack = player.getItemInHand(hand)).isEmpty()) {
            player.getCooldowns().addCooldown(itemStack.getItem(), ticks);
        }
        sourceStack.sendSuccess(() -> Component.translatable("commands.endinglib.message.cooldown.increase.hand", player.getDisplayName(), LoreHelper.withCopyEnum("tooltip.endinglib.", hand), LoreHelper.number(ticks, ChatFormatting.GOLD)), false);
        return ticks;
    }

    private static int increase(CommandSourceStack sourceStack, ServerPlayer player, ItemInput item, int ticks) {
        Item item1 = item.getItem();
        player.getCooldowns().addCooldown(item1, ticks);
        sourceStack.sendSuccess(() -> Component.translatable("commands.endinglib.message.cooldown.increase.item", player.getDisplayName(), LoreHelper.withCopy(Component.empty().append(new ItemStack(item1).getDisplayName()), Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item1)).toString()), LoreHelper.number(ticks, ChatFormatting.GOLD)), false);
        return ticks;
    }

    private static int decrease(CommandSourceStack sourceStack, ServerPlayer player, InteractionHand hand, int ticks) {
        ItemStack itemStack;
        if (!(itemStack = player.getItemInHand(hand)).isEmpty()) {
            player.getCooldowns().addCooldown(itemStack.getItem(), -ticks);
        }
        sourceStack.sendSuccess(() -> Component.translatable("commands.endinglib.message.cooldown.decrease.hand", player.getDisplayName(), LoreHelper.withCopyEnum("tooltip.endinglib.", hand), LoreHelper.number(ticks, ChatFormatting.GOLD)), false);
        return ticks;
    }

    private static int decrease(CommandSourceStack sourceStack, ServerPlayer player, ItemInput item, int ticks) {
        Item item1 = item.getItem();
        player.getCooldowns().addCooldown(item1, -ticks);
        sourceStack.sendSuccess(() -> Component.translatable("commands.endinglib.message.cooldown.decrease.item", player.getDisplayName(), LoreHelper.withCopy(Component.empty().append(new ItemStack(item1).getDisplayName()), Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item1)).toString()), LoreHelper.number(ticks, ChatFormatting.GOLD)), false);
        return ticks;
    }

    private static int remove(CommandSourceStack sourceStack, ServerPlayer player, InteractionHand hand) {
        ItemStack itemStack;
        if (!(itemStack = player.getItemInHand(hand)).isEmpty()) {
            player.getCooldowns().removeCooldown(itemStack.getItem());
        }
        sourceStack.sendSuccess(() -> Component.translatable("commands.endinglib.message.cooldown.clear.hand", player.getDisplayName(), LoreHelper.withCopyEnum("tooltip.endinglib.", hand)), false);
        return 1;
    }

    private static int remove(CommandSourceStack sourceStack, ServerPlayer player, ItemInput item) {
        Item item1 = item.getItem();
        player.getCooldowns().removeCooldown(item1);
        sourceStack.sendSuccess(() -> Component.translatable("commands.endinglib.message.cooldown.clear.item", player.getDisplayName(), LoreHelper.withCopy(Component.empty().append(new ItemStack(item1).getDisplayName()), Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item1)).toString())), false);
        return 1;
    }

    private static int remove(CommandSourceStack sourceStack, ServerPlayer player) {
        AccessorItemCooldowns cooldowns = (AccessorItemCooldowns) player.getCooldowns();
        for (var entry : cooldowns.getCooldowns().entrySet()) {
            player.getCooldowns().removeCooldown(entry.getKey());
        }
        sourceStack.sendSuccess(() -> Component.translatable("commands.endinglib.message.cooldown.clear", player.getDisplayName()), false);
        return 1;
    }

    private static int get(CommandSourceStack sourceStack, ServerPlayer player, InteractionHand hand) {
        ItemStack itemStack;
        if (!(itemStack = player.getItemInHand(hand)).isEmpty()) {
            ItemCooldowns cooldowns = player.getCooldowns();
            AccessorItemCooldowns aic = (AccessorItemCooldowns) cooldowns;
            AccessorCooldownsInstance cooldownsInstance = (AccessorCooldownsInstance) aic.getCooldowns().get(itemStack.getItem());
            return Math.max(0, cooldownsInstance.getEndTime() - aic.getTickCount());
        }
        return 0;
    }

    private static int get(CommandSourceStack sourceStack, ServerPlayer player, ItemInput item) {
        Item item1 = item.getItem();
        ItemCooldowns cooldowns = player.getCooldowns();
        AccessorItemCooldowns aic = (AccessorItemCooldowns) cooldowns;
        AccessorCooldownsInstance cooldownsInstance = (AccessorCooldownsInstance) aic.getCooldowns().get(item1);
        return Math.max(0, cooldownsInstance.getEndTime() - aic.getTickCount());
    }
}
