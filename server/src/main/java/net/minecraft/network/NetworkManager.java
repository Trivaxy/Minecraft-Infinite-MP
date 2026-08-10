package net.minecraft.network;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import net.minecraft.network.packet.Packet;

public class NetworkManager {
    private static Logger cock = Logger.getLogger("Minecraft");
    public static final Object threadSyncObject = new Object();
    public static int numReadThreads;
    public static int numWriteThreads;
    private Object sendQueueLock = new Object();
    private Socket networkSocket;
    private final SocketAddress remoteSocketAddress;
    private DataInputStream socketInputStream;
    private DataOutputStream socketOutputStream;
    private boolean isRunning = true;
    private List<Packet> readPackets = Collections.synchronizedList(new ArrayList<>());
    private List<Packet> dataPackets = Collections.synchronizedList(new ArrayList<>());
    private List<Packet> chunkDataPackets = Collections.synchronizedList(new ArrayList<>());
    private NetworkHandler netHandler;
    private boolean isServerTerminating = false;
    private Thread writeThread;
    private Thread readThread;
    private boolean isTerminating = false;
    private String terminationReason = "";
    private Object[] formatargs;
    private int timeSinceLastRead = 0;
    private int sendQueueByteLength = 0;
    public static long approxBytesTX;
    public static long approxBytesRX;
    public int chunkDataSendCounter = 0;
    private int packetSendFrequency = 50;

    public NetworkManager(Socket cocket, String connName, NetworkHandler handler) throws IOException {
        this.networkSocket = cocket;
        this.remoteSocketAddress = cocket.getRemoteSocketAddress();
        this.netHandler = handler;

        try {
            cocket.setSoTimeout(30000);
            cocket.setTrafficClass(24);
        } catch (SocketException e) {
            cock.warning("Connection: " + connName + ": " + e.getMessage());
        }

        this.socketInputStream = new DataInputStream(cocket.getInputStream());
        this.socketOutputStream = new DataOutputStream(new BufferedOutputStream(cocket.getOutputStream(), 5120));
        this.readThread = new Thread(connName + " read thread") {
            @Override
            public void run() {
                synchronized (NetworkManager.threadSyncObject) {
                    NetworkManager.numReadThreads++;
                }

                while (NetworkManager.this.isRunning && !NetworkManager.this.isServerTerminating) {
                    while (NetworkManager.this.readPacket()) {
                    }

                    try {
                        sleep(2L);
                    } catch (InterruptedException var5) {
                    }
                }

                synchronized (NetworkManager.threadSyncObject) {
                    NetworkManager.numReadThreads--;
                }
            }
        };
        this.writeThread = new Thread(connName + " write thread") {
            @Override
            public void run() {
                synchronized (NetworkManager.threadSyncObject) {
                    NetworkManager.numWriteThreads++;
                }

                while (NetworkManager.this.isRunning) {
                    while (NetworkManager.this.sendPacket()) {
                    }

                    try {
                        sleep(2L);
                    } catch (InterruptedException var5) {
                    }

                    try {
                        if (NetworkManager.this.socketOutputStream != null) {
                            NetworkManager.this.socketOutputStream.flush();
                        }
                    } catch (IOException e) {
                        if (!NetworkManager.this.isTerminating) {
                            NetworkManager.this.onNetworkError(e);
                        }

                        e.printStackTrace();
                    }
                }

                synchronized (NetworkManager.threadSyncObject) {
                    NetworkManager.numWriteThreads--;
                }
            }
        };
        this.readThread.start();
        this.writeThread.start();
    }

    public void sendPacket(Packet packet1) {
        if (!this.isServerTerminating) {
            synchronized (this.sendQueueLock) {
                this.sendQueueByteLength = this.sendQueueByteLength + packet1.getPacketSize() + 1;
                if (packet1.isDelayed) {
                    this.chunkDataPackets.add(packet1);
                } else {
                    this.dataPackets.add(packet1);
                }
            }
        }
    }

    private boolean sendPacket() {
        boolean z1 = false;

        try {
            if (!this.dataPackets.isEmpty()
                && (this.chunkDataSendCounter == 0 || System.currentTimeMillis() - this.dataPackets.get(0).creationTimeMillis >= this.chunkDataSendCounter)) {
                Packet packet2;
                synchronized (this.sendQueueLock) {
                    packet2 = this.dataPackets.remove(0);
                    this.sendQueueByteLength = this.sendQueueByteLength - (packet2.getPacketSize() + 1);
                }

                Packet.writePacket(packet2, this.socketOutputStream);
                approxBytesTX = approxBytesTX + (packet2.getPacketSize() + 1);
                z1 = true;
            }

            if (this.packetSendFrequency-- <= 0
                && !this.chunkDataPackets.isEmpty()
                && (this.chunkDataSendCounter == 0 || System.currentTimeMillis() - this.chunkDataPackets.get(0).creationTimeMillis >= this.chunkDataSendCounter)
                )
             {
                Packet packet2;
                synchronized (this.sendQueueLock) {
                    packet2 = this.chunkDataPackets.remove(0);
                    this.sendQueueByteLength = this.sendQueueByteLength - (packet2.getPacketSize() + 1);
                }

                Packet.writePacket(packet2, this.socketOutputStream);
                approxBytesTX = approxBytesTX + (packet2.getPacketSize() + 1);
                this.packetSendFrequency = 0;
                z1 = true;
            }

            return z1;
        } catch (Exception exception8) {
            if (!this.isTerminating) {
                this.onNetworkError(exception8);
            }

            return false;
        }
    }

    private boolean readPacket() {
        boolean z1 = false;

        try {
            Packet packet2 = Packet.readPacket(this.socketInputStream, this.netHandler.isServerHandler());
            if (packet2 != null) {
                approxBytesRX = approxBytesRX + (packet2.getPacketSize() + 1);
                this.readPackets.add(packet2);
                z1 = true;
            } else {
                this.networkShutdown("disconnect.endOfStream");
            }

            return z1;
        } catch (Exception exception3) {
            if (!this.isTerminating) {
                this.onNetworkError(exception3);
            }

            return false;
        }
    }

    private void onNetworkError(Exception exception1) {
        exception1.printStackTrace();
        this.networkShutdown("disconnect.genericReason", "Internal exception: " + exception1.toString());
    }

    public void networkShutdown(String string1, Object... object2) {
        if (this.isRunning) {
            this.isTerminating = true;
            this.terminationReason = string1;
            this.formatargs = object2;
            (new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(5000L);
                        if (NetworkManager.this.readThread.isAlive()) {
                            try {
                                NetworkManager.this.readThread.stop();
                            } catch (Throwable var3) {
                            }
                        }

                        if (NetworkManager.this.writeThread.isAlive()) {
                            try {
                                NetworkManager.this.writeThread.stop();
                            } catch (Throwable var2) {
                            }
                        }
                    } catch (InterruptedException interruptedException4) {
                        interruptedException4.printStackTrace();
                    }
                }
            }).start();
            this.isRunning = false;

            try {
                this.socketInputStream.close();
                this.socketInputStream = null;
            } catch (Throwable var6) {
            }

            try {
                this.socketOutputStream.close();
                this.socketOutputStream = null;
            } catch (Throwable var5) {
            }

            try {
                this.networkSocket.close();
                this.networkSocket = null;
            } catch (Throwable var4) {
            }
        }
    }

    public void update() {
        if (this.sendQueueByteLength > 1048576) {
            this.networkShutdown("disconnect.overflow");
        }

        if (this.readPackets.isEmpty()) {
            if (this.timeSinceLastRead++ == 1200) {
                this.networkShutdown("disconnect.timeout");
            }
        } else {
            this.timeSinceLastRead = 0;
        }

        int i1 = 100;

        while (!this.readPackets.isEmpty() && i1-- >= 0) {
            Packet packet2 = this.readPackets.remove(0);
            packet2.processPacket(this.netHandler);
        }

        this.stop();
        if (this.isTerminating && this.readPackets.isEmpty()) {
            this.netHandler.handleErrorMessage(this.terminationReason, this.formatargs);
        }
    }

    public int getNumChunkDataPackets() {
        return this.chunkDataPackets.size();
    }

    public void setNetHandler(NetworkHandler netHandler) {
        this.netHandler = netHandler;
    }

    public SocketAddress getRemoteAddress() {
        return this.remoteSocketAddress;
    }

    public void stop() {
        if (this.readThread != null) {
            this.readThread.interrupt();
        }

        if (this.writeThread != null) {
            this.writeThread.interrupt();
        }
    }

    public void closeConnection() {
        this.stop();
        this.isServerTerminating = true;
        this.readThread.interrupt();
        (new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000L);
                    if (NetworkManager.this.isRunning) {
                        NetworkManager.this.writeThread.interrupt();
                        NetworkManager.this.networkShutdown("Connection closed");
                    }
                } catch (Exception exception2) {
                    exception2.printStackTrace();
                }
            }
        }).start();
    }

    public void closeConnection_ping() {
        if (!this.isServerTerminating) {
            this.closeConnection();
        }
    }

    public Socket getSocket() {
        return this.networkSocket;
    }
}
