package com.mega.endinglib.api.item.component.type;

import com.mega.endinglib.api.item.component.ItemComponentManager;
import com.mega.endinglib.util.mc.codec.Codecs;
import com.mega.endinglib.util.mc.codec.RegistryCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.extensions.IForgeItemStack;

import java.util.List;
import java.util.Optional;

/**
 * 所有修改调用: <br>
 * @param slot
 * @param equipSound
 * @param assetId
 * @param cameraOverlay
 * @param allowedEntities
 * @param dispensable
 * @param swappable
 * @param damageOnHurt
 * @param equipOnInteract
 * @param canBeSheared 弃用
 * @param shearingSound 弃用
 */
public record EquippableComponent(
        EquipmentSlot slot,
        Holder<SoundEvent> equipSound,
        Optional<ResourceLocation> assetId,
        Optional<ResourceLocation> cameraOverlay,
        Optional<HolderSet<EntityType<?>>> allowedEntities,
        boolean dispensable,
        boolean swappable,
        boolean damageOnHurt,
        boolean equipOnInteract,
        boolean canBeSheared,
        Holder<SoundEvent> shearingSound
) implements Equipable {
    public static final Codec<EquippableComponent> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Codecs.EQUIPMENT_SLOT_CODEC.fieldOf("slot").forGetter(EquippableComponent::slot),
                            SoundEvent.CODEC.optionalFieldOf("equip_sound", Holder.direct(SoundEvents.ARMOR_EQUIP_GENERIC)).forGetter(EquippableComponent::equipSound),
                            ResourceLocation.CODEC.optionalFieldOf("asset_id").forGetter(EquippableComponent::assetId),
                            ResourceLocation.CODEC.optionalFieldOf("camera_overlay").forGetter(EquippableComponent::cameraOverlay),
                            RegistryCodecs.entryList(Registries.ENTITY_TYPE).optionalFieldOf("allowed_entities").forGetter(EquippableComponent::allowedEntities),
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

    public static final DispenseItemBehavior DISPENSE_ITEM_BEHAVIOR = new DefaultDispenseItemBehavior() {
        protected ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
            return dispenseArmor(blockSource, itemStack) ? itemStack : super.execute(blockSource, itemStack);
        }
    };
    public static boolean dispenseArmor(BlockSource blockSource, ItemStack itemStack) {
        BlockPos blockpos = blockSource.getPos().relative(blockSource.getBlockState().getValue(DispenserBlock.FACING));
        List<LivingEntity> list = blockSource.getLevel().getEntitiesOfClass(LivingEntity.class, new AABB(blockpos), EntitySelector.NO_SPECTATORS.and(new EntitySelector.MobCanWearArmorEntitySelector(itemStack)));
        if (list.isEmpty()) {
            return false;
        } else {
            LivingEntity livingentity = list.get(0);
            EquippableComponent equippableComponent = ItemComponentManager.get(itemStack, ItemComponentManager.EQUIPPABLE);
            if (equippableComponent != null) {
                if (!equippableComponent.allows(livingentity.getType()))
                    return false;
            }
            EquipmentSlot equipmentslot = Mob.getEquipmentSlotForItem(itemStack);
            ItemStack itemstack = itemStack.split(1);
            livingentity.setItemSlot(equipmentslot, itemstack);
            if (livingentity instanceof Mob) {
                ((Mob)livingentity).setDropChance(equipmentslot, 2.0F);
                ((Mob)livingentity).setPersistenceRequired();
            }

            return true;
        }
    }
    public InteractionResultHolder<ItemStack> equip(ItemStack stack, Player player) {
        if (this.allows(player.getType())) {
            ItemStack itemStack = player.getItemBySlot(this.slot);
            if ((!EnchantmentHelper.hasBindingCurse(itemStack) || player.isCreative())
                    && !ItemStack.matches(stack, itemStack)) {
                if (!player.level().isClientSide()) {
                    player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
                }

                if (stack.getCount() <= 1) {
                    ItemStack itemStack2 = itemStack.isEmpty() ? stack : itemStack.copyAndClear();
                    ItemStack itemStack3 = player.isCreative() ? stack.copy() : stack.copyAndClear();
                    player.setItemSlot(this.slot, itemStack3);
                    return InteractionResultHolder.success(itemStack2);
                } else {
                    ItemStack itemStack2 = itemStack.copyAndClear();
                    ItemStack itemStack3 = splitUnlessCreative(stack, 1, player);
                    player.setItemSlot(this.slot, itemStack3);
                    if (!player.getInventory().add(itemStack2)) {
                        player.drop(itemStack2, false);
                    }

                    return InteractionResultHolder.success(stack);
                }
            } else {
                return InteractionResultHolder.fail(stack);
            }
        } else {
            return InteractionResultHolder.pass(stack);
        }
    }
    static ItemStack splitUnlessCreative(ItemStack stack, int amount, Player player) {
        ItemStack itemStack = stack.copyWithCount(amount);
        if (player == null || !player.getAbilities().instabuild)
            stack.shrink(amount);
        return itemStack;
    }
    public InteractionResult equipOnInteract(Player player, LivingEntity entity, ItemStack stack) {
        if (this.allows(entity.getType())) {
            if (entity instanceof Saddleable saddleable && saddleable.isSaddled())
                return InteractionResult.PASS;
            else if (entity instanceof ArmorStand stand && !stand.canTakeItem(stack))
                return InteractionResult.PASS;
            if (!entity.hasItemInSlot(this.slot) && entity.isAlive()) {
                if (!player.level().isClientSide) {
                    entity.setItemSlot(this.slot, stack.split(1));
                    if (entity instanceof Mob mobEntity) {
                        mobEntity.setGuaranteedDrop(this.slot);
                    }
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    public boolean allows(EntityType<?> entityType) {
        return this.allowedEntities.isEmpty() || (this.allowedEntities.get()).contains(entityType.builtInRegistryHolder());
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return this.slot;
    }

    @Override
    public SoundEvent getEquipSound() {
        return this.equipSound.value();
    }
}
