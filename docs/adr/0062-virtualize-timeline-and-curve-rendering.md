# Virtualize large timeline and curve views

The editor keeps the complete authoritative animation model in memory and renders the timeline and curve editor through Timeline Virtualization. Only tracks, Lanes, keyframes, collaborator previews, and curve geometry intersecting the visible track rows and Visible Curve Window are materialized as visual elements. Scrolling, panning, zooming, selection, playhead movement, and collaboration updates recycle or update visual elements without deleting, merging, reordering, or otherwise changing off-screen data. Logical selection, stable keyframe identity, conflict state, and remote preview state remain available even when their visual representation is outside the current viewport.

Virtualization is a rendering and interaction optimization, not a smaller project format. It must preserve responsive layout behavior and remain compatible with undo/redo, property transactions, trajectory overlays, and server-authoritative collaboration.
