# Enable only the published runtime revision

Editor Animation Enable is gated by Runtime Publication. It may start or replace the current UI Client Player's Project Runtime Instance only with the project's current Active Runtime Revision, which must be valid and successfully published. Unpublished Project Changes remain editable and previewable in the editor but cannot enter gameplay runtime through the enable action. When newer project content exists, the editor visibly reports that the runtime is behind the editor; enabling still uses the last valid Active Runtime Revision. If the project has no active published revision, the enable action is unavailable and the user must publish one first.

This keeps collaborative authoring, local preview, and gameplay runtime separate and prevents an incomplete or merely client-visible edit from changing the current player's camera unexpectedly.

The Animation Menu preserves that separation visually: Preview Current Animation may use editor working content, while Enable Published Animation for Me remains disabled without a valid Active Runtime Revision and never enables unpublished changes.
