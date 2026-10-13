package com.mega.endinglib.api.client.shader.post;

import com.mega.endinglib.mixin.accessor.oculus.AccessorIrisRenderingPipeline;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.api.v0.IrisApi;
import net.irisshaders.iris.pipeline.IrisRenderingPipeline;
import net.irisshaders.iris.pipeline.WorldRenderingPipeline;
import net.irisshaders.iris.uniforms.CapturedRenderingState;

/** 仅在已加载 Oculus 且开启光影后调用，避免通用路径加载可选依赖的类型。 */
final class OculusSceneDepth {
    private OculusSceneDepth() {
    }

    static void capture(SceneDepthCapture target) {
        if (IrisApi.getInstance().isRenderingShadowPass()) return;
        WorldRenderingPipeline pipeline = Iris.getPipelineManager().getPipelineNullable();
        if (pipeline instanceof IrisRenderingPipeline) {
            int texture = ((AccessorIrisRenderingPipeline) pipeline).endingLibrary$getRenderTargets()
                    .getDepthTextureNoHand().getTextureId();
            target.capture(texture, CapturedRenderingState.INSTANCE.getGbufferProjection());
        }
    }
}
