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
client/src/main/java        decompiled client  (1435 files)
client/src/main/resources   textures, fonts, lang files (2214 files, verbatim)
server/src/main/java        decompiled server  (1113 files)
server/src/main/resources   17 files, verbatim
libs/vendor-common.jar      fastutil + org.json, lifted out of the shipped jars
libs/vendor-sound.jar       paulscode sound system + jorbis/jogg (client only)
run/                        game directories for runClient/runServer
```

Third-party libraries are **not** decompiled — they're re-jarred verbatim from the shipped
jars, so the bytecode you build against is exactly what the mod was compiled against.
LWJGL 2.9.4, jinput and jutils come from `libraries.minecraft.net` as normal Maven
dependencies; the natives are unpacked into `client/build/natives`.

## Multiplayer

The mod ships a working dedicated server. To play with friends: run the server, and have
everyone connect through the client's **Multiplayer → Direct Connect**.

`run/server/server.properties` sets `online-mode=false`, and that is not optional right
now: this build authenticates against `session.minecraft.net`, which no longer exists, so
`online-mode=true` rejects every login. Restoring real authentication means pointing the
handshake at the modern `sessionserver.mojang.com` endpoints — see the plan for details.

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

The tests live in `server/src/smoketest/java` and speak the real wire format using the
server's own packet classes, so they cannot drift from the game.

**Each bot runs in its own JVM, deliberately.** `UpdateChunkPacket` and `UpdateSectionPacket`
decode through a `private static byte[] temp` scratch buffer. Two clients decoding chunks
concurrently in one JVM corrupt each other and die with `Bad compressed data format`. One
client per process is what the real game does and the only safe arrangement — worth
remembering if anyone ever tries to run a client and a server in the same JVM (an
integrated / "Open to LAN" server would hit exactly this).

## Client and server are separate on purpose

They are **not** two views of one shared codebase — each module carries its own full copy
of the shared game code. They started out genuinely divergent; the multiplayer parity pass
reconciled the gameplay differences, and `./gradlew checkParity` now keeps them that way.

Current state — 980 same-path files, of which:

- **17 differ by `package`/`import` lines only.** The server renamed
  `net.minecraft.game.item.crafting` to `net.minecraft.game.item.recipe`, and keeps its
  packets flat in one package where the client uses sub-packages. No behaviour difference.
- **5 differ deliberately**, and are whitelisted in `checkParity`:
  - `Leaves`, `LeafPile`, `TransparentBlock` — the client's "fast leaves" graphics option.
    The server has no renderer, so there is nothing to reconcile.
  - `NetworkHandler`, `Packet` — eight packets kept vanilla-style names on the server
    (`Packet7UseEntity`) and got friendlier ones on the client (`UsePacket`). Same ids,
    same flows, byte-identical read/write logic, so the wire format is unaffected.
- **Everything else is byte-identical**, and `checkParity` fails the build if that stops
  being true.

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

Merging the two modules into one `common` module is now a much smaller job than it was,
but it still means resolving the package rename and the eight packet names first.

## Provenance / how to regenerate

Sources came from:

- client: `Latest.zip` → `jarmods/InfdevPlus-Client.jar`
- server: `Server-FOR-DEV-WORK-ONLY.jar`

decompiled with [Vineflower](https://github.com/Vineflower/vineflower) 1.12.0:

```bash
java -jar vineflower.jar -dgs=1 -rsy=1 -ind="    " \
     -e=<jdk8-rt.jar> -e=libs/vendor-common.jar -e=libs/vendor-sound.jar -e=<lwjgl jars> \
     <code-only.jar> <output-dir>
```

Vineflower produced zero warnings and every class round-trips: the rebuilt jars contain the
exact same class list as the originals (the only omission is fastutil's
`META-INF/versions/9/module-info.class`, which is inert on Java 8).

### Hand-fixes applied after decompiling

20 edits across 19 files, all mechanical decompiler artifacts — no behaviour changes:

| Symptom | Fix | Files |
| --- | --- | --- |
| `for (Mob x : List<Entity>)` — lost `checkcast` | iterate as the declared type, cast inside the loop | `Aggro`, `AggroWild`, `DetectEnemy`, `ThrownPotion`, `BeaconEntity`, `AnvilLoader`, `FortressPiece`, `StatWriter` |
| `List<?>` where the method returns `List<Entity>` | widen the local to `List<Entity>` | `World` |
| `int x = false;` (dead boolean in an int slot) | `int x = 0;` | `Biome`, `CavesGenerator` |
| lost narrowing on a `short` local | added `(short)` cast | `SkyBiome` |
| two `case` bodies declaring the same local | gave each `case` its own `{ }` scope | `Cathedral`, `OceanMonument`, `Pyramid`, `Tower` |
| a `JScrollPane` merged into an earlier `Component` local | split into its own variable | `ErrorDialog` |

If you ever re-decompile from scratch, you'll have to redo these.
