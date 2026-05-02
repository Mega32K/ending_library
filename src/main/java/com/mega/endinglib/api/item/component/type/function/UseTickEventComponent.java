package com.mega.endinglib.api.item.component.type.function;

import com.mega.endinglib.mixin.accessor.AccessorCommandSourceStack;
import com.mega.endinglib.util.mc.codec.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;

public record UseTickEventComponent(String command, Optional<InteractionHand> onlyInHand, Optional<ResourceLocation> function, Optional<Integer> remainingRequiredMin, Optional<Integer> remainingRequiredMax, int minimumPermission, boolean silent) implements FunctionComponent {
    public static Codec<UseTickEventComponent> CODEC = RecordCodecBuilder.create(
            com -> com.group(
                    Codec.STRING.optionalFieldOf("command","").forGetter(UseTickEventComponent::command),
                    Codecs.HAND_CODEC.optionalFieldOf("only_in_hand").forGetter(UseTickEventComponent::onlyInHand),
                    ResourceLocation.CODEC.optionalFieldOf("function").forGetter(UseTickEventComponent::function),
                    Codec.INT.optionalFieldOf("remaining_required_min").forGetter(UseTickEventComponent::remainingRequiredMin),
                    Codec.INT.optionalFieldOf("remaining_required_max").forGetter(UseTickEventComponent::remainingRequiredMax),
                    Codecs.NON_NEGATIVE_INT.optionalFieldOf("min_permission", 2).forGetter(UseTickEventComponent::minimumPermission),
                    Codec.BOOL.optionalFieldOf("silent", true).forGetter(UseTickEventComponent::silent)
            ).apply(com, UseTickEventComponent::new)
    );
    public void apply(ServerLevel serverLevel, LivingEntity user, InteractionHand hand, int timeLeft) {
        if (this.onlyInHand.isEmpty() || this.onlyInHand.get().equals(hand)) {
            if (this.remainingRequiredMin.map(min -> timeLeft >= min).orElse(true)) {
                if (this.remainingRequiredMax.map(max -> timeLeft <= max).orElse(true)) {
                    if (!command.isEmpty()) {
                        CommandSourceStack sourceStack = user.createCommandSourceStack().withMaximumPermission(minimumPermission);
                        ((AccessorCommandSourceStack) sourceStack).setSilent(silent);
                        serverLevel.getServer().getCommands().performPrefixedCommand(sourceStack, this.command);
                    }
                    function.ifPresent(location -> this.apply(user, location, minimumPermission));
                }
            }
        }
    }
}
