package com.mega.endinglib.common.data;

import com.mega.endinglib.api.client.shader.post.DynamicScreenEffect;
import net.minecraft.resources.ResourceLocation;

public record DynamicEffectData(String name, ResourceLocation location, boolean canUse) {
    public DynamicScreenEffect asEffect() {
        return new DynamicScreenEffect(this.name, this.location, this.canUse);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj instanceof DynamicEffectData data) {
            return data.name.equals(this.name);
        }
        return false;
    }
}
