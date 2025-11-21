package com.mega.endinglib.api.client.shader.post;

import com.mega.endinglib.common.data.DynamicEffectData;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

/**
 * 注册: {@link PostEffectHandler#registerEffect(Supplier)}
 */
public interface CustomScreenEffect {
    /**
     * @return 此特殊屏幕效果的名字
     */
    String getName();
    /**
     * @return 着色器路径
     */

    ResourceLocation getShaderLocation();

    /**
     * 渲染更新方法
     *
     * @param partialTicks 渲染插值
     */

    void onRenderTick(float partialTicks);

    /**
     * @return 启用条件
     */
    boolean canUse();

    /**
     * 注入后处理的位置
     */
    default DynamicEffectData.TransformLayer getTransformLayer() {
        return DynamicEffectData.TransformLayer.LEVEL_RENDERER;
    }
    /**
     * @return 此后处理效果是否由 {@link PostProcessingShaders} 处理渲染
     */
    default boolean autoProcess() {
        return true;
    }

    /**
     * @return 当前特殊屏幕效果的post chain
     */
    default PostChain current() {
        return PostProcessingShaders.postChains.get(this);
    }
}
