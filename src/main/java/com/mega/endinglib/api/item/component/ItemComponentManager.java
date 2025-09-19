package com.mega.endinglib.api.item.component;

import com.mega.endinglib.api.data.CompoundTagUtils;
import com.mega.endinglib.api.data.TagEnum;
import com.mega.endinglib.api.item.component.impl.GliderComponent;
import com.mega.endinglib.api.item.component.impl.ItemModelComponent;
import com.mega.endinglib.util.mixin.data_expand.ExtraItemStackItf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.Optional;

public class ItemComponentManager {
    public static final String HEAD = "Component";
    public static final ResourceLocation COM_ITEM_MODEL = new ResourceLocation("item_model");
    public static final ResourceLocation COM_GLIDER = new ResourceLocation("glider");
    public static final ResourceLocation COM_BREAK_SOUND = new ResourceLocation("break_sound");
    private static final Object2ObjectOpenHashMap<ResourceLocation, ItemComponentType<?>> COMPONENTS = new Object2ObjectOpenHashMap<>();
    public static final ItemComponentType<ItemModelComponent> ITEM_MODEL = register(COM_ITEM_MODEL, ComponentTypeBuilder.create(
            builder -> builder
                    .codec(ItemModelComponent.CODEC)
                    .registryName(COM_ITEM_MODEL)
                    .rootTagType(TagEnum.STRING)
                    .build()
    ));
    public static final ItemComponentType<GliderComponent> GLIDER = register(COM_GLIDER, ComponentTypeBuilder.create(
            builder -> builder
                    .codec(GliderComponent.CODEC)
                    .registryName(COM_GLIDER)
                    .rootTagType(TagEnum.SNBT)
                    .build()
    ));
    public static final ItemComponentType<Holder<SoundEvent>> BREAK_SOUND = register(COM_BREAK_SOUND, ComponentTypeBuilder.create(
            builder -> builder
                    .codec(SoundEvent.CODEC)
                    .registryName(COM_BREAK_SOUND)
                    .rootTagType(TagEnum.SNBT_LIST)
                    .build()
    ));
    private final ItemStack itemStack;

    public ItemComponentManager(ItemStack itemStack) {
        this.itemStack = itemStack;
    }
    public static ItemComponentManager get(ItemStack stack) {
        return ((ExtraItemStackItf) (Object) stack).endingLibrary$getComponentManager();
    }
    public ItemStack getItemStack() {
        return itemStack;
    }

    private CompoundTag forceGetComponent() {
        return itemStack.getOrCreateTag().getCompound(HEAD);
    }

    public Optional<CompoundTag> getComponent() {
        CompoundTag tag = itemStack.getTag();
        if (tag != null) {
            if (tag.isEmpty()) return Optional.empty();
            return Optional.of(tag.getCompound(HEAD));
        } else return Optional.empty();
    }

    public Optional<String> getItemModel() {
        Optional<CompoundTag> component = getComponent();
        if (component.isPresent()) {
            CompoundTag c = component.get();
            if (c.isEmpty()) return Optional.empty();
            if (CompoundTagUtils.containsString(c, COM_ITEM_MODEL.toString())) {
                return Optional.of(c.getString(COM_ITEM_MODEL.toString()));
            }
        }
        return Optional.empty();
    }
    public boolean glider() {
        Optional<CompoundTag> component = getComponent();
        return component.map(compoundTag -> compoundTag.contains(COM_GLIDER.toString())).orElse(false);
    }
    public Optional<Holder<SoundEvent>> breakSound() {
        Optional<CompoundTag> component = getComponent();
        return component.flatMap(compoundTag -> BREAK_SOUND.codec().parse(NbtOps.INSTANCE, compoundTag.getCompound(COM_BREAK_SOUND.toString())).result());
    }
    public int componentsSize() {
        return this.forceGetComponent().size();
    }
    public static <T> ItemComponentType<T> register(ResourceLocation key, ItemComponentType<T> componentType) {
        ItemComponentType<?> codec1 = COMPONENTS.put(key, componentType);
        if (codec1 != null) {
            throw new RuntimeException("Item component [" + key + "] is already exist!");
        }
        return componentType;
    }
    public static ItemComponentType<?> getComponentType(ResourceLocation key) {
        return COMPONENTS.get(key);
    }

    public static Map<ResourceLocation, ItemComponentType<?>> getRegistryMap() {
        return COMPONENTS;
    }
}
