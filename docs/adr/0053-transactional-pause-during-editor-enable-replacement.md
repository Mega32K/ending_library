# Pause only during editor-enable replacement

When Editor Animation Enable would replace an existing Project Runtime Instance for the UI Client Player, the current instance enters a Transactional Runtime Replacement window. It pauses at its current playback position while the new Active Runtime Revision is validated and prepared. If preparation succeeds, the server performs the replacement atomically, discards the old instance, and starts the new instance from time zero. If preparation fails, the old instance resumes from the exact position at which it was paused and remains active. The pause is not persisted, exposed as a resumable project instance, or added to any unbounded queue.

This keeps replacement visually stable without allowing a failed enable attempt to reset or destroy a working runtime camera.
