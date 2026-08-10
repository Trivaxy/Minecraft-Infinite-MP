package net.minecraft.client.gui.screens.worldselect;

import java.io.File;
import net.minecraft.client.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.button.Button;
import net.minecraft.client.gui.button.LockerButton;
import net.minecraft.client.gui.button.TextField;
import net.minecraft.client.gui.screens.DialogueScreen;
import net.minecraft.client.gui.screens.GuiScreen;
import net.minecraft.game.world.WorldInfo;
import net.minecraft.game.world.chunk.loader.format.SaveFormat;
import net.minecraft.localization.Translator;
import org.lwjgl.input.Keyboard;

public class WorldRenamingScreen extends GuiScreen {
    private final GuiScreen parent;
    private TextField nameField;
    private LockerButton lock;
    private final String name;
    private File saves;

    public WorldRenamingScreen(GuiScreen screen, String name) {
        this.parent = screen;
        this.name = name;
    }

    @Override
    public void update() {
        this.nameField.updateCursor();
    }

    @Override
    public void initGui() {
        SaveFormat saveLoader = this.mc.getSaveLoader();
        WorldInfo worldInfo = saveLoader.getWorldInfo(this.name);
        File directory = Minecraft.getMinecraftDir();
        Keyboard.enableRepeatEvents(true);
        this.buttons.clear();
        this.buttons.add(new Button(4, this.width / 2 - 100, this.height / 4 + 72 + 12, 178, 20, "selectWorld.resetIcon", 208, 128));
        this.buttons.add(this.lock = new LockerButton(3, this.width / 2 + 80, this.height / 4 + 72 + 12, "selectWorld.lockIcon"));
        this.buttons.add(new Button(2, this.width / 2 - 100, this.height / 4 + 96 + 12, 200, 20, "options.packFolder", 32, 128));
        this.buttons.add(new Button(1, this.width / 2 + 4, this.height / 4 + 120 + 12, 96, 20, "gui.cancel"));
        this.buttons.add(new Button(0, this.width / 2 - 100, this.height / 4 + 120 + 12, 102, 20, "selectWorld.renameButton"));
        this.saves = new File(directory, "saves/" + this.name + "/icon.png");
        if (!this.saves.canWrite()) {
            this.lockIcon();
        }

        this.nameField = new TextField(this.fontRenderer, this.width / 2 - 100, this.height / 4 + 16 + 12, 200, 20);
        this.nameField.setFocused(true);
        this.nameField.setText(worldInfo.getWorldName());
    }

    @Override
    public void onClose() {
        Keyboard.enableRepeatEvents(false);
    }

    private void lockIcon() {
        this.lock.enabled = false;
        this.lock.locked = true;
    }

    @Override
    protected void buttonClicked(Button button) {
        if (button.enabled) {
            if (button.id == 0) {
                SaveFormat saveLoader = this.mc.getSaveLoader();
                saveLoader.renameWorld(this.name, this.nameField.getText().trim());
                this.mc.setScreen(this.parent);
            }

            if (button.id == 1) {
                this.mc.setScreen(this.parent);
            }

            if (button.id == 2) {
                File directory = Minecraft.getMinecraftDir();
                File saves = new File(directory, "saves/" + this.name);
                ClientUtils.openFile(saves);
            }

            if (button.id == 3) {
                String confirmationString = Translator.getInstance().translateKey("gui.iconLockConfirmation");
                String warning = Translator.getInstance().translateKey("gui.lockIcon");
                String yes = "gui.yes";
                String no = "gui.no";
                DialogueScreen choiceScreen = new DialogueScreen(this, confirmationString, warning, yes, no, 0);
                this.mc.setScreen(choiceScreen);
            }

            if (button.id == 4) {
                this.saves.delete();
                this.mc.textureLoader.textureMap.remove("&&saves/" + this.name + "/icon.png");
            }
        }
    }

    @Override
    public void confirmClicked(boolean check, int id) {
        if (check) {
            this.saves.setReadOnly();
            this.lockIcon();
        }

        this.mc.setScreen(this);
    }

    @Override
    protected void keyTyped(char character, int key) {
        this.nameField.textboxKeyTyped(character, key);
        this.buttons.get(4).enabled = !this.nameField.getText().trim().isEmpty();
        if (character == '\r') {
            this.buttonClicked(this.buttons.get(0));
        }
    }

    @Override
    protected void mouseClicked(int x, int y, int click) {
        super.mouseClicked(x, y, click);
        this.nameField.mouseClicked(x, y, click);
    }

    @Override
    public void drawScreen(int x, int y, float tickDelta) {
        Translator translator = Translator.getInstance();
        this.drawBackground();
        this.drawCenteredString(this.fontRenderer, translator.translateKey("selectWorld.renameTitle"), this.width / 2, 24, 16777215);
        this.drawString(this.fontRenderer, translator.translateKey("selectWorld.enterName"), this.width / 2 - 100, this.height / 4 + 16, 10526880);
        this.nameField.drawTextBox();
        super.drawScreen(x, y, tickDelta);
    }
}
