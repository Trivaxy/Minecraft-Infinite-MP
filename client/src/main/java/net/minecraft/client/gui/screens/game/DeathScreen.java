package net.minecraft.client.gui.screens.game;

import net.minecraft.client.gui.button.Button;
import net.minecraft.client.gui.screens.GuiScreen;
import net.minecraft.client.gui.screens.title.TitleScreen;
import net.minecraft.game.entity.player.PlayerProperties;
import net.minecraft.game.world.World;
import net.minecraft.game.world.chunk.loader.format.SaveFormat;
import net.minecraft.localization.Translator;
import org.lwjgl.opengl.GL11;

public class DeathScreen extends GuiScreen {
    private int counter;

    @Override
    public void initGui() {
        this.buttons.clear();
        if (this.mc.player.properties.getPermadeath()) {
            this.buttons.add(new Button(1, this.width / 2 - 100, this.height / 2 + 24, 200, 20, "gui.spectate"));
            this.buttons.add(new Button(2, this.width / 2 - 100, this.height / 2 + 48, 200, 20, "deathScreen.deleteWorld"));
        } else {
            this.buttons.add(new Button(1, this.width / 2 - 100, this.height / 2 + 24, 200, 20, "gui.respawn"));
            this.buttons.add(new Button(2, this.width / 2 - 100, this.height / 2 + 48, 200, 20, "gui.quit"));
        }

        this.buttons.get(0).enabled = false;
        this.buttons.get(1).enabled = false;
    }

    @Override
    public void update() {
        if (this.counter < 20) {
            this.counter++;
        }

        if (this.counter >= 20) {
            this.buttons.get(0).enabled = true;
            this.buttons.get(1).enabled = true;
        }
    }

    @Override
    protected void keyTyped(char character, int key) {
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    protected void buttonClicked(Button button) {
        if (button.id == 1) {
            if (this.mc.player.properties.getPermadeath() && !this.mc.world.isOnline) {
                this.mc.setGamemode(PlayerProperties.SPECTATOR);
            }

            this.mc.player.respawnPlayer();
            this.mc.setScreen(null);
        }

        if (button.id == 2) {
            World world = this.mc.world;
            if (this.mc.player.properties.getPermadeath() && !world.isOnline) {
                this.mc.changeWorld(null, Translator.getInstance().translateKey("gui.deletelvl"));
                SaveFormat saveLoader = this.mc.getSaveLoader();
                saveLoader.flushCache();
                saveLoader.deleteWorldDirectory(world.getSaveHandler().getSaveDirectoryName());
                this.mc.setScreen(new TitleScreen());
            } else {
                if (this.mc.isMultiplayerWorld()) {
                    this.mc.world.disconnect();
                }

                this.mc.clearWorld();
                this.mc.setScreen(new TitleScreen());
            }
        }
    }

    @Override
    public void drawScreen(int x, int y, float tickDelta) {
        Translator st = Translator.getInstance();
        this.applyBlur();
        this.drawGradient(0, 0, this.width, this.height, 1615855616, -1602211792, 1.0F);
        GL11.glPushMatrix();
        GL11.glScalef(2.0F, 2.0F, 2.0F);
        if (this.mc.player.properties.getPermadeath()) {
            this.drawCenteredString(this.fontRenderer, st.translateKey("deathScreen.gameOver.hardcore"), this.width / 2 / 2, 30, 16777215);
        } else {
            this.drawCenteredString(this.fontRenderer, st.translateKey("deathScreen.gameOver"), this.width / 2 / 2, 30, 16777215);
        }

        GL11.glPopMatrix();
        this.drawCenteredString(this.fontRenderer, this.mc.player.lastDeath, this.width / 2, 84, 16777215);
        if (this.mc.player.properties.getPermadeath()) {
            this.drawCenteredString(this.fontRenderer, st.translateKey("deathScreen.hardcoreInfo"), this.width / 2, this.height / 2 - 10, 16733525);
        }

        this.drawCenteredString(this.fontRenderer, st.translateKey("deathScreen.score") + ": " + "§e" + this.mc.player.score, this.width / 2, 100, 16777215);
        super.drawScreen(x, y, tickDelta);
    }
}
