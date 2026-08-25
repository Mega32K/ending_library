# Bound the structured editor clipboard lifecycle

The Camera Animation Editor keeps its Structured Editor Clipboard in bounded client memory and makes it available across Project Tabs only during the same connected editor lifecycle. It is cleared on world/server exit and disconnection, is never written to project files, checkpoints, the Device Preference Store, or a server queue, and does not survive a new connection.

Editor copy, cut, paste, and duplicate use the typed editor payload rather than the operating-system clipboard. Text and numeric fields continue to use the normal system clipboard. Structured project content and command-event data are never placed into that external clipboard automatically. An explicit Copy as JSON command may serialize supported accepted content for external text use, but it is a separate command with its own bounded output and never becomes the editor's typed paste payload.

The client enforces a finite Clipboard Resource Budget for payload byte size, item count, nesting depth, and command-event text. A copy or cut that exceeds the budget fails with a specific reason and leaves the previous valid payload unchanged. A successful replacement releases the previous payload before retaining the new bounded payload; there is no unbounded clipboard history.

Pasting into the same or another Project Tab creates a local Paste Preview Draft. Cross-project targets require full Cross-Project Paste Validation: project-format compatibility, target animation and track mapping, relative timing, required structure, resource limits, command-event permissions and personal locks, and overwrite conflicts are checked again against the target's current authoritative baseline. Source stable identities, source locks, source permissions, and unsubmitted drafts are never transferred. Only an explicit confirmation can submit one atomic target-project edit, and any rejection leaves the target unchanged.

Clearing the clipboard is a local memory operation. It creates no project revision, personal Undo/Redo entry, collaboration operation, checkpoint, or runtime change.
