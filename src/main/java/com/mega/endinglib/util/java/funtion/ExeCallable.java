package com.mega.endinglib.util.java.funtion;

@FunctionalInterface
public interface ExeCallable<V> {
    V call(V value);
}
