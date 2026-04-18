package com.mega.endinglib.util.mc.entity;

import com.mega.endinglib.api.entity.MobEffectInstanceItf;
import com.mega.endinglib.mixin.accessor.AccessorLivingEntity;
import it.unimi.dsi.fastutil.ints.Int2IntMaps;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class MobEffectUtils {
    public static void forceAdd(LivingEntity willBeAffect, MobEffectInstance mi, @Nullable Entity entity) {
        MobEffectInstanceItf miItf = (MobEffectInstanceItf) mi;
        miItf.setOwnerEntity(entity);
        if (!willBeAffect.addEffect(mi)) {
            //若正常赋予buff未被允许
            MobEffect mobEffect = mi.getEffect();
            MobEffectInstance oldEffect = willBeAffect.getEffect(mobEffect);
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.living.MobEffectEvent.Added(willBeAffect, oldEffect, mi, entity));
            if (oldEffect == null) {
                try {
                    willBeAffect.getActiveEffectsMap().put(mobEffect, mi);
                } catch (UnsupportedOperationException e)  {
                    return;
                }
                ((AccessorLivingEntity) willBeAffect).callOnEffectAdded(mi, entity);
            } else if (oldEffect.update(mi)) {
                ((AccessorLivingEntity) willBeAffect).callOnEffectUpdated(oldEffect, true, entity);
            }
        }
    }
}
