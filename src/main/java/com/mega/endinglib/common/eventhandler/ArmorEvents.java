package com.mega.endinglib.common.eventhandler;

import com.mega.endinglib.EndingLibrary;
import com.mega.endinglib.api.item.armor.ModifiableArmorItem;
import com.mega.endinglib.api.item.armor.OptionArmorMaterial;
import com.mega.endinglib.util.entity.armor.ArmorUtils;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = EndingLibrary.MODID)
public class ArmorEvents {
    @SubscribeEvent
    public static void playerArmorTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        Level world = player.level();
        if (event.phase == TickEvent.Phase.END) {
            if (ArmorUtils.getArmorSet(player) instanceof OptionArmorMaterial currentSet) {
                Map<ArmorItem.Type, ModifiableArmorItem> armorItemEnumMap = ArmorUtils.getModifiableArmors(currentSet);
                if (armorItemEnumMap != null) {
                    ModifiableArmorItem egArmor = armorItemEnumMap.get(ArmorItem.Type.CHESTPLATE);
                    if (currentSet.getOption().setEffect()) {
                        egArmor.when4SetTick(player, world);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEquipmentChangeEvent(LivingEquipmentChangeEvent event) {
        if (ArmorUtils.typeFromEquipmentSlot(event.getSlot()) != null) {
            LivingEntity livingEntity = event.getEntity();
            ArmorMaterial armorSet = ArmorUtils.getArmorSet(livingEntity);
            if (!livingEntity.level().isClientSide) {
                Map<ArmorItem.Type, ModifiableArmorItem> armorItemEnumMap = ArmorUtils.getModifiableArmors(armorSet);
                if (armorSet instanceof OptionArmorMaterial currentSet && armorItemEnumMap != null) {
                    ModifiableArmorItem chestplate = armorItemEnumMap.get(ArmorItem.Type.CHESTPLATE);
                    if (currentSet.getOption().setEffect()) {
                        livingEntity.getAttributes().addTransientAttributeModifiers(chestplate.getSetAttributesModifiers(livingEntity));
                    }
                } else {
                    if (event.getFrom().getItem() instanceof ModifiableArmorItem lastModArmor && lastModArmor.getMaterial() instanceof OptionArmorMaterial lastArmorMaterial) {
                        Map<ArmorItem.Type, ModifiableArmorItem> lastArmorSet = ModifiableArmorItem.ARMOR_MAP.get(lastArmorMaterial);
                        if (lastArmorSet != null && lastArmorSet.get(lastModArmor.getType()).equals(lastModArmor)) {
                            if (lastArmorMaterial.getOption().setEffect()) {
                                livingEntity.getAttributes().removeAttributeModifiers(lastModArmor.getSetAttributesModifiers(livingEntity));
                            }
                        }
                    }
                }

            }
        }
    }

    @SubscribeEvent
    public static void onEffectsApplicable(MobEffectEvent.Applicable event) {
        LivingEntity livingEntity = event.getEntity();
        if (ArmorUtils.getArmorSet(livingEntity) instanceof OptionArmorMaterial currentSet) {
            if (currentSet.getOption().setBuffImmune()) {
                Map<ArmorItem.Type, ModifiableArmorItem> armorItemMap = ModifiableArmorItem.ARMOR_MAP.get(currentSet);
                if (armorItemMap != null && armorItemMap.get(ArmorItem.Type.CHESTPLATE).immuneEffectsWhenSet(livingEntity, event.getEffectInstance()))
                    event.setResult(Event.Result.DENY);
                return;
            }
        }
        for (EquipmentSlot slot : ModifiableArmorItem.ARMOR_SLOTS) {
            ItemStack armorStack = livingEntity.getItemBySlot(slot);
            if (armorStack.getItem() instanceof ModifiableArmorItem modifiableArmorItem) {
                if (modifiableArmorItem.immuneEffects(livingEntity, event.getEffectInstance())) {
                    event.setResult(Event.Result.DENY);
                    return;
                }
            }
        }
    }

    @SubscribeEvent
    public static void livingAttackEvent(LivingAttackEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (ArmorUtils.getArmorSet(livingEntity) instanceof OptionArmorMaterial currentSet) {
            if (currentSet.getOption().armorSetAttackEvent()) {
                Map<ArmorItem.Type, ModifiableArmorItem> armorItemMap = ModifiableArmorItem.ARMOR_MAP.get(currentSet);
                if (armorItemMap != null)
                    armorItemMap.get(ArmorItem.Type.CHESTPLATE).onArmorSetLivingAttack(event);
            }
        }
        for (EquipmentSlot slot : ModifiableArmorItem.ARMOR_SLOTS) {
            ItemStack stack = livingEntity.getItemBySlot(slot);
            if (stack.getItem() instanceof ModifiableArmorItem armorItem && armorItem.getOption(stack, livingEntity).attackEvent())
                armorItem.onLivingAttack(event, stack);
        }
    }

    @SubscribeEvent
    public static void livingHurtEvent(LivingHurtEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (ArmorUtils.getArmorSet(livingEntity) instanceof OptionArmorMaterial currentSet) {
            if (currentSet.getOption().armorSetHurtEvent()) {
                Map<ArmorItem.Type, ModifiableArmorItem> armorItemMap = ModifiableArmorItem.ARMOR_MAP.get(currentSet);
                if (armorItemMap != null)
                    armorItemMap.get(ArmorItem.Type.CHESTPLATE).onArmorSetLivingHurt(event);
            }
        }
        for (EquipmentSlot slot : ModifiableArmorItem.ARMOR_SLOTS) {
            ItemStack stack = livingEntity.getItemBySlot(slot);
            if (stack.getItem() instanceof ModifiableArmorItem armorItem && armorItem.getOption(stack, livingEntity).hurtEvent())
                armorItem.onLivingHurt(event, stack);
        }
        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            if (ArmorUtils.getArmorSet(attacker) instanceof OptionArmorMaterial attackerSet &&
                    attackerSet.getOption().armorSetHurtOther()) {
                Map<ArmorItem.Type, ModifiableArmorItem> armorItemMap = ModifiableArmorItem.ARMOR_MAP.get(attackerSet);
                if (armorItemMap != null)
                    armorItemMap.get(ArmorItem.Type.CHESTPLATE).onSetHurtOthers(event, attacker, livingEntity);
            }
        }
    }

    @SubscribeEvent
    public static void livingDamageEvent(LivingDamageEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (ArmorUtils.getArmorSet(livingEntity) instanceof OptionArmorMaterial currentSet) {
            if (currentSet.getOption().armorSetDamageEvent()) {
                Map<ArmorItem.Type, ModifiableArmorItem> armorItemMap = ModifiableArmorItem.ARMOR_MAP.get(currentSet);
                if (armorItemMap != null)
                    armorItemMap.get(ArmorItem.Type.CHESTPLATE).onArmorSetLivingDamage(event);
            }
        }
        for (EquipmentSlot slot : ModifiableArmorItem.ARMOR_SLOTS) {
            ItemStack stack = livingEntity.getItemBySlot(slot);
            if (stack.getItem() instanceof ModifiableArmorItem armorItem && armorItem.getOption(stack, livingEntity).damageEvent())
                armorItem.onLivingDamage(event, stack);
        }
    }

    @SubscribeEvent
    public static void livingDamageEvent(LivingDeathEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (ArmorUtils.getArmorSet(livingEntity) instanceof OptionArmorMaterial currentSet) {
            if (currentSet.getOption().armorSetDeathEvent()) {
                Map<ArmorItem.Type, ModifiableArmorItem> armorItemMap = ModifiableArmorItem.ARMOR_MAP.get(currentSet);
                if (armorItemMap != null)
                    armorItemMap.get(ArmorItem.Type.CHESTPLATE).onArmorSetLivingDeath(event);
            }
        }
        for (EquipmentSlot slot : ModifiableArmorItem.ARMOR_SLOTS) {
            ItemStack stack = livingEntity.getItemBySlot(slot);
            if (stack.getItem() instanceof ModifiableArmorItem armorItem && armorItem.getOption(stack, livingEntity).deathEvent())
                armorItem.onLivingDeath(event, stack);
        }
    }
}
