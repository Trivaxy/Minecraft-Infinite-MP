# Minecraft Infinite — development environment

The shipped jars have been decompiled into editable Java sources and wired up to a Gradle
build, so you can edit the game like normal source code, run it straight from Gradle, and
build a jar you can drop back into Prism/MultiMC.

## Requirements

- **JDK 17+** to run Gradle itself.
- **JDK 8** to compile and run the game. If you don't have one, Gradle downloads it
  automatically (via the foojay toolchain resolver in `settings.gradle`).
  LWJGL 2.9.4 breaks on Java 21+, so Java 8 is not optional for *running*.

## Commands

```bash
./gradlew :client:runClient           # launch the client
./gradlew :client:runClient -PmcUsername=Steve -PmcSession=-

./gradlew :server:runServer           # launch the server (console commands work)
./gradlew :server:runServer -PserverArgs=""   # with the Swing server GUI

./gradlew build                       # compile everything
./gradlew :client:jarmod              # -> client/build/libs/InfdevPlus-Client.jar
./gradlew :server:serverJar           # -> server/build/libs/minecraft-infinite-server.jar

./gradlew :server:smokeTest           # headless multiplayer check (server must be running)
./gradlew :server:setPlayerDimension -PsmokePlayer=Alice -PsmokeDimension=1
```

`runClient` / `runServer` use `run/client` and `run/server` as their game directories, so
they never touch your real `~/.minecraft`. Both are gitignored.

To play a modified build normally: run `:client:jarmod`, then in Prism right-click the
instance → Version → remove the old `InfdevPlus-Client.jar` → *Add to jar* → pick the new one.

## Layout

```
core/src/main/java          the shared game (1032 files)
core/src/main/resources     lang, struct, font.txt, icons (17 files, verbatim)
client/src/main/java        net.minecraft.client - renderer, GUI, sound, input (359 files)
client/src/main/resources   textures and fonts (2197 files, verbatim)
server/src/main/java        net.minecraft.server - MinecraftServer, tracking, bans (21 files)
server/src/smoketest/java   headless protocol tests
libs/vendor-common.jar      fastutil + org.json, lifted out of the shipped jars
libs/vendor-sound.jar       paulscode sound system + jorbis/jogg (client only)
run/                        game directories for runClient/runServer
```

Third-party libraries are **not** decompiled — they're re-jarred verbatim from the shipped
jars, so the bytecode you build against is exactly what the mod was compiled against.
LWJGL 2.9.4, jinput and jutils come from `libraries.minecraft.net` as normal Maven
dependencies; the natives are unpacked into `client/build/natives`.

### Adding a third-party dependency

There is **no shadow plugin.** `:client:jarmod` and `:server:serverJar` list what they bundle
by hand, so a plain `implementation` dependency compiles, runs under `runClient`/`runServer`,
and is then **missing from both shipped jars** — the first player to install it gets a
`NoClassDefFoundError` and you will not see it in development.

A dependency that has to ship goes in the `bundled` configuration of each module that ships
a jar, on top of wherever it is declared for compilation:

```groovy
// build.gradle (root, subprojects) - one version string
ext.brigadier = 'com.mojang:brigadier:1.3.10'

// core/build.gradle - compile against it
dependencies { api brigadier }

// client/ and server/build.gradle - and ship it
configurations { bundled { canBeConsumed = false; canBeResolved = true } }
dependencies  { bundled brigadier }
// inside the jarmod / serverJar task:
from({ configurations.bundled.collect { zipTree(it) } }) { exclude 'META-INF/**' }
```

Prism applies `InfdevPlus-Client.jar` as a **jarmod** (`dist/prism/patches/custom.jarmod.infdev-plus.json`),
merging its entries into the base `client.jar`, so bundled classes land on the launch
classpath the same way `vendor-common.jar` and `vendor-sound.jar` already do. Check the
artifact, not the build:

```bash
unzip -p client/build/dist/MinecraftInfinite-Prism.zip jarmods/InfdevPlus-Client.jar > /tmp/j.jar
unzip -l /tmp/j.jar | grep -c 'com/mojang/brigadier/.*\.class'    # 54
```

It must also be Java 8 bytecode. Brigadier 1.3.10 is (class file v52) and has no transitive
dependencies at all, which is why it drops in with no other changes.

## Commands

Commands run on **Brigadier** (`com.mojang:brigadier:1.3.10`). The old framework — one
abstract `Command`, a static `Command[256]` with a static cursor, and `input.split(" ")` —
is gone, and so are all 64 commands that were written against it. Commands come back one at
a time, deliberately; **`/give` is the only one registered so far.**

```
net.minecraft.commands
  Commands             one dispatcher per side. perform(), error rendering
  CommandSourceStack   brigadier's source type: who, where from, and with what authority
  CommandOutput        where output goes - chat, the log, the nearest player
  CommandMessages      framework errors, translated at throw time
  CommandExceptions -> arguments/  BlockPos, Vec3, Player, and the `~` reader
  CoreCommands         registration hook for commands both sides get   (/give, /tp, /gamemode)
  core/                the commands themselves

net.minecraft.client.commands.ClientCommands   client-only commands              (empty)
net.minecraft.server.commands.ServerCommands   dedicated-server commands
                                               (/stop, /kick, /ban, /unban, /op, /deop)
```

Registering a command:

```java
dispatcher.register(Commands.literal("home")
    .requires(src -> src.hasPermission(CommandSourceStack.LEVEL_OP))
    .then(Commands.argument("pos", BlockPosArgument.blockPos())
        .executes(ctx -> {
            BlockPosition pos = BlockPosArgument.getBlockPos(ctx, "pos");
            ctx.getSource().sendSuccess("...");
            return 1;
        })));
```

Which hook you pick decides who can run it, and `:client` cannot see `:server` — a command
registered by `ServerCommands` does not exist on a client at all. Anything both sides need
belongs in `CoreCommands`.

Gamemode is not a question for `mc.gamemode`. That field is the client's *transport* — `GameMode`
locally, `MultiplayerMode` over a socket — and asking it what mode the player is in is what kept
creative, adventure and spectator from working in multiplayer at all. Ask
`player.properties.gamemode` (or `isCreative()` / `isAdventure()` / `isSpectator()`); the server
owns it and sends it with `GamemodePacket`. See AGENTS.md.

`~` reads against `CommandSourceStack.getPosition()`, which is the **feet** — and since B-60
that is simply `posY`, on both sides. It used to store `posY - yOffset` because a singleplayer
`LocalPlayer` was anchored at the eye and a raw `posY` would make `/tp ~ ~ ~` climb 1.62 every
time it ran. Anything that moves an entity to a resolved position wants feet too:
`Entity.teleportTo` adds `yOffset` back on, which is zero for a player and non-zero only for
the few entities whose `posY` is their centre.

Two argument branches under one literal, with no literal to tell them apart, is how `/give`
serves both `/give <item>` and `/give <player> <item>`. **Registration order is load-bearing
there.** Brigadier collects every branch that parses and sorts by leftover input and error
count — never by whether the branch actually ends in an `executes()` — and the sort is stable,
so on a tie the branch registered *first* wins. `/give 4096` parses under both (`4096` is a
legal player name to `PlayerArgument`), and only the item branch being registered first makes
it give an item rather than fail. Registering them the other way round breaks the one-argument
form outright; `GiveSmokeTest` catches that.

The same collection rule loses errors: a branch that throws is dropped as soon as any sibling
parses, so `/give 999999` reached the sender as "Unknown command." rather than "No such item".
The fix is an `executes()` on the intermediate `<player>` node that fails with the message the
dropped branch would have given. Any command with sibling argument branches needs the same
treatment, or its errors come out as `Unknown command`.

Permission is an int on the source, not an enum on the command: 0 everyone, 2 op, 4 console
and command blocks. The server reads `ops.txt`; singleplayer reads the world's cheats flag.

A failed `requires()` is **invisible to brigadier** — the node is skipped during parse exactly
as if it did not exist, so a gated command came back as "Unknown command." with no way for the
command author to say otherwise. `Commands.perform` recovers the distinction after the fact: on
any syntax failure it re-parses the same input against `source.withPermission(LEVEL_OWNER)`, and
if the elevated parse gets *further* along the string, the only thing that could have stopped
the real one was permission, so it reports `commands.base.noperms` instead. This costs one extra
parse on the failure path only, and never fires for a source already at level 4.

The cursor comparison is what keeps it honest: a genuinely unknown command stops at the same
place under both sources, and so does a bad argument to a command the sender *can* run, so
neither is mistaken for a refusal. It also means `requires()` predicates testing something other
than permission — `isServer()`, say — still fall through to the ordinary error, because elevating
the level does not change their answer. **Do not put permission checks in `executes()`;** the
gate belongs in `requires()` and the message comes out on its own.

Usage strings are generated, never written. `Commands.usage(dispatcher, node, source)` wraps
`getAllUsage(node, source, true)`, which returns one suffix per runnable leaf with the node's own
name stripped — and a single empty string for a command that takes no arguments — so the helper
prefixes `/<name>` and special-cases that one. Everything that prints usage goes through it, which
is why `/help` and a failed command word things identically. **It filters by `requires()`**, so
usage never names a form the reader cannot run.

`Commands.perform` sorts a syntax failure into one of three answers:

| The sender typed | They get |
|---|---|
| a command that does not exist (`/foo`) | `Unknown command.` |
| a real command, used wrong (`/score give 200`, `/score add Bob`) | `Incorrect command. Usage:` + every usage form |
| an argument that parsed and was rejected (`/give 999999`) | that argument's own message, unchanged |

The middle row is the point: brigadier reports both a bad subcommand and a parse that stops short
of any `executes()` as `dispatcherUnknownArgument`/`dispatcherUnknownCommand`, which used to reach
the sender as a bare "Unknown command." for a command that plainly exists. What separates the first
two rows is `parse.getContext().getNodes()` — brigadier threads one context builder down the whole
parse, so that list is flat and its first entry is the root literal the input actually matched.
Empty means nothing matched. The third row is left alone deliberately: "No such item: 999999" says
more than a usage dump, so only the two structural built-ins are rewritten, and the `§7 …<--[HERE]`
cursor line is dropped when usage replaces the message.

**Brigadier has no description field**, and vanilla never added one — it ships usage strings
only, which is all `/help` shows. Anything richer needs a registry of our own beside the tree;
that was tried and taken back out as not worth carrying yet. It also will not collapse a usage
tree the way vanilla's `/help` output suggests: `getSmartUsage` stops dead at the first alternation
(`/score (add|remove|set)`, arguments and all discarded), so `getAllUsage`'s flat list — one line
per form — is what both `/help` and the error path print.

`/help` is pure introspection — `getRoot().getChildren()` for the listing, `Commands.usage` for the
lines under it — and **both filter by `requires()`**, which is what keeps it from becoming an index
of operator commands for everyone. `/help <command>` also refuses a command the sender cannot use as
`Unknown command.` rather than saying they lack permission, since confirming it exists is the leak.
That refusal throws `CommandMessages.UNKNOWN_COMMAND`, our own type and not the built-in one, so it
lands in the third row above and is not turned into `/help`'s own usage.

One wart worth knowing: the sentinel `executes()` on `/give`'s intermediate `<player>` node —
the one that exists only to report "No such item" instead of losing the error — is a real
`Command` as far as brigadier is concerned, so `/give <player>` shows up as a usage form of its
own, in `/help give` and in any usage `/give` prints on a failure, even though it always throws.
Any command using that workaround will do the same.

**There is no tab completion.** The old client-side one read the deleted `Command[256]` and a
hand-written `suggestions()` table, and went with them; `ChatScreen`'s Tab key now only
completes player names. Brigadier's `getCompletionSuggestions` is there on the dispatcher
whenever completion is wanted — with one catch worth knowing before you wire it up: it does
**not** filter by `requires()`. Vanilla only appears to because it sends each client a
pre-filtered command tree, so a naive hookup offers every player every operator command by
name.

Five things dispatch, all through one `Commands` instance per side:

| Where | Entry |
|---|---|
| singleplayer chat | `LocalPlayer.sendChatMessage` |
| multiplayer chat | `NetServerHandler.handleSlashCommand` → the server's queue |
| server console | `MinecraftServer`'s stdin thread → the queue |
| server GUI | `ServerGUI`'s text field → the queue |
| command block | `CommandBlock.onTick` → `Commands.active()` |

The queue is drained once per tick by `MinecraftServer.commandLineParser()`, so everything
runs on the server thread. **The source is built when the command runs, not when it is
queued** — the stdin thread is up before the worlds are.

`Commands.active()` is the one static: the dispatcher this JVM runs, set at startup by
whichever module owns it, so the command block in `:core` can reach a dispatcher without
knowing which side it is on.

## Multiplayer

The mod ships a working dedicated server. To play with friends: run the server, and have
everyone connect through the client's **Multiplayer → Direct Connect**.

`online-mode=true` works. The login handshake talks to `sessionserver.mojang.com`: the
client POSTs `/session/minecraft/join` with the access token its launcher gave it, and the
server confirms with `/session/minecraft/hasJoined`. The access token never reaches the
server — Mojang is the middleman.

`online-mode` defaults to **true**, so a server jar unpacked into a fresh directory is
authenticated out of the box, and the first run writes the key into `server.properties` so
you have something to edit. That default matters because every identity in the server is a
bare username (`ops.txt`, `white-list.txt`, `world/players/<Name>.dat`): with it off, anyone
who can reach the port can log in as any name — including an opped one — and inherit that
player's saved inventory.

`:server:runServer` writes `online-mode=false` into `run/server/server.properties` when the
key is absent, so dev servers and the smoke tests still need no account. It never overrides
a setting you put there yourself — set it to `true` by hand to play-test auth, and set it
back to `false` before running the smoke suite, which cannot connect to an online server.

The client needs a real access token to join an online-mode server, which means launching
from Prism/MultiMC signed in to a Minecraft account. `runClient` passes `-PmcSession=-`, so
a Gradle-launched client can only reach offline-mode servers; it gets a "launch from a
launcher signed in to a Minecraft account" message instead of a confusing failure.

### Where an entity's eye is

`getEyeHeight()` is **height above `boundingBox.minY`** — the feet — and nothing else. Ask for
the eye through one of two methods, never by adding the height to a position yourself:

| | |
| --- | --- |
| `getEyeY()` | absolute world Y of the eye. Use this for raytraces, projectile spawn points, block probes — anything that is logic. |
| `getEyeOffset()` | the eye relative to `posY`. Use this in render code, which works from an interpolated `posY` and wants the step-up smoothing `posY` carries. |

**Every player is anchored at the feet** (`yOffset` 0), on both sides, since B-60. `yOffset`
survives only for the handful of entities whose `posY` is their centre or their hang point —
`DroppedItem`, `PrimedTNT`, `FallingBlock`, `VoidCrystal` and `Boat` at `height / 2`, `Minecart`,
`Frame` at 1.0, `LingeringCloud` at 1.5, `Particle`. **No pose, packet or subclass may move a
player's anchor again**; if you find yourself writing `posY + 1.62 - yOffset`, you want
`getEyeY()`.

`Player` is the only class whose eye moves with pose, and `getEyeHeight()` is now the *only*
place that table lives — 1.62 standing, 1.42 sneaking, 0.56 crawling — clamped to
`height - 0.1F` so the 0.2m box of death and sleep cannot put the eye outside the body. That
clamp is what drops the camera on death and in bed; nothing writes an offset to do it.
`setCrawling` and `setSneaking` change the *box*, never the anchor.

The two accessors still differ because `yOffset` is still non-zero for the entities above:
`getEyeY()` starts from the box, `getEyeOffset()` subtracts the anchor back out. Render code
that has already interpolated `posY` wants the second — `GameRenderer.updateCamera` translates
by `-getEyeOffset()` for **every** camera entity, which is what puts the eye back on top of a
feet-anchored `posY`. Everything downstream of it (chunk sorting, `renderPosY`, entity and tile
render origins, fog) reads the same interpolated `posY` and composes correctly, so those sites
never mention the eye at all.

### The eye has two forms, and picking the wrong one is a whole bug class

| | |
| --- | --- |
| `getEyeY()` / `getEyeOffset()` | the **crisp** eye: exactly where `getEyeHeight()` says, this tick. Logic takes this — raytraces the server also runs, projectile spawns, block probes, reach. It is the value the server computes too, so the two sides agree. |
| `getEyeOffset(tickDelta)` | the **rendered** eye: `renderEye` settling toward `getEyeHeight()` under the same gravity the box used to fall with, then interpolated across the frame. Anything that *defines or collides with the viewpoint* takes this — the camera translate, both camera collision probes, `Mob.getPosition` (the crosshair and entity-pick origin), the grapple line. |

The rule is: **if a disagreement with the camera would be visible on screen, use the rendered
form.** A crosshair raytraced from the crisp eye while the camera sits mid-crawl highlights a
block you are not looking at for the five ticks the pose takes.

Why this exists at all is worth knowing, because it is how B-60b and B-60c both happened. Before
B-60 the eye lived *inside* `posY`, and it inherited four things from doing so without anyone
writing them down: the renderer's `prevY → posY` interpolation smeared every eye change over at
least a tick; `posY` is the origin the view matrix rotates about, so pitch pivoted at the eye;
the box's own gravity gave the crawl its fall-and-settle; and `colHeight` eased it up steps.
B-60 moved the eye's *value* out correctly and left all four behind. **When you lift a quantity
out of a variable other machinery already processes, the value being equal proves nothing — list
what that machinery was doing for it first.** (Only `colHeight` survived the move for free, and
only because `renderEye` is an offset on top of an already-smoothed `posY`.)

The same mistake has a second shape, which is B-60d: **deleting an offset silently re-scopes every
literal that was calibrated against it.** `putToBed` passed `y + 0.9375` and looked right only
because `yOffset = 0.2` dropped the box back onto the mattress; with the offset gone the constant
was suddenly measured from somewhere else and the sleeper floated exactly 0.2 high. Grepping for
code that *reads* the field does not find these — the tell is a magic number in the same method as
the write you removed. Every `setPosition`/`teleportTo` in `Player` has now been re-derived
against the feet anchor, and the constants that moved are `0.9375 -> 0.7375` (sleeping) and
`+0.1 -> +0.3` (standing out of bed).

**That translate is the last line of `updateCamera`, and the position is load-bearing.** GL
post-multiplies, so the *last* transform issued is the *first* one applied to a vertex: issued
after the pitch/yaw rotations it acts on world axes and the rotations pivot around the eye;
issued before them it acts in view space, and the eye offset tilts with the camera. Get it
backwards and looking down walks the viewpoint forward into the floor and looking up walks it
backward, which is B-60b. The view-space translates in the same method — the third-person
pull-back, the low-ceiling nudge — are issued *before* the rotations on purpose. Anything you
add there has to pick a side deliberately.

### Adding a block with a GUI

A window has to be built twice — once on each side — and the two halves are wired together
only by the `inventoryType` byte in `ContainerOpenPacket`. The ids live on that packet as
`public static final int` constants; take the next one and use the constant on both sides,
never the literal.

1. `Player` gets a `displayGUIX` method whose body is `this.windowNotImplemented("x")`, not
   `{}`. The empty body is reserved for the four screens that are deliberately client-only
   (sign, command block, book, knowledge book) and now means "on purpose".
2. `LocalPlayer` overrides it to open the screen.
3. `EntityPlayerMP` overrides it with one `openWindow(...)` call, passing the same container
   the client's screen builds.
4. `ClientHandler.handleContainerOpen` gets a case that rebuilds the client's side from the
   packet alone.

The packet carries `windowId`, `title`, `slotCount`, `x/y/z` and one `state` boolean, and
that has been enough for every screen so far — position-based ones (anvil, carver,
grindstone) read the coordinates, tile-entity ones build a blank stand-in whose slots and
progress bars arrive over `ContainerContentsPacket` and `ContainerDataPacket`.

**The two containers have to agree on slot count and slot order.** Each side constructs its
own instance and a window click carries a slot *index*, so a mismatch means every click in
that window resolves against a different slot — the server recomputes a different stack,
answers `TransactionPacket(accepted=false)` plus a full resync, and the item snaps back.
That is B-06, and `ContainerOpenSmokeTest` guards it by asserting the length of the
`ContainerContentsPacket` the server sends on open against the client container's count.

Do not copy the workbench's client case as a template for a position-based GUI: it fabricates
the bench at the player's own position with `portable=true`, which survives only because
`WorkbenchContainer.canUse` short-circuits on that flag. `AnvilContainer.canUse` has no such
escape hatch. Use the coordinates off the packet.

### Distribution

`./gradlew :client:prismExport` builds `client/build/dist/MinecraftInfinite-Prism.zip`, a
Prism/MultiMC instance export — the same shape Infdev+ itself shipped (`Latest.zip`). The
vanilla a1.0.4 jar is deliberately **not** in it: `dist/prism/patches/net.minecraft.json`
points Prism at `launcher.mojang.com`, so Prism fetches the base game from Mojang at install
time and the player needs their own account. Ship this, not a bare jar.

`./gradlew :server:serverJar` builds a standalone server jar. It contains the whole game, so
there is no "the launcher fetches the base" story for it — keep it for your own hosting
rather than handing it around.

### Versioning

Nightly, date-stamped: `Minecraft Infinite 1.0-150826` is the build of 15 August 2026
(`1.0-ddMMyy`). Nothing is bumped by hand — `:core:generateBuildVersion` writes
`BuildVersion.java` into `core/build/generated/sources/version` on every build, and
`SharedConstants.VERSION_STRING`/`FULL_VER_STRING` are built from it. Every display of the
version reads those two constants; do not write the string out longhand anywhere.

The generator re-runs every build but rewrites identical content within the same day, so
`compileJava` only actually goes stale when the date rolls over. Two consequences worth
knowing: a full recompile once a day, and a client built on one day paired with a server
built on another will report different versions. Both are cosmetic, because the version
string gates nothing.

**`PROTOCOL_VERSION` is a separate number and is still bumped by hand.** It is the
`yyyyMMdd` compatibility gate `NetLoginHandler` checks at login, and it moves only when the
wire format changes. Tying it to the build date would break every client/server pair
overnight for no reason.

`WorldInfo.setVersion` stamps the dated string into `level.dat` and the world-select list
shows it, so a save records which build made it. Older saves keep whatever they were stamped
with until they are next written.

### Dimensions

The server runs all five dimensions at once — Overworld (0), Hell (1), Sky (2), Void (3) and
Ocean (4) — each with its own `WorldServer`, entity tracker and chunk manager. They live in
`<world>/`, `<world>/DIM1` … `<world>/DIM4`.

All five are **constructed** at boot but only the Overworld's spawn region is **preloaded**;
construction is just a `level.dat` read and a chunk provider, so the other four cost nothing
until someone stands in them. `DIM2`–`DIM4` stay empty directories until first visit — that is
expected, not a bug. Expect one "Can't keep up!" the first time anyone enters a fresh
dimension, while its spawn chunks generate.

A few things are deliberately shared rather than per-world, and all three will bite if
un-shared:

- **One `AnvilSaveHandler`.** It writes its own timestamp into `session.lock` and validates
  against that instance's field, so a second handler invalidates the first.
- **One `MapStorage`.** Map ids come from a counter persisted to a single `data/idcounts.dat`;
  separate instances hand out colliding ids.
- **One writer for `level.dat`.** `World.saveData()` embeds the player list, so only dimension
  0 writes it (`WorldServer.saveData`).

World time is copied from the Overworld to the other four after each tick, so they don't drift.

`allow-dimensions` in `server.properties` (formerly `allow-nether`, still read as a fallback)
gates *travel*, not ticking — every world always ticks, so a player whose save puts them in
Hell is never stranded in a frozen world.

### Headless smoke test

`./gradlew :server:smokeTest` logs two clients into an already-running server and asserts
that both complete login, both receive chunk data, and chat relays between them. Start a
server in another terminal first. Options: `-PsmokeHost=`, `-PsmokePort=`, `-PsmokeArgs=`, and
`-PsmokeClass=net.minecraft.smoketest.LoginSmokeTest` for the single-client version, which
prints the dimension the server put you in:

```bash
./gradlew :server:setPlayerDimension -PsmokePlayer=Alice -PsmokeDimension=1   # server stopped
./gradlew :server:runServer                                                   # then start it
./gradlew :server:smokeTest -PsmokeClass=net.minecraft.smoketest.LoginSmokeTest \
                            -PsmokeArgs="Alice 12"
```

That pair is the end-to-end multi-world check: it should report `dimension=1` and stream Hell
chunks, with no travel code involved.

`-PsmokeClass=net.minecraft.smoketest.HeightSyncSmokeTest` checks that the wire format
carries block content above Y=255 (sections 16–19 of the 20-section world). Run with no
args it round-trips blocks at Y=5/255/300/315 through real packet framing in-process —
no server needed. Pass `-PsmokeArgs="live"` against a running server to have a bot
`/setblock` stone at Y=300 and assert the update comes back: `HeightBot` must be in
`run/server/ops.txt` and the server restarted for the op to take effect.

`-PsmokeClass=net.minecraft.smoketest.KnowledgeBookSmokeTest` guards the slot-index
translation the knowledge book depends on (B-06). The book has no server-side container, so
its clicks have to be renumbered into the player container's layout before they go out. The
bot `/give`s itself an item and sends both indices for that one inventory slot: the book's own
index must come back rejected with a full resync, the translated one accepted. Needs a running
server with `bookbot` in `run/server/ops.txt`.

`-PsmokeClass=net.minecraft.smoketest.WeatherSmokeTest` covers B-25 in two halves. The wire half
flips the server with `/weather rain` and asserts the `WeatherPacket` comes back; the client half
ticks a real `NetworkWorld` and asserts `getRainStrength` ramps at 0.01/tick off the flag, which is
the part `NetworkWorld.tick()` skipped by never calling `super.tick()`. Needs `weatherbot` in
`run/server/ops.txt` — `/weather` is operator-only.

`-PsmokeClass=net.minecraft.smoketest.JukeboxSmokeTest` covers B-56 — the jukebox end to end,
from the record going in to breaking a loaded one. It builds its own floor in the air out of
creative slot writes, so it needs no `/setblock`; it needs `jukebot` in `run/server/ops.txt`
for `/gamemode`, and a second bot that stands next to the jukebox and needs no op of its own.
Persistence is the same site in two halves, because only a restart puts the tile entity
through NBT:

```bash
./gradlew :server:smokeTest -PsmokeClass=net.minecraft.smoketest.JukeboxSmokeTest -PsmokeArgs="jukebot load"
./gradlew :server:runServer     # the load half ends with /stop; start it again
./gradlew :server:smokeTest -PsmokeClass=net.minecraft.smoketest.JukeboxSmokeTest -PsmokeArgs="jukebot eject"
```

The `/stop` is not politeness: this server has **no shutdown hook**, so a killed one saves
nothing and the whole jukebox is missing on the next boot — which reads as a persistence bug
that isn't one.

`-PsmokeClass=net.minecraft.smoketest.QuiverSmokeTest` covers B-57 — arrows put into a quiver
surviving a relog. It equips a quiver, stocks it, drops the connection, logs back in and then
touches an unrelated inventory slot, which is what used to make the arrows disappear. Needs
`quiverbot` in `run/server/ops.txt` for `/give`.

It also shows two things worth stealing for the next container test. **An ordinary click is
answered with a transaction and the single slots that moved, never the whole window** — so to
read the container state, send a click whose claimed stack cannot match what the server
computed (an impossible claim on the empty craft result is a no-op with an empty cursor) and
the server resyncs everything. **A rejected click then blocks the container** — `canUseMP` is
false for that player until the client acknowledges the transaction it was rejected with, and
until it does, every click that follows is dropped in silence. Clicks sent in the first second
or two after login are dropped the same way, so the ask is worth repeating. The run also starts
by emptying the inventory into the trash slot (50), because whatever the last run left equipped
would make its own clicks land on an occupied slot.

The tests live in `server/src/smoketest/java` and speak the real wire format using the game's
own packet classes out of `:core`, so they cannot drift from the game.

`Bot.java` is the headless client they all share — connect, log in, keep the connection pumped,
wait on packets (`await`, `awaitLast`, `awaitAll`), and track where the server says other entities
are (`positionOf`). `Checks.java` is the pass/fail tally.
Together they make a one-off investigation cheap: drop any class with a `main` into that
directory and run it with `-PsmokeClass`, no registration needed.

```java
Bot bot = Bot.join(args, "probe");
bot.chat("/give 3 1");
SlotPacket landed = bot.awaitLast(SlotPacket.class, p -> p.myItemStack != null, 3000);
```

`Bot` keeps every live bot's position echoed on every wait. That is not a convenience: the server
ignores a player until its login teleport is echoed back exactly, so a bot that goes quiet is
dropped and anything it "proves" is about itself, not the game.

`settle(ms)` sinks for a fixed time and stops wherever the timer runs out — over the spawn's open
water (B-01) that is still mid-air. Use `land(ms)` when a test needs to be standing on something:
it sinks until the server refuses the next step down and holds the position that refusal restored.
The refusal is also what clears `hasMoved`, so a bot that keeps sinking through it spends the rest
of the run talking to a server that stopped listening — which looks exactly like a game bug and is
not one.

**Bots that `/give` themselves or run a command need to be opped.** `run/server/ops.txt` must
contain `bookbot`, `heightbot`, `swapbot`, `rockbot`, `weatherbot`, `kickbot`, `banbot`, `tpbot`, `opbot`, `jukebot`, `quiverbot` and `modebot`, and the server must be
restarted for a new entry to take effect — otherwise the test fails with "never saw the /give land in a slot",
which looks like a protocol bug and is not one.

Current state of the suite, run against a live server: `LoginSmokeTest`, `HeightSyncSmokeTest`,
`ProjectileSmokeTest`, `SprintSmokeTest`, `KnowledgeBookSmokeTest` and `WeatherSmokeTest` all
pass. `CrawlSmokeTest` fails its third assertion — see B-22 on the bug board; the assertion
itself is wrong, not the game. `TwoClientTest` and `QuickStackSmokeTest` fail at HEAD as of
2026-08-15, both bots kicked mid-run with "Flying is not enabled on this server" — the B-01
ocean-spawn pattern, confirmed to fail identically on a tree with the B-25 change stashed out,
so it is not a regression from it. `QuickStackSmokeTest`'s failure count varies run to run (10
and 13 seen), which is the kick landing at different points rather than a second defect.

**Each bot runs in its own JVM, deliberately.** `UpdateChunkPacket` and `UpdateSectionPacket`
decode through a `private static byte[] temp` scratch buffer. Two clients decoding chunks
concurrently in one JVM corrupt each other and die with `Bad compressed data format`. One
client per process is what the real game does and the only safe arrangement — worth
remembering if anyone ever tries to run a client and a server in the same JVM (an
integrated / "Open to LAN" server would hit exactly this).

## Module layout

Three Gradle modules. `:core` holds the game; `:client` and `:server` hold only what is
genuinely specific to each, and both depend on `:core`.

| Module | Files | What lives there |
| --- | --- | --- |
| `:core` | 1073 | Blocks, items, entities, world generation, containers, recipes, commands, the protocol, and the `lang/`, `struct/`, `font.txt` and icon resources |
| `:client` | 363 | `net.minecraft.client` only — renderer, GUI, particles, sound, input, options, and every texture |
| `:server` | 41 | `net.minecraft.server` only — `MinecraftServer`, console commands, `EntityTracker`, `WorldServer`, `EntityPlayerMP`, bans, the Swing console |

**`:core` may not reference `net.minecraft.client` or `net.minecraft.server`.** It does not
depend on either module, so this is not a convention anyone has to remember — a reference
simply will not compile. That is the whole point of the split.

Adding shared behaviour means editing one file in `:core`. There is no second copy to keep
in step, and no way to fix a bug on one side only.

### How it used to work, and why that changed

The two trees were decompiled from the shipped jars and each carried a **full copy** of the
shared game code, kept in step by a `./gradlew checkParity` task. That task compared files
**at the same relative path**, which left a blind spot: any divergence that also moved or
renamed a file was invisible to it. Two real bugs were hiding there when the modules were
merged —

- `UpdateChunkPacket` — the client validated the inflated chunk length and threw on a
  mismatch; the server copy discarded the return of `inflate()`. The files sat at
  `packet/world/` and `packet/`, so they were never compared. The client's check won.
- `lang/desc.lang`, `lang/gui.lang`, `lang/item.lang` differed by three lines. Resources
  were not checked at all. The client's copies won, being the ones actually rendered —
  which also keeps the "Eddie's Hatchet" easter egg in the table below intact.

`checkParity` is gone. Its job is now structural.

### What the merge itself had to reconcile

Beyond the two bugs above, the trees differed in five files and two naming schemes:

- `Leaves`, `TransparentBlock` — `fast` and `opacity` were narrower on the server. Widened
  to `public`.
- `LeafPile` — the server always returned `standard` icons; took the client's "fast leaves"
  branch. Both fields exist on both sides and nothing server-side calls `getIcon`.
- `NetworkHandler`, `Packet` — differed only because of the packet names below.
- Eight packets kept vanilla-style names on the server (`Packet7UseEntity`) and friendlier
  ones on the client (`UsePacket`). **The client names won**, since the other 60 already
  used the `*Packet` suffix on both sides. Ids, flows and read/write logic were already
  byte-identical, so the wire format never moved.
- The server kept its packets flat in one package; the client sorted them into
  `packet.{container,entity,misc,player,world}`. **The client layout won.**
- `net.minecraft.game.item.crafting` (client) vs `.recipe` (server). **`.recipe` won**, as
  it matches the class names inside it.

What got reconciled (server was treated as authoritative except where noted):

| Change | Direction | Why |
| --- | --- | --- |
| `enchantable` field on `Tool`/`Axe`/`Pickaxe`/`Shovel`/`Hoe`/`ItemList` | **deleted** | Written in 4 places, read in none. `Tool.isEnchantable(ItemStack, ItemStack)` is the real API `AnvilContainer` calls, and it ignores the field. Abandoned groundwork. |
| `axeSpecial` — item 375, "Eddie's Hatchet" | client → server | A LEGENDARY easter egg with its own lang name and flavour text. Id 375 was free server-side, so it was added rather than deleted. |
| Pillar recipe slab meta `3` → `10` | client → server | Meta 10 is Angelite Bricks and the block is the *Angelite* Pillar; meta 3 is Marble. The server's value was a typo. |
| Snow/sand/ash layer craft yield `3` → `6` | server → client | Pure balance, no evidence either way, so the default rule applied. |
| `FleeSun.canExecute` | client → server | Server read `raining \|\| (daytime && ...)`, making mobs flee whenever it rained, day or night, indoors. The `raining` term had escaped the conjunction. |
| `Chicken.isInWater` / `Ocelot.isInWater` | client → server | Movement is server-authoritative, so the server needs the override or these two behave differently in MP than SP. |
| `Dimension.findSpawnPoint` FLAT guard | client → server | Only the server runs the spawn search in multiplayer. |
| `settings.getDimension()` → `currDim.dimension` (4 sites) | both trees | `WorldInfo.dimension` is read from the *player* tag and is meaningless on a dedicated server. `currDim` is always correct per world. Prerequisite for multi-world. |

That reconciliation is what made the `:core` merge possible: by the end of it the two trees
were byte-identical across all 1073 shared files, so hoisting them was a file move rather
than a code review.

## Provenance / how to regenerate

**The decompiled game is not in this repository.** `core/`, `client/` and `server/` are empty
of sources until you run:

```
./gradlew bootstrap
```

That downloads Vesuvius's published jars, decompiles them, and applies this project's
patches on top. It takes about a minute. Everything under the module source sets is
generated and `.gitignore`d; the 34 files this project actually wrote are listed in
`tools/authored.txt`.

The repository ships the build, the docs, the Brigadier command framework, and two patches.
It does not ship Mojang's or Vesuvius's code or art.

### The two commands

| | |
| --- | --- |
| `./gradlew bootstrap` | Assemble the tree. Safe to re-run; it preserves authored files. |
| `./gradlew capturePatch` | Fold your working-tree edits back into `patches/`. **Run before committing.** |

`capturePatch` is not optional. Derived files are gitignored, so an edit to one is invisible
to git — `capturePatch` is what turns it into a tracked change. It also refreshes
`tools/authored.txt` and the generated half of `.gitignore`, so a file you newly write in a
module is picked up automatically.

### What bootstrap does

| Stage | Task | Result |
| --- | --- | --- |
| 1 | `fetchUpstream` | Downloads the two jars, SHA-256 pinned. Client is the `v1.0` release asset; server is pinned to upstream commit `b7bc008`. |
| 2 | `extractVendor` | Re-jars fastutil + org.json into `libs/vendor-common.jar`, JOrbis/JOgg + Paulscode into `libs/vendor-sound.jar`. |
| 3 | `makeCodeJars` | Strips the jars to game classes only (`net/`, `com/mojang/`, `ca/`). |
| 4 | `decompile*` | Vineflower 1.12.0 → 1435 client + 1113 server sources. |
| 5 | `stageBaseline` | Applies `patches/0000`, then `tools/layout.txt`: 1017 moves into `:core`, 1157 duplicate deletes, 2214 resources extracted from the jar. |
| 6 | `bootstrap` | Applies `patches/0001` and writes the tree. |

Patches are applied with JGit, so no `git` binary is required and the flow is identical on
Windows, macOS and Linux.

### Reproducibility

Vineflower runs on a pinned Java 21 toolchain (it cannot run on 8, and the version is pinned
so a future JDK cannot silently move the output). Verified: two runs on the same JDK are
byte-identical, and JDK 21 and JDK 25 produce byte-identical output. Decompiling the
published `v1.0.jar` yields all 1435 client sources with zero missing or extra files.

`tools/layout.txt` carries a fourth verb, `N`, for two lang files that are CRLF inside the jar
and LF in the tree. A unified diff cannot express a line-ending change on lines it does not
otherwise touch, so the normalisation is recorded in the layout instead of the patch.

### The two patches

- **`patches/0000-decompiler-fixes.patch`** — 76 files. The 20 mechanical decompiler fixes in
  the table below, plus the work done between decompiling and the first commit (the protocol
  bump, the `currDim.dimension` move, `DigPacket`'s widened y, `Chunk.setChunkData` bounds
  checks).
- **`patches/0001-content.patch`** — 143 files. Everything since: the `:core` merge's content
  reconciliation and every bug fix, plus three lang files.

### Hand-fixes applied after decompiling

20 edits across 19 files, all mechanical decompiler artifacts — no behaviour changes. These
live in `patches/0000` now, so they are reapplied automatically; the table is kept because it
explains *why* those files differ from raw decompiler output.

| Symptom | Fix | Files |
| --- | --- | --- |
| `for (Mob x : List<Entity>)` — lost `checkcast` | iterate as the declared type, cast inside the loop | `Aggro`, `AggroWild`, `DetectEnemy`, `ThrownPotion`, `BeaconEntity`, `AnvilLoader`, `FortressPiece`, `StatWriter` |
| `List<?>` where the method returns `List<Entity>` | widen the local to `List<Entity>` | `World` |
| `int x = false;` (dead boolean in an int slot) | `int x = 0;` | `Biome`, `CavesGenerator` |
| lost narrowing on a `short` local | added `(short)` cast | `SkyBiome` |
| two `case` bodies declaring the same local | gave each `case` its own `{ }` scope | `Cathedral`, `OceanMonument`, `Pyramid`, `Tower` |
| a `JScrollPane` merged into an earlier `Component` local | split into its own variable | `ErrorDialog` |

### If upstream republishes

`fetchUpstream` fails loudly on a checksum mismatch rather than bootstrapping something the
patches were not built against. That already happened once: the `Latest.zip` this tree was
decompiled from was superseded by a "Cathedral schematic fix" upload, which changed
`struct/cathedral.bin` and `title/changelog.txt`. Both were adopted — the code decompiles
identically either way, so only those two resources moved.

To move to a new upstream build: update the URL and SHA in `tools/bootstrap.gradle`, run
`bootstrap`, fix whatever fails to apply, then `capturePatch`.
