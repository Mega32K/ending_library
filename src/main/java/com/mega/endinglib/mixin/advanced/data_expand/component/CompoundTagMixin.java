package com.mega.endinglib.mixin.advanced.data_expand.component;

import com.mega.endinglib.util.mixin.data_expand.ICompoundTagMergeCaller;
import com.mega.endinglib.util.mixin.data_expand.InjectCompoundTag;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CompoundTag.class)
public abstract class CompoundTagMixin implements InjectCompoundTag {
    @Unique
    @Nullable
    private ICompoundTagMergeCaller owner;

    @Override
    public void setStoredOwner(ICompoundTagMergeCaller owner) {
        this.owner = owner;
    }

    @Override
    public @Nullable ICompoundTagMergeCaller getStoredOwner() {
        return owner;
    }
    @Inject(method = "merge", at = @At("RETURN"))
    private void mergerCallOwnerOperation(CompoundTag p_128392_, CallbackInfoReturnable<CompoundTag> cir) {
        if (this.owner != null)
            this.owner.mergedTagCallOwnerOperation(cir.getReturnValue());
    }
}
