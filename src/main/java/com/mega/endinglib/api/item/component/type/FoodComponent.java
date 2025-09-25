package com.mega.endinglib.api.item.component.type;

import com.mega.endinglib.util.mc.codec.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public record FoodComponent(int nutrition, float saturation, boolean canAlwaysEat) implements Consumable {
    public static final Codec<FoodComponent> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Codecs.NON_NEGATIVE_INT.fieldOf("nutrition").forGetter(FoodComponent::nutrition),
                            Codec.FLOAT.fieldOf("saturation").forGetter(FoodComponent::saturation),
                            Codec.BOOL.optionalFieldOf("can_always_eat", false).forGetter(FoodComponent::canAlwaysEat)
                    )
                    .apply(instance, FoodComponent::new)
    );
    @Override
    public void onConsume(Level world, LivingEntity user, ItemStack stack, ConsumableComponent consumable) {
        RandomSource random = user.getRandom();
        world.playSound(null, user.getX(), user.getY(), user.getZ(), consumable.sound().value(), SoundSource.NEUTRAL, 1.0F, (float) random.triangle(1.0F, 0.4F));
        if (user instanceof Player playerEntity) {
            playerEntity.getFoodData().eat(this.nutrition(), this.saturation());
            world.playSound(
                    null,
                    playerEntity.getX(),
                    playerEntity.getY(),
                    playerEntity.getZ(),
                    SoundEvents.PLAYER_BURP,
                    SoundSource.PLAYERS,
                    0.5F,
                    random.nextFloat() * 0.1F + 0.9F
            );
        }
    }

    public static class Builder {
        private int nutrition;
        private float saturation;
        private boolean canAlwaysEat;

        /**
         * Specifies the amount of hunger a food item will fill.
         *
         * <p>One hunger is equivalent to half of a hunger bar icon.
         *
         * @param nutrition the amount of hunger
         */
        public FoodComponent.Builder nutrition(int nutrition) {
            this.nutrition = nutrition;
            return this;
        }
        public FoodComponent.Builder saturation(float saturation) {
            this.saturation = saturation;
            return this;
        }

        /**
         * Specifies that a food item can be eaten when the current hunger bar is full.
         */
        public FoodComponent.Builder alwaysEdible() {
            this.canAlwaysEat = true;
            return this;
        }

        public FoodComponent build() {
            return new FoodComponent(this.nutrition, this.saturation, this.canAlwaysEat);
        }
    }
}
