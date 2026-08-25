# Keep invalid command text out of shared projects

The command properties popup may hold empty, incomplete, or invalid text as a local draft while the user edits, but shared project content may contain only a command that passes authoritative validation. Rejecting an edit preserves the previous valid event, while cancelling a new event discards its draft without creating a project revision, collaboration change, or personal Undo/Redo item. This keeps invalid executable content out of the shared animation while preserving a practical typing workflow.
