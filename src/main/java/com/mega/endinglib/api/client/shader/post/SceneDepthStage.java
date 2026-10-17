package com.mega.endinglib.api.client.shader.post;

/** 场景深度快照的捕获阶段；各阶段按需捕获，每帧由所有使用者共享。 */
public enum SceneDepthStage {
    /** 保留透明阶段前、排除手部的场景深度；Oculus 使用 depthtex2。 */
    BEFORE_TRANSLUCENT,
    /**
     * PARTICLE_SHEET_OPAQUE批次结束时的场景深度。
     * 包含此前已经写入的其他深度，不隔离粒子，不合并不写深度的透明表面。
     * 无该批次时在粒子渲染结束处捕获；光影包关闭粒子深度写入时无法补全粒子。
     */
    AFTER_OPAQUE_PARTICLES
}
