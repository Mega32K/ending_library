# Let a new archive outcome revoke reopen availability

Reopen availability is a reversible lifecycle state, not a permission grant or a reservation. If an archived project is restored and then archived again before the player invokes `Reopen Project`, the later authoritative archive outcome supersedes the earlier availability notification. The client disables `Reopen Project`, clears the `Archived Reopen Indicator`, and keeps the tab in `Archived Inspection State` without creating a session, loading new content, or showing a blocking confirmation.

The tab retains the last authorized inspection document already held by the client and continues to permit only the inspection operations allowed by that state, including isolated local preview according to its existing contract. No old availability acknowledgement, delayed notification, or stale focus revalidation may re-enable the action. A later restoration creates a new availability transition and may produce a new indicator; a deletion is terminal and changes the tab to `Deleted Project Notice` without exposing project content.

This rule makes lifecycle state authoritative over UI availability and prevents a stale restore notification from becoming a route into an archived project.
