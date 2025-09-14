package com.mega.endinglib.api.item.fake_component;

import com.mega.endinglib.api.data.CompoundTagUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class ItemComponentManager {
    public static final String HEAD = "FakeComponent";
    public static final String COM_ITEM_MODEL = "item_model";

    static {

    }

    private ItemStack itemStack;

    public ItemComponentManager(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    private CompoundTag forceGetComponent() {
        return itemStack.getOrCreateTag().getCompound(HEAD);
    }

    public Optional<CompoundTag> getComponent() {
        CompoundTag tag = itemStack.getTag();
        if (tag != null) {
            return Optional.of(tag.getCompound(HEAD));
        } else return Optional.empty();
    }

    public void SetItemModel(String modelRL) {
        CompoundTag component = forceGetComponent();
        component.putString(COM_ITEM_MODEL, modelRL);
    }

    public Optional<String> getItemModel() {
        Optional<CompoundTag> component = getComponent();
        if (component.isPresent()) {
            CompoundTag c = component.get();
            if (CompoundTagUtils.containsString(c, COM_ITEM_MODEL)) {
                return Optional.of(c.getString(COM_ITEM_MODEL));
            }
        }
        return Optional.empty();
    }
}
