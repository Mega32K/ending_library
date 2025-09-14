package com.mega.endinglib.common.command.entity.player;

import com.mega.endinglib.api.client.Easing;
import com.mega.endinglib.api.client.camera.*;
import com.mega.endinglib.api.client.cmc.LoreHelper;
import com.mega.endinglib.common.capability.EndingLibraryPlayerCapability;
import com.mega.endinglib.common.command.argument.*;
import com.mega.endinglib.proxy.CommonProxy;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.UUID;

public class CameraCommand {
    public static final byte IS_ENABLED = 'a';
    public static final byte IS_FREEZING_ORIGIN = 'b';
    public static final byte FREEZING_MODE_IS_FOLLOW_POSITION = 'c';
    public static final byte IS_CAMERA_PERSON_LOCKED = 'd';
    public static final byte IS_FOV_LOCKED = 'e';
    public static final byte AVAILABLE_CAMERA_AREA = 'f';
    public static final byte IS_MOUSE_CONTROLLED = 'g';

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("camera")
                .requires((p_138087_) -> p_138087_.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.literal("enable")
                                .then(Commands.argument("value", BoolArgumentType.bool())
                                        .executes(context -> enableCustomCameraMode(context.getSource(), EntityArgument.getPlayer(context, "player"), BoolArgumentType.getBool(context, "value")))
                                )
                                .executes(context -> message(context.getSource(), IS_ENABLED, EntityArgument.getPlayer(context, "player")))
                        )
                        .then(Commands.literal("modifier")
                                .then(Commands.literal("addModifier")
                                        .then(Commands.argument("modifierType", CameraModifierArgumentType.modifierType())
                                                .then(Commands.argument("name", StringArgumentType.word())
                                                        .then(Commands.argument("uuid", UuidArgument.uuid())
                                                                .then(Commands.argument("value", DoubleArgumentType.doubleArg())
                                                                        .then(Commands.argument("operation", CameraOperationArgumentType.operation())
                                                                                .then(Commands.argument("isPermanent", BoolArgumentType.bool())
                                                                                        .executes(context -> addModifier(
                                                                                                context.getSource(),
                                                                                                EntityArgument.getPlayer(context, "player"),
                                                                                                CameraModifierArgumentType.getModifierType(context, "modifierType"),
                                                                                                StringArgumentType.getString(context, "name"),
                                                                                                UuidArgument.getUuid(context, "uuid"),
                                                                                                DoubleArgumentType.getDouble(context, "value"),
                                                                                                CameraOperationArgumentType.getOperation(context, "operation"),
                                                                                                BoolArgumentType.getBool(context, "isPermanent")
                                                                                        ))
                                                                                )
                                                                        )
                                                                )
                                                        ).then(Commands.argument("value", DoubleArgumentType.doubleArg())
                                                                .then(Commands.argument("operation", CameraOperationArgumentType.operation())
                                                                        .then(Commands.argument("isPermanent", BoolArgumentType.bool())
                                                                                .executes(context -> addModifier(
                                                                                        context.getSource(),
                                                                                        EntityArgument.getPlayer(context, "player"),
                                                                                        CameraModifierArgumentType.getModifierType(context, "modifierType"),
                                                                                        StringArgumentType.getString(context, "name"),
                                                                                        null,
                                                                                        DoubleArgumentType.getDouble(context, "value"),
                                                                                        CameraOperationArgumentType.getOperation(context, "operation"),
                                                                                        BoolArgumentType.getBool(context, "isPermanent")
                                                                                ))
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                                .then(Commands.literal("removeModifier")
                                        .then(Commands.argument("modifierType", CameraModifierArgumentType.modifierType())
                                                .then(Commands.argument("uuid", CameraModifierUUIDArgumentType.uuid())
                                                        .executes(context -> removeModifier(
                                                                context.getSource(),
                                                                EntityArgument.getPlayer(context, "player"),
                                                                CameraModifierArgumentType.getModifierType(context, "modifierType"),
                                                                UuidArgument.getUuid(context, "uuid")
                                                        ))
                                                )
                                        )
                                )
                                .then(Commands.literal("removeAllModifiers")
                                        .then(Commands.argument("modifierType", CameraModifierArgumentType.modifierType())
                                                .executes(context -> removeAllModifiers(
                                                        context.getSource(),
                                                        EntityArgument.getPlayer(context, "player"),
                                                        CameraModifierArgumentType.getModifierType(context, "modifierType")
                                                ))
                                        )
                                        .executes(context -> removeAllModifiers(
                                                context.getSource(),
                                                EntityArgument.getPlayer(context, "player")
                                        ))
                                )
                                .then(Commands.literal("getModifiers")
                                        .then(Commands.argument("modifierType", CameraModifierArgumentType.modifierType())
                                                .executes(context -> getModifiers(
                                                        context.getSource(),
                                                        EntityArgument.getPlayer(context, "player"),
                                                        CameraModifierArgumentType.getModifierType(context, "modifierType")
                                                ))
                                        )
                                )
                        )
                        .then(Commands.literal("option")
                                .then(Commands.literal("freezingOrigin")
                                        .then(Commands.argument("value", BoolArgumentType.bool())
                                                .executes(context -> freezeOrigin(context.getSource(), EntityArgument.getPlayer(context, "player"), BoolArgumentType.getBool(context, "value")))
                                        )
                                        .then(Commands.literal("followPosition")
                                                .then(Commands.argument("follow", BoolArgumentType.bool())
                                                        .executes(context -> freezeOrigin_followPosition(context.getSource(), EntityArgument.getPlayer(context, "player"), BoolArgumentType.getBool(context, "follow")))
                                                )
                                                .executes(context -> message(context.getSource(), FREEZING_MODE_IS_FOLLOW_POSITION, EntityArgument.getPlayer(context, "player")))
                                        )
                                        .executes(context -> message(context.getSource(), IS_FREEZING_ORIGIN, EntityArgument.getPlayer(context, "player")))
                                )
                                .then(Commands.literal("lockedCameraPerson")
                                        .then(Commands.argument("value", BoolArgumentType.bool())
                                                .executes(context -> lockedCameraPersion(context.getSource(), EntityArgument.getPlayer(context, "player"), BoolArgumentType.getBool(context, "value")))
                                        )
                                        .executes(context -> message(context.getSource(), IS_CAMERA_PERSON_LOCKED, EntityArgument.getPlayer(context, "player")))
                                )
                                .then(Commands.literal("lockedFov")
                                        .then(Commands.argument("value", BoolArgumentType.bool())
                                                .executes(context -> lockedFov(context.getSource(), EntityArgument.getPlayer(context, "player"), BoolArgumentType.getBool(context, "value")))
                                        )
                                        .executes(context -> message(context.getSource(), IS_FOV_LOCKED, EntityArgument.getPlayer(context, "player")))
                                )
                                .then(Commands.literal("availableCameraArea")
                                        .then(Commands.argument("min", Vec3Argument.vec3(false))
                                                .then(Commands.argument("max", Vec3Argument.vec3(false))
                                                        .executes(context -> availableCameraArea(context.getSource(), EntityArgument.getPlayer(context, "player"), Vec3Argument.getVec3(context, "min"), Vec3Argument.getVec3(context, "max")))
                                                )
                                        )
                                        .executes(context -> message(context.getSource(), AVAILABLE_CAMERA_AREA, EntityArgument.getPlayer(context, "player")))
                                )
                                .then(Commands.literal("mouseControl")
                                        .then(Commands.argument("value", BoolArgumentType.bool())
                                                .executes(context -> mouseControl(context.getSource(), EntityArgument.getPlayer(context, "player"), BoolArgumentType.getBool(context, "value")))
                                        )
                                        .executes(context -> message(context.getSource(), IS_MOUSE_CONTROLLED, EntityArgument.getPlayer(context, "player")))
                                )
                        )
                        .then(Commands.literal("animation")
                                .then(Commands.argument("modifierType", CameraModifierArgumentType.modifierType())
                                        .then(Commands.literal("remove")
                                                .then(Commands.argument("animationTarget", CameraAnimationArgumentType.name())
                                                        .executes(context -> removeCameraAnimation(context.getSource(), EntityArgument.getPlayer(context, "player"), CameraModifierArgumentType.getModifierType(context, "modifierType"), CameraAnimationArgumentType.getName(context, "animationTarget")))
                                                )
                                        )
                                        .then(Commands.literal("add")
                                                .then(Commands.argument("name", StringArgumentType.word())
                                                        .then(Commands.argument("animType", CameraAnimTypeArgumentType.animType())
                                                                .then(Commands.argument("duration", FloatArgumentType.floatArg(0))
                                                                        .executes(context -> addCameraAnimation(context.getSource(), EntityArgument.getPlayer(context, "player"), CameraModifierArgumentType.getModifierType(context, "modifierType"), StringArgumentType.getString(context, "name"), CameraAnimTypeArgumentType.getAnimType(context, "animType"), FloatArgumentType.getFloat(context, "duration")))
                                                                )
                                                        )
                                                )
                                        )
                                        .then(Commands.literal("insertBefore")
                                                .then(Commands.argument("index", IntegerArgumentType.integer())
                                                        .then(Commands.argument("easing", EasingArgumentType.easing())
                                                                .then(Commands.argument("timestamp", FloatArgumentType.floatArg(0F))
                                                                        .then(Commands.argument("endPoint", FloatArgumentType.floatArg())
                                                                                .executes(context -> insertBeforeKeyframe(context.getSource(), EntityArgument.getPlayer(context, "player"), CameraModifierArgumentType.getModifierType(context, "modifierType"), CameraAnimationArgumentType.getName(context, "animationTarget"), IntegerArgumentType.getInteger(context, "index"), EasingArgumentType.getEasing(context, "easing"), FloatArgumentType.getFloat(context, "timestamp"), FloatArgumentType.getFloat(context, "endPoint")))
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                        .then(Commands.literal("get")
                                                .then(Commands.argument("animationTarget", CameraAnimationArgumentType.name())
                                                        .then(Commands.literal("addKeyframe")
                                                                .then(Commands.argument("easing", EasingArgumentType.easing())
                                                                        .then(Commands.argument("timestamp", FloatArgumentType.floatArg(0F))
                                                                                .then(Commands.argument("endPoint", FloatArgumentType.floatArg())
                                                                                        .executes(context -> addKeyframe(context.getSource(), EntityArgument.getPlayer(context, "player"), CameraModifierArgumentType.getModifierType(context, "modifierType"), CameraAnimationArgumentType.getName(context, "animationTarget"), EasingArgumentType.getEasing(context, "easing"), FloatArgumentType.getFloat(context, "timestamp"), FloatArgumentType.getFloat(context, "endPoint")))
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                        .then(Commands.literal("removeKeyframe")
                                                                .then(Commands.argument("index", IntegerArgumentType.integer())
                                                                        .executes(context -> removeKeyframe(context.getSource(), EntityArgument.getPlayer(context, "player"), CameraModifierArgumentType.getModifierType(context, "modifierType"), CameraAnimationArgumentType.getName(context, "animationTarget"), IntegerArgumentType.getInteger(context, "index")))
                                                                )
                                                        )
                                                        .then(Commands.literal("modifyKeyframe")
                                                                .then(Commands.argument("index", IntegerArgumentType.integer())
                                                                        .then(Commands.argument("easing", EasingArgumentType.easing())
                                                                                .then(Commands.argument("timestamp", FloatArgumentType.floatArg(0F))
                                                                                        .then(Commands.argument("endPoint", FloatArgumentType.floatArg())
                                                                                                .executes(context -> modifyKeyframe(context.getSource(), EntityArgument.getPlayer(context, "player"), CameraModifierArgumentType.getModifierType(context, "modifierType"), CameraAnimationArgumentType.getName(context, "animationTarget"), IntegerArgumentType.getInteger(context, "index"), EasingArgumentType.getEasing(context, "easing"), FloatArgumentType.getFloat(context, "timestamp"), FloatArgumentType.getFloat(context, "endPoint")))
                                                                                        )
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                        .executes(context -> getCameraAnimation(context.getSource(), EntityArgument.getPlayer(context, "player"), CameraModifierArgumentType.getModifierType(context, "modifierType"), CameraAnimationArgumentType.getName(context, "animationTarget")))
                                                )
                                        )
                                        .then(Commands.literal("list")
                                                .executes(context -> getCameraAnimations(context.getSource(), EntityArgument.getPlayer(context, "player"), CameraModifierArgumentType.getModifierType(context, "modifierType")))
                                                .then(Commands.argument("animationTarget", CameraAnimationArgumentType.name())
                                                        .executes(context -> listAnimationKeyframes(context.getSource(), EntityArgument.getPlayer(context, "player"), CameraModifierArgumentType.getModifierType(context, "modifierType"), CameraAnimationArgumentType.getName(context, "animationTarget")))
                                                )
                                        )
                                        .then(Commands.literal("start")
                                                .then(Commands.argument("animationTarget", CameraAnimationArgumentType.name())
                                                        .executes(context -> startAnimation(context.getSource(), EntityArgument.getPlayer(context, "player"), CameraModifierArgumentType.getModifierType(context, "modifierType"), CameraAnimationArgumentType.getName(context, "animationTarget")))
                                                )
                                        )
                                        .then(Commands.literal("stop")
                                                .then(Commands.argument("animationTarget", CameraAnimationArgumentType.name())
                                                        .executes(context -> stopAnimation(context.getSource(), EntityArgument.getPlayer(context, "player"), CameraModifierArgumentType.getModifierType(context, "modifierType"), CameraAnimationArgumentType.getName(context, "animationTarget")))
                                                )
                                        )

                                )
                        )
                );
    }

    private static int message(CommandSourceStack stack, byte mode, ServerPlayer player) {
        if (player.isDeadOrDying()) return 0;
        EndingLibraryPlayerCapability cap = CommonProxy.getCameraCap(player);
        switch (mode) {
            case IS_ENABLED ->
                    stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.camera.option.enable").append(LoreHelper.bool(cap.isUsingCustomCamera())), false);
            case IS_FREEZING_ORIGIN ->
                    stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.camera.option.freezing_origin").append(LoreHelper.bool(cap.isVanillaCameraFreezing())), false);
            case FREEZING_MODE_IS_FOLLOW_POSITION ->
                    stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.camera.option.freezing_origin.follow_position").append(LoreHelper.bool(cap.isFollowPosition())), false);
            case IS_CAMERA_PERSON_LOCKED ->
                    stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.camera.option.is_camera_person_locked").append(LoreHelper.bool(cap.isCameraPersonLocked())), false);
            case IS_FOV_LOCKED ->
                    stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.camera.option.is_fov_locked").append(LoreHelper.bool(cap.isFovLocked())), false);
            case AVAILABLE_CAMERA_AREA ->
                    stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.camera.option.available_camera_area").append(cap.getCameraAvailableArea().isPresent() ? LoreHelper.aabb(cap.getCameraAvailableArea().get()) : LoreHelper.wrap(LoreHelper.empty().withStyle(ChatFormatting.GOLD))), false);
            case IS_MOUSE_CONTROLLED ->
                    stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.camera.option.mouse_control").append(LoreHelper.bool(cap.isMouseControlled())), false);
        }
        return 0;
    }

    private static void sendModifyMessage(CommandSourceStack stack, ServerPlayer player) {
        stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.camera.camera_mode_modify", player.getDisplayName()), false);
    }

    private static void sendAnimationMessage(CommandSourceStack stack, CameraKeyframeAnimation animation, MutableComponent base) {
        stack.sendSuccess(() -> base.append(animation.toComponent()), false);
    }

    private static void sendAnimationMessage(CommandSourceStack stack, CameraKeyframeAnimation animation) {
        sendAnimationMessage(stack, animation, Component.empty());
    }

    private static void sendModifierMessage(CommandSourceStack stack, CameraModifier modifier, MutableComponent base) {
        stack.sendSuccess(() -> base.append(modifier.toComponent()), false);
    }

    private static void sendModifierMessage(CommandSourceStack stack, CameraModifier modifier) {
        sendModifierMessage(stack, modifier, Component.empty());
    }

    private static int enableCustomCameraMode(CommandSourceStack stack, ServerPlayer player, boolean flag) {
        if (player.isDeadOrDying()) return 0;
        CommonProxy.getCameraCap(player).setUsingCustomCamera(flag);
        if (flag) {
            stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.camera.camera_enable", player.getDisplayName()), false);
        } else
            stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.camera.camera_disable", player.getDisplayName()), false);
        return 0;
    }

    private static int freezeOrigin(CommandSourceStack stack, ServerPlayer player, boolean flag) {
        if (player.isDeadOrDying()) return 0;
        CommonProxy.getCameraCap(player).setVanillaCameraFreezing(flag);
        sendModifyMessage(stack, player);
        return 0;
    }

    private static int freezeOrigin_followPosition(CommandSourceStack stack, ServerPlayer player, boolean flag) {
        if (player.isDeadOrDying()) return 0;
        CommonProxy.getCameraCap(player).setFollowPosition(flag);
        sendModifyMessage(stack, player);
        return 0;
    }

    private static int lockedCameraPersion(CommandSourceStack stack, ServerPlayer player, boolean flag) {
        if (player.isDeadOrDying()) return 0;
        CommonProxy.getCameraCap(player).setLockedCameraPerson(flag);
        sendModifyMessage(stack, player);
        return 0;
    }

    private static int lockedFov(CommandSourceStack stack, ServerPlayer player, boolean flag) {
        if (player.isDeadOrDying()) return 0;
        CommonProxy.getCameraCap(player).setLockedFov(flag);
        sendModifyMessage(stack, player);
        return 0;
    }

    private static int availableCameraArea(CommandSourceStack stack, ServerPlayer player, Vec3 min, Vec3 max) {
        if (player.isDeadOrDying()) return 0;
        AABB aabb = new AABB(min, max);
        CommonProxy.getCameraCap(player).setCameraAvailableArea(aabb);
        stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.camera.option.available_camera_area.set", player.getDisplayName(), LoreHelper.aabb(aabb)), false);
        return 0;
    }
    private static int mouseControl(CommandSourceStack stack, ServerPlayer player, boolean value) {
        if (player.isDeadOrDying()) return 0;
        EndingLibraryPlayerCapability capability = CommonProxy.getCameraCap(player);
        if (value) {
            if (!capability.isVanillaCameraFreezing()) {
                stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.camera.option.mouse_control.failure.no_freezing_origin"), true);
                return 0;
            }
        }
        capability.setMouseControlled(value);
        sendModifyMessage(stack, player);
        return 0;
    }
    private static int addModifier(CommandSourceStack stack, ServerPlayer player, ModifierType modifierType, String name, @Nullable UUID uuid, double amount, CameraModifier.Operation operation, boolean isPermanent) {
        if (player.isDeadOrDying()) return 0;
        boolean uuidNull = false;
        if (uuid == null) {
            uuid = Mth.createInsecureUUID(RandomSource.createNewThreadLocalInstance());
            uuidNull = true;
        }
        CameraModifier modifier = new CameraModifier(uuid, name, amount, operation);
        if (isPermanent) {
            modifierType.getFieldGetter().apply(CommonProxy.getCameraCap(player).getCameraDataManager()).addPermanentModifier(modifier);
        } else {
            modifierType.getFieldGetter().apply(CommonProxy.getCameraCap(player).getCameraDataManager()).addTransientModifier(modifier);
        }
        sendModifierMessage(stack, modifier, Component.translatable(uuidNull ? "commands.endinglib.message.camera.add_modifier_null_id" : "commands.endinglib.message.camera.add_modifier", player.getDisplayName()).append(Component.literal(modifierType.name() + " : ").withStyle(ChatFormatting.GOLD)));
        return 0;
    }

    private static int removeModifier(CommandSourceStack stack, ServerPlayer player, ModifierType modifierType, UUID uuid) {
        if (player.isDeadOrDying()) return 0;
        if (uuid != null) {
            CameraValueInstance cvi = modifierType.getFieldGetter().apply(CommonProxy.getCameraCap(player).getCameraDataManager());
            CameraModifier modifier = cvi.getModifier(uuid);
            if (modifier != null) {
                cvi.removeModifier(uuid);
                sendModifierMessage(stack, modifier, Component.translatable("commands.endinglib.message.camera.remove_modifier", player.getDisplayName()).append(Component.literal(modifierType.name() + " : ").withStyle(ChatFormatting.GOLD)));
            }
        }
        return 0;
    }

    private static int removeAllModifiers(CommandSourceStack stack, ServerPlayer player) {
        if (player.isDeadOrDying()) return 0;
        int typeCount = 0;
        int count = 0;
        for (ModifierType modifierType : EndingLibraryPlayerCapability.MODIFIER_TYPES) {
            CameraValueInstance cvi = modifierType.getFieldGetter().apply(CommonProxy.getCameraCap(player).getCameraDataManager());
            int c = cvi.getModifiers().size();
            if (c > 0) typeCount++;
            count += c;
            cvi.removeModifiers();
        }
        final int i0 = typeCount;
        final int i1 = count;
        stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.camera.remove_all_modifier", player.getDisplayName(), i0, i1), false);
        return 0;
    }

    private static int removeAllModifiers(CommandSourceStack stack, ServerPlayer player, ModifierType... modifierTypes) {
        if (player.isDeadOrDying()) return 0;
        int typeCount = 0;
        int count = 0;
        for (ModifierType modifierType : modifierTypes) {
            CameraValueInstance cvi = modifierType.getFieldGetter().apply(CommonProxy.getCameraCap(player).getCameraDataManager());
            int c = cvi.getModifiers().size();
            if (c > 0) typeCount++;
            count += c;
            cvi.removeModifiers();
        }
        final int i0 = typeCount;
        final int i1 = count;
        stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.camera.remove_all_modifier", player.getDisplayName(), i0, i1), false);
        return 0;
    }

    private static int getModifiers(CommandSourceStack stack, ServerPlayer player, ModifierType modifierType) {
        if (player.isDeadOrDying()) return 0;
        Set<CameraModifier> modifiers = modifierType.getFieldGetter().apply(CommonProxy.getCameraCap(player).getCameraDataManager()).getModifiers();
        stack.sendSuccess(() -> Component.literal(modifierType.name()).withStyle(ChatFormatting.GREEN), false);
        for (CameraModifier modifier : modifiers) {
            sendModifierMessage(stack, modifier);
        }
        return 0;
    }
    private static int getCameraAnimations(CommandSourceStack stack, ServerPlayer player, ModifierType modifierType) {
        if (player.isDeadOrDying()) return 0;
        CameraValueInstance cvi = modifierType.getFieldGetter().apply(CommonProxy.getCameraCap(player).getCameraDataManager());
        stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.camera.camera_anim.get_anims", modifierType.name()), false);
        for (CameraKeyframeAnimation animation : cvi.getKeyframeAnimations()) {
            sendAnimationMessage(stack, animation);
        }
        return 0;
    }

    private static int getCameraAnimation(CommandSourceStack stack, ServerPlayer player, ModifierType modifierType, String name) {
        if (player.isDeadOrDying()) return 0;
        CameraValueInstance cvi = modifierType.getFieldGetter().apply(CommonProxy.getCameraCap(player).getCameraDataManager());
        stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.camera.camera_anim.get_anim", modifierType.name(), name), false);
        sendAnimationMessage(stack, cvi.getKeyframeAnimation(name));
        return 0;
    }

    private static int addCameraAnimation(CommandSourceStack stack, ServerPlayer player, ModifierType modifierType, String name, CameraKeyframeAnimation.AnimType animType, float duration) {
        if (player.isDeadOrDying()) return 0;
        CameraValueInstance cvi = modifierType.getFieldGetter().apply(CommonProxy.getCameraCap(player).getCameraDataManager());
        CameraKeyframeAnimation animation = new CameraKeyframeAnimation(name, animType, duration);
        cvi.addKeyframeAnimation(animation);
        sendModifyMessage(stack, player);
        return 0;
    }

    private static int removeCameraAnimation(CommandSourceStack stack, ServerPlayer player, ModifierType modifierType, String name) {
        if (player.isDeadOrDying()) return 0;
        CameraValueInstance cvi = modifierType.getFieldGetter().apply(CommonProxy.getCameraCap(player).getCameraDataManager());
        CameraKeyframeAnimation animation = cvi.getKeyframeAnimation(name);
        sendAnimationMessage(stack, animation);
        cvi.removeKeyframeAnimation(name);
        sendModifyMessage(stack, player);
        return 0;
    }

    private static int addKeyframe(CommandSourceStack stack, ServerPlayer player, ModifierType modifierType, String name, Easing easing, float timestamp, float endPoint) {
        if (player.isDeadOrDying()) return 0;
        CameraValueInstance cvi = modifierType.getFieldGetter().apply(CommonProxy.getCameraCap(player).getCameraDataManager());
        CameraKeyframeAnimation animation = cvi.getKeyframeAnimation(name);
        if (animation != null) {
            animation.addKeyframe(new CameraKeyframe(timestamp, endPoint, easing));
            cvi.setAnimDirty();
            sendModifyMessage(stack, player);
        }
        return 0;
    }

    private static int modifyKeyframe(CommandSourceStack stack, ServerPlayer player, ModifierType modifierType, String name, int index, Easing easing, float timestamp, float endPoint) {
        if (player.isDeadOrDying()) return 0;
        CameraValueInstance cvi = modifierType.getFieldGetter().apply(CommonProxy.getCameraCap(player).getCameraDataManager());
        CameraKeyframeAnimation animation = cvi.getKeyframeAnimation(name);
        if (animation != null) {
            animation.replaceIndex(index, new CameraKeyframe(timestamp, endPoint, easing));
            cvi.setAnimDirty();
            sendModifyMessage(stack, player);
        }
        return 0;
    }

    private static int removeKeyframe(CommandSourceStack stack, ServerPlayer player, ModifierType modifierType, String name, int indexOfKeyframe) {
        if (player.isDeadOrDying()) return 0;
        CameraValueInstance cvi = modifierType.getFieldGetter().apply(CommonProxy.getCameraCap(player).getCameraDataManager());
        CameraKeyframeAnimation animation = cvi.getKeyframeAnimation(name);
        if (animation != null) {
            animation.removeIndex(indexOfKeyframe);
            cvi.setAnimDirty();
            sendModifyMessage(stack, player);
        }
        return 0;
    }

    private static int insertBeforeKeyframe(CommandSourceStack stack, ServerPlayer player, ModifierType modifierType, String name, int index, Easing easing, float timestamp, float endPoint) {
        if (player.isDeadOrDying()) return 0;
        CameraValueInstance cvi = modifierType.getFieldGetter().apply(CommonProxy.getCameraCap(player).getCameraDataManager());
        CameraKeyframeAnimation animation = cvi.getKeyframeAnimation(name);
        if (animation != null) {
            animation.insertBefore(index, new CameraKeyframe(timestamp, endPoint, easing));
            cvi.setAnimDirty();
            sendModifyMessage(stack, player);
        }
        return 0;
    }

    private static int listAnimationKeyframes(CommandSourceStack stack, ServerPlayer player, ModifierType modifierType, String name) {
        if (player.isDeadOrDying()) return 0;
        CameraValueInstance cvi = modifierType.getFieldGetter().apply(CommonProxy.getCameraCap(player).getCameraDataManager());
        CameraKeyframeAnimation animation = cvi.getKeyframeAnimation(name);
        if (animation != null) {
            stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.camera.camera_anim.list_keyframes"), false);
            for (int i = 0; i < animation.getKeyframes().size(); i++) {
                CameraKeyframe keyframe = animation.getKeyframes().get(i);
                int finalI = i;
                stack.sendSuccess(() -> Component.literal(String.valueOf(finalI)).append(keyframe.toComponent()), false);
            }
            sendModifyMessage(stack, player);
        }
        return 0;
    }

    private static int startAnimation(CommandSourceStack stack, ServerPlayer player, ModifierType modifierType, String name) {
        if (player.isDeadOrDying()) return 0;
        CameraValueInstance cvi = modifierType.getFieldGetter().apply(CommonProxy.getCameraCap(player).getCameraDataManager());
        CameraKeyframeAnimation animation = cvi.getKeyframeAnimation(name);
        if (animation != null) {
            animation.setStopped(false);
            cvi.setAnimDirty();
            sendModifyMessage(stack, player);
        }
        return 0;
    }

    private static int stopAnimation(CommandSourceStack stack, ServerPlayer player, ModifierType modifierType, String name) {
        if (player.isDeadOrDying()) return 0;
        CameraValueInstance cvi = modifierType.getFieldGetter().apply(CommonProxy.getCameraCap(player).getCameraDataManager());
        CameraKeyframeAnimation animation = cvi.getKeyframeAnimation(name);
        if (animation != null) {
            animation.setStopped(true);
            animation.reset();
            cvi.setAnimDirty();

            sendModifyMessage(stack, player);
        }
        return 0;
    }
}
