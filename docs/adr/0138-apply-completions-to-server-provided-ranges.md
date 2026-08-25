# Apply completions to server-provided ranges

Each command completion carries the exact text range it is intended to replace. Accepting a suggestion changes only that range in the local draft and preserves all surrounding command text; stale results or ranges that no longer match the draft are discarded. If the server does not provide a valid range, the editor replaces only the current cursor word as a conservative fallback. While the completion menu is open, the first `Enter` accepts the suggestion and a later `Enter` confirms the properties dialog rather than executing a command.
