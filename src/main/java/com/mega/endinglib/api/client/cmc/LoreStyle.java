package com.mega.endinglib.api.client.cmc;

import com.mega.endinglib.api.client.text.TextColorUtils;
import com.mega.endinglib.util.java.ExeCallable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public enum LoreStyle {
    NONE(mutableComponent -> mutableComponent),
    CENTER_TOOLTIP(mutableComponent -> mutableComponent.withStyle(TextColorUtils.MIDDLE)),
    ATTRIBUTE_PREFIX(mutableComponent -> Component.literal("§5- ").append(mutableComponent)),
    INDENTATION(mutableComponent -> Component.literal("  ").append(mutableComponent)),
    INDENTATION2(mutableComponent -> Component.literal("    ").append(mutableComponent)),

    INDENTATION_ATTRIBUTE_PREFIX(mutableComponent -> Component.literal("  §5- ").append(mutableComponent)),
    INDENTATION2_ATTRIBUTE_PREFIX(mutableComponent -> Component.literal("    §5- ").append(mutableComponent));
    private final ExeCallable<MutableComponent> delegate;

    LoreStyle(ExeCallable<MutableComponent> delegate) {
        this.delegate = delegate;
    }

    public ExeCallable<MutableComponent> getDelegate() {
        return this.delegate;
    }
}
