package com.mega.endinglib.common.config;

import com.mega.endinglib.EndingLibrary;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = EndingLibrary.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ServerConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec.ConfigValue<Integer> COMMAND_PERMISSION;
    public static final ForgeConfigSpec.ConfigValue<Integer> COMMAND_PERMISSION_CAMERA;
    public static final ForgeConfigSpec.ConfigValue<Integer> COMMAND_PERMISSION_ENDERCHEST;
    public static final ForgeConfigSpec.ConfigValue<Integer> COMMAND_PERMISSION_ENDERCHEST_OTHER;
    public static final ForgeConfigSpec.ConfigValue<Integer> COMMAND_PERMISSION_INV;
    public static final ForgeConfigSpec.ConfigValue<Integer> COMMAND_PERMISSION_PERSONAL_RULE;
    public static final ForgeConfigSpec.ConfigValue<Integer> COMMAND_PERMISSION_SCHEDULE;
    public static final ForgeConfigSpec.ConfigValue<Integer> COMMAND_PERMISSION_TIMESTOP;
    public static final ForgeConfigSpec.ConfigValue<Integer> COMMAND_PERMISSION_CLIENT_ACTION;
    public static final ForgeConfigSpec.ConfigValue<Integer> COMMAND_PERMISSION_SET_FOV;
    public static final ForgeConfigSpec.ConfigValue<Integer> COMMAND_PERMISSION_SET_ROTATION;
    public static final ForgeConfigSpec.ConfigValue<Integer> COMMAND_PERMISSION_MOTION;
    public static final ForgeConfigSpec.ConfigValue<Integer> COMMAND_PERMISSION_FILL_ENTITY;
    public static final ForgeConfigSpec.ConfigValue<Integer> COMMAND_KICK;
    public static final ForgeConfigSpec.ConfigValue<Integer> COMMAND_POSE;
    public static final ForgeConfigSpec SPEC;  

    static { 
        BUILDER.push("Command Permission");
        COMMAND_PERMISSION = BUILDER
                .comment("/endinglib命令的权限要求")
                .defineInRange("RootCommandPermission", 0, 0, 6);
        COMMAND_PERMISSION_CAMERA = BUILDER
                .comment("/endinglib camera命令的权限要求")
                .defineInRange("CameraPermission", 2, 0, 6);
        COMMAND_PERMISSION_ENDERCHEST = BUILDER
                .comment("/enderchest命令的权限要求")
                .defineInRange("EnderChestPermission", 2, 0, 6);
        COMMAND_PERMISSION_ENDERCHEST_OTHER = BUILDER
                .comment("/enderchest <选择器>命令的权限要求")
                .defineInRange("EnderChestOtherPermission", 3, 0, 6);
        COMMAND_PERMISSION_INV = BUILDER
                .comment("/inv命令的权限要求")
                .defineInRange("InventoryPermission", 2, 0, 6);
        COMMAND_PERMISSION_PERSONAL_RULE = BUILDER
                .comment("/endinglib personal命令的权限要求")
                .defineInRange("PersonalRulePermission", 2, 0, 6);
        COMMAND_PERMISSION_SCHEDULE = BUILDER
                .comment("/endinglib schedule命令的权限要求")
                .defineInRange("SchedulePermission", 2, 0, 6);
        COMMAND_PERMISSION_TIMESTOP = BUILDER
                .comment("/endinglib timestop命令的权限要求")
                .defineInRange("TimestopPermission", 2, 0, 6);
        COMMAND_PERMISSION_SET_FOV = BUILDER
                .comment("/endinglib fov命令的权限要求")
                .defineInRange("FovPermission", 2, 0, 6);
        COMMAND_PERMISSION_CLIENT_ACTION = BUILDER
                .comment("/endinglib action命令的权限要求")
                .defineInRange("ActionPermission", 2, 0, 6);
        COMMAND_PERMISSION_SET_ROTATION = BUILDER
                .comment("/endinglib rotate命令的权限要求")
                .defineInRange("RotatePermission", 2, 0, 6);
        COMMAND_PERMISSION_MOTION = BUILDER
                .comment("/endinglib motion命令的权限要求")
                .defineInRange("MotionPermission", 2, 0, 6);
        COMMAND_PERMISSION_FILL_ENTITY = BUILDER
                .comment("/endinglib fillEntity命令的权限要求")
                .defineInRange("FillEntityPermission", 2, 0, 6);
        COMMAND_KICK = BUILDER
                .comment("/endinglib kick命令的权限要求")
                .defineInRange("KickPermission", 2, 0, 6);
        COMMAND_POSE = BUILDER
                .comment("/endinglib pose命令的权限要求")
                .defineInRange("PosePermission", 2, 0, 6);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    @SubscribeEvent
    static void onLoad(ModConfigEvent event) {
        update();
    }

    public static void update() { 
    }
}
