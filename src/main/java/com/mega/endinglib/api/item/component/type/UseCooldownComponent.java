package com.mega.endinglib.api.item.component.type;

import com.mega.endinglib.util.mc.codec.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

public record UseCooldownComponent(float seconds, boolean canUseWhenRelease) {
    public static final Codec<UseCooldownComponent> CODEC = RecordCodecBuilder.create(
            component -> component.group(
                    Codecs.POSITIVE_FLOAT.fieldOf("seconds").forGetter(UseCooldownComponent::seconds),
                    Codec.BOOL.optionalFieldOf("release_applicable", true).forGetter(UseCooldownComponent::canUseWhenRelease)
            ).apply(component, UseCooldownComponent::new)
    );

    public int ticks() {
        return (int)(this.seconds * 20.0F);
    }

    public void apply(LivingEntity entity, Item item) {
        if (entity instanceof Player player) {
            player.getCooldowns().addCooldown(item, this.ticks());
        }

    }
}
