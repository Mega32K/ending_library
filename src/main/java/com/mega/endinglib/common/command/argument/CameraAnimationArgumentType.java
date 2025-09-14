package com.mega.endinglib.common.command.argument;

import com.mega.endinglib.api.client.camera.*;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public class CameraAnimationArgumentType implements ArgumentType<String> {
    public static final SimpleCommandExceptionType ERROR_INVALID_UUID = new SimpleCommandExceptionType(Component.translatable("argument.uuid.invalid"));
    private static final Collection<String> EXAMPLES = List.of("dd12be42-52a9-4a91-a8a1-11c01849e498");
    private static final Pattern ALLOWED_CHARACTERS = Pattern.compile("^([-A-Fa-f0-9]+)");

    public static String getName(CommandContext<CommandSourceStack> p_113854_, String p_113855_) {
        return p_113854_.getArgument(p_113855_, String.class);
    }

    public static CameraAnimationArgumentType name() {
        return new CameraAnimationArgumentType();
    }

    public String parse(StringReader p_113852_) throws CommandSyntaxException {
        return p_113852_.readUnquotedString();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if (context.getSource() instanceof ClientSuggestionProvider) {
            try {
                ICameraManager manager = CameraUtils.getInstance();
                ModifierType modifierType = ModifierType.valueOf(context.getArgument("modifierType", String.class));
                CameraValueInstance cvi = modifierType.getFieldGetter().apply(manager);

                for (CameraKeyframeAnimation animation : cvi.getKeyframeAnimations()) {
                    builder.suggest(animation.getName(), animation.toComponent());
                }

                return builder.buildFuture();
            } catch (Throwable throwable) {
            }
        }
        return ArgumentType.super.listSuggestions(context, builder);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
