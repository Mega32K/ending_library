package com.mega.endinglib.api.item.component.type.function;

import com.mega.endinglib.util.mc.codec.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.ExecuteCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;

public record ReleaseUsingComponent(String command, boolean isFinishedUsing, int timeLeft, Optional<ResourceLocation> function, int minimumPermission) implements FunctionComponent {
    public static Codec<ReleaseUsingComponent> CODEC = RecordCodecBuilder.create(
            com -> com.group(
                    Codec.STRING.optionalFieldOf("command", "").forGetter(ReleaseUsingComponent::command),
                    Codec.BOOL.optionalFieldOf("finished", false).forGetter(ReleaseUsingComponent::isFinishedUsing),
                    Codecs.NON_NEGATIVE_INT.optionalFieldOf("time_left", 0).forGetter(ReleaseUsingComponent::timeLeft),
                    ResourceLocation.CODEC.optionalFieldOf("function").forGetter(ReleaseUsingComponent::function),
                    Codecs.NON_NEGATIVE_INT.optionalFieldOf("min_permission", 2).forGetter(ReleaseUsingComponent::minimumPermission)
            ).apply(com, ReleaseUsingComponent::new)
    );
    public void apply(ServerLevel serverLevel, LivingEntity livingEntity, boolean finished) {
        if (this.isFinishedUsing) {
            if (!finished)
                return;
        }
        if (!command.isEmpty())
            serverLevel.getServer().getCommands().performPrefixedCommand(livingEntity.createCommandSourceStack(), command);
        function.ifPresent(location -> this.apply(livingEntity, location, minimumPermission));
    }
}
