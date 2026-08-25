# Keep gameplay input enabled during runtime camera animation

Editor Animation Enable and ordinary Project Runtime Instances control camera evaluation only by default. They do not implicitly suppress movement, jumping, attacking, interaction, item use, or hotbar selection, so the player continues ordinary gameplay while the published animation affects the camera. Any project that intentionally needs gameplay-input restrictions must express them through an explicit published Runtime Input Policy rather than relying on enable-animation side effects.

This rule does not change Immersive Preview, whose client-local presentation mode deliberately performs Immersive Input Capture under ADR 0044. Runtime input passthrough and immersive preview input capture are separate states and must never be inferred from one another.
