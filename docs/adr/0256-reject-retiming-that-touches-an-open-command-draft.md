# Reject retiming that touches an open command draft

A mutating Advanced Retiming operation is invalid when its resolved footprint contains any Command Animation Effect Event with an active Command Event Personal Edit Lock, including a lock held by the player requesting Retiming. Retiming changes the event's authored time, placement, or Same-Time Effect Order assumptions, so silently rebasing an open draft would make its confirmation meaning ambiguous.

The Tool Operation Preview identifies the locked event and holder, and commit-time validation rejects the complete operation if a relevant lock appears after preview creation. Retiming never skips the event, steals or drains the lock, waits in a queue, or commits the remaining targets; the holder must first confirm or cancel the event edit, after which the requester creates a fresh preview against the current authoritative project.
