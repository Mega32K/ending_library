package com.mega.endinglib.api.item.armor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.mega.endinglib.api.client.cmc.CuriosMutableComponent;
import com.mega.endinglib.api.client.cmc.LoreHelper;
import com.mega.endinglib.api.client.cmc.LoreStyle;
import com.mega.endinglib.util.entity.armor.ArmorModifiersBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class ModifiableArmorItem extends ArmorItem {
    public static final EquipmentSlot[] ARMOR_SLOTS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    public static final Object2ObjectOpenHashMap<OptionArmorMaterial, Map<Type, ModifiableArmorItem>> ARMOR_MAP = new Object2ObjectOpenHashMap<>();
    private static final EnumMap<Type, UUID> ARMOR_MODIFIER_UUID_PER_TYPE = Util.make(new EnumMap<>(ArmorItem.Type.class), (p_266744_) -> {
        p_266744_.put(ArmorItem.Type.BOOTS, UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"));
        p_266744_.put(ArmorItem.Type.LEGGINGS, UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"));
        p_266744_.put(ArmorItem.Type.CHESTPLATE, UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"));
        p_266744_.put(ArmorItem.Type.HELMET, UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150"));
    });
    protected final float knockbackResistance;
    protected final Multimap<Attribute, AttributeModifier> defaultModifiers;
    private final int defense;
    private final float toughness;
    protected boolean hasSimpleDescription;

    public ModifiableArmorItem(OptionArmorMaterial optionArmorMaterial, Type armorType, Properties itemProperties) {
        super(optionArmorMaterial, armorType, itemProperties);
        this.defense = optionArmorMaterial.getDefenseForType(armorType);
        this.toughness = optionArmorMaterial.getToughness();
        this.knockbackResistance = optionArmorMaterial.getKnockbackResistance();
        ArmorModifiersBuilder before = new ArmorModifiersBuilder(ImmutableMultimap.builder());
        injectExtraArmorAttributesBefore(before);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = before.builder();
        UUID uuid = ARMOR_MODIFIER_UUID_PER_TYPE.get(armorType);
        builder.put(Attributes.ARMOR, new AttributeModifier(uuid, "Armor modifier", this.defense, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "Armor toughness", this.toughness, AttributeModifier.Operation.ADDITION));
        if (this.knockbackResistance > 0) {
            builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(uuid, "Armor knockback resistance", this.knockbackResistance, AttributeModifier.Operation.ADDITION));
        }
        ArmorModifiersBuilder modifiersBuilder = new ArmorModifiersBuilder(builder);
        injectExtraArmorAttributes(modifiersBuilder);
        this.defaultModifiers = modifiersBuilder.builder().build();
        if (!ARMOR_MAP.containsKey(optionArmorMaterial)) {
            synchronized (ARMOR_MAP) {
                if (!ARMOR_MAP.containsKey(optionArmorMaterial)) {
                    EnumMap<Type, ModifiableArmorItem> map = new EnumMap<>(Type.class);
                    map.put(this.type, this);
                    ARMOR_MAP.put(optionArmorMaterial, Collections.synchronizedMap(map));
                }
            }
        }
        synchronized (ARMOR_MAP) {
            ARMOR_MAP.get(optionArmorMaterial).put(armorType, this);
        }
    }

    public void injectExtraArmorAttributes(ArmorModifiersBuilder builder) {

    }

    public void injectExtraArmorAttributesBefore(ArmorModifiersBuilder builder) {

    }

    public @NotNull ArmorMaterial getMaterial() {
        return this.material;
    }

    public boolean isValidRepairItem(@NotNull ItemStack p_40392_, @NotNull ItemStack p_40393_) {
        return this.material.getRepairIngredient().test(p_40393_) || super.isValidRepairItem(p_40392_, p_40393_);
    }

    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level p_40395_, @NotNull Player p_40396_, @NotNull InteractionHand p_40397_) {
        return this.swapWithEquipmentSlot(this, p_40395_, p_40396_, p_40397_);
    }

    public @NotNull Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(@NotNull EquipmentSlot p_40390_) {
        return p_40390_ == this.type.getSlot() ? this.defaultModifiers : super.getDefaultAttributeModifiers(p_40390_);
    }

    public void onArmorTick(Level level, LivingEntity living, ItemStack itemStack, Type type) {
    }

    @Override
    public void inventoryTick(@NotNull ItemStack itemStack, @NotNull Level p_41405_, @NotNull Entity p_41406_, int p_41407_, boolean p_41408_) {
        if (p_41406_ instanceof LivingEntity living) {
            if (living instanceof Player player) {
                Inventory inventory = player.getInventory();
                switch (type) {
                    case HELMET -> {
                        if (inventory.armor.get(EquipmentSlot.HEAD.getIndex()).equals(itemStack, true))
                            onArmorTick(p_41405_, player, itemStack, Type.HELMET);
                    }
                    case CHESTPLATE -> {
                        if (inventory.armor.get(EquipmentSlot.CHEST.getIndex()).equals(itemStack, true))
                            onArmorTick(p_41405_, player, itemStack, Type.CHESTPLATE);
                    }
                    case LEGGINGS -> {
                        if (inventory.armor.get(EquipmentSlot.LEGS.getIndex()).equals(itemStack, true))
                            onArmorTick(p_41405_, player, itemStack, Type.LEGGINGS);
                    }
                    case BOOTS -> {
                        if (inventory.armor.get(EquipmentSlot.FEET.getIndex()).equals(itemStack, true))
                            onArmorTick(p_41405_, player, itemStack, Type.BOOTS);
                    }
                }
            } else {
                switch (type) {
                    case HELMET -> {
                        if (living.getItemBySlot(EquipmentSlot.HEAD).equals(itemStack, true))
                            onArmorTick(p_41405_, living, itemStack, Type.HELMET);
                    }
                    case CHESTPLATE -> {
                        if (living.getItemBySlot(EquipmentSlot.CHEST).equals(itemStack, true))
                            onArmorTick(p_41405_, living, itemStack, Type.CHESTPLATE);
                    }
                    case LEGGINGS -> {
                        if (living.getItemBySlot(EquipmentSlot.LEGS).equals(itemStack, true))
                            onArmorTick(p_41405_, living, itemStack, Type.LEGGINGS);
                    }
                    case BOOTS -> {
                        if (living.getItemBySlot(EquipmentSlot.FEET).equals(itemStack, true))
                            onArmorTick(p_41405_, living, itemStack, Type.BOOTS);
                    }
                }
            }
        }
    }

    public int getDefense() {
        return this.defense;
    }

    public float getToughness() {
        return this.toughness;
    }

    public @NotNull SoundEvent getEquipSound() {
        return this.getMaterial().getEquipSound();
    }

    public Multimap<Attribute, AttributeModifier> getSetAttributesModifiers(LivingEntity living) {
        return ImmutableMultimap.of();
    }

    public ArmorOption getOption(ItemStack stack, LivingEntity living) {
        return ((OptionArmorMaterial) this.material).getOption();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack itemStack, @Nullable Level level, @NotNull List<Component> components, @NotNull TooltipFlag tooltipFlag) {
        boolean isShiftKeyDown = LoreHelper.hasShiftDown();
        if (hasSimpleDescription() || hasSetDescription()) {
            if (isShiftKeyDown) {
                List<CuriosMutableComponent> cmcs = new ObjectArrayList<>();
                if (hasSimpleDescription()) {
                    cmcs.add(CuriosMutableComponent.create(Component.translatable("tooltip.endinglib.specialEffect"), LoreStyle.NONE));
                    this.addSimpleDescription(itemStack, level, cmcs, tooltipFlag);
                }
                if (hasSetDescription()) {
                    cmcs.add(CuriosMutableComponent.create(Component.translatable("tooltip.endinglib.setEffect"), LoreStyle.NONE));
                    this.addSetDescription(itemStack, level, cmcs, tooltipFlag);
                }
                cmcs.forEach(cmc -> components.add(cmc.build(itemStack)));
            } else {
                components.add(Component.translatable("tooltip.endinglib.holdShiftEffect"));
            }
        }

        super.appendHoverText(itemStack, level, components, tooltipFlag);
    }

    public boolean hasSimpleDescription() {
        return false;
    }

    public boolean hasSetDescription() {
        return false;
    }

    public void addSetDescription(@NotNull ItemStack itemStack, @Nullable Level level, @NotNull List<CuriosMutableComponent> components, @NotNull TooltipFlag tooltipFlag) {

    }

    public void addSimpleDescription(@NotNull ItemStack itemStack, @Nullable Level level, @NotNull List<CuriosMutableComponent> components, @NotNull TooltipFlag tooltipFlag) {

    }

    public void when4SetTick(LivingEntity living, Level level) {
    }

    public boolean immuneEffectsWhenSet(LivingEntity living, MobEffectInstance mobEffect) {
        return false;
    }

    public boolean immuneEffects(LivingEntity living, MobEffectInstance mobEffect) {
        return false;
    }

    public void onLivingDamage(LivingDamageEvent event, ItemStack armorStack) {
    }

    public void onLivingAttack(LivingAttackEvent event, ItemStack armorStack) {
    }

    public void onLivingHurt(LivingHurtEvent event, ItemStack armorStack) {
    }

    public void onLivingDeath(LivingDeathEvent event, ItemStack armorStack) {
    }

    public void onArmorSetLivingDamage(LivingDamageEvent event) {
    }

    public void onArmorSetLivingAttack(LivingAttackEvent event) {
    }

    public void onArmorSetLivingHurt(LivingHurtEvent event) {
    }

    public void onArmorSetLivingDeath(LivingDeathEvent event) {
    }

    public void onSetHurtOthers(LivingHurtEvent event, LivingEntity attacker, LivingEntity beHurt) {
    }
}
