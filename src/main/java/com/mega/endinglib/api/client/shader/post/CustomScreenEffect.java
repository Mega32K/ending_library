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
     * 是否需要当帧的场景深度，默认不捕获。
     * <p>覆写为 true 后，在 shader program JSON 的 samplers 中声明 SceneDepthSampler，
     * 即可读取 {@link #depthCaptureStage()} 指定阶段的原始深度（标准范围为 0 到 1）。
     * 无需在 post chain 的 auxtargets 中声明它；可选的 InvSceneProjMat uniform
     * 为捕获时场景投影矩阵的逆矩阵。默认阶段排除手部，不合并水面等透明图层。
     * <p>仅自动处理且启用的效果会触发捕获。此方法应只返回需求标志，不执行渲染操作；
     * 管理器会在捕获点提前检查 canUse()。错过当帧捕获点的效果从下一帧开始处理，
     * 深度不可用时跳过该效果，不使用上一帧数据。
     */
    default boolean requiresDepthTexture() {
        return false;
    }

    /**
     * 深度捕获阶段；仅 requiresDepthTexture() 为 true 时生效。
     * 两种阶段统一通过 SceneDepthSampler 和 InvSceneProjMat 读取。
     */
    default SceneDepthStage depthCaptureStage() {
        return SceneDepthStage.BEFORE_TRANSLUCENT;
    }

    /**
     * @return 当前特殊屏幕效果的post chain
     */
    default PostChain current() {
        return PostProcessingShaders.postChains.get(this);
    }
}
