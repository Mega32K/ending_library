package com.mega.endinglib.api.item.component;

import com.mega.endinglib.api.data.TagEnum;
import com.mega.endinglib.api.item.component.type.*;
import com.mega.endinglib.api.item.component.type.function.*;
import com.mega.endinglib.util.SafeClass;
import com.mega.endinglib.util.mc.codec.Codecs;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Unit;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import org.joml.Vector3f;

import java.util.List;

public class DataComponents extends ItemComponentManager {
    public static final ResourceLocation COM_CUSTOM_DATA = new ResourceLocation("custom_data");
    public static final ResourceLocation COM_ITEM_MODEL = new ResourceLocation("item_model");
    public static final ResourceLocation COM_GLIDER = new ResourceLocation("glider");
    public static final ResourceLocation COM_BREAK_SOUND = new ResourceLocation("break_sound");
    public static final ResourceLocation COM_CONSUMABLE = new ResourceLocation("consumable");
    public static final ResourceLocation COM_FOOD = new ResourceLocation("food");
    public static final ResourceLocation COM_MAX_STACK_SIZE = new ResourceLocation("max_stack_size");
    public static final ResourceLocation COM_RARITY = new ResourceLocation("rarity");
    public static final ResourceLocation COM_WEAPON = new ResourceLocation("weapon");
    public static final ResourceLocation COM_DEATH_PROTECTION = new ResourceLocation("death_protection");
    public static final ResourceLocation COM_BLOCKS_ATTACKS = new ResourceLocation("blocks_attacks");
    public static final ResourceLocation COM_DAMAGE_RESISTANT = new ResourceLocation("damage_resistant");
    public static final ResourceLocation COM_ENCHANTABLE = new ResourceLocation("enchantable");
    public static final ResourceLocation COM_ENCHANTMENT_GLINT_OVERRIDE = new ResourceLocation("enchantment_glint_override");
    public static final ResourceLocation COM_EQUIPPABLE = new ResourceLocation("equippable");
    public static final ResourceLocation COM_INTANGIBLE_PROJECTILE = new ResourceLocation("intangible_projectile");
    public static final ResourceLocation COM_PROVIDES_BANNER_PATTERNS = new ResourceLocation("provides_banner_patterns");
    public static final ResourceLocation COM_PROVIDES_TRIM_MATERIAL = new ResourceLocation("provides_trim_material");
    public static final ResourceLocation COM_MAX_DAMAGE = new ResourceLocation("max_damage");
    public static final ResourceLocation COM_REPAIRABLE = new ResourceLocation("repairable");
    public static final ResourceLocation COM_TOOL = new ResourceLocation("tool");
    public static final ResourceLocation COM_USE_COOLDOWN = new ResourceLocation("use_cooldown");
    public static final ResourceLocation COM_USE_REMAINDER = new ResourceLocation("use_remainder");
    public static final ResourceLocation COM_DAMAGE_TYPE = new ResourceLocation("damage_type");
    public static final ResourceLocation COM_MINIMUM_ATTACK_CHARGE = new ResourceLocation("minimum_attack_charge");
    public static final ResourceLocation COM_USE_EFFECTS = new ResourceLocation("use_effects");
    public static final ResourceLocation COM_LORE = new ResourceLocation("lore");

    public static final ResourceLocation COM_BURN_TIME = SafeClass.loc("burn_time");
    public static final ResourceLocation COM_CRAFT_REMAINING = SafeClass.loc("craft_remaining");
    public static final ResourceLocation COM_LIFE_SPAN = SafeClass.loc("life_span");
    public static final ResourceLocation COM_PIGLIN_CURRENCY = SafeClass.loc("piglin_currency");
    public static final ResourceLocation COM_PIGLIN_NEUTRAL = SafeClass.loc("piglin_neutral");
    public static final ResourceLocation COM_ENDER_MUSK = SafeClass.loc("ender_musk");
    public static final ResourceLocation COM_CAN_WALK_ON_POWDERED_SNOW = SafeClass.loc("can_walk_on_powdered_snow");
    public static final ResourceLocation COM_SWEEP_HITBOX_INFLATION = SafeClass.loc("sweep_hitbox_inflation");
    public static final ResourceLocation COM_GRINDSTONE_REPAIRABLE = SafeClass.loc("grindstone_repairable");
    /**
     * {@link ItemStack#getBarWidth()}<br>{@link ItemStack#getBarColor()}<br>{@link ItemStack#isBarVisible()}<br>
     */
    public static final ResourceLocation COM_ITEM_BAR = SafeClass.loc("bar");
    public static final ResourceLocation COM_TAGS = SafeClass.loc("tags");
    public static final ResourceLocation COM_ATTACK_EVENT = SafeClass.loc("function/attack_event");
    public static final ResourceLocation COM_USE_EVENT = SafeClass.loc("function/use_event");
    public static final ResourceLocation COM_RELEASE_USING = SafeClass.loc("function/release_event");
    public static final ResourceLocation COM_HURT_EVENT = SafeClass.loc("function/hurt_event");
    public static final ResourceLocation COM_SWING_EVENT = SafeClass.loc("function/swing_event");
    public static final ResourceLocation COM_DROP_ITEM_EVENT = SafeClass.loc("function/drop_item_event");
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
    public static final ItemComponentType<Unit> INTANGIBLE_PROJECTILE = register(COM_INTANGIBLE_PROJECTILE, ComponentTypeBuilder.create(
            builder -> builder
                    .codec(Codecs.UNIT_CODEC)
                    .registryName(COM_INTANGIBLE_PROJECTILE)
                    .rootTagType(TagEnum.SNBT)
                    .build()
    ));
    public static final ItemComponentType<TagKey<BannerPattern>> PROVIDES_BANNER_PATTERNS = register(COM_PROVIDES_BANNER_PATTERNS, ComponentTypeBuilder.create(
            builder -> builder
                    .codec(TagKey.hashedCodec(Registries.BANNER_PATTERN))
                    .registryName(COM_PROVIDES_BANNER_PATTERNS)
                    .rootTagType(TagEnum.STRING)
                    .build()
    ));
    public static final ItemComponentType<Holder<TrimMaterial>> PROVIDES_TRIM_MATERIAL = register(COM_PROVIDES_TRIM_MATERIAL, ComponentTypeBuilder.create(
            builder -> builder
                    .codec(TrimMaterial.CODEC)
                    .registryName(COM_PROVIDES_TRIM_MATERIAL)
                    .rootTagType(TagEnum.SNBT)
                    .build()
    ));
    public static final ItemComponentType<Integer> MAX_DAMAGE = register(COM_MAX_DAMAGE, ComponentTypeBuilder.create(
            builder -> builder
                    .codec(Codecs.POSITIVE_INT)
                    .registryName(COM_MAX_DAMAGE)
                    .rootTagType(TagEnum.INT)
                    .build()
    ));
    public static final ItemComponentType<RepairableComponent> REPAIRABLE = register(COM_REPAIRABLE, ComponentTypeBuilder.create(
            builder -> builder
                    .codec(RepairableComponent.CODEC)
                    .registryName(COM_REPAIRABLE)
                    .rootTagType(TagEnum.SNBT)
                    .build()
    ));
    public static final ItemComponentType<ToolComponent> TOOL = register(COM_TOOL, ComponentTypeBuilder.create(
            builder -> builder
                    .codec(ToolComponent.CODEC)
                    .registryName(COM_TOOL)
                    .rootTagType(TagEnum.SNBT)
                    .build()
    ));
    public static final ItemComponentType<UseCooldownComponent> USE_COOLDOWN = register(COM_USE_COOLDOWN, ComponentTypeBuilder.create(
            builder -> builder
                    .codec(UseCooldownComponent.CODEC)
                    .registryName(COM_USE_COOLDOWN)
                    .rootTagType(TagEnum.SNBT)
                    .build()
    ));
    public static final ItemComponentType<UseRemainderComponent> USE_REMAINDER = register(COM_USE_REMAINDER, ComponentTypeBuilder.create(
            builder -> builder
                    .codec(UseRemainderComponent.CODEC)
                    .registryName(COM_USE_REMAINDER)
                    .rootTagType(TagEnum.SNBT)
                    .build()
    ));
    public static final ItemComponentType<AttackEventComponent> ATTACK_EVENT = register(COM_ATTACK_EVENT, ComponentTypeBuilder.create(
            builder -> builder
                    .codec(AttackEventComponent.CODEC)
                    .registryName(COM_ATTACK_EVENT)
                    .rootTagType(TagEnum.SNBT)
                    .build()
    ));
    public static final ItemComponentType<UseEventComponent> USE_EVENT = register(COM_USE_EVENT, ComponentTypeBuilder.create(
            builder -> builder
                    .codec(UseEventComponent.CODEC)
                    .registryName(COM_USE_EVENT)
                    .rootTagType(TagEnum.SNBT)
                    .build()
    ));
    public static final ItemComponentType<ReleaseUsingComponent> RELEASE_USING = register(COM_RELEASE_USING, ComponentTypeBuilder.create(
            builder -> builder
                    .codec(ReleaseUsingComponent.CODEC)
                    .registryName(COM_RELEASE_USING)
                    .rootTagType(TagEnum.SNBT)
                    .build()
    ));
    public static final ItemComponentType<Holder<DamageType>> DAMAGE_TYPE = register(COM_DAMAGE_TYPE, ComponentTypeBuilder.create(
            builder -> builder
                    .codec(RegistryFixedCodec.create(Registries.DAMAGE_TYPE))
                    .registryName(COM_DAMAGE_TYPE)
                    .rootTagType(TagEnum.STRING)
                    .build()
    ));
    public static final ItemComponentType<Float> MINIMUM_ATTACK_CHARGE = register(COM_MINIMUM_ATTACK_CHARGE, ComponentTypeBuilder.create(
            builder -> builder
                    .codec(Codecs.O2ONE_FLOAT)
                    .registryName(COM_MINIMUM_ATTACK_CHARGE)
                    .rootTagType(TagEnum.FLOAT)
                    .build()
    ));
    public static final ItemComponentType<UseEffectsComponent> USE_EFFECTS = register(COM_USE_EFFECTS, ComponentTypeBuilder.create(
            builder -> builder
                    .codec(UseEffectsComponent.CODEC)
                    .registryName(COM_USE_EFFECTS)
                    .rootTagType(TagEnum.SNBT)
                    .build()
    ));
    public static final ItemComponentType<HurtEventComponent> HURT_EVENT = register(COM_HURT_EVENT, ComponentTypeBuilder.create(
            builder -> builder
                    .codec(HurtEventComponent.CODEC)
                    .registryName(COM_HURT_EVENT)
                    .rootTagType(TagEnum.SNBT)
                    .build()
    ));
    public static final ItemComponentType<SwingEventComponent> SWING_EVENT = register(COM_SWING_EVENT, ComponentTypeBuilder.create(
            builder -> builder
                    .codec(SwingEventComponent.CODEC)
                    .registryName(COM_SWING_EVENT)
                    .rootTagType(TagEnum.SNBT)
                    .build()
    ));
    public static final ItemComponentType<Integer> BURN_TIME = register(COM_BURN_TIME, ComponentTypeBuilder.create(
            builder -> builder
                    .codec(Codecs.NON_NEGATIVE_INT)
                    .registryName(COM_BURN_TIME)
                    .rootTagType(TagEnum.INT)
                    .build()
    ));
    public static final ItemComponentType<ItemStack> CRAFT_REMAINING = register(COM_CRAFT_REMAINING, ComponentTypeBuilder.create(
            builder -> builder
                    .codec( ItemComponentManager.ITEM_STACK_CODEC)
                    .registryName(COM_CRAFT_REMAINING)
                    .rootTagType(TagEnum.SNBT)
                    .build()
    ));
    public static final ItemComponentType<Integer> LIFE_SPAN = register(COM_LIFE_SPAN, ComponentTypeBuilder.create(
            builder -> builder
                    .codec(Codecs.NON_NEGATIVE_INT)
                    .registryName(COM_LIFE_SPAN)
                    .rootTagType(TagEnum.INT)
                    .build()
    ));
    public static final ItemComponentType<Unit> PIGLIN_CURRENCY = register(COM_PIGLIN_CURRENCY, ComponentTypeBuilder.create(
            builder -> builder
                    .codec(Codecs.UNIT_CODEC)
                    .registryName(COM_PIGLIN_CURRENCY)
                    .rootTagType(TagEnum.SNBT)
                    .build()
    ));
    public static final ItemComponentType<Unit> PIGLIN_NEUTRAL = register(COM_PIGLIN_NEUTRAL, ComponentTypeBuilder.create(
            builder -> builder
                    .codec(Codecs.UNIT_CODEC)
                    .registryName(COM_PIGLIN_NEUTRAL)
                    .rootTagType(TagEnum.SNBT)
                    .build()
    ));
    public static final ItemComponentType<Unit> ENDER_MUSK = register(COM_ENDER_MUSK, ComponentTypeBuilder.create(
            builder -> builder
                    .codec(Codecs.UNIT_CODEC)
                    .registryName(COM_ENDER_MUSK)
                    .rootTagType(TagEnum.SNBT)
                    .build()
    ));
    public static final ItemComponentType<Unit> CAN_WALK_ON_POWDERED_SNOW = register(COM_CAN_WALK_ON_POWDERED_SNOW, ComponentTypeBuilder.create(
            builder -> builder
                    .codec(Codecs.UNIT_CODEC)
                    .registryName(COM_CAN_WALK_ON_POWDERED_SNOW)
                    .rootTagType(TagEnum.SNBT)
                    .build()
    ));
    public static final ItemComponentType<Vec3> SWEEP_HITBOX_INFLATION = register(COM_SWEEP_HITBOX_INFLATION, ComponentTypeBuilder.create(
            builder -> builder
                    .codec(Vec3.CODEC)
                    .registryName(COM_SWEEP_HITBOX_INFLATION)
                    .rootTagType(TagEnum.LIST)
                    .build()
    ));
    public static final ItemComponentType<Unit> GRINDSTONE_REPAIRABLE = register(COM_GRINDSTONE_REPAIRABLE, ComponentTypeBuilder.create(
            builder -> builder
                    .codec(Codecs.UNIT_CODEC)
                    .registryName(COM_GRINDSTONE_REPAIRABLE)
                    .rootTagType(TagEnum.SNBT)
                    .build()
    ));
    public static final ItemComponentType<ItemBarComponent> ITEM_BAR = register(COM_ITEM_BAR, ComponentTypeBuilder.create(
            builder -> builder
                    .codec(ItemBarComponent.CODEC)
                    .registryName(COM_ITEM_BAR)
                    .rootTagType(TagEnum.SNBT)
                    .build()
    ));
    public static final ItemComponentType<List<TagKey<Item>>> TAGS = register(COM_TAGS, ComponentTypeBuilder.create(
            builder -> builder
                    .codec(Codecs.fastUtilListCodec(Codecs.canSerializeAsSingleList(TagKey.hashedCodec(Registries.ITEM))))
                    .registryName(COM_TAGS)
                    .rootTagType(TagEnum.SNBT)
                    .build()
    ));
    public static final ItemComponentType<LoreComponent> LORE = register(COM_LORE, ComponentTypeBuilder.create(
            builder -> builder
                    .codec(LoreComponent.CODEC)
                    .registryName(COM_LORE)
                    .rootTagType(TagEnum.LIST)
                    .build()
    ));
}
