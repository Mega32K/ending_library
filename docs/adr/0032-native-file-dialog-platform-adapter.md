# Separate directory reveal from native file and folder selection

Opening an already known directory uses Minecraft 1.20.1''s verified client platform abstraction, such as `Util.getPlatform().openFile(...)`, so Windows, Linux, and macOS delegate to their normal file-manager behavior. This Directory Reveal action does not choose a path and must not be confused with import or export selection.

Open, save, file-selection, and folder-selection commands use a client-only Native File Dialog Adapter. The adapter selects a verified implementation for the current operating system, such as the Windows native folder-selection API on Windows, while keeping platform classes and native loading outside common code and dedicated-server class paths. The exact binding or library must be verified against the implementation dependencies before coding; the design does not assume JNA, JNI, TinyFileDialogs, or another library is already present.

If the native dialog cannot initialize, the editor exposes a clear Dialog Fallback instead of silently selecting a path or blocking the Minecraft render loop. Dialog results are validated before creating import/export operations, cancellation is a normal no-op, and server-owned project files remain protected by the server protocol rather than arbitrary client filesystem access.

Help-menu logs and documentation actions reuse Directory Reveal and never use the file-selection portion of the adapter to browse server-owned project files.
