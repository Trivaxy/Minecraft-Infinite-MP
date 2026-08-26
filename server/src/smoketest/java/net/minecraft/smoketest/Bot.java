package net.minecraft.smoketest;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import net.minecraft.SharedConstants;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.entity.EntityActionPacket;
import net.minecraft.network.packet.entity.EntityMovementPacket;
import net.minecraft.network.packet.misc.ChatPacket;
import net.minecraft.network.packet.misc.DisconnectPacket;
import net.minecraft.network.packet.misc.HandshakePacket;
import net.minecraft.network.packet.misc.KeepAlivePacket;
import net.minecraft.network.packet.misc.LoginPacket;
import net.minecraft.network.packet.player.PlayerPositionPacket;
import net.minecraft.network.packet.player.PlayerSpawnPacket;
import net.minecraft.network.packet.player.PlayerStatePacket;
import net.minecraft.network.packet.player.PlayerTeleportPacket;
import net.minecraft.network.packet.world.TeleportPacket;

/**
 * A headless client on the real wire, for smoke tests and one-off probes.
 *
 * Two things here are not optional and are why this class exists. First, the
 * server ignores a player until its login teleport is echoed back exactly
 * (NetServerHandler.hasMoved), so a bot that stops sending position packets is
 * silently dropped and every assertion about it becomes meaningless - the
 * MovementSmokeTest failure that B-05 records. Every wait in this class pumps
 * *all* live bots for that reason, so a watcher waiting on a mover keeps the
 * mover alive without the test having to interleave them by hand, and read()
 * adopts every teleport the server sends the way a real client echoes one
 * straight back - the desync rubber-band at NetServerHandler:257 logs nothing,
 * so an unadopted correction leaves a bot whose digs and places are dropped in
 * silence. Second,
 * UpdateChunkPacket and UpdateSectionPacket decode through a static scratch
 * buffer: two bots decoding chunks in one JVM corrupt each other. Bots that
 * only exchange small packets share a JVM safely; anything reading chunk data
 * needs its own process (see TwoClientTest).
 */
public final class Bot {

    private static final List<Bot> LIVE = new ArrayList<Bot>();

    public final String name;
    public int entityId = -1;
    public LoginPacket login;
    public String serverId;
    public String kickReason;

    public double posX = Double.NaN, posY = Double.NaN, posZ = Double.NaN;

    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;
    private final Map<Integer, int[]> tracked = new HashMap<Integer, int[]>();
    private boolean sinking;
    private boolean echoedTeleport;
    private long lastMoveSent;

    public static Bot join(String host, int port, String name) throws Exception {
        return new Bot(host, port, name);
    }

    /** smokeTest always passes host and port as args[0] and args[1]. */
    public static Bot join(String[] args, String name) throws Exception {
        return new Bot(host(args), port(args), name);
    }

    public static String host(String[] args) {
        return args.length > 0 ? args[0] : "127.0.0.1";
    }

    public static int port(String[] args) {
        return args.length > 1 ? Integer.parseInt(args[1]) : 25565;
    }

    private Bot(String host, int port, String name) throws Exception {
        this.name = name;
        this.socket = new Socket();
        this.socket.connect(new InetSocketAddress(host, port), 10000);
        this.socket.setSoTimeout(500);
        this.in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        this.out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        LIVE.add(this);

        send(new HandshakePacket(name));
        boolean loginSent = false;
        long deadline = System.currentTimeMillis() + 15000;
        while (entityId == -1 && System.currentTimeMillis() < deadline) {
            Packet next = read();
            if (next instanceof HandshakePacket && !loginSent) {
                serverId = ((HandshakePacket) next).username;
                if (!"-".equals(serverId)) {
                    throw new IllegalStateException(name + ": server is in ONLINE mode (serverId=\""
                            + serverId + "\"); set online-mode=false in run/server/server.properties");
                }
                send(new LoginPacket(name, SharedConstants.PROTOCOL_VERSION));
                loginSent = true;
            } else if (next instanceof LoginPacket && loginSent) {
                this.login = (LoginPacket) next;
                this.entityId = this.login.protocolVersion;
            } else if (next instanceof DisconnectPacket) {
                throw new IllegalStateException(name + ": kicked during login: " + kickReason);
            }
        }
        if (entityId == -1) {
            throw new IllegalStateException(name + ": never received the server's LoginPacket");
        }
    }

    // ---------------------------------------------------------------- reading

    /** One packet, or null on a read timeout. Keep-alives are answered here and reported as null. */
    public Packet read() throws Exception {
        pumpAll();
        Packet next;
        try {
            next = Packet.readPacket(in, false);
        } catch (java.net.SocketTimeoutException e) {
            return null;
        }
        if (next == null) {
            kickReason = "stream closed by server";
            return null;
        }
        if (next instanceof KeepAlivePacket) {
            send(new KeepAlivePacket());
            return null;
        }
        if (next instanceof PlayerTeleportPacket) {
            PlayerTeleportPacket teleport = (PlayerTeleportPacket) next;
            posX = teleport.xPosition;
            posY = teleport.yPosition;
            posZ = teleport.zPosition;
        }
        trackOther(next);
        if (next instanceof DisconnectPacket) {
            kickReason = ((DisconnectPacket) next).reason;
        }
        return next;
    }

    /**
     * The same three packets ClientHandler drives Entity.serverX/Y/Z from, so
     * positionOf answers where this bot's client would draw another entity.
     */
    private void trackOther(Packet packet) {
        if (packet instanceof PlayerSpawnPacket) {
            PlayerSpawnPacket spawn = (PlayerSpawnPacket) packet;
            tracked.put(spawn.entityId, new int[]{spawn.xPosition, spawn.yPosition, spawn.zPosition});
        } else if (packet instanceof TeleportPacket) {
            TeleportPacket teleport = (TeleportPacket) packet;
            tracked.put(teleport.entityId, new int[]{teleport.xPosition, teleport.yPosition, teleport.zPosition});
        } else if (packet instanceof EntityMovementPacket) {
            EntityMovementPacket move = (EntityMovementPacket) packet;
            int[] at = tracked.get(move.entityId);
            if (at != null) {
                at[0] += move.xPosition;
                at[1] += move.yPosition;
                at[2] += move.zPosition;
            }
        }
    }

    /** Where this bot has been told another entity is, in blocks, or null if it is not tracking it. */
    public double[] positionOf(int otherEntityId) {
        int[] at = tracked.get(otherEntityId);
        return at == null ? null : new double[]{at[0] / 32.0, at[1] / 32.0, at[2] / 32.0};
    }

    public <T extends Packet> T await(Class<T> type, long timeoutMs) throws Exception {
        return await(type, null, timeoutMs);
    }

    /** The first packet of this type the filter accepts, or null on timeout or kick. */
    public <T extends Packet> T await(Class<T> type, Predicate<T> filter, long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline && kickReason == null) {
            Packet next = read();
            if (type.isInstance(next)) {
                T typed = type.cast(next);
                if (filter == null || filter.test(typed)) {
                    return typed;
                }
            }
        }
        return null;
    }

    public <T extends Packet> T awaitLast(Class<T> type, Predicate<T> filter, long windowMs) throws Exception {
        return awaitLast(type, filter, windowMs, windowMs);
    }

    /**
     * The LAST matching packet, for answers that arrive as a burst - a container
     * refresh is preceded by single-slot updates, and a /give can land in more
     * than one slot. Listens for windowMs, cutting the wait short once a match
     * has gone settleMs without being followed by another.
     */
    public <T extends Packet> T awaitLast(Class<T> type, Predicate<T> filter, long windowMs, long settleMs)
            throws Exception {
        T latest = null;
        long deadline = System.currentTimeMillis() + windowMs;
        while (System.currentTimeMillis() < deadline && kickReason == null) {
            Packet next = read();
            if (type.isInstance(next)) {
                T typed = type.cast(next);
                if (filter == null || filter.test(typed)) {
                    latest = typed;
                    deadline = Math.min(deadline, System.currentTimeMillis() + settleMs);
                }
            }
        }
        return latest;
    }

    /** Every matching packet in a fixed window - for asserting on how MANY arrived. */
    public <T extends Packet> List<T> awaitAll(Class<T> type, Predicate<T> filter, long windowMs) throws Exception {
        List<T> found = new ArrayList<T>();
        long deadline = System.currentTimeMillis() + windowMs;
        while (System.currentTimeMillis() < deadline && kickReason == null) {
            Packet next = read();
            if (type.isInstance(next)) {
                T typed = type.cast(next);
                if (filter == null || filter.test(typed)) {
                    found.add(typed);
                }
            }
        }
        return found;
    }

    /** Reads and discards for a fixed window, keeping the connection pumped. */
    public void drain(long millis) throws Exception {
        long deadline = System.currentTimeMillis() + millis;
        while (System.currentTimeMillis() < deadline && kickReason == null) {
            read();
        }
    }

    public boolean awaitSpawnOf(Bot other, long timeoutMs) throws Exception {
        return await(PlayerSpawnPacket.class, p -> p.entityId == other.entityId, timeoutMs) != null;
    }

    // ---------------------------------------------------------------- position

    /**
     * The spawn point can be open ocean (B-01), where a bot holding still is
     * kicked as flying after four seconds. Sinking bots fall the way a real
     * client would; the rest hold the position the server last gave them.
     */
    public Bot sink() {
        this.sinking = true;
        return this;
    }

    /**
     * Falls to wherever the spawn point actually drops you, then holds there:
     * the server has accepted that position, which is what re-arms hasMoved.
     */
    public void settle(long millis) throws Exception {
        boolean wasSinking = sinking;
        sinking = true;
        long deadline = System.currentTimeMillis() + 15000;
        while (Double.isNaN(posX) && System.currentTimeMillis() < deadline && kickReason == null) {
            read();
        }
        drain(millis);
        sinking = wasSinking;
    }

    /**
     * Adopts the position of a server-initiated teleport and echoes it back,
     * which is what re-arms hasMoved. Until that echo the server ignores the
     * player completely: NetServerHandler.handlePlayerMovement does movement,
     * chunk streaming and the health sync inside one `if (hasMoved)`, so a bot
     * that respawns or /teleports and keeps sending its old position is talking
     * to a server that has stopped listening.
     */
    public boolean followTeleport(long timeoutMs) throws Exception {
        PlayerTeleportPacket teleport = await(PlayerTeleportPacket.class, timeoutMs);
        if (teleport == null) {
            return false;
        }
        posX = teleport.xPosition;
        posY = teleport.yPosition;
        posZ = teleport.zPosition;
        drain(1000);
        return true;
    }

    /**
     * Falls until the server refuses the next step down, then holds the position it
     * accepted. settle() stops on a timer, wherever the bot happens to be by then -
     * over water or a cave that is still mid-air. The push-back that refuses the next
     * step down is both the signal and the hazard: it clears hasMoved, so the sinking
     * has to stop on that packet or the server spends the rest of the run ignoring this
     * bot (see followTeleport). A bot that logs back in already standing on something
     * gets the same push-back on its first step, so this works either way.
     */
    public boolean land(long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;

        while (Double.isNaN(posY) && System.currentTimeMillis() < deadline && kickReason == null) {
            read();
        }

        drain(500);
        boolean wasSinking = sinking;
        sinking = true;
        PlayerTeleportPacket ground = await(PlayerTeleportPacket.class, deadline - System.currentTimeMillis());
        sinking = wasSinking;
        if (ground == null) {
            return false;
        }

        posX = ground.xPosition;
        posY = ground.yPosition;
        posZ = ground.zPosition;
        drain(1000);
        return true;
    }

    private void pump() throws Exception {
        if (Double.isNaN(posX) || System.currentTimeMillis() - lastMoveSent < 100) {
            return;
        }
        if (echoedTeleport && sinking) {
            posY -= 0.2;
        }
        echoedTeleport = true;
        send(new PlayerPositionPacket(posX, posY, posY + 1.62, posZ, true));
        lastMoveSent = System.currentTimeMillis();
    }

    private static void pumpAll() throws Exception {
        for (Bot bot : LIVE.toArray(new Bot[0])) {
            bot.pump();
        }
    }

    // ---------------------------------------------------------------- sending

    public void send(Packet packet) throws Exception {
        Packet.writePacket(packet, out);
        out.flush();
    }

    public void chat(String message) throws Exception {
        send(new ChatPacket(message));
    }

    /** EntityActionPacket state: 3 wake, 4 sprint start, 5 sprint stop. */
    public void sendAction(int state) throws Exception {
        EntityActionPacket action = new EntityActionPacket();
        action.entityId = entityId;
        action.state = state;
        send(action);
    }

    public void sendState(int type, boolean state) throws Exception {
        send(new PlayerStatePacket(entityId, (byte) type, state));
    }

    // ---------------------------------------------------------------- lifecycle

    public void close() {
        LIVE.remove(this);
        try {
            socket.close();
        } catch (Exception ignored) {
        }
    }

    public static void closeAll() {
        for (Bot bot : LIVE.toArray(new Bot[0])) {
            bot.close();
        }
    }
}
