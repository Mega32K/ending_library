package com.mega.endinglib.common.config.advanced;

import com.mega.endinglib.EndingLibrary;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = EndingLibrary.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AdvancedClientConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.ConfigValue<Boolean> DISABLE_ENTITY_RENDERING;
    public static final ForgeConfigSpec SPEC;
    public static boolean DisableEntityUpdate = false;

    static {
        BUILDER.comment("进阶性客户端配置");
        DISABLE_ENTITY_RENDERING = BUILDER
                .comment("ZH_CN:禁用所有实体渲染")
                .comment("EN_US:Disable all entities rendering")
                .define("DisableEntityRendering", false);
        SPEC = BUILDER.build();
    }

    @SubscribeEvent
    static void onLoad(ModConfigEvent event) {
        update();
    }

    public static void update() {
        if (SPEC.isLoaded()) {
            DisableEntityUpdate = DISABLE_ENTITY_RENDERING.get();
        }
    }
}
