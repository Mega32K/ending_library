package com.mega.endinglib.api.client.camera;

import com.mega.endinglib.common.capability.EndingLibraryPlayerCapability;

public interface ICameraManager {
    void oldUpdate();

    void onFreezingMode(EndingLibraryPlayerCapability capability);

    void updateModifier();

    void tick(EndingLibraryPlayerCapability capability);
    CameraValueInstance getX();

    CameraValueInstance getY();

    CameraValueInstance getZ();

    CameraValueInstance getXOffset0();

    CameraValueInstance getYOffset0();

    CameraValueInstance getZOffset0();

    CameraValueInstance getXRelative0();

    CameraValueInstance getYRelative0();

    CameraValueInstance getZRelative0();

    CameraValueInstance getXRot0();

    CameraValueInstance getYRot0();

    CameraValueInstance getZRot0();

    CameraValueInstance getFovOffset0();
    CameraValueInstance getZoomOffset0();
    CameraValueInstance getRaycastOffset0();
    void addXModifier(CameraModifier modifier);

    void addYModifier(CameraModifier modifier);

    void addZModifier(CameraModifier modifier);

    void addRelativeXModifier(CameraModifier modifier);

    void addRelativeYModifier(CameraModifier modifier);

    void addRelativeZModifier(CameraModifier modifier);

    void addTranslationXModifier(CameraModifier modifier);

    void addTranslationYModifier(CameraModifier modifier);

    void addTranslationZModifier(CameraModifier modifier);

    void addRotationXModifier(CameraModifier modifier);

    void addRotationYModifier(CameraModifier modifier);

    void addRotationZModifier(CameraModifier modifier);

    void addFovModifier(CameraModifier modifier);
    void addZoomModifier(CameraModifier modifier);
    void addRaycastModifier(CameraModifier modifier);
    void addPermanentXModifier(CameraModifier modifier);

    void addPermanentYModifier(CameraModifier modifier);

    void addPermanentZModifier(CameraModifier modifier);

    void addPermanentRelativeXModifier(CameraModifier modifier);

    void addPermanentRelativeYModifier(CameraModifier modifier);

    void addPermanentRelativeZModifier(CameraModifier modifier);

    void addPermanentTranslationXModifier(CameraModifier modifier);

    void addPermanentTranslationYModifier(CameraModifier modifier);

    void addPermanentTranslationZModifier(CameraModifier modifier);

    void addPermanentRotationXModifier(CameraModifier modifier);

    void addPermanentRotationYModifier(CameraModifier modifier);

    void addPermanentRotationZModifier(CameraModifier modifier);

    void addPermanentFovModifier(CameraModifier modifier);
    void addPermanentZoomModifier(CameraModifier modifier);
    void addPermanentRaycastModifier(CameraModifier modifier);
    void removeXModifier(CameraModifier modifier);

    void removeYModifier(CameraModifier modifier);

    void removeZModifier(CameraModifier modifier);
    void removeRelativeXModifier(CameraModifier modifier);

    void removeRelativeYModifier(CameraModifier modifier);

    void removeRelativeZModifier(CameraModifier modifier);

    void removeTranslationXModifier(CameraModifier modifier);

    void removeTranslationYModifier(CameraModifier modifier);

    void removeTranslationZModifier(CameraModifier modifier);

    void removeRotationXModifier(CameraModifier modifier);

    void removeRotationYModifier(CameraModifier modifier);

    void removeRotationZModifier(CameraModifier modifier);

    void removeFovModifier(CameraModifier modifier);
    void removeZoomModifier(CameraModifier modifier);
    void removeRaycastModifier(CameraModifier modifier);
    double getX(float partialTicks);

    double getY(float partialTicks);

    double getZ(float partialTicks);
    double getXRelative(float partialTicks);

    double getYRelative(float partialTicks);

    double getZRelative(float partialTicks);

    double getXOffset(float partialTicks);

    double getYOffset(float partialTicks);

    double getZOffset(float partialTicks);

    double getXRotation(float partialTicks);

    double getYRotation(float partialTicks);

    double getZRotation(float partialTicks);

    double getFovOffset(float partialTicks);
    double getZoomOffset(float partialTicks);
    double getRaycastOffset(float partialTicks);
    double getOriginX();
    void setOriginX(double originX);
    double getOriginY();

    void setOriginY(double originY);

    double getOriginZ();

    void setOriginZ(double originZ);

    double getOriginXRot();

    void setOriginXRot(double originXRot);
    void lockOriginXRot(float originX);
    void unlockOriginXRot();
    double getOriginYRot();

    void setOriginYRot(double originYRot);
    void lockOriginYRot(float originY);
    void unlockOriginYRot();

    double getOriginZRot();

    void setOriginZRot(double originZRot);
    void lockOriginZRot(float originZ);
    void unlockOriginZRot();

    float getOriginZoom();

    void setOriginZoom(float originZoom);
}
