# Keep one command per effect event

Each Command Animation Effect Event stores exactly one command payload and produces exactly one command-dispatch invocation whenever an eligible playback crossing triggers it. Multiple commands that should run at the same authored time are represented as separate stable-identity events and ordered through Same-Time Effect Order. The model does not provide multiline command lists, hidden batches, partial-success semantics, or event-level rollback, keeping validation, failure isolation, collaboration revisions, and Undo/Redo atomic at the event boundary.
