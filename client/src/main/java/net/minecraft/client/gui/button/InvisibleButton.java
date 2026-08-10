package net.minecraft.client.gui.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.localization.Font;
import net.minecraft.localization.Translator;
import org.lwjgl.opengl.GL11;

public class InvisibleButton extends Button {
    public InvisibleButton(int id, int x, int y, int width, int height, String name) {
        super(id, x, y, width, height, name);
    }

    public InvisibleButton(int id, int x, int y, int width, int height, String name, String desc) {
        super(id, x, y, width, height, name, desc);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            boolean hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            this.drawCenteredStringNoShadow(mc.fontRenderer, this.label, this.x + this.width / 2, this.y + (this.height - 8) / 2, 4210752);
            if (hovered && !this.description.isEmpty()) {
                this.drawTooltip(mouseX, mouseY, mc.fontRenderer);
            }
        }
    }

    @Override
    protected void drawBg(Minecraft mc, byte state, boolean hovered) {
    }

    @Override
    protected void drawTooltip(int mouseX, int mouseY, Font font) {
        GL11.glPushMatrix();
        int newX = mouseX + 12;
        int newY = mouseY - 12;
        int strWidth = font.getStringWidth(Translator.getInstance().translateKey(this.description));
        GL11.glTranslatef(0.0F, 0.0F, 50.0F);
        this.drawGradient(newX - 3, newY - 3, newX + strWidth + 3, newY + 11, 1610941696, -1607454624, 1.5F);
        this.drawString(font, Translator.getInstance().translateKey(this.description), newX, newY, -1);
        GL11.glPopMatrix();
    }
}
