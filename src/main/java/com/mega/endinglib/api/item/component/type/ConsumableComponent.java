package com.mega.endinglib.api.item.component.type;

import com.mega.endinglib.api.item.component.DataComponents;
import com.mega.endinglib.api.item.component.ItemComponentManager;
import com.mega.endinglib.api.item.consume.ConsumeEffect;
import com.mega.endinglib.api.item.consume.impl.PlaySoundConsumeEffect;
import com.mega.endinglib.mixin.accessor.AccessorLivingEntity;
import com.mega.endinglib.util.mc.codec.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.List;

public record ConsumableComponent(
        float consumeSeconds,
        UseAnim useAnimation,
        Holder<SoundEvent> sound,
        boolean hasConsumeParticles,
        List<ConsumeEffect> onConsumeEffects) {
    public static final float DEFAULT_CONSUME_SECONDS = 1.6F;
    private static final int PARTICLES_AND_SOUND_TICK_INTERVAL = 4;
    private static final float PARTICLES_AND_SOUND_TICK_THRESHOLD = 0.21875F;
    public static final Codec<ConsumableComponent> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Codecs.NON_NEGATIVE_FLOAT.optionalFieldOf("consume_seconds", DEFAULT_CONSUME_SECONDS).forGetter(ConsumableComponent::consumeSeconds),
                            Codecs.USE_ANIM_CODEC.optionalFieldOf("animation", UseAnim.EAT).forGetter(ConsumableComponent::useAnimation),
                            SoundEvent.CODEC.optionalFieldOf("sound", Holder.direct(SoundEvents.GENERIC_EAT)).forGetter(ConsumableComponent::sound),
                            Codec.BOOL.optionalFieldOf("has_consume_particles", true).forGetter(ConsumableComponent::hasConsumeParticles),
                            Codecs.canSerializeAsSingleList(ConsumeEffect.CODEC).optionalFieldOf("on_consume_effects", List.of()).forGetter(ConsumableComponent::onConsumeEffects)
                    )
                    .apply(instance, ConsumableComponent::new)
    );
    public InteractionResultHolder<ItemStack> consume(LivingEntity user, ItemStack stack, InteractionHand hand) {
        if (!this.canConsume(user, stack)) {
            return InteractionResultHolder.fail(stack);
        } else {
            boolean bl = this.getConsumeTicks() > 0;
            if (bl) {
                user.startUsingItem(hand);
                return InteractionResultHolder.consume(stack);
            } else {
                ItemStack itemStack = this.finishConsumption(user.level(), user, stack);
                return InteractionResultHolder.consume(itemStack);
            }
        }
    }

    public ItemStack finishConsumption(Level level, LivingEntity user, ItemStack stack) {
        RandomSource random = user.getRandom();
        this.spawnParticlesAndPlaySound(user, stack, 16);
        if (user instanceof ServerPlayer serverPlayer) {
            serverPlayer.awardStat(Stats.ITEM_USED.get(stack.getItem()));
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
        }

        ItemComponentManager.get(stack).getComponents().streamAll(Consumable.class).forEach(consumable -> consumable.onConsume(level, user, stack, this));
        if (!level.isClientSide) {
            this.onConsumeEffects.forEach(effect -> effect.onConsume(level, stack, user));
        }

        user.gameEvent(this.useAnimation == UseAnim.DRINK ? GameEvent.DRINK : GameEvent.EAT);
        if (user instanceof Player player && !player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return stack;
    }
    public boolean canConsume(LivingEntity user, ItemStack stack) {
        FoodComponent foodComponent = ItemComponentManager.get(stack, DataComponents.FOOD);
        return foodComponent == null || !(user instanceof Player playerEntity) || playerEntity.canEat(foodComponent.canAlwaysEat());
    }

    public int getConsumeTicks() {
        return (int)(this.consumeSeconds * 20.0F);
    }
    public void spawnParticlesAndPlaySound(LivingEntity user, ItemStack itemStack, int particleCount) {
        RandomSource random = user.getRandom();
        float f = random.nextBoolean() ? 0.5F : 1.0F;
        float g = (float) random.triangle(1.0F, 0.2F);
        float h = 0.5F;
        float i = random.nextFloat() * 0.1F + 0.9F;
        float j = this.useAnimation == UseAnim.DRINK ? 0.5F : f;
        float k = this.useAnimation == UseAnim.DRINK ? i : g;

        SoundEvent soundEvent = user instanceof ConsumableComponent.ConsumableSoundProvider consumableSoundProvider
                ? consumableSoundProvider.getConsumeSound(itemStack)
                : this.sound.value();
        if (this.hasConsumeParticles()) {
            ((AccessorLivingEntity) user).callSpawnItemParticles(itemStack, particleCount);
        }
        user.playSound(soundEvent, j, k);
    }

    public boolean shouldSpawnParticlesAndPlaySounds(int remainingUseTicks) {
        int i = this.getConsumeTicks() - remainingUseTicks;
        int j = (int)(this.getConsumeTicks() * 0.21875F);
        boolean bl = i > j;
        return bl && remainingUseTicks % 4 == 0;
    }
    public static ConsumableComponent.Builder builder() {
        return new ConsumableComponent.Builder();
    }

    public static class Builder {
        private float consumeSeconds = DEFAULT_CONSUME_SECONDS;
        private UseAnim useAction = UseAnim.EAT;
        private Holder<SoundEvent> sound = Holder.direct(SoundEvents.GENERIC_EAT);
        private boolean consumeParticles = true;
        private final List<ConsumeEffect> consumeEffects = new ObjectArrayList<>();

        Builder() {
        }

        public ConsumableComponent.Builder consumeSeconds(float consumeSeconds) {
            this.consumeSeconds = consumeSeconds;
            return this;
        }

        public ConsumableComponent.Builder useAction(UseAnim useAction) {
            this.useAction = useAction;
            return this;
        }

        public ConsumableComponent.Builder sound(Holder<SoundEvent> sound) {
            this.sound = sound;
            return this;
        }

        public ConsumableComponent.Builder finishSound(Holder<SoundEvent> finishSound) {
            return this.consumeEffect(new PlaySoundConsumeEffect(finishSound));
        }

        public ConsumableComponent.Builder consumeParticles(boolean consumeParticles) {
            this.consumeParticles = consumeParticles;
            return this;
        }

        public ConsumableComponent.Builder consumeEffect(ConsumeEffect consumeEffect) {
            this.consumeEffects.add(consumeEffect);
            return this;
        }

        public ConsumableComponent build() {
            return new ConsumableComponent(this.consumeSeconds, this.useAction, this.sound, this.consumeParticles, this.consumeEffects);
        }
    }

    public interface ConsumableSoundProvider {
        SoundEvent getConsumeSound(ItemStack stack);
    }
}
