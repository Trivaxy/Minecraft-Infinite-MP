package net.minecraft.client.network;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectScreen;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.packet.misc.DisconnectPacket;
import net.minecraft.network.packet.misc.ServerIconPacket;

public class PingHandler extends NetworkHandler {
    public NetworkManager connection;
    public ServerSelectScreen.ServerListData data;
    public long startTime;
    protected boolean done = false;

    public PingHandler(String ipaddr, int port, ServerSelectScreen.ServerListData data) throws IOException {
        this.connection = new NetworkManager(new Socket(InetAddress.getByName(ipaddr), port), "Ping", this);
        this.data = data;
        this.startTime = System.currentTimeMillis();
    }

    public void processReadPackets() {
        if (!this.done) {
            this.connection.update();
        }

        this.connection.stop();
    }

    @Override
    public boolean isServerHandler() {
        return false;
    }

    private int parseIntWithDefault(String string, int id) {
        try {
            return Integer.parseInt(string.trim());
        } catch (Exception exception) {
            return id;
        }
    }

    @Override
    public void handleDisconnect(DisconnectPacket packet) {
        String[] serverInfo = packet.reason.split("\u0000");
        this.data.serversideName = serverInfo[0];
        this.data.motd = serverInfo[1];
        this.data.playerCount = serverInfo[2];
        this.data.maxPlayers = serverInfo[4];
        this.data.gamemode = this.parseIntWithDefault(serverInfo[3], -1);
        this.data.ping = System.currentTimeMillis() - this.startTime;
        this.data.pinged = true;
        this.connection.networkShutdown("disconnect.kicked");
        this.done = true;
    }

    @Override
    public void handleServerIcon(ServerIconPacket packet) {
        try {
            File iconDir = new File(Minecraft.getMinecraftDir(), "servers");
            if (!iconDir.exists()) {
                iconDir.mkdirs();
            }

            String safeName = packet.serverAddress.replaceAll("[^a-zA-Z0-9._-]", "_");
            File iconFile = new File(iconDir, safeName + ".png");

            try (FileOutputStream fos = new FileOutputStream(iconFile)) {
                fos.write(packet.iconData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class PingServerThread extends Thread {
        private final ServerSelectScreen.ServerListData server;
        public PingHandler ping = null;
        public boolean done = false;

        public PingServerThread(ServerSelectScreen.ServerListData server) {
            this.server = server;
        }

        public boolean isDone() {
            return this.done;
        }

        public void tick() {
            if (this.ping != null && !this.done) {
                this.ping.processReadPackets();
                if (this.ping.done) {
                    this.done = true;
                }
            }
        }

        private int parseIntWithDefault(String string, int id) {
            try {
                return Integer.parseInt(string.trim());
            } catch (Exception exception) {
                return id;
            }
        }

        @Override
        public void run() {
            String ip = this.server.data.ip;
            String[] strings = ip.split(":");
            if (ip.startsWith("[")) {
                int i4 = ip.indexOf("]");
                if (i4 > 0) {
                    String string5 = ip.substring(1, i4);
                    String string6 = ip.substring(i4 + 1).trim();
                    if (string6.startsWith(":") && string6.length() > 0) {
                        string6 = string6.substring(1);
                        strings = new String[]{string5, string6};
                    } else {
                        strings = new String[]{string5};
                    }
                }
            }

            if (strings.length > 2) {
                strings = new String[]{ip};
            }

            String ipaddr = strings[0];
            int port = strings.length > 1 ? this.parseIntWithDefault(strings[1], 25565) : 25565;

            try {
                this.ping = new PingHandler(ipaddr, port, this.server);
                this.ping.connection.sendPacket(new DisconnectPacket("Impdev"));
            } catch (Exception var6) {
            }
        }
    }
}
