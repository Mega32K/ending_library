# Populate the timeline from the current animation only

The timeline population command operates on the currently edited animation and adds every non-empty scalar track and Command Animation Effect Event row to the timeline's logical display set. It does not include other animations, empty tracks, unsupported effect-event types, or rows based only on recent edit history, and it does not duplicate rows that are already present.

Population updates only the Current Edited Animation's Animation Timeline View State. It is a player-local workspace operation that changes neither project content nor playback behavior, creates no project revision, and does not enter project Undo/Redo. Channel visibility filters remain independent: an added row may be logically present while temporarily hidden by the current rotation, position, command, or hide-empty-channel filter. Timeline virtualization controls rendering only and must not change the population result.
