# Separate command editing and runtime feedback

The initial camera editor does not provide a separate test-command button or any local command execution path. Command feedback is divided into three lifecycle layers. While typing, the field may show immediate local diagnostics for empty, incomplete, or obviously malformed text, but these diagnostics are advisory and never imply that a command ran. When the properties action is confirmed, the server performs authoritative syntax and permission validation; rejection keeps the previous valid event unchanged, or discards a new event, while the local draft remains available for correction.

During actual runtime playback, a command event may still fail because of live world or execution conditions. Such a failure is reported through bounded, throttled runtime diagnostics only. It does not reopen the properties popup, interrupt camera playback, create a project revision, alter Undo/Redo, or retry automatically. The runtime source remains silent, and the absence of a diagnostic must never be interpreted as successful command output.

This separation preserves the familiar command-entry experience without making editing destructive or implying that preview typing is execution. It also keeps repeated failures in looping animations from producing unbounded UI state or log history.
