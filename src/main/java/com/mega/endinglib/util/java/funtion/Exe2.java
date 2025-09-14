package com.mega.endinglib.util.java.funtion;

@FunctionalInterface
public interface Exe2<T, V> {
    void run(T owner, V value);
}
