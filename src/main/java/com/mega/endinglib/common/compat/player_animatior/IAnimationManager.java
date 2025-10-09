package com.mega.endinglib.common.compat.player_animatior;

import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

public interface IAnimationManager {
    void updateAnimations(Map<ResourceLocation, List<byte[]>> data);
}
