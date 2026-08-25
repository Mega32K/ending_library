# Preserve core regions through ordered responsive degradation

The responsive Camera Animation Editor uses one deterministic Responsive Degradation Order whenever the current logical bounds cannot keep every visible region at its bounded minimum. The central Live World Viewport and bottom Timeline/Curve Editor are the Core Editor Region and remain reachable first; the shell never solves an overflow by shrinking them, their hit targets, or their world-space overlays into an unusable layout.

After preserving the Core Editor Region, the shell applies substitutions in this order:

1. Convert the right Contextual Property Inspector into a responsive drawer or temporary panel tab.
2. Convert the left Animation Resource Browser and its navigation surface into a real drawer or Panel Tab Group while retaining the current animation and local navigation state.
3. Compress collaboration and status details into semantic icon entry points; full read-only details remain available through the Status Detail Surface.
4. Move lower-frequency top-toolbar commands into one accessible overflow menu while keeping File, Settings, and other commands reachable.

The order is a layout policy, not permission to discard or silently close state. Every substitution preserves panel search, selection, scroll, focus, and restoration metadata; the current animation, playhead, playback state, project tab, editing session, and runtime camera remain unchanged. The View menu and established toolbar or context-menu routes remain valid even when a surface is no longer continuously visible. The shell may apply a bounded density profile within its declared minimum and maximum values, but it never uses exaggerated global scaling as a substitute for the ordered substitutions.

Responsive degradation is client-local Project Workspace Layout behavior. It produces no project revision, collaboration operation, checkpoint, runtime change, or personal Undo/Redo item. When bounds recover, the shell restores the nearest valid prior placement without forcing a hidden panel into view or changing the user's editing context. A layout state that cannot be restored exactly reports the substitution and retains all reachable content and controls.
