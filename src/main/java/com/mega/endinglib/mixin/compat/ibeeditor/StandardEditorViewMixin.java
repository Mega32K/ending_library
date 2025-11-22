package com.mega.endinglib.mixin.compat.ibeeditor;

import com.github.franckyi.guapi.api.node.builder.HBoxBuilder;
import com.github.franckyi.guapi.api.node.builder.TexturedButtonBuilder;
import com.github.franckyi.ibeeditor.client.ModTextures;
import com.github.franckyi.ibeeditor.client.screen.view.StandardEditorView;
import com.github.franckyi.ibeeditor.client.util.texteditor.StyleFormatting;
import com.github.franckyi.ibeeditor.client.util.texteditor.StyleType;
import com.github.franckyi.ibeeditor.common.ModTexts;
import com.mega.endinglib.EndingLibrary;
import com.mega.endinglib.common.compat.ibeeditor.IBESafeClass;
import com.mega.endinglib.util.SafeClass;
import com.mega.endinglib.util.annotation.ModDependsMixin;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.data.DataCommands;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StandardEditorView.class)
@ModDependsMixin("ibeeditor")
public abstract class StandardEditorViewMixin {
    @Shadow(remap = false) protected abstract TexturedButtonBuilder createTextButton(StyleType target, ResourceLocation id, MutableComponent tooltipText);

    @Inject(method = "lambda$build$0(Lcom/github/franckyi/guapi/api/node/builder/HBoxBuilder;)V", remap = false, at = @At(value = "INVOKE", remap = false, target = "Lcom/github/franckyi/guapi/api/node/builder/HBoxBuilder;add(Lcom/github/franckyi/guapi/api/node/Node;)Lcom/github/franckyi/guapi/api/node/Group;", shift = At.Shift.AFTER, ordinal = 0))
    private void addStyleFormattingMiddle(HBoxBuilder middle, CallbackInfo ci) {
        middle.add(this.createTextButton(IBESafeClass.CENTERED, SafeClass.loc("textures/ui/text_centered.png"), Component.translatable("tooltip.endinglib.text.centered")));
    }

}
