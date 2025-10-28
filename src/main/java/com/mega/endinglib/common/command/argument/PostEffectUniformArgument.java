package com.mega.endinglib.common.command.argument;

import com.google.gson.JsonObject;
import com.mega.endinglib.client.ClientWrapped;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class PostEffectUniformArgument implements ArgumentType<String> {
    private final boolean isSingle;
    public PostEffectUniformArgument(boolean isSingle) {
        this.isSingle = isSingle;
    }
    public static PostEffectUniformArgument singlePassUniforms() {
        return new PostEffectUniformArgument(true);
    }
    public static PostEffectUniformArgument allPassesUniforms() {
        return new PostEffectUniformArgument(false);
    }

    public static String getUniformName(final CommandContext<?> context, final String name) {
        return context.getArgument(name, String.class);
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        return reader.readString();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if (context.getSource() instanceof SharedSuggestionProvider) {
            return this.isSingle ? ClientWrapped.suggestSinglePassUniforms(context, builder) : ClientWrapped.suggestAllUniforms(context, builder);
        }
        return Suggestions.empty();
    }
    public static class PostEffectUniformArgumentInfo implements ArgumentTypeInfo<PostEffectUniformArgument, PostEffectUniformArgument.PostEffectUniformArgumentInfo.Template> {
        @Override
        public void serializeToNetwork(@NotNull PostEffectUniformArgument.PostEffectUniformArgumentInfo.Template template, @NotNull FriendlyByteBuf byteBuf) {
            byteBuf.writeBoolean(template.isSingle);
        }

        @Override
        public @NotNull PostEffectUniformArgument.PostEffectUniformArgumentInfo.Template deserializeFromNetwork(@NotNull FriendlyByteBuf byteBuf) {
            return new PostEffectUniformArgument.PostEffectUniformArgumentInfo.Template(byteBuf.readBoolean());
        }

        @Override
        public void serializeToJson(@NotNull PostEffectUniformArgument.PostEffectUniformArgumentInfo.Template template, @NotNull JsonObject jsonObject) {
            jsonObject.addProperty("isSingle", template.isSingle);
        }

        @Override
        public @NotNull PostEffectUniformArgument.PostEffectUniformArgumentInfo.Template unpack(@NotNull PostEffectUniformArgument PostEffectUniformArgument) {
            return new PostEffectUniformArgument.PostEffectUniformArgumentInfo.Template(PostEffectUniformArgument.isSingle);
        }

        public final class Template implements ArgumentTypeInfo.Template<PostEffectUniformArgument> {
            final boolean isSingle;

            public Template(boolean isSingle) {
                this.isSingle = isSingle;
            }

            public @NotNull PostEffectUniformArgument instantiate(@NotNull CommandBuildContext context) {
                return new PostEffectUniformArgument(isSingle);
            }

            public @NotNull ArgumentTypeInfo<PostEffectUniformArgument, ?> type() {
                return PostEffectUniformArgument.PostEffectUniformArgumentInfo.this;
            }
        }
    }
}
