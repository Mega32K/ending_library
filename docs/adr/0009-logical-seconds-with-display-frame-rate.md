# Use logical seconds with an independent display frame rate

Project documents use logical seconds as the canonical timeline meaning, advanced from Minecraft game time rather than wall-clock time. Legacy animation timestamps and durations convert at exactly one hundred legacy units per second, preserving the existing behavior in which twenty game ticks advance one hundred animation units. Runtime preview may interpolate with partial ticks for smooth rendering without changing the authoritative time meaning.

The editor may display seconds, game ticks, frames, or timecode. Each project has a display frame rate, defaulting to 20 FPS, that controls ruler labels, frame numbering, and optional frame snapping only; changing it does not retime keys or change playback duration. Snapping can independently target frames, game ticks, keys, and markers, or be disabled.

Timeline > Exact Time Entry accepts whichever display form is active and converts it to canonical Timeline Time, while display format, frame rate, and default snapping configuration remain under Settings > Timeline.
