# Preserve legacy keyframe groups as additive lanes

The legacy camera animation format stores a map from a group name to a list of scalar keyframes. The current evaluator computes each group independently and adds the group results together. The rewritten editor must preserve that behavior rather than flattening all groups into one timestamp-sorted sequence.

For every `ModifierType` scalar track, the project document therefore stores zero or more named Additive Lanes. Each imported legacy group becomes exactly one lane with the same name, including `default`; each lane retains its own keyframe identities, interpolation segments, visibility, selection, and editor metadata. Lane evaluation happens independently at the same project time, then the lane results are summed to produce the scalar track value.

The editor may present lanes as collapsible children of a scalar track and may provide explicit lane-level muting, soloing, locking, duplication, and selection operations. These are authoring and preview controls, not a change to the legacy-compatible project value. A lane is not merged with another lane because keyframes share timestamps, and deleting a lane is an explicit destructive operation with normal Undo/Redo coverage.

Legacy export reconstructs the `keyframes` map from lane names and lane-local keyframes. The default lane remains the `default` group. If a project feature cannot be represented in the legacy format, export reports a warning and preserves the feature in the native project document instead of silently changing lane evaluation.
