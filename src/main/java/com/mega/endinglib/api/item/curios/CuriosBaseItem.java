package com.mega.endinglib.api.item.curios;

import com.mega.endinglib.api.item.NameCenteredItem;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

@SuppressWarnings("UnstableApiUsage")
public class CuriosBaseItem extends NameCenteredItem implements ICurioItem {
    public CuriosBaseItem(Properties properties) {
        super(properties);
    }

    @NotNull
    public ICurio.@NotNull SoundInfo getEquipSound(SlotContext slotContext, ItemStack stack) {
        return new ICurio.SoundInfo(SoundEvents.ARMOR_EQUIP_CHAIN, 1.0F, 1.0F);
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return !CuriosApi.getCuriosHelper().findEquippedCurio(this, slotContext.entity()).isPresent();
    }

    @Override
    public boolean canRightClickEquip(ItemStack stack) {
        return true;
    }
}
