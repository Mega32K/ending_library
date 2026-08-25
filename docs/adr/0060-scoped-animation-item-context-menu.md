# Keep animation item context menus scoped

Each animation item exposes a compact context menu styled consistently with the supplied Blockbench reference. The menu directly provides Properties, Rename, Duplicate, Delete, Enable Published Animation for Me, Stop Enabled Animation for Me, and Preview Current Animation when their state allows it. High-impact or project-wide actions such as Runtime Publication, import/export, recovery, checkpoint restoration, project close/archive, and editor preferences remain in the top-level Animation, File, Settings, or dedicated review surfaces rather than being hidden behind an animation item.

The context menu must derive its enabled, disabled, and status states from the current selection, permission, active revision, runtime instance, and collaboration state. It must not silently publish content or bypass the same server validation, confirmation, transaction, and Undo/Redo rules used by the primary command surfaces.
