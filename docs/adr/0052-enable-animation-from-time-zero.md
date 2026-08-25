# Start editor-enabled animation from time zero

Every Editor Animation Enable action performs a Fresh Runtime Start for the UI Client Player. It creates or replaces that player's Project Runtime Instance at project time zero and does not inherit the editor playhead, the progress of a previously stopped instance, or any other prior runtime position. Repeated enable therefore has deterministic restart semantics rather than behaving differently according to hidden client state.

The initial editor does not provide a runtime resume action. Preview seeking remains an editor-only operation, and an Atomic Runtime Handoff for an already running instance remains the separate case that preserves playback continuity.
