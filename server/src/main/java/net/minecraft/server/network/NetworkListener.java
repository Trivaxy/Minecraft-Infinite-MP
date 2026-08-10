package net.minecraft.server.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.server.MinecraftServer;

public class NetworkListener {
    public static Logger logger = Logger.getLogger("Minecraft");
    private ServerSocket serverSocket;
    private Thread networkAcceptThread;
    public volatile boolean isListening = false;
    private int timePassed = 0;
    private ArrayList<NetLoginHandler> pendingConnections = new ArrayList<>();
    private ArrayList<NetServerHandler> playerList = new ArrayList<>();
    public MinecraftServer mcServer;

    public NetworkListener(MinecraftServer minecraftServer1, InetAddress inetAddress2, int i3) throws IOException {
        this.mcServer = minecraftServer1;
        this.serverSocket = new ServerSocket(i3, 0, inetAddress2);
        this.serverSocket.setPerformancePreferences(0, 2, 1);
        this.isListening = true;
        this.networkAcceptThread = new Thread("Listen thread") {
            @Override
            public void run() {
                HashMap<InetAddress, Long> hashMap1 = new HashMap<>();

                while (NetworkListener.this.isListening) {
                    try {
                        Socket socket2 = NetworkListener.this.serverSocket.accept();
                        if (socket2 != null) {
                            InetAddress inetAddress3 = socket2.getInetAddress();
                            if (hashMap1.containsKey(inetAddress3)
                                && !"127.0.0.1".equals(inetAddress3.getHostAddress())
                                && System.currentTimeMillis() - hashMap1.get(inetAddress3) < 5000L) {
                                hashMap1.put(inetAddress3, System.currentTimeMillis());
                                socket2.close();
                            } else {
                                hashMap1.put(inetAddress3, System.currentTimeMillis());
                                NetLoginHandler netLoginHandler4 = new NetLoginHandler(
                                    NetworkListener.this.mcServer, socket2, "Connection #" + NetworkListener.this.timePassed++
                                );
                                NetworkListener.this.addPendingConnection(netLoginHandler4);
                            }
                        }
                    } catch (IOException iOException5) {
                        iOException5.printStackTrace();
                    }
                }
            }
        };
        this.networkAcceptThread.start();
    }

    public void addPlayer(NetServerHandler netServerHandler1) {
        this.playerList.add(netServerHandler1);
    }

    private void addPendingConnection(NetLoginHandler netLoginHandler1) {
        if (netLoginHandler1 == null) {
            throw new IllegalArgumentException("Got null pendingconnection!");
        }

        this.pendingConnections.add(netLoginHandler1);
    }

    public void handleNetworkListenThread() {
        for (int i1 = 0; i1 < this.pendingConnections.size(); i1++) {
            NetLoginHandler netLoginHandler2 = this.pendingConnections.get(i1);

            try {
                netLoginHandler2.tryLogin();
            } catch (Exception exception5) {
                netLoginHandler2.kickUser("Internal server error");
                logger.log(Level.WARNING, "Failed to handle packet: " + exception5, exception5);
            }

            if (netLoginHandler2.finishedProcessing) {
                this.pendingConnections.remove(i1--);
            }

            netLoginHandler2.netManager.stop();
        }

        for (int var6 = 0; var6 < this.playerList.size(); var6++) {
            NetServerHandler netServerHandler6 = this.playerList.get(var6);

            try {
                netServerHandler6.handlePackets();
            } catch (Exception exception4) {
                logger.log(Level.WARNING, "Failed to handle packet: " + exception4, exception4);
                netServerHandler6.kickPlayer("Internal server error");
            }

            if (netServerHandler6.connectionClosed) {
                this.playerList.remove(var6--);
            }

            netServerHandler6.netManager.stop();
        }
    }
}
