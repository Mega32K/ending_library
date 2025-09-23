package com.mega.endinglib.api.item.consume.impl;

import com.mega.endinglib.api.item.consume.ConsumeEffect;
import com.mega.endinglib.util.codec.Codecs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public record TeleportRandomlyConsumeEffect(float diameter) implements ConsumeEffect {
    private static final float DEFAULT_DIAMETER = 16.0F;
    public static final MapCodec<TeleportRandomlyConsumeEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(Codecs.POSITIVE_FLOAT.optionalFieldOf("diameter", 16.0F).forGetter(TeleportRandomlyConsumeEffect::diameter))
                    .apply(instance, TeleportRandomlyConsumeEffect::new)
    );

    public TeleportRandomlyConsumeEffect() {
        this(16.0F);
    }

    @Override
    public ConsumeEffect.Type<TeleportRandomlyConsumeEffect> getType() {
        return ConsumeEffect.Type.TELEPORT_RANDOMLY;
    }

    @Override
    public boolean onConsume(Level level, ItemStack stack, LivingEntity user) {
        boolean bl = false;

        double d0 = user.getX();
        double d1 = user.getY();
        double d2 = user.getZ();
        for (int i = 0; i < 16; i++) {
            double d = user.getX() + (user.getRandom().nextDouble() - 0.5) * this.diameter;
            double e = Mth.clamp(
                    user.getY() + (double)(user.getRandom().nextInt(16) - 8),
                    level.getMinBuildHeight(),
                    level.getMinBuildHeight() + ((ServerLevel)level).getLogicalHeight() - 1);
            double f = user.getZ() + (user.getRandom().nextDouble() - 0.5) * this.diameter;
            if (user.isPassenger()) {
                user.stopRiding();
            }

            Vec3 vec3d = user.position();
            level.gameEvent(GameEvent.TELEPORT, vec3d, GameEvent.Context.of(user));
            net.minecraftforge.event.entity.EntityTeleportEvent.ChorusFruit event = net.minecraftforge.event.ForgeEventFactory.onChorusFruitTeleport(user, d, e, f);
            if (event.isCanceled()) return false;
            if (user.randomTeleport(d, e, f, true)) {
                SoundEvent soundevent = user instanceof Fox ? SoundEvents.FOX_TELEPORT : SoundEvents.CHORUS_FRUIT_TELEPORT;
                level.playSound(null, d0, d1, d2, soundevent, SoundSource.PLAYERS, 1.0F, 1.0F);
                user.playSound(soundevent, 1.0F, 1.0F);
                bl = true;
                break;
            }
        } 

        return bl;
    }
}
