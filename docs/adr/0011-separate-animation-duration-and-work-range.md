# Separate shared animation duration from each participant's work range

Animation duration is authored project data and remains shared across all participants. Each player may maintain a temporary work range for one project, defaulting to zero through the animation duration. The work range controls that player's preview loop, timeline selection, batch editing scope, and export scope, but cannot silently retime or truncate the project.

The work range is part of the player's project workspace snapshot: it is saved only when the player explicitly closes the editor and is discarded on connection-lifecycle exit. Playback modes remain project authoring data; local preview may apply them within the current player's work range without changing the shared document.

Timeline > Work Range is the primary command group for setting, clearing, resetting, and selecting that local interval; none of those commands silently edits Animation Duration.
