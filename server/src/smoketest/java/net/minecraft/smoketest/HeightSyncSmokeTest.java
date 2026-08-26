package net.minecraft.smoketest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;

import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldSettings;
import net.minecraft.game.world.chunk.Chunk;
import net.minecraft.game.world.chunk.loader.handler.AnvilSaveHandler;
import net.minecraft.game.world.chunk.loader.handler.SaveHandler;
import net.minecraft.network.packet.world.BlockEventPacket;
import net.minecraft.network.packet.player.DigPacket;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.player.PlacePacket;
import net.minecraft.network.packet.player.PlayerTeleportPacket;
import net.minecraft.network.packet.world.UpdateBlockPacket;
import net.minecraft.network.packet.world.UpdateChunkPacket;
import net.minecraft.network.packet.world.UpdateSectionPacket;

/**
 * Verifies that the wire format carries content above Y=255 (sections 16-19 of a
 * 20-section, 320-block world).
 *
 * Two layers:
 *
 *   1. Wire round-trip (always runs, needs no server). The server's own packet
 *      classes are serialized to a byte stream and read back through the real
 *      framing (Packet.writePacket / Packet.readPacket), then asserted to still
 *      describe blocks and sections above Y=255. This is the load-bearing check:
 *      with 16-bit section masks or byte/short-packed coordinates it goes red.
 *
 *   2. Live check (only when "live" is passed as the third arg; needs an
 *      ALREADY-RUNNING server). Logs in as HeightBot, waits for its spawn chunks,
 *      then places stone at Y=300 under its own feet with /setblock and asserts
 *      the resulting block update reaches it with Y=300 intact. The bot must be
 *      an operator: put its name in run/server/ops.txt and restart the server.
 *
 * Run:  ./gradlew :server:smokeTest -PsmokeClass=net.minecraft.smoketest.HeightSyncSmokeTest
 * Live: ./gradlew :server:smokeTest -PsmokeClass=net.minecraft.smoketest.HeightSyncSmokeTest \
 *           -PsmokeArgs="live"
 */
public class HeightSyncSmokeTest {

    private static final Checks checks = new Checks();

    public static void main(String[] args) throws Exception {
        wireRoundTrip();

        if (args.length > 2 && args[2].equals("live")) {
            liveCheck(args);
        } else {
            checks.skip("live check (pass smokeArgs=\"live\" against a running server)");
        }

        checks.done();
    }

    // ---------------------------------------------------------------- wire round-trip

    private static void wireRoundTrip() throws Exception {
        File dir = new File(System.getProperty("java.io.tmpdir"), "mc-height-wire-" + System.nanoTime());
        dir.mkdirs();
        SaveHandler save = new AnvilSaveHandler(dir, "heightwire", true);
        World world = new World(save, "heightwire", 20250101L, new WorldSettings());

        checks.check("test world has 20 sections", world.getMaxSections() == 20,
                "maxSections=" + world.getMaxSections());

        // A chunk with content in section 0, at the old Y=255 boundary, and in
        // sections 18 and 19. The block at Y=315 has an id > 255, which forces
        // the section's upper (12th-12th bit) block array to exist.
        Chunk src = new Chunk(world, 3, 7);
        src.setBlockID(5, 5, 9, 1);
        src.setBlockMetadata(5, 5, 9, 3);
        src.setBlockID(1, 255, 1, 1);
        src.setBlockID(6, 300, 7, 1);
        src.setBlockMetadata(6, 300, 7, 5);
        src.setBlockID(2, 315, 3, 258);

        // Full-chunk (initialize) packet, as sent to a player who just entered the chunk.
        UpdateChunkPacket fullOut = new UpdateChunkPacket(src, true, 0);
        UpdateChunkPacket full = roundTrip(fullOut, false);

        checks.check("full-chunk mask covers section 18 (Y 288-303)",
                (full.hasLowerBlocks & 1 << 18) != 0,
                "mask=0x" + Integer.toHexString(full.hasLowerBlocks));
        checks.check("full-chunk mask covers section 19 (Y 304-319)",
                (full.hasLowerBlocks & 1 << 19) != 0,
                "mask=0x" + Integer.toHexString(full.hasLowerBlocks));
        checks.check("full-chunk upper mask covers section 19",
                (full.hasUpperBlocks & 1 << 19) != 0,
                "upper=0x" + Integer.toHexString(full.hasUpperBlocks));
        checks.check("full-chunk upper mask excludes section 18 (no id>255 there)",
                (full.hasUpperBlocks & 1 << 18) == 0,
                "upper=0x" + Integer.toHexString(full.hasUpperBlocks));

        // The client allocates exactly this many bytes for the decompressed payload
        // (12288 per section + 2048 per upper array + 256 biomes) and rejects the
        // packet on mismatch, so the wire payload must match it.
        int expectedSize = 12288 * Integer.bitCount(full.hasLowerBlocks)
                + 2048 * Integer.bitCount(full.hasUpperBlocks) + 256;
        checks.check("decompressed chunk data matches the layout the client expects",
                full.chunkData.length == expectedSize,
                "expected=" + expectedSize + " actual=" + full.chunkData.length);

        // Apply it the way the client does and check every height survived.
        Chunk dst = new Chunk(world, 3, 7);
        dst.setChunkData(full.chunkData, full.hasLowerBlocks, full.hasUpperBlocks, true);
        checks.check("block round-trips at Y=5",
                dst.getBlockID(5, 5, 9) == 1 && dst.getBlockMetadata(5, 5, 9) == 3,
                "id=" + dst.getBlockID(5, 5, 9) + " meta=" + dst.getBlockMetadata(5, 5, 9));
        checks.check("block round-trips at Y=255 (old boundary)",
                dst.getBlockID(1, 255, 1) == 1,
                "id=" + dst.getBlockID(1, 255, 1));
        checks.check("block round-trips at Y=300 (section 18)",
                dst.getBlockID(6, 300, 7) == 1 && dst.getBlockMetadata(6, 300, 7) == 5,
                "id=" + dst.getBlockID(6, 300, 7) + " meta=" + dst.getBlockMetadata(6, 300, 7));
        checks.check("id>255 block round-trips at Y=315 (section 19)",
                dst.getBlockID(2, 315, 3) == 258,
                "id=" + dst.getBlockID(2, 315, 3));

        // Partial mask packet, as sent for multi-block resyncs. The mask bit for
        // section 18 must survive the wire even when no other sections are included.
        UpdateChunkPacket partial = roundTrip(new UpdateChunkPacket(src, false, 1 << 18), false);
        checks.check("partial-chunk mask keeps bit 18",
                (partial.hasLowerBlocks & 1 << 18) != 0,
                "mask=0x" + Integer.toHexString(partial.hasLowerBlocks));
        Chunk dst2 = new Chunk(world, 3, 7);
        dst2.setChunkData(partial.chunkData, partial.hasLowerBlocks, partial.hasUpperBlocks, false);
        checks.check("partial-chunk block round-trips at Y=300",
                dst2.getBlockID(6, 300, 7) == 1,
                "id=" + dst2.getBlockID(6, 300, 7));

        // Multi-block update: coordinates are packed into one value per block.
        // Y=300 needs more than 8 bits, so the packing must carry it.
        world.setBlockAndMetadataWithNotify(53, 300, 118, 1, 5);
        int packed = (53 & 15) << 20 | (118 & 15) << 16 | 300;
        UpdateSectionPacket section = roundTrip(new UpdateSectionPacket(3, 7, new int[] { packed }, 1, world), false);
        int coord = section.coordinateArray[0];
        int cx = coord >> 20 & 15;
        int cz = coord >> 16 & 15;
        int cy = coord & 0xFFFF;
        checks.check("multi-block update coordinate keeps Y=300",
                cx == 5 && cz == 6 && cy == 300,
                "decoded=(" + cx + "," + cy + "," + cz + ")");
        checks.check("multi-block update carries the block id/meta",
                section.typeArray[0] == 1 && section.metadataArray[0] == 5,
                "id=" + section.typeArray[0] + " meta=" + section.metadataArray[0]);

        // Single-block update: Y is a short on the wire, must hold 300.
        UpdateBlockPacket block = roundTrip(new UpdateBlockPacket(53, 300, 118, world), false);
        checks.check("single-block update keeps Y=300",
                block.yPosition == 300 && block.type == 1 && block.metadata == 5,
                "y=" + block.yPosition + " id=" + block.type + " meta=" + block.metadata);

        // Client->server dig and place: Y was one byte on the wire (0-255).
        DigPacket dig = roundTrip(new DigPacket(0, 53, 300, 118, 2), true);
        checks.check("dig packet keeps Y=300", dig.yPosition == 300, "y=" + dig.yPosition);
        PlacePacket place = roundTrip(new PlacePacket(53, 319, 118, 1, null), true);
        checks.check("place packet keeps Y=319", place.yPosition == 319, "y=" + place.yPosition);

        // Block events (chests, note blocks...) carry a Y that was one byte.
        BlockEventPacket event = roundTrip(new BlockEventPacket(1, 53, 300, 118, 7), false);
        checks.check("block event keeps Y=300",
                event.x == 53 && event.y == 300 && event.z == 118,
                "y=" + event.y);

        deleteRecursive(dir);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Packet> T roundTrip(T packet, boolean serverbound) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bos);
        Packet.writePacket(packet, out);
        out.flush();
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(bos.toByteArray()));
        Packet read = Packet.readPacket(in, serverbound);
        if (read == null || read.getClass() != packet.getClass()) {
            throw new IllegalStateException("round trip returned " + read);
        }
        return (T) read;
    }

    private static void deleteRecursive(File file) {
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                deleteRecursive(child);
            }
        }
        file.delete();
    }

    // ---------------------------------------------------------------- live check

    private static void liveCheck(String[] args) {
        try {
            runLiveBot(args);
        } catch (Exception e) {
            checks.check("live check ran", false, "threw " + e);
        }
    }

    private static void runLiveBot(String[] args) throws Exception {
        Bot bot = Bot.join(args, "HeightBot");
        if (bot.login.maxSections < 20) {
            checks.check("live: server world is 20 sections tall", false,
                    "maxSections=" + bot.login.maxSections + "; this test needs a 320-block world");
            return;
        }

        // Chunk streaming runs from the server's movement handlers, so a bot
        // that never moves never receives chunks. Bot pumps position for us
        // once the login teleport has been read (one chunk per packet, see
        // EntityPlayerMP).
        if (bot.await(PlayerTeleportPacket.class, 15000) == null) {
            checks.check("live: server sent a spawn position", false, "no PlayerTeleportPacket within 15s");
            return;
        }

        int cx = (int) Math.floor(bot.posX / 16.0);
        int cz = (int) Math.floor(bot.posZ / 16.0);
        // Once our own chunk has been sent the server considers it loaded and
        // will deliver block updates for it.
        UpdateChunkPacket own = bot.await(UpdateChunkPacket.class, p -> p.xCh == cx && p.zCh == cz, 20000);
        if (own == null) {
            checks.check("live: our own chunk streamed in", false,
                    "no UpdateChunkPacket for (" + cx + ", " + cz + ") within 20s"
                            + (bot.kickReason == null ? "" : " - kicked: " + bot.kickReason));
            return;
        }

        int x = (int) Math.floor(bot.posX);
        int z = (int) Math.floor(bot.posZ);
        // Glass first, then stone: re-running this test against a world where
        // the block is already stone would make stone->stone a no-op in
        // World.setBlockAndMetadata (no change, no update packet). Starting
        // from a known state keeps the test deterministic; the cleanup below
        // leaves glass behind. (Air is not usable for this: id 0 has no
        // BlockList entry, so /setblock rejects it as an invalid block.)
        bot.chat("/setblock " + x + " 300 " + z + " 20");
        bot.chat("/setblock " + x + " 300 " + z + " 1");

        String kind = awaitHeightUpdate(bot, 15000);
        checks.check("live: block placed at Y=300 reached the placing client", kind != null,
                bot.kickReason != null ? "kicked: " + bot.kickReason
                        : "no block update for Y=300 arrived after /setblock"
                                + " - the bot must be op; add HeightBot to run/server/ops.txt and restart the server");
        if (kind != null) {
            checks.note("live: " + kind);
        }

        bot.chat("/setblock " + x + " 300 " + z + " 20");
        bot.drain(500);
        bot.close();
    }

    /** Any of the three shapes a block change at Y=300 can reach a client as. */
    private static String awaitHeightUpdate(Bot bot, long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline && bot.kickReason == null) {
            Packet next = bot.read();
            if (next instanceof UpdateBlockPacket) {
                UpdateBlockPacket ub = (UpdateBlockPacket) next;
                if (ub.yPosition == 300 && ub.type == 1) {
                    return "UpdateBlockPacket y=" + ub.yPosition + " id=" + ub.type;
                }
            } else if (next instanceof UpdateSectionPacket) {
                UpdateSectionPacket us = (UpdateSectionPacket) next;
                for (int i = 0; i < us.sizeOfAllArrays; i++) {
                    if ((us.coordinateArray[i] & 0xFFFF) == 300 && us.typeArray[i] == 1) {
                        return "UpdateSectionPacket y=300 id=" + us.typeArray[i];
                    }
                }
            } else if (next instanceof UpdateChunkPacket) {
                UpdateChunkPacket uc = (UpdateChunkPacket) next;
                if ((uc.hasLowerBlocks & 1 << 18) != 0) {
                    return "UpdateChunkPacket mask=0x" + Integer.toHexString(uc.hasLowerBlocks);
                }
            }
        }
        return null;
    }
}
