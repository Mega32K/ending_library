# Clear the reopen indicator on tab focus

The `Archived Reopen Indicator` is a player-local unread state, not a requirement for reopening. When the player focuses the marked archived tab and the lifecycle explanation is rendered, the client immediately clears the marker and records no content or collaboration operation. `Reopen Project` remains available, and the tab continues to display its persistent lifecycle state so clearing the marker cannot be mistaken for a successful rejoin.

The acknowledgement does not load the current Project Document, create an Editing Session, acquire locks or Presence, apply drafts, alter selection, change playback, or add a personal Undo/Redo entry. If a later authoritative update says that reopening is unavailable, the client replaces the availability state with the applicable archived or deleted outcome regardless of the prior acknowledgement.
