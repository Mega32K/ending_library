package com.mega.endinglib.common.command.argument;

import com.mega.endinglib.api.client.camera.CameraModifier;
import com.mega.endinglib.api.client.camera.CameraUtils;
import com.mega.endinglib.api.client.camera.ICameraManager;
import com.mega.endinglib.api.client.camera.ModifierType;
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

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CameraModifierUUIDArgument implements ArgumentType<UUID> {
    public static final SimpleCommandExceptionType ERROR_INVALID_UUID = new SimpleCommandExceptionType(Component.translatable("argument.uuid.invalid"));
    private static final Collection<String> EXAMPLES = Arrays.asList("dd12be42-52a9-4a91-a8a1-11c01849e498");
    private static final Pattern ALLOWED_CHARACTERS = Pattern.compile("^([-A-Fa-f0-9]+)");

    public static UUID getUuid(CommandContext<CommandSourceStack> p_113854_, String p_113855_) {
        return p_113854_.getArgument(p_113855_, UUID.class);
    }

    public static CameraModifierUUIDArgument uuid() {
        return new CameraModifierUUIDArgument();
    }

    public UUID parse(StringReader p_113852_) throws CommandSyntaxException {
        String s = p_113852_.getRemaining();
        Matcher matcher = ALLOWED_CHARACTERS.matcher(s);
        if (matcher.find()) {
            String s1 = matcher.group(1);

            try {
                UUID uuid = UUID.fromString(s1);
                p_113852_.setCursor(p_113852_.getCursor() + s1.length());
                return uuid;
            } catch (IllegalArgumentException illegalargumentexception) {
            }
        }

        throw ERROR_INVALID_UUID.create();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if (context.getSource() instanceof ClientSuggestionProvider) {
            try {
                ICameraManager manager = CameraUtils.getInstance();
                ModifierType modifierType = ModifierType.valueOf(context.getArgument("modifierType", String.class));
                String remaining = builder.getRemaining().toLowerCase(Locale.ROOT);
                for (CameraModifier modifier : modifierType.getFieldGetter().apply(manager).getModifiers()) {
                    String uuidToString = modifier.getId().toString();
                    if (remaining.isEmpty() || uuidToString.toLowerCase(Locale.ROOT).startsWith(remaining))
                        builder.suggest(uuidToString, modifier.toComponent());
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
