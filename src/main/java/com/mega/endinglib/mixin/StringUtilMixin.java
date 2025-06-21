package com.mega.endinglib.mixin;

import com.mega.endinglib.config.CommonConfig;
import net.minecraft.util.StringUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(StringUtil.class)
public abstract class StringUtilMixin {
    @Shadow
    public static String truncateStringIfNecessary(String p_144999_, int p_145000_, boolean p_145001_) {
        return null;
    }

    /**
     * @author Mega
     * @reason Nothing
     */
    @Overwrite
    public static String trimChatMessage(String p_216470_) {
        return truncateStringIfNecessary(p_216470_, CommonConfig.max_edit_length, false);
    }
}
