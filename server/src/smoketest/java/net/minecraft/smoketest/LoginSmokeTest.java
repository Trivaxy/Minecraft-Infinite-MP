package net.minecraft.smoketest;

import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.world.SpawnPacket;
import net.minecraft.network.packet.world.UpdateTimePacket;

/**
 * Headless multiplayer login smoke test. Speaks the real protocol using the game's own
 * packet classes, so the wire format cannot drift from the game's.
 *
 * Verifies the offline-mode handshake end to end:
 *   C->S HandshakePacket(username)
 *   S->C HandshakePacket("-")          <- proves online-mode=false took effect
 *   C->S LoginPacket(username, PROTOCOL_VERSION)
 *   S->C LoginPacket(entityId, seed, dimension, maxPlayers, maxSections)
 *   S->C SpawnPacket + whatever else the server pushes
 *
 * Usage: LoginSmokeTest <host> <port> <username> [secondsToListen]
 */
public class LoginSmokeTest {

    public static void main(String[] args) throws Exception {
        Checks checks = new Checks();
        String username = args.length > 2 ? args[2] : "SmokeBot";
        int listenSeconds = args.length > 3 ? Integer.parseInt(args[3]) : 5;

        checks.note("connecting to " + Bot.host(args) + ":" + Bot.port(args) + " as " + username);
        Bot bot = Bot.join(args, username);
        checks.check("offline mode confirmed, logged in", true);

        SpawnPacket spawn = null;
        long lastWorldTime = -1L;
        Map<String, Integer> seen = new LinkedHashMap<String, Integer>();
        seen.put("LoginPacket", 1);

        long deadline = System.currentTimeMillis() + listenSeconds * 1000L;
        while (System.currentTimeMillis() < deadline && bot.kickReason == null) {
            Packet next = bot.read();
            if (next == null) {
                continue;
            }
            String name = next.getClass().getSimpleName();
            Integer count = seen.get(name);
            seen.put(name, count == null ? 1 : count + 1);

            if (next instanceof SpawnPacket && spawn == null) {
                spawn = (SpawnPacket) next;
            }
            // The server sends one of these per dimension per second, carrying that world's own
            // clock. Two bots in different dimensions should report the same value: each world
            // advances its own time, and the server re-syncs them to the Overworld every tick.
            if (next instanceof UpdateTimePacket) {
                lastWorldTime = ((UpdateTimePacket) next).time;
            }
        }

        checks.note("packets received: " + seen);
        if (bot.kickReason != null) {
            checks.abort("server kicked us: \"" + bot.kickReason + "\"");
        }

        checks.note("entityId=" + bot.entityId
                + "  seed=" + bot.login.mapSeed
                + "  dimension=" + bot.login.dimension
                + "  maxPlayers=" + bot.login.maxPlayers
                + "  maxSections=" + bot.login.maxSections
                + "  worldTime=" + lastWorldTime);

        checks.check("server sent a spawn point", spawn != null,
                "no SpawnPacket seen");
        if (spawn != null) {
            checks.note("spawn point = (" + spawn.xPosition + ", " + spawn.yPosition + ", " + spawn.zPosition + ")");
        }

        checks.check("chunk data streaming",
                seen.containsKey("PrepChunkPacket") || seen.containsKey("UpdateChunkPacket"),
                "NOT received - client would see a void world");

        checks.done();
    }
}
