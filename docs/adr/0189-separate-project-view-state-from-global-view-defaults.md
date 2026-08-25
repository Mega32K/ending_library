# Separate project view state from global view defaults

Each open project has a player-local Project View State containing its active trajectory, world-space keyframe-node, Gizmo, camera-frustum, guide, target-connection, Preview Stage axis, collaborator-indicator, and other View Display Layer choices together with Viewport Observation State. This state belongs to the Project Workspace Layout and is restored only when the same player reopens that project during the same connected lifecycle. Server/world exit or disconnection clears it together with the rest of the workspace snapshot.

The Device Preference Store contains a separate Default View Layer Profile used only when a project has no current-lifecycle Project View State. It survives game restart, project changes, and server changes. Toggling a layer in an existing project changes only that project's local state and never silently rewrites the global defaults.

The Settings Panel provides an explicit Promote Current View Defaults action. It copies the active project's current layer choices into the Default View Layer Profile for future project openings. The action is unavailable when no editable or inspectable project view exists, explains that reason through the shared tooltip contract, and never alters existing workspace snapshots for other projects.

Project View State, default-layer changes, and promotion remain client-local display behavior. They create no project revision, checkpoint, collaboration operation, personal Undo/Redo entry, runtime publication, or gameplay-camera change, and they never control what overlays another participant sees.
