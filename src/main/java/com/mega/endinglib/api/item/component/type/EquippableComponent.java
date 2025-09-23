package com.mega.endinglib.api.item.component.type;

import com.mega.endinglib.util.codec.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public record EquippableComponent(
        EquipmentSlot slot,
        Holder<SoundEvent> equipSound,
        Optional<ResourceLocation> assetId,
        Optional<ResourceLocation> cameraOverlay,
        Optional<List<EntityType<?>>> allowedEntities,
        boolean dispensable,
        boolean swappable,
        boolean damageOnHurt,
        boolean equipOnInteract,
        boolean canBeSheared,
        Holder<SoundEvent> shearingSound
) {
    public static final Codec<EquippableComponent> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Codecs.EQUIPMENT_SLOT_CODEC.fieldOf("slot").forGetter(EquippableComponent::slot),
                            SoundEvent.CODEC.optionalFieldOf("equip_sound", Holder.direct(SoundEvents.ARMOR_EQUIP_GENERIC)).forGetter(EquippableComponent::equipSound),
                            ResourceLocation.CODEC.optionalFieldOf("asset_id").forGetter(EquippableComponent::assetId),
                            ResourceLocation.CODEC.optionalFieldOf("camera_overlay").forGetter(EquippableComponent::cameraOverlay),
                            Codecs.ENTITY_TYPE_DIRECT_CODEC.listOf().optionalFieldOf("allowed_entities").forGetter(EquippableComponent::allowedEntities),
                            Codec.BOOL.optionalFieldOf("dispensable", true).forGetter(EquippableComponent::dispensable),
                            Codec.BOOL.optionalFieldOf("swappable", true).forGetter(EquippableComponent::swappable),
                            Codec.BOOL.optionalFieldOf("damage_on_hurt", true).forGetter(EquippableComponent::damageOnHurt),
                            Codec.BOOL.optionalFieldOf("equip_on_interact", false).forGetter(EquippableComponent::equipOnInteract),
                            Codec.BOOL.optionalFieldOf("can_be_sheared", false).forGetter(EquippableComponent::canBeSheared),
                            SoundEvent.CODEC
                                    .optionalFieldOf("shearing_sound", Holder.direct(SoundEvents.SNOW_GOLEM_SHEAR))
                                    .forGetter(EquippableComponent::shearingSound)
                    )
                    .apply(instance, EquippableComponent::new)
    );
    public InteractionResultHolder<ItemStack> equip(ItemStack stack, Player player) {
        if (this.allows(player.getType())) {
            ItemStack itemStack = player.getEquippedStack(this.slot);
            if ((!EnchantmentHelper.hasAnyEnchantmentsWith(itemStack, EnchantmentEffectComponentTypes.PREVENT_ARMOR_CHANGE) || player.isCreative())
                    && !ItemStack.areItemsAndComponentsEqual(stack, itemStack)) {
                if (!player.getWorld().isClient()) {
                    player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
                }

                if (stack.getCount() <= 1) {
                    ItemStack itemStack2 = itemStack.isEmpty() ? stack : itemStack.copyAndEmpty();
                    ItemStack itemStack3 = player.isCreative() ? stack.copy() : stack.copyAndEmpty();
                    player.equipStack(this.slot, itemStack3);
                    return InteractionResultHolder<ItemStack>.SUCCESS.withNewHandStack(itemStack2);
                } else {
                    ItemStack itemStack2 = itemStack.copyAndEmpty();
                    ItemStack itemStack3 = stack.splitUnlessCreative(1, player);
                    player.equipStack(this.slot, itemStack3);
                    if (!player.getInventory().insertStack(itemStack2)) {
                        player.dropItem(itemStack2, false);
                    }

                    return InteractionResultHolder<ItemStack>.SUCCESS.withNewHandStack(stack);
                }
            } else {
                return InteractionResultHolder<ItemStack>.FAIL;
            }
        } else {
            return InteractionResultHolder<ItemStack>.PASS;
        }
    }

    public InteractionResultHolder<ItemStack> equipOnInteract(PlayerEntity player, LivingEntity entity, ItemStack stack) {
        if (entity.canEquip(stack, this.slot) && !entity.hasStackEquipped(this.slot) && entity.isAlive()) {
            if (!player.getWorld().isClient()) {
                entity.equipStack(this.slot, stack.split(1));
                if (entity instanceof MobEntity mobEntity) {
                    mobEntity.setDropGuaranteed(this.slot);
                }
            }

            return InteractionResultHolder<ItemStack>.SUCCESS;
        } else {
            return InteractionResultHolder<ItemStack>.PASS;
        }
    }

    public boolean allows(EntityType<?> entityType) {
        return this.allowedEntities.isEmpty() || (this.allowedEntities.get()).contains(entityType);
    }
}
