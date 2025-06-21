package com.mega.endinglib.util;

import com.mega.endinglib.util.java.Exe;
import com.mega.endinglib.util.java.ExeCollection;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class MUtils {
    @SuppressWarnings("StatementWithEmptyBody")
    public static <T> void safelyForEach(final List<T> collection, Exe<T> callable) {
        if (collection.size() == 0) {
        }
        else if (collection.size() == 1) callable.run(collection.get(0));
        else if (collection.size() == 2) {
            callable.run(collection.get(0));
            callable.run(collection.get(1));
        } else for (int i = collection.size() - 1; i >= 0; i--)
            callable.run(collection.get(i));
    }
    @SuppressWarnings("StatementWithEmptyBody")
    public static <T> void safelyForEach(final List<T> collection, ExeCollection<T> callable) {
        if (collection.size() == 0) {
        }
        else if (collection.size() == 1) callable.run(collection.get(0), 0);
        else if (collection.size() == 2) {
            callable.run(collection.get(0), 0);
            callable.run(collection.get(1), 1);
        } else for (int i = collection.size() - 1; i >= 0; i--)
            callable.run(collection.get(i), i);
    }
    public static Vec3 getCenter(Vec3i vec3i) {
        return Vec3.atCenterOf(vec3i);
    }
}
