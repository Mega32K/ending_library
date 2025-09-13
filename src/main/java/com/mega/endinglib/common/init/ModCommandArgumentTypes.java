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
    public static final RegistryObject<ArgumentTypeInfo<CameraModifierArgumentType, ?>> CAMERA_MODIFIER = REGISTRIES.register("camera_modifier", ()-> SingletonArgumentInfo.contextFree(CameraModifierArgumentType::modifierType));
    public static final RegistryObject<ArgumentTypeInfo<CameraOperationArgumentType, ?>> CAMERA_OPERATION = REGISTRIES.register("camera_operation", ()-> SingletonArgumentInfo.contextFree(CameraOperationArgumentType::operation));
    public static final RegistryObject<ArgumentTypeInfo<CameraActionArgumentType, ?>> CAMERA_ACTION = REGISTRIES.register("camera_action", ()-> SingletonArgumentInfo.contextFree(CameraActionArgumentType::action));
    public static final RegistryObject<ArgumentTypeInfo<CameraModifierUUIDArgumentType, ?>> CAMERA_MODIFIER_ID = REGISTRIES.register("camera_modifier_id", ()-> SingletonArgumentInfo.contextFree(CameraModifierUUIDArgumentType::uuid));
    public static final RegistryObject<ArgumentTypeInfo<CameraAnimationArgumentType, ?>> CAMERA_ANIMATION_ID = REGISTRIES.register("camera_animation_id", ()-> SingletonArgumentInfo.contextFree(CameraAnimationArgumentType::name));
    public static final RegistryObject<ArgumentTypeInfo<CameraAnimTypeArgumentType, ?>> CAMERA_ANIMATION_TYPE = REGISTRIES.register("camera_animation_type", ()-> SingletonArgumentInfo.contextFree(CameraAnimTypeArgumentType::animType));
    public static final RegistryObject<ArgumentTypeInfo<EasingArgumentType, ?>> EASING = REGISTRIES.register("easing", ()-> SingletonArgumentInfo.contextFree(EasingArgumentType::easing));
    public static final RegistryObject<ArgumentTypeInfo<CommandArgumentType, ?>> COMMAND = REGISTRIES.register("command", ()-> SingletonArgumentInfo.contextFree(CommandArgumentType::command));
    public static final RegistryObject<ArgumentTypeInfo<CommandBlockArgumentType, ?>> COMMAND_BLOCK = REGISTRIES.register("command_block", ()-> SingletonArgumentInfo.contextFree(CommandBlockArgumentType::commandBlock));


    public static void init(IEventBus bus) {
        REGISTRIES.register(bus);
    }
}
