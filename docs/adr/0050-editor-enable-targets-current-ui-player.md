# Restrict editor animation enable to the current UI player

The Camera Animation Editor's Enable Animation action is a constrained runtime entry point: it starts or replaces a Project Runtime Instance only for the UI Client Player currently operating that editor page. The editor does not expose a target-player selector, does not accept a player collection, and does not fan the action out to other players. This rule is separate from Runtime Publication, which selects the project revision available to runtime, and from server-side integrations that may have their own explicitly defined targeting contract.

The action must not mutate another player's camera, editor workspace, capability state, or runtime instance. Repeated enable for the same UI Client Player follows the existing per-player replacement rule rather than creating duplicate instances.

The Animation Preview Command Group labels this action as Enable Published Animation for Me and pairs it with Stop Enabled Animation for Me so its target and scope remain distinct from project-wide Runtime Publication controls.
