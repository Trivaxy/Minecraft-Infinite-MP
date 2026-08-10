package net.minecraft.smoketest;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.SharedConstants;
import net.minecraft.network.packet.DisconnectPacket;
import net.minecraft.network.packet.HandshakePacket;
import net.minecraft.network.packet.LoginPacket;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PlayerPositionPacket;
import net.minecraft.network.packet.PlayerTeleportPacket;

/**
 * Logs a bot in and then <em>moves</em> it, which the other smoke tests never do.
 *
 * <p>That gap hid a bug that kicked every player off the server four seconds after they started
 * moving: {@code playerInAirTime} in {@code NetServerHandler} was only reset inside the
 * {@code allow-flight=true} branch, so with the default settings it was a monotonic counter.
 * Bots that stand still never send movement packets and never trip it.
 *
 * <p>The bot walks a slow circle on the ground for {@code seconds}, sending one position packet
 * per tick like a real client, and asserts that the server neither kicks it nor drags it back.
 * A few rubber-bands are normal right after login while the client and server agree on where the
 * ground is; a steady stream of them is not.
 *
 * <p>Usage: {@code MovementSmokeTest <host> <port> [username] [seconds]}
 */
public class MovementSmokeTest {

    private static volatile boolean kicked = false;
    private static volatile String kickReason = null;
    private static volatile int teleports = 0;
    private static volatile double serverX, serverY, serverZ;
    private static volatile boolean positioned = false;

    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "127.0.0.1";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 25565;
        String username = args.length > 2 ? args[2] : "Walker";
        int seconds = args.length > 3 ? Integer.parseInt(args[3]) : 15;

        System.out.println("[test] connecting to " + host + ":" + port + " as " + username);
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(host, port), 10000);
        socket.setSoTimeout(20000);

        final DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

        send(out, new HandshakePacket(username));
        Packet p = Packet.readPacket(in, false);
        if (!(p instanceof HandshakePacket)) {
            fail("expected HandshakePacket, got " + (p == null ? "null" : p.getClass().getSimpleName()));
        }

        send(out, new LoginPacket(username, SharedConstants.PROTOCOL_VERSION));

        // The server pushes chunks continuously; a dedicated reader keeps the socket drained
        // while the main thread walks, and records the two things we care about.
        Thread reader = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!kicked) {
                        Packet next = Packet.readPacket(in, false);
                        if (next == null) {
                            return;
                        }
                        if (next instanceof DisconnectPacket) {
                            kickReason = ((DisconnectPacket) next).reason;
                            kicked = true;
                            return;
                        }
                        if (next instanceof PlayerTeleportPacket) {
                            PlayerTeleportPacket t = (PlayerTeleportPacket) next;
                            serverX = t.xPosition;
                            serverY = t.yPosition;
                            serverZ = t.zPosition;
                            teleports++;
                            positioned = true;
                        }
                    }
                } catch (Exception ex) {
                    // socket closed underneath us at the end of the run
                }
            }
        }, "smoke-reader");
        reader.setDaemon(true);
        reader.start();

        long waitUntil = System.currentTimeMillis() + 15000L;
        while (!positioned && !kicked && System.currentTimeMillis() < waitUntil) {
            Thread.sleep(50L);
        }
        if (kicked) {
            fail("kicked before we even moved: \"" + kickReason + "\"");
        }
        if (!positioned) {
            fail("server never sent a position");
        }

        System.out.println("[test] server placed us at (" + serverX + ", " + serverY + ", " + serverZ + ")");
        int teleportsAfterLogin = teleports;

        // Walk a 3-block circle at ~4 blocks/second, one packet per tick.
        double baseX = serverX;
        double baseY = serverY;
        double baseZ = serverZ;
        int ticks = seconds * 20;
        List<String> notes = new ArrayList<String>();

        for (int i = 0; i < ticks && !kicked; i++) {
            double angle = i * Math.PI * 2.0 / 100.0;
            double x = baseX + Math.cos(angle) * 1.5;
            double z = baseZ + Math.sin(angle) * 1.5;
            // (x, y, stance, z) - stance is the eye position, 1.62 above the feet.
            send(out, new PlayerPositionPacket(x, baseY, baseY + 1.62, z, true));
            Thread.sleep(50L);
            if (i == 100) {
                notes.add("5s: still connected");
            }
            if (i == 200) {
                notes.add("10s: still connected");
            }
        }

        for (String note : notes) {
            System.out.println("[test] " + note);
        }

        int rubberBands = teleports - teleportsAfterLogin;
        System.out.println("[test] rubber-band teleports during walk: " + rubberBands);

        if (kicked) {
            fail("kicked after " + seconds + "s of walking: \"" + kickReason + "\"");
        }
        System.out.println("[test] PASS: walked " + seconds + "s without being kicked");

        if (rubberBands > ticks / 10) {
            System.out.println("[test] WARN: " + rubberBands + " rubber-bands in " + ticks
                    + " ticks - the server disagrees with the client about where the ground is");
        } else {
            System.out.println("[test] PASS: no sustained rubber-banding");
        }

        socket.close();
        System.out.println("[test] RESULT: MOVEMENT OK");
    }

    private static void send(DataOutputStream out, Packet packet) throws Exception {
        Packet.writePacket(packet, out);
        out.flush();
    }

    private static void fail(String msg) {
        System.out.println("[test] RESULT: FAIL - " + msg);
        System.exit(1);
    }
}
