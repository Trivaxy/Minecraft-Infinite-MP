package net.minecraft.client.gui.screens.multiplayer;

import com.mojang.nbt.NBTReader;
import com.mojang.nbt.tag.special.CompoundTag;
import com.mojang.nbt.tag.special.ListTag;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiList;
import net.minecraft.client.gui.button.Button;
import net.minecraft.client.gui.button.TextField;
import net.minecraft.client.gui.screens.DialogueScreen;
import net.minecraft.client.gui.screens.GuiScreen;
import net.minecraft.client.network.PingHandler;
import net.minecraft.client.render.Tessellator;
import net.minecraft.localization.Translator;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class ServerSelectScreen extends GuiScreen {
    private GuiScreen parent;
    private ServerSelectScreen.ServerSelectionMenu serverSlots;
    private List<ServerSelectScreen.ServerListData> serverList = new ArrayList<>();
    private ServerSelectScreen.ServerListData tempServer = null;
    private Button edit;
    private Button select;
    private Button delete;
    private TextField searchField;
    private boolean deleteClicked = false;
    private boolean addClicked = false;
    private boolean editClicked = false;
    private boolean directClicked = false;
    private int selectedServer = -1;
    private int pingTime = 0;

    public ServerSelectScreen(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void update() {
        if (this.pingTime < 100) {
            this.pingTime++;
        }

        this.searchField.updateCursor();
    }

    @Override
    public void initGui() {
        this.loadServerList();
        Keyboard.enableRepeatEvents(true);
        this.buttons.clear();
        this.serverSlots = new ServerSelectScreen.ServerSelectionMenu();
        this.buttons.add(this.edit = new Button(7, this.width / 2 - 154, this.height - 28, 70, 20, "gui.edit"));
        this.buttons.add(this.delete = new Button(2, this.width / 2 - 74, this.height - 28, 70, 20, "gui.delete"));
        this.buttons.add(this.select = new Button(1, this.width / 2 - 154, this.height - 52, 100, 20, "multiplayer.connect"));
        this.buttons.add(new Button(4, this.width / 2 - 50, this.height - 52, 100, 20, "gui.connect"));
        this.buttons.add(new Button(3, this.width / 2 + 4 + 50, this.height - 52, 100, 20, "gui.addServer"));
        this.buttons.add(new Button(8, this.width / 2 + 4, this.height - 28, 70, 20, "gui.refresh"));
        this.buttons.add(new Button(0, this.width / 2 + 4 + 76, this.height - 28, 75, 20, "gui.cancel"));
        boolean clickable = this.selectedServer >= 0 && this.selectedServer < this.serverSlots.listSize();
        this.select.enabled = clickable;
        this.edit.enabled = clickable;
        this.delete.enabled = clickable;
        this.searchField = new TextField(this.fontRenderer, this.width / 2 - 100, 6, 200, 20);
        this.searchField.setFlavorText(Translator.getInstance().translateKey("gui.searchMP"));
        this.searchField.setMaxStringLength(32);
    }

    private void loadServerList() {
        try {
            CompoundTag compound = NBTReader.read(new File(Minecraft.getMinecraftDir(), "servers.dat"));
            ListTag<CompoundTag> list = compound.getTagList("servers");
            this.serverList.clear();

            for (int i = 0; i < list.size(); i++) {
                this.serverList.add(ServerSelectScreen.ServerListData.createFromCompound(list.get(i)));
            }
        } catch (Exception var4) {
        }
    }

    private void saveServerList() {
        try {
            ListTag<CompoundTag> list = new ListTag<>();

            for (ServerSelectScreen.ServerListData serverListData : this.serverList) {
                list.add(serverListData.getCompoundTag());
            }

            CompoundTag compound = new CompoundTag();
            compound.setTag("servers", list);
            NBTReader.safeWrite(compound, new File(Minecraft.getMinecraftDir(), "servers.dat"));
        } catch (Exception var4) {
        }
    }

    private void filterServerList() {
        try {
            CompoundTag compound = NBTReader.read(new File(Minecraft.getMinecraftDir(), "servers.dat"));
            ListTag<CompoundTag> list = compound.getTagList("servers");
            String query = this.searchField.getText().toLowerCase();
            this.serverList.clear();

            for (int i = 0; i < list.size(); i++) {
                ServerSelectScreen.ServerListData server = ServerSelectScreen.ServerListData.createFromCompound(list.get(i));
                if (server.data.name.toLowerCase().contains(query)) {
                    this.serverList.add(server);
                }
            }

            Collections.sort(this.serverList);
            this.selectedServer = -1;
            this.select.enabled = false;
            this.edit.enabled = false;
            this.delete.enabled = false;
        } catch (Exception var6) {
        }
    }

    @Override
    public void onClose() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void mouseClicked(int x, int y, int click) {
        super.mouseClicked(x, y, click);
        this.searchField.mouseClicked(x, y, click);
    }

    @Override
    protected void buttonClicked(Button button) {
        if (button.enabled) {
            if (button == this.delete) {
                String serverName = this.serverList.get(this.selectedServer).data.name;
                if (serverName != null) {
                    this.deleteClicked = true;
                    String confirmationString = Translator.getInstance().translateKey("gui.serverDeletionConfirmation");
                    String warning = "'" + serverName + "' " + Translator.getInstance().translateKey("gui.serverDeletion");
                    String yes = "gui.delete";
                    String no = "gui.cancel";
                    DialogueScreen choiceScreen = new DialogueScreen(this, confirmationString, warning, yes, no, this.selectedServer);
                    this.mc.setScreen(choiceScreen);
                }
            } else if (button == this.select) {
                this.joinServer(this.selectedServer);
            } else if (button.id == 4) {
                this.directClicked = true;
                this.mc.setScreen(new DirectConnectScreen(this, this.tempServer = new ServerSelectScreen.ServerListData("Minecraft Server", "")));
            } else if (button.id == 3) {
                this.addClicked = true;
                this.mc.setScreen(new AddServerScreen(this, this.tempServer = new ServerSelectScreen.ServerListData("Minecraft Server", ""), false));
            } else if (button == this.edit) {
                this.editClicked = true;
                ServerSelectScreen.ServerListData ldata = this.serverList.get(this.selectedServer);
                this.mc.setScreen(new AddServerScreen(this, this.tempServer = new ServerSelectScreen.ServerListData(ldata.data.name, ldata.data.ip), true));
            } else if (button.id == 0) {
                this.mc.setScreen(this.parent);
            } else if (button.id == 8) {
                this.mc.setScreen(new ServerSelectScreen(this.parent));
            }
        }
    }

    @Override
    public void confirmClicked(boolean state, int index) {
        if (this.deleteClicked) {
            this.deleteClicked = false;
            if (state) {
                this.serverList.remove(index);
                this.saveServerList();
            }

            this.mc.setScreen(this);
        } else if (this.directClicked) {
            this.directClicked = false;
            if (state) {
                this.joinServer(this.tempServer);
            } else {
                this.mc.setScreen(this);
            }
        } else if (this.addClicked) {
            this.addClicked = false;
            if (state) {
                this.serverList.add(this.tempServer);
                this.saveServerList();
            }

            this.mc.setScreen(this);
        } else if (this.editClicked) {
            this.editClicked = false;
            if (state) {
                ServerSelectScreen.ServerListData serverlistdata = this.serverList.get(this.selectedServer);
                serverlistdata.data.name = this.tempServer.data.name;
                serverlistdata.data.ip = this.tempServer.data.ip;
                this.saveServerList();
            }

            this.mc.setScreen(this);
        }
    }

    @Override
    public void scroll(int scrollValue) {
        this.serverSlots.scrollMouse();
    }

    private int parseIntWithDefault(String hostname, int number) {
        try {
            return Integer.parseInt(hostname.trim());
        } catch (Exception exception4) {
            return number;
        }
    }

    @Override
    protected void keyTyped(char character, int index) {
        if (character == '\r') {
            this.buttonClicked(this.select);
        }

        if (this.searchField.isFocused()) {
            this.searchField.textboxKeyTyped(character, index);
            this.filterServerList();
        }

        super.keyTyped(character, index);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawBackground();
        this.serverSlots.draw(mouseX, mouseY, partialTicks);
        this.searchField.drawTextBox();
        if (this.serverList.isEmpty()) {
            this.mc
                .fontRenderer
                .drawStringWithShadow(Translator.getInstance().translateKey("multiplayer.no"), this.width / 2 - 75, this.height / 2 - 20, 16733525);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void joinServer(int index) {
        this.joinServer(this.serverList.get(index));
    }

    private void joinServer(ServerSelectScreen.ServerListData serverData) {
        String ip = serverData.data.ip;
        String[] splitIp = ip.split(":");
        if (ip.startsWith("[")) {
            int temp = ip.indexOf("]");
            if (temp > 0) {
                String hostname = ip.substring(1, temp);
                String port = ip.substring(temp + 1).trim();
                if (port.startsWith(":") && port.length() > 0) {
                    port = port.substring(1);
                    splitIp = new String[]{hostname, port};
                } else {
                    splitIp = new String[]{hostname};
                }
            }
        }

        if (splitIp.length > 2) {
            splitIp = new String[]{ip};
        }

        this.mc.setScreen(new ConnectingScreen(this, this.mc, splitIp[0], splitIp.length > 1 ? this.parseIntWithDefault(splitIp[1], 25565) : 25565));
    }

    private String setGamemodeString(int gm) {
        switch (gm) {
            case 0:
                return "§cSurvival";
            case 1:
                return "§9Creative";
            case 2:
                return "§aAdventure";
            case 3:
                return "§eSpectator";
            default:
                return "";
        }
    }

    public static class ServerData {
        public String name;
        public String ip;

        public ServerData(String name, String ip) {
            this.name = name;
            this.ip = ip;
        }

        public CompoundTag getCompoundTag() {
            CompoundTag compound = new CompoundTag();
            compound.setString("name", this.name);
            compound.setString("ip", this.ip);
            return compound;
        }

        public static ServerSelectScreen.ServerData createFromCompound(CompoundTag compound) {
            return new ServerSelectScreen.ServerData(compound.getString("name"), compound.getString("ip"));
        }
    }

    public static class ServerListData implements Comparable<ServerSelectScreen.ServerListData> {
        public ServerSelectScreen.ServerData data;
        public String playerCount;
        public String maxPlayers;
        public String motd;
        public String serversideName;
        public int gamemode = -1;
        public long ping;
        public boolean pinged = false;

        public ServerListData(ServerSelectScreen.ServerData serverData) {
            this.data = serverData;
        }

        public ServerListData(String name, String ip) {
            this.data = new ServerSelectScreen.ServerData(name, ip);
        }

        public CompoundTag getCompoundTag() {
            return this.data.getCompoundTag();
        }

        public static ServerSelectScreen.ServerListData createFromCompound(CompoundTag compound) {
            return new ServerSelectScreen.ServerListData(ServerSelectScreen.ServerData.createFromCompound(compound));
        }

        public int compareTo(ServerSelectScreen.ServerListData other) {
            return this.data.name.compareToIgnoreCase(other.data.name);
        }
    }

    private class ServerSelectionMenu extends GuiList {
        private Map<ServerSelectScreen.ServerData, PingHandler.PingServerThread> pingThreads = new HashMap<>();
        private boolean selectedSlot;

        public ServerSelectionMenu() {
            super(ServerSelectScreen.this.mc, ServerSelectScreen.this.width, ServerSelectScreen.this.height, 32, ServerSelectScreen.this.height - 64, 48);
        }

        @Override
        protected int listSize() {
            return ServerSelectScreen.this.serverList.size();
        }

        @Override
        protected void elementClicked(int idx, boolean selected) {
            ServerSelectScreen.this.selectedServer = idx;
            boolean validServerSelected = ServerSelectScreen.this.selectedServer >= 0 && ServerSelectScreen.this.selectedServer < this.listSize();
            ServerSelectScreen.this.select.enabled = validServerSelected;
            ServerSelectScreen.this.edit.enabled = validServerSelected;
            ServerSelectScreen.this.delete.enabled = validServerSelected;
            if ((selected || this.selectedSlot) && validServerSelected) {
                ServerSelectScreen.this.joinServer(idx);
            }
        }

        @Override
        protected boolean isSelected(int index) {
            return index == ServerSelectScreen.this.selectedServer;
        }

        @Override
        protected int contentSize() {
            return ServerSelectScreen.this.serverList.size() * 48;
        }

        @Override
        protected void drawBackground() {
            ServerSelectScreen.this.drawBackground();
        }

        @Override
        protected void drawSlot(int slotIndex, int slotX, int slotY, int offset, Tessellator tess) {
            ServerSelectScreen.ServerListData serverStorage = ServerSelectScreen.this.serverList.get(slotIndex);
            if (this.pingThreads.containsKey(serverStorage.data)) {
                PingHandler.PingServerThread serverPingThread = this.pingThreads.get(serverStorage.data);
                if (!serverPingThread.isDone()) {
                    serverPingThread.tick();
                }
            } else {
                PingHandler.PingServerThread tps = new PingHandler.PingServerThread(serverStorage);
                this.pingThreads.put(serverStorage.data, tps);
                tps.start();
                serverStorage.serversideName = "§8" + Translator.getInstance().translateKey("gui.ping");
            }

            this.drawString(ServerSelectScreen.this.fontRenderer, serverStorage.data.name, slotX + offset + 2, slotY + 1, 16777215);
            this.drawString(ServerSelectScreen.this.fontRenderer, serverStorage.serversideName, slotX + offset + 2, slotY + 12, 12566463);
            this.drawString(ServerSelectScreen.this.fontRenderer, serverStorage.motd, slotX + offset + 2, slotY + 12 + 11, 12566463);
            String playerCount = (serverStorage.playerCount + "/" + serverStorage.maxPlayers).replace("null", "0");
            this.drawString(
                ServerSelectScreen.this.fontRenderer,
                playerCount,
                slotX + 215 - ServerSelectScreen.this.fontRenderer.getStringWidth(playerCount),
                slotY + 12,
                8421504
            );
            String gamemodeString = ServerSelectScreen.this.setGamemodeString(serverStorage.gamemode);
            this.drawString(
                ServerSelectScreen.this.fontRenderer,
                gamemodeString,
                slotX + 215 - ServerSelectScreen.this.fontRenderer.getStringWidth(gamemodeString),
                slotY + 12 + 22,
                8421504
            );
            this.drawString(ServerSelectScreen.this.fontRenderer, serverStorage.data.ip, slotX + offset + 2, slotY + 12 + 22, 3158064);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.textureLoader.bindTexture(this.mc.textureLoader.getTexture("/gui/icons.png"));
            int iconX = 0;
            byte imgOffset;
            int heightOffset;
            if (serverStorage.pinged && serverStorage.ping != -2L) {
                imgOffset = 0;
                if (serverStorage.ping < 0L) {
                    heightOffset = 5;
                } else if (serverStorage.ping < 150L) {
                    heightOffset = 0;
                } else if (serverStorage.ping < 300L) {
                    heightOffset = 1;
                } else if (serverStorage.ping < 600L) {
                    heightOffset = 2;
                } else if (serverStorage.ping < 1000L) {
                    heightOffset = 3;
                } else {
                    heightOffset = 4;
                }
            } else {
                imgOffset = 1;
                heightOffset = (int)(System.currentTimeMillis() / 100L + slotIndex * 2 & 7L);
                if (heightOffset > 4) {
                    heightOffset = 8 - heightOffset;
                }

                iconX = 32;
                if (ServerSelectScreen.this.pingTime >= 100) {
                    serverStorage.serversideName = "§4" + Translator.getInstance().translateKey("gui.nocon");
                    imgOffset = 0;
                    heightOffset = 5;
                    iconX = 48;
                }
            }

            this.drawGraphic(slotX + 205, slotY, 0 + imgOffset * 10, 31 + heightOffset * 8, 10, 8);

            try {
                this.mc
                    .textureLoader
                    .bindTexture(this.mc.textureLoader.getTexture("&&servers/" + serverStorage.serversideName.replaceAll("[^a-zA-Z0-9._-]", "_") + ".png"));
            } catch (Exception ex) {
                this.mc.textureLoader.bindTexture(this.mc.textureLoader.getTexture("/gui/unknown_pack.png"));
            }

            tess.begin();
            tess.setColor(16777215);
            tess.addTexturedVertex(slotX, slotY + offset, 0.0, 0.0F, 1.0F);
            tess.addTexturedVertex(slotX + offset, slotY + offset, 0.0, 1.0F, 1.0F);
            tess.addTexturedVertex(slotX + offset, slotY, 0.0, 1.0F, 0.0F);
            tess.addTexturedVertex(slotX, slotY, 0.0, 0.0F, 0.0F);
            tess.draw();
            this.mc.textureLoader.bindTexture(this.mc.textureLoader.getTexture("/gui/gui.png"));
            int scaledWidth = this.mc.rescaler.getScaledWidth();
            int scaledHeight = this.mc.rescaler.getScaledHeight();
            int mouseX = Mouse.getX() * scaledWidth / this.mc.displayWidth;
            int mouseY = scaledHeight - Mouse.getY() * scaledHeight / this.mc.displayHeight - 1;
            int minX = this.width / 2 - this.startWidth;
            int maxX = this.width / 2 + this.endWidth;
            if (mouseY >= slotY - 3 && mouseY <= slotY + this.slotHeight - 3) {
                if (mouseX <= slotX + offset) {
                    iconX = 16;
                    if (ServerSelectScreen.this.pingTime >= 100) {
                        iconX = 64;
                    }

                    this.selectedSlot = true;
                } else {
                    this.selectedSlot = false;
                }

                if (mouseX >= minX && mouseX <= maxX) {
                    this.drawGradient(slotX, slotY, slotX + offset, slotY + offset, -2130706433, -2130706433, 1.0F);
                    this.drawGraphic(slotX + 13, slotY + 11, iconX, 22, 16, 24);
                }
            }
        }
    }
}
