package com.mega.endinglib.mixin.advanced.data_expand.component.custom_data;

import com.mega.endinglib.api.item.component.DataComponents;
import com.mega.endinglib.api.item.component.ItemComponentManager;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.commands.data.DataCommands;
import net.minecraft.server.commands.data.EntityDataAccessor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Arrow.class)
public abstract class ArrowMixin extends AbstractArrow {
    ArrowMixin(EntityType<? extends AbstractArrow> p_36721_, Level p_36722_) {
        super(p_36721_, p_36722_);
    }

    @Shadow public abstract void addAdditionalSaveData(@NotNull CompoundTag p_36881_);

    @Inject(method = "setEffectsFromItem", at = @At("HEAD"))
    private void setCustomDataFromItem(ItemStack itemStack, CallbackInfo ci) {
        CompoundTag tag = ItemComponentManager.get(itemStack, DataComponents.CUSTOM_DATA);
        if (tag != null) {
            EntityDataAccessor dataAccessor = new EntityDataAccessor(this);
            try {
                dataAccessor.setData(dataAccessor.getData().merge(tag));
            } catch (CommandSyntaxException ignore) {}
        }
    }
}
