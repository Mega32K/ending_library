package com.mega.endinglib.api.item.component.type.command;

import com.mega.endinglib.util.mc.codec.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;

public record UseEventComponent(String command, Optional<InteractionHand> onlyInHand, Optional<ResourceLocation> function, int minimumPermission) implements FunctionComponent {
    public static Codec<UseEventComponent> CODEC = RecordCodecBuilder.create(
            com -> com.group(
                    Codec.STRING.optionalFieldOf("command","").forGetter(UseEventComponent::command),
                    Codecs.HAND_CODEC.optionalFieldOf("only_in_hand").forGetter(UseEventComponent::onlyInHand),
                    ResourceLocation.CODEC.optionalFieldOf("function").forGetter(UseEventComponent::function),
                    Codecs.NON_NEGATIVE_INT.optionalFieldOf("min_permission", 2).forGetter(UseEventComponent::minimumPermission)
            ).apply(com, UseEventComponent::new)
    );
    public void apply(ServerLevel serverLevel, LivingEntity user, InteractionHand hand) {
        if (this.onlyInHand.isEmpty() || this.onlyInHand.get().equals(hand)) {
            if (!command.isEmpty())
                serverLevel.getServer().getCommands().performPrefixedCommand(user.createCommandSourceStack(), command);
            function.ifPresent(location -> this.apply(user, location, minimumPermission));
        }
    }
}
