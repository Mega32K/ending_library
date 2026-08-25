# Break tangent coupling with Alt-drag

Holding `Alt` when a Bézier control-handle drag begins is an explicit request to edit only the grabbed side. If the boundary is `AUTO`, `ALIGNED`, or `MIRRORED`, the editor first freezes the currently evaluated handle positions, converts the boundary to `BROKEN`, and then applies the pointer delta only to the grabbed handle. If the boundary is already `BROKEN`, `Alt` does not introduce another state change.

The conversion and movement are one transient preview and one atomic authored transaction. They create one revision and one personal Undo/Redo entry, even when the former coupling would otherwise have affected two neighboring Curve Segments. The gesture must not produce a visible jump before the pointer delta is applied.

Cancelling with `Esc`, aborting the pointer gesture, losing the connection, or receiving a server conflict restores the prior Tangent Mode and every handle position. The shortcut is scoped to an active handle drag, so it does not redefine unrelated `Alt` interactions elsewhere in the timeline or editor.