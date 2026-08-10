package net.minecraft.client.gui.screens.multiplayer;

import net.minecraft.client.gui.button.Button;
import net.minecraft.client.gui.button.TextField;
import net.minecraft.client.gui.screens.GuiScreen;
import net.minecraft.localization.Translator;
import org.lwjgl.input.Keyboard;

public class DirectConnectScreen extends GuiScreen {
    private final GuiScreen parent;
    private final ServerSelectScreen.ServerListData server;
    private TextField inputField;

    public DirectConnectScreen(GuiScreen screen, ServerSelectScreen.ServerListData server) {
        this.parent = screen;
        this.server = server;
    }

    @Override
    public void update() {
        this.inputField.updateCursor();
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttons.clear();
        this.buttons.add(new Button(0, this.width / 2 - 100, this.height / 4 + 96 + 12, 200, 20, "gui.connect"));
        this.buttons.add(new Button(1, this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20, "gui.cancel"));
        this.inputField = new TextField(this.fontRenderer, this.width / 2 - 100, 116, 200, 20);
        this.inputField.setMaxStringLength(128);
        this.inputField.setFocused(true);
        String lastServer = this.mc.options.lastServer;
        if (lastServer.length() != 0) {
            this.inputField.setText(lastServer.trim());
        } else {
            this.inputField.setText("");
        }

        if (this.inputField.getText() != null) {
            this.buttons.get(0).enabled = this.inputField.getText().length() > 0 && this.inputField.getText().split(":").length > 0;
        }
    }

    @Override
    public void onClose() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void buttonClicked(Button button) {
        if (button.enabled) {
            if (button.id == 1) {
                this.parent.confirmClicked(false, 0);
            } else if (button.id == 0) {
                String txt = this.inputField.getText();
                this.mc.options.lastServer = txt;
                this.mc.options.saveOptions();
                this.server.data.ip = txt;
                this.parent.confirmClicked(true, 0);
            }
        }
    }

    @Override
    protected void keyTyped(char character, int key) {
        if (key == 1) {
            this.buttonClicked(this.buttons.get(1));
        } else if (key == 28) {
            this.buttonClicked(this.buttons.get(0));
        } else {
            this.inputField.textboxKeyTyped(character, key);
        }

        this.buttons.get(0).enabled = this.inputField.getText().length() > 0 && this.inputField.getText().split(":").length > 0;
    }

    @Override
    protected void mouseClicked(int x, int y, int click) {
        super.mouseClicked(x, y, click);
        this.inputField.mouseClicked(x, y, click);
    }

    @Override
    public void drawScreen(int x, int y, float tickDelta) {
        this.drawBackground();
        this.drawCenteredString(
            this.fontRenderer, Translator.getInstance().translateKey("gui.directConnect"), this.width / 2, this.height / 4 - 60 + 20, 16777215
        );
        this.drawString(this.fontRenderer, Translator.getInstance().translateKey("gui.ip"), this.width / 2 - 100, 100, 10526880);
        this.inputField.drawTextBox();
        super.drawScreen(x, y, tickDelta);
    }
}
