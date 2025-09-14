package com.mega.endinglib.mixin.accessor;

import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(ModelPart.class)
public interface AccessorModelPart {
    @Accessor
    List<ModelPart.Cube> getCubes();

    @Accessor
    Map<String, ModelPart> getChildren();
}
