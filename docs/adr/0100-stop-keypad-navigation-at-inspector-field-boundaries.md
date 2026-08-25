# Stop keypad navigation at inspector field boundaries

Numeric-keypad field navigation in the Curve Segment Inspector does not wrap. At the first field, the keypad-left gesture validates and confirms the current edit but leaves focus on that field. At the last field, the keypad-right gesture performs the same bounded behavior.

The focused field provides a restrained boundary cue so the user can tell that navigation reached the end, but it does not use an exaggerated shake, scale animation, popup, or sound. Invalid input continues to block confirmation and navigation without creating project content. This bounded behavior preserves the user's position during precise curve editing and prevents an accidental jump between unrelated start-boundary and end-boundary values.