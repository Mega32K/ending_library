# Create markers immediately at the playhead

The Set Marker toolbar action is a one-step server-authoritative creation command. When the playhead is within the valid nonzero marker boundary, the server validates authorization, expected project revision, the per-animation Marker Resource Budget of `4096` markers by default, and time, then assigns a stable marker identity and the next generated default name and commits one project revision and one personal Undo/Redo item. The accepted marker becomes the local Marker Selection without moving the playhead, opening properties, or changing playback state; users may double-click later for precise naming or timing.

No provisional marker, local creation draft, empty shared record, or placeholder revision exists before acceptance. At time zero the control is disabled with an explanatory tooltip. Rejection leaves project and selection state unchanged and reports a bounded reason through the normal editor status surface. Multiple accepted markers may occupy the same time and remain distinct by stable identity. Every other marker-creating operation uses the same finite budget and rejects a complete over-limit request without partial creation.

Set Marker remains the final action in the Timeline Panel's ordered left tool cluster and is also available from Timeline > Markers without changing its one-step creation behavior.
