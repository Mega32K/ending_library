package com.mega.endinglib.common.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TextColor;
import org.jetbrains.annotations.Nullable;

public class TextColorArgument implements ArgumentType<TextColor> {
    public static final TextColor NULL_COLOR = TextColor.parseColor("red");
    public static TextColorArgument color() {
        return new TextColorArgument();
    }
    public static TextColor getColor(CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, TextColor.class);
    }
    @Override
    public TextColor parse(StringReader reader) throws CommandSyntaxException {
        String s = reader.readQuotedString();
        if (s.startsWith("#")) {
            return TextColor.parseColor(s);
        } else {
            try {
                return TextColor.fromRgb(Integer.parseInt(s));
            } catch (NumberFormatException exception) {
                TextColor r = TextColor.parseColor(s);
                if (r == null) r = NULL_COLOR;
                return r;
            }
        }
    }
}
