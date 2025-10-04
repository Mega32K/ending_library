package com.mega.endinglib.api.item.component.type.command;

import com.mega.endinglib.util.mc.codec.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

public record ReleaseUsingComponent(String command, boolean isFinishedUsing, int timeLeft) {
    public static Codec<ReleaseUsingComponent> CODEC = RecordCodecBuilder.create(
            com -> com.group(
                    Codec.STRING.fieldOf("command").forGetter(ReleaseUsingComponent::command),
                    Codec.BOOL.optionalFieldOf("finished", false).forGetter(ReleaseUsingComponent::isFinishedUsing),
                    Codecs.NON_NEGATIVE_INT.optionalFieldOf("time_left", 0).forGetter(ReleaseUsingComponent::timeLeft)
            ).apply(com, ReleaseUsingComponent::new)
    );
    public void apply(ServerLevel serverLevel, LivingEntity livingEntity, boolean finished) {
        if (this.isFinishedUsing) {
            if (!finished)
                return;
        }
        serverLevel.getServer().getCommands().performPrefixedCommand(livingEntity.createCommandSourceStack(), command);
    }
}
