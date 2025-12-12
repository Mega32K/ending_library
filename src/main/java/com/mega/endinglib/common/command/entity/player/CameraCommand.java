package com.mega.endinglib.common.command.entity.player;

import com.mega.endinglib.api.client.Easing;
import com.mega.endinglib.api.client.camera.*;
import com.mega.endinglib.api.client.cmc.LoreHelper;
import com.mega.endinglib.common.capability.EndingLibraryPlayerCapability;
import com.mega.endinglib.common.command.argument.*;
import com.mega.endinglib.common.config.CommandConfig;
import com.mega.endinglib.common.network.PacketHandler;
import com.mega.endinglib.common.network.s2c.camera.S2CBuildAnimationOperationPacket;
import com.mega.endinglib.common.network.s2c.camera.S2CSetCameraEntityPacket;
import com.mega.endinglib.common.network.s2c.camera.S2CSetCameraOriginRotationPacket;
import com.mega.endinglib.common.network.s2c.camera.clientload.S2CCameraAnimationNoticePacket;
import com.mega.endinglib.proxy.CommonProxy;
import com.mega.endinglib.util.java.Args;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.commands.arguments.coordinates.Vec2Argument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
//TODO 支持客户端静态动画
public class CameraCommand {
    public static final DynamicCommandExceptionType KEYFRAME_GROUP_NOT_FOUND = new DynamicCommandExceptionType((a1) -> Component.translatable("commands.endinglib.argument.camera.keyframe.group_not_found", a1));
    static CameraModeDefault DEFAULT_FREEZING_ORIGIN = new CameraModeDefault(CameraCommand::freezeOrigin_default);
    static CameraModeDefault DEFAULT_FREEZING_ORIGIN_FOLLOW_POSITION = new CameraModeDefault(CameraCommand::freezeOrigin_followPosition_default);
    static CameraModeDefault DEFAULT_LOCKED_CAMERA_PERSON = new CameraModeDefault(CameraCommand::lockedCameraPerson_default);
    static CameraModeDefault DEFAULT_LOCKED_FOV = new CameraModeDefault(CameraCommand::lockedFov_default);
    static CameraModeDefault DEFAULT_AVAILABLE_CAMERA_AREA = new CameraModeDefault(CameraCommand::availableCameraArea_default);
    static CameraModeDefault DEFAULT_MOUSE_CONTROL = new CameraModeDefault(CameraCommand::mouseControl_default);
    static CameraModeDefault DEFAULT_LOCKED_CAMERA_ORIGIN_X_ROT = new CameraModeDefault(CameraCommand::freezeOrigin_unlockOriginCameraRot_x);
    static CameraModeDefault DEFAULT_LOCKED_CAMERA_ORIGIN_Y_ROT = new CameraModeDefault(CameraCommand::freezeOrigin_unlockOriginCameraRot_y);
    public static final byte IS_ENABLED = 'a';
    public static final byte IS_FREEZING_ORIGIN = 'b';
    public static final byte FREEZING_MODE_IS_FOLLOW_POSITION = 'c';
    public static final byte IS_CAMERA_PERSON_LOCKED = 'd';
    public static final byte IS_FOV_LOCKED = 'e';
    public static final byte AVAILABLE_CAMERA_AREA = 'f';
    public static final byte IS_MOUSE_CONTROLLED = 'g';

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("camera")
                .requires(stack -> stack.hasPermission(CommandConfig.COMMAND_PERMISSION_CAMERA.get()))
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.literal("enable")
                                .then(Commands.argument("value", BoolArgumentType.bool())
                                        .executes(context -> enableCustomCameraMode(context.getSource(), getPlayer(context), BoolArgumentType.getBool(context, "value")))
                                )
                                .executes(context -> message(context.getSource(), IS_ENABLED, getPlayer(context)))
                        )
                        .then(Commands.literal("buildAnimationJson")
                                .executes(context -> {
                                    AtomicInteger i = new AtomicInteger(0);
                                    ServerPlayer player = getPlayer(context);
                                    CommonProxy.getCameraCapOptional(player).ifPresent(capability -> {
                                        for (ModifierType modifierType : EndingLibraryPlayerCapability.MODIFIER_TYPES) {
                                            if (!modifierType.getFieldGetter().apply(capability.getCameraDataManager()).getKeyframeAnimations().isEmpty())
                                                i.addAndGet(buildAnimationJson(context.getSource(), player, modifierType));
                                        }
                                    });
                                    return i.get();
                                })
                        )
                        .then(Commands.literal("modifier")
                                .then(Commands.literal("addModifier")
                                        .then(Commands.argument("modifierType", CameraModifierArgument.modifierType())
                                                .then(Commands.argument("name", StringArgumentType.word())
                                                        .then(Commands.argument("uuid", UuidArgument.uuid())
                                                                .then(Commands.argument("value", DoubleArgumentType.doubleArg())
                                                                        .then(Commands.argument("operation", CameraOperationArgument.operation())
                                                                                .then(Commands.argument("isPermanent", BoolArgumentType.bool())
                                                                                        .executes(context -> addModifier(
                                                                                                context.getSource(),
                                                                                                getPlayer(context),
                                                                                                CameraModifierArgument.getModifierType(context, "modifierType"),
                                                                                                StringArgumentType.getString(context, "name"),
                                                                                                UuidArgument.getUuid(context, "uuid"),
                                                                                                DoubleArgumentType.getDouble(context, "value"),
                                                                                                CameraOperationArgument.getOperation(context, "operation"),
                                                                                                BoolArgumentType.getBool(context, "isPermanent")
                                                                                        ))
                                                                                )
                                                                        )
                                                                )
                                                        ).then(Commands.argument("value", DoubleArgumentType.doubleArg())
                                                                .then(Commands.argument("operation", CameraOperationArgument.operation())
                                                                        .then(Commands.argument("isPermanent", BoolArgumentType.bool())
                                                                                .executes(context -> addModifier(
                                                                                        context.getSource(),
                                                                                        getPlayer(context),
                                                                                        CameraModifierArgument.getModifierType(context, "modifierType"),
                                                                                        StringArgumentType.getString(context, "name"),
                                                                                        null,
                                                                                        DoubleArgumentType.getDouble(context, "value"),
                                                                                        CameraOperationArgument.getOperation(context, "operation"),
                                                                                        BoolArgumentType.getBool(context, "isPermanent")
                                                                                ))
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                                .then(Commands.literal("removeModifier")
                                        .then(Commands.argument("modifierType", CameraModifierArgument.modifierType())
                                                .then(Commands.argument("uuid", CameraModifierUUIDArgument.uuid())
                                                        .executes(context -> removeModifier(
                                                                context.getSource(),
                                                                getPlayer(context),
                                                                CameraModifierArgument.getModifierType(context, "modifierType"),
                                                                UuidArgument.getUuid(context, "uuid")
                                                        ))
                                                )
                                        )
                                )
                                .then(Commands.literal("removeAllModifiers")
                                        .then(Commands.argument("modifierType", CameraModifierArgument.modifierType())
                                                .executes(context -> removeAllModifiers(
                                                        context.getSource(),
                                                        getPlayer(context),
                                                        CameraModifierArgument.getModifierType(context, "modifierType")
                                                ))
                                        )
                                        .executes(context -> removeAllModifiers(
                                                context.getSource(),
                                                getPlayer(context)
                                        ))
                                )
                                .then(Commands.literal("getModifiers")
                                        .then(Commands.argument("modifierType", CameraModifierArgument.modifierType())
                                                .executes(context -> getModifiers(
                                                        context.getSource(),
                                                        getPlayer(context),
                                                        CameraModifierArgument.getModifierType(context, "modifierType")
                                                ))
                                        )
                                )
                        )
                        .then(Commands.literal("option")
                                .then(Commands.literal("freezingOrigin")
                                        .executes(context -> message(context.getSource(), IS_FREEZING_ORIGIN, getPlayer(context)))
                                        .then(Commands.literal("default")
                                                .executes(DEFAULT_FREEZING_ORIGIN::execute)
                                        )
                                        .then(Commands.argument("value", BoolArgumentType.bool())
                                                .executes(context -> freezeOrigin(context.getSource(), getPlayer(context), BoolArgumentType.getBool(context, "value")))
                                        )
                                        .then(Commands.literal("followPosition")
                                                .then(Commands.literal("default")
                                                        .executes(DEFAULT_FREEZING_ORIGIN_FOLLOW_POSITION::execute)
                                                )
                                                .then(Commands.argument("follow", BoolArgumentType.bool())
                                                        .executes(context -> freezeOrigin_followPosition(context.getSource(), getPlayer(context), BoolArgumentType.getBool(context, "follow")))
                                                )
                                                .executes(context -> message(context.getSource(), FREEZING_MODE_IS_FOLLOW_POSITION, getPlayer(context)))
                                        )
                                        .then(Commands.literal("setOriginCameraRotation")
                                                .then(Commands.argument("rotation", Vec2Argument.vec2(false))
                                                        .executes(context -> freezeOrigin_setOriginCameraRotation(context.getSource(), getPlayer(context), Vec2Argument.getVec2(context, "rotation")))
                                                )
                                        )
                                        .then(Commands.literal("lockOriginCameraRotation")
                                                .then(Commands.literal("xRot")
                                                        .then(Commands.argument("rotation", FloatArgumentType.floatArg())
                                                                .executes(context -> freezeOrigin_lockOriginCameraRot_x(context.getSource(), getPlayer(context), FloatArgumentType.getFloat(context, "rotation")))
                                                        )
                                                        .then(Commands.literal("default")
                                                                .executes(DEFAULT_LOCKED_CAMERA_ORIGIN_X_ROT::execute)
                                                        )
                                                )
                                                .then(Commands.literal("yRot")
                                                        .then(Commands.argument("rotation", FloatArgumentType.floatArg())
                                                                .executes(context -> freezeOrigin_lockOriginCameraRot_y(context.getSource(), getPlayer(context), FloatArgumentType.getFloat(context, "rotation")))
                                                        )
                                                        .then(Commands.literal("default")
                                                                .executes(DEFAULT_LOCKED_CAMERA_ORIGIN_Y_ROT::execute)
                                                        )
                                                )
                                        )
                                )
                                .then(Commands.literal("lockedCameraPerson")
                                        .then(Commands.literal("default")
                                                .executes(DEFAULT_LOCKED_CAMERA_PERSON::execute)
                                        )
                                        .then(Commands.argument("value", BoolArgumentType.bool())
                                                .executes(context -> lockedCameraPerson(context.getSource(), getPlayer(context), BoolArgumentType.getBool(context, "value")))
                                        )
                                        .executes(context -> message(context.getSource(), IS_CAMERA_PERSON_LOCKED, getPlayer(context)))
                                )
                                .then(Commands.literal("lockedFov")
                                        .then(Commands.literal("default")
                                                .executes(DEFAULT_LOCKED_FOV::execute)
                                        )
                                        .then(Commands.argument("value", BoolArgumentType.bool())
                                                .executes(context -> lockedFov(context.getSource(), getPlayer(context), BoolArgumentType.getBool(context, "value")))
                                        )
                                        .executes(context -> message(context.getSource(), IS_FOV_LOCKED, getPlayer(context)))
                                )
                                .then(Commands.literal("availableCameraArea")
                                        .then(Commands.literal("default")
                                                .executes(DEFAULT_AVAILABLE_CAMERA_AREA::execute)
                                        )
                                        .then(Commands.argument("min", Vec3Argument.vec3(false))
                                                .then(Commands.argument("max", Vec3Argument.vec3(false))
                                                        .executes(context -> availableCameraArea(context.getSource(), getPlayer(context), Vec3Argument.getVec3(context, "min"), Vec3Argument.getVec3(context, "max")))
                                                )
                                        )
                                        .executes(context -> message(context.getSource(), AVAILABLE_CAMERA_AREA, getPlayer(context)))
                                )
                                .then(Commands.literal("mouseControl")
                                        .then(Commands.literal("default")
                                                .executes(DEFAULT_MOUSE_CONTROL::execute)
                                        )
                                        .then(Commands.argument("value", BoolArgumentType.bool())
                                                .executes(context -> mouseControl(context.getSource(), getPlayer(context), BoolArgumentType.getBool(context, "value")))
                                        )
                                        .executes(context -> message(context.getSource(), IS_MOUSE_CONTROLLED, getPlayer(context)))
                                )
                                .then(Commands.literal("cameraEntity")
                                        .then(Commands.literal("default")
                                                .executes(context -> setCameraEntity(context.getSource(), getPlayer(context), null))
                                        )
                                        .then(Commands.argument("value", EntityArgument.entity())
                                                .executes(context -> setCameraEntity(context.getSource(), getPlayer(context), EntityArgument.getEntity(context, "value")))
                                        )
                                )
                        )
                        .then(Commands.literal("animation")
                                .then(Commands.literal("static")
                                        .then(animation(true))
                                )
                                .then(animation(false))
                        )
                );
    }
    static ArgumentBuilder<CommandSourceStack, ?> animation(boolean isStatic) {
        if (!isStatic) {
            return Commands.argument("modifierType", CameraModifierArgument.modifierType())
                    .then(Commands.literal("buildJson")
                            .executes(context -> buildAnimationJson(context.getSource(), getPlayer(context), CameraModifierArgument.getModifierType(context, "modifierType")))
                    )
                    .then(Commands.literal("remove")
                            .then(Commands.argument("animationTarget", CameraAnimationArgument.name())
                                    .executes(context -> removeCameraAnimation(context.getSource(), getPlayer(context), CameraModifierArgument.getModifierType(context, "modifierType"), CameraAnimationArgument.getName(context, "animationTarget")))
                            )
                    )
                    .then(Commands.literal("add")
                            .then(Commands.argument("name", StringArgumentType.word())
                                    .then(Commands.argument("animType", CameraAnimTypeArgument.animType())
                                            .then(Commands.argument("duration", FloatArgumentType.floatArg(0))
                                                    .executes(context -> addCameraAnimation(context.getSource(), getPlayer(context), CameraModifierArgument.getModifierType(context, "modifierType"), StringArgumentType.getString(context, "name"), CameraAnimTypeArgument.getAnimType(context, "animType"), FloatArgumentType.getFloat(context, "duration")))
                                            )
                                    )
                            )
                    )
                    .then(Commands.literal("get")
                            .then(Commands.argument("animationTarget", CameraAnimationArgument.name())
                                    .then(Commands.literal("group")
                                            .then(Commands.argument("animationGroup", CameraAnimationGroupArgument.group())
                                                    .then(Commands.literal("addKeyframe")
                                                            .then(Commands.argument("easing", EasingArgument.easing())
                                                                    .then(Commands.argument("timestamp", FloatArgumentType.floatArg(0F))
                                                                            .then(Commands.argument("endPoint", FloatArgumentType.floatArg())
                                                                                    .executes(context -> addKeyframe(context.getSource(), CameraAnimationGroupArgument.getGroup(context, "animationGroup"), getPlayer(context), CameraModifierArgument.getModifierType(context, "modifierType"), CameraAnimationArgument.getName(context, "animationTarget"), EasingArgument.getEasing(context, "easing"), FloatArgumentType.getFloat(context, "timestamp"), FloatArgumentType.getFloat(context, "endPoint")))
                                                                            )
                                                                    )
                                                            )
                                                    )
                                                    .then(Commands.literal("removeKeyframe")
                                                            .then(Commands.argument("index", IntegerArgumentType.integer())
                                                                    .executes(context -> removeKeyframe(context.getSource(), CameraAnimationGroupArgument.getGroup(context, "animationGroup"), getPlayer(context), CameraModifierArgument.getModifierType(context, "modifierType"), CameraAnimationArgument.getName(context, "animationTarget"), IntegerArgumentType.getInteger(context, "index")))
                                                            )
                                                    )
                                                    .then(Commands.literal("modifyKeyframe")
                                                            .then(Commands.argument("index", IntegerArgumentType.integer())
                                                                    .then(Commands.argument("easing", EasingArgument.easing())
                                                                            .then(Commands.argument("timestamp", FloatArgumentType.floatArg(0F))
                                                                                    .then(Commands.argument("endPoint", FloatArgumentType.floatArg())
                                                                                            .executes(context -> modifyKeyframe(context.getSource(), CameraAnimationGroupArgument.getGroup(context, "animationGroup"), getPlayer(context), CameraModifierArgument.getModifierType(context, "modifierType"), CameraAnimationArgument.getName(context, "animationTarget"), IntegerArgumentType.getInteger(context, "index"), EasingArgument.getEasing(context, "easing"), FloatArgumentType.getFloat(context, "timestamp"), FloatArgumentType.getFloat(context, "endPoint")))
                                                                                    )
                                                                            )
                                                                    )
                                                            )
                                                    )
                                                    .then(Commands.literal("insertBefore")
                                                            .then(Commands.argument("index", IntegerArgumentType.integer())
                                                                    .then(Commands.argument("easing", EasingArgument.easing())
                                                                            .then(Commands.argument("timestamp", FloatArgumentType.floatArg(0F))
                                                                                    .then(Commands.argument("endPoint", FloatArgumentType.floatArg())
                                                                                            .executes(context -> insertBeforeKeyframe(context.getSource(), CameraAnimationGroupArgument.getGroup(context, "animationGroup"), getPlayer(context), CameraModifierArgument.getModifierType(context, "modifierType"), CameraAnimationArgument.getName(context, "animationTarget"), IntegerArgumentType.getInteger(context, "index"), EasingArgument.getEasing(context, "easing"), FloatArgumentType.getFloat(context, "timestamp"), FloatArgumentType.getFloat(context, "endPoint")))
                                                                                    )
                                                                            )
                                                                    )
                                                            )
                                                    )
                                            )
                                    )
                                    .then(Commands.literal("addKeyframe")
                                            .then(Commands.argument("easing", EasingArgument.easing())
                                                    .then(Commands.argument("timestamp", FloatArgumentType.floatArg(0F))
                                                            .then(Commands.argument("endPoint", FloatArgumentType.floatArg())
                                                                    .executes(context -> addKeyframe(context.getSource(), CameraKeyframeAnimation.DEFAULT_KEY, getPlayer(context), CameraModifierArgument.getModifierType(context, "modifierType"), CameraAnimationArgument.getName(context, "animationTarget"), EasingArgument.getEasing(context, "easing"), FloatArgumentType.getFloat(context, "timestamp"), FloatArgumentType.getFloat(context, "endPoint")))
                                                            )
                                                    )
                                            )
                                    )
                                    .then(Commands.literal("removeKeyframe")
                                            .then(Commands.argument("index", IntegerArgumentType.integer())
                                                    .executes(context -> removeKeyframe(context.getSource(), CameraKeyframeAnimation.DEFAULT_KEY, getPlayer(context), CameraModifierArgument.getModifierType(context, "modifierType"), CameraAnimationArgument.getName(context, "animationTarget"), IntegerArgumentType.getInteger(context, "index")))
                                            )
                                    )
                                    .then(Commands.literal("modifyKeyframe")
                                            .then(Commands.argument("index", IntegerArgumentType.integer())
                                                    .then(Commands.argument("easing", EasingArgument.easing())
                                                            .then(Commands.argument("timestamp", FloatArgumentType.floatArg(0F))
                                                                    .then(Commands.argument("endPoint", FloatArgumentType.floatArg())
                                                                            .executes(context -> modifyKeyframe(context.getSource(), CameraKeyframeAnimation.DEFAULT_KEY, getPlayer(context), CameraModifierArgument.getModifierType(context, "modifierType"), CameraAnimationArgument.getName(context, "animationTarget"), IntegerArgumentType.getInteger(context, "index"), EasingArgument.getEasing(context, "easing"), FloatArgumentType.getFloat(context, "timestamp"), FloatArgumentType.getFloat(context, "endPoint")))
                                                                    )
                                                            )
                                                    )
                                            )
                                    )
                                    .then(Commands.literal("insertBefore")
                                            .then(Commands.argument("index", IntegerArgumentType.integer())
                                                    .then(Commands.argument("easing", EasingArgument.easing())
                                                            .then(Commands.argument("timestamp", FloatArgumentType.floatArg(0F))
                                                                    .then(Commands.argument("endPoint", FloatArgumentType.floatArg())
                                                                            .executes(context -> insertBeforeKeyframe(context.getSource(), CameraKeyframeAnimation.DEFAULT_KEY, getPlayer(context), CameraModifierArgument.getModifierType(context, "modifierType"), CameraAnimationArgument.getName(context, "animationTarget"), IntegerArgumentType.getInteger(context, "index"), EasingArgument.getEasing(context, "easing"), FloatArgumentType.getFloat(context, "timestamp"), FloatArgumentType.getFloat(context, "endPoint")))
                                                                    )
                                                            )
                                                    )
                                            )
                                    )
                                    .executes(context -> getCameraAnimation(context.getSource(), getPlayer(context), CameraModifierArgument.getModifierType(context, "modifierType"), CameraAnimationArgument.getName(context, "animationTarget")))
                            )
                    )
                    .then(Commands.literal("list")
                            .executes(context -> getCameraAnimations(context.getSource(), getPlayer(context), CameraModifierArgument.getModifierType(context, "modifierType")))
                            .then(Commands.argument("animationTarget", CameraAnimationArgument.name())
                                    .then(Commands.argument("animationGroup", CameraAnimationGroupArgument.group())
                                            .executes(context -> listAnimationKeyframes(context.getSource(), CameraAnimationGroupArgument.getGroup(context, "animationGroup"), getPlayer(context), CameraModifierArgument.getModifierType(context, "modifierType"), CameraAnimationArgument.getName(context, "animationTarget")))
                                    )
                                    .executes(context -> listAnimationKeyframes(context.getSource(), CameraKeyframeAnimation.DEFAULT_KEY, getPlayer(context), CameraModifierArgument.getModifierType(context, "modifierType"), CameraAnimationArgument.getName(context, "animationTarget")))
                            )
                    )
                    .then(Commands.literal("start")
                            .then(Commands.argument("animationTarget", CameraAnimationArgument.name())
                                    .executes(context -> startAnimation(context.getSource(), getPlayer(context), CameraModifierArgument.getModifierType(context, "modifierType"), CameraAnimationArgument.getName(context, "animationTarget")))
                            )
                    )
                    .then(Commands.literal("stop")
                            .then(Commands.argument("animationTarget", CameraAnimationArgument.name())
                                    .executes(context -> stopAnimation(context.getSource(), getPlayer(context), CameraModifierArgument.getModifierType(context, "modifierType"), CameraAnimationArgument.getName(context, "animationTarget")))
                            )
                    );
        } else {
            return Commands.argument("modifierType", CameraModifierArgument.modifierType())
                    .then(Commands.literal("list")
                            .executes(context -> getCameraAnimationsStatic(context.getSource(), getPlayer(context), CameraModifierArgument.getModifierType(context, "modifierType")))
                            .then(Commands.argument("animationTarget", CameraAnimationArgument.name())
                                    .then(Commands.argument("animationGroup", CameraAnimationGroupArgument.group())
                                            .executes(context -> listAnimationKeyframesStatic(CameraAnimationGroupArgument.getGroup(context, "animationGroup"), getPlayer(context), CameraModifierArgument.getModifierType(context, "modifierType"), CameraAnimationArgument.getName(context, "animationTarget")))
                                    )
                                    .executes(context -> listAnimationKeyframesStatic(CameraKeyframeAnimation.DEFAULT_KEY, getPlayer(context), CameraModifierArgument.getModifierType(context, "modifierType"), CameraAnimationArgument.getName(context, "animationTarget")))
                            )
                    )
                    .then(Commands.literal("start")
                            .then(Commands.argument("animationTarget", CameraAnimationArgument.name())
                                    .executes(context -> startAnimationStatic(getPlayer(context), CameraModifierArgument.getModifierType(context, "modifierType"), CameraAnimationArgument.getName(context, "animationTarget")))
                            )
                    )
                    .then(Commands.literal("stop")
                            .then(Commands.argument("animationTarget", CameraAnimationArgument.name())
                                    .executes(context -> stopAnimationStatic(getPlayer(context), CameraModifierArgument.getModifierType(context, "modifierType"), CameraAnimationArgument.getName(context, "animationTarget")))
                            )
                    );
        }
    }
    static ServerPlayer getPlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return EntityArgument.getPlayer(context, "player");
    }
    private static int message(CommandSourceStack stack, byte mode, ServerPlayer player) {
        EndingLibraryPlayerCapability cap = CommonProxy.getCameraCap(player);
        switch (mode) {
            case IS_ENABLED -> {
                boolean b = cap.isUsingCustomCamera();
                stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.camera.option.enable").append(LoreHelper.openoff(b)), false);
                return b ? 1 : 0;
            }
            case IS_FREEZING_ORIGIN -> {
                boolean b = cap.isVanillaCameraFreezing();
                stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.camera.option.freezing_origin").append(LoreHelper.openoff(b)), false);
                return b ? 1 : 0;
            }
            case FREEZING_MODE_IS_FOLLOW_POSITION -> {
                boolean b = cap.isFollowPosition();
                stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.camera.option.freezing_origin.follow_position").append(LoreHelper.openoff(b)), false);
                return b ? 1 : 0;
            }
            case IS_CAMERA_PERSON_LOCKED -> {
                boolean b = cap.isCameraPersonLocked();
                stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.camera.option.is_camera_person_locked").append(LoreHelper.openoff(b)), false);
                return b ? 1 : 0;
            }
            case IS_FOV_LOCKED -> {
                boolean b = cap.isFovLocked();
                stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.camera.option.is_fov_locked").append(LoreHelper.openoff(b)), false);
                return b ? 1 : 0;
            }
            case AVAILABLE_CAMERA_AREA -> {
                Optional<AABB> optional = cap.getCameraAvailableArea();
                stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.camera.option.available_camera_area").append(optional.map(LoreHelper::aabb).orElseGet(() -> LoreHelper.wrap(LoreHelper.empty().withStyle(ChatFormatting.GOLD)))), false);
                return optional.map(AABB::hashCode).orElse(0);
            }
            case IS_MOUSE_CONTROLLED -> {
                boolean b = cap.isMouseControlled();
                stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.camera.option.mouse_control").append(LoreHelper.openoff(b)), false);
                return b ? 1 : 0;
            }
        }
        return 0;
    }
    private static void sendModifyVanillaMessage(CommandSourceStack stack, ServerPlayer player) {
        stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.camera.modify", player.getDisplayName()), false);
    }
    private static void sendModifyMessage(CommandSourceStack stack, ServerPlayer player) {
        stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.camera.camera_mode_modify", player.getDisplayName()), false);
    }
    private static void sendDefaultMessage(CommandSourceStack stack, ServerPlayer player) {
        stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.camera.camera_mode_default", player.getDisplayName()), false);
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
    private static void sendFailedInvalidTarget(CommandSourceStack stack, Entity target) {
        stack.sendFailure(Component.translatable("commands.endinglib.message.message.invalid_target", target.getDisplayName()));
    }
    private static int enableCustomCameraMode(CommandSourceStack stack, ServerPlayer player, boolean flag) {
        CommonProxy.getCameraCapOptional(player).ifPresent(capability -> capability.setUsingCustomCamera(flag));
        if (flag) {
            stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.camera.camera_enable", player.getDisplayName()), false);
        } else
            stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.camera.camera_disable", player.getDisplayName()), false);
        return 0;
    }

    private static int freezeOrigin(CommandSourceStack stack, ServerPlayer player, boolean flag) {
        CommonProxy.getCameraCapOptional(player).ifPresent(capability -> capability.setVanillaCameraFreezing(flag));
        sendModifyMessage(stack, player);
        return 0;
    }
    private static int freezeOrigin_default(CommandSourceStack stack, ServerPlayer player) {
        CommonProxy.getCameraCapOptional(player).ifPresent(capability -> capability.setVanillaCameraFreezing(false));
        return 0;
    }
    private static int freezeOrigin_followPosition(CommandSourceStack stack, ServerPlayer player, boolean flag) {
        CommonProxy.getCameraCapOptional(player).ifPresent(capability -> capability.setFollowPosition(flag));
        sendModifyMessage(stack, player);
        return 0;
    }
    private static int freezeOrigin_followPosition_default(CommandSourceStack stack, ServerPlayer player) {
        CommonProxy.getCameraCapOptional(player).ifPresent(capability -> capability.setFollowPosition(false));
        return 0;
    }
    private static int freezeOrigin_setOriginCameraRotation(CommandSourceStack stack, ServerPlayer player, Vec2 rotation) {
        PacketHandler.sendToPlayer(new S2CSetCameraOriginRotationPacket(rotation.x, rotation.y), player);
        sendModifyMessage(stack, player);
        return 0;
    }
    private static int freezeOrigin_lockOriginCameraRot_x(CommandSourceStack stack, ServerPlayer player, float value) {
        CommonProxy.getCameraCapOptional(player).ifPresent(capability -> {
            capability.lockCameraOriginXRot(value);
            sendModifyMessage(stack, player);
        });
        return 0;
    }
    private static int freezeOrigin_unlockOriginCameraRot_x(CommandSourceStack stack, ServerPlayer player) {
        CommonProxy.getCameraCapOptional(player).ifPresent(EndingLibraryPlayerCapability::unlockCameraOriginXRot);
        return 0;
    }
    private static int freezeOrigin_lockOriginCameraRot_y(CommandSourceStack stack, ServerPlayer player, float value) {
        CommonProxy.getCameraCapOptional(player).ifPresent(capability -> {
            capability.lockCameraOriginYRot(value);
            sendModifyMessage(stack, player);
        });
        return 0;
    }
    private static int freezeOrigin_unlockOriginCameraRot_y(CommandSourceStack stack, ServerPlayer player) {
        CommonProxy.getCameraCapOptional(player).ifPresent(EndingLibraryPlayerCapability::unlockCameraOriginYRot);
        return 0;
    }
    private static int lockedCameraPerson(CommandSourceStack stack, ServerPlayer player, boolean flag) {
        CommonProxy.getCameraCapOptional(player).ifPresent(capability -> capability.setLockedCameraPerson(flag));
        sendModifyMessage(stack, player);
        return 0;
    }
    private static int lockedCameraPerson_default(CommandSourceStack stack, ServerPlayer player) {
        CommonProxy.getCameraCapOptional(player).ifPresent(capability -> capability.setLockedCameraPerson(false));
        return 0;
    }
    private static int lockedFov(CommandSourceStack stack, ServerPlayer player, boolean flag) {
        CommonProxy.getCameraCapOptional(player).ifPresent(capability -> capability.setLockedFov(flag));
        sendModifyMessage(stack, player);
        return 0;
    }
    private static int lockedFov_default(CommandSourceStack stack, ServerPlayer player) {
        CommonProxy.getCameraCapOptional(player).ifPresent(capability -> capability.setLockedFov(false));
        return 0;
    }
    private static int availableCameraArea(CommandSourceStack stack, ServerPlayer player, Vec3 min, Vec3 max) {
        AABB aabb = new AABB(min, max);
        CommonProxy.getCameraCapOptional(player).ifPresent(capability -> {
            capability.setCameraAvailableArea(aabb);
            stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.camera.option.available_camera_area.set", player.getDisplayName(), LoreHelper.aabb(aabb)), false);
        });
        return 0;
    }
    private static int availableCameraArea_default(CommandSourceStack stack, ServerPlayer player) {
        CommonProxy.getCameraCapOptional(player).ifPresent(capability -> capability.setCameraAvailableArea(null));
        return 0;
    }
    private static int mouseControl(CommandSourceStack stack, ServerPlayer player, boolean value) {
        CommonProxy.getCameraCapOptional(player).ifPresent(capability -> {
            capability.setMouseControlled(value);
            sendModifyMessage(stack, player);
        });
        return 0;
    }
    private static int mouseControl_default(CommandSourceStack stack, ServerPlayer player) {
        CommonProxy.getCameraCapOptional(player).ifPresent(capability -> {
            capability.setMouseControlled(false);
        });
        return 0;
    }
    private static int setCameraEntity(CommandSourceStack stack, ServerPlayer player, @Nullable Entity target) {
        PacketHandler.sendToPlayer(new S2CSetCameraEntityPacket(target == null ? -1 : target.getId()), player);
        sendModifyVanillaMessage(stack, player);
        return 0;
    }
    private static int addModifier(CommandSourceStack stack, ServerPlayer player, ModifierType modifierType, String name, final @Nullable UUID uuid, double amount, CameraModifier.Operation operation, boolean isPermanent) {
        CommonProxy.getCameraCapOptional(player).ifPresent(capability -> {
            boolean uuidNull = false;
            UUID id = uuid;
            if (id == null) {
                id = Mth.createInsecureUUID(RandomSource.createNewThreadLocalInstance());
                uuidNull = true;
            }
            CameraModifier modifier = new CameraModifier(id, name, amount, operation);
            if (isPermanent) {
                modifierType.getFieldGetter().apply(capability.getCameraDataManager()).addPermanentModifier(modifier);
            } else {
                modifierType.getFieldGetter().apply(capability.getCameraDataManager()).addTransientModifier(modifier);
            }
            sendModifierMessage(stack, modifier, Component.translatable(uuidNull ? "commands.endinglib.message.camera.add_modifier_null_id" : "commands.endinglib.message.camera.add_modifier", player.getDisplayName()).append(Component.literal(modifierType.name() + " : ").withStyle(ChatFormatting.GOLD)));

        });
        return 0;
    }

    private static int removeModifier(CommandSourceStack stack, ServerPlayer player, ModifierType modifierType, final UUID uuid) {
        if (uuid != null) {
            CommonProxy.getCameraCapOptional(player).ifPresent(capability -> {
                CameraValueInstance cvi = modifierType.getFieldGetter().apply(capability.getCameraDataManager());
                CameraModifier modifier = cvi.getModifier(uuid);
                if (modifier != null) {
                    cvi.removeModifier(uuid);
                    sendModifierMessage(stack, modifier, Component.translatable("commands.endinglib.message.camera.remove_modifier", player.getDisplayName()).append(Component.literal(modifierType.name() + " : ").withStyle(ChatFormatting.GOLD)));
                }
            });
        }
        return 0;
    }

    private static int removeAllModifiers(CommandSourceStack stack, ServerPlayer player) {
        CommonProxy.getCameraCapOptional(player).ifPresent(capability -> {
            int typeCount = 0;
            int count = 0;
            for (ModifierType modifierType : EndingLibraryPlayerCapability.MODIFIER_TYPES) {
                CameraValueInstance cvi = modifierType.getFieldGetter().apply(capability.getCameraDataManager());
                int c = cvi.getModifiers().size();
                if (c > 0) typeCount++;
                count += c;
                cvi.removeModifiers();
            }
            final int i0 = typeCount;
            final int i1 = count;
            stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.camera.remove_all_modifier", player.getDisplayName(), i0, i1), false);
        });
        return 0;
    }

    private static int removeAllModifiers(CommandSourceStack stack, ServerPlayer player, ModifierType... modifierTypes) {
        CommonProxy.getCameraCapOptional(player).ifPresent(capability -> {
            int typeCount = 0;
            int count = 0;
            for (ModifierType modifierType : modifierTypes) {
                CameraValueInstance cvi = modifierType.getFieldGetter().apply(capability.getCameraDataManager());
                int c = cvi.getModifiers().size();
                if (c > 0) typeCount++;
                count += c;
                cvi.removeModifiers();
            }
            final int i0 = typeCount;
            final int i1 = count;
            stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.camera.remove_all_modifier", player.getDisplayName(), i0, i1), false);
        });
        return 0;
    }

    private static int getModifiers(CommandSourceStack stack, ServerPlayer player, ModifierType modifierType) {
        CommonProxy.getCameraCapOptional(player).ifPresent(capability -> {
            Set<CameraModifier> modifiers = modifierType.getFieldGetter().apply(capability.getCameraDataManager()).getModifiers();
            stack.sendSuccess(() -> Component.literal(modifierType.name()).withStyle(ChatFormatting.GREEN), false);
            for (CameraModifier modifier : modifiers) {
                sendModifierMessage(stack, modifier);
            }
        });
        return 0;
    }
    private static int buildAnimationJson(CommandSourceStack stack, ServerPlayer player, ModifierType modifierType) {
        CommonProxy.getCameraCapOptional(player).ifPresent(capability -> {
            CameraValueInstance cvi = modifierType.getFieldGetter().apply(capability.getCameraDataManager());
            Map<String, CameraKeyframeAnimation> map = cvi.animationMap();
            if (map.isEmpty()) {
                stack.sendFailure(Component.translatable("commands.endinglib.message.camera.camera_anim.build.empty", modifierType.name()));
            } else {
                PacketHandler.sendToPlayer(new S2CBuildAnimationOperationPacket(modifierType), player);
                stack.sendSuccess(()-> Component.translatable("commands.endinglib.message.camera.camera_anim.build", modifierType.name()), false);
            }
        });
        return 0;
    }
    private static int getCameraAnimations(CommandSourceStack stack, ServerPlayer player, ModifierType modifierType) {
        CommonProxy.getCameraCapOptional(player).ifPresent(capability -> {
            CameraValueInstance cvi = modifierType.getFieldGetter().apply(capability.getCameraDataManager());
            stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.camera.camera_anim.get_anims", modifierType.name()), false);
            for (CameraKeyframeAnimation animation : cvi.getKeyframeAnimations()) {
                sendAnimationMessage(stack, animation);
            }
        });
        return 0;
    }
    private static int getCameraAnimationsStatic(CommandSourceStack stack, ServerPlayer player, ModifierType modifierType) {
        PacketHandler.sendToPlayer(new S2CCameraAnimationNoticePacket(S2CCameraAnimationNoticePacket.Type.GET_INFO, modifierType, new Args()), player);
        return 0;
    }
    private static int getCameraAnimation(CommandSourceStack stack, ServerPlayer player, ModifierType modifierType, String name) {
        CommonProxy.getCameraCapOptional(player).ifPresent(capability -> {
            CameraValueInstance cvi = modifierType.getFieldGetter().apply(capability.getCameraDataManager());
            stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.camera.camera_anim.get_anim", modifierType.name(), name), false);
            sendAnimationMessage(stack, cvi.getKeyframeAnimation(name));
        });
        return 0;
    }

    private static int addCameraAnimation(CommandSourceStack stack, ServerPlayer player, ModifierType modifierType, String name, CameraKeyframeAnimation.AnimType animType, float duration) {
        CommonProxy.getCameraCapOptional(player).ifPresent(capability -> {
            CameraValueInstance cvi = modifierType.getFieldGetter().apply(capability.getCameraDataManager());
            CameraKeyframeAnimation animation = new CameraKeyframeAnimation(name, animType, duration);
            cvi.addKeyframeAnimation(animation);
            sendModifyMessage(stack, player);
        });
        return 0;
    }

    private static int removeCameraAnimation(CommandSourceStack stack, ServerPlayer player, ModifierType modifierType, String name) {
        CommonProxy.getCameraCapOptional(player).ifPresent(capability -> {
            CameraValueInstance cvi = modifierType.getFieldGetter().apply(capability.getCameraDataManager());
            CameraKeyframeAnimation animation = cvi.getKeyframeAnimation(name);
            sendAnimationMessage(stack, animation);
            cvi.removeKeyframeAnimation(name);
            sendModifyMessage(stack, player);
        });
        return 0;
    }

    private static int addKeyframe(CommandSourceStack stack, String group, ServerPlayer player, ModifierType modifierType, String name, Easing easing, float timestamp, float endPoint) {
        CommonProxy.getCameraCapOptional(player).ifPresent(capability -> {
            CameraValueInstance cvi = modifierType.getFieldGetter().apply(capability.getCameraDataManager());
            CameraKeyframeAnimation animation = cvi.getKeyframeAnimation(name);
            if (animation != null) {
                animation.addKeyframe(group, new CameraKeyframe(timestamp, endPoint, easing));
                cvi.setAnimDirty();
                sendModifyMessage(stack, player);
            }
        });
        return 0;
    }

    private static int modifyKeyframe(CommandSourceStack stack, String group, ServerPlayer player, ModifierType modifierType, String name, int index, Easing easing, float timestamp, float endPoint) throws CommandSyntaxException{
        CameraValueInstance cvi = modifierType.getFieldGetter().apply(CommonProxy.getCameraCap(player).getCameraDataManager());
        CameraKeyframeAnimation animation = cvi.getKeyframeAnimation(name);
        if (animation != null) {
            if (!animation.replaceIndex(group, index, new CameraKeyframe(timestamp, endPoint, easing)))
                throw KEYFRAME_GROUP_NOT_FOUND.create(group);
            cvi.setAnimDirty();
            sendModifyMessage(stack, player);
        }
        return 0;
    }

    private static int removeKeyframe(CommandSourceStack stack, String group, ServerPlayer player, ModifierType modifierType, String name, int indexOfKeyframe) throws CommandSyntaxException{
        CameraValueInstance cvi = modifierType.getFieldGetter().apply(CommonProxy.getCameraCap(player).getCameraDataManager());
        CameraKeyframeAnimation animation = cvi.getKeyframeAnimation(name);
        if (animation != null) {
            if (!animation.removeIndex(group, indexOfKeyframe))
                throw KEYFRAME_GROUP_NOT_FOUND.create(group);
            cvi.setAnimDirty();
            sendModifyMessage(stack, player);
        }
        return 0;
    }

    private static int insertBeforeKeyframe(CommandSourceStack stack, String group, ServerPlayer player, ModifierType modifierType, String name, int index, Easing easing, float timestamp, float endPoint) throws CommandSyntaxException {
        CameraValueInstance cvi = modifierType.getFieldGetter().apply(CommonProxy.getCameraCap(player).getCameraDataManager());
        CameraKeyframeAnimation animation = cvi.getKeyframeAnimation(name);
        if (animation != null) {
            if (!animation.insertBefore(group, index, new CameraKeyframe(timestamp, endPoint, easing)))
                throw KEYFRAME_GROUP_NOT_FOUND.create(group);
            cvi.setAnimDirty();
            sendModifyMessage(stack, player);
        }
        return 0;
    }

    private static int listAnimationKeyframes(CommandSourceStack stack, String group, ServerPlayer player, ModifierType modifierType, String name) throws CommandSyntaxException {
        CameraValueInstance cvi = modifierType.getFieldGetter().apply(CommonProxy.getCameraCap(player).getCameraDataManager());
        CameraKeyframeAnimation animation = cvi.getKeyframeAnimation(name);
        if (animation != null) {
            List<CameraKeyframe> cameraKeyframes = animation.getKeyframes().get(group);
            if (cameraKeyframes == null) throw KEYFRAME_GROUP_NOT_FOUND.create(group);
            stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.camera.camera_anim.list_keyframes"), false);
            if (!cameraKeyframes.isEmpty()) {
                for (int i = 0; i < cameraKeyframes.size(); i++) {
                    CameraKeyframe keyframe = cameraKeyframes.get(i);
                    int finalI = i;
                    stack.sendSuccess(() -> Component.literal(String.valueOf(finalI)).append(keyframe.toComponent()), false);
                }
            }
        }
        return 0;
    }
    private static int listAnimationKeyframesStatic(String group, ServerPlayer player, ModifierType modifierType, String name) {
        if (!group.equals(CameraKeyframeAnimation.DEFAULT_KEY))
            PacketHandler.sendToPlayer(new S2CCameraAnimationNoticePacket(S2CCameraAnimationNoticePacket.Type.GET_KEYFRAMES, modifierType, new Args(name, group)), player);
        else PacketHandler.sendToPlayer(new S2CCameraAnimationNoticePacket(S2CCameraAnimationNoticePacket.Type.GET_KEYFRAMES_DEFAULT, modifierType, new Args(name)), player);
        return 0;
    }
    private static int startAnimation(CommandSourceStack stack, ServerPlayer player, ModifierType modifierType, String name) {
        CommonProxy.getCameraCapOptional(player).ifPresent(capability -> {
            CameraValueInstance cvi = modifierType.getFieldGetter().apply(capability.getCameraDataManager());
            CameraKeyframeAnimation animation = cvi.getKeyframeAnimation(name);
            if (animation != null) {
                animation.setStopped(false);
                cvi.setAnimDirty();
                sendModifyMessage(stack, player);
            }
        });
        return 0;
    }
    private static int startAnimationStatic(ServerPlayer player, ModifierType modifierType, String name) {
        PacketHandler.sendToPlayer(new S2CCameraAnimationNoticePacket(S2CCameraAnimationNoticePacket.Type.START_ANIM, modifierType, new Args(name)), player);
        return 0;
    }
    private static int stopAnimation(CommandSourceStack stack, ServerPlayer player, ModifierType modifierType, String name) {
        CommonProxy.getCameraCapOptional(player).ifPresent(capability -> {
            CameraValueInstance cvi = modifierType.getFieldGetter().apply(capability.getCameraDataManager());
            CameraKeyframeAnimation animation = cvi.getKeyframeAnimation(name);
            if (animation != null) {
                animation.setStopped(true);
                animation.reset();
                cvi.setAnimDirty();

                sendModifyMessage(stack, player);
            }
        });
        return 0;
    }
    private static int stopAnimationStatic(ServerPlayer player, ModifierType modifierType, String name) {
        PacketHandler.sendToPlayer(new S2CCameraAnimationNoticePacket(S2CCameraAnimationNoticePacket.Type.STOP_ANIM, modifierType, new Args(name)), player);
        return 0;
    }
    public static class CameraModeDefault {
        private final BiFunction<CommandSourceStack, ServerPlayer, Integer> consumer;

        public CameraModeDefault(BiFunction<CommandSourceStack, ServerPlayer, Integer> consumer) {
            this.consumer = consumer;
            DEFAULT_CONSUMERS.add(this.consumer);
        }
        public int execute(CommandContext<CommandSourceStack> sourceStackCommandContext) throws CommandSyntaxException {
            CommandSourceStack stack = sourceStackCommandContext.getSource();
            ServerPlayer player = EntityArgument.getPlayer(sourceStackCommandContext, "player");
            int i = this.execute0(stack, player);
            sendDefaultMessage(stack, player);
            return i;
        }
        public int execute0(CommandSourceStack stack, ServerPlayer player) {
            return this.consumer.apply(stack, player);
        }
        public static List<BiFunction<CommandSourceStack, ServerPlayer, Integer>> DEFAULT_CONSUMERS = new ObjectArrayList<>();
    }
}
