# Clear selected timeline track data destructively

The Clear Timeline command deletes all authored keyframes, curve segments, and scalar-track data from the currently selected clearable tracks. A multi-track clear is one atomic server-validated project operation, collaboration footprint, journal entry, and personal Undo/Redo unit. Failure or conflict rolls back the complete clear.

The command does not shrink Animation Duration and does not affect unselected tracks, Animation Effect Events, Project Timeline Markers, Animation Properties, or player workspace state. It is visually and semantically distinct from Timeline Channel Visibility and other view-only filters. When no clearable track is selected, the command is disabled and explains why.

Clear Timeline is exposed in the Keyframe Menu's Time and Removal group and remains one grouped operation for every selected clearable track.
