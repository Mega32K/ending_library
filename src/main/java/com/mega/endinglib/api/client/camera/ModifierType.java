package com.mega.endinglib.api.client.camera;

import java.util.function.Function;

public enum ModifierType {
    TRANSLATION_X(ICameraManager::getXOffset0),
    TRANSLATION_Y(ICameraManager::getYOffset0),
    TRANSLATION_Z(ICameraManager::getZOffset0),
    RELATIVE_X(ICameraManager::getXRelative0),
    RELATIVE_Y(ICameraManager::getYRelative0),
    RELATIVE_Z(ICameraManager::getZRelative0),
    ROTATION_X(ICameraManager::getXRot0),
    ROTATION_Y(ICameraManager::getYRot0),
    ROTATION_Z(ICameraManager::getZRot0),
    FOV(ICameraManager::getFovOffset0),
    ZOOM(ICameraManager::getZoomOffset0),
    RAYCAST(ICameraManager::getRaycastOffset0);
    private final Function<ICameraManager, CameraValueInstance> field;

    ModifierType(Function<ICameraManager, CameraValueInstance> field) {
        this.field = field;
    }

    public Function<ICameraManager, CameraValueInstance> getFieldGetter() {
        return field;
    }
}
