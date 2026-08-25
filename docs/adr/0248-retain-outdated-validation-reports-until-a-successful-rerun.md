# Retain outdated validation reports until a successful rerun

Any accepted project-content change after a Validation Report's captured state changes the displayed report into an Outdated Validation Report. The editor marks it clearly as `Report Outdated` without exposing internal revision numbers, does not rerun validation automatically, and offers an explicit `Run Again` action; while that cancellable run is pending, the outdated report remains visible.

Only a matching successful terminal run atomically replaces the outdated report. Cancellation, failure, or an unavailable result preserves it, and findings remain readable; a Validation Focus Result stays usable only while its target still exists and can be identified, otherwise its focus action is disabled without deleting the finding. This preserves diagnostic context without misrepresenting it as current or generating background validation churn.
