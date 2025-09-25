package com.mega.endinglib.api.item.component.type;

import com.mega.endinglib.util.mc.codec.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record WeaponComponent(int itemDamagePerAttack, float disableBlockingForSeconds) {
    public static final float AXE_DISABLE_BLOCKING_FOR_SECONDS = 5.0F;
    public static final Codec<WeaponComponent> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Codecs.NON_NEGATIVE_INT.optionalFieldOf("item_damage_per_attack", 1).forGetter(WeaponComponent::itemDamagePerAttack),
                            Codecs.NON_NEGATIVE_FLOAT.optionalFieldOf("disable_blocking_for_seconds", 0.0F).forGetter(WeaponComponent::disableBlockingForSeconds)
                    )
                    .apply(instance, WeaponComponent::new)
    );

    public WeaponComponent(int itemDamagePerAttack) {
        this(itemDamagePerAttack, 0.0F);
    }
}
