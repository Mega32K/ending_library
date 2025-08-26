package com.mega.endinglib.util.java.funtion;

@FunctionalInterface
public interface ExeCollection<V> {
    void run(V v, int index);
}
