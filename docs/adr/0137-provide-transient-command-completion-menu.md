# Provide a transient command completion menu

The completion list is an Anchored Utility Surface under ADR-0203. It remains scoped to the command field, flips or clamps inside available bounds, and does not commit the surrounding command draft merely by opening or browsing suggestions.

The command field shows a transient completion menu after debounced text changes while it has focus and supports immediate refresh with `Ctrl+Space`. Keyboard navigation selects suggestions, `Tab` or `Enter` inserts the selected suggestion into the local draft, and `Esc` dismisses the menu without discarding the draft. Cursor movement, draft changes, or a changed server context invalidate older results; an empty result does not render an empty menu, and request failure leaves ordinary text editing available without a blocking dialog.
