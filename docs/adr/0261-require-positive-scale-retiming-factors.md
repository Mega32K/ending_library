# Require positive Scale Retiming factors

Scale Retiming accepts only a finite Scale Retiming Factor strictly greater than zero. A factor between zero and one compresses target-time distances around the selected anchor, one produces an unchanged preview whose confirmation is disabled, and a factor above one stretches them. The preview reports the valid factor domain and any narrower effective range imposed by time boundaries, duration limits, finite precision, or server resource policy.

Zero is rejected instead of collapsing every target onto the anchor and creating destructive ambiguous overlaps. Negative values are rejected because reversing time is an explicit Reverse operation with its own curve and Same-Time Effect Order rules rather than a hidden side effect of numeric scaling. Invalid or no-change input remains locally editable but creates no project revision, duration mutation, or personal Undo/Redo entry.
