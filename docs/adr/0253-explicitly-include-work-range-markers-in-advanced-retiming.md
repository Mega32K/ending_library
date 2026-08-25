# Explicitly include Work Range markers in advanced retiming

Advanced Retiming in Work Range scope exposes an `Include Markers` option that is enabled by default. When enabled, Project Timeline Markers at or between the Work Range boundaries receive the same offset, scale, or reverse transform as the scoped animation content; Selection scope never implicitly includes markers because Marker Selection remains a separate selection domain.

The Tool Operation Preview lists every affected marker by name with its original and proposed time. Confirmation is blocked if any resulting marker time violates Marker Time Boundary: markers are never silently clamped, removed, or excluded. The marker changes and authored-content changes commit as one server-validated atomic project operation and one personal Undo/Redo entry, while markers remain project metadata and never enter runtime animation JSON.
