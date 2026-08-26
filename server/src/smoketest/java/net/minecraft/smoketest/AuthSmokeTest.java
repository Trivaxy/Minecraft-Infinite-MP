package net.minecraft.smoketest;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import net.minecraft.SharedConstants;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.misc.DisconnectPacket;
import net.minecraft.network.packet.misc.HandshakePacket;
import net.minecraft.network.packet.misc.LoginPacket;

/**
 * Logs in without calling Mojang's /join, the way an impostor would, and expects a kick.
 * Speaks the wire directly because Bot refuses a real serverId.
 *
 * Needs online-mode=true and network access, so it is not part of a default run.
 */
public final class AuthSmokeTest {

    public static void main(String[] args) throws Exception {
        Checks checks = new Checks();
        String host = Bot.host(args);
        int port = Bot.port(args);
        String name = "authbot";

        checks.note("connecting to " + host + ":" + port + " as " + name);

        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(host, port), 10000);
        socket.setSoTimeout(20000);
        DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

        Packet.writePacket(new HandshakePacket(name), out);
        out.flush();

        Packet next = Packet.readPacket(in, false);
        boolean gotHandshake = next instanceof HandshakePacket;
        checks.check("server answered the handshake", gotHandshake,
                gotHandshake ? "" : "got " + (next == null ? "nothing" : next.getClass().getSimpleName()));
        if (!gotHandshake) {
            checks.done();
        }

        String serverId = ((HandshakePacket) next).username;
        checks.note("serverId = \"" + serverId + "\"");
        if (!checks.check("server is in online mode", !"-".equals(serverId),
                "serverId is \"-\", so online-mode=false - this test proves nothing here")) {
            checks.done();
        }

        Packet.writePacket(new LoginPacket(name, SharedConstants.PROTOCOL_VERSION), out);
        out.flush();

        String kick = null;
        LoginPacket accepted = null;
        long deadline = System.currentTimeMillis() + 20000;
        while (kick == null && accepted == null && System.currentTimeMillis() < deadline) {
            Packet reply = Packet.readPacket(in, false);
            if (reply instanceof DisconnectPacket) {
                kick = ((DisconnectPacket) reply).reason;
            } else if (reply instanceof LoginPacket) {
                accepted = (LoginPacket) reply;
            } else if (reply == null) {
                break;
            }
        }

        socket.close();

        checks.check("unauthenticated login was rejected", accepted == null,
                accepted == null ? "" : "server let an unauthenticated client in");
        checks.check("kicked with the verify-username reason", kick != null && kick.contains("Failed to verify username"),
                kick == null ? "no DisconnectPacket arrived" : "reason was: " + kick);
        if (kick != null) {
            checks.note("kick reason: " + kick);
        }

        checks.done();
    }
}
