# Present checkpoint differences summary first

The summary is a read-only state of the Blocking Transaction Surface defined by ADR-0203. It may expose detail and preview, but restore cannot commit until the owner explicitly confirms within that surface.

Checkpoint restoration begins with a bounded, server-derived Checkpoint Difference Summary rather than an opaque confirmation or a default full-document dump. The summary compares the current accepted project state with the selected checkpoint and never includes local drafts, another participant's unsubmitted work, editor workspace state, runtime instances, or transient collaborative-preview state.

The first view presents project-level counts for changed animations, scalar tracks, authored keyframes, curve segments, command events, timeline markers, and animation properties. Users may expand animation and track groups to inspect Checkpoint Difference Detail. Each detail item identifies the affected stable object or logical location and provides concise before-and-after values appropriate to its type: time, scalar value, interpolation or curve data, command text, marker name/time, or property value. The detail surface is read-only and uses bounded virtualization for large projects; it never attempts to render an unbounded JSON or text diff in the modal body.

The summary may offer a secondary raw-data view for exact inspection and a Checkpoint Target Preview action for visual inspection of the selected accepted checkpoint. Both are read-only and client-local. Target preview uses the established isolated editor preview camera and cannot alter the active project, current editing session, collaboration presence, playhead, local drafts, runtime publication, or gameplay camera.

Comparison defaults to the selected checkpoint against the current accepted mainline head. The user may instead select another available checkpoint or retained Revision Commit Node as the comparison target. Both sides are identified by stable server references and validated before detail generation; an unavailable side produces an explicit unavailable state rather than an approximate comparison.

Only after the summary has loaded successfully and the owner explicitly confirms does Checkpoint Restore Preflight proceed. If the current accepted baseline, checkpoint identity, references, locks, or resource state changes while the summary is open, the server rejects the stale proposal and requires a refreshed summary; it never restores from an obsolete comparison or silently applies a partial result.
