# Preserve animation-type-specific natural completion

Natural Animation Completion remains distinct from user-requested Runtime Stop. `STOP` reaches the authored end, stops advancing, and holds the terminal camera result while its runtime instance remains present. `LOOP` returns to time zero and continues. `FOREVER` reaches the authored end and continues holding the terminal result as an active unbounded state. `STOP_BACK_TO_ZERO` reaches the authored end, resets the animation time and contribution to zero, and stops that animation only; it does not destroy the Project Runtime Instance and does not restore the Camera Restoration Snapshot.

An explicit Stop Animation action still performs Runtime Stop for every `AnimType`, terminating the instance and restoring the pre-enable camera state. The editor and runtime status model must therefore distinguish a completed animation, a terminal hold, a zero-value completion, and a terminated runtime instance.
