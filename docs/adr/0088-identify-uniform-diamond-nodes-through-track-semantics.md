# Identify uniform diamond nodes through track semantics

All timeline items use the same compact diamond node, including scalar keyframes, command events, and camera-parameter keyframes. The node silhouette, hover state, and ordinary selection rendering do not encode the item's semantic type.

Type identification is provided by the track-tree path and label, the selected item's property inspector, context-menu wording, keyboard-focus text, and accessible name. The inspector and operation routes remain type-aware: they expose the fields and actions appropriate to the underlying item even though the node artwork remains uniform. Track visibility, selection, and editing behavior therefore stay visually consistent without reducing semantic clarity or relying on color alone.

The type metadata remains part of the project document and runtime data model. It is never inferred from a node's color, shape, or pointer state. Any generated icon may support surrounding menus or inspector controls, but it must not be required to distinguish one timeline node type from another.
