package com.mega.endinglib.mixin.dev;

import com.mega.endinglib.api.client.text.TextColorUtils;
import com.mega.endinglib.util.annotation.DeprecatedMixin;
import com.mega.endinglib.util.annotation.DevEnvMixin;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ShieldItem.class)
@DevEnvMixin
@DeprecatedMixin
public abstract class ShieldItemMixin extends Item {
    ShieldItemMixin(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public Component getName(ItemStack p_41458_) {
        return TextColorUtils.component();
    }

    @Inject(method = "appendHoverText", at = @At("TAIL"))
    private void aht(ItemStack p_43094_, Level p_43095_, List<Component> p_43096_, TooltipFlag p_43097_, CallbackInfo ci) {
        p_43096_.add(TextColorUtils.component());
    }
}
