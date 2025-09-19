package com.mega.endinglib.mixin.data_expand.component;

import com.mega.endinglib.api.item.component.ItemComponentManager;
import com.mega.endinglib.common.command.argument.ItemComponentArgument;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.GiveCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(GiveCommand.class)
public abstract class GiveCommandMixin {
    @Shadow public static void register(CommandDispatcher<CommandSourceStack> p_214446_, CommandBuildContext p_214447_) {

    }

    @Inject(method = "register", at = @At("TAIL"))
    private static void register(CommandDispatcher<CommandSourceStack> p_214446_, CommandBuildContext p_214447_, CallbackInfo ci) {
        registerGiveComponent(p_214446_, p_214447_);
    }
    @Unique
    private static void registerGiveComponent(CommandDispatcher<CommandSourceStack> p_214446_, CommandBuildContext p_214447_) {
        p_214446_.register(Commands.literal("give")
                .requires((p_137777_) -> p_137777_.hasPermission(2))
                .then(Commands.argument("targets", EntityArgument.players())
                        .then(Commands.argument("item", ItemArgument.item(p_214447_))
                                .then(Commands.argument("count", IntegerArgumentType.integer(1))
                                        .then(Commands.argument("component", ItemComponentArgument.component())
                                                .executes(context -> giveComponentItem(context.getSource(), ItemArgument.getItem(context, "item"), EntityArgument.getPlayers(context, "targets"), IntegerArgumentType.getInteger(context, "count"), ItemComponentArgument.getItemComponent(context, "component"))))
                                        )
                                )
                        )
                );
    }
    @Unique
    private static int giveComponentItem(CommandSourceStack stack, ItemInput itemInput, Collection<ServerPlayer> players, int count, CompoundTag component) throws CommandSyntaxException {
        int i = itemInput.getItem().getMaxStackSize();
        int j = i * 100;
        ItemStack itemstack = itemInput.createItemStack(count, false);
        if (count > j) {
            stack.sendFailure(Component.translatable("commands.give.failed.toomanyitems", j, itemstack.getDisplayName()));
            return 0;
        } else {
            for(ServerPlayer serverplayer : players) {
                int k = count;

                while(k > 0) {
                    int l = Math.min(i, k);
                    k -= l;
                    ItemStack itemstack1 = itemInput.createItemStack(l, false);
                    CompoundTag tag = new CompoundTag();
                    tag.put(ItemComponentManager.HEAD, component);
                    itemstack1.setTag(tag);
                    boolean flag = serverplayer.getInventory().add(itemstack1);
                    if (flag && itemstack1.isEmpty()) {
                        itemstack1.setCount(1);
                        ItemEntity itementity1 = serverplayer.drop(itemstack1, false);
                        if (itementity1 != null) {
                            itementity1.makeFakeItem();
                        }

                        serverplayer.level().playSound(null, serverplayer.getX(), serverplayer.getY(), serverplayer.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((serverplayer.getRandom().nextFloat() - serverplayer.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                        serverplayer.containerMenu.broadcastChanges();
                    } else {
                        ItemEntity itementity = serverplayer.drop(itemstack1, false);
                        if (itementity != null) {
                            itementity.setNoPickUpDelay();
                            itementity.setTarget(serverplayer.getUUID());
                        }
                    }
                }
            }

            if (players.size() == 1) {
                stack.sendSuccess(() -> Component.translatable("commands.give.success.single", count, itemstack.getDisplayName(), players.iterator().next().getDisplayName()), true);
            } else {
                stack.sendSuccess(() -> Component.translatable("commands.give.success.single", count, itemstack.getDisplayName(), players.size()), true);
            }

            return players.size();
        }
    }
}
