# Make properties popup dismissal explicit

The Animation Properties Popup uses an explicit Property Popup Focus Contract. Opening it focuses the first suitable control, and name fields select their existing text for immediate replacement. `Esc` cancels and rolls back the complete Property Preview Transaction; `Enter` confirms it unless a multiline control owns focus; the visible Confirm and Cancel buttons perform the same actions. Clicking outside the popup does not dismiss it, preventing accidental loss of a local preview transaction. No dismissal path accepts only a subset of changed fields.

The surface restores focus to the invoking control or nearest still-valid target after it closes.
