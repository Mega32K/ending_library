package com.mega.endinglib.util.java;

import java.util.List;

public class MUtils {
    @SuppressWarnings("StatementWithEmptyBody")
    public static <T> void safelyForEach(final List<T> collection, Exe<T> callable) {
        if (collection.size() == 0) {
        } else if (collection.size() == 1) callable.run(collection.get(0));
        else if (collection.size() == 2) {
            callable.run(collection.get(0));
            callable.run(collection.get(1));
        } else for (int i = collection.size() - 1; i >= 0; i--)
            callable.run(collection.get(i));
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public static <T> void safelyForEach(final List<T> collection, ExeCollection<T> callable) {
        if (collection.size() == 0) {
        } else if (collection.size() == 1) callable.run(collection.get(0), 0);
        else if (collection.size() == 2) {
            callable.run(collection.get(0), 0);
            callable.run(collection.get(1), 1);
        } else for (int i = collection.size() - 1; i >= 0; i--)
            callable.run(collection.get(i), i);
    }
}
