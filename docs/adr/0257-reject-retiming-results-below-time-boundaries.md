# Reject retiming results below time boundaries

Every Advanced Retiming candidate must keep each affected keyframe, Timeline Bézier Node, Command Animation Effect Event, and included Project Timeline Marker within that item type's existing lower time boundary. Project Timeline Markers retain their stricter rule of being later than animation start. A candidate may remain visible as an invalid Tool Operation Preview, but confirmation is disabled and the preview identifies every offending item, its proposed time, and the exact valid range for the entered transform value.

The editor never permits negative authored time, clamps individual items to the boundary, or silently translates the whole result to compensate, because any of those outcomes would change the requested exact Offset or other Retiming transform. Commit-time server validation repeats the boundary check and rejects the complete operation without a project revision, partial mutation, or personal Undo/Redo entry.
