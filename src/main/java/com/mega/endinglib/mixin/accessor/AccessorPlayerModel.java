package com.mega.endinglib.mixin.accessor;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerModel.class)
public interface AccessorPlayerModel {
    @Accessor
    ModelPart getCloak();
    @Accessor
    ModelPart getEar();
}
