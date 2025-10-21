package com.mega.endinglib.util.asm;

import com.mega.endinglib.client.ClientWrapped;
import com.mega.endinglib.proxy.CommonProxy;
import com.mega.endinglib.util.java.Args;
import com.mega.endinglib.util.mixin.data_expand.ExtraEntity;
import com.mega.endinglib.util.mixin.data_expand.ExtraEntityData;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class ClientEventUtil {
    public static boolean hotbarKeyConsumeClickAndCanUse(boolean origin, int index) {
        Player player = ClientWrapped.clientPlayer();
        if (origin) {
            if (player != null) {
                boolean[] args = new boolean[] {true};
                CommonProxy.getCameraCapOptional(player).ifPresent(capability -> {
                    int i = capability.getLockedHotbar();
                    if (i > 0 && (i - 1) != index)
                        args[0] = false;
                });
                if (!args[0])
                    return false;
            }
        }
        return origin;
    }
    public static <T extends Entity> ResourceLocation wrapGetTextureLocations(Object owner, T entity, ResourceLocation origin) {
        ResourceLocation custom = ((ExtraEntity) entity).endinglib$getExtraEntityData().customModelTexture;
        return custom == null ? origin : custom;
    }
}
