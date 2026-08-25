# Review stale reconnect drafts in one surface

Permission loss is a separate terminal branch. When the player has neither Owner nor Editor access, ADR-0208 replaces authoritative-value comparison with a permission-revoked explanation and permits only discard or bounded local retention of draft material.

Project archive or deletion is an earlier terminal branch under ADR-0215. A project lifecycle outcome found during Reconnect Revalidation removes that project's retained drafts and transitions its tab before this review surface can be considered; the three ordinary draft-resolution choices do not apply to archived or deleted projects.

When Reconnect Revalidation finds one or more stale or conflicting Retained Reconnect Drafts, the Camera Animation Editor opens one bounded Reconnect Draft Review Surface rather than one dialog per draft. The surface is a read-only recovery state under the existing Blocking Transaction Surface rules: it owns the relevant input, remains responsive and scrollable, and prevents normal authoring from resuming until every listed draft has an explicit resolution.

Each review item identifies the project tab and animation, the track/event/field affected, the retained local value or concise draft summary, the current authoritative value, the baseline or revision that diverged, and a truthful reason such as an overlapping edit, deleted identity, changed permission, unavailable lock, or validation change. The surface never displays another participant's uncommitted draft or raw command output beyond the bounded values needed to explain the conflict.

Every item offers the same three `Reconnect Draft Resolution` choices:

1. `Refresh Authoritative Value`: discard that retained local draft and use the current server-accepted value.
2. `Keep as New Re-edit Draft`: preserve the retained value as a fresh client-local draft against the current authoritative baseline; it is not submitted and does not claim the old lock or identity until the user deliberately edits and confirms it.
3. `Discard Draft`: remove the retained local draft and return to the current authoritative state without preserving its values for automatic reuse.

The editor remains read-only until all review items are resolved and the user confirms the resolution set. Applying resolutions changes only client-local draft and view state; it creates no project revision, journal entry, checkpoint, publication, or personal Undo/Redo item. A kept re-edit draft may then reopen the appropriate ordinary Property Modal Surface or command-event editing flow, subject to fresh permission, identity, validation, and lock acquisition. No resolution automatically submits, force-overwrites, merges, renews a lease, or reopens a stale operation.

The review surface is finite and virtualized when necessary, uses one grouped recovery flow instead of a prompt storm, and exposes bounded failure and retry behavior if current authoritative details cannot be loaded. Cancelling the review leaves the editor read-only until the user resolves it or reaches Connection Lifecycle Exit; terminal lifecycle cleanup discards all remaining retained drafts under ADR-0206. Compatible drafts that pass Reconnect Revalidation do not appear in this conflict surface and may resume through their ordinary local-draft contract.

The same review gate applies after `Explicit Permission Rejoin` succeeds. If any Permission-Revoked Draft Material remains, the rejoined Project Tab immediately opens one Reconnect Draft Review Surface against the freshly loaded authoritative Project Document and stays read-only until every item is resolved and the resolution set is confirmed. The permission-revoked material is never auto-applied merely because access returned. If no retained material exists, the new Editing Session enters ordinary editable state immediately.
