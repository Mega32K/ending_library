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
    public static final ForgeConfigSpec.ConfigValue<Integer> COMMAND_COOLDOWN;
    public static final ForgeConfigSpec.ConfigValue<Integer> COMMAND_INPUT;
    public static final ForgeConfigSpec.ConfigValue<Integer> COMMAND_HOTBAR;
    public static final ForgeConfigSpec.ConfigValue<Integer> COMMAND_TARGET;
    public static final ForgeConfigSpec.ConfigValue<Integer> COMMAND_ANIMATE;
    public static final ForgeConfigSpec.ConfigValue<Integer> COMMAND_TESTFOR;
    public static final ForgeConfigSpec.ConfigValue<Integer> COMMAND_SOUND;
    public static final ForgeConfigSpec.ConfigValue<Integer> COMMAND_SHADER;
    public static final ForgeConfigSpec.ConfigValue<Integer> COMMAND_FREEZE;
    public static final ForgeConfigSpec.ConfigValue<Integer> COMMAND_DATA;
    public static final ForgeConfigSpec.ConfigValue<Integer> COMMAND_MOB_CONTROL;
    public static final ForgeConfigSpec SPEC;  

    static { 
        BUILDER.push("Command Permission");
        BUILDER.comment("定义所有指令所需权限等级");
        BUILDER.comment("若<0则表示禁用了此命令");
        COMMAND_PERMISSION = BUILDER
                .comment("/endinglib命令的权限要求")
                .defineInRange("RootCommandPermission", -1, 0, 6);
        COMMAND_PERMISSION_CAMERA = BUILDER
                .comment("/endinglib camera命令的权限要求")
                .defineInRange("CameraPermission", 2, -1, 6);
        COMMAND_PERMISSION_ENDERCHEST = BUILDER
                .comment("/enderchest命令的权限要求")
                .defineInRange("EnderChestPermission", 2, -1, 6);
        COMMAND_PERMISSION_ENDERCHEST_OTHER = BUILDER
                .comment("/enderchest <选择器>命令的权限要求")
                .defineInRange("EnderChestOtherPermission", 3, -1, 6);
        COMMAND_PERMISSION_INV = BUILDER
                .comment("/inv命令的权限要求")
                .defineInRange("InventoryPermission", 3, -1, 6);
        COMMAND_PERMISSION_PERSONAL_RULE = BUILDER
                .comment("/endinglib personal命令的权限要求")
                .defineInRange("PersonalRulePermission", 2, -1, 6);
        COMMAND_PERMISSION_SCHEDULE = BUILDER
                .comment("/endinglib schedule命令的权限要求")
                .defineInRange("SchedulePermission", 2, -1, 6);
        COMMAND_PERMISSION_TIMESTOP = BUILDER
                .comment("/endinglib timestop命令的权限要求")
                .defineInRange("TimestopPermission", 2, -1, 6);
        COMMAND_PERMISSION_SET_FOV = BUILDER
                .comment("/endinglib fov命令的权限要求")
                .defineInRange("FovPermission", 2, -1, 6);
        COMMAND_PERMISSION_CLIENT_ACTION = BUILDER
                .comment("/endinglib action命令的权限要求")
                .defineInRange("ActionPermission", 2, -1, 6);
        COMMAND_PERMISSION_SET_ROTATION = BUILDER
                .comment("/endinglib rotate命令的权限要求")
                .defineInRange("RotatePermission", 2, -1, 6);
        COMMAND_PERMISSION_MOTION = BUILDER
                .comment("/endinglib motion命令的权限要求")
                .defineInRange("MotionPermission", 2, -1, 6);
        COMMAND_PERMISSION_FILL_ENTITY = BUILDER
                .comment("/endinglib fillEntity命令的权限要求")
                .defineInRange("FillEntityPermission", 2, -1, 6);
        COMMAND_KICK = BUILDER
                .comment("/endinglib kick命令的权限要求")
                .defineInRange("KickPermission", 2, -1, 6);
        COMMAND_POSE = BUILDER
                .comment("/endinglib pose命令的权限要求")
                .defineInRange("PosePermission", 2, -1, 6);
        COMMAND_COOLDOWN = BUILDER
                .comment("/endinglib cooldown命令的权限要求")
                .defineInRange("CooldownPermission", 2, -1, 6);
        COMMAND_INPUT = BUILDER
                .comment("/endinglib input命令的权限要求")
                .defineInRange("InputPermission", 2, -1, 6);
        COMMAND_HOTBAR = BUILDER
                .comment("/endinglib hotbar命令的权限要求")
                .defineInRange("HotbarPermission", 2, -1, 6);
        COMMAND_TARGET = BUILDER
                .comment("/endinglib target命令的权限要求")
                .defineInRange("TargetPermission", 2, -1, 6);
        COMMAND_ANIMATE = BUILDER
                .comment("/endinglib animate命令的权限要求")
                .defineInRange("AnimatePermission", 2, -1, 6);
        COMMAND_TESTFOR = BUILDER
                .comment("/endinglib testfor命令的权限要求")
                .defineInRange("TestforPermission", 2, -1, 6);
        COMMAND_SOUND = BUILDER
                .comment("/endinglib sound命令的权限要求")
                .defineInRange("SoundPermission", 2, -1, 6);
        COMMAND_SHADER = BUILDER
                .comment("/endinglib shader命令的权限要求")
                .defineInRange("ShaderPermission", 2, -1, 6);
        COMMAND_FREEZE = BUILDER
                .comment("/endinglib freeze命令的权限要求")
                .defineInRange("FreezePermission", 2, -1, 6);
        COMMAND_DATA = BUILDER
                .comment("/endinglib data命令的权限要求")
                .defineInRange("DataPermission", 2, -1, 6);
        COMMAND_MOB_CONTROL = BUILDER
                .comment("/endinglib mobControl命令的权限要求")
                .defineInRange("MobControlPermission", 2, -1, 6);
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
