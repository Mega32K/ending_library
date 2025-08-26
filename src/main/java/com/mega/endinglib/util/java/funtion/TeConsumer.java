package com.mega.endinglib.util.java.funtion;

@FunctionalInterface
public interface TeConsumer<T, U, M> {
    void accept(T t, U u, M m);
}
