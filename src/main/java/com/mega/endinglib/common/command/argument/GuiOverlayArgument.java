package com.mega.endinglib.common.command.argument;

import com.mega.endinglib.client.ClientWrapped;
import com.mega.endinglib.common.command.CommandsEvent;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.GuiOverlayManager;
import net.minecraftforge.client.gui.overlay.NamedGuiOverlay;

import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class GuiOverlayArgument implements ArgumentType<ResourceLocation> {
    public static GuiOverlayArgument overlay() {
        return new GuiOverlayArgument();
    }
    public static ResourceLocation getOverlayId(CommandContext<CommandSourceStack> context, String c) {
        return context.getArgument(c, ResourceLocation.class);
    }

    @Override
    public ResourceLocation parse(StringReader reader) throws CommandSyntaxException {
        return ResourceLocation.read(reader);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if (context.getSource() instanceof SharedSuggestionProvider) {

            suggest(GuiOverlayManager.getOverlays()
                    .stream()
                    .map(NamedGuiOverlay::id)
                    .toList(), "tooltip.endinglib.hud.", builder);
            return builder.buildFuture();
        }
        return Suggestions.empty();
    }
    public static void suggest(Collection<ResourceLocation> examples, String translationKey, SuggestionsBuilder builder) {
        String remaining = builder.getRemaining().toLowerCase(Locale.ROOT);
        if (!translationKey.contains("%s"))
            translationKey = translationKey + "%s";
        for (ResourceLocation rl : examples) {
            String ex = rl.toString();
            String toLowerExample = ex.toLowerCase(Locale.ROOT);
            if (remaining.isEmpty() || toLowerExample.startsWith(remaining)) {
                String langKey = translationKey.formatted(rl.getNamespace() + "."+rl.getPath());
                if (I18n.exists(langKey))
                    builder.suggest(ex, Component.translatable(langKey));
                else builder.suggest(ex);
            }
        }
    }
}
