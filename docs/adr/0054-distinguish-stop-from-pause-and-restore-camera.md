# Treat stop as terminal and pause as temporary

Runtime Stop is not a synonym for pause. A user-requested stop, lifecycle teardown, or equivalent terminal runtime exit ends the UI Client Player's Project Runtime Instance, restores the Camera Restoration Snapshot when it is valid, and otherwise uses the safest verified ordinary player-camera fallback; it does not retain a resumable playback position. Runtime Pause is a non-terminal temporary state that retains the instance and its current playback position, and is used only where an explicit transition requires it, such as the preparation window of Transactional Runtime Replacement.

Stopping does not alter the Project Document, Active Runtime Revision, editor playhead, or published history. It only releases the current runtime execution and restores the local camera presentation state.

The Timeline Menu may expose a visually separated alias of Stop Enabled Animation for Me, but it invokes this same Runtime Stop command and never creates a second local-preview stop, rewind, or pause meaning. The compact Timeline Panel transport cluster therefore uses play or pause plus navigation controls rather than an ambiguous stop button.
