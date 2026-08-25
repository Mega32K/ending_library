# Cap keyframe reduction with large server-authoritative budgets

Bounded Key-Reduction Runs use deliberately generous built-in hard maxima while remaining finite and predictable. One run may contain at most `20,000` deletion candidates, `100,000` source scalar keyframes, `512` affected Additive Lanes, `20,000,000` exact scalar error comparisons, and a `16 MiB` immutable snapshot. Client preview computation has a `10s` deadline and emits at most four progress updates per second.

The client reduction worker permits one active calculation and one replaceable latest pending request; superseded work is cancelled rather than accumulated. The server permits at most two active reduction tasks and four queued tasks, with a `5s` queue-wait deadline, a `15s` active-computation deadline, and a `64 MiB` aggregate cap across every retained active or queued reduction snapshot. Queue count, task limits, and aggregate snapshot memory are independent admission checks, so whichever limit is reached first controls acceptance.

These values are the maximum safety envelope, not minimum guaranteed capacity. A server administrator may lower any effective limit but cannot raise it above the built-in hard maximum. The authoritative effective policy is sent to clients when they enter the editor so local preview rejects known-over-budget work before wasting computation; an integrated server publishes and enforces the same policy as a dedicated server.

Crossing any effective limit rejects the whole preview or confirmation with the localized budget name, actual measured value, effective limit, and scope-reduction guidance. The editor does not split the request, truncate candidates, lower fidelity, keep partial deletions, or retry automatically.
