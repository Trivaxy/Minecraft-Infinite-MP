package net.minecraft.client.gui.overlay;

import java.awt.Color;
import net.minecraft.client.localization.Font;
import net.minecraft.localization.Translator;
import org.lwjgl.opengl.GL11;

public class MusicOverlay {
    public String trackName = "";
    public int duration = 0;

    public void setTrack(String record) {
        this.trackName = Translator.getInstance().translateKey("menu.playing") + ": " + record;
        this.duration = 60;
    }

    public void display(Font font, float deltaTime, int width, int height) {
        if (this.duration > 0) {
            float timer = this.duration - deltaTime;
            int maxValue = (int)(timer * 256.0F / 10.0F);
            if (maxValue > 255) {
                maxValue = 255;
            }

            if (maxValue > 8) {
                GL11.glPushMatrix();
                GL11.glEnable(3042);
                GL11.glBlendFunc(770, 771);
                int randomColor = Color.HSBtoRGB(timer / 50.0F, 0.7F, 0.6F) & 16777215;
                font.drawStringWithShadow(this.trackName, 8, 6, randomColor + (maxValue << 24));
                GL11.glDisable(3042);
                GL11.glPopMatrix();
            }
        }
    }
}
