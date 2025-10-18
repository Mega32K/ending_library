package com.mega.endinglib.mixin.advanced.client.custom_style;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mega.endinglib.api.client.text.StyleItf;
import net.minecraft.network.chat.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.lang.reflect.Type;

@Mixin(Style.Serializer.class)
public abstract class StyleSerializerMixin {
    @Shadow
    @Nullable
    private static Boolean getOptionalFlag(JsonObject p_131206_, String p_131207_) {
        throw new AssertionError("");
    }

    @Inject(method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/network/chat/Style;", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    private void modifyStyle(JsonElement par1, Type par2, JsonDeserializationContext par3, CallbackInfoReturnable<Style> cir) {
        JsonObject jsonobject = par1.getAsJsonObject();
        Boolean b = getOptionalFlag(jsonobject, "centered");
        if (b != null) {
            Style style = cir.getReturnValue();
            ((StyleItf) style).endingLibrary$withCentered(b);
            cir.setReturnValue(style);
        }
    }

    @Inject(method = "serialize(Lnet/minecraft/network/chat/Style;Ljava/lang/reflect/Type;Lcom/google/gson/JsonSerializationContext;)Lcom/google/gson/JsonElement;", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    private void modifyStyle(Style p_131209_, Type p_131210_, JsonSerializationContext p_131211_, CallbackInfoReturnable<JsonObject> cir) {
        Boolean b = ((StyleItf) p_131209_).endingLibrary$isCentered();
        if (b) {
            JsonObject jsonObject = cir.getReturnValue();
            jsonObject.addProperty("centered", b);
            cir.setReturnValue(jsonObject);
        }
    }
}
