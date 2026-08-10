package net.minecraft.client.gui.screens.multiplayer;

import net.minecraft.client.gui.button.Button;
import net.minecraft.client.gui.screens.GuiScreen;
import net.minecraft.client.network.ClientHandler;
import net.minecraft.localization.Translator;
import net.minecraft.network.packet.misc.KeepAlivePacket;

public class LoginScreen extends GuiScreen {
    private ClientHandler netHandler;
    private int updateCounter = 0;

    public LoginScreen(ClientHandler clientHandler) {
        this.netHandler = clientHandler;
    }

    @Override
    protected void keyTyped(char character, int key) {
    }

    @Override
    public void initGui() {
        this.buttons.clear();
    }

    @Override
    public void update() {
        this.updateCounter++;
        if (this.updateCounter % 20 == 0) {
            this.netHandler.sendPacket(new KeepAlivePacket());
        }

        if (this.netHandler != null) {
            this.netHandler.process();
        }
    }

    @Override
    protected void buttonClicked(Button button) {
    }

    @Override
    public void drawScreen(int x, int y, float ticks) {
        this.renderDirtBackground();
        this.drawCenteredString(
            this.fontRenderer, Translator.getInstance().translateKey("multiplayer.downloadingTerrain"), this.width / 2, this.height / 2 - 50, 16777215
        );
        super.drawScreen(x, y, ticks);
    }
}
