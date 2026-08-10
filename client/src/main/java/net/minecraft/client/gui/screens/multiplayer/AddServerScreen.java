package net.minecraft.client.gui.screens.multiplayer;

import net.minecraft.client.gui.button.Button;
import net.minecraft.client.gui.button.TextField;
import net.minecraft.client.gui.screens.GuiScreen;
import net.minecraft.localization.Translator;
import org.lwjgl.input.Keyboard;

public class AddServerScreen extends GuiScreen {
    private GuiScreen parent;
    private TextField address;
    private TextField name;
    private ServerSelectScreen.ServerListData serverList;
    private boolean editing = false;

    public AddServerScreen(GuiScreen screen, ServerSelectScreen.ServerListData data, boolean edit) {
        this.parent = screen;
        this.serverList = data;
        this.editing = edit;
    }

    @Override
    public void update() {
        this.name.updateCursor();
        this.address.updateCursor();
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttons.clear();
        this.buttons.add(new Button(0, this.width / 2 - 100, this.height / 4 + 96 + 12, 200, 20, this.editing ? "selectWorld.renameButton" : "gui.addServer"));
        this.buttons.add(new Button(1, this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20, "gui.cancel"));
        this.name = new TextField(this.fontRenderer, this.width / 2 - 100, 76, 200, 20);
        this.name.setFocused(true);
        this.name.setText(this.serverList.data.name);
        this.address = new TextField(this.fontRenderer, this.width / 2 - 100, 116, 200, 20);
        this.address.setMaxStringLength(128);
        this.address.setText(this.serverList.data.ip);
        this.buttons.get(0).enabled = this.address.getText().length() > 0 && this.address.getText().split(":").length > 0 && this.name.getText().length() > 0;
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
                this.serverList.data.name = this.name.getText();
                this.serverList.data.ip = this.address.getText();
                this.parent.confirmClicked(true, 0);
            }
        }
    }

    @Override
    protected void keyTyped(char eventChar, int key) {
        if (key == 15) {
            if (this.name.focused) {
                this.name.setFocused(false);
                this.address.setFocused(true);
            } else {
                this.name.setFocused(true);
                this.address.setFocused(false);
            }
        } else if (key == 28) {
            this.buttonClicked(this.buttons.get(0));
        } else if (key == 1) {
            this.buttonClicked(this.buttons.get(1));
        } else {
            this.name.textboxKeyTyped(eventChar, key);
            this.address.textboxKeyTyped(eventChar, key);
        }

        this.buttons.get(0).enabled = this.address.getText().length() > 0 && this.address.getText().split(":").length > 0 && this.name.getText().length() > 0;
    }

    @Override
    protected void mouseClicked(int x, int y, int click) {
        super.mouseClicked(x, y, click);
        this.address.mouseClicked(x, y, click);
        this.name.mouseClicked(x, y, click);
    }

    @Override
    public void drawScreen(int x, int y, float tick) {
        this.drawBackground();
        this.drawCenteredString(
            this.fontRenderer,
            Translator.getInstance().translateKey(this.editing ? "gui.editServer" : "gui.addServer"),
            this.width / 2,
            this.height / 4 - 60 + 20,
            16777215
        );
        this.drawString(this.fontRenderer, Translator.getInstance().translateKey("gui.name"), this.width / 2 - 100, 63, 10526880);
        this.drawString(this.fontRenderer, Translator.getInstance().translateKey("gui.ip"), this.width / 2 - 100, 104, 10526880);
        this.name.drawTextBox();
        this.address.drawTextBox();
        super.drawScreen(x, y, tick);
    }
}
