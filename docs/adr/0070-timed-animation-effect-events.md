# Model timed animation effects separately from keyframes

The initial camera animation project supports timed Command Animation Effect Events in addition to scalar keyframe tracks. A command event has an authored time and command payload but is not a numeric keyframe and does not become a translation, rotation, FOV, zoom, or raycast track. In Graph Editor Mode command events render as point events on the command channel; the initial effect model defines no other event type.

The rewritten animation model, JSON definition, server validation, runtime scheduling, and client packet path must represent these events deliberately.

The editor never executes command events during local preview, scrubbing, marker activation, or other non-runtime playback actions. When active runtime playback crosses a command event, the server-authorized runtime path executes the command under the fixed command permission, silent-source, crossing, loop, publication, and failure-isolation rules. Editor-only channel visibility may hide the command row without disabling the runtime event. No alternate effect dispatch, preview-only effect path, or event-level recipient selector is part of the initial implementation or may appear as a hidden or partially functional control.
