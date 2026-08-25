# Edit marker properties with local drafts

If a Marker Properties Draft is retained through a transient interruption and fails revalidation, it appears in the grouped Reconnect Draft Review Surface under ADR-0207 rather than opening a separate marker dialog or submitting the old baseline.

Marker editing uses the shared Property Modal Surface under ADR-0203, including outside-click protection, responsive scrolling, explicit bottom actions, and focus restoration after close.

Double-clicking a Project Timeline Marker opens a responsive properties popup with the current name selected for immediate replacement. Name and exact-time edits remain in a private Marker Properties Draft: intermediate keystrokes and preview values are local to the editing client, do not acquire a Command Event Personal Edit Lock, and are not broadcast or revisioned. Cancelling, pressing `Esc`, closing the popup, switching projects, closing the editor, or leaving the connected lifecycle discards the draft.

Confirmation submits only the changed marker fields together with the baseline used to edit them. The server may merge a name change with a disjoint concurrent time change, but a same-field conflict or deleted marker rejects the complete commit, returns the latest accepted state for explanation, and leaves the draft available for correction without a force-overwrite path. A successful confirmation creates one authoritative project revision and one personal Undo/Redo item; it preserves the playhead and playback state.
