package com.mega.endinglib.mixin.dev;

import com.mega.endinglib.api.entity.TimeStopEntity;
import com.mega.endinglib.util.annotation.DeprecatedMixin;
import com.mega.endinglib.util.annotation.DevEnvMixin;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Sheep.class)
@DevEnvMixin
@DeprecatedMixin
public abstract class SheepMixin implements TimeStopEntity {
    @Override
    public void updateSkill(LivingEntity living, Level level) {
        if (level instanceof ServerLevel serverLevel)
            serverLevel.tickNonPassenger(living);
        else if (level instanceof ClientLevel clientLevel)
            clientLevel.tickNonPassenger(living);
    }

    @Override
    public boolean smoothPartialTicks() {
        return true;
    }
}
