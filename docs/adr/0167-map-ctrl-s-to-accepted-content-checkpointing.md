# Map Ctrl+S to accepted-content checkpointing

`Ctrl+S` is a shortcut for the same Lock-Neutral Save / Checkpoint operation exposed by the File menu. It flushes the project's server-accepted Operation Journal and requests an atomic Project Snapshot; it never acts as a universal confirm key for the currently focused editor control. A focused property popup, command editor, Auto Key proposal, or other local content draft must be confirmed through its own explicit confirmation contract.

The shortcut may run while local drafts or active command-event leases exist. It excludes those drafts from the snapshot without cancelling them, preserves their leases, and keeps the Project Tab Draft Dot visible. It may also run while an operation is awaiting acknowledgement, in which case the Collaboration Status Area reports the bounded pending state and the client does not claim that the pending operation is durably saved.

Successful checkpointing clears no editor workspace state, does not change the playhead or playback, does not publish a runtime revision, and does not alter personal Undo/Redo history. Failure leaves the accepted document and local drafts unchanged and reports the reason through the normal status surface. The File menu and shortcut therefore share one persistence contract rather than presenting two subtly different save behaviors.
