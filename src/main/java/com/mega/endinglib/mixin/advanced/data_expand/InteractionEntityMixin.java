package com.mega.endinglib.mixin.advanced.data_expand;

import com.mega.endinglib.api.data.CompoundTagUtils;
import com.mega.endinglib.mixin.capability.EntityMixin;
import com.mega.endinglib.util.mixin.data_expand.ExtraEntity;
import net.minecraft.commands.CommandSigningContext;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.TaskChainer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(Interaction.class)
public abstract class InteractionEntityMixin extends Entity {
    @Unique
    private String interactionCommand;
    @Unique
    private String attackCommand;
    @Unique
    private String tickingCommand;

    InteractionEntityMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    private void writeExtraAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        if (this.interactionCommand != null && !this.interactionCommand.isEmpty()) {
            tag.putString("InteractionCommand", this.interactionCommand);
        }
        if (this.attackCommand != null && !this.attackCommand.isEmpty()) {
            tag.putString("AttackCommand", this.attackCommand);
        }
        if (this.tickingCommand != null && !this.tickingCommand.isEmpty()) {
            tag.putString("TickingCommand", this.tickingCommand);
        }
    }
    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    private void readExtraAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        if (CompoundTagUtils.containsString(tag, "InteractionCommand"))
            this.interactionCommand = tag.getString("InteractionCommand");
        if (CompoundTagUtils.containsString(tag, "AttackCommand"))
            this.attackCommand = tag.getString("AttackCommand");
        if (CompoundTagUtils.containsString(tag, "TickingCommand"))
            this.tickingCommand = tag.getString("TickingCommand");
    }
    @Inject(method = "interact", at = @At(value = "RETURN", ordinal = 1, shift = At.Shift.BEFORE))
    private void onInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (this.level() instanceof ServerLevel sl) {
            if (this.interactionCommand != null && !this.interactionCommand.isEmpty())
                sl.getServer().getCommands().performPrefixedCommand(endingLibrary$customCSS(sl), this.interactionCommand);
        }
    }
    @Inject(method = "skipAttackInteraction", at = @At(value = "RETURN", ordinal = 0, shift = At.Shift.BEFORE))
    private void attackCommand(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (this.level() instanceof ServerLevel sl) {
            if (this.attackCommand != null && !this.attackCommand.isEmpty()) {
                sl.getServer().getCommands().performPrefixedCommand(endingLibrary$customCSS(sl).withMaximumPermission(2), this.attackCommand);
            }
        }
    }
    @Inject(method = "tick", at = @At("HEAD"))
    private void tickCommand(CallbackInfo ci) {
        if (this.level() instanceof ServerLevel sl) {
            if (this.tickingCommand != null && !this.tickingCommand.isEmpty())
                sl.getServer().getCommands().performPrefixedCommand(endingLibrary$customCSS(sl), this.tickingCommand);
        }
    }
    @Unique
    private CommandSourceStack endingLibrary$customCSS(ServerLevel serverLevel) {
        return endingLibrary$newCSS(this, this.position(), this.getRotationVector(), serverLevel, Math.max(this.getPermissionLevel(), 2), this.getName().getString(), this.getDisplayName(), serverLevel.getServer(), this);
    }
    @Unique
    private static CommandSourceStack endingLibrary$newCSS(CommandSource p_81302_, Vec3 p_81303_, Vec2 p_81304_, ServerLevel p_81305_, int p_81306_, String p_81307_, Component p_81308_, MinecraftServer p_81309_, @Nullable Entity p_81310_) {
        return new CommandSourceStack(p_81302_, p_81303_, p_81304_, p_81305_, p_81306_, p_81307_, p_81308_, p_81309_, p_81310_, true, (p_81361_, p_81362_, p_81363_) -> {
        }, EntityAnchorArgument.Anchor.FEET, CommandSigningContext.ANONYMOUS, TaskChainer.immediate(p_81309_), (p_280930_) -> {
        });
    }
}
