# Build the first editor as a server-side real-time collaborative workspace

The first rewritten camera animation editor will be designed around real-time multiplayer collaboration through the server rather than a local-only JSON workflow. The existing `CameraModifyScreen` structure and interaction model may be removed instead of preserved, because retaining it would constrain the new layout, collaboration model, and professional animation-authoring workflow; compatibility remains focused on the camera-animation data capabilities rather than the legacy interface implementation.
