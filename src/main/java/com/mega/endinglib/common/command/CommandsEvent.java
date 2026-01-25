package com.mega.endinglib.common.command;

import com.mega.endinglib.common.command.entity.*;
import com.mega.endinglib.common.command.entity.player.*;
import com.mega.endinglib.common.command.test.DHPExtraCommandCommand;
import com.mega.endinglib.common.command.test.DumpCommand;
import com.mega.endinglib.common.command.test.RunFunctionCommand;
import com.mega.endinglib.common.config.CommandConfig;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommandsEvent {
    @SubscribeEvent
    public static void load(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("endinglib")
                        .requires(stack -> stack.hasPermission(CommandConfig.COMMAND_PERMISSION.get()))
                        .then(TimeStopCommand.register())
                        .then(FillEntityCommand.register(event.getBuildContext()))
                        .then(CameraCommand.register())
                        .then(ClientActionCommand.register())
                        .then(PersonalRuleCommand.register())
                        .then(SetFovCommand.register())
                        .then(SetRotationCommand.register())
                        .then(MotionCommand.register())
                        .then(ScheduleCommand.register(event.getDispatcher()))
                        .then(KickCommand.register())
                        .then(PoseCommand.register())
                        .then(CooldownCommand.register(event.getBuildContext()))
                        .then(InputCommand.register())
                        .then(HotbarCommand.register())
                        .then(TargetCommand.register())
                        .then(AnimationCommand.register())
                        .then(TestforCommand.register(event.getDispatcher(), event.getBuildContext()))
                        .then(SoundCommand.register())
                        .then(ShaderCommand.register())
                        .then(FreezeCommand.register())
                        .then(DataCommand.register())
                        .then(HealCommand.register())
                        .then(EntityAnimateCommand.register())
                        .then(DisplayCommand.register())
                        .then(AbilitiesCommand.register())
                        .then(DynamicKeysCommand.register())
                        .then(Commands.literal("hack")
                                .then(DumpCommand.register())
                                .then(RunFunctionCommand.register())
                                //.then(DHPExtraCommandCommand.register())
                        )
        );
    }

    public static void suggestFromExamples(Collection<String> examples, String translationKey, SuggestionsBuilder builder) {
        String remaining = builder.getRemaining().toLowerCase(Locale.ROOT);
        if (!translationKey.contains("%s"))
            translationKey = translationKey + "%s";
        for (String ex : examples) {
            String toLowerExample = ex.toLowerCase(Locale.ROOT);
            if (remaining.isEmpty() || toLowerExample.startsWith(remaining)) {
                builder.suggest(ex, Component.translatable(translationKey.formatted(toLowerExample)));
            }
        }
    }
    public static void suggestFromExamples(Collection<String> examples, SuggestionsBuilder builder) {
        String remaining = builder.getRemaining().toLowerCase(Locale.ROOT);
        for (String ex : examples) {
            String toLowerExample = ex.toLowerCase(Locale.ROOT);
            if (remaining.isEmpty() || toLowerExample.startsWith(remaining)) {
                builder.suggest(ex);
            }
        }
    }

    public static <T> void suggestFromExamples(Map<String, T> examples, SuggestionsBuilder builder, Function<T, Component> function) {
        String remaining = builder.getRemaining().toLowerCase(Locale.ROOT);
        for (var entry : examples.entrySet()) {
            String toLowerExample = entry.getKey().toLowerCase(Locale.ROOT);
            if (remaining.isEmpty() || toLowerExample.startsWith(remaining)) {
                builder.suggest(entry.getKey(), function.apply(entry.getValue()));
            }
        }
    }
}
