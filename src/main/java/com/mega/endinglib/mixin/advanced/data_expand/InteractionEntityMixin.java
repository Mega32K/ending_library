package com.mega.endinglib.mixin.advanced.data_expand;

import com.mega.endinglib.api.data.CompoundTagUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
                sl.getServer().getCommands().performPrefixedCommand(this.createCommandSourceStack().withMaximumPermission(2), this.interactionCommand);
        }
    }
    @Inject(method = "skipAttackInteraction", at = @At(value = "RETURN", ordinal = 0, shift = At.Shift.BEFORE))
    private void attackCommand(Entity entity, CallbackInfoReturnable<Boolean> cir) {if (this.level() instanceof ServerLevel sl) {
        if (this.attackCommand != null && !this.attackCommand.isEmpty())
            sl.getServer().getCommands().performPrefixedCommand(this.createCommandSourceStack().withMaximumPermission(2), this.attackCommand);
    }
    }
    @Inject(method = "tick", at = @At("HEAD"))
    private void tickCommand(CallbackInfo ci) {
        if (this.level() instanceof ServerLevel sl) {
            if (this.tickingCommand != null && !this.tickingCommand.isEmpty())
                sl.getServer().getCommands().performPrefixedCommand(this.createCommandSourceStack().withMaximumPermission(2), this.tickingCommand);
        }
    }
}
