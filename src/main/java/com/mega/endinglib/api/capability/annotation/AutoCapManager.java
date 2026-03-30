package com.mega.endinglib.api.capability.annotation;

import net.minecraftforge.common.capabilities.Capability;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <h2>任何获取模组实体能力的方法的所在类必须持有此注解</h2><br>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface AutoCapManager {
}
