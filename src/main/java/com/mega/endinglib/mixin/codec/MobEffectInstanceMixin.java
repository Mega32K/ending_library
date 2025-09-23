package com.mega.endinglib.mixin.codec;

import com.mega.endinglib.util.codec.impl.MobEffectInstanceParameters;
import com.mega.endinglib.util.mixin.data_expand.ExtraMobEffectInstanceItf;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.Optional;

@Mixin(MobEffectInstance.class)
public abstract class MobEffectInstanceMixin implements ExtraMobEffectInstanceItf {
    @Shadow @Nullable private MobEffectInstance hiddenEffect;

    @Shadow public abstract int getAmplifier();

    @Shadow public abstract int getDuration();

    @Shadow public abstract boolean isAmbient();

    @Shadow public abstract boolean isVisible();

    @Shadow private boolean showIcon;

    @Override
    public MobEffectInstanceParameters asParameters() {
        return new MobEffectInstanceParameters(
                this.getAmplifier(),
                this.getDuration(),
                this.isAmbient(),
                this.isVisible(),
                this.showIcon,
                Optional.ofNullable(this.hiddenEffect).map(p -> ((ExtraMobEffectInstanceItf) p).asParameters())
        );
    }
}
