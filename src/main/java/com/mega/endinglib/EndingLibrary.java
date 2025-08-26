package com.mega.endinglib;

import com.mega.endinglib.common.init.ModSounds;
import com.mega.endinglib.network.PacketHandler;
import com.mega.endinglib.config.CommonConfig;
import com.mega.endinglib.proxy.ClientProxy;
import com.mega.endinglib.proxy.CommonProxy;
import com.mega.endinglib.proxy.ModProxy;
import com.mega.endinglib.proxy.ServerProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(EndingLibrary.MODID)
public class EndingLibrary {
    public static final String MODID = "ending_library";
    public static final ModProxy PROXY = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);
    public static final Logger LOGGER = LogManager.getLogger();

    public EndingLibrary() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC, "ending_library/ending_library-common.toml");
        IEventBus bus = getModEventBus();
        ModSounds.SOUNDS.register(bus);
        PacketHandler.registerPackets();
        new CommonProxy();
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static IEventBus getModEventBus() {
        return FMLJavaModLoadingContext.get().getModEventBus();
    }
}
