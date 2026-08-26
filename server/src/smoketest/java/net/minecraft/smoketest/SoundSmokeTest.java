package net.minecraft.smoketest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import net.minecraft.game.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.player.DigPacket;
import net.minecraft.network.packet.player.ItemSwitchPacket;
import net.minecraft.network.packet.player.PlacePacket;
import net.minecraft.network.packet.player.SlotPacket;
import net.minecraft.network.packet.world.SoundPacket;

/**
 * B-41: Multiplayer sound broadcasting for doors, interactables, and redstone.
 *
 * In multiplayer, WorldManager's sound listener methods were empty stubs and no
 * generic sound packet existed on the wire, so interacting with doors, trapdoors,
 * fence gates, chests, barrels, anvils, pistons, etc. played no sound on other
 * clients (and server-driven actions like redstone door toggles and chest closes
 * played no sound for anyone).
 *
 * Fixed with SoundPacket (packet id 62, SERVER_TO_CLIENT) carrying sound name,
 * coordinates, volume, pitch, and sound category; WorldManager broadcasting
 * through sendPacketToPlayerCoord; and player attribution on direct interactions
 * to avoid duplicate audio on the interacting client.
 *
 *   phase 0 - serialization: round-trip SoundPacket through Packet.writePacket
 *             and Packet.readPacket (clientbound decoder).
 *   phase 1 - door direct interaction: doorbot right-clicks a door, watchbot
 *             receives SoundPacket (random.door_open/close), doorbot receives
 *             no duplicate packet from the server.
 *   phase 2 - redstone door toggle: doorbot powers the door with a battery;
 *             both doorbot and watchbot receive SoundPacket.
 *   phase 3 - chest interaction: chest opened, sound packet broadcast received.
 *   phase 4 - block placement sound attribution: actor places a block, watcher
 *             receives SoundPacket (step.stone), actor receives no duplicate packet.
 *
 * Run: ./gradlew :server:smokeTest -PsmokeClass=net.minecraft.smoketest.SoundSmokeTest
 */
public class SoundSmokeTest {

    private static final int LAND_X = 875, LAND_Y = 150, LAND_Z = 768;
    private static final int WATCH_Z = LAND_Z + 3;

    private static final int TARGET_X = LAND_X + 1, TARGET_Y = LAND_Y, TARGET_Z = LAND_Z;

    private static final int STONE = 1;
    private static final int DIRT = 3;
    private static final int CHEST = 54;
    private static final int BATTERY = 198;
    private static final int DOOR_ITEM = 324;
    private static final int HOTBAR_START = 40;

    private static final int ARRIVAL_MS = 60000;

    private static final Checks checks = new Checks();

    private static String home;

    public static void main(String[] args) throws Exception {
        // ---------------- phase 0: serialization & protocol registration
        testSerialization();

        // ---------------- live phases: connect bots to running server
        Bot actor = Bot.join(args, "doorbot");
        Bot watcher = Bot.join(args, "watchbot");
        actor.settle(3000);
        watcher.settle(3000);
        if (actor.kickReason != null || watcher.kickReason != null) {
            checks.abort("a bot was kicked before the test began: doorbot=" + actor.kickReason
                    + " watchbot=" + watcher.kickReason);
        }

        home = " " + (int)actor.posX + " " + (int)actor.posY + " " + (int)actor.posZ;

        standOn(actor, LAND_X, LAND_Y, LAND_Z);
        placeFloor(actor, LAND_X, LAND_Y - 1, WATCH_Z);
        actor.chat("/teleport " + watcher.name + " " + LAND_X + " " + LAND_Y + " " + WATCH_Z);
        if (!watcher.followTeleport(ARRIVAL_MS)) {
            checks.abort("watchbot was never teleported next to doorbot - is doorbot in run/server/ops.txt?");
        }

        // ---------------- phase 1: door direct interaction
        testDoorInteraction(actor, watcher);

        // ---------------- phase 2: redstone door toggle
        testRedstoneDoorToggle(actor, watcher);

        // ---------------- phase 3: chest sound broadcast
        testChestSound(actor, watcher);

        // ---------------- phase 4: block placement sound attribution
        testBlockPlacementSound(actor, watcher);

        finish(actor, watcher);
    }

    private static void testSerialization() throws Exception {
        SoundPacket original = new SoundPacket("random.door_open", 10.5, 64.0, -20.5, 1.0F, 0.95F, SoundPacket.TYPE_BLOCK);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        Packet.writePacket(original, dos);
        dos.flush();

        byte[] bytes = baos.toByteArray();
        checks.check("packet writes with id 62 prefix", bytes[0] == 62,
                "expected packet id 62, got " + bytes[0]);

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bytes));
        Packet decoded = Packet.readPacket(dis, false);

        checks.check("clientbound decoder recognizes packet 62 as SoundPacket", decoded instanceof SoundPacket,
                "decoded as " + (decoded != null ? decoded.getClass().getSimpleName() : "null"));

        if (decoded instanceof SoundPacket) {
            SoundPacket sp = (SoundPacket) decoded;
            checks.check("soundName matches after serialization", "random.door_open".equals(sp.soundName),
                    "expected 'random.door_open', got '" + sp.soundName + "'");
            checks.check("coordinates match after serialization",
                    Math.abs(sp.x - 10.5) < 0.001 && Math.abs(sp.y - 64.0) < 0.001 && Math.abs(sp.z - (-20.5)) < 0.001,
                    "expected (10.5, 64.0, -20.5), got (" + sp.x + ", " + sp.y + ", " + sp.z + ")");
            checks.check("volume and pitch match after serialization",
                    Math.abs(sp.volume - 1.0F) < 0.01F && Math.abs(sp.pitch - 0.95F) < 0.01F,
                    "expected vol=1.0 pitch=0.95, got vol=" + sp.volume + " pitch=" + sp.pitch);
            checks.check("soundType matches after serialization", sp.soundType == SoundPacket.TYPE_BLOCK,
                    "expected TYPE_BLOCK (" + SoundPacket.TYPE_BLOCK + "), got " + sp.soundType);
        }
    }

    private static void testDoorInteraction(Bot actor, Bot watcher) throws Exception {
        // Ensure stone floor underneath and clear adjacent space
        placeFloor(actor, TARGET_X, TARGET_Y - 1, TARGET_Z);
        actor.chat("/setblock " + (TARGET_X + 1) + " " + TARGET_Y + " " + TARGET_Z + " " + DIRT);
        actor.drain(300);
        actor.send(new DigPacket(0, TARGET_X + 1, TARGET_Y, TARGET_Z, 1));
        actor.drain(200);
        actor.send(new DigPacket(2, TARGET_X + 1, TARGET_Y, TARGET_Z, 1));
        actor.drain(500);

        // Place fresh wooden door (lower half 64:0, upper half 64:8)
        actor.chat("/setblock " + TARGET_X + " " + TARGET_Y + " " + TARGET_Z + " 64:0");
        actor.drain(400);
        actor.chat("/setblock " + TARGET_X + " " + (TARGET_Y + 1) + " " + TARGET_Z + " 64:8");
        actor.drain(1000);

        // Drain any previous packets
        actor.drain(500);
        watcher.drain(500);

        // actorbot clicks the lower door block to open it
        actor.send(new PlacePacket(TARGET_X, TARGET_Y, TARGET_Z, 1, null));

        // watcherbot should receive SoundPacket (door_open)
        SoundPacket watchOpen = watcher.await(SoundPacket.class,
                p -> p.soundName != null && p.soundName.contains("door_open"),
                10000);

        checks.check("watcher receives SoundPacket when door is opened", watchOpen != null,
                "no SoundPacket(door_open) received by watcher within 10s of door click");

        if (watchOpen != null) {
            checks.check("door sound is categorized as TYPE_BLOCK", watchOpen.soundType == SoundPacket.TYPE_BLOCK,
                    "expected TYPE_BLOCK, got " + watchOpen.soundType);
            checks.check("door sound coordinate is near target",
                    Math.abs(watchOpen.x - (TARGET_X + 0.5)) < 1.0 && Math.abs(watchOpen.z - (TARGET_Z + 0.5)) < 1.0,
                    "expected near (" + (TARGET_X + 0.5) + ", " + (TARGET_Z + 0.5) + "), got (" + watchOpen.x + ", " + watchOpen.z + ")");
        }

        // actorbot should NOT receive duplicate SoundPacket (it predicted locally)
        SoundPacket actorPacket = actor.await(SoundPacket.class,
                p -> p.soundName != null && p.soundName.contains("door_open"),
                2000);
        checks.check("interacting player is excluded from server broadcast (no double-audio)", actorPacket == null,
                "actorbot received server SoundPacket when it already played sound locally");

        // actorbot clicks the door again to close it (leaves door in closed state for redstone phase)
        actor.drain(500);
        watcher.drain(500);
        actor.send(new PlacePacket(TARGET_X, TARGET_Y, TARGET_Z, 1, null));

        SoundPacket watchClose = watcher.await(SoundPacket.class,
                p -> p.soundName != null && p.soundName.contains("door_close"),
                10000);
        checks.check("watcher receives SoundPacket when door is closed", watchClose != null,
                "no SoundPacket(door_close) received by watcher within 10s of second click");
    }

    private static void testRedstoneDoorToggle(Bot actor, Bot watcher) throws Exception {
        actor.drain(500);
        watcher.drain(500);

        // Power the closed door with a charged battery on adjacent block
        placeFloor(actor, TARGET_X + 1, TARGET_Y - 1, TARGET_Z);
        actor.chat("/setblock " + (TARGET_X + 1) + " " + TARGET_Y + " " + TARGET_Z + " " + BATTERY + ":10");

        // Both bots should receive the server-driven redstone door sound
        SoundPacket watchPkt = watcher.await(SoundPacket.class,
                p -> p.soundName != null && p.soundName.contains("door_open"),
                10000);
        SoundPacket actorPkt = actor.await(SoundPacket.class,
                p -> p.soundName != null && p.soundName.contains("door_open"),
                10000);

        checks.check("watcher receives SoundPacket on redstone door toggle", watchPkt != null,
                "no SoundPacket on watcher for redstone door toggle");
        checks.check("actor receives SoundPacket on redstone door toggle", actorPkt != null,
                "no SoundPacket on actor for redstone door toggle");

        // Remove battery
        actor.chat("/setblock " + (TARGET_X + 1) + " " + TARGET_Y + " " + TARGET_Z + " " + DIRT);
        actor.drain(300);
        actor.send(new DigPacket(0, TARGET_X + 1, TARGET_Y, TARGET_Z, 1));
        actor.drain(200);
        actor.send(new DigPacket(2, TARGET_X + 1, TARGET_Y, TARGET_Z, 1));
        actor.drain(1000);
    }

    private static void testChestSound(Bot actor, Bot watcher) throws Exception {
        int chestX = TARGET_X + 2;
        // Place stone underneath and chest at separate target
        placeFloor(actor, chestX, TARGET_Y - 1, TARGET_Z);
        actor.chat("/setblock " + chestX + " " + TARGET_Y + " " + TARGET_Z + " " + CHEST);
        actor.drain(1500);

        actor.drain(500);
        watcher.drain(500);

        // actorbot opens the chest
        actor.send(new PlacePacket(chestX, TARGET_Y, TARGET_Z, 1, null));

        // Opening a chest triggers chest_open on server
        SoundPacket chestPkt = watcher.await(SoundPacket.class,
                p -> p.soundName != null && p.soundName.contains("chest_open"),
                10000);

        checks.check("watcher receives SoundPacket when chest is opened", chestPkt != null,
                "no SoundPacket(random.chest_open) received by watcher");

        // Clean up chest
        actor.chat("/setblock " + chestX + " " + TARGET_Y + " " + TARGET_Z + " " + DIRT);
        actor.drain(300);
        actor.send(new DigPacket(0, chestX, TARGET_Y, TARGET_Z, 1));
        actor.drain(200);
        actor.send(new DigPacket(2, chestX, TARGET_Y, TARGET_Z, 1));
        actor.drain(1000);
    }

    private static void testBlockPlacementSound(Bot actor, Bot watcher) throws Exception {
        int placeX = TARGET_X + 3;
        // Ensure air at place target and stone underneath
        placeFloor(actor, placeX, TARGET_Y - 1, TARGET_Z);
        actor.chat("/setblock " + placeX + " " + TARGET_Y + " " + TARGET_Z + " " + DIRT);
        actor.drain(300);
        actor.send(new DigPacket(0, placeX, TARGET_Y, TARGET_Z, 1));
        actor.drain(200);
        actor.send(new DigPacket(2, placeX, TARGET_Y, TARGET_Z, 1));
        actor.drain(500);

        // Give actor stone blocks
        int slot = stock(actor, STONE, 10);
        if (slot < 0) {
            checks.abort("could not stock stone into actor's hotbar");
        }
        actor.send(new ItemSwitchPacket(slot));
        actor.drain(500);
        watcher.drain(500);

        // actorbot places stone on top of (placeX, TARGET_Y - 1, TARGET_Z) with face 1 (top)
        actor.send(new PlacePacket(placeX, TARGET_Y - 1, TARGET_Z, 1, new ItemStack(STONE, 10, 0)));

        // watcherbot should receive SoundPacket (step.stone)
        SoundPacket watchPlace = watcher.await(SoundPacket.class,
                p -> p.soundName != null && p.soundName.contains("step.stone"),
                10000);

        checks.check("watcher receives SoundPacket when stone is placed", watchPlace != null,
                "no SoundPacket(step.stone) received by watcher within 10s of block place");

        if (watchPlace != null) {
            checks.check("placed block sound is categorized as TYPE_BLOCK", watchPlace.soundType == SoundPacket.TYPE_BLOCK,
                    "expected TYPE_BLOCK, got " + watchPlace.soundType);
        }

        // actorbot should NOT receive duplicate SoundPacket (it predicted locally)
        SoundPacket actorPacket = actor.await(SoundPacket.class,
                p -> p.soundName != null && p.soundName.contains("step.stone"),
                2000);
        checks.check("placing player is excluded from server broadcast (no duplicate placement audio)", actorPacket == null,
                "actorbot received server SoundPacket for its own block placement");

        // Clean up placed block
        actor.chat("/setblock " + placeX + " " + TARGET_Y + " " + TARGET_Z + " " + DIRT);
        actor.drain(300);
        actor.send(new DigPacket(0, placeX, TARGET_Y, TARGET_Z, 1));
        actor.drain(200);
        actor.send(new DigPacket(2, placeX, TARGET_Y, TARGET_Z, 1));
        actor.drain(500);
    }

    private static int stock(Bot bot, int itemId, int count) throws Exception {
        bot.drain(2000);
        bot.chat("/clearinv");
        bot.drain(2000);
        bot.chat("/give " + itemId + " " + count);
        SlotPacket given = bot.awaitLast(SlotPacket.class,
                p -> p.windowId == 0 && p.myItemStack != null && p.myItemStack.itemID == itemId, 6000);
        if (given == null) {
            java.util.List<SlotPacket> any = bot.awaitAll(SlotPacket.class, null, 3000);
            checks.note("no slot update; " + any.size() + " other slot packets in the window");
            for (SlotPacket sp : any) {
                checks.note("got SlotPacket win=" + sp.windowId + " slot=" + sp.itemSlot + " item=" + (sp.myItemStack != null ? sp.myItemStack.itemID : "null"));
            }
            return -1;
        }
        if (given.itemSlot < HOTBAR_START) {
            checks.note("item landed in slot " + given.itemSlot + ", outside hotbar (HOTBAR_START=" + HOTBAR_START + ")");
            return -1;
        }
        return given.itemSlot - HOTBAR_START;
    }

    private static void standOn(Bot bot, int x, int y, int z) throws Exception {
        bot.chat("/teleport " + x + " " + y + " " + z);
        if (!bot.followTeleport(ARRIVAL_MS)) {
            checks.abort("no teleport came back for /teleport " + x + " " + y + " " + z
                    + " - is " + bot.name + " in run/server/ops.txt?");
        }
        bot.drain(3000);
        placeFloor(bot, x, y - 1, z);
    }

    private static void placeFloor(Bot bot, int x, int y, int z) throws Exception {
        bot.chat("/setblock " + x + " " + y + " " + z + " " + DIRT);
        bot.drain(600);
        bot.chat("/setblock " + x + " " + y + " " + z + " " + STONE);
        bot.drain(900);
    }

    private static void finish(Bot actor, Bot watcher) {
        try {
            if (home != null) {
                actor.chat("/teleport " + actor.name + home);
                if (watcher != null) {
                    actor.chat("/teleport " + watcher.name + home);
                }
                actor.drain(2500);
            }
        } catch (Exception ignored) {
        }
        actor.close();
        if (watcher != null) {
            watcher.close();
        }
        checks.done();
    }
}
