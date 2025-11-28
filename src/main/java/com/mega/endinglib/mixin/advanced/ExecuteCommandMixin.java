package com.mega.endinglib.mixin.advanced;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.nbt.*;
import net.minecraft.server.commands.ExecuteCommand;
import net.minecraft.server.commands.data.DataAccessor;
import net.minecraft.server.commands.data.DataCommands;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.IntFunction;

@Mixin(ExecuteCommand.class)
public abstract class ExecuteCommandMixin {
    @Shadow
    private static CommandSourceStack storeData(CommandSourceStack p_137118_, DataAccessor p_137119_, NbtPathArgument.NbtPath p_137120_, IntFunction<Tag> p_137121_, boolean p_137122_) {
        throw new AssertionError();
    }

    @Inject(method = "wrapStores", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/commands/data/DataCommands$DataProvider;wrap(Lcom/mojang/brigadier/builder/ArgumentBuilder;Ljava/util/function/Function;)Lcom/mojang/brigadier/builder/ArgumentBuilder;", shift = At.Shift.BEFORE))
    private static void extraStoreType(LiteralCommandNode<CommandSourceStack> p_137094_, LiteralArgumentBuilder<CommandSourceStack> p_137095_, boolean p_137096_, CallbackInfoReturnable<ArgumentBuilder<CommandSourceStack, ?>> cir, @Local DataCommands.DataProvider dataProvider) {
        dataProvider.wrap(p_137095_, (p_137101_) -> p_137101_.then(Commands.argument("path", NbtPathArgument.nbtPath())
                .then(Commands.literal("string")
                        .then(Commands.argument("scale", DoubleArgumentType.doubleArg())
                                .then(Commands.argument("format", StringArgumentType.string())
                                        .redirect(p_137094_, (p_180156_) -> storeData(p_180156_.getSource(), dataProvider.access(p_180156_), NbtPathArgument.getPath(p_180156_, "path"), (p_180165_) -> StringTag.valueOf(StringArgumentType.getString(p_180156_, "format").formatted(((int) ((double) p_180165_ * DoubleArgumentType.getDouble(p_180156_, "scale"))))), p_137096_))
                                )
                        )
                )
        ));
    }
}
