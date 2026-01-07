package com.mega.endinglib.common.init;

import com.mega.endinglib.EndingLibrary;
import com.mega.endinglib.common.command.argument.*;
import com.mega.endinglib.common.command.argument.scehdule.MobTypeArgument;
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
    public static final RegistryObject<ArgumentTypeInfo<CameraAnimationGroupArgument, ?>> CAMERA_ANIMATION_GROUP = REGISTRIES.register("camera_animation_group", () -> SingletonArgumentInfo.contextFree(CameraAnimationGroupArgument::group));
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
    public static final RegistryObject<ArgumentTypeInfo<FloatArrayArgument, ?>> FLOAT_ARRAY = REGISTRIES.register("float_array", FloatArrayArgument.FloatArrayArgumentInfo::new);
    public static final RegistryObject<ArgumentTypeInfo<MobTypeArgument, ?>> MOB_TYPE = REGISTRIES.register("mob_type", () -> SingletonArgumentInfo.contextFree(MobTypeArgument::mobType));
    public static final RegistryObject<ArgumentTypeInfo<PostEffectArgument, ?>> POST_EFFECT = REGISTRIES.register("post_effect", () -> SingletonArgumentInfo.contextFree(PostEffectArgument::postEffect));
    public static final RegistryObject<ArgumentTypeInfo<PostEffectPassArgument, ?>> POST_EFFECT_PASS = REGISTRIES.register("post_effect_pass", () -> SingletonArgumentInfo.contextFree(PostEffectPassArgument::pass));
    public static final RegistryObject<ArgumentTypeInfo<PostEffectUniformArgument, ?>> POST_EFFECT_UNIFORM = REGISTRIES.register("post_effect_uniform", PostEffectUniformArgument.PostEffectUniformArgumentInfo::new);
    public static final RegistryObject<ArgumentTypeInfo<PostShadersArgument, ?>> POST_SHADERS = REGISTRIES.register("post_shaders", () -> SingletonArgumentInfo.contextFree(PostShadersArgument::id));
    public static final RegistryObject<ArgumentTypeInfo<CameraStaticGroupAnimationArgument, ?>> CAMERA_GROUP_ANIMATIONS = REGISTRIES.register("camera_animation_groups", () -> SingletonArgumentInfo.contextFree(CameraStaticGroupAnimationArgument::group));
    public static final RegistryObject<ArgumentTypeInfo<VanillaAnimationArgument, ?>> CAMERA_VANILLA_ANIMATIONS = REGISTRIES.register("vanilla_animations", () -> SingletonArgumentInfo.contextFree(VanillaAnimationArgument::animation));
    public static void init(IEventBus bus) {
        REGISTRIES.register(bus);
    }
}
