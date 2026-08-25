# Keep runtime start separate from publication

Publishing a project revision changes the Active Runtime Revision but does not automatically create or start a camera execution instance when none is running. Runtime execution begins only through an explicit runtime-side command, event, or integration call, which makes the chosen active revision available to the requested runtime target. If an instance is already running, publication may atomically replace its revision under ADR 0046; this exception does not turn publication into a general-purpose start command.

This separation prevents an editor action from unexpectedly beginning gameplay camera control while still allowing an explicitly running instance to receive an authorized revision handoff.

Accordingly, Publish Current Accepted Revision in the Animation Menu never substitutes for Enable Published Animation for Me. Publication with no running instance selects the Active Runtime Revision only; the editor's enable action remains an explicit current-player start at time zero.
