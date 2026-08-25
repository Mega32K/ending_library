---
status: superseded by ADR-0132
---

# Limit event recipient selectors to particle and sound effects

The earlier effect-event design limited event-level recipient selectors to particle and sound events; Command Animation Effect Events had no independent recipient field because their command payload used normal server command targeting. ADR-0132 removes particle and sound events from the initial editor and runtime, so no event type now uses an event-level recipient selector.
