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
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public record UseEventComponent(String command, Optional<InteractionHand> onlyInHand, Optional<ResourceLocation> function, int cooldownTicks, int minimumPermission) implements FunctionComponent {
    public static Codec<UseEventComponent> CODEC = RecordCodecBuilder.create(
            com -> com.group(
                    Codec.STRING.optionalFieldOf("command","").forGetter(UseEventComponent::command),
                    Codecs.HAND_CODEC.optionalFieldOf("only_in_hand").forGetter(UseEventComponent::onlyInHand),
                    ResourceLocation.CODEC.optionalFieldOf("function").forGetter(UseEventComponent::function),
                    Codec.INT.optionalFieldOf("cooldown_ticks", 0).forGetter(UseEventComponent::cooldownTicks),
                    Codecs.NON_NEGATIVE_INT.optionalFieldOf("min_permission", 2).forGetter(UseEventComponent::minimumPermission)
            ).apply(com, UseEventComponent::new)
    );
    public void apply(ServerLevel serverLevel, LivingEntity user, InteractionHand hand) {
        if (this.onlyInHand.isEmpty() || this.onlyInHand.get().equals(hand)) {
            if (!command.isEmpty()) {
                CommandSourceStack sourceStack = user.createCommandSourceStack().withMaximumPermission(minimumPermission);
                ((AccessorCommandSourceStack) sourceStack).setSilent(true);
                serverLevel.getServer().getCommands().performPrefixedCommand(sourceStack, this.command);
            }
            function.ifPresent(location -> this.apply(user, location, minimumPermission));
            if (user instanceof Player player) {
                if (cooldownTicks != 0)
                    player.getCooldowns().addCooldown(user.getItemInHand(hand).getItem(), cooldownTicks);
            }
        }
    }
}
