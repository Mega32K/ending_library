package com.mega.endinglib.util.mc.codec.impl;

import com.mega.endinglib.util.mc.codec.Codecs;
import com.mega.endinglib.util.mixin.data_expand.ExtraMobEffectInstanceItf;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.Optional;

public record MobEffectInstanceParameters(
        int amplifier, int duration, boolean ambient, boolean showParticles, boolean showIcon, Optional<MobEffectInstanceParameters> hiddenEffect
) {
    public static final MapCodec<MobEffectInstanceParameters> CODEC = Codecs.recursive(
            "MobEffectInstance.Details",
            codec -> RecordCodecBuilder.mapCodec(
                    instance -> instance.group(
                                    Codecs.UNSIGNED_BYTE.optionalFieldOf("amplifier", 0).forGetter(MobEffectInstanceParameters::amplifier),
                                    Codec.INT.optionalFieldOf("duration", 0).forGetter(MobEffectInstanceParameters::duration),
                                    Codec.BOOL.optionalFieldOf("ambient", false).forGetter(MobEffectInstanceParameters::ambient),
                                    Codec.BOOL.optionalFieldOf("show_particles", true).forGetter(MobEffectInstanceParameters::showParticles),
                                    Codec.BOOL.optionalFieldOf("show_icon").forGetter(parameters -> Optional.of(parameters.showIcon())),
                                    codec.optionalFieldOf("hidden_effect").forGetter(MobEffectInstanceParameters::hiddenEffect)
                            )
                            .apply(instance, MobEffectInstanceParameters::create)
            )
    );

    private static MobEffectInstanceParameters create(
            int amplifier, int duration, boolean ambient, boolean showParticles, Optional<Boolean> showIcon, Optional<MobEffectInstanceParameters> hiddenEffect
    ) {
        return new MobEffectInstanceParameters(amplifier, duration, ambient, showParticles, (Boolean)showIcon.orElse(showParticles), hiddenEffect);
    }
    public static MobEffectInstance fromParameters(MobEffect mobEffect, MobEffectInstanceParameters parameters) {
        return new MobEffectInstance(
                mobEffect,
                parameters.duration(),
                parameters.amplifier(),
                parameters.ambient(),
                parameters.showParticles(),
                parameters.showIcon(),
                parameters.hiddenEffect().map(par -> fromParameters(mobEffect, parameters)).orElse(null),
                Optional.empty());
    }


    private MobEffectInstanceParameters asParameters(MobEffectInstance instance) {
        return ((ExtraMobEffectInstanceItf) instance).asParameters();
    }
}
