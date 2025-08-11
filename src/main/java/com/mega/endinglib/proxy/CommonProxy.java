package com.mega.endinglib.proxy;

import com.mega.endinglib.EndingLibrary;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CommonProxy implements ModProxy {
    public CommonProxy() {
        IEventBus modBus = EndingLibrary.getModEventBus();
        modBus.addListener(this::commonSetup);
    }

    public void commonSetup(final FMLCommonSetupEvent event) {
    }

}
