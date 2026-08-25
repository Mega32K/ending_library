# Persist global editor preferences on the client device

Global Editor Preferences are stored in a validated client-local Device Preference Store. They survive game restart, project changes, server changes, and reopening the Camera Animation Editor. They include only player-local presentation and interaction choices such as the Editor Theme Profile, shortcut preset, default panel arrangement, Default View Layer Profile, display quality, and default snapping or timeline presentation.

The Device Preference Store never contains Project Document content, accepted revisions, checkpoint metadata, personal Undo/Redo history, Project Workspace Layout, collaborator presence, local authoring drafts, collaborative-preview state, or runtime camera execution state. Project Workspace Layout remains player-and-project scoped and is restored only within the same connected lifecycle; connection exit clears it and the shortcut history as already defined.

Preference writes are validated before persistence and use a versioned schema. Missing, malformed, out-of-range, or unsupported values fall back to safe defaults for the affected setting or category without preventing the editor from opening. Category reset and full reset update the local store only, require no server operation, and do not create a project revision or Undo/Redo entry.

Changing servers or opening another project reads the same device preferences, while the project-specific workspace arrangement remains independent. The Settings Menu and Settings Panel must make this scope clear so users do not mistake a local preference change for a shared project edit.
