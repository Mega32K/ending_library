package com.mega.endinglib.common.command.test;

import com.mega.endinglib.util.mc.forge.ClassBytecodesGetter;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class DumpCommand {
    private static final AtomicBoolean DUMP_IN_PROGRESS = new AtomicBoolean();

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return LiteralArgumentBuilder.<CommandSourceStack>literal("dump")
                .requires(stack -> stack.hasPermission(4))
                .then(Commands.argument("class", StringArgumentType.string())
                        .then(Commands.argument("reason", StringArgumentType.word())
                                .suggests((context, builder) -> builder.suggest(ClassBytecodesGetter.CLASSLOADING_REASON).suggest(ClassBytecodesGetter.COMPUTING_FRAMES_REASON).buildFuture())
                                .executes(context -> file(context.getSource(), StringArgumentType.getString(context, "class"), StringArgumentType.getString(context, "reason")))
                        )
                );
    }
    private static int file(CommandSourceStack stack, String classname , String reason) {
        if (!DUMP_IN_PROGRESS.compareAndSet(false, true)) return 0;
        CompletableFuture.runAsync(() -> {
            try {
                Class<?> c = Class.forName(classname, false, ClassBytecodesGetter.transformLoader());
                byte[] bytes = ClassBytecodesGetter.copyBytecodesFromClass(c, reason);
                try {
                    File file1 = new File( classname + ".class");
                    FileOutputStream stream = new FileOutputStream(file1);
                    stream.write(bytes);
                    stream.close();
                    stack.sendSuccess(()-> Component.literal("Class %s dump successful".formatted(classname)).withStyle(ChatFormatting.GREEN).withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file1.getAbsolutePath()))), false);

                } catch (IOException e) {
                    stack.sendFailure(Component.literal("Dumped Class failed:+"+e.getLocalizedMessage()));
                }
            } catch (ClassNotFoundException exception) {
                stack.sendFailure(Component.literal("No class def found:"+exception.getLocalizedMessage()));
            } finally {
                DUMP_IN_PROGRESS.set(false);
            }
        });
        return 0;
    }
}
