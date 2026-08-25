# Edit animation properties as one local-preview transaction

If this local property draft is retained across a Transient Connection Interruption and becomes stale, it is handled in the grouped Reconnect Draft Review Surface under ADR-0207 before the Property Modal Surface can become editable again.

Editing an Animation Properties Popup updates the current participant's local preview immediately, but does not change the authoritative project, notify collaborators, append an operation journal entry, or create Undo/Redo history for each keystroke. Confirming the popup converts all changed fields into one Property Commit Transaction, one server revision, one journal entry, and one personal Undo/Redo unit. Cancelling restores the pre-open values and produces no project operation. If server validation or collaboration conflict rejects the commit, the entire local preview transaction rolls back; no subset of fields is accepted.

The popup must keep its original values until the transaction ends so rollback is deterministic, and its local preview must not be mistaken for an accepted collaborative revision or a runtime publication.

Opening and closing the popup follow the Property Modal Surface and Overlay Focus Restoration contracts; neither action changes the playhead, runtime camera, or project history by itself.
