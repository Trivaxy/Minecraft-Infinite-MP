package net.minecraft.client.gui.screens.multiplayer;

import net.minecraft.client.gui.button.Button;
import net.minecraft.client.network.ClientHandler;
import net.minecraft.client.network.player.NetworkPlayer;
import net.minecraft.network.packet.entity.EntityActionPacket;
import org.lwjgl.input.Keyboard;

public class SleepScreen extends ChatScreen {
    @Override
    public void initGui() {
        super.initGui();
        this.buttons.add(new Button(1, this.width / 2 - 100, this.height - 40, 200, 20, "gui.cancel"));
    }

    @Override
    public void onClose() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    protected void keyTyped(char character, int key) {
        if (key == 1) {
            this.wakeEntity();
        } else if (key == 28) {
            String msg = this.inputField.getText().trim();
            if (!msg.isEmpty()) {
                this.mc.player.sendChatMessage(msg);
            }

            this.inputField.setText("");
        } else {
            super.keyTyped(character, key);
        }
    }

    @Override
    protected void buttonClicked(Button button) {
        if (button.id == 1) {
            this.wakeEntity();
        } else {
            super.buttonClicked(button);
        }
    }

    private void wakeEntity() {
        if (this.mc.player instanceof NetworkPlayer) {
            ClientHandler clientHandler = ((NetworkPlayer)this.mc.player).connection;
            clientHandler.sendPacket(new EntityActionPacket(this.mc.player, 3));
            return;
        }

        this.mc.player.wakeUpPlayer(true, true, false);
    }
}
