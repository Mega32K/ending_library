package com.mega.endinglib.api.item.component.type.command;

import com.mega.endinglib.util.mc.codec.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;

public record UseEventComponent(String command, Optional<InteractionHand> onlyInHand) {
    public static Codec<UseEventComponent> CODEC = RecordCodecBuilder.create(
            com -> com.group(
                    Codec.STRING.fieldOf("command").forGetter(UseEventComponent::command),
                    Codecs.HAND_CODEC.optionalFieldOf("only_in_hand").forGetter(UseEventComponent::onlyInHand)
            ).apply(com, UseEventComponent::new)
    );
    public void apply(ServerLevel serverLevel, LivingEntity user, InteractionHand hand) {
        if (this.onlyInHand.isEmpty() || this.onlyInHand.get().equals(hand)) {
            serverLevel.getServer().getCommands().performPrefixedCommand(user.createCommandSourceStack(), command);
        }
    }
}
