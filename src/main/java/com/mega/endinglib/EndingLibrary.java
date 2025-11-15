package com.mega.endinglib;

import com.mega.endinglib.common.config.ClientConfig;
import com.mega.endinglib.common.config.CommonConfig;
import com.mega.endinglib.common.config.CommandConfig;
import com.mega.endinglib.common.config.advanced.AdvancedClientConfig;
import com.mega.endinglib.common.config.advanced.AdvancedCommonConfig;
import com.mega.endinglib.common.init.ModAttributes;
import com.mega.endinglib.common.init.ModCommandArgumentTypes;
import com.mega.endinglib.common.init.ModMenus;
import com.mega.endinglib.common.init.ModSounds;
import com.mega.endinglib.common.network.PacketHandler;
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
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC, "endinglib/endinglib-common.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC, "endinglib/endinglib-client.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommandConfig.SPEC, "endinglib/endinglib-command.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, AdvancedClientConfig.SPEC, "endinglib/advanced/endinglib-client.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, AdvancedCommonConfig.SPEC, "endinglib/advanced/endinglib-common.toml");
        IEventBus bus = getModEventBus();
        ModSounds.SOUNDS.register(bus);
        ModMenus.REGISTRIES.register(bus);
        ModCommandArgumentTypes.REGISTRIES.register(bus);
        ModAttributes.ATTRIBUTES.register(bus);
        PacketHandler.registerPackets();
        new CommonProxy();
        MinecraftForge.EVENT_BUS.register(this);
        //ModConstructorRun.run();
    }

    public static IEventBus getModEventBus() {
        return FMLJavaModLoadingContext.get().getModEventBus();
    }

}
