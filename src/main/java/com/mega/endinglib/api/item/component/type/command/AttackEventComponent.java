package com.mega.endinglib.api.item.component.type.command;

import com.mega.endinglib.util.mc.codec.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

public record AttackEventComponent(String command, float attackCooldownRequirement) {
    public static Codec<AttackEventComponent> CODEC = RecordCodecBuilder.create(
            com -> com.group(
                    Codec.STRING.fieldOf("command").forGetter(AttackEventComponent::command),
                    Codecs.O2ONE_FLOAT.optionalFieldOf("attack_cooldown_requirement", 0F).forGetter(AttackEventComponent::attackCooldownRequirement)
            ).apply(com, AttackEventComponent::new)
    );
    private boolean canUse(Player player) {
        return player.getAttackStrengthScale(0.5F) >= attackCooldownRequirement;
    }
    public void apply(ServerLevel serverLevel, Player player) {
        if (this.canUse(player)) {
            serverLevel.getServer().getCommands().performPrefixedCommand(player.createCommandSourceStack(), this.command);
        }
    }
}
