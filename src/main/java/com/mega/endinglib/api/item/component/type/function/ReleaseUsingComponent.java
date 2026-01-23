package com.mega.endinglib.api.item.component.type.function;

import com.mega.endinglib.mixin.accessor.AccessorCommandSourceStack;
import com.mega.endinglib.util.mc.codec.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.ExecuteCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public record ReleaseUsingComponent(String command, boolean isFinishedUsing, int timeLeft, Optional<ResourceLocation> function, int cooldownTicks, int minimumPermission) implements FunctionComponent {
    public static Codec<ReleaseUsingComponent> CODEC = RecordCodecBuilder.create(
            com -> com.group(
                    Codec.STRING.optionalFieldOf("command", "").forGetter(ReleaseUsingComponent::command),
                    Codec.BOOL.optionalFieldOf("finished", false).forGetter(ReleaseUsingComponent::isFinishedUsing),
                    Codecs.NON_NEGATIVE_INT.optionalFieldOf("time_left", 0).forGetter(ReleaseUsingComponent::timeLeft),
                    ResourceLocation.CODEC.optionalFieldOf("function").forGetter(ReleaseUsingComponent::function),
                    Codec.INT.optionalFieldOf("cooldown_ticks", 0).forGetter(ReleaseUsingComponent::cooldownTicks),
                    Codecs.NON_NEGATIVE_INT.optionalFieldOf("min_permission", 2).forGetter(ReleaseUsingComponent::minimumPermission)
            ).apply(com, ReleaseUsingComponent::new)
    );
    public void apply(ServerLevel serverLevel, LivingEntity livingEntity, boolean finished, ItemStack itemStack) {
        if (this.isFinishedUsing) {
            if (!finished)
                return;
        }if (!command.isEmpty()) {
            CommandSourceStack sourceStack = livingEntity.createCommandSourceStack().withMaximumPermission(minimumPermission);
            ((AccessorCommandSourceStack) sourceStack).setSilent(true);
            serverLevel.getServer().getCommands().performPrefixedCommand(sourceStack, this.command);
        }
        function.ifPresent(location -> this.apply(livingEntity, location, minimumPermission));
        if (livingEntity instanceof Player player) {
            if (cooldownTicks != 0)
                player.getCooldowns().addCooldown(itemStack.getItem(), cooldownTicks);
        }
    }
}
