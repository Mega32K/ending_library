package com.mega.endinglib;

import com.mega.endinglib.config.CommonConfig;
import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(EndingLibrary.MODID)
public class EndingLibrary {
    public static final String MODID = "ending_library";
    private static final Logger LOGGER = LogUtils.getLogger();
    public EndingLibrary() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CommonConfig.SPEC, "ending_library/ending_library-common.toml");
        MinecraftForge.EVENT_BUS.register(this);
        LOGGER.info("Mod Loaded!");
    }
}
