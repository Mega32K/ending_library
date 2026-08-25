# Keep checkpoint restore notifications session-scoped

Only collaborators who are currently connected and have the affected Animation Project open receive a Session Collaboration Notification for a successful checkpoint restore. The compact non-modal record identifies the restoring player, checkpoint name, and event time. It is retained in a bounded current-session notification list inside the Collaboration Status Area and may be opened as read-only Restore Notification Detail showing the target authoritative revision and the authorized restore summary or history entry.

The notification list is not a project artifact, operation-journal entry, Undo/Redo record, or permanent chat history. It is cleared on Connection Lifecycle Exit and remains player-local; it is not restored through Project Workspace Snapshot or reopened project state. A player who was offline or did not have the project open receives no replayed notification after joining. That player may still inspect any separately authorized checkpoint or project-history information through the normal project surfaces.

A Transient Connection Interruption may retain the already visible bounded list inside the same connected lifecycle, but missed notifications are not replayed as durable history after reconnection. Reconnect Revalidation refreshes only currently authoritative status and available detail links.

The list has a finite, generous capacity and deterministic oldest-first eviction. Duplicate or repeated status records may be coalesced when they describe the same restore event, but the implementation must not retain an unbounded notification object or detail payload. Closing, clearing, or expiring a notification never changes the project, checkpoint, restore history, playback, selection, or runtime camera.
