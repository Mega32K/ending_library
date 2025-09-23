package com.mega.endinglib.api.item.consume;

import com.mega.endinglib.api.item.consume.impl.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface ConsumeEffect {
    Codec<ConsumeEffect> CODEC = ConsumeManager.TYPE_CODEC.dispatch(ConsumeEffect::getType, type -> type.codec().codec());
    ConsumeEffect.Type<? extends ConsumeEffect> getType();
    boolean onConsume(Level level, ItemStack stack, LivingEntity user);
    record Type<T extends ConsumeEffect>(ResourceLocation id, MapCodec<T> codec) {
        public static final ConsumeEffect.Type<ApplyEffectsConsumeEffect> APPLY_EFFECTS = ConsumeManager.register(
                "apply_effects", ApplyEffectsConsumeEffect.CODEC
        );
        public static final ConsumeEffect.Type<RemoveEffectsConsumeEffect> REMOVE_EFFECTS = ConsumeManager.register(
                "remove_effects", RemoveEffectsConsumeEffect.CODEC
        );
        public static final ConsumeEffect.Type<ClearAllEffectsConsumeEffect> CLEAR_ALL_EFFECTS = ConsumeManager.register(
                "clear_all_effects", ClearAllEffectsConsumeEffect.CODEC
        );
        public static final ConsumeEffect.Type<TeleportRandomlyConsumeEffect> TELEPORT_RANDOMLY = ConsumeManager.register(
                "teleport_randomly", TeleportRandomlyConsumeEffect.CODEC
        );
        public static final ConsumeEffect.Type<PlaySoundConsumeEffect> PLAY_SOUND = ConsumeManager.register(
                "play_sound", PlaySoundConsumeEffect.CODEC
        );

    }
}
