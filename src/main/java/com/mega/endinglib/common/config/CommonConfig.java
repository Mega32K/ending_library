package com.mega.endinglib.common.config;

import com.mega.endinglib.EndingLibrary;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = EndingLibrary.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    private static final ForgeConfigSpec.ConfigValue<Integer> MAX_EDIT_LENGTH;
    private static final ForgeConfigSpec.ConfigValue<Boolean> TIME_STOP;
    public static int max_edit_length;
    public static boolean enableTS = true;

    static {
        BUILDER.push("Time");
        TIME_STOP = BUILDER.comment("if false, disbale the \"time stop\" settings").define("enableTimeStop", true);
        BUILDER.pop();
        BUILDER.push("Misc");
        MAX_EDIT_LENGTH = BUILDER.comment("Set Edit box max length(for example in Chat Screen).").define("maxEditLength", 512);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    @SubscribeEvent
    static void onLoad(ModConfigEvent event) {
        update();
    }

    public static void update() {
        max_edit_length = Math.min(MAX_EDIT_LENGTH.get(), 32767);
        enableTS = TIME_STOP.get();
    }
}
