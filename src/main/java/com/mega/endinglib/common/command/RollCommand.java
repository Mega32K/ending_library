package com.mega.endinglib.common.command;

import com.mega.endinglib.api.client.cmc.LoreHelper;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RollCommand {

    @SubscribeEvent
    public static void load(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("roll")
                        .requires((p_138087_) -> p_138087_.hasPermission(2))
                        .then(Commands.argument("min", IntegerArgumentType.integer())
                                .then(Commands.argument("max", IntegerArgumentType.integer())
                                        .executes(context -> {
                                            Entity entity = context.getSource().getEntity();
                                            if (entity == null)
                                                return 0;
                                            final int i = RandomSource.create().nextInt(IntegerArgumentType.getInteger(context, "min"), IntegerArgumentType.getInteger(context, "max") + 1);
                                            context.getSource().sendSuccess(() -> Component.translatable("commands.endinglib.message.roll",
                                                            entity.getDisplayName(),
                                                            LoreHelper.withCopy(Component.literal(String.valueOf(i)).withStyle(ChatFormatting.GOLD), String.valueOf(i)))
                                                    , false);
                                            return i;
                                        })
                                )
                        )

        );
    }
}
