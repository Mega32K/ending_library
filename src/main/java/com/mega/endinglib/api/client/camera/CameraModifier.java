package com.mega.endinglib.api.client.camera;

import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

public class CameraModifier {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final double amount;
    private final CameraModifier.Operation operation;
    private final Supplier<String> nameGetter;
    private final UUID id;

    public CameraModifier(String p_22196_, double p_22197_, CameraModifier.Operation p_22198_) {
        this(Mth.createInsecureUUID(RandomSource.createNewThreadLocalInstance()), () -> p_22196_, p_22197_, p_22198_);
    }

    public CameraModifier(UUID p_22200_, String p_22201_, double p_22202_, CameraModifier.Operation p_22203_) {
        this(p_22200_, () -> p_22201_, p_22202_, p_22203_);
    }

    public CameraModifier(UUID p_22205_, Supplier<String> p_22206_, double p_22207_, CameraModifier.Operation p_22208_) {
        this.id = p_22205_;
        this.nameGetter = p_22206_;
        this.amount = p_22207_;
        this.operation = p_22208_;
    }

    @Nullable
    public static CameraModifier load(CompoundTag p_22213_) {
        try {
            UUID uuid = p_22213_.getUUID("UUID");
            CameraModifier.Operation CameraModifier$operation = CameraModifier.Operation.fromValue(p_22213_.getInt("Operation"));
            return new CameraModifier(uuid, p_22213_.getString("Name"), p_22213_.getDouble("Amount"), CameraModifier$operation);
        } catch (Exception exception) {
            LOGGER.warn("Unable to create modifier: {}", (Object) exception.getMessage());
            return null;
        }
    }

    public UUID getId() {
        return this.id;
    }

    public String getName() {
        return this.nameGetter.get();
    }

    public CameraModifier.Operation getOperation() {
        return this.operation;
    }

    public double getAmount() {
        return this.amount;
    }

    public boolean equals(Object p_22221_) {
        if (this == p_22221_) {
            return true;
        } else if (p_22221_ != null && this.getClass() == p_22221_.getClass()) {
            CameraModifier CameraModifier = (CameraModifier) p_22221_;
            return Objects.equals(this.id, CameraModifier.id);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.id.hashCode();
    }

    public String toString() {
        return "CameraModifier{amount=" + this.amount + ", operation=" + this.operation + ", name='" + this.nameGetter.get() + "', id=" + this.id + "}";
    }

    public CompoundTag save() {
        CompoundTag compoundtag = new CompoundTag();
        compoundtag.putString("Name", this.getName());
        compoundtag.putDouble("Amount", this.amount);
        compoundtag.putInt("Operation", this.operation.toValue());
        compoundtag.putUUID("UUID", this.id);
        return compoundtag;
    }

    public MutableComponent toComponent() {
        return Component.literal("  {").withStyle(ChatFormatting.GREEN)
                .append(
                        Component.literal("\"Name\"").withStyle(ChatFormatting.AQUA)
                                .append(Component.literal(":"))
                                .append(Component.literal("\"%s\"".formatted(this.getName())).withStyle(ChatFormatting.GREEN))
                                .append(Component.literal(", "))
                ).append(
                        Component.literal("\"UUID\"").withStyle(ChatFormatting.AQUA)
                                .append(Component.literal(":"))
                                .append(Component.literal("\"%s\"".formatted(this.getId())).withStyle(ChatFormatting.GREEN).withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, this.getId().toString())).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click")))))
                                .append(Component.literal(", "))
                ).append(
                        Component.literal("\"Amount\"").withStyle(ChatFormatting.GOLD)
                                .append(Component.literal(":"))
                                .append(Component.literal("\"%s\"".formatted(this.getAmount())).withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.valueOf(this.getAmount()))).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click")))))
                                .append(Component.literal(", "))
                ).append(
                        Component.literal("\"Operation\"").withStyle(ChatFormatting.YELLOW)
                                .append(Component.literal(":"))
                                .append(Component.literal("\"%s\"".formatted(this.getOperation())).withStyle(ChatFormatting.GOLD))

                ).append(Component.literal("}").withStyle(ChatFormatting.GREEN));
    }

    public enum Operation {
        ADDITION(0),
        MULTIPLY_BASE(1),
        MULTIPLY_TOTAL(2);

        private static final CameraModifier.Operation[] OPERATIONS = new CameraModifier.Operation[]{ADDITION, MULTIPLY_BASE, MULTIPLY_TOTAL};
        private final int value;

        Operation(int p_22234_) {
            this.value = p_22234_;
        }

        public static CameraModifier.Operation fromValue(int p_22237_) {
            if (p_22237_ >= 0 && p_22237_ < OPERATIONS.length) {
                return OPERATIONS[p_22237_];
            } else {
                throw new IllegalArgumentException("No operation with value " + p_22237_);
            }
        }

        public int toValue() {
            return this.value;
        }
    }
}
