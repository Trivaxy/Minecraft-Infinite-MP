# Working notes for agents

## Current goal: bug-fixing phase

Multiplayer works end to end — login, chat, chunk streaming, movement, and all five dimensions
running server-side. What's left before new features is a backlog of real bugs found by playing.

**We are not starting Phase 4 (dimension transfer) until the board below is clear.** Layering
dimension transfer on top of unverified movement and sync would make transfer bugs nearly
impossible to diagnose.

### How a session works

**One bug per session.** Pick the top open bug, fix it, verify it, mark it done, stop. Don't
opportunistically fix three things — the point is that each fix gets a real verification pass
instead of a plausible-looking diff.

A session is done when:

1. The bug has a **named root cause**, not a symptom description. "The counter is only reset in
   the branch where it is never read" — not "players get kicked".
2. The fix is **verified against something that fails without it.** A log line from real play, a
   smoke test that goes red when reverted, or a measurement. If a bug can be caught by a bot,
   write the bot — see the smoke tests, which is how the movement bugs became testable.
3. `./gradlew checkParity build` is clean and `:server:smokeTest` passes.
4. The board below is updated with what was found, including anything ruled out.

Ruling a suspicion out is a real result. Record it.

---

## Bug board

### Open

| # | Bug | Where | Status |
|---|---|---|---|
| B-01 | **Spawn point can land in open ocean.** `findSpawnPoint` random-walks ±64 blocks looking for solid ground and gives up after 1000 attempts, then uses wherever the walk stopped — no suitability check, no warning. Observed spawning ~1000 blocks from origin in water, which is about what 1000 random steps gets you. | `game/world/dimension/Dimension.java:100` | Diagnosed by reading, **not yet confirmed** |
| B-02 | **Container contents may not sync when several blocks change in one chunk in one tick.** The tile-entity loop decodes packed coordinates out of `numBlocksToUpdate` (the *count*) where it should read `blocksToUpdate[i2]`. Repro: place ~6 chests in one chunk within a second, put items in, have the other player open each one. | `server/world/ServerChunkDistance.java:290-292` | Predicted, **unconfirmed** |
| B-03 | **Chunks may stream in slowly.** One chunk per movement packet, gated on fewer than 4 packets pending. Symptom would be terrain crawling in while running or flying. | `server/player/EntityPlayerMP.java:223` | Predicted; **premise confirmed during B-04 live verification** — a stationary client receives `PrepChunkPacket`s but zero chunk *data*; streaming only advances on movement packets (`HeightSyncSmokeTest` live mode has to pump position packets) |

B-01 affects singleplayer too — it is not a multiplayer regression.

B-02 note: the packed block-update coordinates changed in B-04 — they are now `int`s laid out
`x << 20 | z << 16 | y` (decode: `x = c >> 20 & 15`, `z = c >> 16 & 15`, `y = c & 0xFFFF`). The
loop's actual bug — decoding out of `numBlocksToUpdate` (the count) instead of
`blocksToUpdate[i2]` — was left untouched and now uses the new layout.

### Fixed

| # | Bug | Root cause | Verified by |
|---|---|---|---|
| B-00a | Every player kicked ~4s after they started moving ("floating too long") | `playerInAirTime` was reset only inside the `allow-flight=true` branch — the branch where it is never read — so with the default it was a monotonic counter. It also tested `d15`, which the line above forces to `0.0` unconditionally, so the hovering question was never actually asked. | Real play log (kick at exactly 80 ticks) → `MovementSmokeTest` walks 15s clean |
| B-00b | Being kicked crashed the client instead of showing the disconnect screen | `NetworkWorld.tick()` drains packets, so the kick lands inside `Minecraft.runTick`'s `world.tick()` call; `ClientHandler` responds with `changeWorld(null)`, nulling `world` and `player` while `runTick` is still on the stack. | Guarded; covers every disconnect path (kick, lost connection, server stop) |
| B-00c | Every server-initiated teleport placed the player 1.62 blocks too high, including on login | `PlayerTeleportPacket(x, y, stance, z)` was called as `(x, y + 1.62, y, z)` — y and stance swapped. | `MovementSmokeTest` reports 0 rubber-bands |
| B-00d | `save-on` disabled saving and `save-off` enabled it; every later `save-all` silently did nothing | `setSaving(true)` is what *suspends* saving (`canSave()` returns `!levelSaving`). The two commands had the booleans the wrong way round. | Console: `save-off` → `save-all` writes nothing; `save-on` → `save-all` writes all five worlds |
| B-04 | Multiplayer could not carry blocks above Y=255: sections 16–19 of the 20-section world never synced, and dig/place above Y=255 corrupted | The wire format was 256-high in four places: `UpdateChunkPacket` sent its section masks as 16-bit shorts (`& 0xFFFF` truncation) and `ServerChunkDistance` clamped section bits ≥ 16 out of the update mask; `UpdateSectionPacket` packed coordinates into a short with only 8 bits of Y; `DigPacket`/`Packet15Place`/`BlockEventPacket` sent Y as one byte. The client chunk-size check also assumed 14336 bytes per section (an upper nibble array for *every* section) where the writer only sends one for sections with block ids > 255. | Masks widened to 32-bit ints and Y fields to shorts in both trees; exact-size check computed from both masks. `HeightSyncSmokeTest` round-trips blocks at Y=5/255/300/315 through real packet framing (goes red with the 16-bit mask restored — negative test) and its live mode `/setblock`s at Y=300 against a running server, receiving the block back as `UpdateSectionPacket y=300 id=1`. `PROTOCOL_VERSION` bumped to 20260810 so a stale client jar disconnects at login instead of misparsing |
| B-04b | `Chunk.setChunkData` diverged between the trees (`checkParity` red before this session touched anything) | The client copy had bounds-guarded `arraycopy`s (tolerating short payloads); the server tree still had the raw copies. | Client version mirrored into the server tree; `checkParity` clean |

---

## Verification

```bash
./gradlew checkParity build                 # must be clean before any session ends
./gradlew :server:runServer                 # terminal 1
./gradlew :server:smokeTest                 # two clients: login, chunks, chat relay
./gradlew :server:smokeTest -PsmokeClass=net.minecraft.smoketest.MovementSmokeTest \
                            -PsmokeArgs="Walker 15"
./gradlew :server:smokeTest -PsmokeClass=net.minecraft.smoketest.LoginSmokeTest \
                            -PsmokeArgs="Alice 12"
./gradlew :server:smokeTest -PsmokeClass=net.minecraft.smoketest.HeightSyncSmokeTest
./gradlew :server:smokeTest -PsmokeClass=net.minecraft.smoketest.HeightSyncSmokeTest \
                            -PsmokeArgs="live"   # needs a running server + HeightBot in run/server/ops.txt
./gradlew :server:setPlayerDimension -PsmokePlayer=Alice -PsmokeDimension=1   # server stopped
```

The smoke tests speak the real wire format using the server's own packet classes, so they cannot
drift from the game. **Each bot runs in its own JVM deliberately** — see DEVELOPING.md.

Known blind spot, and the one that hid B-00a for a whole phase: **bots that don't move never send
movement packets.** When a bug lives in a code path no bot exercises, extend the bots rather than
hand the test to a human.

### Still needs a human

Baseline items B2 and B4–B16 (server list ping, block/chest/furnace sync, crafting, mobs, item
pickup, PvP, death and respawn, sleeping, rejoin persistence, console commands). See the plan's
Verification section. These have never been run.

---

## Where things live

| | |
|---|---|
| Build, layout, provenance, multiplayer and dimension docs | `DEVELOPING.md` |
| Full multiplayer plan with line-level detail | `~/.claude/plans/fancy-swinging-pike.md` (outside the repo) |
| Smoke tests and test tooling | `server/src/smoketest/java/net/minecraft/smoketest/` |
| Dev server config | `run/server/server.properties` (gitignored) |

The plan file lives outside the repo, so **Future work** below carries enough to start any phase
without it. Go to the plan for exact line numbers and the reasoning behind each decision.

---

## House rules

- **Two source trees, one game.** `client/` and `server/` each carry a full copy of the shared
  code. A fix applied to one and not the other is a silent desync. `checkParity` fails the build
  when a shared file diverges beyond the documented exceptions — do not add exceptions to make it
  pass without a comment explaining why the difference is intended.
- **Server-only paths** (`net/minecraft/server/…`) and client-only paths
  (`net/minecraft/client/…`) don't exist in the other tree, so they're free to edit alone.
- **Negative-test load-bearing fixes.** If a line is holding something important up, remove it and
  watch the failure before trusting it. This is how the `level.dat` flush and `checkParity` itself
  were validated.
- **This is decompiled source.** Inverted booleans, tautological conditions and swapped arguments
  are the house style of bug here — three of the four fixed bugs above are exactly that. When
  something looks wrong, it usually is.
- `online-mode=false` is a **stopgap**, not the end state. Modern auth against
  `sessionserver.mojang.com` is deferred, not abandoned; keep `online-mode` working as a config
  switch.

---

# Future work

Nothing below is started. The bug board comes first.

## Phase status

| Phase | Scope | State |
|---|---|---|
| 0 | Authentication + `server.properties` | done |
| 1 | Latent crash cleanup | done |
| 2 | Client/server content parity | done |
| 3 | Server goes multi-world | done |
| 4 | Dimension transfer via `/dimension` | **future** |
| 5 | Organic dimension travel | **future** |
| 6 | Ocean dimension + polish | **future** |
| — | Modern authentication | **deferred, needs research** |

Decisions already taken, and not up for re-litigation: full parity across all five dimensions;
dedicated server only (no integrated server / Open to LAN); the server is authoritative on
content differences.

## Phase 4 — `/dimension` transfer

The machinery mostly exists. `Player.switchDimension` is an **empty stub** server-side and
`EntityPlayerMP` never overrides it, which is why `/dim` does nothing today. All the travel rules
currently live in the client-only `LocalPlayer`.

**Server-side travel rules.** Add an `EntityPlayerMP.switchDimension(int, boolean)` that ports
`LocalPlayer`'s decision tree, with two deliberate deviations:

- `forced` must `return` early. `LocalPlayer` has no `return` there, so `/dim` falls through into
  the organic checks and can double-switch — `/dim overworld` while below y=0 bounces you straight
  to Hell.
- Achievements, stats and the credits screen stay client-side. `EntityPlayerMP` has no `addStat`
  override, so `Player.addStat` is already a no-op there.

**Do not transfer synchronously.** `Player.onTick()` calls `switchDimension(0, false)` *every
tick*, and on the server that runs from inside `NetServerHandler`'s movement handlers — i.e.
inside packet dispatch, which is about to tick the very entity you would be replacing. Defer
through the existing `MinecraftServer.mainThreadEvents` queue (drained one per tick), and guard
with a `pendingDimension` field: without it, a player standing below y=0 enqueues 20 transfers a
second.

**Reuse `ServerConfigurationManager.recreatePlayerEntity`, don't rewrite it.** It already untracks,
removes from the old chunk manager and world, forces the arrival chunk loaded, un-suffocates,
sends `RespawnPacket`, teleports, re-registers and respawns. Split it into a shared core with two
entry points (death respawn / travel). Four things the core needs that it doesn't do today:

1. **Preserve player state.** It copies only `entityId` and the net handler — inventory, health,
   hunger, effects and score are lost. Correct for death, fatal for travel. `Player.clone(old,
   true)` copies the right set.
2. **Repoint `NetServerHandler.playerEntity`** at the new entity, inside the core. Only one call
   site does this today, so any other caller leaves the handler driving a dead object.
3. **Positioning.** Use the travel arrival position unconditionally when there is one; keep the
   existing bed-spawn branch byte-for-byte for the death path.
4. **Container hygiene.** `clone(old, true)` shares the old container, whose listener list still
   holds the old player — close the old crafting GUI before cloning or every slot update goes out
   twice.

`ServerConfigurationManager.removePlayersFromList` is currently dead code kept deliberately; it is
the intended chunk-manager handoff helper for this phase.

**Arrival terrain rules** split by ownership. The server owns: dimension-3 randomisation, the
top-tile search *on the target world*, return-to-Overworld bed/world spawn, the Hell lava landing
platform, the Sky empty-space cloud, and clearing the portal block. The client keeps
achievements, visit stats, ambience, `GabrielStaff.canUseStaff`, the `gui.dim_N` message and the
credits check.

**Client side needs no protocol change.** `RespawnPacket` carries a dimension byte and that is
enough — position follows via teleport, chunks via the chunk manager, and seed and height are
unchanged across dimensions. Keeping the wire format frozen means a new server still works against
an unmodified client jar. `ClientHandler.handleRespawn` currently ignores `packet.dimension`
entirely; it needs to build a fresh `NetworkWorld` and hand it to a new `Minecraft.enterDimensionMP`.
Do **not** route through `Minecraft.switchDimension(int)` — that builds a plain local `World` with
a real chunk provider and would sever the connection.

`NetworkPlayer.switchDimension` staying an empty no-op is **correct**, and is what resolves the
ordering hazard: both sides call it every tick, the client swallows it, and `EntityPlayerMP`'s is
the only implementation that acts. The client never simulates travel; it waits to be told.

## Phase 5 — Organic travel

No new code beyond Phase 4 — the rules already fire from `Player.onTick`. This phase is testing
and fixing what surfaces: falling below y=0 into Hell, falling out of Sky, flying above Hell's
ceiling, end portals to Void and back, Dream Catcher sleep into Sky, Respawn Anchor (Hell only)
and Altar (Sky only), teleporters indexing per-dimension, and two players travelling at once —
they will arrive a tick apart because the event queue drains one per tick.

Sleeping outside the Overworld should spawn a Phantom and force-wake with **no** dimension switch.

## Phase 6 — Ocean dimension

Ocean (4) has **no entry point today, in singleplayer either.** `DimensionCommand` refuses ids
≥ 4, nothing calls `switchDimension(4, …)`, and `gui.lang` only has `gui.dim_0`…`gui.dim_3`.
Note `WorldType.OCEAN` is a different feature — it makes the *Overworld* an ocean world.

Phases 3–5 build `worlds[4]` as working infrastructure and leave the guard in place. Phase 6 lifts
it deliberately: an `"ocean" → 4` command mapping, a `gui.dim_4` string, ambience and achievement
cases, and matching command suggestions on both sides. `Player.getSpawn(3)` and `getSpawn(4)` both
alias to `playerSpawn` — harmless while neither is respawnable, but revisit if Ocean becomes
livable.

## Deferred — modern authentication

Offline mode unblocks everything; this replaces it once multiplayer is solid. **Needs research
against a real modern Minecraft source** — the notes below are informed guesses, not facts.

Today both sides talk to `session.minecraft.net` (`joinserver.jsp` from the client,
`checkserver.jsp` from the server). Both endpoints are long dead. The modern equivalents are
believed to be `POST sessionserver.mojang.com/session/minecraft/join` and
`GET …/hasJoined?username=&serverId=`.

**The promising part:** the shape of the existing flow already matches — the server mints a
`serverId`, the client registers it with Mojang, the server asks Mojang to confirm. Modern
Minecraft derives `serverId` from a SHA-1 over the shared secret and public key, which normally
drags in the whole encryption handshake — but Mojang's session server only ever *string-compares*
what the client registered against what the server queries. If both sides use the same plaintext
random `serverId` this codebase already generates, identity verification may work **without
implementing packet encryption at all**. Verify before relying on it.

Open questions: whether the client actually receives a usable access token (Prism/MultiMC passes
one as `args[1]`, so probably yes for free); where the player **UUID** comes from, since this
protocol never carries one; whether to migrate player `.dat` files from username-keyed to
UUID-keyed (username-keyed is fine for a friends server); and **TLS 1.2 on Java 8** — late 8
updates handle Mojang's cert chain, early ones don't, and the whole project is pinned to Java 8 by
LWJGL 2.9.4. JSON parsing is already available: `org.json` is vendored in `libs/vendor-common.jar`.

## Known unknowns

- **Client and server are still two source trees.** Merging them into one `common` module is much
  smaller than it was after the parity pass, but still means resolving the
  `game.item.crafting` → `game.item.recipe` rename and eight packet class names first.
- **`BiomeList.enabledBiomes` is static** and reassigned in every `World` constructor. Benign
  while all five dimensions load from the same `level.dat`; a real bug the moment per-dimension
  biome settings appear.
- **First entry into a fresh dimension** generates its spawn chunks synchronously. Measured at one
  ~2s stall, so no warming is needed — but that was one machine, one seed.
- **Wire format changed in B-04.** Section masks widened short→int, `UpdateSectionPacket` coords
  short→int (layout `x << 20 | z << 16 | y`), and dig/place/block-event Y byte→short. This is a
  breaking change vs. the original protocol — the Phase 4 note about "keeping the wire format
  frozen" no longer holds for pre-B-04 clients. `PROTOCOL_VERSION` was bumped (20260810) so a stale
  client jar is rejected at login rather than misparsing chunk data.
