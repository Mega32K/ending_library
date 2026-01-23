package com.mega.endinglib.api.item.component.type.function;

import com.mega.endinglib.mixin.accessor.AccessorCommandSourceStack;
import com.mega.endinglib.util.mc.codec.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public record AttackEventComponent(String command, float attackCooldownRequirement, Optional<ResourceLocation> function, int contactCooldownTicks, int minimumPermission) implements FunctionComponent {
    public static Codec<AttackEventComponent> CODEC = RecordCodecBuilder.create(
            com -> com.group(
                    Codec.STRING.optionalFieldOf("command", "").forGetter(AttackEventComponent::command),
                    Codecs.O2ONE_FLOAT.optionalFieldOf("attack_cooldown_requirement", 0F).forGetter(AttackEventComponent::attackCooldownRequirement),
                    ResourceLocation.CODEC.optionalFieldOf("function").forGetter(AttackEventComponent::function),
                    Codecs.NON_NEGATIVE_INT.optionalFieldOf("contact_cooldown_ticks", 0).forGetter(AttackEventComponent::contactCooldownTicks),
                    Codec.INT.optionalFieldOf("min_permission", 2).forGetter(AttackEventComponent::minimumPermission)
            ).apply(com, AttackEventComponent::new)
    );
    private boolean canUse(Player player) {
        return player.getAttackStrengthScale(0.5F) >= attackCooldownRequirement;
    }
    public void apply(ServerLevel serverLevel, Player player) {
        if (this.canUse(player)) {
            if (!command.isEmpty()) {
                CommandSourceStack sourceStack = player.createCommandSourceStack().withMaximumPermission(minimumPermission);
                ((AccessorCommandSourceStack) sourceStack).setSilent(true);
                serverLevel.getServer().getCommands().performPrefixedCommand(sourceStack, this.command);
            }
            function.ifPresent(location -> this.apply(player, location, minimumPermission));
            if (this.contactCooldownTicks != 0)
                player.getCooldowns().addCooldown(player.getMainHandItem().getItem(), contactCooldownTicks);
        }
    }
}
