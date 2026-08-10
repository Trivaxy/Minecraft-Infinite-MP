package net.minecraft.client.gui.screens;

import net.minecraft.client.gui.button.Button;

public class DialogueScreen extends GuiScreen {
    protected GuiScreen parentScreen;
    private String title;
    private String message;
    protected String confirm;
    protected String deny;
    private int worldNumber;

    public DialogueScreen(GuiScreen parent, String title, String msg, String yes, String no, int slot) {
        this.parentScreen = parent;
        this.title = title;
        this.message = msg;
        this.confirm = yes;
        this.deny = no;
        this.worldNumber = slot;
    }

    @Override
    public void initGui() {
        this.buttons.add(new Button(0, this.width / 2 - 155 + 0, this.height / 6 + 96, 150, 20, this.confirm));
        this.buttons.add(new Button(1, this.width / 2 - 155 + 160, this.height / 6 + 96, 150, 20, this.deny));
    }

    @Override
    protected void buttonClicked(Button button) {
        this.parentScreen.confirmClicked(button.id == 0, this.worldNumber);
    }

    @Override
    public void drawScreen(int x, int y, float tickDelta) {
        this.drawBackground();
        this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 70, 16777215);
        this.drawCenteredString(this.fontRenderer, this.message, this.width / 2, 90, 16777215);
        super.drawScreen(x, y, tickDelta);
    }
}
