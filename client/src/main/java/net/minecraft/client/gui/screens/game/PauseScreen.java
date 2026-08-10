package net.minecraft.client.gui.screens.game;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshotter;
import net.minecraft.client.gui.button.Button;
import net.minecraft.client.gui.overlay.MusicOverlay;
import net.minecraft.client.gui.screens.GuiScreen;
import net.minecraft.client.gui.screens.options.GeneralScreen;
import net.minecraft.client.gui.screens.stats.AchievementsScreen;
import net.minecraft.client.gui.screens.stats.StatsScreen;
import net.minecraft.client.gui.screens.title.TitleScreen;
import net.minecraft.client.options.OptionValues;
import net.minecraft.localization.Translator;
import net.minecraft.util.math.MathHelper;

public class PauseScreen extends GuiScreen {
    private MusicOverlay currentTrack = new MusicOverlay();
    private int updateCounter = 0;

    @Override
    public void initGui() {
        super.initGui();
        if (this.mc.sndManager.sndSystem.playing("BgMusic")) {
            this.currentTrack.setTrack(this.mc.sndManager.trackArtist + " - " + this.mc.sndManager.currentTrack);
        }

        this.buttons.clear();
        this.buttons.add(new Button(1, this.width / 2 - 100, this.height / 4 + 120, 200, 20, "menu.save", 96, 128));
        this.buttons.add(new Button(2, this.width / 2 - 100, this.height / 4 + 48, 98, 20, "gui.achievements", 112, 128));
        this.buttons.add(new Button(3, this.width / 2 + 2, this.height / 4 + 48, 98, 20, "gui.stats", 128, 128));
        this.buttons.add(new Button(4, this.width / 2 - 100, this.height / 4 + 24, 200, 20, "menu.back", 144, 128));
        this.buttons.add(new Button(0, this.width / 2 - 100, this.height / 4 + 96, 200, 20, "menu.options", 48, 128));
        if (this.mc.isMultiplayerWorld()) {
            this.buttons.get(0).label = Translator.getInstance().translateKey("gui.disconnect");
        }
    }

    @Override
    protected void buttonClicked(Button guiButton1) {
        if (guiButton1.id == 0) {
            this.mc.setScreen(new GeneralScreen(this.mc.options));
        }

        if (guiButton1.id == 1) {
            if (this.mc.isMultiplayerWorld()) {
                this.mc.world.disconnect();
            }

            this.mc.clearWorld();
            this.mc.setScreen(new TitleScreen());
            OptionValues.showHUD = true;
        }

        if (guiButton1.id == 2) {
            this.mc.setScreen(new AchievementsScreen(this.mc.statistics));
        }

        if (guiButton1.id == 3) {
            this.mc.setScreen(new StatsScreen(this, this.mc.statistics));
        }

        if (guiButton1.id == 4) {
            this.mc.setScreen(null);
            this.mc.setFocus();
        }
    }

    @Override
    public void update() {
        super.update();
        this.updateCounter++;
        if (this.currentTrack.duration > 0) {
            this.currentTrack.duration--;
        }
    }

    @Override
    public void drawScreen(int x, int y, float tickDelta) {
        if (this.updateCounter == 1) {
            Screenshotter.createIcon(Minecraft.getMinecraftDir(), this.mc.displayWidth, this.mc.displayHeight);
            this.mc.world.saveAll();
        }

        this.drawBackground();
        if (this.updateCounter < 60) {
            float alpha = (this.updateCounter % 10 + tickDelta) / 10.0F;
            alpha = MathHelper.sin(alpha * (float) Math.PI * 2.0F) * 0.2F + 0.8F;
            int col = (int)(255.0F * alpha);
            this.drawString(this.fontRenderer, Translator.getInstance().translateKey("menu.saving"), 8, this.height - 16, col << 16 | col << 8 | col);
        }

        this.currentTrack.display(this.fontRenderer, tickDelta, 12 + this.mc.fontRenderer.getStringWidth(this.currentTrack.trackName), 56);
        this.drawCenteredString(this.fontRenderer, Translator.getInstance().translateKey("menu.menu"), this.width / 2, 40, 16777215);
        super.drawScreen(x, y, tickDelta);
    }
}
