# Keep timeline rows visually clean

The camera editor timeline keeps its track rows minimal in all states, including hover and selection. Hovering or selecting a row must not reveal inline action buttons or expand the row's visual footprint; selection is communicated through semantic highlighting, linked keyframe/curve emphasis, and focus treatment only. Track operations are exposed through the global toolbar, keyboard and focus-aware commands, row context menus, and the property inspector, with responsive overflow behavior that preserves the clean timeline body.

When a suitable small icon is missing from the existing icon set, the implementation may use a GPT image-generation tool to create a dedicated asset, but generated icons must be reviewed, reduced to the target size, made visually consistent with the Blockbench-inspired dark UI, and kept semantically distinct from status colors. Decorative generated art must not replace a real interaction affordance or conceal missing behavior.

The supplied Blockbench timeline screenshot guides panel proportions, track-tree hierarchy, ruler, lane spacing, scrolling, and transport placement, but its persistent row-local visibility and add buttons are deliberately omitted. Written interaction requirements take precedence over the reference image.
