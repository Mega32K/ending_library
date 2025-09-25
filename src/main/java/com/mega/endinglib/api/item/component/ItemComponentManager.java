package com.mega.endinglib.api.item.component;

import com.mega.endinglib.api.data.TagEnum;
import com.mega.endinglib.api.item.component.type.*;
import com.mega.endinglib.api.item.consume.ConsumeEffect;
import com.mega.endinglib.util.mc.codec.Codecs;
import com.mega.endinglib.util.mixin.data_expand.ExtraItemStackItf;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.phys.Vec3;

import java.util.Map;

public class ItemComponentManager {
    public static final String HEAD = "Component";
    public static final ResourceLocation COM_CUSTOM_DATA = new ResourceLocation("custom_data");
    public static final ResourceLocation COM_ITEM_MODEL = new ResourceLocation("item_model");
    public static final ResourceLocation COM_GLIDER = new ResourceLocation("glider");
    public static final ResourceLocation COM_BREAK_SOUND = new ResourceLocation("break_sound");
    public static final ResourceLocation COM_CONSUMABLE = new ResourceLocation("consumable");
    public static final ResourceLocation COM_FOOD = new ResourceLocation("food");
    public static final ResourceLocation COM_MAX_STACK_SIZE = new ResourceLocation("max_stack_size");
    public static final ResourceLocation COM_RARITY = new ResourceLocation("rarity");
    public static final ResourceLocation COM_BLOCKS_ATTACKS = new ResourceLocation("blocks_attacks");
    public static final ResourceLocation COM_WEAPON = new ResourceLocation("weapon");
    public static final ResourceLocation COM_DAMAGE_RESISTANT = new ResourceLocation("damage_resistant");
    public static final ResourceLocation COM_DEATH_PROTECTION = new ResourceLocation("death_protection");
    public static final ResourceLocation COM_ENCHANTABLE = new ResourceLocation("enchantable");
    public static final ResourceLocation COM_ENCHANTMENT_GLINT_OVERRIDE = new ResourceLocation("enchantment_glint_override");
    public static final ResourceLocation COM_EQUIPPABLE = new ResourceLocation("equippable");
    private static final Object2ObjectOpenHashMap<ResourceLocation, ItemComponentType<?>> COMPONENTS = new Object2ObjectOpenHashMap<>();
    public static final ItemComponentType<CompoundTag> CUSTOM_DATA = register(COM_CUSTOM_DATA, ComponentTypeBuilder.create(
            builder -> builder
                    .codec(CompoundTag.CODEC)
                    .registryName(COM_CUSTOM_DATA)
                    .rootTagType(TagEnum.SNBT)
                    .build()
    ));
    public static final ItemComponentType<ItemModelComponent> ITEM_MODEL = register(COM_ITEM_MODEL, ComponentTypeBuilder.create(
            builder -> builder
                    .codec(ItemModelComponent.CODEC)
                    .registryName(COM_ITEM_MODEL)
                    .rootTagType(TagEnum.STRING)
                    .build()
    ));
    public static final ItemComponentType<Unit> GLIDER = register(COM_GLIDER, ComponentTypeBuilder.create(
            builder -> builder
                    .codec(Codecs.UNIT_CODEC)
                    .registryName(COM_GLIDER)
                    .rootTagType(TagEnum.SNBT)
                    .build()
    ));
    public static final ItemComponentType<Holder<SoundEvent>> BREAK_SOUND = register(COM_BREAK_SOUND, ComponentTypeBuilder.create(
            builder -> builder
                    .codec(SoundEvent.CODEC)
                    .registryName(COM_BREAK_SOUND)
                    .rootTagType(TagEnum.SNBT)
                    .build()
    ));
    public static final ItemComponentType<ConsumableComponent> CONSUMABLE = register(COM_CONSUMABLE, ComponentTypeBuilder.create(
            builder -> builder
                    .codec(ConsumableComponent.CODEC)
                    .registryName(COM_CONSUMABLE).
                    rootTagType(TagEnum.SNBT)
                    .build()
    ));
    public static final ItemComponentType<FoodComponent> FOOD = register(COM_FOOD, ComponentTypeBuilder.create(
            builder -> builder
                    .codec(FoodComponent.CODEC)
                    .registryName(COM_FOOD).
                    rootTagType(TagEnum.SNBT)
                    .build()
    ));
    public static final ItemComponentType<Integer> MAX_STACK_SIZE = register(COM_MAX_STACK_SIZE, ComponentTypeBuilder.create(
            builder -> builder
                    .codec(Codec.intRange(1, 99))
                    .registryName(COM_MAX_STACK_SIZE)
                    .rootTagType(TagEnum.INT)
                    .build()
    ));
    public static final ItemComponentType<Rarity> RARITY = register(COM_RARITY, ComponentTypeBuilder.create(
            builder -> builder
                    .codec(Codecs.ITEM_RARITY_CODEC)
                    .registryName(COM_RARITY)
                    .rootTagType(TagEnum.STRING)
                    .build()
    ));
    public static final ItemComponentType<WeaponComponent> WEAPON = register(COM_WEAPON, ComponentTypeBuilder.create(
            builder -> builder
                    .codec(WeaponComponent.CODEC)
                    .registryName(COM_WEAPON)
                    .rootTagType(TagEnum.SNBT)
                    .build()
    ));
    public static final ItemComponentType<BlocksAttacksComponent> BLOCKS_ATTACKS = register(COM_BLOCKS_ATTACKS, ComponentTypeBuilder.create(
       builder -> builder
               .codec(BlocksAttacksComponent.CODEC)
               .registryName(COM_BLOCKS_ATTACKS)
               .rootTagType(TagEnum.SNBT)
               .build()
    ));
    public static final ItemComponentType<DamageResistantComponent> DAMAGE_RESISTANT = register(COM_DAMAGE_RESISTANT, ComponentTypeBuilder.create(
            builder -> builder
                    .codec(DamageResistantComponent.CODEC)
                    .registryName(COM_DAMAGE_RESISTANT)
                    .rootTagType(TagEnum.SNBT)
                    .build()
    ));
    public static final ItemComponentType<DeathProtectionComponent> DEATH_PROTECTION = register(COM_DEATH_PROTECTION, ComponentTypeBuilder.create(
            builder -> builder
                    .codec(DeathProtectionComponent.CODEC)
                    .registryName(COM_DEATH_PROTECTION)
                    .rootTagType(TagEnum.SNBT)
                    .build()
    ));
    public static final ItemComponentType<EnchantableComponent> ENCHANTABLE = register(COM_ENCHANTABLE, ComponentTypeBuilder.create(
            builder -> builder
                    .codec(EnchantableComponent.CODEC)
                    .registryName(COM_ENCHANTABLE)
                    .rootTagType(TagEnum.SNBT)
                    .build()
    ));
    public static final ItemComponentType<Boolean> ENCHANTMENT_GLINT_OVERRIDE = register(COM_ENCHANTMENT_GLINT_OVERRIDE, ComponentTypeBuilder.create(
            builder -> builder
                    .codec(Codec.BOOL)
                    .registryName(COM_ENCHANTMENT_GLINT_OVERRIDE)
                    .rootTagType(TagEnum.BOOLEAN)
                    .build()
    ));
    public static final ItemComponentType<EquippableComponent> EQUIPPABLE = register(COM_EQUIPPABLE, ComponentTypeBuilder.create(
            builder -> builder
                    .codec(EquippableComponent.CODEC)
                    .registryName(COM_EQUIPPABLE)
                    .rootTagType(TagEnum.SNBT)
                    .build()
    ));
    final MergedComponentMap components;
    private final ItemStack itemStack;

    public ItemComponentManager(ItemStack itemStack, MergedComponentMap mergedComponentMap) {
        this.itemStack = itemStack;
        this.components = mergedComponentMap;
    }

    public static ItemComponentManager get(ItemStack stack) {
        return ((ExtraItemStackItf) (Object) stack).endingLibrary$getComponentManager();
    }
    public static  <T> T get(ItemStack stack, ItemComponentType<? extends T> type) {
        return ItemComponentManager.get(stack).components.get(type);
    }
    public <T> T get(ItemComponentType<? extends T> type) {
        return this.components.get(type);
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

    public MergedComponentMap getComponents() {
        return components;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public int componentsSize() {
        return this.components.size();
    }
    public static float getWeaponDisableBlockingForSeconds(LivingEntity living) {
        WeaponComponent weaponComponent = ItemComponentManager.get(living.getMainHandItem(), WEAPON);
        return weaponComponent != null ? weaponComponent.disableBlockingForSeconds() : 0.0F;
    }
    public static ItemStack getBlockingItem(LivingEntity living) {
        if (!living.isUsingItem()) {
            return null;
        } else {
            ItemStack useItem = living.getUseItem();
            BlocksAttacksComponent blocksAttacksComponent = get(useItem, BLOCKS_ATTACKS);
            int i = useItem.getItem().getUseDuration(useItem) - living.getUseItemRemainingTicks();
            if (i >= (blocksAttacksComponent == null ? 5 : blocksAttacksComponent.getBlockDelayTicks())) {
                return useItem;
            }

            return null;
        }
    }
    public static float getDamageBlockedAmount(LivingEntity living, DamageSource source, float amount, ItemStack itemStack, BlocksAttacksComponent blocksAttacksComponent) {
        if (amount <= 0.0F) {
            return 0.0F;
        } else {
            {
                if (blocksAttacksComponent != null && !(Boolean)blocksAttacksComponent.bypassedBy().map(source::is).orElse(false)) {
                    if (source.getDirectEntity() instanceof AbstractArrow abstractArrow && abstractArrow.getPierceLevel() > 0) {
                        return 0.0F;
                    } else {
                        Vec3 vec3d = source.getSourcePosition();
                        double d;
                        if (vec3d != null) {
                            Vec3 vec3 = living.getViewVector(1.0F);
                            Vec3 vec31 = vec3d.vectorTo(living.position());
                            vec31 = new Vec3(vec31.x, 0.0D, vec31.z).normalize();
                            d = Mth.PI - Math.acos(vec31.dot(vec3));
                        } else {
                            d = (float) Math.PI;
                        }

                        float f = blocksAttacksComponent.getDamageReductionAmount(source, amount, d);
                        blocksAttacksComponent.onShieldHit(living.level(), itemStack, living, living.getUsedItemHand(), f);
                        /*
                        if (!source.is(DamageTypeTags.IS_PROJECTILE) && source.getEntity() instanceof LivingEntity livingEntity) {
                            this.blockUsingShield(livingEntity);
                        }
                         */

                        return f;
                    }
                } else {
                    return 0.0F;
                }
            }
        }
    }
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void init() {
        ConsumeEffect.Type.APPLY_EFFECTS.id();
    }
}
