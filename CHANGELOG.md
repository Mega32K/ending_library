### Fixes & Changes
* Improved entity update handling during time stop.

### Additions
* Added support for smoother rendering of custom time-stop entities.
* Added optional scene-depth sampling for post-processing effects, with vanilla and Oculus shader support. Depth is captured only when an active effect requests it, excludes the player's hand, and is shared between effects each frame.
