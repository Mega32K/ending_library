package com.mega.endinglib.common.config.advanced;

import com.mega.endinglib.EndingLibrary;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = EndingLibrary.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AdvancedCommonConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.ConfigValue<Boolean> CANCEL_ENTITY_UPDATE;
    public static final ForgeConfigSpec SPEC;
    public static boolean CancelEntityUpdate = false;

    static {
        BUILDER.comment("进阶性通用配置");
        CANCEL_ENTITY_UPDATE = BUILDER
                .comment("ZH_CN:禁用所有非玩家实体/方块实体更新")
                .comment("EN_US:Cancel all non-player-entities/block-entities updating")
                .define("DisableEntityUpdate", false);
        SPEC = BUILDER.build();
    }

    @SubscribeEvent
    static void onLoad(ModConfigEvent event) {
        update();
    }

    public static void update() {
        if (SPEC.isLoaded()) {
            CancelEntityUpdate = CANCEL_ENTITY_UPDATE.get();
        }
    }
}
