package com.mega.endinglib.util.mc.entity;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class EntityAttributesUtil {
    public static boolean addOrReplaceModifier(AttributeInstance instance, AttributeModifier modifier) {
        AttributeModifier srcModifier = instance.getModifier(modifier.getId());
        if (srcModifier == null) {
            instance.addTransientModifier(modifier);
            return true;
        } else if (!srcModifier.getOperation().equals(modifier.getOperation()) || (srcModifier.getAmount() - modifier.getAmount()) < 1.0e-7) {
            instance.removeModifier(srcModifier);
            instance.addTransientModifier(modifier);
            return true;
        }
        return false;
    }

    public static boolean addOrReplacePermanentModifier(AttributeInstance instance, AttributeModifier modifier) {
        AttributeModifier srcModifier = instance.getModifier(modifier.getId());
        if (srcModifier == null) {
            instance.addPermanentModifier(modifier);
            return true;
        } else if (!srcModifier.getOperation().equals(modifier.getOperation()) || (srcModifier.getAmount() - modifier.getAmount()) < 1.0e-7) {
            instance.removePermanentModifier(srcModifier.getId());
            instance.addPermanentModifier(modifier);
            return true;
        }
        return false;
    }
}
