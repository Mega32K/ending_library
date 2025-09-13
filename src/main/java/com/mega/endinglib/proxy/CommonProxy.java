package com.mega.endinglib.proxy;

import com.mega.endinglib.EndingLibrary;
import com.mega.endinglib.common.capability.EndingLibraryPlayerCapability;
import com.mega.endinglib.api.capability.ELCapabilityManager;
import com.mega.endinglib.common.command.argument.*;
import com.mega.endinglib.common.command.gamerule.EndingLibraryGameRules;
import com.mega.endinglib.common.init.ModAttributes;
import com.mega.endinglib.common.init.ModCommandArgumentTypes;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
@Mod.EventBusSubscriber

public class CommonProxy implements ModProxy {
    public static LazyOptional<Capability<EndingLibraryPlayerCapability>> CAMERA_CAP = LazyOptional.of(()-> ELCapabilityManager.getCapability(EndingLibraryPlayerCapability.NAME.toString()));
    public CommonProxy() {
        IEventBus modBus = EndingLibrary.getModEventBus();
        modBus.addListener(this::commonSetup);
        modBus.addListener(this::addAttributes);
    }

    public void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            EndingLibraryGameRules.init();
            ELCapabilityManager.regsterCapability(EndingLibraryPlayerCapability::new);
            ArgumentTypeInfos.registerByClass(CameraModifierArgumentType.class, ModCommandArgumentTypes.CAMERA_MODIFIER.get());
            ArgumentTypeInfos.registerByClass(CameraOperationArgumentType.class, ModCommandArgumentTypes.CAMERA_OPERATION.get());
            ArgumentTypeInfos.registerByClass(CameraActionArgumentType.class, ModCommandArgumentTypes.CAMERA_ACTION.get());
            ArgumentTypeInfos.registerByClass(CameraModifierUUIDArgumentType.class, ModCommandArgumentTypes.CAMERA_MODIFIER_ID.get());
            ArgumentTypeInfos.registerByClass(CameraAnimationArgumentType.class, ModCommandArgumentTypes.CAMERA_ANIMATION_ID.get());
            ArgumentTypeInfos.registerByClass(CameraAnimTypeArgumentType.class, ModCommandArgumentTypes.CAMERA_ANIMATION_TYPE.get());
            ArgumentTypeInfos.registerByClass(EasingArgumentType.class, ModCommandArgumentTypes.EASING.get());
            ArgumentTypeInfos.registerByClass(CommandArgumentType.class, ModCommandArgumentTypes.COMMAND.get());
            ArgumentTypeInfos.registerByClass(CommandBlockArgumentType.class, ModCommandArgumentTypes.COMMAND_BLOCK.get());
        });
    }

    public void addAttributes(EntityAttributeModificationEvent event) {
        ModAttributes.addAttributes(event);
    }
    public static EndingLibraryPlayerCapability getCameraCap(Player player) {
        return player.getCapability(CAMERA_CAP.orElse(ELCapabilityManager.getCapability(EndingLibraryPlayerCapability.NAME.toString()))).orElseThrow(NullPointerException::new);
    }
    public static LazyOptional<EndingLibraryPlayerCapability> getCameraCapOptional(Player player) {
        return player.getCapability(CAMERA_CAP.orElse(ELCapabilityManager.getCapability(EndingLibraryPlayerCapability.NAME.toString())));
    }
}
