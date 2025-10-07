package com.mega.endinglib.common.data;

import com.mega.endinglib.client.ClientWrapped;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.Map;

public enum InputOperations {
    UNDEFINED("undefined"),
    ALL("all"),
    MOVEMENT("movement"),
    LATERAL_MOVE(MOVEMENT, "movement/lateral_move"),
    MOVE_FORWARD(LATERAL_MOVE, "movement/lateral_move/forward"),
    MOVE_BACKWARD(LATERAL_MOVE, "movement/lateral_move/backward"),
    MOVE_LEFT(LATERAL_MOVE, "movement/lateral_move/left"),
    MOVE_RIGHT(LATERAL_MOVE, "movement/lateral_move/right"),
    JUMP(MOVEMENT, "movement/jump"),
    SNEAK(MOVEMENT, "movement/sneak"),
    ROTATION("rotation"),
    ROTATION_HORIZONTAL(ROTATION, "rotation/horizontal"),
    ROTATION_VERTICAL(ROTATION, "rotation/vertical"),
    MOUSE("mouse"),
    MOUSE_ATTACK(MOUSE, "mouse/attack"),
    MOUSE_USE(MOUSE,"mouse/use"),
    MOUSE_PICK_ITEM(MOUSE,"mouse/pick_item"),
    SMOOTH_CAMERA("smooth_camera"),
    HOTBAR("hotbar"),
    HOTBAR_1(HOTBAR, "hotbar/1"),
    HOTBAR_2(HOTBAR, "hotbar/2"),
    HOTBAR_3(HOTBAR, "hotbar/3"),
    HOTBAR_4(HOTBAR, "hotbar/4"),
    HOTBAR_5(HOTBAR, "hotbar/5"),
    HOTBAR_6(HOTBAR, "hotbar/6"),
    HOTBAR_7(HOTBAR, "hotbar/7"),
    HOTBAR_8(HOTBAR, "hotbar/8"),
    HOTBAR_9(HOTBAR, "hotbar/9"),
    SOCIAL_INTERACTION("social_interaction"),
    INVENTORY("inventory"),
    ADVANCEMENT("advancement"),
    SWAP_HAND("swap_hand"),
    DROP_ITEM("drop_item"),
    CHAT("chat");
    public static final Map<ResourceLocation, InputOperations> NAME_2_OPERATIONS = Util.make(() -> {
        Object2ObjectOpenHashMap<ResourceLocation, InputOperations> map = new Object2ObjectOpenHashMap<>();
        for (InputOperations io : values()) {
            map.put(io.name, io);
        }
        return Collections.unmodifiableMap(map);
    });
    final ResourceLocation name;
    InputOperations parent = null;
    InputOperations(InputOperations parent, String name) {
        this(parent, new ResourceLocation(name));
    }
    InputOperations(String name) {
        this(null, new ResourceLocation(name));
    }
    InputOperations(InputOperations parent, ResourceLocation name) {
        this.parent = parent;
        this.name = name;
    }
    public static InputOperations of(ResourceLocation location) {
        return NAME_2_OPERATIONS.getOrDefault(location, UNDEFINED);
    }

    public ResourceLocation getName() {
        return name;
    }

    public InputOperations getParent() {
        return parent;
    }

    public void operate() {
        ClientWrapped.operateInputAction(this);
    }
}
