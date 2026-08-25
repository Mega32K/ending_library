---
status: superseded by ADR-0132
---

# Allow restricted player recipient selectors

The earlier effect-event design allowed an event-level recipient selector rooted at `@p`, with validated selector arguments, in addition to the default target player. ADR-0132 removes the particle and sound event types that required this recipient model, while Command Animation Effect Events keep all targeting inside their command payload. The editor and runtime therefore no longer store, render, or evaluate this event-level selector.
