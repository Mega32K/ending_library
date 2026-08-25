# Limit initial redundant-keyframe removal to linear runs

Remove Redundant Keyframes is an explicit optional Optimization operation, not a required or automatic phase of Bake. It may process eligible manually authored content as well as dense post-Bake output, while Bake itself remains Non-Simplifying and commits the exact keyframe set shown in its own preview.

The initial optimization release uses Linear Redundancy Reconstruction only within one Additive Lane. A candidate must be an interior node of a continuous `LINEAR` run; after one or more candidates are provisionally removed, each pair of surviving neighbors reconnects with a `LINEAR` segment. The candidate result is accepted only when comparison of the complete affected interval before and after deletion satisfies the operation's chosen fidelity contract. Numeric-value collinearity alone is insufficient because it does not prove time-domain trajectory equivalence.

Lane endpoints, `CONSTANT` discontinuities, and nodes or segments touching Easing or Bézier interpolation are protected. The tool does not inherit an arbitrary adjacent interpolation mode, convert complex curves to `LINEAR`, or perform hidden Bézier fitting. More advanced Easing or Bézier simplification, if added later, remains a separately designed capability rather than an implicit extension of this conservative operation.
