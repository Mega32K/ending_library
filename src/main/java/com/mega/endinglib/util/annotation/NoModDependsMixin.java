package com.mega.endinglib.util.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.CLASS)
public @interface NoModDependsMixin {
    String value() default "ending_library";
}
