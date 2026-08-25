# Link animation duration to keyframe bounds without implicit truncation

Animation Duration is shared project authoring data and maps to the legacy JSON `duration` field through the existing one-hundred-legacy-units-per-second conversion. Inserting or moving any keyframe beyond the current project end automatically extends the duration to contain that keyframe, so the editor never creates content outside the declared animation length.

Removing a keyframe or moving the last keyframe earlier never automatically shortens the duration. The editor provides explicit duration operations for manual entry, fitting to all keyframes, trimming to the current Work Range, and setting the end to a selected keyframe. A duration edit is rejected when it would place an existing Project Timeline Marker beyond the new end unless the same explicit transaction moves or deletes every affected marker; duration changes never silently clamp or discard markers. These operations are ordinary server-ordered edit operations and therefore participate in revision history and Undo/Redo once that semantic is defined.

If the playback type is `FOREVER`, the project still has a finite Animation Duration that defines its authored content and final value. Runtime playback may continue beyond that endpoint by holding the terminal result; `FOREVER` does not make the editor timeline unbounded. The final value of a track is held between its last keyframe and the project end unless another playback rule explicitly changes it.

Timeline > Animation Duration exposes the explicit manual, fit-to-keyframes, trim-to-Work-Range, and set-end-to-selected-keyframe operations without changing the automatic extension and no-implicit-shortening rules.
