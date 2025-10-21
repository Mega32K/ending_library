package com.mega.endinglib.api.item.component.type;

import com.mega.endinglib.util.mc.codec.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

public record BlocksAttacksComponent(
        float blockDelaySeconds,
        float disableCooldownScale,
        List<BlocksAttacksComponent.DamageReduction> damageReductions,
        BlocksAttacksComponent.ItemDamage itemDamage,
        Optional<TagKey<DamageType>> bypassedBy,
        Optional<Holder<SoundEvent>> blockSound,
        Optional<Holder<SoundEvent>> disableSound
) {
    public static final Codec<BlocksAttacksComponent> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Codecs.NON_NEGATIVE_FLOAT.optionalFieldOf("block_delay_seconds", 0.0F).forGetter(BlocksAttacksComponent::blockDelaySeconds),
                            Codecs.NON_NEGATIVE_FLOAT.optionalFieldOf("disable_cooldown_scale", 1.0F).forGetter(BlocksAttacksComponent::disableCooldownScale),
                            Codecs.canSerializeAsSingleList(BlocksAttacksComponent.DamageReduction.CODEC)
                                    .optionalFieldOf("damage_reductions", List.of(new BlocksAttacksComponent.DamageReduction(90.0F, Optional.empty(), 0.0F, 1.0F)))
                                    .forGetter(BlocksAttacksComponent::damageReductions),
                            BlocksAttacksComponent.ItemDamage.CODEC
                                    .optionalFieldOf("item_damage", BlocksAttacksComponent.ItemDamage.DEFAULT)
                                    .forGetter(BlocksAttacksComponent::itemDamage),
                            TagKey.hashedCodec(Registries.DAMAGE_TYPE).optionalFieldOf("bypassed_by").forGetter(BlocksAttacksComponent::bypassedBy),
                            SoundEvent.CODEC.optionalFieldOf("block_sound").forGetter(BlocksAttacksComponent::blockSound),
                            SoundEvent.CODEC.optionalFieldOf("disabled_sound").forGetter(BlocksAttacksComponent::disableSound)
                    )
                    .apply(instance, BlocksAttacksComponent::new)
    );

    public void playBlockSound(Level level, LivingEntity from) {
        this.blockSound
                .ifPresent(
                        sound -> {
                            level.playSound(null, from.getX(), from.getY(), from.getZ(), sound.value(), from.getSoundSource(), 1.0F, 0.8F + level.random.nextFloat() * 0.4F);
                        }
                );
    }

    public void applyShieldCooldown(Level world, LivingEntity affectedEntity, float cooldownSeconds, ItemStack stack) {
        int i = this.convertCooldownToTicks(cooldownSeconds);
        if (i > 0) {
            if (affectedEntity instanceof Player playerEntity) {
                playerEntity.getCooldowns().addCooldown(stack.getItem(), i);
            }

            affectedEntity.stopUsingItem();
            this.disableSound
                    .ifPresent(
                            sound -> world.playSound(
                                    null,
                                    affectedEntity.getX(),
                                    affectedEntity.getY(),
                                    affectedEntity.getZ(),
                                    sound.value(),
                                    affectedEntity.getSoundSource(),
                                    0.8F,
                                    0.8F + world.random.nextFloat() * 0.4F
                            )
                    );
        }
    }

    public void onShieldHit(Level world, ItemStack stack, LivingEntity entity, InteractionHand hand, float itemDamage) {
        if (entity instanceof Player playerEntity) {
            if (!world.isClientSide) {
                playerEntity.awardStat(Stats.ITEM_USED.get(stack.getItem()));
            }

            int i = this.itemDamage.calculate(itemDamage);
            if (i > 0) {
                stack.hurtAndBreak(i, entity, (c) -> c.broadcastBreakEvent(hand));
            }
        }
    }

    private int convertCooldownToTicks(float cooldownSeconds) {
        float f = cooldownSeconds * this.disableCooldownScale;
        return f > 0.0F ? Math.round(f * 20.0F) : 0;
    }

    public int getBlockDelayTicks() {
        return Math.round(this.blockDelaySeconds * 20.0F);
    }

    public float getDamageReductionAmount(DamageSource source, float damage, double angle) {
        float f = 0.0F;

        for (BlocksAttacksComponent.DamageReduction damageReduction : this.damageReductions) {
            f += damageReduction.getReductionAmount(source, damage, angle);
        }

        return Mth.clamp(f, 0.0F, damage);
    }

    public record DamageReduction(float horizontalBlockingAngle, Optional<HolderSet<DamageType>> type, float base, float factor) {
        public static final Codec<BlocksAttacksComponent.DamageReduction> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                Codecs.POSITIVE_FLOAT.optionalFieldOf("horizontal_blocking_angle", 90.0F).forGetter(BlocksAttacksComponent.DamageReduction::horizontalBlockingAngle),
                                RegistryCodecs.homogeneousList(Registries.DAMAGE_TYPE).optionalFieldOf("type").forGetter(BlocksAttacksComponent.DamageReduction::type),
                                Codec.FLOAT.fieldOf("base").forGetter(BlocksAttacksComponent.DamageReduction::base),
                                Codec.FLOAT.fieldOf("factor").forGetter(BlocksAttacksComponent.DamageReduction::factor)
                        )
                        .apply(instance, BlocksAttacksComponent.DamageReduction::new)
        );

        public float getReductionAmount(DamageSource source, float damage, double angle) {
            if (angle > (float) Mth.DEG_TO_RAD * this.horizontalBlockingAngle) {
                return 0.0F;
            } else {
                return this.type.isPresent() && !(this.type.get()).contains(source.typeHolder())
                        ? 0.0F
                        : Mth.clamp(this.base + this.factor * damage, 0.0F, damage);
            }
        }
    }

    public record ItemDamage(float threshold, float base, float factor) {
        public static final Codec<BlocksAttacksComponent.ItemDamage> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                Codecs.NON_NEGATIVE_FLOAT.fieldOf("threshold").forGetter(BlocksAttacksComponent.ItemDamage::threshold),
                                Codec.FLOAT.fieldOf("base").forGetter(BlocksAttacksComponent.ItemDamage::base),
                                Codec.FLOAT.fieldOf("factor").forGetter(BlocksAttacksComponent.ItemDamage::factor)
                        )
                        .apply(instance, BlocksAttacksComponent.ItemDamage::new)
        );
        public static final BlocksAttacksComponent.ItemDamage DEFAULT = new BlocksAttacksComponent.ItemDamage(1.0F, 0.0F, 1.0F);

        public int calculate(float itemDamage) {
            return itemDamage < this.threshold ? 0 : Mth.floor(this.base + this.factor * itemDamage);
        }
    }
}
