package com.mega.endinglib.util.mc.entity.armor;

import com.mega.endinglib.api.item.armor.ArmorOption;
import com.mega.endinglib.api.item.armor.ModifiableArmorItem;
import com.mega.endinglib.api.item.armor.OptionArmorMaterial;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@SuppressWarnings("unused")
public class ArmorUtils {
    public static ArmorItem.Type typeFromEquipmentSlot(EquipmentSlot slot) {
        switch (slot) {
            case HEAD -> {
                return ArmorItem.Type.HELMET;
            }
            case CHEST -> {
                return ArmorItem.Type.CHESTPLATE;
            }
            case LEGS -> {
                return ArmorItem.Type.LEGGINGS;
            }
            case FEET -> {
                return ArmorItem.Type.BOOTS;
            }
            default -> {
                return null;
            }
        }
    }

    public static EquipmentSlot equipmentSlotFromType(ArmorItem.Type type) {
        switch (type) {
            case HELMET -> {
                return EquipmentSlot.HEAD;
            }
            case CHESTPLATE -> {
                return EquipmentSlot.CHEST;
            }
            case LEGGINGS -> {
                return EquipmentSlot.LEGS;
            }
            case BOOTS -> {
                return EquipmentSlot.FEET;
            }
        }
        return EquipmentSlot.HEAD;
    }

    public static ArmorOption getArmorOption(ArmorMaterial material) {
        if (material instanceof OptionArmorMaterial oam)
            return oam.getOption();
        return null;
    }

    public static ModifiableArmorItem getModifiableArmor(ArmorMaterial material, ArmorItem.Type type) {
        return getModifiableArmors(material).get(type);
    }

    public static Map<ArmorItem.Type, ModifiableArmorItem> getModifiableArmors(ArmorMaterial material) {
        if (material == null) return null;
        return ModifiableArmorItem.ARMOR_MAP.getOrDefault(material, null);
    }

    public static boolean isFire(DamageSource source) {
        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY))
            return false;
        if (source.is(DamageTypeTags.IS_FIRE))
            return true;
        return source.is(DamageTypes.FIREBALL) || source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.ON_FIRE) || source.is(DamageTypes.LAVA);
    }

    public static boolean isMagicDamage(DamageSource source) {
        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY))
            return false;
        return source.is(DamageTypes.MAGIC) || source.is(DamageTypes.INDIRECT_MAGIC) || source.is(DamageTypes.DRAGON_BREATH) || source.is(DamageTypeTags.WITCH_RESISTANT_TO);
    }

    public static boolean findHelmet(Player player, Item item) {
        return player.getItemBySlot(EquipmentSlot.HEAD).getItem() == item;
    }

    public static boolean findLeggings(Player player, Item item) {
        return player.getItemBySlot(EquipmentSlot.LEGS).getItem() == item;
    }

    public static boolean findChestplate(LivingEntity player, Item item) {
        return player.getItemBySlot(EquipmentSlot.CHEST).getItem() == item;
    }

    public static boolean findBoots(LivingEntity player, Item item) {
        return player.getItemBySlot(EquipmentSlot.FEET).getItem() == item;
    }

    public static boolean findHelmet(LivingEntity player, ArmorMaterial material) {
        return player.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof ArmorItem armorItem && armorItem.getMaterial().equals(material);
    }

    public static boolean findLeggings(LivingEntity player, ArmorMaterial material) {
        return player.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof ArmorItem armorItem && armorItem.getMaterial().equals(material);
    }

    public static boolean findChestplate(LivingEntity player, ArmorMaterial material) {
        return player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof ArmorItem armorItem && armorItem.getMaterial().equals(material);
    }

    public static boolean findBoots(LivingEntity player, ArmorMaterial material) {
        return player.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof ArmorItem armorItem && armorItem.getMaterial().equals(material);
    }

    public static boolean armorSet(LivingEntity living, ArmorMaterial material) {
        return getArmorSet(living) == material;
    }

    public static int armorSetCount(LivingEntity living, ArmorMaterial material) {
        int i = 0;
        Item firstCheck = living.getItemBySlot(EquipmentSlot.HEAD).getItem();
        if (firstCheck instanceof ArmorItem helmet) {
            if (helmet.getMaterial() == material) {
                EquipmentSlot[] var10 = EquipmentSlot.values();
                int var5 = var10.length;

                for (EquipmentSlot equipmentSlot : var10) {
                    if (equipmentSlot.getType() == EquipmentSlot.Type.ARMOR) {
                        Item item = living.getItemBySlot(equipmentSlot).getItem();
                        if (item instanceof ArmorItem armorItem) {
                            if (armorItem.getMaterial() == material) {
                                ++i;
                            }
                        }
                    }
                }
            }
        }

        return i;
    }

    public static @Nullable ArmorMaterial getArmorSet(LivingEntity living) {
        int i = 0;
        Item firstCheck = living.getItemBySlot(EquipmentSlot.HEAD).getItem();
        ArmorMaterial material = null;
        if (firstCheck instanceof ArmorItem helmet) {
            material = helmet.getMaterial();
            EquipmentSlot[] var10 = EquipmentSlot.values();
            int var5 = var10.length;

            for (EquipmentSlot equipmentSlot : var10) {
                if (equipmentSlot.getType() == EquipmentSlot.Type.ARMOR) {
                    Item item = living.getItemBySlot(equipmentSlot).getItem();
                    if (item instanceof ArmorItem armorItem) {
                        if (armorItem.getMaterial() == material) {
                            ++i;
                        }
                    }
                }
            }
        }

        return i >= 4 ? material : null;
    }
}
