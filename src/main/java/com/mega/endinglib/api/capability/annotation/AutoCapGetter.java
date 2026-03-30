package com.mega.endinglib.api.capability.annotation;

import com.mega.endinglib.api.capability.EntitySyncCapabilityBase;
import net.minecraftforge.common.capabilities.Capability;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.invoke.MethodHandle;

/**
 * <h2>任何获取模组实体能力的方法必须持有此注解</h2><br>
 * <h3>代码示例如下</h3><br>
 *  <blockquote><pre>{@code
 *     @AutoCapGetter(Cap.class)
 *     public static Cap getCap(Entity entity) {
 *         return entity.getCapability(<Capability>).<method>;
 *     }
 *  }</blockquote></pre>
 *  或<br>
 *  <blockquote><pre>{@code
 *     @AutoCapGetter(Cap.class)
 *     public static LazyOptional<Cap> getCap(Entity entity) {
 *         return entity.getCapability(<Capability>);
 *     }
 *  }</blockquote></pre>
 *  两种Getter方法必须满足静态, 有且只有唯一参数Entity<br>
 *  其内部的{@link net.minecraftforge.common.capabilities.ICapabilityProvider#getCapability(Capability)}都将会被动态替换成速度更快的获取方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface AutoCapGetter {
    Class<? extends EntitySyncCapabilityBase> value();
}
