package com.mega.endinglib.mixin.compat.ibeeditor;

import com.github.franckyi.ibeeditor.client.util.texteditor.StyleFormatting;
import com.github.franckyi.ibeeditor.client.util.texteditor.TextEditorInputParser;
import com.mega.endinglib.api.client.text.StyleItf;
import com.mega.endinglib.common.compat.ibeeditor.IBESafeClass;
import com.mega.endinglib.util.annotation.ModDependsMixin;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.LiteralContents;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TextEditorInputParser.class, remap = false)
@ModDependsMixin("ibeeditor")
public abstract class TextEditorInputParserMixin {
    @Shadow protected abstract void addStyleFormatting(StyleFormatting formatting);

    @Shadow private int index;

    @Inject(method = "parse", at = @At(value = "JUMP", opcode = Opcodes.IFEQ, shift = At.Shift.BEFORE, ordinal = 2))
    private void parseIfCentered(MutableComponent text, CallbackInfo ci) {
        if (text.getContents() instanceof LiteralContents lc) {
            int length = lc.text().length();
            if (((StyleItf) text.getStyle()).endingLibrary$isCentered())
                addStyleFormatting(new StyleFormatting(this.index, this.index + length, IBESafeClass.CENTERED));
        }
    }
}
