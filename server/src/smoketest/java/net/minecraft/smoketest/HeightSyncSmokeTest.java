package net.minecraft.smoketest;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.InetSocketAddress;
import java.net.Socket;

import net.minecraft.SharedConstants;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldSettings;
import net.minecraft.game.world.chunk.Chunk;
import net.minecraft.game.world.chunk.loader.handler.AnvilSaveHandler;
import net.minecraft.game.world.chunk.loader.handler.SaveHandler;
import net.minecraft.network.packet.BlockEventPacket;
import net.minecraft.network.packet.ChatPacket;
import net.minecraft.network.packet.DigPacket;
import net.minecraft.network.packet.DisconnectPacket;
import net.minecraft.network.packet.HandshakePacket;
import net.minecraft.network.packet.KeepAlivePacket;
import net.minecraft.network.packet.LoginPacket;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet15Place;
import net.minecraft.network.packet.PlayerPositionPacket;
import net.minecraft.network.packet.PlayerTeleportPacket;
import net.minecraft.network.packet.UpdateBlockPacket;
import net.minecraft.network.packet.UpdateChunkPacket;
import net.minecraft.network.packet.UpdateSectionPacket;

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

    public static void main(String[] args) throws Exception {
        int failures = 0;

        failures += wireRoundTrip();

        if (args.length > 2 && args[2].equals("live")) {
            failures += liveCheck(args[0], Integer.parseInt(args[1]));
        } else {
            System.out.println("[test] SKIP  live check (pass smokeArgs=\"live\" against a running server)");
        }

        System.out.println(failures == 0 ? "[test] RESULT: ALL PASS" : "[test] RESULT: " + failures + " FAILURE(S)");
        System.exit(failures == 0 ? 0 : 1);
    }

    private static int check(String name, boolean ok, String detail) {
        System.out.println("[test] " + (ok ? "PASS  " : "FAIL  ") + name
                + (ok || detail.isEmpty() ? "" : "   -> " + detail));
        return ok ? 0 : 1;
    }

    // ---------------------------------------------------------------- wire round-trip

    private static int wireRoundTrip() throws Exception {
        File dir = new File(System.getProperty("java.io.tmpdir"), "mc-height-wire-" + System.nanoTime());
        dir.mkdirs();
        SaveHandler save = new AnvilSaveHandler(dir, "heightwire", true);
        World world = new World(save, "heightwire", 20250101L, new WorldSettings());

        int failures = 0;
        failures += check("test world has 20 sections", world.getMaxSections() == 20,
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

        failures += check("full-chunk mask covers section 18 (Y 288-303)",
                (full.hasLowerBlocks & 1 << 18) != 0,
                "mask=0x" + Integer.toHexString(full.hasLowerBlocks));
        failures += check("full-chunk mask covers section 19 (Y 304-319)",
                (full.hasLowerBlocks & 1 << 19) != 0,
                "mask=0x" + Integer.toHexString(full.hasLowerBlocks));
        failures += check("full-chunk upper mask covers section 19",
                (full.hasUpperBlocks & 1 << 19) != 0,
                "upper=0x" + Integer.toHexString(full.hasUpperBlocks));
        failures += check("full-chunk upper mask excludes section 18 (no id>255 there)",
                (full.hasUpperBlocks & 1 << 18) == 0,
                "upper=0x" + Integer.toHexString(full.hasUpperBlocks));

        // The client allocates exactly this many bytes for the decompressed payload
        // (12288 per section + 2048 per upper array + 256 biomes) and rejects the
        // packet on mismatch, so the wire payload must match it.
        int expectedSize = 12288 * Integer.bitCount(full.hasLowerBlocks)
                + 2048 * Integer.bitCount(full.hasUpperBlocks) + 256;
        failures += check("decompressed chunk data matches the layout the client expects",
                full.chunkData.length == expectedSize,
                "expected=" + expectedSize + " actual=" + full.chunkData.length);

        // Apply it the way the client does and check every height survived.
        Chunk dst = new Chunk(world, 3, 7);
        dst.setChunkData(full.chunkData, full.hasLowerBlocks, full.hasUpperBlocks, true);
        failures += check("block round-trips at Y=5",
                dst.getBlockID(5, 5, 9) == 1 && dst.getBlockMetadata(5, 5, 9) == 3,
                "id=" + dst.getBlockID(5, 5, 9) + " meta=" + dst.getBlockMetadata(5, 5, 9));
        failures += check("block round-trips at Y=255 (old boundary)",
                dst.getBlockID(1, 255, 1) == 1,
                "id=" + dst.getBlockID(1, 255, 1));
        failures += check("block round-trips at Y=300 (section 18)",
                dst.getBlockID(6, 300, 7) == 1 && dst.getBlockMetadata(6, 300, 7) == 5,
                "id=" + dst.getBlockID(6, 300, 7) + " meta=" + dst.getBlockMetadata(6, 300, 7));
        failures += check("id>255 block round-trips at Y=315 (section 19)",
                dst.getBlockID(2, 315, 3) == 258,
                "id=" + dst.getBlockID(2, 315, 3));

        // Partial mask packet, as sent for multi-block resyncs. The mask bit for
        // section 18 must survive the wire even when no other sections are included.
        UpdateChunkPacket partial = roundTrip(new UpdateChunkPacket(src, false, 1 << 18), false);
        failures += check("partial-chunk mask keeps bit 18",
                (partial.hasLowerBlocks & 1 << 18) != 0,
                "mask=0x" + Integer.toHexString(partial.hasLowerBlocks));
        Chunk dst2 = new Chunk(world, 3, 7);
        dst2.setChunkData(partial.chunkData, partial.hasLowerBlocks, partial.hasUpperBlocks, false);
        failures += check("partial-chunk block round-trips at Y=300",
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
        failures += check("multi-block update coordinate keeps Y=300",
                cx == 5 && cz == 6 && cy == 300,
                "decoded=(" + cx + "," + cy + "," + cz + ")");
        failures += check("multi-block update carries the block id/meta",
                section.typeArray[0] == 1 && section.metadataArray[0] == 5,
                "id=" + section.typeArray[0] + " meta=" + section.metadataArray[0]);

        // Single-block update: Y is a short on the wire, must hold 300.
        UpdateBlockPacket block = roundTrip(new UpdateBlockPacket(53, 300, 118, world), false);
        failures += check("single-block update keeps Y=300",
                block.yPosition == 300 && block.type == 1 && block.metadata == 5,
                "y=" + block.yPosition + " id=" + block.type + " meta=" + block.metadata);

        // Client->server dig and place: Y was one byte on the wire (0-255).
        DigPacket dig = roundTrip(new DigPacket(0, 53, 300, 118, 2), true);
        failures += check("dig packet keeps Y=300", dig.yPosition == 300, "y=" + dig.yPosition);
        Packet15Place place = roundTrip(new Packet15Place(53, 319, 118, 1, null), true);
        failures += check("place packet keeps Y=319", place.yPosition == 319, "y=" + place.yPosition);

        // Block events (chests, note blocks...) carry a Y that was one byte.
        BlockEventPacket event = roundTrip(new BlockEventPacket(1, 53, 300, 118, 7), false);
        failures += check("block event keeps Y=300",
                event.x == 53 && event.y == 300 && event.z == 118,
                "y=" + event.y);

        deleteRecursive(dir);
        return failures;
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

    private static int liveCheck(String host, int port) {
        try {
            return runLiveBot(host, port);
        } catch (Exception e) {
            System.out.println("[test] FAIL  live check threw " + e);
            return 1;
        }
    }

    private static int runLiveBot(String host, int port) throws Exception {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(host, port), 10000);
        socket.setSoTimeout(15000);
        DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

        send(out, new HandshakePacket("HeightBot"));
        Packet p = Packet.readPacket(in, false);
        if (!(p instanceof HandshakePacket)) {
            System.out.println("[test] FAIL  live: expected HandshakePacket, got " + p);
            return 1;
        }
        if (!"-".equals(((HandshakePacket) p).username)) {
            System.out.println("[test] FAIL  live: server is in online mode");
            return 1;
        }
        send(out, new LoginPacket("HeightBot", SharedConstants.PROTOCOL_VERSION));

        double posX = Double.NaN, posY = Double.NaN, posZ = Double.NaN;
        boolean placed = false;
        boolean sawUpdate = false;
        String sawUpdateKind = "";
        StringBuilder chat = new StringBuilder();
        long start = System.currentTimeMillis();
        long deadline = start + 30000;
        long lastMoveSent = 0;

        while (System.currentTimeMillis() < deadline) {
            // Chunk streaming runs from the server's movement handlers, so a bot
            // that never moves never receives chunks. Pump position packets to
            // pull the spawn area in (one chunk per packet, see EntityPlayerMP).
            if (!Double.isNaN(posX) && !placed
                    && System.currentTimeMillis() - lastMoveSent >= 100) {
                send(out, new PlayerPositionPacket(posX, posY, posY + 1.62, posZ, true));
                lastMoveSent = System.currentTimeMillis();
            }

            Packet next;
            try {
                next = Packet.readPacket(in, false);
            } catch (java.net.SocketTimeoutException e) {
                continue;
            }
            if (next == null) {
                break;
            }
            if (next instanceof LoginPacket) {
                LoginPacket lp = (LoginPacket) next;
                if (lp.maxSections < 20) {
                    System.out.println("[test] FAIL  live: server world has only " + lp.maxSections
                            + " sections; this test needs a 20-section (320-block) world");
                    socket.close();
                    return 1;
                }
            } else if (next instanceof PlayerTeleportPacket && Double.isNaN(posX)) {
                posX = ((PlayerTeleportPacket) next).xPosition;
                posY = ((PlayerTeleportPacket) next).yPosition;
                posZ = ((PlayerTeleportPacket) next).zPosition;
            } else if (next instanceof UpdateChunkPacket && !placed && !Double.isNaN(posX)) {
                // Chunks streaming in; once our own chunk has been sent the server
                // considers it loaded and will deliver block updates for it.
                UpdateChunkPacket chunk = (UpdateChunkPacket) next;
                int cx = (int) Math.floor(posX / 16.0);
                int cz = (int) Math.floor(posZ / 16.0);
                if (chunk.xCh == cx && chunk.zCh == cz) {
                    int x = (int) Math.floor(posX);
                    int z = (int) Math.floor(posZ);
                    // Glass first, then stone: re-running this test against a world where
                    // the block is already stone would make stone->stone a no-op in
                    // World.setBlockAndMetadata (no change, no update packet). Starting
                    // from a known state keeps the test deterministic; the final cleanup
                    // below leaves glass behind. (Air is not usable for this: id 0 has no
                    // BlockList entry, so /setblock rejects it as an invalid block.)
                    send(out, new ChatPacket("/setblock " + x + " 300 " + z + " 20"));
                    send(out, new ChatPacket("/setblock " + x + " 300 " + z + " 1"));
                    placed = true;
                }
            } else if (placed && next instanceof UpdateBlockPacket) {
                UpdateBlockPacket ub = (UpdateBlockPacket) next;
                if (ub.yPosition == 300 && ub.type == 1) {
                    sawUpdate = true;
                    sawUpdateKind = "UpdateBlockPacket y=" + ub.yPosition + " id=" + ub.type;
                    break;
                }
            } else if (placed && next instanceof UpdateSectionPacket) {
                UpdateSectionPacket us = (UpdateSectionPacket) next;
                for (int i = 0; i < us.sizeOfAllArrays; i++) {
                    if ((us.coordinateArray[i] & 0xFFFF) == 300 && us.typeArray[i] == 1) {
                        sawUpdate = true;
                        sawUpdateKind = "UpdateSectionPacket y=300 id=" + us.typeArray[i];
                        break;
                    }
                }
                if (sawUpdate) {
                    break;
                }
            } else if (placed && next instanceof UpdateChunkPacket) {
                UpdateChunkPacket uc = (UpdateChunkPacket) next;
                if ((uc.hasLowerBlocks & 1 << 18) != 0) {
                    sawUpdate = true;
                    sawUpdateKind = "UpdateChunkPacket mask=0x" + Integer.toHexString(uc.hasLowerBlocks);
                    break;
                }
            } else if (next instanceof ChatPacket) {
                chat.append(((ChatPacket) next).message).append(" | ");
            } else if (next instanceof KeepAlivePacket) {
                send(out, new KeepAlivePacket());
            } else if (next instanceof DisconnectPacket) {
                System.out.println("[test] FAIL  live: kicked: " + ((DisconnectPacket) next).reason);
                return 1;
            }
        }

        if (placed) {
            // Leave the world in a known state: reset the test block to glass.
            int x = (int) Math.floor(posX);
            int z = (int) Math.floor(posZ);
            send(out, new ChatPacket("/setblock " + x + " 300 " + z + " 20"));
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }
        }

        socket.close();

        if (!placed) {
            System.out.println("[test] FAIL  live: never got spawn position/chunks "
                    + "(posX=" + posX + ") chat: " + chat);
            return 1;
        }
        if (!sawUpdate) {
            System.out.println("[test] FAIL  live: no block update for Y=300 arrived after /setblock");
            System.out.println("[test]       chat heard: " + chat);
            System.out.println("[test]       hint: the bot must be op - add HeightBot to run/server/ops.txt and restart the server");
            return 1;
        }
        return check("live: block placed at Y=300 reached the placing client (" + sawUpdateKind + ")",
                true, "");
    }

    private static void send(DataOutputStream out, Packet packet) throws Exception {
        Packet.writePacket(packet, out);
        out.flush();
    }
}
