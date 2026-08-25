# Let owners and editors edit checkpoint metadata

Both Project Owners and Project Editors may edit the user-facing name and description of any Named Project Checkpoint available to the project. This does not introduce a checkpoint-creator permission tier: the creator remains visible as historical metadata, but does not gain exclusive control over later naming changes.

Checkpoint metadata changes are server-ordered operations over the label metadata version. They do not change the immutable target Revision Commit Node, create a project-content revision, alter playback or runtime state, or enter personal Undo/Redo. If two participants edit the same checkpoint metadata concurrently, the first valid operation accepted by the server succeeds; a later stale operation is rejected with the current metadata and a visible conflict explanation rather than being merged or silently overwriting the accepted value.

Only the Project Owner may pin, unpin, delete, or restore a checkpoint. Keeping those lifecycle actions owner-only preserves the established two-role permission model while allowing Editors to organize the shared checkpoint list. Disabled owner-only controls follow the common explanatory Button Tooltip contract.
