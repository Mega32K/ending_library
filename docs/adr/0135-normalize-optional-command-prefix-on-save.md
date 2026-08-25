# Normalize the optional command prefix on save

The command editor accepts either a command beginning with `/` or the same command without it. Before validation and shared commit, the editor trims surrounding whitespace, removes one optional leading `/`, and stores the canonical payload without that slash; command-internal whitespace, quoting, selector arguments, and other meaningful characters remain unchanged. Repeated leading slashes are rejected instead of being guessed, so equivalent commands have one stable representation across collaboration, serialization, validation, and Undo/Redo.
