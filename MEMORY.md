# 重要记忆

- 在本工作区进行操作时时刻注意 `ending-library-forge-1201` skill。

# EndingLibrary Workspace Memory

## Required Reading

Read this file at the beginning of every task in this workspace. It records durable operational context, but current source, `gradle.properties`, `build.gradle`, and resource metadata remain authoritative for facts that can change.

Before proposing or editing code:

1. Read this file and the `ending-library-forge-1201` skill.
2. Preserve existing uncommitted changes; inspect `git status --short --branch` before writing.
3. Verify the exact source file and signature involved. Do not infer Minecraft, Forge, or EndingLibrary APIs from memory.
4. Use the skill reference indexes to locate symbols, then re-check the live repository source before changing them.
5. Do not run a build, compile, or test after Java edits unless the user explicitly asks.

## Project Identity

- Mod: `EndingLibrary` / `终焉图书馆`
- Mod ID: `ending_library`
- Package root: `com.mega.endinglib`
- Entry point: `com.mega.endinglib.EndingLibrary`
- Current toolchain: Minecraft `1.20.1`, Forge `47.3.12`, Java `17`
- Current build version at the last verification: `2.1.19fix`

## Bootstrap and Lifecycle

- `EndingLibrary` registers five Forge config specifications, the sound/menu/command-argument/attribute registries, and `PacketHandler`.
- `DistExecutor.safeRunForDist` creates the client or server `ModProxy`; `CommonProxy` owns common setup and entity-attribute setup.
- `CommonProxy` uses `@AutoCapManager`, registers player/living/entity/text-display capabilities during common setup, initializes item components, entity selectors, and game rules.
- Client-only registration and rendering integrations belong in `ClientProxy` or the `client` package. Keep dedicated-server loading safe.

## Module Map

- `api` is the public reusable surface: item components and consumption, client camera/screen/text/shader APIs, capabilities, events, data, menu, entity, server, and time helpers.
- `common` is runtime implementation: commands, networking, configuration, capabilities, data, registries, compatibility, event handlers, and menus.
- `mixin` expands vanilla and compatibility behavior. The active mixin configuration declares 108 common and 93 client mixins; high-risk areas include `advanced`, `accessor`, `time`, `capability`, camera, shader, and compatibility packages.
- `client` implements client renderers, shaders/post effects, screens, camera screens, client tasks, advanced behavior, and reloadable resources.
- `util` is cross-cutting Java and Minecraft/Forge support. `server` provides server functions/resources. `coremod` contains ModLauncher service implementations. `proxy` keeps side-specific startup separate.

## MixinExtras

- This workspace packages MixinExtras as a dependency. Before changing a Mixin, verify its currently configured version and the exact local API signatures.
- For new or refactored injections, prefer a MixinExtras injector or Sugar API when it expresses the required behavior clearly and safely. Do not mechanically replace an existing correct native Mixin injection; retain native Mixin when MixinExtras cannot express the behavior equivalently or would make the change less clear.
- Consult the [MixinExtras wiki](https://github.com/LlamaLad7/MixinExtras/wiki) when selecting an injector, then verify the chosen API against the local dependency.

## Runtime Contracts

- Networking is centralized in `common.network.PacketHandler`, which registers 49 messages. Existing traffic covers capability synchronization, user input and dynamic keys, time-stop state, camera/rotation/FOV control, screen effects, shaders, sounds, and client actions.
- Capability consumers should use the existing `LazyOptional` accessors and inspect their exact return types before adding a new access path.
- Changes involving shaders require matching Java-side uniform setup, post/program JSON, and GLSL resources; compilation alone does not validate shader linkage.
- Mixin and CoreMod changes need source-level verification against the exact targeted Minecraft/Forge classes and must retain side and compatibility guards.

## Change Boundaries

- Make the smallest source-compatible change that directly serves the request. Do not refactor adjacent code or alter numeric constants, IDs, version ranges, or configuration values unless explicitly requested.
- Treat the live checkout as authoritative over historical counts or this summary. A structural scan observed 592 Java files; this is orientation only and may drift.
- Keep this file concise and update it only when a durable, verified workflow or architecture fact changes.
