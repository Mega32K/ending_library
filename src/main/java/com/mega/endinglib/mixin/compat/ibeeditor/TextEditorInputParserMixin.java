package com.mega.endinglib.mixin.compat.ibeeditor;

import com.github.franckyi.ibeeditor.client.util.texteditor.ColorFormatting;
import com.github.franckyi.ibeeditor.client.util.texteditor.StyleFormatting;
import com.github.franckyi.ibeeditor.client.util.texteditor.StyleType;
import com.github.franckyi.ibeeditor.client.util.texteditor.TextEditorInputParser;
import com.mega.endinglib.api.client.text.StyleItf;
import com.mega.endinglib.common.compat.ibeeditor.IBESafeClass;
import com.mega.endinglib.util.annotation.ModDependsMixin;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.LiteralContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = TextEditorInputParser.class, remap = false)
@ModDependsMixin("ibeeditor")
public abstract class TextEditorInputParserMixin {
    @Shadow protected abstract void addStyleFormatting(StyleFormatting formatting);

    @Shadow private int index;

    /*
    @Inject(method = "parse", at = @At(value = "JUMP", opcode = Opcodes.IFEQ, shift = At.Shift.BEFORE, ordinal = 2))
    private void parseIfCentered(MutableComponent text, CallbackInfo ci) {
        if (text.getContents() instanceof LiteralContents lc) {
            int length = lc.text().length();
            if (((StyleItf) text.getStyle()).endingLibrary$isCentered())
                addStyleFormatting(new StyleFormatting(this.index, this.index + length, IBESafeClass.CENTERED));
        }
    }
     */

    @Shadow protected abstract void addColorFormatting(ColorFormatting formatting);

    /**
     * @author MegaDarkness
     * @reason 尝试添加新style formatting
     */
    @Overwrite
    public void parse(MutableComponent text) {
        if (text.getContents() instanceof LiteralContents lc) {
            int length = lc.text().length();
            if (StyleItf.of(text.getStyle()).endingLibrary$isCentered()) {
                addStyleFormatting(new StyleFormatting(index, index + length, IBESafeClass.CENTERED));
            }

            if (text.getStyle().isBold()) {
                addStyleFormatting(new StyleFormatting(index, index + length, StyleType.BOLD));
            }
            if (text.getStyle().isItalic()) {
                addStyleFormatting(new StyleFormatting(index, index + length, StyleType.ITALIC));
            }
            if (text.getStyle().isUnderlined()) {
                addStyleFormatting(new StyleFormatting(index, index + length, StyleType.UNDERLINED));
            }
            if (text.getStyle().isStrikethrough()) {
                addStyleFormatting(new StyleFormatting(index, index + length, StyleType.STRIKETHROUGH));
            }
            if (text.getStyle().isObfuscated()) {
                addStyleFormatting(new StyleFormatting(index, index + length, StyleType.OBFUSCATED));
            }
            if (text.getStyle().getColor() != null) {
                addColorFormatting(new ColorFormatting(index, index + length, text.getStyle().getColor().toString()));
            }
            index += lc.text().length();
        }
        text.getSiblings().stream()
                .filter(MutableComponent.class::isInstance)
                .map(MutableComponent.class::cast)
                .forEach(this::parse);
    }
}
