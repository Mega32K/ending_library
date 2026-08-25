# Provide safe actions for stale overwrite dialogs

The conflict state remains within the active Blocking Transaction Surface and follows its single-surface focus and recovery rules.

When an overwrite confirmation becomes stale because a collaborator changed the target, the conflict dialog offers only `Cancel` and, while the target still exists, `Refresh and confirm again`. Refresh reads the authoritative target state and presents a new comparison while retaining the incoming local edit as an uncommitted proposal; it never commits automatically. If the target was deleted, the dialog closes through `Cancel` and the user must start a new insertion or Auto Key action. No force-overwrite control is exposed, and no stale proposal creates a revision or personal Undo/Redo entry.
