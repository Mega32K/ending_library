package com.mega.endinglib.proxy;

import com.mega.endinglib.EndingLibrary;
import com.mega.endinglib.api.capability.ELCapabilityManager;
import com.mega.endinglib.api.capability.EntitySyncCapabilityBase;
import com.mega.endinglib.api.item.component.ItemComponentManager;
import com.mega.endinglib.common.capability.EndingLibraryLivingCapability;
import com.mega.endinglib.common.capability.EndingLibraryPlayerCapability;
import com.mega.endinglib.common.command.argument.*;
import com.mega.endinglib.common.command.entity.selector.NearestEntitySelector;
import com.mega.endinglib.common.command.gamerule.EndingLibraryGameRules;
import com.mega.endinglib.common.init.ModAttributes;
import com.mega.endinglib.common.init.ModCommandArgumentTypes;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.command.EntitySelectorManager;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber

public class CommonProxy implements ModProxy {
    public static LazyOptional<Capability<EndingLibraryPlayerCapability>> PLAYER_CAP = LazyOptional.of(() -> ELCapabilityManager.getCapability(EndingLibraryPlayerCapability.NAME.toString()));
    public static LazyOptional<Capability<EndingLibraryLivingCapability>> LIVING_CAP = LazyOptional.of(() -> ELCapabilityManager.getCapability(EndingLibraryLivingCapability.NAME.toString()));

    public CommonProxy() {
        IEventBus modBus = EndingLibrary.getModEventBus();
        modBus.addListener(this::commonSetup);
        modBus.addListener(this::addAttributes);
    }

    public static EndingLibraryPlayerCapability getCameraCap(Player player) {
        return player.getCapability(PLAYER_CAP.orElse(ELCapabilityManager.getCapability(EndingLibraryPlayerCapability.NAME.toString()))).orElseThrow(NullPointerException::new);
    }

    public static LazyOptional<EndingLibraryPlayerCapability> getCameraCapOptional(Player player) {
        return player.getCapability(PLAYER_CAP.orElse(ELCapabilityManager.getCapability(EndingLibraryPlayerCapability.NAME.toString())));
    }

    public static LazyOptional<EndingLibraryLivingCapability> getLivingCapOptional(LivingEntity livingEntity) {
        return livingEntity.getCapability(LIVING_CAP.orElse(ELCapabilityManager.getCapability(EndingLibraryLivingCapability.NAME.toString())));
    }
    public void commonSetup(final FMLCommonSetupEvent event) {
        ItemComponentManager.init();
        EntitySelectorManager.register("n", new NearestEntitySelector());
        event.enqueueWork(() -> {
            EndingLibraryGameRules.init();
            ELCapabilityManager.regsterCapability(EndingLibraryPlayerCapability::new, new CapabilityToken<EndingLibraryPlayerCapability>() {
            });
            ELCapabilityManager.regsterCapability(EndingLibraryLivingCapability::new, new CapabilityToken<EndingLibraryLivingCapability>() {
            });
            ArgumentTypeInfos.registerByClass(CameraModifierArgument.class, ModCommandArgumentTypes.CAMERA_MODIFIER.get());
            ArgumentTypeInfos.registerByClass(CameraOperationArgument.class, ModCommandArgumentTypes.CAMERA_OPERATION.get());
            ArgumentTypeInfos.registerByClass(CameraActionArgument.class, ModCommandArgumentTypes.CAMERA_ACTION.get());
            ArgumentTypeInfos.registerByClass(CameraModifierUUIDArgument.class, ModCommandArgumentTypes.CAMERA_MODIFIER_ID.get());
            ArgumentTypeInfos.registerByClass(CameraAnimationArgument.class, ModCommandArgumentTypes.CAMERA_ANIMATION_ID.get());
            ArgumentTypeInfos.registerByClass(CameraAnimTypeArgument.class, ModCommandArgumentTypes.CAMERA_ANIMATION_TYPE.get());
            ArgumentTypeInfos.registerByClass(EasingArgument.class, ModCommandArgumentTypes.EASING.get());
            ArgumentTypeInfos.registerByClass(CommandArgument.class, ModCommandArgumentTypes.COMMAND.get());
            ArgumentTypeInfos.registerByClass(CommandBlockArgument.class, ModCommandArgumentTypes.COMMAND_BLOCK.get());
            ArgumentTypeInfos.registerByClass(ItemComponentArgument.class, ModCommandArgumentTypes.ITEM_COMPONENT.get());
            ArgumentTypeInfos.registerByClass(PoseArgument.class, ModCommandArgumentTypes.POSE.get());
            ArgumentTypeInfos.registerByClass(InteractionHandArgument.class, ModCommandArgumentTypes.HAND.get());
            ArgumentTypeInfos.registerByClass(InputOperationArgument.class, ModCommandArgumentTypes.INPUT_OPERATION.get());
        });
    }
    public void addAttributes(EntityAttributeModificationEvent event) {
        ModAttributes.addAttributes(event);
    }
}
