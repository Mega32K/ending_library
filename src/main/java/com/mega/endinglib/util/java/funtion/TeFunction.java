package com.mega.endinglib.util.java.funtion;

@FunctionalInterface
public interface TeFunction<T,U,R,M> {
    M apply(T t, U u, R r);
}
