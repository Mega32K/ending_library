# Capture and suppress gameplay input during immersive preview

Immersive Preview is a client-local presentation mode in which the editor temporarily owns all relevant input routing. Mouse input controls only the preview camera and editor shortcuts remain available, while movement, jumping, attacking, interaction, item use, and hotbar switching are suppressed without moving or immobilizing the player entity; `Esc` exits Immersive Preview first. Losing window focus pauses the preview and releases or safely restores mouse capture, and focus or exit handling must restore the prior input state. This keeps the mode visually immersive without allowing editing gestures to leak into gameplay or requiring a server-side player freeze.

## Consequences

- The input layer must distinguish editor commands, preview-camera controls, and gameplay actions before dispatch.
- Input suppression is local presentation state, not a project revision, capability mutation, or server gameplay command.
- Every exit and focus-loss path must be idempotent and cooperate with the existing Camera Restoration Snapshot.
