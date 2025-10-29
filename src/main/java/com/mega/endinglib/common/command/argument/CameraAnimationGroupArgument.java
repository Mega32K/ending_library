package com.mega.endinglib.common.command.argument;

import com.mega.endinglib.api.client.camera.*;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.commands.CommandSourceStack;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class CameraAnimationGroupArgument implements ArgumentType<String> {

    public static String getGroup(CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, String.class);
    }

    public static CameraAnimationGroupArgument group() {
        return new CameraAnimationGroupArgument();
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
                String animationTarget = context.getArgument("animationTarget", String.class);
                String remaining = builder.getRemaining().toLowerCase(Locale.ROOT);

                for (CameraKeyframeAnimation animation : cvi.getKeyframeAnimations()) {
                    String animName = animation.getName();
                    if (animName.equals(animationTarget)) {
                        for (String keyGroup : animation.getKeyframes().keySet()) {
                            if (remaining.isEmpty() || keyGroup.toLowerCase(Locale.ROOT).startsWith(remaining))
                                builder.suggest(keyGroup);
                        }
                        break;
                    }
                }

                return builder.buildFuture();
            } catch (Throwable throwable) {
            }
        }
        return Suggestions.empty();
    }
}
