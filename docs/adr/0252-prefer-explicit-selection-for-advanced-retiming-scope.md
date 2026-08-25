# Prefer explicit selection for advanced retiming scope

Advanced Retiming resolves exactly one explicit Retiming Target Scope: `Selection` or `Work Range`. An eligible explicit Selection Set is the default when available; otherwise the active Work Range is the default. When both are available, the tool surface exposes a clear scope selector, and choosing one never silently intersects it with the other or falls back to the whole animation.

Changing the scope selector only regenerates the local Tool Operation Preview. The preview identifies the resolved animation, tracks, authored-item count, and canonical time span before confirmation; if neither scope is available, the command is disabled with a truthful explanation. Commit-time server validation re-resolves the chosen scope and rejects stale or invalid targets atomically rather than widening, substituting, or partially applying the operation.
