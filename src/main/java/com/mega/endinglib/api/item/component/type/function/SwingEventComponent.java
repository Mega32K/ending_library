package com.mega.endinglib.api.item.component.type.function;

import com.mega.endinglib.util.mc.codec.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import java.util.Optional;

public record SwingEventComponent(String command, float attackCooldownRequirement, Optional<ResourceLocation> function, int minimumPermission, boolean cancelFurtherProcessing) implements FunctionComponent {
    public static Codec<SwingEventComponent> CODEC = RecordCodecBuilder.create(
            com -> com.group(
                    Codec.STRING.optionalFieldOf("command", "").forGetter(SwingEventComponent::command),
                    Codecs.O2ONE_FLOAT.optionalFieldOf("attack_cooldown_requirement", 0F).forGetter(SwingEventComponent::attackCooldownRequirement),
                    ResourceLocation.CODEC.optionalFieldOf("function").forGetter(SwingEventComponent::function),
                    Codecs.NON_NEGATIVE_INT.optionalFieldOf("min_permission", 2).forGetter(SwingEventComponent::minimumPermission),
                    Codec.BOOL.optionalFieldOf("cancel_further_processing", false).forGetter(SwingEventComponent::cancelFurtherProcessing)
            ).apply(com, SwingEventComponent::new)
    );
    private boolean canUse(Player player) {
        return player.getAttackStrengthScale(0.5F) >= attackCooldownRequirement;
    }
    public boolean apply(ServerLevel serverLevel, LivingEntity livingEntity) {
        if (!(livingEntity instanceof Player player) || this.canUse(player)) {
            if (!command.isEmpty())
                serverLevel.getServer().getCommands().performPrefixedCommand(livingEntity.createCommandSourceStack(), this.command);
            function.ifPresent(location -> this.apply(livingEntity, location, minimumPermission));
            return cancelFurtherProcessing;
        }
        return false;
    }
}
