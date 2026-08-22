package net.minecraft.client.gui.screens.multiplayer;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.logging.Logger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.button.Button;
import net.minecraft.client.gui.screens.GuiScreen;
import net.minecraft.client.network.ClientHandler;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.localization.Translator;
import net.minecraft.network.packet.misc.HandshakePacket;

public class ConnectingScreen extends GuiScreen {
    static Logger info = Logger.getLogger("Minecraft");
    private ClientHandler clientHandler;
    private ServerSelectScreen parent;
    private String hostDispString;
    private boolean cancelled = false;

    public ConnectingScreen(ServerSelectScreen screen, Minecraft minecraft, String hostname, int port) {
        this(screen, minecraft, hostname, port, false);
    }

    public ConnectingScreen(ServerSelectScreen screen, Minecraft minecraft, String hostname) {
        this(screen, minecraft, hostname, ServerAddress.DEFAULT_PORT, true);
    }

    private ConnectingScreen(ServerSelectScreen screen, Minecraft minecraft, String hostname, int port, boolean resolveSrv) {
        this.mc = minecraft;
        info.info("Connecting to " + hostname + ", " + port);
        this.hostDispString = Translator.getInstance().translateKeyFormat("connect.connecting", hostname + ":" + port);
        minecraft.clearWorld();
        this.parent = screen;
        new ConnectingScreen.ThreadConnectToServer(this, minecraft, hostname, port, resolveSrv).start();
    }

    @Override
    public void update() {
        if (this.clientHandler != null) {
            this.clientHandler.process();
        }
    }

    @Override
    public void onClose() {
    }

    @Override
    protected void keyTyped(char character, int key) {
    }

    @Override
    public void initGui() {
        this.buttons.clear();
        this.buttons.add(new Button(0, this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20, "gui.cancel"));
    }

    @Override
    protected void buttonClicked(Button button) {
        if (button.id == 0) {
            this.cancelled = true;
            if (this.clientHandler != null) {
                this.clientHandler.shutdown();
            }

            this.mc.setScreen(this.parent);
        }
    }

    static ClientHandler setNetClientHandler(ConnectingScreen screen, ClientHandler clientHandler) {
        return screen.clientHandler = clientHandler;
    }

    static ClientHandler getNetClientHandler(ConnectingScreen screen) {
        return screen.clientHandler;
    }

    static boolean isCancelled(ConnectingScreen screen) {
        return screen.cancelled;
    }

    public static ServerSelectScreen getParent(ConnectingScreen screen) {
        return screen.parent;
    }

    @Override
    public void drawScreen(int x, int y, float deltaTick) {
        Translator translator = Translator.getInstance();
        this.drawBackground();
        if (this.clientHandler == null) {
            this.drawCenteredString(this.fontRenderer, this.hostDispString, this.width / 2, this.height / 2 - 50, 16777215);
            this.drawCenteredString(this.fontRenderer, "", this.width / 2, this.height / 2 - 10, 16777215);
        } else {
            this.drawCenteredString(this.fontRenderer, translator.translateKeyFormat("connect.authorizing"), this.width / 2, this.height / 2 - 50, 16777215);
            this.drawCenteredString(this.fontRenderer, this.clientHandler.serverField, this.width / 2, this.height / 2 - 10, 16777215);
        }

        super.drawScreen(x, y, deltaTick);
    }

    private class ThreadConnectToServer extends Thread {
        final Minecraft mc;
        final String ip;
        final int port;
        final boolean resolveSrv;
        final ConnectingScreen connectingGui;

        private ThreadConnectToServer(ConnectingScreen screen, Minecraft minecraft, String hostname, int port, boolean resolveSrv) {
            this.connectingGui = screen;
            this.mc = minecraft;
            this.ip = hostname;
            this.port = port;
            this.resolveSrv = resolveSrv;
        }

        @Override
        public void run() {
            try {
                ServerAddress address = this.resolveSrv ? ServerAddress.resolve(this.ip) : ServerAddress.of(this.ip, this.port);
                ConnectingScreen.setNetClientHandler(this.connectingGui, new ClientHandler(this.mc, address.getHost(), address.getPort()));
                if (ConnectingScreen.isCancelled(this.connectingGui)) {
                    return;
                }

                ConnectingScreen.getNetClientHandler(this.connectingGui).sendPacket(new HandshakePacket(this.mc.session.username));
            } catch (UnknownHostException ex) {
                if (ConnectingScreen.isCancelled(this.connectingGui)) {
                    return;
                }

                this.mc
                    .setScreen(
                        new DisconnectedScreen(
                            ConnectingScreen.getParent(this.connectingGui), "connect.failed", "disconnect.genericReason", "Unknown host '" + this.ip + "'"
                        )
                    );
            } catch (ConnectException ex) {
                if (ConnectingScreen.isCancelled(this.connectingGui)) {
                    return;
                }

                this.mc
                    .setScreen(
                        new DisconnectedScreen(ConnectingScreen.getParent(this.connectingGui), "connect.failed", "disconnect.genericReason", ex.getMessage())
                    );
            } catch (Exception ex) {
                if (ConnectingScreen.isCancelled(this.connectingGui)) {
                    return;
                }

                ex.printStackTrace();
                this.mc
                    .setScreen(
                        new DisconnectedScreen(ConnectingScreen.getParent(this.connectingGui), "connect.failed", "disconnect.genericReason", ex.toString())
                    );
            }
        }
    }
}
