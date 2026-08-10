package net.minecraft.client.gui.screens.worldselect;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiList;
import net.minecraft.client.gui.button.Button;
import net.minecraft.client.gui.button.TextField;
import net.minecraft.client.gui.screens.DialogueScreen;
import net.minecraft.client.gui.screens.GuiScreen;
import net.minecraft.client.render.Tessellator;
import net.minecraft.game.world.WorldInfo;
import net.minecraft.game.world.chunk.loader.format.FormatComparator;
import net.minecraft.game.world.chunk.loader.format.SaveFormat;
import net.minecraft.game.world.chunk.loader.handler.SaveHandler;
import net.minecraft.localization.Translator;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class WorldSelectScreen extends GuiScreen {
    private final DateFormat dateFormatter = new SimpleDateFormat();
    private List<FormatComparator> saveList;
    protected GuiScreen parent;
    private WorldSelectScreen.WorldSlot world;
    protected String title;
    private String name;
    private String[] gamemodes = new String[6];
    private Button rename;
    private Button select;
    private Button delete;
    private Button recreate;
    private TextField searchField;
    private boolean deleting;
    private int selectedWorld;

    public WorldSelectScreen(GuiScreen guiScreen1) {
        this.parent = guiScreen1;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        Translator translator = Translator.getInstance();
        this.title = translator.translateKey("selectWorld.title");
        this.name = translator.translateKey("selectWorld.world");
        this.gamemodes[0] = translator.translateKey("gameMode.survival");
        this.gamemodes[1] = translator.translateKey("gameMode.creative");
        this.gamemodes[2] = translator.translateKey("gameMode.adventure");
        this.gamemodes[3] = translator.translateKey("gameMode.spectator");
        this.loadSaves();
        this.world = new WorldSelectScreen.WorldSlot();
        this.initButtons();
    }

    private void loadSaves() {
        SaveFormat saves = this.mc.getSaveLoader();
        this.saveList = saves.getSaveList();
        Collections.sort(this.saveList);
        this.selectedWorld = -1;
    }

    private void filterWorlds() {
        SaveFormat saves = this.mc.getSaveLoader();
        List<FormatComparator> allSaves = saves.getSaveList();
        String query = this.searchField.getText().toLowerCase();
        this.saveList.clear();

        for (FormatComparator save : allSaves) {
            String name = save.getDisplayName().toLowerCase();
            if (name.contains(query)) {
                this.saveList.add(save);
            }
        }

        Collections.sort(this.saveList);
        this.selectedWorld = -1;
        this.select.enabled = false;
        this.rename.enabled = false;
        this.delete.enabled = false;
        this.recreate.enabled = false;
    }

    protected String getSaveFileName(int index) {
        return this.saveList.get(index).getFileName();
    }

    protected String getSaveName(int index) {
        String worldName = this.saveList.get(index).getDisplayName();
        if (worldName == null || MathHelper.stringNullOrLengthZero(worldName)) {
            worldName = Translator.getInstance().translateKey("selectWorld.world") + " " + (index + 1);
        }

        return worldName;
    }

    public void initButtons() {
        this.buttons.add(this.select = new Button(1, this.width / 2 - 154, this.height - 52, 150, 20, "selectWorld.select"));
        this.buttons.add(this.rename = new Button(6, this.width / 2 - 154, this.height - 28, 78, 20, "gui.edit"));
        this.buttons.add(this.delete = new Button(2, this.width / 2 - 74, this.height - 28, 70, 20, "gui.delete"));
        this.buttons.add(this.recreate = new Button(4, this.width / 2 + 4, this.height - 28, 78, 20, "selectWorld.recreate"));
        this.buttons.add(new Button(3, this.width / 2 + 4, this.height - 52, 150, 20, "selectWorld.create"));
        this.buttons.add(new Button(0, this.width / 2 + 84, this.height - 28, 70, 20, "gui.cancel"));
        this.select.enabled = false;
        this.rename.enabled = false;
        this.delete.enabled = false;
        this.recreate.enabled = false;
        this.searchField = new TextField(this.fontRenderer, this.width / 2 - 100, 6, 200, 20);
        this.searchField.setFlavorText(this.title);
        this.searchField.setMaxStringLength(32);
    }

    @Override
    protected void keyTyped(char character, int key) {
        if (this.searchField.isFocused()) {
            this.searchField.textboxKeyTyped(character, key);
            this.filterWorlds();
        }

        super.keyTyped(character, key);
    }

    @Override
    protected void mouseClicked(int x, int y, int click) {
        super.mouseClicked(x, y, click);
        this.searchField.mouseClicked(x, y, click);
    }

    @Override
    public void update() {
        this.searchField.updateCursor();
    }

    @Override
    public void onClose() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void buttonClicked(Button button) {
        if (button.enabled) {
            if (button.id == 2) {
                String worldName = this.getSaveName(this.selectedWorld);
                if (worldName != null) {
                    this.deleting = true;
                    Translator translator = Translator.getInstance();
                    String title = translator.translateKey("selectWorld.deleteQuestion");
                    String warning = "'" + worldName + "' " + translator.translateKey("selectWorld.deleteWarning");
                    String yes = translator.translateKey("gui.delete");
                    String no = translator.translateKey("gui.cancel");
                    DialogueScreen choiceScreen = new DialogueScreen(this, title, warning, yes, no, this.selectedWorld);
                    this.mc.setScreen(choiceScreen);
                }
            } else if (button.id == 1) {
                this.loadWorld(this.mc, this.selectedWorld);
            } else if (button.id == 3) {
                this.mc.setScreen(new WorldOptionsScreen());
            } else if (button.id == 6) {
                this.mc.setScreen(new WorldRenamingScreen(this, this.getSaveFileName(this.selectedWorld)));
            } else if (button.id == 0) {
                this.mc.setScreen(this.parent);
            } else if (button.id == 4) {
                WorldOptionsScreen optionsScreen = new WorldOptionsScreen();
                SaveHandler saves = this.mc.getSaveLoader().getSaveLoader(this.getSaveFileName(this.selectedWorld), false);
                optionsScreen.recreateWorld(saves.loadWorldInfo());
                this.mc.setScreen(optionsScreen);
            }
        }
    }

    @Override
    public void scroll(int scrollValue) {
        this.world.scrollMouse();
    }

    private void loadWorld(Minecraft mc, int worldIndex) {
        String name = this.getSaveFileName(worldIndex);
        SaveHandler save = mc.getSaveLoader().getSaveLoader(name, false);
        WorldInfo worldInfo = save.loadWorldInfo();
        int gamemode = worldInfo.getGamemode();
        mc.clientSetGamemode(gamemode);
        String worldName = name;
        if (worldName == null) {
            worldName = "World" + worldIndex;
        }

        mc.startWorld(worldName, name, 0L, null);
        mc.setScreen(null);
    }

    @Override
    public void confirmClicked(boolean click, int index) {
        if (this.deleting) {
            this.deleting = false;
            if (click) {
                SaveFormat saveLoader = this.mc.getSaveLoader();
                saveLoader.flushCache();
                saveLoader.deleteWorldDirectory(this.getSaveFileName(index));
                this.loadSaves();
            }

            this.mc.setScreen(this);
        }
    }

    @Override
    public void drawScreen(int x, int y, float tickDelta) {
        this.world.draw(x, y, tickDelta);
        this.searchField.drawTextBox();
        super.drawScreen(x, y, tickDelta);
    }

    private class WorldSlot extends GuiList {
        private boolean selectedSlot = false;

        public WorldSlot() {
            super(WorldSelectScreen.this.mc, WorldSelectScreen.this.width, WorldSelectScreen.this.height, 32, WorldSelectScreen.this.height - 64, 36);
            this.endWidth = 120;
        }

        @Override
        protected int listSize() {
            return WorldSelectScreen.this.saveList.size();
        }

        @Override
        protected void elementClicked(int index, boolean clicked) {
            WorldSelectScreen.this.selectedWorld = index;
            boolean worldSelected = WorldSelectScreen.this.selectedWorld >= 0 && WorldSelectScreen.this.selectedWorld < this.listSize();
            WorldSelectScreen.this.select.enabled = worldSelected;
            WorldSelectScreen.this.rename.enabled = worldSelected;
            WorldSelectScreen.this.delete.enabled = worldSelected;
            WorldSelectScreen.this.recreate.enabled = worldSelected;
            if ((clicked || this.selectedSlot) && worldSelected) {
                WorldSelectScreen.this.loadWorld(WorldSelectScreen.this.mc, index);
            }
        }

        @Override
        protected boolean isSelected(int index) {
            return index == WorldSelectScreen.this.selectedWorld;
        }

        @Override
        protected int contentSize() {
            return WorldSelectScreen.this.saveList.size() * 36;
        }

        @Override
        protected void drawBackground() {
            WorldSelectScreen.this.drawBackground();
        }

        @Override
        protected void drawSlot(int id, int x, int y, int height, Tessellator tess) {
            FormatComparator save = WorldSelectScreen.this.saveList.get(id);
            String worldName = save.getDisplayName();
            if (worldName == null || MathHelper.stringNullOrLengthZero(worldName)) {
                worldName = WorldSelectScreen.this.name + " " + (id + 1);
            }

            String worldFilename = save.getFileName();
            worldFilename = worldFilename + " (" + WorldSelectScreen.this.dateFormatter.format(new Date(save.getLastTimePlayed()));
            worldFilename = worldFilename + ")";
            String additionalInfo = "";
            int iconX = 0;
            if (save.requiresConversion()) {
                additionalInfo = Translator.getInstance().translateKey("selectWorld.conversion") + " " + additionalInfo;
                iconX = 32;
            } else {
                additionalInfo = WorldSelectScreen.this.gamemodes[save.getGameType()]
                    + (save.commandsEnabled() ? ", " + Translator.getInstance().translateKey("selectWorld.cheats") : "")
                    + ", "
                    + save.versionString;
            }

            WorldSelectScreen.this.drawString(WorldSelectScreen.this.fontRenderer, worldName, x + 34, y + 1, 16777215);
            WorldSelectScreen.this.drawString(WorldSelectScreen.this.fontRenderer, worldFilename, x + 34, y + 12, 8421504);
            WorldSelectScreen.this.drawString(WorldSelectScreen.this.fontRenderer, additionalInfo, x + 34, y + 12 + 10, 8421504);

            try {
                WorldSelectScreen.this.mc
                    .textureLoader
                    .bindTexture(WorldSelectScreen.this.mc.textureLoader.getTexture("&&saves/" + save.getFileName() + "/icon.png"));
            } catch (Exception ex) {
                WorldSelectScreen.this.mc.textureLoader.bindTexture(WorldSelectScreen.this.mc.textureLoader.getTexture("/gui/unknown_pack.png"));
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            tess.begin();
            tess.setColor(16777215);
            tess.addTexturedVertex(x, y + height, 0.0, 0.0F, 1.0F);
            tess.addTexturedVertex(x + height, y + height, 0.0, 1.0F, 1.0F);
            tess.addTexturedVertex(x + height, y, 0.0, 1.0F, 0.0F);
            tess.addTexturedVertex(x, y, 0.0, 0.0F, 0.0F);
            tess.draw();
            WorldSelectScreen.this.mc.textureLoader.bindTexture(WorldSelectScreen.this.mc.textureLoader.getTexture("/gui/gui.png"));
            int scaledWidth = this.mc.rescaler.getScaledWidth();
            int scaledHeight = this.mc.rescaler.getScaledHeight();
            int mouseX = Mouse.getX() * scaledWidth / this.mc.displayWidth;
            int mouseY = scaledHeight - Mouse.getY() * scaledHeight / this.mc.displayHeight - 1;
            int minX = this.width / 2 - this.startWidth;
            int maxX = this.width / 2 + this.endWidth;
            if (mouseY >= y - 3 && mouseY <= y + this.slotHeight - 3) {
                if (mouseX <= x + height) {
                    iconX = 16;
                    if (save.requiresConversion()) {
                        iconX = 48;
                    }

                    this.selectedSlot = true;
                } else {
                    this.selectedSlot = false;
                }

                if (mouseX >= minX && mouseX <= maxX) {
                    this.drawGradient(x, y, x + height, y + height, -2130706433, -2130706433, 1.0F);
                    this.drawGraphic(x + 10, y + 5, iconX, 22, 16, 24);
                }
            }
        }
    }
}
