# Use a non-blocking runtime diagnostic panel

Runtime command failures are surfaced through the bottom Editor Status Bar rather than through modal dialogs or player-facing chat. Its compact diagnostic badge displays a failure indicator and aggregate count. Opening the badge uses the shared Status Detail Surface to show the Runtime Diagnostic Panel: a bounded list of the current session's coalesced diagnostics, with the event identity, authored time, failure category, latest occurrence, and aggregate count when available. The top Workspace Bar Status Entry may summarize a diagnostic only when selected by the shared Status Priority State and never owns a second diagnostic collection.

Selecting a diagnostic is a navigation aid only. If its event identity still exists in the current project, the editor selects that event and focuses the corresponding timeline row; it does not move the playhead, pause or resume playback, change the viewport, edit the event, or create an Undo/Redo entry. If the event was deleted or replaced, the record remains readable but is marked unavailable rather than resolving to a different event.

The clear action removes only the current session's visible diagnostic state. It does not affect command execution, project revisions, collaboration history, runtime state, or server-side bounded accounting. The panel follows the responsive workspace rules: it may remain inline when space permits and become a drawer or popover when the available layout is narrow, without forcing the timeline or viewport into an unusable size.

Help > Support opens this same panel and may offer an explicit privacy-filtered Support Summary action; opening Help or copying that summary does not change the diagnostic state, playback, selection, or project history.
