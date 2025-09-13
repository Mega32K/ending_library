package com.mega.endinglib.mixin.advanced.data_expand;

import com.mega.endinglib.util.annotation.DeprecatedMixin;
import com.mega.endinglib.util.mixin.data_expand.ExtraServerPlayerItf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
@DeprecatedMixin
public abstract class ServerPlayerMixin implements ExtraServerPlayerItf {
    @Unique
    private final short[] endingLibrary$clientInput = new short[] {0,0,0,0,0,0};

    @Override
    public short[] endinglib$getClientInputData() {
        return this.endingLibrary$clientInput;
    }
    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    private void tempInputInject(CompoundTag p_9197_, CallbackInfo ci) {
        CompoundTag tag = new CompoundTag();
        tag.putShort("attack", endingLibrary$clientInput[0]);
        tag.putShort("use", endingLibrary$clientInput[1]);
        tag.putShort("space", endingLibrary$clientInput[2]);
        tag.putShort("sneak", endingLibrary$clientInput[3]);
        tag.putShort("ctrl", endingLibrary$clientInput[4]);
        tag.putShort("mid", endingLibrary$clientInput[5]);
        p_9197_.put("Options", tag);
    }
}
