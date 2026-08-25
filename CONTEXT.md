# Camera Animation Authoring

This context defines the language used for creating, collaboratively editing, previewing, and publishing EndingLibrary camera animations.

## Language

**Camera Animation Editor**:
The in-game professional workspace in which authorized participants create, inspect, preview, and collaboratively edit camera animations in real time.
_Avoid_: Camera bench, camera modify screen

**Real-time Collaboration**:
A mode in which accepted animation changes become visible to every participant in the same editing activity without exchanging files or manually reloading resources.
_Avoid_: Shared preview, synchronized screen

**Editing Session**:
The server-owned collaborative activity for one camera-animation project, including its participants, accepted revision, and ordered edit history.
_Avoid_: Room, shared screen

**Collaboration Resource Budget**:
A server-authoritative set of independent finite limits for participants, transient collaboration state, edit transactions, project structure, and pending persistence work; values are intentionally generous for the target workload but never unbounded.
_Avoid_: Client preference, single shared counter

**Generous Configured Limit**:
A deliberately high operational limit chosen for substantial animations and multiple editors, still enforced before resource allocation and adjustable through server policy rather than client claims.
_Avoid_: Unlimited capacity, arbitrary truncation

**Animation Project**:
A server-world-owned camera-animation document that can be reopened, shared, versioned, previewed, exported, and used independently of any one player's capability.
_Avoid_: Player animation, capability animation

**Project Catalog**:
The world-persisted index of animation projects and their ownership, access, lifecycle, and revision metadata.
_Avoid_: Project document, animation file

**Project Document**:
The independently persisted content and editor metadata for one animation project, loaded and saved separately from the project catalog.
_Avoid_: Catalog entry, player capability

**Project Participant**:
A player currently authorized to view or edit an animation project; participation does not transfer project ownership.
_Avoid_: Project owner

**Project Owner**:
The single participant who controls an animation project's lifecycle and editor membership while retaining full editing access.
_Avoid_: Server administrator, project participant

**Project Editor**:
A participant authorized by the project owner to open, edit, preview, import, and export an animation project without controlling its ownership or editor membership.
_Avoid_: Read-only participant, project owner

**Permission-Revoked Inspection State**:
The frozen client-local project-tab state entered when authoritative permission revalidation confirms that the player holds neither Project Owner nor Project Editor access. It retains only the Last Authorized Inspection View, leaves the Editing Session, receives no later project or presence subscription, disables every project-authorized action, and remains visibly distinct from an active read-only format tab.
_Avoid_: Project Editor downgrade, live project subscription, offline editable copy

**Last Authorized Inspection View**:
The clearly marked, potentially stale project content already held by the client at the moment its last valid Owner or Editor access ended. It may be navigated and locally previewed inside Permission-Revoked Inspection State, but it is not refreshed, exported as current authoritative content, submitted, published, saved, or treated as proof of continuing access.
_Avoid_: Current authoritative revision, Project Snapshot, cached editing session

**Permission-Revoked Draft Material**:
A Retained Reconnect Draft whose project permission was revoked. It may be discarded or kept only as bounded client-local reference material inside the current connected lifecycle; it cannot reopen project editing, acquire a lock, submit, export, or survive Connection Lifecycle Exit, and can become a new edit only after a later explicit authorized project opening and ordinary validation.
_Avoid_: Authorized draft, durable offline file, automatic permission recovery

**Explicit Permission Rejoin**:
A player-initiated action after permission restoration that freshly authorizes and opens the current authoritative project before creating a new Editing Session. On success it reuses the frozen Project Tab in place, atomically replaces its stale view with the current authoritative document, and leaves Permission-Revoked Draft Material unapplied; it never revives the old session or released resources.
_Avoid_: Automatic permission recovery, duplicate replacement tab, lock renewal

**Atomic Rejoin Baseline**:
The single stable authoritative project revision that binds permission validation, project validation, the loaded document, and the new Editing Session during Explicit Permission Rejoin. No editing, lock acquisition, Presence broadcast, or retained-draft application is allowed before this baseline is established.
_Avoid_: Frozen client baseline, partial snapshot, optimistic rejoin merge

**Runtime Publication**:
An explicit server-authorized action by either the Project Owner or a Project Editor that makes a selected valid project revision the active revision used by the camera runtime, or replaces/stops the currently active runtime application as requested.
_Avoid_: Save, checkpoint, editor preview, export-only operation

**Runtime Publication Record**:
The finite server-retained audit entry for a publish, active-revision replacement, or project-runtime stop, containing the actor, event time, selected accepted revision, operation type, and outcome. It is inspectable through the project history surface but is not a Project Document revision, Personal Edit History item, command-input history, or permanent unbounded log.
_Avoid_: Edit Operation, Undo/Redo entry, runtime diagnostic spam

**Active Runtime Revision**:
The project revision currently selected by Runtime Publication for gameplay-facing camera execution; it remains fixed until another authorized publication replaces it or an authorized stop ends the runtime application.
_Avoid_: Current editor revision, collaborative preview revision

**Atomic Runtime Handoff**:
The verified transition in which a newly published project revision replaces the Active Runtime Revision as one server-controlled runtime step, leaving the previous revision active until the new revision is ready and retaining the current playback state by default.
_Avoid_: Partial reload, live field patch, editor preview switch

**Runtime Playback Continuity**:
The default publication rule that preserves the active runtime target, playback time, loop state, and equivalent execution context when an Atomic Runtime Handoff succeeds, while a publication with no running instance establishes the revision for immediate runtime use.
_Avoid_: Editor playhead restoration, collaborative preview restoration

**Explicit Runtime Start**:
The separate runtime-side command, event, or integration call that creates or starts a camera execution instance from the Active Runtime Revision; Runtime Publication alone never starts an instance when none is running.
_Avoid_: Editor open, publish auto-start, local preview play

**Editor Animation Enable**:
The editor action that starts or replaces a Project Runtime Instance only for the UI Client Player currently operating that editor page; it has no target-player selector and does not affect other players' runtime instances.
_Avoid_: Runtime Publication, Collaborative Preview, server-wide start

**Published Runtime Gate**:
The rule that Editor Animation Enable may instantiate only the project's current Active Runtime Revision; newer uncommitted or unpublished editor changes remain available for editor preview but cannot enter gameplay runtime through that action.
_Avoid_: Save prompt, local preview restriction, automatic publication

**Fresh Runtime Start**:
The Editor Animation Enable behavior that creates or replaces the UI Client Player's Project Runtime Instance at project time zero, without inheriting the editor playhead, a stopped instance's playback position, or any prior runtime progress.
_Avoid_: Resume, seek-to-editor-playhead, hot handoff

**Transactional Runtime Replacement**:
The Editor Animation Enable replacement flow in which the current UI Client Player instance pauses only while the new published revision is validated and prepared, then is discarded after a successful atomic switch or resumed from its prior position if preparation fails.
_Avoid_: Persistent pause state, queued project stack, playback reset on failure

**Runtime Pause**:
The non-terminal temporary state in which a Project Runtime Instance remains allocated with its current playback position and can resume or be replaced by an explicitly defined runtime transition; it is distinct from Runtime Stop.
_Avoid_: Stop, teardown, reset-to-zero

**Runtime Stop**:
The terminal action that ends the UI Client Player's Project Runtime Instance, restores its Camera Restoration Snapshot or the safest verified player-camera fallback, and releases the instance instead of retaining resumable playback state.
_Avoid_: Pause, transactional replacement hold, editor preview pause

**Project Runtime Lifecycle Drain**:
The atomic project-lifecycle transition used before archive or deletion that reports affected runtime instances and target players during preflight, stops every affected instance, restores each camera context or safe fallback, and invalidates the Active Runtime Revision before the lifecycle result is exposed. A failed drain leaves runtime, camera, and project state unchanged; an archived project requires explicit republish/start after restoration.
_Avoid_: Runtime pause, per-player best-effort stop, automatic archive resume

**Camera Restoration Delivery**:
The bounded, idempotent post-commit instruction sent to an online player after a Project Runtime Instance has already been terminated. It restores the valid Camera Restoration Snapshot or a safe player-camera fallback, may be retried only within a finite delivery budget, and cannot block, roll back, or recreate the committed project lifecycle state when acknowledgement is absent.
_Avoid_: Distributed transaction acknowledgement, unbounded retry, runtime resurrection

**Natural Animation Completion**:
The non-user-initiated result of an animation reaching its authored end, governed by its `AnimType` and never treated as Runtime Stop unless a separate explicit runtime rule requests teardown.
_Avoid_: Stop button, disconnect cleanup, project replacement

**Terminal Value Hold**:
The completed animation state used by `STOP` and the unbounded terminal state used by `FOREVER`, in which evaluation remains at the final authored value without restoring the pre-enable camera.
_Avoid_: Runtime Stop, reset-to-zero completion

**Zero-Value Completion**:
The `STOP_BACK_TO_ZERO` completion state that resets the animation's time and contribution to zero and stops that animation after its endpoint without destroying the Project Runtime Instance or restoring the Camera Restoration Snapshot.
_Avoid_: Runtime Stop, pause, terminal value hold

**Gameplay Input Passthrough**:
The default Project Runtime Instance policy in which camera animation changes only camera evaluation while movement, jumping, attacking, interaction, item use, and hotbar input continue to reach ordinary gameplay.
_Avoid_: Immersive Input Capture, implicit player freeze

**Runtime Input Policy**:
An explicit setting stored by the current animation that may request gameplay-input restrictions while that animation runs; absence of that setting always means Gameplay Input Passthrough.
_Avoid_: Project-wide default, editor shortcut policy, hidden enable-animation side effect

**Animation Properties**:
The current animation's authoring and runtime-behavior settings, including playback type and every supported discrete camera or input field that belongs to that animation rather than to the whole project or a separate event track.
_Avoid_: Project preferences, runtime instance state, event-track approximation

**Animation Menu**:
The top-level command surface for the current animation, divided into Animation Content, Editor Preview, and Runtime Publication groups. Content commands author the project through normal server revisions and Undo/Redo; preview commands change only local editor or UI Client Player state according to their existing contracts; publication commands operate only on server-accepted revisions and create Runtime Publication Records rather than personal edit-history items.
_Avoid_: File Menu, mixed preview/publication action, context-menu-only lifecycle

**Animation Content Command Group**:
The Animation Menu group containing create, properties, rename, duplicate, and delete actions for project animations. Every enabled command uses the established authoring validation, confirmation, conflict, revision, and personal Undo/Redo rules.
_Avoid_: Runtime publication, editor playback control, project import

**Animation Preview Command Group**:
The Animation Menu group containing Preview Current Animation, Enable Published Animation for Me, and Stop Enabled Animation for Me. Local preview may show the editor's working content, while enable is restricted by the Published Runtime Gate and targets only the UI Client Player; the group never publishes content or targets another player.
_Avoid_: Runtime Publication, collaborative preview leadership, server-wide stop

**Animation Publication Command Group**:
The Animation Menu group containing Publish Current Accepted Revision, Replace Active Runtime Revision when applicable, and Stop Published Runtime Application. These are server-authorized Lock-Neutral Project Operations over accepted content, visibly separated from preview controls and recorded in finite runtime publication history without entering Personal Edit History.
_Avoid_: Enable Animation for Me, project save, implicit runtime start

**Animation Properties Popup**:
The Blockbench-inspired responsive contextual editor for Animation Properties, opened from the animation item's Properties action or by double-clicking the item; it uses a compact dark modal layout, adapts to available bounds without absolute Minecraft screen coordinates, edits only the current animation, and commits or cancels as one coherent interaction.
_Avoid_: Generic settings screen, project settings, screenshot-only option list

**Camera Editor Button Tooltip**:
The concise explanatory surface shown for a camera-animation editor button after the pointer remains over that button for more than one second; it is anchored beside the button, uses the Blockbench-inspired visual language, does not reflow the layout, and explains the button's purpose without performing or previewing the action.
_Avoid_: Timeline-node tooltip, field validation message, hover decoration

**Button Tooltip Delay**:
The strict one-second residence interval before a Camera Editor Button Tooltip becomes eligible to render from either pointer hover or keyboard/controller focus; leaving the button, changing the focused or hovered target, or closing the containing surface cancels the pending tooltip.
_Avoid_: Instant tooltip, arbitrary animation delay, persistent help panel

**Disabled Button Tooltip**:
A Camera Editor Button Tooltip shown for a disabled button after the same one-second pointer delay; it explains the button's normal purpose and the current blocking reason, while the button remains non-clickable and the tooltip never implies that the action succeeded.
_Avoid_: Disabled-button click, error toast replacement, hidden permission failure

**Property Preview Transaction**:
The local uncommitted state produced while editing an Animation Properties Popup, immediately reflected in the current preview but invisible to collaborators and absent from project revision, server journal, and personal Undo/Redo until confirmation.
_Avoid_: Draft revision, collaborative gesture preview, partial server operation

**Property Commit Transaction**:
The single atomic edit submitted when an Animation Properties Popup is confirmed, containing all changed property fields as one authoritative revision and one Undo/Redo unit; cancellation or server rejection discards the entire preview transaction.
_Avoid_: Per-field commit, live network typing, partial rollback

**Property Popup Focus Contract**:
The Animation Properties Popup interaction rule in which `Esc` cancels and rolls back, `Enter` confirms unless a multiline field owns focus, clicking outside does not dismiss the popup, and the first suitable field receives focus on open; name inputs select their existing text.
_Avoid_: Accidental outside-click close, implicit partial save, focusless modal

**Editor Overlay Layer**:
The shared responsive surface above the Camera Animation Editor that hosts ephemeral tooltips, anchored menus, property editors, confirmations, and progress states without changing the underlying workspace layout. It owns deterministic z-order, input routing, focus restoration, dismissal, and lifecycle rules for every editor overlay.
_Avoid_: Unrelated screen replacement, operating-system window, arbitrary popup stack

**Tooltip Layer**:
The passive lowest-priority surface within the Editor Overlay Layer. It appears beside a qualifying button after the established delay, never receives keyboard focus, never captures input, never reflows the editor, and is dismissed when its target or containing context changes.
_Avoid_: Help dialog, validation popup, hover decoration on timeline nodes

**Anchored Utility Surface**:
A lightweight Editor Overlay Layer surface anchored to its trigger, including top menus, context menus, command completion, and compact selectors. It may flip or clamp within responsive bounds, closes through outside-click or `Esc`, and never creates project content merely by opening or browsing it.
_Avoid_: Property editor, blocking confirmation, permanent panel

**Property Modal Surface**:
A focused, responsive editor surface for transactional properties of an animation, marker, or command effect. It keeps a local draft, does not close on outside-click, follows the established `Esc`/`Enter` and name-selection rules, and commits or cancels as one coherent interaction.
_Avoid_: Inline field, non-transactional menu, blocking project operation

**Blocking Transaction Surface**:
A modal Editor Overlay Layer surface for high-impact confirmation, migration, restore, deletion, replacement, or progress operations. It applies a restrained input-blocking scrim, validates authorization and stale state at the commit boundary, exposes truthful loading/success/failure/cancel paths, and permits only one blocking surface at a time.
_Avoid_: Toast, passive status message, silently queued confirmation

**Overlay Focus Restoration**:
The rule that records the invoking control or still-valid target before an Editor Overlay Layer surface opens, routes input to the active surface, and restores focus to that target when the surface closes or reaches a terminal state without changing playback, selection, project content, or runtime camera implicitly.
_Avoid_: Focus loss, automatic playhead movement, hidden activation

**Single Blocking Surface Rule**:
The invariant that at most one Blocking Transaction Surface owns input at any moment. Opening one closes irrelevant lower-priority utility or tooltip surfaces, while nested confirmations are represented as an in-surface state transition or a clearly serialized step rather than an unbounded stack of competing dialogs.
_Avoid_: Confirmation storm, hidden queue, competing modal windows

**Unpublished Project Changes**:
Accepted or locally visible project content newer than the Active Runtime Revision that may be edited and previewed but has not yet passed explicit Runtime Publication for gameplay use.
_Avoid_: Unsaved editor workspace state, active runtime revision

**UI Client Player**:
The local player represented by the client currently hosting the open Camera Animation Editor page and therefore the implicit target of Editor Animation Enable.
_Avoid_: Project Owner, Preview Leader, arbitrary runtime target

**Project Runtime Instance**:
The runtime execution state created for one target player from a project's Active Runtime Revision, with its own playback time, loop state, target, and stop lifecycle; the instance does not own or mutate the project document.
_Avoid_: Shared playhead, player capability animation, editor preview instance

**Revision Fan-Out**:
The coordinated application of one project's Active Runtime Revision to every currently running Project Runtime Instance for that project, preserving each target player's independent playback state during an Atomic Runtime Handoff.
_Avoid_: One global camera state, synchronized editor playhead

**Runtime Instance Teardown**:
The terminal cleanup of a Project Runtime Instance when its target player disconnects, leaves the world, changes dimension, or when the server or world closes; the instance is stopped, detached, and discarded rather than persisted for automatic recovery.
_Avoid_: Pause, editor close, checkpoint restoration

**Non-Persistent Runtime Instance**:
A Project Runtime Instance whose playback and target state exists only for the current connected world and dimension lifecycle and must be recreated through Explicit Runtime Start after any lifecycle exit or server restart.
_Avoid_: Saved runtime session, resumable project state

**Edit Operation**:
A single intentional change to animation content, such as inserting a keyframe, moving a keyframe, or changing an easing mode, submitted to and ordered by the server.
_Avoid_: Full save, JSON overwrite

**Operation Footprint**:
The set of stable logical object, field, and structural paths an Edit Operation may read or change, used by the server to distinguish disjoint edits from overlapping edits without comparing arbitrary client JSON blobs.
_Avoid_: Screen selection, raw packet size

**Field-Level Merge**:
The server's semantic acceptance of a stale operation when its Operation Footprint does not overlap intervening accepted field changes and the resulting document remains valid.
_Avoid_: Last-writer-wins, client silent retry

**Structural Conflict**:
A collaboration conflict involving creation, deletion, rename, reordering, or a shared structural ancestor whose existence or ordering affects another operation, even when serialized field locations differ.
_Avoid_: Different-field edit, visual overlap

**Collaborative Edit Conflict**:
The authoritative refusal of an Edit Operation whose footprint overlaps an intervening change or violates a structural conflict rule, leaving the shared document unchanged by that operation.
_Avoid_: Presence collision, transient preview

**Over-Limit Rejection**:
The atomic refusal of a proposed content change that exceeds a Collaboration Resource Budget, with a specific reason and no silent truncation, partial application, or baseline-changing retry.
_Avoid_: Undo Conflict, network timeout

**Personal Edit History**:
The temporary per-player, per-project collection of accepted edit operations, organized into Editing Session History Segments and used to choose Undo and Redo targets without replacing the shared project document. Only the active segment can execute Undo or Redo; older segments may remain as read-only references within the same connected lifecycle.
_Avoid_: Project revision history, client snapshot, cross-session undo

**Editing Session History Segment**:
A bounded personal Undo/Redo sequence tied to one authorized Editing Session and its document baseline. Explicit Permission Rejoin starts a new empty segment; the previous segment cannot be applied to, merged with, or automatically rebased onto the newly loaded authoritative document.
_Avoid_: Project branch, shared history, migrated undo stack

**Compensating Operation**:
A new server-ordered edit operation that reverses or reapplies the effect of an earlier accepted operation while preserving intervening edits and increasing the authoritative revision.
_Avoid_: JSON restore, mutable history entry

**Restore Checkpoint Operation**:
A special owner-authorized full-document Edit Operation that creates a Protection Checkpoint for the current state and applies a selected historical checkpoint as a new authoritative revision instead of rewinding project history.
_Avoid_: File overwrite, server rollback

**Protection Checkpoint**:
The automatically created durable snapshot of the current project immediately before a checkpoint restoration, retained so the restore can be undone without embedding a second full document in personal shortcut history.
_Avoid_: User workspace snapshot, temporary drag preview

**Restore Barrier**:
The project revision marker created by checkpoint restoration that causes pre-restore personal Undo/Redo entries to be retained but revalidated or temporarily suspended before they can be applied again.
_Avoid_: Permission boundary, editing lock

**Restore Content Refresh**:
The atomic client application of a successfully restored authoritative Project Document to every open editable view of that project, replacing stale authored content while preserving each player's valid local observation state.
_Avoid_: Client-side merge, tab close and reopen, collaborative preview reset

**Restore Draft Invalidation**:
The deliberate cancellation of every local content draft and transient authoring preview after a successful checkpoint restore because its baseline no longer matches the restored document; it never attempts an automatic merge.
_Avoid_: Accepted edit rejection, workspace-state reset, draft publication

**Restore Selection Reconciliation**:
The local cleanup performed after Restore Content Refresh that preserves still-valid selections and removes only identities no longer present in the restored document, with a concise status explanation.
_Avoid_: Selection broadcast, forced playhead seek, full UI reset

**Undo Conflict**:
A server rejection or user-visible warning raised when a requested personal undo would overwrite a newer incompatible change made by another participant or violate current project validation.
_Avoid_: Network retry, permission denial

**Restore Footprint**:
The conservative full-document Operation Footprint used by checkpoint restoration and its compensations, covering project content and relevant structure so a restore cannot silently overwrite later collaborative edits.
_Avoid_: Visual diff, file path

**Redo Branch**:
The per-player sequence of undone operations that remains eligible for Redo until the player submits a new edit or the connected lifecycle ends.
_Avoid_: Project branch, revision fork

**Keyframe Identity**:
The stable identity of one keyframe within one scalar track, independent of its timestamp, value, or visual ordering, used for selection, collaboration, conflict checks, and compensating operations.
_Avoid_: List index, timestamp

**Drag Overwrite**:
A time-edit gesture in which selected keyframe nodes may replace unselected nodes of the same scalar track and Additive Lane when they are dragged onto the same canonical time. The moving nodes keep their identities, overwritten target nodes are removed, different tracks or lanes may share a time, and the complete move-plus-overwrite is one atomic edit and Undo/Redo unit.
_Avoid_: Accidental merge, cross-lane overwrite, partial drag commit

**Overwrite Confirmation**:
The explicit confirmation shown when keyframe insertion or Auto Key would write onto an existing keyframe in the same scalar track and Additive Lane at the same canonical time. It identifies the animation, track, lane, and time, compares the current value with the incoming value, defaults focus to cancellation, and has no permanent skip option. One multi-target gesture uses one grouped confirmation with every affected target listed. Confirmation is revalidated against all target identities and values before commit; a stale target makes the grouped proposal a conflict instead of being silently overwritten. Its conflict state offers cancellation or a fresh read-and-confirm cycle, never a force-overwrite path. Confirming replaces the existing authored values in one edit transaction; cancelling leaves project content and Undo/Redo history unchanged.
_Avoid_: Silent overwrite, automatic merge, duplicate-time insertion, accidental Enter confirmation, stale forced write, confirmation storm

**Selection Set**:
The temporary collection of independently identified keyframes or tracks selected by one participant for inspection, linked editing, duplication, deletion, or batch property changes. A plain left click replaces the set, `Ctrl` plus left click adds or toggles an item into the set, and `Shift` plus left click removes an item from the set. Dragging an empty timeline region creates a marquee selection, while `Ctrl` adds or toggles the marquee result and `Shift` removes the marquee result; clicking empty space clears the set.
_Avoid_: Composite keyframe, project content

**Selection Modifier Gesture**:
A local selection operation that changes a Selection Set without changing project content: plain left click replaces the set, `Ctrl` plus left click adds or toggles a node, and `Shift` plus left click removes a node. It is distinct from an edit gesture that changes authored animation data.
_Avoid_: Shift range selection, content edit, modifier-key mutation

**Edit Gesture Transaction**:
The atomic user-intent boundary captured from one continuous editor gesture, submitted on release or explicit confirmation as one server operation, one authoritative revision, one journal entry, and one meaningful Undo/Redo unit even when it contains many field-level changes.
_Avoid_: Network packet, per-frame drag update

**Gesture Baseline**:
The pointer-down or edit-start snapshot of selected target identities, expected project revision, authored values, and resolved Gizmo Edit Target used to calculate and validate one later Edit Gesture Transaction.
_Avoid_: Project Snapshot, workspace snapshot

**Local Gesture Preview**:
The client-predicted result of an uncommitted continuous gesture, rendered immediately for the local participant without creating project content or an authoritative revision.
_Avoid_: Project Document, accepted edit

**Gesture Commit**:
The release or explicit confirmation event that converts a Local Gesture Preview into one validated Edit Gesture Transaction; cancellation discards the preview instead.
_Avoid_: Autosave, journal compaction

**Collaborator Presence**:
The ephemeral session-visible identity, color, cursor, playhead, selection, observation camera, and in-progress gesture preview associated with one participant; it never becomes project content or an editing lock.
_Avoid_: Project member record, edit permission

**Rejoin Presence Boundary**:
The lifecycle boundary at which a new Editing Session discards the previous session's collaborator state and accepts only a fresh server presence snapshot after handshake and permission validation. Restored local selection and observation state is not broadcast before that boundary, and old locks, subscriptions, or unfinished gestures never cross it.
_Avoid_: Resumed presence, stale cursor replay, lock revival

**Gesture Preview**:
A temporary participant-facing projection of another participant's uncommitted drag or batch edit, sent as throttled Collaborator Presence and shown for awareness until the server accepts or rejects the corresponding Edit Gesture Transaction.
_Avoid_: Authoritative keyframe, draft revision

**Live World Viewport**:
The editor's real-time Minecraft scene used to evaluate and display the selected project revision against the available world and dimension.
_Avoid_: Player camera, gameplay camera

**Embedded Preview Viewport**:
The editor-owned central viewport whose main content is a centered, aspect-preserving Game Frame Preview evaluated through a client-local preview camera; it never moves the player, changes the gameplay camera entity, or mutates runtime camera capability state.
_Avoid_: Gameplay camera, server runtime playback

**Game Frame Preview**:
The visual presentation inside the central editor viewport that uses the current client game scene when available, scales it to fit the bounded preview frame, preserves its aspect ratio, centers it, and applies the selected camera animation only to the editor-local preview view; it is not the player's actual gameplay camera window.
_Avoid_: Actual gameplay window, player camera, stretched preview

**Immersive Preview**:
An explicitly entered client-only presentation mode that temporarily routes the local render camera through the editor preview evaluator while preserving and later restoring the player's prior camera and input state.
_Avoid_: Default editor viewport, published animation

**Immersive Input Capture**:
The Immersive Preview state in which the editor owns local input routing: mouse movement controls only the preview camera, editor shortcuts remain active, gameplay movement and actions are suppressed, and `Esc` exits the mode before ordinary gameplay handling.
_Avoid_: Gameplay input passthrough, runtime camera control

**Gameplay Input Suppression**:
The explicit rule that player movement, jumping, attacking, interaction, item use, and hotbar switching cannot reach gameplay while Immersive Input Capture is active, even though the player entity remains in place.
_Avoid_: Player freeze, server-side immobilization

**Window Focus Loss**:
The client event in which the editor window or game loses focus during Immersive Preview; it pauses the preview and releases or safely restores mouse capture until focus returns or the mode exits.
_Avoid_: Connection Lifecycle Exit, preview cancellation

**Camera Restoration Snapshot**:
The temporary client-local capture of camera entity, view transform, lens state, input focus, mouse state, and routing flags required to restore gameplay after Immersive Preview ends or fails.
_Avoid_: Project Snapshot, Preview Revision Snapshot

**Abstract Stage Preview**:
A deterministic fallback visualization using an abstract grid and the stored stage transform when the recorded Minecraft dimension is unavailable, without silently replacing it with a participant's current position.
_Avoid_: Automatic recapture, live-world preview

**Trajectory Overlay**:
The world-space editor-rendered visual layer that maps scalar camera tracks into a path, nodes, orientation markers, frustum, lens bounds, raycast range, Gizmo handles, and collaborator indicators inside the Game Frame Preview or Abstract Stage Preview; these overlays are projected by the same preview camera and rendered in the scene rather than in a separate director window.
_Avoid_: Animation content, world entity

**Viewport Control Bar**:
The compact Blockbench-inspired control strip attached above the Game Frame Preview inside the Embedded Preview Viewport. It reports Preview Availability State and exposes only functional local observation, View Display Layer, and Immersive Preview actions; responsive overflow preserves the same commands without covering the rendered frame.
_Avoid_: Global toolbar, animation-content editor, floating overlay over the scene

**Preview Availability State**:
The explicit client-local condition of the central preview: live-world rendering with its dimension, Abstract Stage Preview with a truthful fallback reason, Immersive Preview, paused focus-loss recovery, or degraded restoration. It never silently substitutes the UI Client Player's current position or implies that a fallback is live gameplay rendering.
_Avoid_: Runtime publication state, connection status, hidden fallback

**Viewport Observation State**:
The current participant's local orbit, pan, follow-camera, and resettable observation transform used to inspect the Game Frame Preview or Abstract Stage Preview. It changes neither Preview Stage metadata nor authored camera tracks, and it is restored only through the current Project Workspace Layout lifecycle.
_Avoid_: Preview Stage, gameplay camera transform, Gizmo Edit Target

**Stable World Overlay Scale**:
The bounded screen-readable presentation of a world-anchored camera node, handle, or marker. Its artwork and invisible hit target remain usable across distance and GUI density without becoming a detached screen-space widget, shrinking below reliable interaction size, or growing into an exaggerated scene obstruction.
_Avoid_: Fixed world size at every distance, unbounded billboard, enlarged hover artwork

**Adaptive Overlay Quality**:
The client-local rendering policy that keeps world-space trajectories, nodes, frustums, guides, collaborator markers, and related camera overlays within a bounded frame-cost budget. It may switch between full, reduced-density, and minimal presentation with hysteresis, while preserving selected and interaction-critical visuals and never changing authored data, animation evaluation, camera motion, or collaboration semantics.
_Avoid_: Data simplification, animation baking, server quality state, frame-by-frame quality thrash

**Occluded Selection Trace**:
The restrained dashed outline or equivalent non-solid depth cue retained only for a locally selected world-space node or trajectory portion when terrain would otherwise hide it completely. It preserves spatial context without turning every overlay into an always-visible X-ray layer.
_Avoid_: Full-scene wallhack, unselected through-wall overlay, hover glow

**Workspace Shell**:
The responsive UI framework and editor frame that owns the top command surface, dockable panels, central preview and timeline, status area, focus routing, and narrow-window substitutions without storing animation content itself; every region derives from available content bounds and responsive constraints rather than Minecraft's absolute screen coordinates.
_Avoid_: Project document, screen coordinates

**Responsive UI Framework**:
The layout system that adapts the complete Camera Animation Editor, including the Workspace Shell, panels, timeline, property popups, toolbars, controls, labels, hit targets, and overlays, through relative sizing, bounded scaling, dock constraints, and explicit narrow-window substitutions without overlap or exaggerated zoom.
_Avoid_: Fixed-coordinate screen, uniform global scaling, clipped desktop layout

**Responsive Degradation Order**:
The deterministic narrow-layout priority that preserves the Core Editor Region first, converts the right Contextual Property Inspector to a drawer or temporary tab next, converts the left Animation Resource Browser/navigation surface after that, compresses collaboration and status details into icon entry points, and finally moves lower-frequency top-toolbar commands into an overflow menu. Each substitution preserves a reachable command path and local panel state rather than shrinking the complete editor below usable bounds.
_Avoid_: Random panel collapse, global shrink-to-fit, lost controls, hidden workspace state

**Dock Zone**:
A bounded region of the Workspace Shell that accepts one panel, a tabbed panel group, or a responsive drawer substitution. Dock zones expose visible insertion feedback during a drag, enforce panel minimums and maximums, and never allow a panel to be dropped where it would obscure the Minimum Usable Region.
_Avoid_: Absolute screen rectangle, project region, operating-system window

**Panel Tab Group**:
A dock-zone stack of compatible editor panels that share one bounded surface and expose a responsive tab strip. Only the active panel renders its interactive body, while each tab retains its local selection and navigation state according to its panel contract.
_Avoid_: Project Tab, browser tab, hidden panel

**Floating Editor Panel**:
A client-local editor panel temporarily detached from a Dock Zone but still rendered inside the Minecraft editor surface. It has a bounded relative position and size, a focusable title region, explicit close and dock-back actions, and cannot become an operating-system-level window or bypass Workspace Shell input routing.
_Avoid_: OS window, modal dialog, detached project session

**Panel Docking Preview**:
The transient insertion indicator shown while dragging a Floating Editor Panel or docked panel toward a valid Dock Zone. It previews the resulting dock, tab-group, or drawer placement without changing Project Workspace Layout until the drag is released.
_Avoid_: Content preview, collaborator gesture, immediate layout mutation

**Core Editor Region**:
The central Live World Viewport and bottom timeline/curve editor, which remain reachable in every valid Workspace Shell arrangement. They may resize, switch presentation mode, or share a tabbed substitute only when the substitute still exposes the complete editing path; they cannot be permanently hidden by ordinary panel-visibility commands.
_Avoid_: Optional inspector, decorative preview, uncloseable modal

**Panel Visibility State**:
The local visible, hidden, drawer, tabbed, or floating state of an auxiliary editor panel. Hiding a panel releases its render surface but preserves its search, selection, scroll, and navigation state according to the panel contract; View-menu controls can restore it without changing project content.
_Avoid_: Deleted panel, project permission, server subscription

**Panel Reopen Placement**:
The last valid Dock Zone, Panel Tab Group, or Floating Editor Panel placement remembered for an auxiliary panel. Reopening uses that placement when it fits current bounds, otherwise selects the nearest compatible responsive placement or drawer and reports the substitution without discarding panel state.
_Avoid_: Fixed screen coordinate, forced layout reset, project revision

**Timeline Virtualization**:
The rendering strategy that keeps the complete track, Lane, keyframe, and curve model available while creating visual rows, nodes, and curve geometry only for the currently visible tracks and time range, without deleting, merging, or changing off-screen animation data.
_Avoid_: Data truncation, lazy data loss, fixed maximum visible keys

**Visible Curve Window**:
The current time and track viewport used to bound curve evaluation and drawing; changing zoom, pan, or selection updates the rendered subset while preserving logical curve and selection state outside the window.
_Avoid_: Curve clipping as data deletion, preview range, project work range

**File Menu**:
The top-level command surface for project lifecycle and document exchange, including create, open, close, duplicate, archive, import, export, recovery, and explicit save or checkpoint actions.
_Avoid_: Resource reload, editor settings

**Edit Menu**:
The top-level focus-aware command surface for personal Undo/Redo, selection commands, structured cut/copy/paste, duplication, and deletion of the current compatible selection. It uses the same command registrations as shortcuts, context menus, and the Command Palette, and contains no save, checkpoint, publication, recovery, or import/export lifecycle actions.
_Avoid_: File Menu, project history viewer, global operation rewind

**Timeline Menu**:
The top-level command surface for the current animation's timeline, grouped into preview transport, playhead navigation, exact time entry, Animation Duration, Project Timeline Markers, and Work Range. Play, pause, and loop operate on Local Preview; its visually separated stop entry invokes the existing Stop Enabled Animation for Me command with Runtime Stop semantics rather than inventing a resumable preview-stop state.
_Avoid_: Keyframe Menu, timeline presentation settings, second stop meaning

**Timeline Runtime Stop Alias**:
The Timeline Menu entry that invokes the same Stop Enabled Animation for Me command exposed by the Animation Menu. It is visually separated from local preview transport and ends the UI Client Player's Project Runtime Instance instead of pausing, rewinding, or changing the editor playhead.
_Avoid_: Preview pause, return-to-zero, duplicate runtime command

**Tools Menu**:
The top-level command surface for advanced, non-inline animation authoring tools, divided into Validation, Advanced Retiming, and Bake and Optimization. Validation is read-only; retiming, baking, and optimization create local previews first and submit only explicitly confirmed, server-validated atomic project operations.
_Avoid_: File export, ordinary keyframe editing, one-click repair-all

**Help Menu**:
The top-level client-local read-only command surface for learning, reference, support, and About information. It opens the bundled Camera Wiki, context-specific help, shortcut and field references, bounded runtime diagnostics, privacy-preserving support summaries, platform directory actions, and editor/version information without changing project content, playback, runtime instances, revision history, or personal Undo/Redo.
_Avoid_: Settings Menu, project mutation, remote-only help dependency

**Camera Wiki**:
The versioned, bundled Markdown documentation surface for the camera runtime framework and rewritten Camera Animation Editor UI. It explains verified data flow, field meaning, interaction rules, collaboration boundaries, compatibility formats, lifecycle behavior, and validation limits; a source or UI change updates the matching article or creates a focused article instead of leaving the behavior undocumented.
_Avoid_: Generic external wiki, implementation guess, stale screenshot catalog

**Editor Localization Contract**:
The rule that every camera-editor control, menu, status, validation message, error, tooltip, and accessible label is rendered from a translation key with equivalent Simplified Chinese and English coverage. Server messages carry semantic codes and bounded arguments rather than pre-rendered prose; user-authored names and commands remain unchanged. Camera fields expose both a localized label and their stable raw identifier for JSON, search, and documentation use.
_Avoid_: Hard-coded display text, server-selected client language, translated user content

**Editor Accessibility Contract**:
The interaction and presentation rule that makes every camera-editor control keyboard reachable, visibly focused, locally narrated where useful, and understandable without color alone. Focus follows the responsive visual hierarchy, text fields retain editing-key priority, high-frequency timeline and collaboration changes use bounded narration, and reduced UI motion affects editor transitions only rather than authored camera playback.
_Avoid_: Mouse-only control, color-only state, per-frame narration

**Contextual Help Target**:
The stable documentation anchor associated with a selected editor button, field, track, node type, menu action, or diagnostic category. Help opened from that target resolves directly to the relevant Camera Wiki section when available and falls back to a concise built-in explanation when the article is unavailable; it never edits or changes focus state as a side effect.
_Avoid_: Tooltip replacement, selection mutation, homepage-only help

**Help Reference Surface**:
The searchable, responsive read-only surface for shortcut commands, camera fields, project-format profiles, Legacy Animation Format compatibility, owner/editor roles, and collaboration behavior. Search is local to bundled metadata and documentation, while links to external resources are optional supplements rather than required runtime dependencies.
_Avoid_: Command Palette, settings search, server query

**First-Use Editor Tour**:
The optional, skippable, device-local guided walkthrough offered when the Camera Animation Editor is first opened. It anchors explanations to the real responsive controls for project navigation, world preview, animation resources, tracks, timeline, properties, preview, and publication without creating a project, mutating server state, starting playback, or taking over the camera. Its completed or skipped state is a Global Editor Preference and Help may replay it.
_Avoid_: Mandatory tutorial, sample-project mutation, fixed-coordinate overlay

**Actionable Editor Empty State**:
The non-decorative state shown when a Project Browser, panel, list, track, result, or other editor surface has no content or cannot display content. It states the reason and exposes only valid next actions, such as create, open, import, clear a filter, request permission, or inspect an error, rather than leaving an unexplained blank region.
_Avoid_: Blank panel, decorative icon only, disabled dead end

**Support Summary**:
The explicitly requested client-local diagnostic package containing only bounded, privacy-filtered editor and runtime information needed for troubleshooting. It may be copied as text or used to open the logs directory, never includes raw command output, private drafts, credentials, or unrestricted world data, and does not create project or runtime state.
_Avoid_: Full debug dump, player-facing command feedback, automatic upload

**About Surface**:
The read-only version and capability summary for EndingLibrary, the camera editor, Minecraft, Forge, the native Project Format, and the server-advertised editor features. It reports verified local and session information without implying that a feature is available when the current server or project format does not support it.
_Avoid_: Changelog editor, settings panel, publication status

**Validation Tool Group**:
The Tools Menu group for read-only validation whose default is the current animation or focused authoring scope, with an explicit Whole Project switch. It runs through a Cancellable Validation Run, reports truthful phases rather than fabricated percentages, and checks runtime-data validity, curve and timing consistency, resource-budget diagnostics, and Legacy Animation Format or Extended Camera Animation JSON compatibility. A Validation Report may focus an existing item in the editor but never mutates content or creates history.
_Avoid_: Auto repair, export side effect, runtime test execution

**Validation Scope**:
The explicit read-only range selected for a Validation Tool Group run: Current Animation or Whole Project. Current Animation is the default when an animation or focused authoring context exists; switching to Whole Project is deliberate, starts a new bounded run, and cancels or supersedes an obsolete active run without changing content, playback, selection, runtime state, or history.
_Avoid_: Silent whole-project scan, guessed replacement scope, validation mutation

**Cancellable Validation Run**:
One bounded, read-only background execution of the Validation Tool Group that captures the active project, Project Tab, animation or focused scope, selected Validation Scope, and accepted-revision identity before checking the authoritative context. It runs without blocking editor input, rendering, playback, selection, collaboration, or ordinary navigation; exposes an explicit Cancel action; reports meaningful phases and truthful terminal states without fabricated percentages; and releases its temporary state on cancellation, lifecycle exit, context replacement, or completion. Only a matching valid terminal result may replace the displayed report, while late, stale, unavailable, cancelled, or failed results are discarded and never create project history.
_Avoid_: UI-thread scan, unbounded validation queue, false progress, stale report overwrite, automatic retry

**Validation Report**:
The completed, read-only diagnostic snapshot produced by one matching Cancellable Validation Run for a Validation Scope, carrying its terminal state, complete severity totals, and retained findings with stable focus targets. It may be complete-detail or a Details-Limited Validation Report; grouping, filtering, and expansion remain views over the same report and never alter project content, playback, runtime state, or history.
_Avoid_: Live validation stream, editable repair list, filtered report mutation

**Validation Finding**:
One immutable diagnostic entry in a Validation Report, carrying a stable semantic code, Validation Severity, bounded description arguments, and an optional Validation Focus Result. It records an observation only and never edits, automatically repairs, or itself authorizes or rejects a project operation.
_Avoid_: Auto-fix action, editable warning, operation result

**Validation Severity**:
The fixed semantic impact class of a Validation Finding: Error for invalid or unusable checked content, Warning for valid but risky, lossy, or resource-concerning content, and Information for correctness-neutral observations or optimization opportunities. It is defined by the diagnostic type and cannot be changed by the user, report presentation, or client localization.
_Avoid_: User priority, filter category, client-selected importance

**Validation Finding Group**:
A presentation-only collection of Validation Findings that share one Validation Severity and stable diagnostic code, with Whole Project reports additionally exposing affected-animation subdivisions. It preserves complete occurrence counts and every retained finding's identity and focus target; visually similar findings with different diagnostic codes remain separate, and omitted details are explicit when the report is details-limited.
_Avoid_: Deduplicated finding, localized-text grouping, merged diagnostic identity

**Validation Detail Budget**:
The server-authoritative finite allowance for retained focusable Validation Finding details in one Validation Report, independently bounded overall, per Validation Severity, and per stable diagnostic code while successful scans preserve complete aggregate counts. Its defaults are generous and server-configurable only within a hard ceiling, with concrete values selected through implementation load testing.
_Avoid_: Unlimited finding list, client-controlled memory limit, hidden truncation

**Details-Limited Validation Report**:
A successfully completed Validation Report whose full-scan totals are available but whose retained focusable instances reached the Validation Detail Budget. It exposes total-versus-retained counts and clearly limits search or filtering to retained instances while offering narrower explicit reruns without claiming omitted findings are absent.
_Avoid_: Failed validation, complete-detail report, silent truncation

**Outdated Validation Report**:
A Validation Report that no longer represents current accepted project content because content changed after its bound check. It remains read-only and visibly non-current until a successful explicit rerun replaces it or the normal lifecycle releases it; findings remain inspectable, while missing or unidentifiable focus targets become unavailable.
_Avoid_: Current diagnostic authority, automatically refreshed report, hidden stale result

**Advanced Retiming Tool Group**:
The Tools Menu group for preview-first time operations over one explicit Retiming Target Scope, including exact time offset, time scaling around an explicit anchor, and reversal. It preserves canonical Timeline Time semantics, applies explicit duration and marker boundary rules, and commits the complete result as one atomic Undo/Redo operation after confirmation.
_Avoid_: Playback-speed change, implicit coordinate conversion, per-key retime history

**Retiming Target Scope**:
The explicit operation domain used by one Advanced Retiming operation: Selection or Work Range. An eligible Selection is the default when present, Work Range is the fallback, and choosing either never implicitly intersects the two or expands to the whole animation. Work Range may explicitly include its boundary-inclusive Project Timeline Markers, while Selection never implicitly includes Marker Selection.
_Avoid_: Implicit whole-animation scope, selection-range intersection, guessed retiming target, hidden marker inclusion

**Work Range Retiming Membership**:
The closed-interval membership rule for Advanced Retiming: a time-bearing authored item belongs when its canonical timestamp is at or between the Work Range boundaries. A Curve Segment that merely crosses a boundary is not itself admitted, and scope resolution never creates boundary nodes; affected cross-boundary connections are disclosed in preview.
_Avoid_: Open interval, overlap-based segment selection, implicit boundary key

**Curve Retiming Transform**:
The time-domain transformation applied to active and Dormant Curve Data when their nodes are retimed. Offset and non-reversing scale preserve normalized handle times and authored values, while reversal swaps incoming and outgoing sides and mirrors normalized handle times so a fully covered segment traces its previous shape backward; `AUTO` data is deterministically reevaluated after topology resolution and cross-boundary changes remain explicit in preview.
_Avoid_: Hidden bake, handle deletion, untransformed reverse tangent, automatic boundary fitting

**Retiming Lock Conflict**:
The invalid Advanced Retiming footprint created when any affected Command Animation Effect Event has an active Command Event Personal Edit Lock, including one held by the requesting player. The complete operation neither skips nor waits for locked content; the event edit must finish or be cancelled before a fresh Tool Operation Preview can be confirmed.
_Avoid_: Partial locked-event skip, lock takeover, hidden draft rebase, queued retiming

**Retiming Time Boundary Violation**:
The invalid Tool Operation Preview state in which at least one proposed time fails its target item's lower boundary, including the Project Timeline Marker rule that remains strictly above animation start. It blocks the complete Retiming operation while preserving the exact requested transform and showing the offending items and valid input range.
_Avoid_: Negative authored time, per-item clamping, compensating whole-result shift, partial commit

**Scale Retiming Anchor**:
The canonical Timeline Time held fixed while a Scale Retiming transform expands or contracts every other target time around it. Each newly opened Scale tool defaults to Scope Start and may explicitly use Scope Start, Scope Center, Scope End, a captured Playhead time, or Custom Time without retaining a hidden previous choice.
_Avoid_: Playback speed, remembered implicit pivot, moving live playhead pivot, average key time

**Scale Retiming Factor**:
The finite strictly positive multiplier applied to target-time distance from the Scale Retiming Anchor: values below one compress time, one is an unchanged preview, and values above one stretch time. Zero is invalid because it collapses timing identity, while negative values are invalid because direction reversal belongs to the explicit Reverse operation.
_Avoid_: Playback rate, zero-time collapse, hidden negative reversal, non-finite multiplier

**Retiming Node Overwrite**:
The preview-bound replacement of an unaffected scalar keyframe when one Retiming result reaches the same scalar track and exact canonical time, regardless of either node's interpolation mode. One aggregate confirmation lets every moved node retain its identity while deleting the disclosed residents atomically; collisions among moved nodes are invalid, and changed or previously unseen residents require a new preview and consent.
_Avoid_: Per-collision dialog, silent resident deletion, arbitrary moved-node survivor, stale overwrite consent

**Bake and Optimization Tool Group**:
The Tools Menu group for preview-first conversion of evaluated animation into authored keyframes, removal of provably redundant keyframes, and removal of empty tracks. Every operation reports its affected scope, node-count change, and trajectory error or compatibility consequence before confirmation; partial or silent cleanup is forbidden.
_Avoid_: Hidden resampling, lossy cleanup, automatic publication

**Bake Track Scope**:
The explicit set of populated, evaluable Scalar Animation Tracks whose results one Bake operation converts into authored keyframes. It defaults to Selected Tracks and may explicitly expand to All Populated Numeric Tracks; Track Group selection resolves eligible populated children, while visibility filters, empty tracks, command events, markers, and coordinate-family conversion never alter the scope.
_Avoid_: Visible-tracks-only bake, implicit empty-track creation, effect-event bake, coordinate conversion

**Bake Time Scope**:
The explicit closed Timeline Time interval evaluated by one Bake operation: Work Range, Full Animation, or Custom Range. A valid Work Range is the default, Full Animation is the fallback from zero through Animation Duration, and Custom Range is a local preview input; keyframe selection never implies the interval and Bake never extends project duration.
_Avoid_: Selected-key span, hidden full-animation bake, zero-length range, duration-extending bake

**Bake Boundary Anchor**:
A scalar keyframe exactly at a Bake Time Scope boundary that separates the rebuilt in-scope curve from the preserved out-of-scope trajectory. It reuses an existing boundary keyframe when available or is created through Trajectory-Preserving Key Insertion, and only its in-scope-facing curve data may be replaced by Bake.
_Avoid_: Duplicate boundary sample, ordinary sample-grid point, Work Range handle, out-of-scope curve rewrite

**Lane-Preserving Bake**:
A Bake that rebuilds every populated Additive Lane of each target Scalar Animation Track independently while retaining each lane's stable identity, name, order, and additive contribution. It never substitutes the track's accumulated result into one merged lane.
_Avoid_: Flattened Bake, merged Baked lane, accumulated-value replacement

**Bake Sampling Mode**:
The explicit rule that selects generated keyframe times during Bake: deterministic Adaptive Error-Bounded sampling by default or a user-selected Fixed Interval. Scope endpoints, source key times, and discontinuity boundaries remain mandatory, while Display Frame Rate participates only when a fixed interval is expressed in frames.
_Avoid_: Timeline-zoom sampling, implicit display-FPS bake, random refinement, silent fidelity relaxation

**Bake Output Interpolation**:
The Curve Interpolation Mode assigned to rebuilt in-scope Bake segments: `LINEAR` for continuous motion and `CONSTANT` only for source discontinuities that must remain exact. Rebuilt segments retain no Easing Preset or active or Dormant Curve Data, while a Bake Boundary Anchor keeps its original outside-facing curve semantics.
_Avoid_: Automatic smoothing, hidden Bézier fitting, dormant pre-Bake curve restoration

**Retained Bake Keyframe**:
An existing scalar keyframe at a mandatory in-scope Bake sample time that keeps its stable identity, canonical time, and Additive Lane membership while its in-scope value and curve semantics are rebuilt. It is distinct from a newly generated Bake sample, which receives a new identity.
_Avoid_: Recreated source keyframe, retained pre-Bake curve, generated sample with borrowed identity

**Fixed-Interval Fidelity Warning**:
The Bake preview state in which an exact Fixed Interval candidate exceeds the recommended trajectory fidelity but has no hard validity or resource failure. It permits one explicit `Bake Anyway` confirmation without changing the interval, sampling mode, or generated sample set.
_Avoid_: Adaptive target failure, automatic refinement, silent tolerance relaxation, remembered warning bypass

**Trajectory Fidelity Profile**:
The shared unit-aware error contract selected independently by each Bake or Remove Redundant Keyframes operation. It combines relative trajectory tolerance with absolute block- or degree-unit caps and applies to every participating Additive Lane and its accumulated Scalar Animation Track without linking the two tools' current selections.
_Avoid_: Bake-only tolerance, shared mutable tool setting, per-lane override, normalized-only error, accumulated-track-only validation

**Maximum Validation Probe Gap**:
The largest permitted Timeline Time distance between temporary Bake probes used to verify a candidate trajectory. It bounds validation coverage without requiring those probes to become generated keyframes or altering an exact Fixed Interval output grid.
_Avoid_: Output keyframe interval, Display Frame Rate, automatic keyframe grid, incomplete-validation waiver

**Non-Simplifying Bake**:
A Bake contract that commits the exact retained and generated keyframe set disclosed by its Tool Operation Preview without post-generation decimation or redundant-key removal. Any later node reduction belongs to a separate Optimization operation with its own preview and history entry.
_Avoid_: Optimize-after-Bake checkbox, hidden cleanup, combined Bake-and-reduction transaction

**Redundant-Key Candidate Scope**:
The explicit set of numeric scalar keyframes that Remove Redundant Keyframes may delete: eligible selected keyframes by default, or keyframes resolved from a user-chosen track scope and closed time scope when range mode is selected. Neighboring out-of-scope keyframes may provide read-only trajectory context but can never become deletion candidates.
_Avoid_: Implicit full-animation cleanup, Work Range intersection, visibility-based optimization, neighboring-key deletion

**Linear Redundancy Reconstruction**:
The initial Remove Redundant Keyframes rule in which an eligible interior node of one continuous `LINEAR` Additive Lane run may be deleted only when its surviving neighbors reconnect with `LINEAR` and the complete affected trajectory remains within the chosen fidelity contract. Lane endpoints, `CONSTANT` discontinuities, and every Easing- or Bézier-adjacent node are protected.
_Avoid_: Value-only collinearity test, automatic Bézier fitting, Easing rewrite, endpoint deletion, Bake-only cleanup

**Deterministic Least-Error Reduction**:
The repeated Remove Redundant Keyframes selection rule that provisionally scores every eligible deletion against the immutable operation-start trajectory, accepts the passing candidate with the lowest normalized fidelity consumption, and resolves equal scores by stable project order. It stops when no remaining candidate passes and does not claim a globally minimal keyframe set.
_Avoid_: Intermediate-result baseline, chronological-first deletion, random tie-break, global-optimum claim, cumulative drift

**Bounded Key-Reduction Run**:
The cancellable background computation that previews or authoritatively recalculates one Remove Redundant Keyframes candidate from a finite immutable snapshot. Project Tab previews are latest-wins, server confirmations are single-flight per player and project, and cancellation, staleness, lifecycle exit, deadline, or budget exhaustion produces no partial reduction.
_Avoid_: Render-thread optimization, unbounded queue, world-object capture, partial candidate, automatic retry, detached task

**Key-Reduction Resource Budget**:
The server-authoritative finite ceilings for one Bounded Key-Reduction Run and for all retained reduction work, covering content size, exact error work, snapshots, deadlines, concurrency, queueing, and aggregate memory. Servers may lower the advertised effective limits but cannot raise them beyond the built-in hard maxima used by client preview and authoritative recalculation.
_Avoid_: Client-only budget, unbounded operator override, hidden overcommit, partial fallback, mismatched integrated-server limits

**Key-Reduction Boundary Guard**:
The Tracks in Time Range rule that treats its closed Time Scope as a strict structural cut: any deletion whose replacement segment would cross a range endpoint is protected even when its measured trajectory error would be zero. It creates no boundary nodes and leaves every out-of-scope node and segment unchanged.
_Avoid_: Boundary-anchor insertion, cross-boundary collinearity exception, out-of-range tolerance, implicit scope widening

**Pristine Empty Track**:
An instantiated Scalar Animation Track with no accepted keyframes and no authored Additive Lane structure worth preserving; it contains either no Lane or only one untouched system-default empty Lane. Removing it releases redundant project storage without removing the supported camera channel or any named, imported, reordered, or otherwise authored empty Lane.
_Avoid_: Zero-valued keyed track, Legacy Group Lane, Remove Empty Lanes, channel removal, UI-hidden track

**Empty-Track Cleanup Scope**:
The explicit structural target set for Remove Empty Tracks: Selected Tracks, Current Animation, or Whole Project. A valid track or Track Group selection defaults to Selected Tracks; without one no broader scope is inferred, and time ranges, visibility, filtering, and viewport presence never alter the chosen set.
_Avoid_: Implicit current-animation fallback, Work Range cleanup, visible-only cleanup, selection intersection, silent whole-project cleanup

**Bounded Empty-Track Scan**:
The cancellable background classification of one Empty-Track Cleanup Scope from a finite structural summary, with latest-wins client preview and independent server-authoritative recalculation. It retains no keyframe values, full Project Document, world objects, or partial candidate list beyond any terminal outcome.
_Avoid_: Render-thread scan, client-trusted candidates, full-document snapshot, per-track progress stream, partial cleanup result

**Empty-Track Cleanup Reconciliation**:
The local editor-state response to accepted empty-track removal: identities that no longer exist are discarded while surviving animation context, timeline navigation, playback, supported channels, and stable viewport position remain intact.
_Avoid_: Neighbor auto-selection, animation switching, dangling track reference, restored stale selection

**Tool Operation Preview**:
The local, non-authoritative result shown before a mutating Tools Menu operation is submitted. It contains the resolved target scope, baseline revision, proposed field and structure changes, validation warnings, resource impact, and any trajectory or compatibility metrics; closing, cancelling, losing the baseline, or disconnecting discards it without a project revision or Undo/Redo entry.
_Avoid_: Live shared draft, background mutation, unbounded preview history

**Structured Editor Clipboard**:
The client-local typed payload created from accepted selected editor content, including the content kind, relative timing and structural relationships, source schema, and compatibility metadata needed for validated paste or duplication. It remains available across Project Tabs during the same connected editor lifecycle, is held in bounded client memory only, and is cleared on world/server exit or disconnection. Copying does not modify project content, acquire a command lock, or expose another participant's uncommitted draft.
_Avoid_: Operating-system text clipboard, project snapshot, server queue

**Operating-System Clipboard Boundary**:
The separation between the Structured Editor Clipboard and the device's ordinary text clipboard. Camera editor copy, cut, paste, and duplication use the typed in-memory editor payload; they never write project structures or command-event data into the operating-system clipboard. Text fields continue to use the normal system clipboard, and an explicit Copy as JSON command is the only path that serializes supported accepted content for external text use.
_Avoid_: Automatic JSON serialization, hidden disk clipboard, shared project clipboard

**Clipboard Resource Budget**:
The finite client-memory allowance for one or more Structured Editor Clipboard payloads, including serialized size, item count, nesting depth, and command-event text limits. A copy or cut that exceeds the allowance fails without replacing the previous valid payload; successful replacement releases the previous payload before retaining the new bounded one.
_Avoid_: Unbounded clipboard history, silent truncation, server queue limit

**Cross-Project Paste Validation**:
The complete validation performed when a Structured Editor Clipboard payload is pasted into another Project Tab, including project-format compatibility, target animation and track mapping, relative time, required structure, resource budgets, command-event lock and permission rules, and overwrite conflicts. Source stable identities are never reused as target identities, and a failed validation leaves the target project unchanged.
_Avoid_: Blind cross-project insertion, source-project lock transfer, identity collision

**Paste Preview Draft**:
The local, non-authoritative placement preview created from a Structured Editor Clipboard payload before paste is submitted. It resolves the target animation, tracks, lanes, relative time, required structure, command-event locks, resource budgets, and overwrite conflicts; when the target is another Project Tab it also runs Cross-Project Paste Validation. Only explicit confirmation may submit one atomic server edit.
_Avoid_: Accepted paste, runtime preview, automatic clipboard insertion

**Atomic Cut Intent**:
The editing command that first verifies and writes a Structured Editor Clipboard payload and then submits one all-or-nothing delete transaction for the selected accepted content. If deletion is rejected, the project remains unchanged while the successfully copied payload remains available; the editor never deletes first or reports a partial project cut as success.
_Avoid_: Move operation, destructive clipboard clear, partial server deletion

**View Menu**:
The top-level local display command surface for auxiliary panel visibility, workspace presets and layout reset, timeline presentation mode, world-space trajectory overlays, preview guides, collaborator indicators, focus-to-content actions, and immersive preview entry or exit. Its commands change only the current client's view or Workspace Layout; they never edit project content, publish or restore a revision, save a document, or enter Undo/Redo.
_Avoid_: Project content menu, File Menu, runtime camera command

**View Display Layer**:
One independently toggleable client-local visual layer in the editor, such as trajectory lines, world-space keyframe nodes, Gizmo handles, camera frustum, target connections, or collaborator indicators. A layer controls rendering only and does not remove, alter, or deselect the underlying project data.
_Avoid_: Animation track, hidden project field, server visibility permission

**Project View State**:
The player-and-project-scoped selection of active View Display Layers and presentation choices for one open Project Tab. It is stored only in that project's Project Workspace Layout during the current connected lifecycle and is cleared on server/world exit or disconnection.
_Avoid_: Global view default, project content, collaborator visibility

**Default View Layer Profile**:
The device-persisted Global Editor Preference that selects which View Display Layers are initially active for a project without a current-lifecycle Project View State. It supplies defaults only and is never updated merely because the user toggles a layer in the current project.
_Avoid_: Current project view, project preset, server policy

**Promote Current View Defaults**:
The explicit local Settings action that copies the current Project View State into the Default View Layer Profile for future project openings. It does not change the current project content, another participant's view, or any existing saved Project Workspace Layout.
_Avoid_: Automatic preference update, project save, shared layout publication

**Timeline Presentation Mode**:
The local rendering mode of the timeline, including ordinary timeline display and Graph Editor Mode. Switching modes changes visible controls, curves, and navigation without changing authored values, keyframes, selection identities, playback, or project content.
_Avoid_: Animation format, interpolation conversion, project mode

**Animation Properties Action**:
The context-menu command labeled Properties that opens the current animation's Animation Properties Popup; it is paired with the same popup-opening behavior on a left-button double-click.
_Avoid_: Rename action, project inspector, unrelated context-menu features

**Animation Item Context Menu**:
The compact Blockbench-inspired menu for one animation item, containing Properties, Rename, Duplicate, Delete, Enable Animation, Stop Animation, and Preview Current Animation when each action is valid; publication, import/export, recovery, project lifecycle, and editor preferences remain in their dedicated top-level menus.
_Avoid_: Full project command palette, unrestricted file menu clone, hidden publication trigger

**Animation Resource Browser**:
The dockable, responsive editor surface for discovering, searching, filtering, sorting, selecting, creating, importing, and managing animations in the current project. Selecting one resource establishes the Current Edited Animation, while the browser remains distinct from both the world-level Project Browser and the Timeline Track Tree Surface. Every advertised command must operate on real animation state.
_Avoid_: Decorative animation list, timeline track list, mock resource panel

**Current Edited Animation**:
The single animation resource whose tracks, curves, playhead evaluation context, viewport preview, Timeline Track Tree Surface, and Contextual Property Inspector are currently presented for authoring. Selecting it is local navigation and does not by itself play, enable, publish, or edit the animation.
_Avoid_: Active Project Tab, enabled runtime animation, selected track

**Left Animation Navigation Stack**:
The default left Dock Zone arrangement that places the Animation Resource Browser above the Timeline Track Tree Surface with a bounded draggable vertical splitter. The two panels share width but retain independent Panel Visibility State, docking, focus, scroll, and responsive drawer or Panel Tab Group fallbacks.
_Avoid_: Single merged tree, fixed-height sidebar, timeline lane canvas

**Animation Timeline View State**:
The player-, project-, and animation-scoped local navigation state for one Current Edited Animation, including its logical displayed-track set, Camera Track Tree expansion, timeline and graph scroll or zoom, playhead time, presentation mode, and locally selected track, keyframe, curve, marker, or event identities while they remain valid. It is retained only through the current Project Workspace Layout lifecycle.
_Avoid_: Animation content, shared playback state, runtime instance

**Local Preview Snapshot**:
The bounded client-local state captured immediately before a participant joins Collaborative Preview, containing the valid Current Edited Animation, playhead, playing or paused state, loop state, work range, viewport observation, Animation Timeline View State, and relevant local selection or panel navigation. It is used only for Preview Restoration within the same connected lifecycle and never becomes project content, shared playback state, or runtime camera state.
_Avoid_: Preview Revision Snapshot, gameplay camera snapshot, durable workspace file

**Animation Switch Synchronization**:
The local transition caused by selecting another animation resource: capture the prior animation's Animation Timeline View State, establish the new Current Edited Animation, restore its valid local view state, and update timeline lanes, curves, playhead evaluation, viewport preview, selection, and Contextual Property Inspector without creating a project edit or enabling playback.
_Avoid_: Project Tab switch, runtime animation replacement, automatic timeline mutation

**Directory Reveal**:
A client-only action that asks Minecraft's verified platform abstraction to open an existing directory in the operating system's default file manager.
_Avoid_: Folder selection dialog, server file access

**Help Directory Action**:
A Help-menu route that invokes Directory Reveal for a verified local logs or documentation directory. It uses the existing client-only platform adapter and reports failure visibly; it never exposes server-owned files or invents a path.
_Avoid_: Server file browser, arbitrary path opener, native call from common code

**Native File Dialog Adapter**:
A client-only platform boundary for native open, save, file-selection, and folder-selection dialogs, using the current operating system's supported dialog implementation without leaking platform-specific APIs into common or dedicated-server code.
_Avoid_: Minecraft screen, direct shared Win32 call

**Dialog Fallback**:
The explicit secondary chooser used when a native platform dialog cannot be initialized, accompanied by a visible status or error instead of silently failing or blocking the render thread.
_Avoid_: Automatic path selection, hidden exception

**Settings Menu**:
The top-level command surface for player-local editor preferences such as layout, input, display, snapping, timeline units, collaboration indicators, preview quality, and reset-to-default actions; it opens as a responsive Blockbench-style grouped menu, while complex settings open a searchable dockable Settings Panel. Direct toggles and bounded selectors apply immediately to local UI behavior, category reset restores only that category, and full reset restores all editor preferences after explicit confirmation. It must not silently change project content.
_Avoid_: Project properties, animation operation

**Settings Category**:
One independently searchable and resettable group of Global Editor Preferences: Interface, Timeline, World Viewport, Input and Shortcuts, Collaboration Display, or Performance and Preview. A category owns only player-local preference values and never contains project-authored animation fields.
_Avoid_: Project tab, animation track group, permission group

**Settings Panel**:
The responsive, dockable surface opened for complex Settings Menu options. It derives layout from available bounds, supports category navigation and search, shows current values and reset affordances, and releases its local search/result state when closed; opening it never creates a project revision or acquires a collaboration lock.
_Avoid_: Animation Properties Popup, project inspector, fixed-coordinate dialog

**Immediate Preference Application**:
The settings interaction contract in which a valid local preference change updates the client presentation or input behavior as soon as it is accepted by the control. No Apply or Cancel transaction is used. A preference that requires reopening the editor or reloading a client resource reports that boundary and remains pending only in the local preference store; it never blocks project editing or mutates project content.
_Avoid_: Project edit transaction, deferred universal apply, unsaved animation draft

**Project Browser**:
The non-destructive library entry surface for discovering, searching, sorting, filtering, creating, importing, and opening server-world Animation Projects. It occupies the main workspace when no project is open and otherwise appears through one temporary Project Browser Tool Tab without closing existing Project Tabs.
_Avoid_: File chooser, project document, animation resource browser

**Project Browser Tool Tab**:
The temporary non-project utility tab used to show the Project Browser while one or more Project Tabs remain open. Activating it saves the previous tab's Project Workspace Layout, leaves that project's live Editing Session and Collaborator Presence, and keeps the project tab available for later restoration; it never displays a Project Tab Draft Dot or internal revision label.
_Avoid_: Project Tab, read-only project tab, background editing session

**Project Catalog Query**:
One server-authoritative search, category, sort, filter, and cursor request over Project Catalog metadata. A newer query supersedes older in-flight results, pages are finite and virtualized, and opening a project loads its Project Document only after the selected catalog entry passes permission and format checks.
_Avoid_: Full catalog copy, project document load, client-only permission filter

**Project Browser Detail Surface**:
The read-only detail region for the selected Project Catalog result, showing its complete catalog metadata, compatibility, permissions, membership summary, and currently valid lifecycle actions. It is a side pane at comfortable widths and a real drawer at narrow widths; opening or closing it never opens the project by itself.
_Avoid_: Animation Properties Popup, inline row action strip, project document editor

**Project Browser State Surface**:
The operational content shown when the Project Browser is loading, empty, permission-limited, disconnected, rejected, migration-required, or unable to read a supported catalog page. It explains the state with text, iconography, and valid recovery actions rather than leaving a blank list or relying on color alone.
_Avoid_: Decorative spinner, silent empty list, generic modal error

**Recent Project**:
A player-local reference to an Animation Project opened during the connected editor lifecycle, used only for navigation convenience and never as project ownership or content.
_Avoid_: Project catalog entry, workspace snapshot

**Project Tab**:
A client-side tab representing one opened Animation Project, including its restored Project Workspace Layout and local editing context; it is not a second copy of the server document.
_Avoid_: Project document, server session, Project Browser Tool Tab

**Archived Inspection State**:
The client-local tab state entered after a project archive commits. It retains only the last authorized Project Document already held by that client for navigation, inspection, and isolated local preview, while leaving the Editing Session and releasing Presence, locks, subscriptions, and drafts; it cannot edit, publish, enable runtime, or export current content. The tab always exposes a clearly labeled reopen action, but the action is visibly unavailable while the project remains archived; after restoration it becomes available for an explicit fresh open or rejoin rather than reviving the old session.
_Avoid_: Archived editing session, live archived project, automatic restore

**Archived Reopen Availability Update**:
The bounded project-lifecycle notification that tells an Archived Inspection State tab that restoration has completed and its Reopen Project action may become available. It updates only lifecycle status and action availability; it does not load a Project Document, recreate an Editing Session, restore Presence or locks, apply drafts, or start playback. A missed update is repaired by a later explicit status revalidation when the tab or Project Browser regains attention.
_Avoid_: Automatic rejoin, document refresh, polling loop

Availability is reversible: a later authoritative archive outcome supersedes a previous reopen-available state, disables the action, and clears its indicator without discarding the held inspection document or interrupting isolated local inspection. A deletion instead applies the terminal Deleted Project Notice contract.

**Archived Reopen Indicator**:
The small player-local blue state marker shown on an inactive Archived Inspection State tab after an Archived Reopen Availability Update makes Reopen Project available. It is distinct from the Project Tab Draft Dot, does not change tab order or focus, and does not imply that document content, a session, playback, or runtime state has been restored. Selecting the tab exposes the lifecycle explanation and the explicit reopen action.
_Avoid_: Unsaved-content marker, presence badge, automatic tab switch

**Reopen Indicator Acknowledgement**:
The player-local read acknowledgement created when an inactive archived tab is actually focused and its lifecycle explanation becomes visible. It clears the Archived Reopen Indicator only; it leaves Reopen Project available and creates no content, history, session, or server-editing change. A later authoritative lifecycle update may replace the cleared state with a new indicator or a terminal notice.
_Avoid_: Successful rejoin, permission acknowledgement, document refresh

**Cancellable Reopen Request**:
The read-only request state entered after the player invokes Reopen Project and before Explicit Permission Rejoin reaches its atomic commit boundary. The player may cancel the request or close the tab while preparation remains cancellable; cancellation releases request state and leaves the tab in Archived Inspection State without creating a session, loading new content, or adding history. Once atomic commit has begun, cancellation no longer interrupts the transaction, and every response is accepted only by the still-current request and tab identity.
_Avoid_: Reversible committed rejoin, background auto-retry, old-tab resurrection

**Reopen Failure Outcome**:
The authoritative semantic state selected when Explicit Permission Rejoin cannot complete. A current archive returns to Archived Inspection State, deletion enters Deleted Project Notice, permission loss enters Permission-Revoked Inspection State, transient transport or server-capacity failure retains reopen eligibility with an inline manual retry, and project-format incompatibility routes to the applicable read-only format or migration surface. It never creates a generic editable fallback, automatic retry, old-session restoration, or personal history entry.
_Avoid_: One generic retry error, optimistic editable tab, silent migration

**Bounded Reopen Retry Gate**:
The server-authoritative single-flight and short-cooldown boundary for manual Reopen Project attempts by one player, project, and tab identity. It allows at most one active request, never queues repeated clicks, requires a new explicit attempt after a transient failure and cooldown, and revalidates all permission, lifecycle, format, and baseline conditions with a new request generation. Its request, cooldown, and bounded recent-result data release on tab closure, project-state replacement, or Connection Lifecycle Exit.
_Avoid_: Automatic retry, click queue, permanent throttle record

**Reopen Focus Handoff**:
The rule governing a Reopen Project request when its tab loses Active Project Tab focus. Focus loss cancels a still-cancellable request, while a request already inside atomic commit reaches one terminal result without reclaiming focus. A successful committed result updates only the original still-open tab, then leaves it as an inactive local container without sustained Presence, locks, or live subscriptions until a later focus-time revalidation; a failed result records only the applicable state on that tab.
_Avoid_: Forced tab activation, blocked tab switching, background live session

**Inactive Reopen Success Indicator**:
The temporary player-local blue marker shown when a committed Reopen Project request succeeds for a tab that is no longer active. The tab returns to ordinary inactive project styling and clears its archived reopen marker, but the success indicator remains distinct from the Project Tab Draft Dot until the player focuses the tab and current-state revalidation succeeds. It never implies active Presence, locks, live subscriptions, preview playback, or runtime activation.
_Avoid_: Unsaved-content dot, active-session badge, automatic tab focus

**Focus-Time Reopen Revalidation State**:
The temporary read-only state entered when the player focuses a tab carrying an Inactive Reopen Success Indicator. The tab immediately becomes visible and renders its already loaded authoritative document with a lightweight validation status, but disables editing, history execution, Auto Key, locks, publication, runtime enablement, playback restoration, and camera takeover until current permission, lifecycle, format, baseline, and active-tab checks succeed. Failure replaces the state in place with its semantic outcome.
_Avoid_: Blank loading page, editable optimistic focus, modal validation screen

**Non-Interrupting Lifecycle Notice**:
The presentation rule for an Archived Reopen Availability Update while the player is viewing an archived tab. It updates the project tab, status bar, and Reopen Project action without stealing focus, opening a blocking surface, changing selection or workspace placement, pausing local inspection playback, or replacing the held inspection document. If the affected tab is active, it may show one short-lived non-blocking status notice; the user still chooses when to rejoin.
_Avoid_: Forced rejoin, modal interruption, automatic preview reset

**Deleted Project Notice**:
The terminal client-local tab state entered after project deletion commits. It retains only bounded project identity and deletion metadata needed to explain closure, exposes no deleted Project Document or preview, and offers only tab closure without editing, export, recovery through the old tab, or personal Undo/Redo.
_Avoid_: Deleted project snapshot, recoverable tab, hidden content cache

**Project Lifecycle Revalidation Outcome**:
The terminal project-state result checked before ordinary Reconnect Draft Review. An archived or deleted project clears that project's retained drafts and transitions its tab to Archived Inspection State or Deleted Project Notice, so stale-draft resolution choices cannot delay, reverse, or bypass the committed lifecycle result.
_Avoid_: Draft-first recovery, lifecycle retry through old tab, archive merge

**Active Project Tab**:
The single currently focused Project Tab whose editor joins the live Editing Session, receives active Collaborator Presence, and may participate in Collaborative Preview; inactive tabs remain local UI containers. When the Project Browser Tool Tab is focused, no Project Tab is active and the previously focused project remains open but outside its live Editing Session.
_Avoid_: Project owner, background server session, selected utility tab

**Project Workspace Bar**:
The responsive top-level strip that identifies open Project Tabs and the temporary Project Browser Tool Tab while exposing user-facing connection and local navigation state. An active project tab uses the same surface color as the editor region below to create a fused Blockbench-like workspace, local unsubmitted content is marked with a Project Tab Draft Dot, and the internal project revision number is not shown as ordinary project-tab content.
_Avoid_: Revision banner, project document metadata dump

**Project Tab Draft Dot**:
The small player-local indicator shown on a Project Tab while that player has content-changing work that differs from its baseline but has not yet been accepted by the server, including retained drafts, active authoring previews, and submitted operations awaiting acknowledgement.
_Avoid_: Server persistence indicator, playback marker, workspace-layout change, shared unsaved state

**Project Persistence Status**:
The Collaboration Status Area state that reports whether accepted authoritative edits are journaled, flushing, snapshotting, delayed, or failed; it is never represented by the Project Tab Draft Dot.
_Avoid_: Local draft state, project revision label, tab selection state

**Workspace Bar Status Entry**:
The compact top-bar summary of the active project's connection and collaboration condition. It exposes the current high-priority state and opens the detailed Collaboration Status Area, but never displays the internal project revision as ordinary project-tab content.
_Avoid_: Full status panel, revision label, decorative online dot

**Collaboration Status Area**:
The detailed interactive status region in the bottom Editor Status Bar that reports connection, synchronization, participant, role, preview, conflict, lock, persistence, and runtime-diagnostic state using semantic icons, text, and interaction feedback; the Workspace Bar exposes only a compact entry into it.
_Avoid_: Decorative online badge, revision display, passive status light

**Editor Status Bar**:
The responsive bottom status surface of the Camera Animation Editor that presents the current Collaboration Status Area, accepted revision summary, local draft and persistence state, permission, active lock, collaborator, runtime-diagnostic, and enabled-runtime summaries without becoming a log console or content editor.
_Avoid_: Chat bar, permanent event log, project revision editor

**Status Detail Surface**:
The responsive read-only popup, drawer, or panel opened from one Editor Status Bar item. It shows the bounded details and valid recovery or navigation actions for that state, preserves the current playhead, selection, playback, viewport, drafts, and runtime camera, and closes without creating project or history state.
_Avoid_: Modal mutation flow, hidden state change, unrestricted diagnostic dump

**Transient Status Notice**:
The short-lived, non-blocking client-facing result notice paired with the Editor Status Bar. It briefly surfaces a meaningful success, failure, cancellation, or synchronization result without taking focus, interrupting playback, changing project content, or replacing the persistent status state; selecting it opens the corresponding Status Detail Surface.
_Avoid_: Modal confirmation, field validation message, player chat, permanent event log

**Status Notice Coalescing**:
The bounded presentation rule that permits only one visible Transient Status Notice at a time, merges repeated equivalent results into one notice with concise aggregate context, and retains only a finite pending set before deterministic oldest-first eviction. Coalescing never hides an action-required conflict or changes the authoritative outcome.
_Avoid_: Unbounded toast queue, last-message loss, silent error suppression

**Status Priority State**:
The deterministic display priority used when several status conditions coexist: connection failure or disconnection takes precedence, then an action-required conflict or permission/blocking state, then active synchronization or persistence work, then runtime diagnostics or collaborator activity, with connected-and-ready as the quiet baseline. Lower-priority conditions remain discoverable in the Status Detail Surface rather than being discarded.
_Avoid_: Last-message-wins banner, random status color, unbounded notification stack

**Session Collaboration Notification**:
The bounded, non-modal, session-scoped status record shown to currently connected collaborators for notable shared events such as checkpoint restoration; it carries concise actor, target, and time context and is not project content or a durable chat history.
_Avoid_: Persistent project log, player chat message, unbounded toast queue

**Restore Notification Detail**:
The read-only detail surface opened from a Session Collaboration Notification that identifies the restoring player, checkpoint, event time, target authoritative revision, and an authorized link to the corresponding restore summary or history record.
_Avoid_: Editable restore operation, automatic playhead seek, hidden audit log

**Tab Switch**:
The explicit transition from one Project Tab to another that checkpoints the current tab's workspace state, leaves its ephemeral presence, and restores the destination project's player-scoped workspace state without changing project content.
_Avoid_: Connection lifecycle exit, project close

**Panel Focus**:
The active interaction target within the Workspace Shell that receives keyboard shortcuts and routes commands to the viewport, timeline, track browser, inspector, or menus without relying on fixed screen coordinates.
_Avoid_: Selected keyframe, mouse position

**Project Workspace Layout**:
The player-and-project-scoped arrangement and navigation state of the editor, including panel geometry, collapsed sections, active tabs, selected content, tools, Project View State, timeline view, work range, and 3D observation view, restored only within the same connected lifecycle.
_Avoid_: Global editor preference, project document

**Global Editor Preference**:
A player-local editor setting independent of any project, such as theme, shortcut preset, default layout, display quality, or default snapping behavior. It is persisted on the current client device across game restarts, project changes, and server changes, but is never sent to the server or stored in a Project Document, checkpoint, revision, or personal history.
_Avoid_: Project Workspace Layout, animation metadata

**Editor Theme Profile**:
A player-local visual palette selection for the Camera Animation Editor. The initial profiles are Standard Dark and High-Contrast Dark, both derived from the Blockbench-inspired charcoal, near-black, restrained panel, and blue active-state language. A profile changes semantic color tokens and contrast treatment without changing project content or collaborator-authored data.
_Avoid_: Project theme, arbitrary per-widget colors, Minecraft world lighting

**Semantic Color Token**:
A named theme color role used consistently across editor surfaces, such as workspace background, panel surface, separator, primary text, muted text, selection, focus, warning, error, synchronization, Auto Key, and collaborator presence. Components consume roles rather than scattered literal colors, and non-color shape, text, border, or icon cues remain available for important states.
_Avoid_: Hard-coded widget color, texture palette, color-only status

**High-Contrast Dark Profile**:
The accessibility-oriented dark Editor Theme Profile that increases text, separator, focus, selection, and state contrast while preserving the same layout, interaction semantics, and Blockbench-inspired visual hierarchy. It does not rely on glow or excessive scaling and remains compatible with the semantic distinction between selection, warning, error, synchronization, Auto Key, and collaborator states.
_Avoid_: Bright theme, enlarged UI mode, alternate editor behavior

**Editor Density Profile**:
The bounded local spacing and hit-target profile applied inside the Minecraft GUI Scale bounds. `Compact` favors dense timeline and track editing, `Standard` is the default balance, and `Comfortable` increases spacing and interaction targets without proportionally enlarging every visual. Density changes preserve the responsive layout rules and never permit controls, nodes, text, or panels to exceed their safe maximums.
_Avoid_: Unlimited UI zoom, world camera zoom, project scale

**Minimum Usable Region**:
The layout constraint that preserves enough space for the central Live World Viewport and the bottom timeline/curve editor at every supported window size and density. When the available bounds cannot satisfy all panel minima, lower-priority panels collapse into drawers or tabs before core regions are compressed below usability.
_Avoid_: Fixed pixel canvas, decorative minimum, data truncation

**GUI-Scale-Constrained Layout**:
The rule that treats Minecraft's active GUI Scale as the outer logical-content boundary while deriving every Camera Animation Editor region from relative bounds, density tokens, minimum sizes, and bounded maximums. The editor does not multiply Minecraft's scale again or use unrestricted absolute coordinates.
_Avoid_: Double scaling, raw screen-coordinate layout, unbounded font scaling

**Collaborator Color Pool**:
The finite client-selected set of distinguishable semantic colors assigned to active collaborators for presence indicators. Assignments remain supplemented by names, icons, borders, or patterns and are not project content or permission data; the pool is reused deterministically when participants leave.
_Avoid_: User-authored material color, permanent player color identity, color-only permission

**Device Preference Store**:
The client-local durable storage boundary for Global Editor Preferences. It contains only validated preference values and schema/version metadata, loads before the Settings Menu or Settings Panel is shown, and uses safe default values when a stored value is missing, invalid, or from an unsupported preference schema. It does not contain project content, workspace snapshots, collaborator state, local drafts, or runtime playback state.
_Avoid_: Project file, server profile, workspace session cache

**Workspace Preset**:
A named arrangement of panels and interaction emphasis for a particular editing task, such as camera blocking, curve editing, or review, applied without changing project content. The initial presets are Animation, Camera Preview, Curve Editor, and Collaboration Review; the saved project layout remains the actual restored arrangement rather than only a preset name.
_Avoid_: Project revision, project template

**Operational UI Completeness**:
The invariant that every advertised editor surface is implemented as a usable interaction path, including its state model, input handling, validation, feedback, persistence boundary, collaboration behavior, and failure handling; a visual placeholder or presentation-only shell is not considered complete.
_Avoid_: UI shell, mock-only control, unfinished surface

**Rename Input Contract**:
The shared behavior for any rename or name-setting field: open it with the current name populated and its entire text selected, so typing immediately replaces the old name while still allowing partial edits.
_Avoid_: Destructive rename, placeholder text

**Focused Text Selection**:
The selection state in which a text input owns focus and its existing name content is selected on open, with keyboard typing, clipboard operations, and caret movement remaining available.
_Avoid_: Selected keyframe, panel focus

**Command Palette**:
A searchable, focus-aware command surface that exposes available editor actions, their categories, shortcuts, and disabled reasons without requiring a fixed toolbar location.
_Avoid_: File menu, chat command

**Shortcut Routing**:
The focus-sensitive dispatch rule that sends a key gesture to text editing, numeric editing, the timeline, the viewport, a menu, or the global command system according to the active Panel Focus.
_Avoid_: Global key hook, mouse coordinate

**Shortcut Profile**:
A player-local, configurable mapping of editor commands to key gestures, with immediate conflict detection, explicit conflict resolution, scope-aware routing, and restore-default support independent of project content. A conflicting change is not persisted until the user cancels, removes the previous binding, or explicitly swaps the bindings; the editor never silently overwrites an existing command.
_Avoid_: Project operation, server protocol

**Shortcut Scope**:
The interaction boundary in which a shortcut is eligible: global editor, viewport, timeline, inspector, menu, or focused text/numeric input. A more specific active scope takes precedence over a broader scope, while text and numeric editing retain ownership of their editing keys before command dispatch.
_Avoid_: Permission scope, project scope, screen coordinates

**Shortcut Conflict Resolution**:
The explicit local decision required when a proposed shortcut conflicts with another active binding or a verified Minecraft key mapping. The user may cancel, remove the previous binding, or swap the two editor bindings when the scopes are compatible; unresolved conflicts are not written to the Device Preference Store.
_Avoid_: Silent override, last-writer-wins keymap, server conflict

**Auto Key**:
An explicit editor mode in which changing an animatable property at the current playhead creates or updates that property's keyframe at that time, while the accepted change remains an ordinary server-ordered edit and Undo/Redo target.
_Avoid_: Playback sampling, implicit project save

**Auto-Key Edit**:
The Edit Gesture Transaction produced by one Auto Key property change, including any newly created track, inserted or updated keyframe, duration auto-extension, and linked-channel edits needed to represent the user's gesture.
_Avoid_: Runtime camera update, separate undo stack

**Numeric Property Inspector**:
The precise editor surface for inspecting and changing an animatable camera value with a unit label, bounded drag adjustment, direct text entry, clipboard actions, reset, keyframe control, and a visible distinction between authored and evaluated values.
_Avoid_: Slider-only control, runtime state panel

**Contextual Property Inspector**:
The responsive right-side editor panel whose visible content is derived from the local Selection Set. It keeps a fixed Inspector Target Header, renders only fields meaningful for the selected target or complete compatible selection, and becomes a real drawer when the workspace cannot preserve both its usable width and the Minimum Usable Region. It does not duplicate Animation Properties, command text editing, or project settings.
_Avoid_: Animation Properties Popup, universal property dump, fixed-coordinate sidebar

**Inspector Target Header**:
The fixed identity area at the top of the Contextual Property Inspector that identifies the selected target count, semantic type, name, owning animation, and current editable, read-only, or lock condition without becoming an action toolbar.
_Avoid_: Breadcrumb navigation history, hover action strip, project tab

**Compatible Inspector Selection**:
A Selection Set whose targets expose a field with the same semantic meaning, unit, validation contract, and mutation behavior. Only compatible fields may be batch-edited; a heterogeneous selection exposes only genuinely shared fields and never coerces unrelated values into one control.
_Avoid_: Same visual node shape, same timestamp alone, implicit unit conversion

**Mixed Inspector Value**:
The explicit non-value state shown when a compatible multi-selection contains different accepted values for one shared field. Committing a new value replaces that field across the complete compatible target set as one atomic Inspector Batch Edit; leaving it untouched preserves every original value.
_Avoid_: Zero, average, first-selected value, indeterminate server state

**Inspector Batch Edit**:
One valid committed field change applied to every target in a Compatible Inspector Selection, submitted as one Edit Gesture Transaction and one personal Undo/Redo item. An invalid field draft, cancellation, conflict, or server rejection changes none of the selected targets.
_Avoid_: Per-target operation burst, partial batch acceptance, local-only mutation

**Authored Value**:
The value stored by the selected project track or keyframe before interpolation, modifiers, partial-tick sampling, or preview context are applied.
_Avoid_: Evaluated value, base value

**Evaluated Value**:
The value produced at the current timeline time after interpolation and applicable preview context are evaluated, shown for inspection without implying that it is directly stored as a keyframe value.
_Avoid_: Authored value, runtime capability state

**Unit-Aware Input**:
A numeric editing interaction that communicates the semantic unit and sign convention of a camera channel, rejects invalid input visibly, and preserves precision without converting offsets into misleading percentages.
_Avoid_: Generic number field, hidden coercion

**Curve Segment**:
The interpolation definition for exactly one interval between two neighboring keyframe identities, with an explicit Curve Interpolation Mode, normalized control data when applicable, and an unambiguous start and end key reference.
_Avoid_: Keyframe value, whole-track curve

**Curve Interpolation Mode**:
The algorithm used to evaluate one Curve Segment: `EASING_PRESET`, `BEZIER`, `LINEAR`, or `CONSTANT`. `EASING_PRESET` references an existing `Easing` value; `BEZIER` evaluates authored control points; `LINEAR` and `CONSTANT` do not expose editable Bézier handles.
_Avoid_: Tangent coupling, playback mode, visual curve style

**Segment Boundary**:
The keyframe identity and timeline position that terminates one Curve Segment and begins the next; changing it may require deterministic split or merge behavior.
_Avoid_: Track boundary, project duration

**Tangent Mode**:
The local coupling rule for Bézier handles at a keyframe boundary: `AUTO`, `ALIGNED`, `MIRRORED`, or `BROKEN`. `AUTO` calculates handles from neighboring authored keys, `ALIGNED` keeps both sides collinear with independently adjustable lengths, `MIRRORED` keeps them collinear with matched lengths, and `BROKEN` keeps both sides independent. It is not a Curve Interpolation Mode.
_Avoid_: Easing preset, linear interpolation, constant interpolation

**Cross-Segment Tangent Edit**:
One curve-edit gesture that changes handles in more than one adjacent Curve Segment because a boundary uses `AUTO`, `ALIGNED`, or `MIRRORED` coupling; every affected segment previews, validates, commits, and enters Undo/Redo as one atomic operation.
_Avoid_: Hidden neighbor mutation, independent segment edit

**Legacy Curve Mapping**:
The conversion rule that maps a legacy keyframe's easing value to the Curve Segment ending at that keyframe, preserving the existing target-keyframe interpolation meaning.
_Avoid_: Format migration warning, runtime easing state

**Trajectory-Preserving Key Insertion**:
An explicit insertion strategy that samples the existing curve at the new time and splits its Curve Segment so the evaluated animation path remains unchanged until the inserted key is deliberately edited. It is not the implicit behavior of ordinary timeline insertion for a new Timeline Bézier Node, which uses deterministic node data and does not secretly fit the existing chain.
_Avoid_: Hidden fitting, flat insertion, nearest-key duplication

**Transform Write Target**:
The explicit scalar-track family that receives a 3D viewport transform edit: world translation (`TRANSLATION_X/Y/Z`) or camera-relative translation (`RELATIVE_X/Y/Z`). It is independent from the visual orientation of the manipulation gizmo.
_Avoid_: Gizmo orientation, evaluated position

**Gizmo Orientation**:
The coordinate basis used to draw and interpret manipulation axes, such as world, Preview Stage, or camera-local orientation; it does not decide which track family receives the edit.
_Avoid_: Transform write target, camera space track

**Binding Conflict**:
The safe unresolved state in which a viewport selection contains incompatible transform write targets or lacks an explicit target; editing is blocked until the user chooses the destination track family.
_Avoid_: Runtime ambiguity, collaboration conflict

**Transform Binding Feedback**:
The coordinated visual state that distinguishes the active transform write target through selected tracks, trajectory contributions, gizmo handle treatment, and conflict rendering without attaching a text label or mode badge to the gizmo.
_Avoid_: Gizmo orientation label, collaborator marker

**Gizmo Edit Target**:
The authored object a 3D manipulation gesture affects, derived implicitly from keyframe-node selection: compatible selected keyframes when any node is selected, otherwise the evaluated camera pose at the current playhead time.
_Avoid_: Transform write target, gizmo orientation

**Playhead-Targeted Edit**:
A gizmo edit made with no keyframe node selected. Auto Key converts the gesture into authored keys at the playhead; without Auto Key, the gesture remains a non-authoring ghost preview until explicitly confirmed.
_Avoid_: Selected-keyframe edit, timeline seek

**Selected-Keyframe Edit**:
A gizmo edit made while one or more keyframe nodes are selected, restricted to compatible authored channels in that selection rather than silently redirecting the gesture to the playhead.
_Avoid_: Playhead-targeted edit, selection-only preview

**Ghost Transform Preview**:
The temporary visual result of a playhead-targeted gizmo gesture while Auto Key is disabled, showing the proposed camera transform without changing the project document until an authoring action is confirmed.
_Avoid_: Auto-Key edit, collaborator presence

**Curve Split Strategy**:
The selected rule for dividing an existing Curve Segment when a keyframe is inserted, including trajectory-preserving, preset-inheriting, and flat-tangent alternatives.
_Avoid_: Curve merge, keyframe interpolation mode

**Authoritative Revision**:
The latest server-accepted state of an editing session, identified by a monotonically increasing revision number.
_Avoid_: Client copy, local version

**Operation Journal**:
The project-local, server-ordered durable sequence of accepted Edit Operations newer than the latest completed Project Snapshot, used to recover acknowledged revisions without rewriting the full document for every gesture.
_Avoid_: Personal edit history, audit chat log

**Project History and Recovery Panel**:
The responsive, dockable, read-only File-menu surface that shows the finite server-retained history of accepted project revisions and atomic operations, including operator, event time, operation type, affected scope, persistence state, and optional read-only differences. Owner and Editor may inspect it and filter by operator, operation type, time range, checkpoint, or restore event, but it never exposes local drafts, command input history, creates a revision, executes personal Undo/Redo, or changes preview and runtime state.
_Avoid_: Personal Edit History, chat log, command history, mutable revision editor

History records are delivered through server-side cursor pagination. The panel initially loads only a bounded page, requests later pages as the user approaches the list end, and discards and restarts pagination when filters change. Difference summaries and detail are loaded only when requested, while the client keeps only a finite cache of loaded pages and releases it when the panel closes.

**History Record Availability**:
The explicit display state of a retained history record whose operation metadata remains inspectable while its referenced checkpoint, snapshot, or detailed diff is no longer retained. Such a record is shown as `Unavailable` and is never completed from another revision or approximated by the client.
_Avoid_: Reconstructed history, hidden deletion, inferred diff

**Project Snapshot**:
An atomically committed, versioned serialization of a complete Project Document at a specific Authoritative Revision, used as the durable base for loading and recovery.
_Avoid_: Project Workspace Snapshot, preview revision snapshot

**Pinned Checkpoint**:
A checkpoint explicitly protected from automatic retention cleanup by the project owner until it is manually unpinned or deleted through the owner-authorized lifecycle.
_Avoid_: Active project snapshot, permanent backup

**Checkpoint Reference**:
A stable ID or snapshot reference held by an eligible restore, protection, preview, recovery, or history record, which prevents the referenced checkpoint from automatic deletion while the reference remains live.
_Avoid_: In-memory document copy, UI selection

**Pending Cleanup**:
The retention state of an otherwise deletable checkpoint or journal resource that is waiting for all active references to be released before physical cleanup.
_Avoid_: Corrupt snapshot, active edit lock

**Unavailable History Action**:
A retained Undo, Redo, or restore record whose required checkpoint or stable target is no longer available; it remains inspectable but cannot be executed or approximated from another version.
_Avoid_: Undo conflict, silently pruned action

**Durable Checkpoint**:
An explicit or lifecycle-triggered request to flush the project's accepted operation journal and create an atomic Project Snapshot without introducing a private client-side saved-versus-unsaved state or submitting local content drafts. File > Save and `Ctrl+S` are the ordinary user-facing triggers; Named Project Checkpoint creation adds a separately retained, user-identifiable version after this flush.
_Avoid_: Editor close, legacy export

**Project Save**:
The Lock-Neutral File > Save or `Ctrl+S` operation that flushes accepted project content and requests an atomic Project Snapshot without creating a named retention entry, confirming local drafts, changing project content, or entering personal Undo/Redo.
_Avoid_: Named Project Checkpoint, universal confirm shortcut, client-side save buffer

**Named Project Checkpoint**:
The user-facing checkpoint created as an internal Git-like label pointing to one server-accepted Revision Commit Node, with its own stable identity, name, description, creator, creation time, and retention reference. Its target node is immutable: changing the checkpoint name or description never retargets it, and creating, renaming, or deleting the checkpoint is not a project-content edit and does not create a separate editable branch. The UI calls it “检查点”, not a generic “标签”.
_Avoid_: Ordinary save, Revision Commit Node, personal undo item

**Checkpoint Metadata Edit**:
The server-ordered change of a Named Project Checkpoint's name or description by either the Project Owner or a Project Editor. It uses the current metadata version as its baseline, never changes the immutable Checkpoint Label Target, creates no project-content revision, and does not enter personal Undo/Redo; a concurrent stale edit is rejected and refreshed rather than merged or silently overwritten.
_Avoid_: Checkpoint retarget, project content edit, last-writer-wins rename

**Checkpoint Label Target**:
The immutable Revision Commit Node reference stored by a Named Project Checkpoint. It is selected once when the label is created and remains the only valid restore and comparison target for that label until the label is deleted; a new target requires a new label.
_Avoid_: Movable branch pointer, current project head, mutable tag target

**Checkpoint Name Identity**:
The project-wide, trimmed and case-insensitive identity of a Named Project Checkpoint name. Visible names must be non-empty and unique within one project; a duplicate is rejected with an inline explanation and suggested available name rather than silently selecting a different checkpoint.
_Avoid_: Stable checkpoint UUID, display-only duplicate name, global cross-project name

**Historical Checkpoint Creation**:
The explicit creation of a Named Project Checkpoint on an available historical Revision Commit Node from the Project History and Recovery Panel. The server exposes the checkpoint only after that revision has a validated durable reconstruction anchor; an unavailable or unverifiable revision cannot receive a new checkpoint.
_Avoid_: Branch creation, revision restore, client-only bookmark

**Shared Checkpoint Target Retention**:
The reference-sharing rule for multiple Named Project Checkpoints that point to the same Revision Commit Node. They share one durable reconstruction anchor without duplicating project content, while each checkpoint retains independent metadata and pin state; deleting one checkpoint never deletes the target while another live reference remains.
_Avoid_: Duplicate snapshot per checkpoint, shared checkpoint metadata, cascade deletion

**System Recovery Point**:
The server-created protected historical reference used for migration protection, pre-restore protection, or crash recovery. It is shown only in the Project History and Recovery Panel with a system indicator, not mixed into the ordinary user checkpoint list, and cannot be renamed or manually deleted while its recovery reference remains active.
_Avoid_: Named Project Checkpoint, periodic hidden snapshot, ordinary user tag

**Revision Commit Node**:
The immutable, lightweight historical node created for every accepted atomic project edit, including its parent authoritative revision, operation metadata, affected scope, and content fingerprints. It does not duplicate the complete Project Document in the history record and may be referenced by a Named Project Checkpoint.
_Avoid_: Named Project Checkpoint, full document snapshot, mutable project tab

**Single-Mainline Checkpoint History**:
The Git-like project history model in which every accepted edit appends one Revision Commit Node to one authoritative mainline and Named Project Checkpoints annotate nodes on that line. The first version does not provide branches, branch switching, merge, or cherry-pick; restoring an older labeled node creates a new mainline revision rather than moving the head backward.
_Avoid_: Full Git repository, forked editing session, destructive rewind

**Checkpoint Creation Dialog**:
The responsive Blockbench-inspired popup used to name and optionally describe a Named Project Checkpoint; the default name is selected on open, local draft text is not project content, and confirmation creates the checkpoint only from accepted server state.
_Avoid_: Animation Properties Popup, migration prompt, content edit dialog

**Checkpoint Management Panel**:
The responsive dockable File-menu surface that lists Named Project Checkpoints and their referenced Revision Commit Nodes, and exposes inspection, creation, naming, pinning, restore, and deletion actions according to Owner/Editor permissions and current reference state. It presents parent and mainline relationships as read-only history context and does not expose branch or merge controls.
_Avoid_: Operation Journal viewer, project revision timeline, read-only animation track

**History and Recovery Menu Entry**:
The File-menu action that opens the Project History and Recovery Panel without changing the active project tab, playback, selection, editor preview, or runtime camera. It is available to both Owner and Editor, while any restore action inside the panel must enter the normal Checkpoint Restore Preflight and permission flow.
_Avoid_: Undo/Redo command, project reload, checkpoint restore shortcut

**History Cursor Page**:
A bounded server response containing one ordered slice of Project History and Recovery Panel records plus the opaque cursor needed to request the next slice under the same filter set. A cursor is invalidated when the filter or project session baseline changes; the client never assumes that a missing page is an empty history range.
_Avoid_: Full history download, client-generated pagination, permanent page cache

**Checkpoint Restore Preflight**:
The server-authoritative validation step before a checkpoint restore that checks permission, checkpoint availability, current project baseline, resource references, and affected Command Event Lock Leases before any lock drain or project mutation.
_Avoid_: Client confirmation only, runtime publication, ordinary Undo

**Checkpoint Difference Summary**:
The bounded comparison between the current accepted project state and a selected checkpoint, presented before restore so the user can understand structural and authored-content consequences without exposing local drafts or silently applying changes.
_Avoid_: Full document diff dump, live collaborative preview, revision rewind

**Checkpoint Difference Detail**:
The expandable, grouped read-only portion of a Checkpoint Difference Summary that lists affected animations, scalar tracks, keyframes, curve segments, command events, markers, and animation properties with concise before-and-after values.
_Avoid_: Editable inspector, unbounded JSON dump, collaborator draft view

**Checkpoint Target Preview**:
The client-local, read-only preview of a selected checkpoint used to inspect its trusted accepted content before restoration; it never changes the active project, joins collaboration, publishes runtime state, or includes local drafts.
_Avoid_: Restore, collaborative preview, runtime instance

**Retention Budget**:
A server-authoritative finite allowance for project snapshots, checkpoint metadata, journal storage, and personal history; cleanup prefers the oldest unpinned and unreferenced resources after valid recovery coverage is preserved.
_Avoid_: Unlimited archive, client cache size

**Journal Compaction**:
The removal or archival of Operation Journal entries already covered by a successfully committed Project Snapshot, keeping recovery storage bounded without discarding newer accepted revisions.
_Avoid_: Undo-history clearing, animation trimming

**Crash Recovery**:
The deterministic reconstruction of a Project Document by loading the newest valid Project Snapshot and replaying later Operation Journal entries in revision order.
_Avoid_: Undo, workspace restoration

**Recovery-Required State**:
The visible protected state entered when snapshot validation or journal replay encounters corruption, a revision gap, or an operation that cannot be applied safely, preventing silent partial recovery.
_Avoid_: Synchronization delay, archived project

**Presence State**:
Ephemeral participant information such as cursor position, selected track, selected keyframe, viewport, and playhead focus that does not change animation content.
_Avoid_: Animation data

**Editor Workspace State**:
A player's non-content view state for one project, such as the last opened editor location, timeline viewport, playhead time, and other UI navigation values that may be restored only within the same connected server lifecycle. It is keyed by both player and project, so switching projects never reuses another project's view state.
_Avoid_: Animation content, project document, durable project data

**Project Workspace Snapshot**:
The temporary workspace-state record belonging to one player and one animation project, restored after an explicit editor close during the same connected lifecycle and discarded when that lifecycle ends.
_Avoid_: Project snapshot, animation revision

**Explicit Editor Close**:
The normal user action that closes the current camera animation editor page and checkpoints that player's editor workspace state for the current project.
_Avoid_: Disconnect, world leave, server shutdown

**Connection Lifecycle Exit**:
The definitive end of the current connected editor lifecycle through leaving the world or server, explicit disconnection, server/world shutdown, returning to a disconnected screen, cancellation or expiry of reconnection, or rejection of the prior session identity. It clears session-scoped editor workspace state, retained drafts, personal shortcut history, notices, and presence rather than preserving them for a future connection.
_Avoid_: Explicit editor close, Transient Connection Interruption, project archive

**Fresh Connection Re-entry**:
A later world or server entry after Connection Lifecycle Exit that starts a new editor lifecycle with no restored project tabs, project workspace state, drafts, personal history, Presence, locks, pending operations, preview playback, or runtime instances. Projects are opened explicitly from current server state, while only player-local Global Editor Preferences may carry across connections.
_Avoid_: Reconnect continuation, automatic project reopen, durable UI session

**Transient Connection Interruption**:
A temporary loss of collaborative transport while the client remains in the same editor and world context and a bounded reconnection attempt for the same connected lifecycle is still active. It freezes server-authoring actions without becoming a Connection Lifecycle Exit or persisting state into a future connection.
_Avoid_: Explicit disconnect, server switch, offline editing mode, durable reconnect recovery

**Reconnecting Read-Only State**:
The editor state entered during a Transient Connection Interruption. It retains project tabs, playhead, panel layout, local drafts, and local browsing or preview context, but disables every operation that would submit, publish, lock, checkpoint, or otherwise mutate server-authoritative state.
_Avoid_: Older-format read-only project, spectator permission, offline project copy

**Reconnect Revalidation**:
The server-authoritative reconciliation required before leaving Reconnecting Read-Only State. It refreshes permission, current project revision, object identities, pending-operation outcomes, and lock validity, then either resumes editing or requires an explicit refresh, discard, or re-edit choice for a stale retained draft.
_Avoid_: Blind retry, automatic draft submission, last-writer-wins reconnect

**Retained Reconnect Draft**:
A client-local uncommitted property, marker, command, or equivalent authoring draft held only through a Transient Connection Interruption. It never preserves an active server lease, never submits automatically, and is discarded on Connection Lifecycle Exit unless Reconnect Revalidation explicitly permits a new edit flow.
_Avoid_: Accepted revision, durable offline draft, renewed command lock

**Reconnect Draft Review Surface**:
The single bounded review surface shown after Reconnect Revalidation discovers stale or conflicting Retained Reconnect Drafts, or after Explicit Permission Rejoin restores access while Permission-Revoked Draft Material remains. It compares each retained item with the freshly authorized project state and keeps the editor read-only until every listed draft receives an explicit resolution.
_Avoid_: One-dialog-per-draft prompt storm, automatic merge, hidden draft loss

**Reconnect Draft Resolution**:
The explicit per-draft choice made in a Reconnect Draft Review Surface: refresh from the authoritative value, keep the retained value as a new local re-edit draft, or discard the retained draft. Resolution never submits content automatically, renews a lock implicitly, or changes the authoritative project until a new ordinary edit is deliberately confirmed.
_Avoid_: Force overwrite, automatic resubmission, accepted revision

**Project Format**:
The versioned document format that preserves the complete collaborative animation model and editor extensions for an animation project.
_Avoid_: Extended camera animation JSON, legacy-compatible animation JSON, resource animation

**Project Format Version**:
The explicit compatibility identity carried by every Project Format document, used to determine whether the document is directly editable, requires a supported migration, or is newer than the current server can safely understand.
_Avoid_: Project revision, mod version, runtime JSON version

**Project Format Migration**:
The server-authoritative, all-or-nothing structural conversion of a supported older Project Format document into the current Project Format before an editable collaboration session begins.
_Avoid_: Runtime JSON import, partial field repair, client-side upgrade

**Project Migration Prompt**:
The explicit Blockbench-inspired modal decision shown before migrating a supported older project, presenting its current and target format versions, protection-checkpoint guarantee, warnings, and the actions Migrate and Open, Open Read-Only, and Cancel.
_Avoid_: Silent auto-upgrade, generic error popup, runtime JSON import dialog

**Migration Progress Surface**:
The bounded modal status view that reports the authoritative migration phase without inventing a precise percentage, then transitions to success, retryable failure, read-only opening, or cancellation while retaining the original project on failure.
_Avoid_: Background toast-only migration, fake progress bar, partial editor session

**Migration Protection Checkpoint**:
The durable exact pre-migration project document retained before a Project Format Migration replaces the active project file, allowing recovery and a defined compensating history action without relying on an in-memory duplicate.
_Avoid_: Restore protection checkpoint, workspace snapshot, migrated result

**Migration Undo Boundary**:
The rule that a migration can be compensated only while it remains the latest accepted project change; compensation restores the pre-migration document and leaves it read-only until a supported migration is explicitly performed again.
_Avoid_: Schema downgrade edit, ordinary keyframe undo, silent session rewind

**Older-Format Read-Only State**:
The protected project-open state shown for a supported older Project Format document when migration has been declined or undone; it permits safe inspection and an explicit migration request but no collaborative editing, runtime publication, or silent format rewrite.
_Avoid_: Newer-format incompatibility, failed migration, editable compatibility mode

**Newer-Format Read-Only State**:
The protected project-open state used when a document declares a Project Format Version newer than the server supports; it exposes safe metadata and diagnostics without migrating, rewriting, publishing, or editing the document.
_Avoid_: Failed migration, editable compatibility mode, silent downgrade

**Read-Only Inspection Tab**:
The non-editing Project Tab used to inspect a supported older document or the trusted metadata of a newer unsupported document without joining an Editing Session, publishing runtime content, or creating collaborative presence.
_Avoid_: Inactive editable tab, read-only participant, shared preview session

**Older-Format Local Preview**:
The client-only inspection preview available when an older Project Format document can be parsed safely; it permits timeline navigation and visual evaluation without mutating, synchronizing, publishing, or enabling the animation for gameplay.
_Avoid_: Collaborative Preview, runtime instance, migration result

**Existing Runtime Camera Animation JSON**:
The current per-channel resource JSON consumed by `CameraKeyframeAnimation.JSON_CODEC` and `StaticCameraAnimationReloadListener`. It stores `name`, `animType`, optional `duration`, and a `keyframes` map whose entries contain `timestamp`, `endPoint`, and `easing`; the `ModifierType` channel is supplied by the containing resource directory rather than by the JSON object. This is the format informally called legacy JSON in compatibility discussions: legacy means older than the new project document, not removed or inherently unsupported.
_Avoid_: Project document, marker-bearing format, self-describing multi-channel file, deprecated-by-name assumption

**Extended Camera Animation JSON**:
The versioned per-channel camera-animation JSON that retains all existing animation fields and adds optional lossless custom Curve Segment data. Files without the extension retain their original `easing` meaning, while files with custom curve data keep `easing` as the explicit compatibility fallback.
_Avoid_: Project format, modifier-owning document, legacy-compatible export

**Legacy Animation Format**:
The unversioned, easing-only compatibility profile of Existing Runtime Camera Animation JSON used when targeting readers that do not evaluate custom Curve Segment data. The label describes compatibility age relative to the project document; it does not by itself mean the format is unavailable or removed.
_Avoid_: Extended camera animation JSON, project format, project document, obsolete-file assumption

**Modifier Context**:
The explicit `ModifierType` destination or source context that binds an extended or legacy-compatible camera-animation JSON file to one scalar camera channel because the per-channel payload itself does not carry that field.
_Avoid_: Value-based inference, JSON modifier field

**Legacy Import Binding**:
The validated rule that maps one legacy JSON file to the selected scalar track, an explicitly chosen `ModifierType`, or a verified modifier-named directory during import.
_Avoid_: Automatic channel guessing, default-channel fallback

**Native Multi-Channel Project**:
The lossless project representation containing all supported scalar camera tracks and their Additive Lanes in one collaborative document, distinct from each per-channel extended or legacy-compatible camera-animation JSON file.
_Avoid_: Legacy animation file, single-channel export

**Format Conversion**:
An explicit, validated import or export between the project format and either extended or legacy-compatible camera-animation JSON, including warnings whenever the selected target profile cannot represent a source concept.
_Avoid_: Save, synchronization

**Batch Import Transaction**:
The validated project operation or explicitly segmented operation set that imports a directory of legacy files only after all files, Modifier Context destinations, conversion warnings, and resource budgets have been checked; it never silently applies a partial subset.
_Avoid_: Best-effort import, unreported partial mutation

**Scalar Animation Track**:
The authoritative animation channel for exactly one camera modifier value. Each supported modifier type owns its own scalar track, and the track evaluates its named Additive Lanes independently before summing their results.
_Avoid_: Track group, vector track, flattened legacy group

**Additive Lane**:
A named, lane-local keyframe sequence inside one Scalar Animation Track. It preserves one legacy `keyframes` group, evaluates independently, and contributes its result additively to the scalar track value.
_Avoid_: Track Group, merged keyframe list

**Legacy Group Lane**:
An Additive Lane created by importing one legacy camera-animation `keyframes` group, retaining that group's name and independent interpolation sequence for round-trip export.
_Avoid_: Visual folder, automatic merge

**Lane Accumulation**:
The evaluation rule that sums the independently evaluated results of all active Additive Lanes belonging to one Scalar Animation Track.
_Avoid_: Keyframe blending, vector interpolation

**Track Group**:
A visual and organizational collection of related scalar animation tracks, used for navigation and coordinated editing without becoming a separate animation value.
_Avoid_: Scalar animation track, parent animation

**Camera Track Tree**:
The current animation's timeline hierarchy, organized as four sibling Track Groups: Position, Rotation, Camera Parameters, and Animation Effects. Position directly contains `X/Y/Z`, `TRANSLATION_*`, and `RELATIVE_*`; Rotation contains `ROTATION_*`; Camera Parameters contains `FOV`, `ZOOM`, and `RAYCAST`; Animation Effects contains Command Animation Effect Event rows only.
_Avoid_: Base Position group, camera parameters nested under position

**Linked Channel Edit**:
One editing gesture that produces coordinated edit operations across multiple scalar animation tracks, such as moving three translation channels together, while preserving their independent stored values.
_Avoid_: Vector track, merged keyframe

**Easing Preset**:
A named interpolation function, including the existing `Easing` values, that can be selected as a compatible curve preset for a keyframe interval.
_Avoid_: Curve data, playback mode

**Custom Curve Segment**:
The first-class authored interpolation data for one interval between neighboring keyframes, including its curve mode and optional Bézier control points; it is persisted, collaboratively synchronized, evaluated by the camera runtime, and representable in Extended Camera Animation JSON, but may not be representable in the Legacy Animation Format.
_Avoid_: Keyframe value, track group

**Curve Downgrade Warning**:
An explicit conversion warning shown when a custom curve cannot be represented by the legacy animation format and would need to be replaced by a compatible easing preset or omitted.
_Avoid_: Validation error, synchronization conflict

**Timeline Time**:
The project-wide logical time measured in seconds and advanced by Minecraft game time, with an exact legacy conversion of one hundred legacy animation units per second.
_Avoid_: Wall-clock time, render frame count

**Display Frame Rate**:
A project editor preference used for ruler labels, timecode, frame numbering, and snapping; changing it does not retime animation content or alter runtime duration.
_Avoid_: Game tick rate, playback speed

**Timeline Snapping**:
An optional editing constraint that aligns time changes to display frames, game ticks, keyframes, markers, or other enabled timeline targets without changing the canonical timeline-time unit.
_Avoid_: Quantized playback, forced frame rate

**Timeline Drag Semantics**:
The mode-dependent meaning of dragging a camera keyframe: compact Timeline Mode changes authored time horizontally while leaving value unchanged, whereas Graph Editor Mode can change time horizontally and the selected scalar authored value vertically. The write target always comes from the selected track's verified field identity, never from spatial guesses.
_Avoid_: Implicit channel inference, ordinary-timeline value editing, mode toggle button

**Graph Editor Mode**:
The visual timeline mode that renders selected scalar camera channels as value-versus-time curves, automatically fits the visible numeric range with useful padding and a visible zero line when relevant, supports manual vertical zoom and pan, allows authored values to be adjusted through vertical keyframe dragging under the normal gesture and Auto Key rules, and lowers the opacity of unselected curves without changing channel semantics.
_Avoid_: Separate animation format, playback-only graph, detached chart

**Timeline Channel Visibility**:
The editor-only filter that controls which channel families are rendered in the current timeline or Graph Editor Mode; the camera editor exposes rotation, position, and command families, while scale is not a supported channel in this design and hiding empty channels is a separate view filter. Changing visibility never mutes, disables, deletes, retimes, or otherwise changes the actual animation effect.
_Avoid_: Runtime channel toggle, track deletion, animation mute

**Timeline Population Command**:
The explicit local workspace action that adds every non-empty scalar track or Command Animation Effect Event row from the currently edited animation to the timeline's logical display set, without duplicating entries or changing project content, playback behavior, or project revisions; channel visibility filters may still keep an added row visually hidden.
_Avoid_: Import animation, create keyframes, automatic data mutation

**Clear Selected Timeline Tracks**:
The destructive project operation that removes all authored keyframes, curve segments, and scalar-track data from the currently selected clearable tracks as one validated collaboration and Undo/Redo transaction, without shrinking Animation Duration or affecting unselected tracks, effect events, markers, animation properties, or workspace state.
_Avoid_: Hide channel, clear timeline view, delete entire animation

**Animation Effect Event**:
In the initial camera editor, this term means a Command Animation Effect Event: a timed, non-keyframe command attached to an Animation Project, rendered as a point in Graph Editor Mode and scheduled for its server-authoritative runtime path when playback reaches its authored time. It is distinct from numeric camera tracks and must be represented deliberately in the project model and runtime JSON/protocol.
_Avoid_: Scalar keyframe, decorative marker, client-only click effect

**Single-Command Event**:
The invariant that each Command Animation Effect Event owns exactly one command payload and produces one command dispatch whenever an eligible playback crossing triggers it. Multiple commands at the same authored time remain separate stable-identity events and use Same-Time Effect Order.
_Avoid_: Multiline command batch, hidden command list, partial event execution

**Command Draft Validation**:
The editing boundary where command text may be temporarily empty, incomplete, or invalid while a player is typing, but only a complete command that passes authoritative validation becomes shared project content. Rejecting a draft preserves the previously shared event, while cancelling a new event discards it without a project revision or Undo/Redo entry.
_Avoid_: Shared invalid command, delayed validation-only-at-runtime, partial event replacement

**Command Event Creation Draft**:
The private client-local candidate created when a participant starts adding a new Command Animation Effect Event. It has only a temporary local identity and becomes a shared stable-identity event, project revision, and personal Undo/Redo item together only after one successful authoritative confirmation; cancellation or connected-lifecycle exit discards it completely.
_Avoid_: Empty shared event, provisional server event, preallocated stable event identity, creation lock

**Command Event Clipboard Snapshot**:
The client-local copy captured from the latest server-accepted content of one or more Command Animation Effect Events. It never contains another participant's uncommitted draft; pasting it creates a new local Command Event Creation Draft at the current playhead time and cannot itself create a project revision, event lock, or shared event.
_Avoid_: Live shared clipboard, draft-inclusive copy, stable pasted event, immediate paste commit

**Batch Command Event Operation**:
A selected set of Command Animation Effect Event mutations evaluated as one server-authoritative all-or-nothing transaction. Read-only operations may include locked events, but any mutation is rejected in full when a selected event is missing, stale, unauthorized, or locked by another participant; a successful batch creates one accepted revision and one personal Undo/Redo item without bypassing or silently skipping a lock.
_Avoid_: Partial batch success, silent locked-item skipping, Owner lock bypass, per-event Undo fragmentation

**Command Event Personal Edit Lock**:
The server-authoritative temporary lock held by one authorized participant while editing one Command Animation Effect Event. It protects the event's complete authored identity and meaning, including command text, authored time, Same-Time Effect Order, deletion, replacement, and any structural operation whose footprint mutates that event. Other participants may still view, select, preview, and inspect diagnostics for the event, but cannot modify any protected part until release; the lock does not block edits to other events or the rest of the project. It is scoped to the event and active Editing Session, is distinct from Collaborator Presence, and does not grant permanent ownership or change project roles.
_Avoid_: Command-text-only lock, project-wide lock, permanent event ownership, lock on mere selection, hidden lock failure, blocking unrelated tracks

**Command Event Lock Lease**:
The server-owned lifecycle record that keeps a Command Event Personal Edit Lock valid only while its holder has that event's properties editor open and remains connected and responsive. Opening the properties editor requests acquisition; confirmation, cancellation, or closing it releases the lease, while authoritative validation failure keeps it active so the holder can correct the draft. Project switching, editor closure, connection exit, server or world shutdown, or heartbeat expiry releases the lease and discards the holder's uncommitted draft. The visible lock state identifies the current holder without exposing that draft.
_Avoid_: Lock on selection, permanent reservation, client-authoritative timeout, draft recovery after disconnect, hidden holder

**Locked Command Event Read-Only View**:
The properties surface shown when a participant opens a Command Animation Effect Event whose Command Event Personal Edit Lock belongs to someone else. It displays the latest server-accepted command text, authored time, Same-Time Effect Order, validation state, and visible lock holder identity, while all mutation controls, completion requests, and confirmation actions remain disabled. It creates no local draft and never exposes the holder's uncommitted text; an explicit `Enter Edit` action may request the lock again after release, and a bounded Command Lock Release Request may ask the holder to finish without transferring ownership.
_Avoid_: Silent failure to open, stale editable copy, holder draft preview, automatic lock stealing, hidden disabled controls

**Command Lock Release Request**:
The ephemeral, non-blocking request sent from a Locked Command Event Read-Only View asking the current holder to finish or release a valid Command Event Personal Edit Lock. It never queues ownership, transfers the lock, exposes either participant's draft, or grants the Project Owner a takeover path. Requests are server-rate-limited, coalesced per requester and event, visible only during the active Editing Session, and disappear when handled, expired, disconnected, or the lease ends.
_Avoid_: Lock wait queue, forced takeover, modal interruption, request spam, persistent notification history

**Lock-Neutral Project Operation**:
A project action such as Durable Checkpoint creation, export, or Runtime Publication that reads or persists only a specified server-accepted Authoritative Revision and therefore neither waits for nor releases active Command Event Lock Leases. Uncommitted command drafts are excluded from its result and remain private to their holders.
_Avoid_: Draft-inclusive export, implicit lock release, publication of local text, save as draft commit

**Destructive Project Lock Drain**:
The irreversible commit boundary reached only after a Destructive Project Preflight succeeds for a project deletion, archive, checkpoint restoration, replacement import, or removal of a current lock holder's editor permission. At this boundary the server atomically invalidates the affected editing surfaces, releases their leases, discards their uncommitted drafts, and commits the already prepared global operation without opening a race for new mutations.
_Avoid_: Preflight validation, ordinary lock takeover, early draft loss, partial global operation, permanent project lock

**Destructive Project Preflight**:
The reversible owner-confirmed preparation phase that precedes a Destructive Project Lock Drain. A short project mutation barrier blocks new affected leases and mutations while existing leases and local drafts remain intact; the server settles in-flight work and validates authorization, revision, identities, inputs, migrations, budgets, serialization, and every other precondition that can be checked before draft loss. Failure removes the barrier and preserves the same locks and drafts, while success prepares one atomic drain-and-commit transition.
_Avoid_: Early lease release, draft copy on the server, partial validation, permanent editing freeze, destructive action before preparation

**Layered Command Feedback**:
The separation between local typing diagnostics, authoritative submission validation, and bounded runtime-failure diagnostics for a Command Animation Effect Event. Each layer reports only the state it owns: editing feedback never means execution, submission feedback never mutates a rejected event, and runtime feedback never interrupts playback or changes project content. The editor has no separate test-command action in this initial design.
_Avoid_: Test execution while editing, one ambiguous error channel, runtime failure as an edit rejection, unbounded diagnostic history

**Session-Visible Runtime Diagnostic**:
The short, shared diagnostic shown only to the currently connected Project Owner and Project Editors who have the same Animation Project open when a runtime Command Animation Effect Event fails or is cancelled. It is separate from command output: it never appears in the target player's chat or ActionBar, never exposes raw command feedback, never becomes project content, and is retained only within a bounded active-session diagnostic window or aggregate count.
_Avoid_: Player-facing command feedback, persisted project log, unbounded failure list, diagnostic as execution output

**Runtime Diagnostic Panel**:
The non-blocking, responsive status surface for Session-Visible Runtime Diagnostics. Its compact state shows a failure indicator and aggregate count; opening it reveals only the bounded current-session records. A record may identify the event, authored time, failure category, latest occurrence, and coalesced count. Selecting a record focuses its corresponding event in the timeline when the identity is still available, but never seeks the playhead, changes playback, edits project content, or creates history; unavailable records remain inspectable without blocking the editor. Clearing the panel clears only this session's diagnostic view.
_Avoid_: Modal error dialog, automatic playback jump, command output console, persistent project log, decorative counter

**Runtime Diagnostic Anchor**:
The stable identity and semantic context used to associate a Session-Visible Runtime Diagnostic with the exact Command Animation Effect Event that produced it. It records the event identity, authored track path, authored time, failure category, observed project revision, and an opaque fingerprint of the relevant event meaning without retaining raw command text or command output. A disjoint project revision does not invalidate the anchor; a change to the anchored event semantics makes it expired, while deletion makes it unavailable. Replacement events receive new identities rather than inheriting an old diagnostic anchor.
_Avoid_: Revision-only lookup, raw command log, current selection, automatic rebinding, reused event identity

**Diagnostic Binding State**:
The current relationship between a Runtime Diagnostic Anchor and the project: `LOCATABLE` when the same event identity and anchored meaning still match, `EXPIRED` when the event exists but its relevant authored meaning changed, and `UNAVAILABLE` when the event no longer exists. Only a locatable diagnostic may focus a timeline event; expired and unavailable records remain readable but never redirect to another event.
_Avoid_: Best-effort replacement lookup, stale selection, deleted-event substitution

**Command Text Normalization**:
The input rule that accepts one optional leading `/` for user convenience, trims surrounding whitespace, and stores the shared command payload without that leading slash. Command-internal spacing, quoting, selector arguments, and other meaningful characters are preserved; repeated leading slashes are invalid rather than guessed.
_Avoid_: Two stored forms for one command, aggressive whitespace rewriting, chat-command execution during editing

**Vanilla-Style Command Input**:
The command-entry experience that intentionally feels like Minecraft's command-block field and player chat command field: a focused single-line command editor with familiar cursor movement, text selection, insertion, replacement, optional leading slash, completion, and inline syntax feedback. This describes the editing interaction and visual language only; it does not execute a command locally, grant player chat behavior, or relax server-authoritative validation and shared-content rules.
_Avoid_: Local command execution, multiline event payload, client-only validation, chat message submission

**Command Draft History Boundary**:
The initial command event editor does not maintain or recall a command history. Because editing never executes a command or submits chat, closing the properties editor clears its uncommitted draft and does not add the text to a history store; reopening shows only the latest server-accepted event. While the completion menu is open, `Up` and `Down` navigate its transient suggestions rather than recalling text from another event, player, project, or session.
_Avoid_: Chat history clone, cross-event command leakage, persistent draft archive, arrow-key history recall

**Command Completion**:
The non-authoritative editing aid that requests command and argument suggestions from the server using the same command tree, runtime context, and fixed permission policy as command validation. Suggestions may update or insert only the local command draft; they never execute commands, create project content, or replace final validation.
_Avoid_: Client-only command registry, completion as execution, completion as shared content, stale suggestion as validation

**Command Completion Menu**:
The transient completion surface that appears after debounced command-text changes while the command field has focus and can be requested immediately with `Ctrl+Space`. It supports keyboard selection, `Tab` or `Enter` acceptance, `Esc` dismissal without discarding the draft, and invalidation when the cursor, draft, or server response context changes. An unavailable or failed request leaves the field usable as ordinary text editing.
_Avoid_: Persistent suggestion panel, empty placeholder menu, stale insertion, completion failure blocking editing

**Completion Replacement Range**:
The server-provided text interval identifying exactly which portion of the local command draft a completion replaces. Acceptance preserves all text outside that interval, including meaningful spacing, quoting, selector arguments, and text before or after the cursor. If no valid interval is returned, the editor falls back to the current cursor word only; the first `Enter` accepts an open suggestion and a later `Enter` confirms the properties dialog.
_Avoid_: Whole-line replacement, whitespace-token guessing as the primary rule, accepting stale ranges, Enter executing a command

**Effect Event Crossing**:
The forward-playback transition across a Command Animation Effect Event's authored timestamp that makes the command eligible to trigger once in the current playback pass. Scrubbing, seeking, backward movement, pausing, terminal holding, and resetting without a new pass do not count as crossings.
_Avoid_: Seek catch-up, scrub trigger, backward retrigger, terminal repetition

**Effect Playback Pass**:
One continuous forward traversal of an animation's time domain for command-event trigger purposes. A loop wrap begins a new pass and permits command events to trigger again; a stop, terminal hold, or `STOP_BACK_TO_ZERO` completion does not begin another pass.
_Avoid_: Editor session, project revision, preview range

**Same-Time Effect Order**:
The explicit stable ordering among independent Command Animation Effect Events that share one authored timestamp. Runtime dispatch sorts by time and then this order; Reverse preserves the internal order of events that were already simultaneous, while a retimed group entering an occupied destination is appended after unaffected events as one order-preserving block unless an editor performs an undoable reorder operation. Reordering a locked event is a mutation of that event and is available only to its Command Event Personal Edit Lock holder.
_Avoid_: Event merging, client-derived order, nondeterministic collection order, implicit reverse of equal-time commands, extra timeline hierarchy

**Silent Command Runtime Source**:
The current Project Runtime Instance target player used as the entity and live execution context for one Command Animation Effect Event. The command runs once per crossing with the Command Effect Permission Policy and a suppressed-output source, so command success, failure, and feedback are not returned as command output to that player or the editor UI; a separate Session-Visible Runtime Diagnostic may still report a bounded failure summary to the active editing session.
_Avoid_: One execution per recipient, editor opener as source, chat feedback, raw command output in the editor, client-side command execution

**Command Effect Permission Policy**:
The server-wide command authority used by every Command Animation Effect Event, initially defaulting to permission level `2`. It is independent of the runtime target player's operator status, project role, and event data, and clients cannot raise it.
_Avoid_: Player-inherited OP level, per-event permission, owner-only hidden authority, client-selected permission

**Command Effect Failure Isolation**:
The runtime rule that a failed Command Animation Effect Event ends only that event crossing. It does not interrupt camera playback, roll back or skip neighboring events, retry within the same playback pass, substitute a new source player, or produce unbounded diagnostics.
_Avoid_: Animation abort, same-time rollback, automatic retry, fallback command source, log flooding

**Command Targeting Boundary**:
The rule that Command Animation Effect Events have no independent event-level targeting field. The command payload itself uses the normal server command selector syntax, while the runtime source remains the target player and command dispatch occurs exactly once per playback crossing regardless of how many targets the command resolves.
_Avoid_: Per-target command loop, second command-targeting layer, hidden target substitution, selector-as-execution-count

**Effect Event Editor**:
The responsive Blockbench-style editing surface for creating and changing Single-Command Events as discrete timed points, with stable identity, authored time, Same-Time Effect Order, one normalized command text payload, Command Draft Validation, a complete-event Command Event Personal Edit Lock maintained by a Command Event Lock Lease, a Locked Command Event Read-Only View for non-holders, Command Completion and its transient Command Completion Menu, Completion Replacement Range handling, time dragging, selection, duplication, deletion, and atomic collaboration/Undo/Redo behavior.
_Avoid_: Curve track, decorative point, unvalidated command text

**Timeline Delete Operation**:
The direct, ordinary deletion of the currently selected authored content items, such as keyframes, curve data, or Command Animation Effect Events, submitted as one server-validated and undoable transaction without a confirmation dialog. It preserves the playhead, clears deleted-item selection, keeps the timeline focused, and is rejected as a whole when permissions, locks, revisions, or local command drafts make any selected target unsafe; it does not consume the separate Marker Selection, and broad destructive project operations remain separately confirmed.
_Avoid_: Marker deletion, track clearing, project deletion, implicit duration trimming, partial locked deletion, irreversible delete

**Project Timeline Marker**:
A shared project-level time bookmark created at a valid nonzero time and used to seek the local animation preview; an animation may contain multiple stable-ID markers with editable names and times. Names follow Marker Name Validation and times follow Marker Time Boundary. Markers are synchronized as project metadata and participate in project revisions and personal Undo/Redo, but are not exported into Existing Runtime Camera Animation JSON or client runtime animation files. Activating one seeks immediately and preserves pause state when paused, while an animation that has not started or has already ended enters playback from the marker. Marker editing uses a Marker Selection that is separate from authored content selection; Advanced Retiming may move boundary-inclusive Work Range markers only through its explicit Include Markers option.
_Avoid_: Animation keyframe, runtime event, server playback command, mixed content selection, implicit duration mutation, silently retimed marker

**Marker Resource Budget**:
The server-authoritative finite per-animation marker capacity, initially set to `4096` markers and adjustable through server policy. It applies uniformly to direct creation, property-preserving duplication, project import, batch paste, checkpoint restoration, and any other operation that would increase marker count; exceeding it rejects the complete operation without truncation or automatic deletion.
_Avoid_: Per-player quota, client-only limit, silent truncation, unbounded marker collection

**Set Marker Command**:
The one-step toolbar action that creates a Project Timeline Marker at the current valid nonzero playhead time. It creates no local placeholder or properties draft: one authoritative server transaction validates the request against Marker Resource Budget, assigns stable identity and the next generated default name, creates one project revision and one personal Undo/Redo item, then selects the accepted marker locally without changing playhead or playback state.
_Avoid_: Open-properties-first creation, local ghost marker, time-zero marker, implicit playback change

**Marker Interaction**:
The local interaction contract for Project Timeline Markers: plain left click selects and seeks according to playback state, `Ctrl` plus left click adds or toggles without seeking, `Shift` plus left click removes without seeking, double left click opens the marker properties surface, drag previews a marker-time change locally and submits it on release, right click opens a compact marker menu, and clicking empty marker space clears only Marker Selection. Marker interactions do not add hover-only controls or visual clutter.
_Avoid_: Mixed node selection, drag-to-move-playhead, hover toolbar, implicit content edit

**Marker Properties Draft**:
The private local name-and-time candidate opened for one Project Timeline Marker. It previews only that marker, does not acquire a personal lock or broadcast intermediate values, and is discarded on cancel, `Esc`, popup closure, project switching, editor closure, or connected-lifecycle exit unless one authoritative confirmation commits it.
_Avoid_: Live collaborative typing, command-event lock, draft revision, playhead preview

**Marker Property Commit**:
The atomic project edit submitted from a confirmed Marker Properties Draft. It carries only changed marker fields and its baseline; disjoint concurrent field changes may merge, while a same-field conflict or deleted marker rejects the whole commit and keeps the local draft available for correction. Success creates one project revision and one personal Undo/Redo item.
_Avoid_: Per-keystroke broadcast, forced overwrite, partial marker commit, silent stale retry

**Marker Name Validation**:
The authoritative normalization and validity rule for a Project Timeline Marker name: trim leading and trailing whitespace, reject empty, newline, and control-character results, enforce a finite UI-safe length, and allow duplicate names because stable marker identity—not display text—distinguishes markers. New markers may receive a generated localized default name that remains editable.
_Avoid_: Globally unique display names, hidden whitespace-only names, multiline marker labels, unbounded text

**Marker Time Boundary**:
The validity boundary for a Project Timeline Marker time: it must be finite, strictly greater than animation start, and no later than the current finite Animation Duration. Multiple markers may share a time; marker edits never implicitly extend or shorten duration, and a duration change that would invalidate a marker is rejected unless the same explicit transaction moves or removes every affected marker.
_Avoid_: Automatic clamping, implicit duration extension, marker at time zero, invalid persisted marker

**Marker Selection**:
The local selection domain used only for inspecting, moving, renaming, or deleting Project Timeline Markers. Deleting the selected marker is an immediate server-validated and undoable project edit without a confirmation dialog; it preserves the current playhead and playback state and never joins a keyframe, curve-node, or command-event batch deletion.
_Avoid_: Selection Set, mixed timeline batch, marker activation only, destructive project action

**View Compatibility Policy**:
The author-defined project rule that determines whether playback applies in every camera view, only in first person, or only outside first person.
_Avoid_: Editor viewport, camera origin lock

**Authoring State**:
The durable animation meaning intentionally controlled by editors, including project metadata, playback type, duration, view compatibility, tracks, keys, and interpolation.
_Avoid_: Runtime playback state, editor workspace state

**Runtime Playback State**:
Transient execution data such as current tick, previous tick, stopped status, dirty synchronization status, dynamic source classification, and captured camera origins.
_Avoid_: Authoring state, project document

**Animation Duration**:
The shared length of an animation project, converted to and from the legacy JSON `duration` field and independent of any participant's local preview range. It automatically expands when time-bearing animation content is placed beyond its end, but never automatically shrinks and is never extended by a Project Timeline Marker alone; explicit duration commands are required for shortening or fitting it. For `FOREVER`, it remains a finite authored content length even though runtime playback may hold the terminal result indefinitely.
_Avoid_: Work range, playback cursor, authoring duration

**Duration Auto-Extension**:
The automatic increase of Animation Duration when an inserted, moved, or retimed scalar keyframe, Timeline Bézier Node, or Command Animation Effect Event exceeds the current project end, preventing authored animation content from being silently clipped.
_Avoid_: Work-range expansion, playback loop, marker-driven extension

**Explicit Duration Adjustment**:
A deliberate editor operation that sets Animation Duration manually, fits it to keyframes, trims it to a chosen work range, or sets it to a selected keyframe's end time.
_Avoid_: Automatic shortening, undo reset

**Work Range**:
A player's temporary preview and editing interval for one project, with a start and end time used for looping, selection, preview, and export scope without changing the project's animation duration.
_Avoid_: Animation duration, project revision

**Local Preview**:
Playback of a selected project revision only in the current player's editor preview, without changing project content or forcing another participant's camera state.
_Avoid_: Collaborative Preview, runtime camera session

**Collaborative Preview**:
An explicitly joined review mode that synchronizes a project revision, playhead, playback state, and preview range among participating editors while leaving ordinary players unaffected.
_Avoid_: Real-time collaboration, server-wide playback

**Collaborative Preview Cardinality**:
The server rule that each Project has at most one active Collaborative Preview session at a time. Local Previews remain independent; concurrent start requests are serialized so one creates the session and the others receive a bounded already-active result that can be followed by an explicit join, with no hidden second session or unbounded start queue.
_Avoid_: Per-editor parallel shared previews, automatic merge, queued start

**Collaborative Preview Start Conflict**:
The bounded semantic result shown when a player asks to start a Collaborative Preview for a Project that already has one. It leaves local preview and workspace state untouched, never auto-joins or queues the request, and exposes a separate explicit Join action that revalidates current permission, lifecycle, capacity, and preview-session generation.
_Avoid_: Silent auto-join, hidden state replacement, unbounded retry queue

**Explicit Collaborative Preview Join**:
The player-initiated action that adds the current active Project Tab to an existing Collaborative Preview after the server validates Owner or Editor permission, project lifecycle and format, active-tab identity, participant capacity, and the current preview session. It requires no Preview Leader approval, receives the current shared state, and captures the player's local preview workspace for later Preview Restoration; activating Join is itself confirmation and does not open a second blocking dialog.
_Avoid_: Automatic admission, leader invitation queue, gameplay camera join

**Cancellable Collaborative Preview Join**:
The single-flight Join request state that remains cancellable until the server enters atomic application of accepted shared preview state. Before that boundary, cancellation, tab closure, focus switch, or lifecycle exit releases bounded request data without changing local preview; after it, exactly one terminal result is allowed and stale responses are ignored by connection, tab, project, and request-generation identity.
_Avoid_: Queued joins, partial shared-state application, late response resurrection

**Preview Leader**:
The participant currently controlling play, pause, seek, and preview-range changes for a collaborative preview; leadership is temporary, does not change project ownership, and is never transferred to another participant. If the leader leaves or loses the active editing session, the collaborative preview terminates.
_Avoid_: Project owner, editing lock

**Collaborative Preview Follower State**:
The joined participant state in which shared playback, seek, work-range, and preview-revision controls are read-only and visibly follow the Preview Leader, while local viewport observation, selection, property inspection, and otherwise authorized project editing remain available. Follower actions cannot alter the active Preview Revision Snapshot or request control transfer; the state exists only while its Project Tab is active, and switching to another Project Tab leaves the preview and restores local state instead of retaining a hidden background follower.
_Avoid_: Editing lock, passive spectator with no inspection, hidden control takeover

**Collaborative Preview Termination**:
The server-authoritative end of a Collaborative Preview when its Preview Leader voluntarily leaves, disconnects, loses permission, switches away from the joined active Project Tab, or reaches a connection or project-session lifecycle exit. It transfers no leadership, ends shared playback and control, releases bounded preview resources, and applies Preview Restoration to every remaining participant; a later preview requires an explicit new start or join.
_Avoid_: Leader handoff, orphaned shared playback, runtime stop

**Collaborative Preview Focus Exit**:
The active-Project-Tab boundary that ends a joined participant's preview membership when the player switches to another Project Tab or closes the joined tab. A follower leaves and restores its Local Preview Snapshot; a Preview Leader terminates the whole Collaborative Preview without handoff. Ordinary panel or menu focus changes do not trigger this boundary.
_Avoid_: Background follower, hidden subscription, panel-focus exit

**Collaborative Preview Join Rejection**:
The server-authored terminal outcome of a Join request that cannot be admitted. It leaves local preview, workspace, selection, playback, content, history, and runtime state unchanged; non-retryable causes are explained without Retry, while temporary transport, timeout, or bounded-capacity causes may expose one explicit new-generation Retry action through the status surface.
_Avoid_: Blocking error modal, automatic retry, stale rejection overwriting newer state

**Collaborative Preview Join Retry Gate**:
The bounded server-authoritative single-flight and short-cooldown boundary for manual retries after a retryable Join rejection. It disables Retry during the cooldown, never retries automatically or queues clicks, invalidates state on lifecycle/focus/tab or preview-generation replacement, and requires a new fully validated request identity afterward.
_Avoid_: Retry storm, client-controlled cooldown, stale throttle across sessions

**Preview Restoration**:
Returning a participant to the valid contents of their Local Preview Snapshot after leaving or ending a collaborative preview, including local animation, playhead, playing or paused state, loop state, work range, viewport observation, timeline presentation, and selection where identities still exist.
_Avoid_: Undo, project rollback

**Preview Revision Snapshot**:
The immutable authoritative project revision selected when a collaborative preview starts; later accepted edits remain available in the editor but do not alter the active preview until an explicit synchronized switch.
_Avoid_: Project backup, editing lock

**Collaborative Preview Stage Snapshot**:
The immutable Preview Stage captured when a Collaborative Preview starts. It gives every joined participant the same spatial basis for relative tracks, trajectory rendering, frustums, and lens visualization even when the live Project Document later receives stage metadata edits.
_Avoid_: Player observation camera, live stage mutation, runtime camera snapshot

**Preview Logical Time Alignment**:
The server-authoritative join rule that binds a participant to one Collaborative Preview session generation, revision snapshot, logical playhead, playback state, and bounded time reference. A playing preview starts the joiner at the current projected logical time, a paused preview restores the exact paused time, and small client drift may be corrected locally without slow catch-up, project mutation, or shared-control input.
_Avoid_: Wall-clock guess, visual catch-up animation, local playhead authority

**Preview Revision Switch**:
An explicit server-coordinated action that changes all joined collaborative-preview participants to a newer or selected project revision and defines how their playhead resumes.
_Avoid_: Live reload, undo

**Preview Stage Switch**:
The explicit Preview Leader action that changes all joined participants from the current Collaborative Preview Stage Snapshot to a newly validated project Preview Stage using one synchronized pause/apply/resume transition. It preserves the current logical playhead and work range, resumes only if the preview was playing before the switch, and never implicitly resets time. Ordinary Stage Capture or stage editing never changes the active preview implicitly.
_Avoid_: Automatic stage jump, player teleport, project rollback

**Preview Stage**:
The shared spatial reference for visualizing and previewing camera animation, including world dimension, position, orientation, and optional scene-preview metadata; it gives relative camera channels a deterministic coordinate basis.
_Avoid_: Player position, camera capability state

**Stage Capture**:
An explicit action that records the current editor observer's camera reference as the project's Preview Stage without changing animation tracks.
_Avoid_: Keyframe insertion, player teleport

**Persistent Preview Stage**:
The versioned project-document extension containing the Preview Stage dimension, transform, lens baseline, label, and capture metadata so every later editing session can reconstruct the same spatial reference.
_Avoid_: Player location, workspace snapshot

**Generated UI Icon Fallback**:
The icon-production fallback for a missing camera-editor glyph: try the GPT image2 generation path first; if that invocation fails, use the currently open Cherry Studio application, send the prepared image-generation prompt to its GPT image2 conversation, and wait for the generated texture before continuing. Generated assets remain subject to the editor's size, contrast, and visual-consistency review.
_Avoid_: Silent placeholder, unreviewed icon, unrelated image source

**Blockbench Visual Language**:
The camera editor's visual direction based on the supplied Blockbench reference: dark charcoal workspace surfaces, restrained panel contrast, compact top menus, blue hover and selection feedback, blue selected animation nodes, and clear light text and borders. The reference image is a visual reference rather than an implementation specification; the user's written interaction clarifications are authoritative.
_Avoid_: Pixel-perfect clone, Minecraft default GUI palette

**Clean Timeline Surface**:
The timeline body keeps its track rows visually minimal in every hover and selection state; it does not reveal inline control buttons. Track actions are reached through the global toolbar, context menus, keyboard/focus commands, or the property inspector, while selection feedback remains limited to semantic highlighting and linked visual states.
_Avoid_: Hover action strip, expanded row controls, button-heavy timeline

**Timeline Panel**:
The responsive bottom Core Editor Region containing a compact panel header, the timeline tool and transport clusters, a time readout, a synchronized ruler and lane canvas, and bounded scroll and zoom controls. It row-synchronizes with the independently visible Timeline Track Tree Surface in the default Left Animation Navigation Stack. It follows the Blockbench-inspired dark hierarchy while remaining a fully functional editor surface rather than a visual shell.
_Avoid_: Detached chart window, fixed-coordinate strip, decorative mock timeline

**Timeline Tool Cluster**:
The ordered left-side Timeline Panel actions for Graph Editor Mode, Timeline Channel Visibility, Clear Selected Timeline Tracks, Timeline Population Command, Command Animation Effect Event creation, and Set Marker Command. Each action uses a compact icon, the established delayed button tooltip, and responsive overflow without changing its command semantics.
_Avoid_: Particle or sound event tool, row-local action strip, decorative icon

**Timeline Transport Cluster**:
The compact Timeline Panel controls for previous navigation, Local Preview play or pause, and next navigation, with start, end, exact seek, loop, and Runtime Stop remaining available through the Timeline Menu and shortcuts. The toolbar intentionally omits an ambiguous stop button because Runtime Stop is terminal and not a local preview rewind.
_Avoid_: Runtime publication control, second playback instance, pause-shaped stop

**Timeline Time Readout**:
The compact display beside the timeline ruler that shows the current Timeline Time in the selected device-local display format together with its corresponding display-frame position. Changing its format belongs to Settings and never retimes animation content.
_Avoid_: Animation Duration field, runtime tick counter, editable keyframe value

**Timeline Ruler and Playhead**:
The synchronized time scale and blue current-time indicator spanning the visible lane canvas. The playhead uses a compact head marker and vertical line, remains distinct from selected blue keyframe diamonds, and supports precise seeking without becoming project content.
_Avoid_: Work Range boundary, keyframe node, runtime camera position

**Timeline Track Tree Surface**:
The independently visible auxiliary panel that presents the Current Edited Animation's Camera Track Tree with disclosure state, hierarchy labels, and row alignment synchronized to the Timeline Panel lane canvas. Its default placement is the lower half of the Left Animation Navigation Stack, but it may be hidden, tabbed, drawn out, or restored without closing the Timeline Panel. It keeps operations out of persistent row-local buttons under the Clean Timeline Surface contract.
_Avoid_: Animation Resource Browser, inline plus-button column, independent lane canvas

**Timeline Node Visual State**:
The non-interactive visual encoding of a timeline item: scalar keyframes and Command Animation Effect Events use the same compact diamond node, while selected nodes use Blockbench-inspired blue emphasis. Hovering a node does not add any visual rendering effect, outline, scale change, animation, or inline control. Conflict, collaborator, and playhead states add borders, markers, or overlays without replacing the local selection signal or relying on color alone.
_Avoid_: Button-bearing node, hover highlight, alternate effect-node shape, color-only status

**Track Operation Entry**:
A non-inline route for invoking an operation on a selected track or node, such as the global toolbar, keyboard command, context menu, or property inspector. The entry point exposes real behavior without adding persistent action controls to the clean timeline row.
_Avoid_: Inline row button, hover action strip, decorative affordance

**Validation Focus Result**:
The bounded link from a Validation Report finding to an existing animation, track, lane, node, marker, or field that remains identifiable in the current accepted project. Focusing a result changes only local selection or inspector navigation and never edits, seeks playback, or creates history; an unavailable identity, including one removed after the report became outdated, remains a report-only finding with its focus action disabled.
_Avoid_: Auto-fix target, runtime failure anchor, hidden selection mutation

**Semantic UI Color Token**:
A named role-based color used consistently for surfaces, text, borders, hover, selection, playhead, keyframes, collaborators, Auto Key, warnings, errors, and synchronization status; visual state must not rely on color alone.
_Avoid_: Hard-coded widget color, unlabelled RGB value

**Selection Blue**:
The shared blue feedback state for hovered menu items, selected keyframe nodes, selected tracks, focused controls, and other active editor targets, with shape, icon, border, or text reinforcement where necessary.
_Avoid_: Collaborator color, warning color

**Legacy Camera Interface**:
The existing free-camera-oriented screen and its four module panels, which are not a compatibility boundary for the rewritten editor.
_Avoid_: Animation editor

**Timeline Node Hit Target**:
The invisible interaction region surrounding a compact timeline node, sized independently from the node artwork so the node remains visually restrained while still being reliably selectable at every supported interface scale. Overlapping targets resolve to the nearest eligible node on the relevant track without adding hover feedback.
_Avoid_: Enlarged node artwork, visible hit box, hover enlargement
**Timeline Type Identification**:
The semantic type of a timeline item is identified by its track-tree path, track label, property inspector, context menu wording, and accessible label rather than by changing the compact diamond's silhouette. Command, scalar, and camera-parameter items therefore share one visual node language while retaining complete type-specific editing behavior.
_Avoid_: Type-by-color-only, type-specific node silhouettes, ambiguous inspector fields
**Context-Aware Node Menu**:
The Blockbench-styled right-click menu for one or more selected timeline items, exposing shared edit, clipboard, duplication, deletion, and playhead operations while adding only the type-specific actions valid for the complete target selection. Numeric keyframes may expose interpolation controls; effect events never receive meaningless interpolation actions.
_Avoid_: Universal menu with invalid actions, inline node controls, type-blind bulk operation
**Keyframe Menu**:
The context-aware menu for selected numeric timeline keyframes, organized into four purpose-built groups: Keyframe Creation and Insertion, Curve and Tangent, Easing-to-Bézier Simulation, and Time and Removal. The menu shows only actions valid for the complete current selection and keeps the clean timeline row free of persistent controls.
_Avoid_: Flat command dump, event menu, inline action strip
**Keyframe Creation and Insertion Group**:
The Keyframe Menu group for creating, inserting, duplicating, and otherwise authoring numeric keyframe nodes. It uses the established trajectory-preserving insertion, grouped overwrite confirmation, duration auto-extension, selection, collaboration, and atomic Undo/Redo contracts.
_Avoid_: Runtime sampling, silent overwrite, view-only insertion
**Curve and Tangent Group**:
The Keyframe Menu group for changing Curve Interpolation Mode and applicable Bézier Tangent Mode, with mode-specific availability determined by the complete selection and explicit confirmation creating one content edit and one Undo/Redo item.
_Avoid_: Hover mutation, hidden neighbor edit, visual-only curve switch
**Easing-to-Bézier Simulation Group**:
The Keyframe Menu group for opening the bounded Easing-to-Bézier conversion preview when the selected keyframes define one valid continuous Easing Conversion Range. It never mutates project content before explicit confirmation, and a confirmed conversion becomes one ordinary editable Bézier edit.
_Avoid_: Immediate baking, partial conversion, background project mutation
**Time and Removal Group**:
The Keyframe Menu group for moving selected nodes in time, moving the playhead to a node, deleting nodes, and clearing selected timeline-track data. Content changes are atomic and undoable; deleting or clearing data never implicitly shortens Animation Duration.
_Avoid_: Automatic duration shrink, per-node undo fragmentation, timeline visibility filter
**Context Menu Selection Preservation**:
A right-click on an already selected timeline item preserves the complete local selection and applies the resulting menu operation to that selection. A right-click on an unselected item replaces the selection with that item before opening its menu. Right-clicking timeline whitespace does not clear the existing selection; it opens the whitespace menu without silently changing node selection.
_Avoid_: Right-click toggling selection, accidental multi-selection loss, hidden selection mutation
**Legacy Easing Fallback**:
The explicit existing `Easing` value retained beside a Custom Curve Segment for older readers and Legacy Animation Format export. Converting a preset preserves that preset, handle editing never changes it implicitly, a curve without a source preset begins with `LINEAR`, and only an explicit inspector edit may replace it.
_Avoid_: Automatic curve fitting, hidden fallback mutation, silent downgrade
**Curve Handle Visibility**:
Bézier control handles exist only in Graph Editor Mode after the user explicitly selects a Curve Segment by clicking its rendered curve. Selecting a keyframe, hovering a node or curve, opening an inspector, or viewing the ordinary timeline does not reveal handles. Clicking graph whitespace clears the Curve Segment selection and hides its handles without changing project content.
_Avoid_: Hover-only handles, always-visible graph clutter, handle state inferred from node hover
**Auto Tangent Promotion**:
Dragging a visible control handle whose boundary is in `AUTO` first freezes the currently computed handle positions, promotes that boundary to `ALIGNED`, and then applies the pointer movement under the aligned coupling rule. Promotion and the drag are one authored gesture and one Undo/Redo item; returning to `AUTO` recomputes the boundary from neighboring keyframes.
_Avoid_: Silent curve jump, hidden mode change, separate promotion history entry
**Tangent Break Gesture**:
Holding `Alt` while beginning a Bézier handle drag freezes the currently evaluated handle positions, changes an `AUTO`, `ALIGNED`, or `MIRRORED` boundary to `BROKEN`, and moves only the grabbed handle. The mode conversion and movement are one atomic authored gesture; cancellation or rejection restores the complete prior tangent state.
_Avoid_: Temporary-only break, second undo entry, partial rollback
**Curve Segment Inspector**:
The precise right-side editor shown for an explicitly selected Curve Segment. It exposes the Curve Interpolation Mode and, for `BEZIER`, separate start-boundary and end-boundary sections containing Tangent Mode, normalized handle time, unit-aware handle value, and a visible statement of any adjacent segment affected by coupling. `AUTO` coordinates are read-only until direct numeric editing promotes the boundary to `ALIGNED` without changing the evaluated shape first.
_Avoid_: Combined ambiguous handle fields, hidden neighbor effects, imprecise slider-only editing
**Numeric Inspector Field Navigation**:
The keyboard contract for moving between ordered numeric fields in the Curve Segment Inspector. The physical left and right keys on the numeric keypad confirm the current valid field before moving to the previous or next field, independent of NumLock state. Navigation does not wrap: at the first or last field the value is confirmed, focus remains in place, and a restrained boundary cue is shown. Main-keyboard left and right arrows remain text-caret controls, and an invalid field blocks navigation without committing project content.
_Avoid_: Arrow-key caret hijacking, navigation past invalid input, cyclic field wrapping, Tab-only curve editing
**Curve Numeric Field Order**:
The numeric-keypad sequence for the currently selected `BEZIER` Curve Segment is Start Boundary Time, Start Boundary Value, End Boundary Time, and End Boundary Value. The sequence includes only fields that are currently visible and editable; `AUTO` read-only coordinates, fields hidden by `LINEAR` or `CONSTANT`, mode/preset controls, and adjacent Curve Segment fields are skipped.
_Avoid_: Cross-segment keypad traversal, hidden-field focus, mode controls mixed into numeric order

**Normalized Handle Time**:
The forward-only time coordinate of a Bézier handle within its own Curve Segment, presented as `0%` through `100%` rather than as an animation timestamp or the camera runtime's internal time scale. The start handle cannot be later than the end handle.
_Avoid_: Keyframe timestamp, runtime tick, track value

**Unit-Aware Handle Value**:
The Bézier handle's value coordinate expressed in the owning track's real authored unit. It may extend beyond either boundary keyframe to create overshoot while remaining finite and valid for that track.
_Avoid_: Normalized value, percentage value, clamped endpoint value

**Curve Mode Selection**:
The deliberate choice of a Curve Interpolation Mode or Tangent Mode whose candidates may be browsed without changing project content. Only explicit confirmation authors the selected mode and creates one Undo/Redo operation.
_Avoid_: Hover preview mutation, scroll-wheel mode change, focus-only selection

**Dormant Curve Data**:
Custom Bézier data retained while a Curve Segment is evaluated as `EASING_PRESET`, `LINEAR`, or `CONSTANT`. Dormant data is hidden from the active mode without being deleted and becomes active again when the segment returns to `BEZIER`.
_Avoid_: Deleted curve, discarded conversion, mode-specific overwrite

**Direct Curve Mode Switch**:
An explicit Curve Interpolation Mode change that immediately replaces the active evaluation algorithm without attempting to preserve the previous mode's rendered trajectory. Dormant Curve Data is restored when available; otherwise the editor creates a fresh editable Bézier shape without approximation, baking, or confirmation UI.
_Avoid_: Easing conversion, trajectory-preserving mode change, automatic keyframe generation

**Bézier Node Chain**:
The ordered run of time-adjacent keyframe nodes on one track whose intervals are authored as Bézier nodes, with no intervening keyframe or non-Bézier node. A two-node chain evaluates and renders as `LINEAR`; a chain of three or more nodes exposes a continuous Bézier presentation whose interior nodes may have incoming and outgoing handles.
_Avoid_: Whole-track Bézier mode, isolated decorative curve, unrelated neighboring tracks

**Timeline Bézier Node**:
A normal compact diamond keyframe that carries Bézier participation without exposing handle controls in Compact Timeline Mode. It can be inserted, moved, copied, deleted, and multi-selected like other nodes; precise curve editing is available only in Graph Editor Mode.
_Avoid_: Special node silhouette, graph-only keyframe, hover-only control

**Bézier Chain Reconfiguration**:
The deterministic structural update performed after an ordinary timeline insertion, deletion, or time move changes which Bézier nodes are adjacent on one track. It splits, joins, or reduces chains according to the new sorted order without deleting authored node data or silently fitting a previous trajectory, and the complete structural edit is one Undo/Redo transaction.
_Avoid_: Background mutation, partial chain update, automatic trajectory fitting

**Initial Auto Handle**:
The non-overshooting `AUTO` tangent created only for a newly introduced Timeline Bézier Node when a chain first contains three or more nodes and no authored handle data exists. Existing authored or dormant handles are restored unchanged, and reducing a chain to two nodes merely deactivates its handles rather than deleting them.
_Avoid_: Bézier preset, Easing conversion, whole-chain recalculation

**Easing-to-Bézier Simulation**:
The explicit `Convert to Bézier` operation that replaces an Easing-evaluated interval with enough editable Timeline Bézier Nodes and Curve Segments to reproduce the source trajectory within a declared tolerance. It preserves the interval endpoints, avoids a material playback change, and differs from a Direct Curve Mode Switch, which performs no fitting. Its inserted nodes and converted source nodes become ordinary Timeline Bézier Nodes with no provenance-dependent visual behavior or automatic regeneration after the transaction.
_Avoid_: Direct mode switch, handle-only styling, mathematically exact claim, locked generated output

**Easing Conversion Range**:
The inclusive timeline span between exactly two selected endpoint keyframes on one scalar track when every interval inside that span is evaluated by an Easing Preset. Existing intermediate keyframes are included automatically and retain their stable identity, time, and value; the start endpoint's incoming interval is outside the range.
_Avoid_: Cross-track conversion, mixed-mode span, selected-node-only subset

**Easing Conversion Preview**:
The non-authoring comparison shown before committing an Easing-to-Bézier Simulation. It overlays the source Easing result and candidate Bézier chain, reports error and generated-node counts, and permits fidelity adjustment without creating project content, a revision, or an Undo/Redo entry.
_Avoid_: Direct conversion commit, hidden node generation, curve-mode dropdown preview

**Dual Conversion Fidelity**:
The acceptance check for Easing-to-Bézier Simulation that compares both normalized Easing output error and actual owning-track-unit error, using adaptive sampling around extrema, rapid changes, and equal-endpoint excursions. A tolerance miss requires an explicit warning acknowledgement; invalid samples, non-finite values, or a hard collaboration resource limit always block submission.
_Avoid_: Normalized-only check, fixed sparse sampling, silent tolerance failure

**Per-Interval Easing Simulation**:
The fidelity-first conversion rule that approximates each source Easing interval independently while preserving every source keyframe's time and value. A shared source boundary defaults to `BROKEN`, so the incoming and outgoing generated handles reproduce their respective intervals without forced smoothing; the user may later author a smoother tangent explicitly.
_Avoid_: Whole-range curve fitting, hidden C1 smoothing, shared-handle compromise

**Global Conversion Fidelity**:
The single target tolerance selected for one Easing-to-Bézier Simulation. Every source interval is computed independently against that same target, while the preview reports each interval's errors, generated-node count, Easing name, and shared-boundary state without exposing separate per-interval authoring controls.
_Avoid_: Hidden local tolerance, inconsistent range rules, preview-only mutation

**Ordinary Converted Bézier Node**:
A source or inserted keyframe produced by an accepted Easing-to-Bézier Simulation that behaves exactly like any other Timeline Bézier Node after commit. It keeps ordinary selection, editing, clipboard, deletion, and Undo/Redo semantics, and its former conversion provenance never triggers hidden regeneration or special rendering.
_Avoid_: Locked generated node, simulation proxy, auto-restored node

**Conversion Source Fallback Range**:
Range-level compatibility metadata that retains the original Easing interval or intervals used by an accepted Easing-to-Bézier Simulation without participating in current Bézier evaluation or node rendering. It is usable only as an explicitly disclosed Legacy Animation Format fallback and becomes stale after any relevant converted-node time, value, or curve edit.
_Avoid_: Per-node copied easing, active Bézier modifier, hidden regeneration trigger

**Stale Conversion Fallback**:
A Conversion Source Fallback Range whose source Bézier output has been edited after conversion. It may remain available for an explicit legacy export choice, but the export must warn that it no longer describes the current Bézier result and must never claim equivalence silently.
_Avoid_: Automatically refreshed fallback, exact legacy export, silent downgrade

**Minimum Conversion Node Count**:
The smallest editable Bézier representation permitted for one converted source interval. A linear source may remain a two-node chain and therefore use the deliberate `LINEAR` presentation; every non-linear source requires at least one inserted interior Timeline Bézier Node so that the result contains at least two active Bézier intervals.
_Avoid_: Two-node nonlinear curve, endpoint-only conversion, hidden nonlinearity

**Maximum-Error Adaptive Insertion**:
The deterministic Easing-to-Bézier fitting strategy that repeatedly inserts a node at the greatest measured candidate error and then refines the greatest-error child interval. It preserves source endpoint times and values, keeps generated times strictly increasing without automatic grid snapping, exposes generated positions in preview, and yields the same result for equal inputs, fidelity settings, and resource budgets.
_Avoid_: Fixed sparse sampling, random refinement, endpoint drift, silent grid snapping, partial conversion

**Conversion Budget Exhaustion**:
The state reached when Easing-to-Bézier Simulation hits a server-authoritative bounded resource limit before satisfying its selected fidelity target. The candidate cannot be submitted or retained partially; the user may explicitly recompute with a lower fidelity target or cancel, while the source project and all histories remain unchanged.
_Avoid_: Partial conversion, silent truncation, client-side bypass, hidden retry

**Authoritative Conversion Recalculation**:
The confirmation boundary where a responsive client-generated Easing Conversion Preview is reproduced and validated against its declared project baseline by the server before becoming project content. Only the complete server-verified candidate may create a collaboration revision and personal Undo/Redo item.
_Avoid_: Trusting client-generated nodes, preview-as-project-state, partial server acceptance

**Stale Conversion Preview**:
An Easing Conversion Preview whose declared project baseline is older than the current authoritative revision. Disjoint intervening edits may be semantically rebased, but any change to the source range or its structural boundaries invalidates the candidate and requires a newly generated preview plus a new explicit confirmation.
_Avoid_: Force-applying an old preview, automatic recommit, last-writer-wins conversion

**Bounded Conversion Worker**:
The camera-editor-owned asynchronous worker used for authoritative Easing-to-Bézier recomputation. It has finite concurrency and queue capacity, allows only one active confirmation task per player and project, captures only immutable request data, and releases its task handle and snapshot at every terminal lifecycle outcome.
_Avoid_: Shared unbounded pool, world-object capture, duplicate confirmation queue, detached task

**Conversion Terminal-State Gate**:
The server-thread decision point that gives one Easing conversion request exactly one final outcome: cancelled, committed, rejected, or failed. Cancellation discards any result before atomic commit, while a request already inside atomic commit completes without rollback; late callbacks and lifecycle exits cannot create a second outcome or mutate an inactive request.
_Avoid_: Client-timed cancellation, double completion, late callback mutation, partial rollback

**Conversion Deadline**:
The server-controlled finite deadline applied separately to Easing conversion queue waiting and active computation. A request that misses either deadline is cancelled or rejected without retaining a partial candidate, retrying automatically, or creating project history; only a complete result that reaches the terminal-state gate in time may commit.
_Avoid_: Unbounded worker time, silent retry, client-extended timeout, partial timeout result

**Throttled Conversion Progress**:
The bounded status stream for an Easing conversion request. It reports lifecycle phase and useful aggregate diagnostics at a controlled cadence without sending per-sample updates or partial node lists; adaptive work may use an indeterminate progress indicator when completion cannot be estimated reliably.
_Avoid_: Per-sample packets, partial project nodes, unbounded progress history, false precision

**Non-Locking Conversion Footprint**:
The lightweight logical source range observed by an active Easing conversion task. It never prevents collaborators from editing; disjoint changes allow computation to continue, while an accepted overlapping change invalidates and cancels the task before any result can commit.
_Avoid_: Track lock, project lock, blocking collaborator edits, stale background computation
