package net.minecraft.server.network;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Random;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import net.minecraft.SharedConstants;
import net.minecraft.game.item.potion.EffectData;
import net.minecraft.game.world.util.Position;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.packet.misc.ChatPacket;
import net.minecraft.network.packet.misc.DisconnectPacket;
import net.minecraft.network.packet.entity.EntityEffectPacket;
import net.minecraft.network.packet.misc.HandshakePacket;
import net.minecraft.network.packet.misc.LoginPacket;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.misc.PingPacket;
import net.minecraft.network.packet.misc.ServerIconPacket;
import net.minecraft.network.packet.world.SpawnPacket;
import net.minecraft.network.packet.world.UpdateTimePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.player.EntityPlayerMP;
import net.minecraft.server.world.WorldServer;

public class NetLoginHandler extends NetworkHandler {
    public static Logger logger = Logger.getLogger("Minecraft");
    private static Random rand = new Random();
    public NetworkManager netManager;
    public boolean finishedProcessing = false;
    private MinecraftServer mcServer;
    private int loginTimer = 0;
    private String username = null;
    private LoginPacket packet1login = null;
    private String serverId = "";

    public NetLoginHandler(MinecraftServer minecraftServer1, Socket socket2, String string3) throws IOException {
        this.mcServer = minecraftServer1;
        this.netManager = new NetworkManager(socket2, string3, this);
        this.netManager.chunkDataSendCounter = 0;
    }

    public void tryLogin() {
        if (this.packet1login != null) {
            this.doLogin(this.packet1login);
            this.packet1login = null;
        }

        if (this.loginTimer++ == 600) {
            this.kickUser("Took too long to log in");
        } else {
            this.netManager.update();
        }
    }

    @Override
    public void handlePing(PingPacket packet) {
        try {
            String string = this.mcServer.configManager.playerEntities.size() + this.mcServer.configManager.getMaxPlayers() + "§" + this.mcServer.serverName;
            this.netManager.sendPacket(new DisconnectPacket(string));
            this.netManager.closeConnection_ping();
            this.finishedProcessing = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void kickUser(String string1) {
        try {
            logger.info("Disconnecting " + this.getUserAndIPString() + ": " + string1);
            this.netManager.sendPacket(new DisconnectPacket(string1));
            this.netManager.closeConnection();
            this.finishedProcessing = true;
        } catch (Exception exception3) {
            exception3.printStackTrace();
        }
    }

    @Override
    public void handleHandshake(HandshakePacket packet2Handshake1) {
        if (this.mcServer.onlineMode) {
            this.serverId = Long.toHexString(rand.nextLong());
            this.netManager.sendPacket(new HandshakePacket(this.serverId));
        } else {
            this.netManager.sendPacket(new HandshakePacket("-"));
        }
    }

    @Override
    public void handleLogin(final LoginPacket packet1Login1) {
        this.username = packet1Login1.username;
        if (packet1Login1.protocolVersion != SharedConstants.PROTOCOL_VERSION) {
            if (packet1Login1.protocolVersion > SharedConstants.PROTOCOL_VERSION) {
                this.kickUser("Outdated server!");
            } else {
                this.kickUser("Outdated client!");
            }
        } else if (!this.mcServer.onlineMode) {
            this.doLogin(packet1Login1);
        } else {
            (new Thread() {
                    @Override
                    public void run() {
                        try {
                            String string1 = NetLoginHandler.this.serverId;
                            URL uRL2 = new URL(
                                "https://session.minecraft.net/game/checkserver.jsp?user="
                                    + URLEncoder.encode(packet1Login1.username, "UTF-8")
                                    + "&serverId="
                                    + URLEncoder.encode(string1, "UTF-8")
                            );
                            BufferedReader bufferedReader3 = new BufferedReader(new InputStreamReader(uRL2.openStream()));
                            String string4 = bufferedReader3.readLine();
                            bufferedReader3.close();
                            if (string4.equals("YES")) {
                                NetLoginHandler.this.packet1login = packet1Login1;
                            } else {
                                NetLoginHandler.this.kickUser("Failed to verify username!");
                            }
                        } catch (Exception exception5) {
                            NetLoginHandler.this.kickUser("Failed to verify username! [internal error " + exception5 + "]");
                            exception5.printStackTrace();
                        }
                    }
                })
                .start();
        }
    }

    public void doLogin(LoginPacket packet1Login1) {
        EntityPlayerMP player = this.mcServer.configManager.login(this, packet1Login1.username);
        if (player != null) {
            this.mcServer.configManager.readPlayerDataFromFile(player);
            // Clamp the id itself, not just the lookups. getWorldManager() degrades an
            // out-of-range dimension to the Overworld, but the field would keep its bad value
            // and every sendPacketToAllDimension filter compares against it — the player would
            // stand in the Overworld while being invisible to it and receiving nothing.
            // Also the door to a disabled dimension: without this, a save that puts someone in
            // Hell would ignore allow-dimensions=false.
            if (player.dimension < 0 || player.dimension >= MinecraftServer.DIMENSION_COUNT || !this.mcServer.allowDimensions && player.dimension != 0) {
                player.dimension = 0;
            }

            player.setWorld(this.mcServer.getWorldManager(player.dimension));
            logger.info(
                this.getUserAndIPString()
                    + " logged in with entity id "
                    + player.entityId
                    + " at ("
                    + player.posX
                    + ", "
                    + player.posY
                    + ", "
                    + player.posZ
                    + ")"
            );
            WorldServer level = this.mcServer.getWorldManager(player.dimension);
            Position pos = level.getSpawnPoint();
            NetServerHandler csnh = new NetServerHandler(this.mcServer, this.netManager, player);
            // 4th argument is the dimension, and the client feeds it straight into
            // `new NetworkWorld(this, seed, packet.dimension)`. Hardcoded to 0, a player who
            // logged out in Hell got an Overworld client world back on reconnect: Hell terrain
            // rendered with Overworld sky, lighting and fog.
            csnh.sendPacket(
                new LoginPacket(
                    "",
                    player.entityId,
                    level.getSeed(),
                    (byte)player.dimension,
                    (byte)this.mcServer.configManager.getMaxPlayers(),
                    (byte)level.getMaxSections()
                )
            );
            csnh.sendPacket(new SpawnPacket(pos.x, pos.y, pos.z));
            this.mcServer.configManager.updateWorldTimeForPlayers(player, level);
            this.mcServer.configManager.sendPacketToAll(new ChatPacket("§e" + player.username + " joined the game."));
            this.mcServer.configManager.playerLoggedIn(player);
            csnh.teleportTo(player.posX, player.posY, player.posZ, player.yaw, player.pitch);
            this.mcServer.networkListener.addPlayer(csnh);
            csnh.sendPacket(new UpdateTimePacket(level.getWorldTime()));

            for (EffectData ef : player.getEffects()) {
                csnh.sendPacket(new EntityEffectPacket(player.entityId, ef));
            }

            player.onGuiOpened();
        }

        this.finishedProcessing = true;
    }

    @Override
    public void handleDisconnect(DisconnectPacket packet) {
        String pingDisconnectData = this.mcServer.serverName
            + "\u0000"
            + this.mcServer.motd
            + "\u0000"
            + this.mcServer.configManager.playerEntities.size()
            + "\u0000"
            + this.mcServer.worlds[0].getSettings().getGamemode()
            + "\u0000"
            + this.mcServer.configManager.getMaxPlayers();
        File iconFile = new File("server-icon.png");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            BufferedImage icon = ImageIO.read(iconFile);
            if (icon.getWidth() > 128 || icon.getHeight() > 128) {
                BufferedImage resized = new BufferedImage(128, 128, 2);
                Graphics2D g = resized.createGraphics();
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g.drawImage(icon, 0, 0, 128, 128, null);
                g.dispose();
                icon = resized;
            }

            ImageIO.write(icon, "PNG", baos);
            baos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            this.netManager.sendPacket(new DisconnectPacket(pingDisconnectData));
            byte[] iconBytes = baos.toByteArray();
            this.netManager.sendPacket(new ServerIconPacket(iconBytes, this.mcServer.serverName));
            this.netManager.closeConnection();
            this.finishedProcessing = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleErrorMessage(String string1, Object... fmt) {
        logger.info(this.getUserAndIPString() + " lost connection");
        this.finishedProcessing = true;
    }

    @Override
    public void registerPacket(Packet packet1) {
        this.kickUser("Protocol error: " + packet1);
    }

    public String getUserAndIPString() {
        return this.username != null
            ? this.username + " [" + this.netManager.getRemoteAddress().toString() + "]"
            : this.netManager.getRemoteAddress().toString();
    }

    @Override
    public boolean isServerHandler() {
        return true;
    }
}
