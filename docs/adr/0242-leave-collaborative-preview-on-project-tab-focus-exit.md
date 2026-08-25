# Leave collaborative preview when the joined project tab loses focus

Collaborative Preview participation is tied to the active Project Tab, not to the editor screen or the player's entire connection. Moving focus between ordinary panels, menus, the viewport, the timeline, and the inspector does not leave the preview. Switching to another Project Tab or closing the joined tab does.

When a non-leader joined tab loses active-tab focus, the client performs Leave Collaborative Preview and restores the valid Local Preview Snapshot. The server releases that participant's bounded preview membership, synchronization subscription, and presence for the shared preview; the tab remains an ordinary local editor container and does not continue as a hidden background follower. Returning to the tab does not auto-join or auto-resume shared playback; the player must invoke the explicit Join action again.

When the Preview Leader's joined tab loses active-tab focus, the server treats it as the leader leaving the active editing/session lifecycle and performs Collaborative Preview Termination. Leadership is not transferred, the shared preview does not remain ownerless, and every remaining participant receives Preview Restoration. If focus changes before Join reaches atomic application, the pending request follows the earlier cancellable-join rule instead.

This keeps active-tab participation visible and bounded without making ordinary editor navigation disruptive.