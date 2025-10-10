package com.mega.endinglib.common.init;

import com.mega.endinglib.EndingLibrary;
import com.mega.endinglib.common.command.argument.*;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModCommandArgumentTypes {
    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> REGISTRIES = DeferredRegister.create(ForgeRegistries.COMMAND_ARGUMENT_TYPES, EndingLibrary.MODID);
    public static final RegistryObject<ArgumentTypeInfo<CameraModifierArgument, ?>> CAMERA_MODIFIER = REGISTRIES.register("camera_modifier", () -> SingletonArgumentInfo.contextFree(CameraModifierArgument::modifierType));
    public static final RegistryObject<ArgumentTypeInfo<CameraOperationArgument, ?>> CAMERA_OPERATION = REGISTRIES.register("camera_operation", () -> SingletonArgumentInfo.contextFree(CameraOperationArgument::operation));
    public static final RegistryObject<ArgumentTypeInfo<CameraActionArgument, ?>> CAMERA_ACTION = REGISTRIES.register("camera_action", () -> SingletonArgumentInfo.contextFree(CameraActionArgument::action));
    public static final RegistryObject<ArgumentTypeInfo<CameraModifierUUIDArgument, ?>> CAMERA_MODIFIER_ID = REGISTRIES.register("camera_modifier_id", () -> SingletonArgumentInfo.contextFree(CameraModifierUUIDArgument::uuid));
    public static final RegistryObject<ArgumentTypeInfo<CameraAnimationArgument, ?>> CAMERA_ANIMATION_ID = REGISTRIES.register("camera_animation_id", () -> SingletonArgumentInfo.contextFree(CameraAnimationArgument::name));
    public static final RegistryObject<ArgumentTypeInfo<CameraAnimTypeArgument, ?>> CAMERA_ANIMATION_TYPE = REGISTRIES.register("camera_animation_type", () -> SingletonArgumentInfo.contextFree(CameraAnimTypeArgument::animType));
    public static final RegistryObject<ArgumentTypeInfo<EasingArgument, ?>> EASING = REGISTRIES.register("easing", () -> SingletonArgumentInfo.contextFree(EasingArgument::easing));
    public static final RegistryObject<ArgumentTypeInfo<CommandArgument, ?>> COMMAND = REGISTRIES.register("command", () -> SingletonArgumentInfo.contextFree(CommandArgument::command));
    public static final RegistryObject<ArgumentTypeInfo<CommandBlockArgument, ?>> COMMAND_BLOCK = REGISTRIES.register("command_block", () -> SingletonArgumentInfo.contextFree(CommandBlockArgument::commandBlock));
    public static final RegistryObject<ArgumentTypeInfo<ItemComponentArgument, ?>> ITEM_COMPONENT = REGISTRIES.register("item_component", () -> SingletonArgumentInfo.contextFree(ItemComponentArgument::component));
    public static final RegistryObject<ArgumentTypeInfo<PoseArgument, ?>> POSE = REGISTRIES.register("pose", () -> SingletonArgumentInfo.contextFree(PoseArgument::pose));
    public static final RegistryObject<ArgumentTypeInfo<InteractionHandArgument, ?>> HAND = REGISTRIES.register("hand", () -> SingletonArgumentInfo.contextFree(InteractionHandArgument::hand));
    public static final RegistryObject<ArgumentTypeInfo<InputOperationArgument, ?>> INPUT_OPERATION = REGISTRIES.register("input_operation", () -> SingletonArgumentInfo.contextFree(InputOperationArgument::operation));
    public static final RegistryObject<ArgumentTypeInfo<PlayerAnimationArgument, ?>> PLAYER_ANIMATION = REGISTRIES.register("player_animation", () -> SingletonArgumentInfo.contextFree(PlayerAnimationArgument::animation));
    public static final RegistryObject<ArgumentTypeInfo<DirectionArgument, ?>> DIRECTION = REGISTRIES.register("block_direction", () -> SingletonArgumentInfo.contextFree(DirectionArgument::direction));
    public static void init(IEventBus bus) {
        REGISTRIES.register(bus);
    }
}
