# Default validation to the current animation scope

The Validation Tool Group defaults to the smallest useful scope supplied by the editor context. Opening validation from an animation, track, node, or equivalent focused authoring surface starts a read-only check for that current animation or focused scope. Opening `Tools > Validation` directly also defaults to the current animation when one is active; it never silently starts a whole-project scan.

The validation surface exposes an explicit scope switch for `Current Animation` and `Whole Project`. Changing the scope starts a new bounded read-only run against the current authoritative context and replaces the previous report only when the new run reaches a terminal result. The scope choice changes diagnostics, not project content, playback, selection, runtime state, or personal history. No confirmation dialog is required for either scope.

A Validation Report may focus valid existing animation, track, node, or field identities without editing them. If the current context disappears or the project format cannot support the requested check, the run reports a semantic unavailable result rather than guessing a replacement scope. A completed report remains inspectable through the validation surface and status system until the normal client lifecycle releases it; later project-content changes mark it outdated rather than silently changing its scope or findings.

This makes the common check immediate while keeping whole-project validation deliberate, visible, and bounded.
