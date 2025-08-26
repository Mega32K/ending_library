package com.mega.endinglib.proxy;

import com.mega.endinglib.EndingLibrary;
import com.mega.endinglib.common.capability.ELCapabilityManager;
import com.mega.endinglib.test.TestCapability;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CommonProxy implements ModProxy {
    public CommonProxy() {
        IEventBus modBus = EndingLibrary.getModEventBus();
        modBus.addListener(this::commonSetup);
    }

    public void commonSetup(final FMLCommonSetupEvent event) {
        /*
        event.enqueueWork(() -> {
            ELCapabilityManager.regsterCapability(TestCapability.INSTANCE_SUPPLIER.get());
        });
         */
    }

}
