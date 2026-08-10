package net.minecraft.smoketest;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.SharedConstants;
import net.minecraft.network.packet.DisconnectPacket;
import net.minecraft.network.packet.HandshakePacket;
import net.minecraft.network.packet.LoginPacket;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.SpawnPacket;
import net.minecraft.network.packet.UpdateTimePacket;

/**
 * Headless multiplayer login smoke test. Speaks the real protocol using the server
 * module's own packet classes, so the wire format cannot drift from the game's.
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
        String host = args.length > 0 ? args[0] : "127.0.0.1";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 25565;
        String username = args.length > 2 ? args[2] : "SmokeBot";
        int listenSeconds = args.length > 3 ? Integer.parseInt(args[3]) : 5;

        System.out.println("[test] connecting to " + host + ":" + port + " as " + username);
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(host, port), 10000);
        socket.setSoTimeout(15000);

        DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

        // --- handshake ---
        send(out, new HandshakePacket(username));
        Packet p = Packet.readPacket(in, false);
        if (!(p instanceof HandshakePacket)) {
            fail("expected HandshakePacket, got " + describe(p));
            return;
        }
        String serverId = ((HandshakePacket) p).username;
        System.out.println("[test] server handshake serverId=\"" + serverId + "\"");
        if (!"-".equals(serverId)) {
            fail("server is in ONLINE mode (serverId=\"" + serverId + "\"). "
                    + "session.minecraft.net is dead, so login cannot succeed. "
                    + "Set online-mode=false in run/server/server.properties.");
            return;
        }
        System.out.println("[test] PASS: offline mode confirmed");

        // --- login ---
        send(out, new LoginPacket(username, SharedConstants.PROTOCOL_VERSION));

        LoginPacket login = null;
        SpawnPacket spawn = null;
        long lastWorldTime = -1L;
        Map<String, Integer> seen = new LinkedHashMap<String, Integer>();
        long deadline = System.currentTimeMillis() + listenSeconds * 1000L;

        while (System.currentTimeMillis() < deadline) {
            Packet next;
            try {
                next = Packet.readPacket(in, false);
            } catch (java.net.SocketTimeoutException e) {
                break;
            }
            if (next == null) {
                System.out.println("[test] stream closed by server");
                break;
            }
            String name = next.getClass().getSimpleName();
            Integer c = seen.get(name);
            seen.put(name, c == null ? 1 : c + 1);

            if (next instanceof DisconnectPacket) {
                fail("server kicked us: \"" + ((DisconnectPacket) next).reason + "\"");
            return;
            }
            if (next instanceof LoginPacket && login == null) {
                login = (LoginPacket) next;
            }
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

        System.out.println("[test] packets received: " + seen);

        if (login == null) {
            fail("never received the server's LoginPacket");
            return;
        }
        System.out.println("[test] PASS: logged in"
                + "  entityId=" + login.protocolVersion
                + "  seed=" + login.mapSeed
                + "  dimension=" + login.dimension
                + "  maxPlayers=" + login.maxPlayers
                + "  maxSections=" + login.maxSections
                + "  worldTime=" + lastWorldTime);

        if (spawn != null) {
            System.out.println("[test] spawn point = (" + spawn.xPosition + ", " + spawn.yPosition + ", " + spawn.zPosition + ")");
        } else {
            System.out.println("[test] WARN: no SpawnPacket seen");
        }

        boolean gotChunks = seen.containsKey("PrepChunkPacket") || seen.containsKey("UpdateChunkPacket");
        System.out.println("[test] " + (gotChunks ? "PASS" : "WARN") + ": chunk data "
                + (gotChunks ? "streaming" : "NOT received - client would see a void world"));

        socket.close();
        System.out.println("[test] RESULT: LOGIN OK");
    }

    private static void send(DataOutputStream out, Packet packet) throws Exception {
        Packet.writePacket(packet, out);
        out.flush();
    }

    private static String describe(Packet p) {
        return p == null ? "null (stream closed)" : p.getClass().getSimpleName();
    }

    private static void fail(String msg) {
        System.out.println("[test] RESULT: FAIL - " + msg);
        System.exit(1);
    }
}
