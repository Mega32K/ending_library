package com.mega.endinglib.api.item.component.type.command;

import com.mega.endinglib.util.mc.codec.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public record AttackEventComponent(String command, float attackCooldownRequirement, Optional<ResourceLocation> function, int minimumPermission) implements FunctionComponent {
    public static Codec<AttackEventComponent> CODEC = RecordCodecBuilder.create(
            com -> com.group(
                    Codec.STRING.optionalFieldOf("command", "").forGetter(AttackEventComponent::command),
                    Codecs.O2ONE_FLOAT.optionalFieldOf("attack_cooldown_requirement", 0F).forGetter(AttackEventComponent::attackCooldownRequirement),
                    ResourceLocation.CODEC.optionalFieldOf("function").forGetter(AttackEventComponent::function),
                    Codecs.NON_NEGATIVE_INT.optionalFieldOf("min_permission", 2).forGetter(AttackEventComponent::minimumPermission)
            ).apply(com, AttackEventComponent::new)
    );
    private boolean canUse(Player player) {
        return player.getAttackStrengthScale(0.5F) >= attackCooldownRequirement;
    }
    public void apply(ServerLevel serverLevel, Player player) {
        if (this.canUse(player)) {
            if (!command.isEmpty())
                serverLevel.getServer().getCommands().performPrefixedCommand(player.createCommandSourceStack(), this.command);
            function.ifPresent(location -> this.apply(player, location, minimumPermission));
        }
    }
}
