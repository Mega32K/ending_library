# Resolve shortcut conflicts explicitly

Editing a Shortcut Profile performs immediate conflict detection against active editor commands and the verified Minecraft key mapping. The conflict result identifies each affected command, its current binding, the proposed binding, and the applicable scope. The editor does not silently replace an existing command or claim that a binding is available when the live keymap rejects it.

The user may cancel the proposal, remove the previous editor binding, or explicitly swap two compatible editor bindings. A swap is rejected when either binding belongs to an incompatible scope or is reserved by focused text/numeric editing. Unresolved proposals are not persisted in the Device Preference Store and do not affect the currently active shortcut profile.

Shortcut routing remains scope-aware. Focused text and numeric inputs receive their editing keys first; menus and modal popups receive their navigation and confirmation keys while open; timeline and viewport commands receive eligible bindings only when their Panel Focus is active; global commands are the fallback. A more specific scope wins over a broader scope without requiring duplicate bindings or coordinate-based checks.

Restoring defaults runs the same conflict validation. If a live Minecraft keymap or another editor binding prevents a default, the settings surface reports the exact conflict and leaves the current profile unchanged until the user resolves it. Shortcut changes remain local preference operations: they create no project revision, do not enter personal Undo/Redo, and do not affect collaborators.
