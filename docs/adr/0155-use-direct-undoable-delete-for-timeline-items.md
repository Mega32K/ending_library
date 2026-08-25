# Use direct undoable delete for timeline items

Deleting selected authored timeline items is an ordinary reversible editor operation, not a confirmation-gated destructive project operation. The client submits the complete selected target set to the server; a successful transaction removes all targets together, creates one authoritative revision and one Personal Edit History item, preserves the playhead and timeline focus, and clears selection only for items that no longer exist. A missing, stale, unauthorized, differently locked, or locally drafted Command Animation Effect Event rejects the whole request without partial deletion or silent skipping.

The Delete interaction is separate from clearing an entire track, deleting an animation or project, replacing imported content, restoring a checkpoint, or other operations that can destroy broad project state and therefore retain explicit confirmation and destructive preflight. Ordinary deletion never implicitly shortens Animation Duration and never releases another participant's lock.

Edit Menu Delete, its shortcut, and applicable context-menu actions invoke this same transaction. Atomic Cut Intent creates its clipboard payload before requesting this deletion; a rejected delete keeps the payload and leaves project content unchanged.
