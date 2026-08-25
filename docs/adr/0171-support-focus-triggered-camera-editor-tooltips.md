# Support focus-triggered camera editor tooltips

Camera Editor Button Tooltip behavior is input-method neutral. Mouse users receive a tooltip after remaining over one button for more than one second; keyboard and controller users receive the same tooltip after the same button retains navigation focus for more than one second. The tooltip text, disabled-state explanation, responsive placement, and cancellation behavior are shared rather than maintained as separate input-specific descriptions.

Focus navigation must expose the same meaningful button targets as pointer navigation without making decorative elements or timeline nodes part of the button focus order. Moving focus to another button cancels the previous delay and starts a fresh delay for the new target. Losing focus to a text field, menu, popup, viewport, or closed surface cancels the pending or visible tooltip unless that destination has its own separately defined tooltip contract.

The focus-triggered tooltip remains client-local and passive. It does not activate the button, alter selection, steal keyboard focus, capture controller input, or change the current editor panel. A disabled button may explain its purpose and blocking reason through focus, but remains non-activatable. Tooltip rendering is never persisted, synchronized, journaled, or included in Undo/Redo.

Tooltips remain concise action explanations; Help > Contextual Help is the route for opening the fuller Camera Wiki reference without changing the focused control or performing its action.
