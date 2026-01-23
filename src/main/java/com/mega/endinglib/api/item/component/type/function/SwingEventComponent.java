package com.mega.endinglib.api.item.component.type.function;

import com.mega.endinglib.mixin.accessor.AccessorCommandSourceStack;
import com.mega.endinglib.util.mc.codec.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import java.util.Optional;

public record SwingEventComponent(String command, float attackCooldownRequirement, Optional<ResourceLocation> function, int minimumPermission, boolean cancelFurtherProcessing, boolean silent) implements FunctionComponent {
    public static Codec<SwingEventComponent> CODEC = RecordCodecBuilder.create(
            com -> com.group(
                    Codec.STRING.optionalFieldOf("command", "").forGetter(SwingEventComponent::command),
                    Codecs.O2ONE_FLOAT.optionalFieldOf("attack_cooldown_requirement", 0F).forGetter(SwingEventComponent::attackCooldownRequirement),
                    ResourceLocation.CODEC.optionalFieldOf("function").forGetter(SwingEventComponent::function),
                    Codecs.NON_NEGATIVE_INT.optionalFieldOf("min_permission", 2).forGetter(SwingEventComponent::minimumPermission),
                    Codec.BOOL.optionalFieldOf("cancel_further_processing", false).forGetter(SwingEventComponent::cancelFurtherProcessing),
                    Codec.BOOL.optionalFieldOf("silent", true).forGetter(SwingEventComponent::silent)
            ).apply(com, SwingEventComponent::new)
    );
    private boolean canUse(Player player) {
        return player.getAttackStrengthScale(0.5F) >= attackCooldownRequirement;
    }
    public boolean apply(ServerLevel serverLevel, LivingEntity livingEntity) {
        if (!(livingEntity instanceof ServerPlayer player) || this.canUse(player)) {
            if (!command.isEmpty()) {
                CommandSourceStack sourceStack = livingEntity.createCommandSourceStack().withMaximumPermission(minimumPermission);
                ((AccessorCommandSourceStack) sourceStack).setSilent(silent);
                serverLevel.getServer().getCommands().performPrefixedCommand(sourceStack, this.command);
            }
            function.ifPresent(location -> this.apply(livingEntity, location, minimumPermission));
            return cancelFurtherProcessing;
        }
        return false;
    }
}
