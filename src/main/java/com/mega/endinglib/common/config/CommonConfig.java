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
    private static final ForgeConfigSpec.ConfigValue<Boolean> TIME_STOP;
    public static boolean enableTS = true;

    static {
        TIME_STOP = BUILDER.comment("if false, disable the \"time stop\" settings").define("enableTimeStop", true);
        SPEC = BUILDER.build();
    }

    @SubscribeEvent
    static void onLoad(ModConfigEvent event) {
        update();
    }

    public static void update() {
        if (SPEC.isLoaded()) {
            enableTS = TIME_STOP.get();
        }
    }
}
