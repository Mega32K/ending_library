package com.mega.endinglib.proxy;

import com.mega.endinglib.EndingLibrary;
import com.mega.endinglib.api.capability.ELCapabilityManager;
import com.mega.endinglib.api.item.component.ItemComponentManager;
import com.mega.endinglib.common.capability.EndingLibraryEntityCapability;
import com.mega.endinglib.common.capability.EndingLibraryLivingCapability;
import com.mega.endinglib.common.capability.EndingLibraryPlayerCapability;
import com.mega.endinglib.common.capability.display.TextDisplayCapability;
import com.mega.endinglib.common.command.argument.*;
import com.mega.endinglib.common.command.argument.scehdule.MobTypeArgument;
import com.mega.endinglib.common.command.entity.selector.MobEntitySelector;
import com.mega.endinglib.common.command.entity.selector.NearestEntitySelector;
import com.mega.endinglib.common.command.gamerule.EndingLibraryGameRules;
import com.mega.endinglib.common.init.ModAttributes;
import com.mega.endinglib.common.init.ModCommandArgumentTypes;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.world.entity.Entity;
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
    public static LazyOptional<Capability<EndingLibraryEntityCapability>> ENTITY_CAP = LazyOptional.of(() -> ELCapabilityManager.getCapability(EndingLibraryEntityCapability.NAME.toString()));
    public static LazyOptional<Capability<TextDisplayCapability>> TEXT_CAP = LazyOptional.of(() -> ELCapabilityManager.getCapability(TextDisplayCapability.NAME.toString()));

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
    public static LazyOptional<EndingLibraryEntityCapability> getEntityCapOptional(Entity entity) {
        return entity.getCapability(ENTITY_CAP.orElse(ELCapabilityManager.getCapability(EndingLibraryEntityCapability.NAME.toString())));
    }
    public static LazyOptional<TextDisplayCapability> getTextCapOptional(Entity entity) {
        return entity.getCapability(TEXT_CAP.orElse(ELCapabilityManager.getCapability(TextDisplayCapability.NAME.toString())));
    }
    public void commonSetup(final FMLCommonSetupEvent event) {
        ItemComponentManager.init();
        EntitySelectorManager.register("m", new MobEntitySelector());
        EntitySelectorManager.register("n", new NearestEntitySelector());
        event.enqueueWork(() -> {
            EndingLibraryGameRules.init();
            ELCapabilityManager.regsterCapability(EndingLibraryPlayerCapability::new, new CapabilityToken<EndingLibraryPlayerCapability>() {
            });
            ELCapabilityManager.regsterCapability(EndingLibraryLivingCapability::new, new CapabilityToken<EndingLibraryLivingCapability>() {
            });
            ELCapabilityManager.regsterCapability(EndingLibraryEntityCapability::new, new CapabilityToken<EndingLibraryEntityCapability>() {
            });
            ELCapabilityManager.regsterCapability(TextDisplayCapability::new, new CapabilityToken<TextDisplayCapability>() {
            });
            ArgumentTypeInfos.registerByClass(CameraModifierArgument.class, ModCommandArgumentTypes.CAMERA_MODIFIER.get());
            ArgumentTypeInfos.registerByClass(CameraOperationArgument.class, ModCommandArgumentTypes.CAMERA_OPERATION.get());
            ArgumentTypeInfos.registerByClass(CameraActionArgument.class, ModCommandArgumentTypes.CAMERA_ACTION.get());
            ArgumentTypeInfos.registerByClass(CameraModifierUUIDArgument.class, ModCommandArgumentTypes.CAMERA_MODIFIER_ID.get());
            ArgumentTypeInfos.registerByClass(CameraAnimationArgument.class, ModCommandArgumentTypes.CAMERA_ANIMATION_ID.get());
            ArgumentTypeInfos.registerByClass(CameraAnimationGroupArgument.class, ModCommandArgumentTypes.CAMERA_ANIMATION_GROUP.get());
            ArgumentTypeInfos.registerByClass(CameraAnimTypeArgument.class, ModCommandArgumentTypes.CAMERA_ANIMATION_TYPE.get());
            ArgumentTypeInfos.registerByClass(EasingArgument.class, ModCommandArgumentTypes.EASING.get());
            ArgumentTypeInfos.registerByClass(CommandArgument.class, ModCommandArgumentTypes.COMMAND.get());
            ArgumentTypeInfos.registerByClass(CommandBlockArgument.class, ModCommandArgumentTypes.COMMAND_BLOCK.get());
            ArgumentTypeInfos.registerByClass(ItemComponentArgument.class, ModCommandArgumentTypes.ITEM_COMPONENT.get());
            ArgumentTypeInfos.registerByClass(PoseArgument.class, ModCommandArgumentTypes.POSE.get());
            ArgumentTypeInfos.registerByClass(InteractionHandArgument.class, ModCommandArgumentTypes.HAND.get());
            ArgumentTypeInfos.registerByClass(InputOperationArgument.class, ModCommandArgumentTypes.INPUT_OPERATION.get());
            ArgumentTypeInfos.registerByClass(PlayerAnimationArgument.class, ModCommandArgumentTypes.PLAYER_ANIMATION.get());
            ArgumentTypeInfos.registerByClass(DirectionArgument.class, ModCommandArgumentTypes.DIRECTION.get());
            ArgumentTypeInfos.registerByClass(FloatArrayArgument.class, ModCommandArgumentTypes.FLOAT_ARRAY.get());
            ArgumentTypeInfos.registerByClass(MobTypeArgument.class, ModCommandArgumentTypes.MOB_TYPE.get());
            ArgumentTypeInfos.registerByClass(PostEffectArgument.class, ModCommandArgumentTypes.POST_EFFECT.get());
            ArgumentTypeInfos.registerByClass(PostEffectPassArgument.class, ModCommandArgumentTypes.POST_EFFECT_PASS.get());
            ArgumentTypeInfos.registerByClass(PostEffectUniformArgument.class, ModCommandArgumentTypes.POST_EFFECT_UNIFORM.get());
            ArgumentTypeInfos.registerByClass(PostShadersArgument.class, ModCommandArgumentTypes.POST_SHADERS.get());
            ArgumentTypeInfos.registerByClass(CameraStaticGroupAnimationArgument.class, ModCommandArgumentTypes.CAMERA_GROUP_ANIMATIONS.get());
            ArgumentTypeInfos.registerByClass(VanillaAnimationArgument.class, ModCommandArgumentTypes.CAMERA_VANILLA_ANIMATIONS.get());
            ArgumentTypeInfos.registerByClass(GuiOverlayArgument.class, ModCommandArgumentTypes.GUI_OVERLAY_ANIMATIONS.get());;
            ArgumentTypeInfos.registerByClass(TextColorArgument.class, ModCommandArgumentTypes.TEXT_COLOR.get());
        });
    }
    public void addAttributes(EntityAttributeModificationEvent event) {
        ModAttributes.addAttributes(event);
    }
}
