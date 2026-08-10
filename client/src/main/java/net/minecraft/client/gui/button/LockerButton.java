package net.minecraft.client.gui.button;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

public class LockerButton extends Button {
    public boolean locked;

    public LockerButton(int id, int x, int y, String str) {
        super(id, x, y, 20, 20, "", str);
    }

    @Override
    protected void drawBg(Minecraft mc, byte state, boolean hovered) {
        GL11.glPushMatrix();
        if (this.locked) {
            this.drawGraphic(this.x, this.y, 200, 46 + state * 20, this.width / 2, this.height);
            this.drawGraphic(this.x + this.width / 2, this.y, 220 - this.width / 2, 46 + state * 20, this.width / 2, this.height);
        } else {
            this.drawGraphic(this.x, this.y, 220, 46 + state * 20, this.width / 2, this.height);
            this.drawGraphic(this.x + this.width / 2, this.y, 240 - this.width / 2, 46 + state * 20, this.width / 2, this.height);
        }

        GL11.glPopMatrix();
    }
}
