package net.minecraft.client.gui.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.options.OptionsList;
import net.minecraft.localization.Translator;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class Slider extends Button {
    public float sliderValue = 1.0F;
    public boolean dragging = false;

    public Slider(int id, int x, int y, String label, int width, int height) {
        super(id, x, y, width, height, label);
        this.description = Translator.getInstance().translateKey(label);
    }

    @Override
    public boolean mousePressed(Minecraft minecraft, int mouseX, int mouseY) {
        if (super.mousePressed(minecraft, mouseX, mouseY)) {
            this.dragging = true;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        this.dragging = false;
    }

    @Override
    public final void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
        if (this.visible) {
            GL11.glBindTexture(3553, minecraft.textureLoader.getTexture("/gui/gui.png"));
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            byte graphic = 0;
            int col = 14737632;
            this.drawGraphic(this.x, this.y, 0, 46 + graphic * 20, this.width / 2, this.height);
            this.drawGraphic(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + graphic * 20, this.width / 2, this.height);
            boolean hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            if (hovered) {
                graphic = 2;
                col = 16777120;
            } else {
                graphic = 1;
            }

            this.sliderValue = (Float)OptionsList.options[this.id].getValue();
            this.label = OptionsList.options[this.id].getOptionLabel(this.id);
            if (this.dragging) {
                this.sliderValue = (float)(mouseX - (this.x + 4)) / (this.width - 8);
                this.sliderValue = MathHelper.clamp(this.sliderValue, 0.0F, 1.0F);
                minecraft.options.setOptionValue(OptionsList.options[this.id], 0, this.sliderValue);
                this.label = OptionsList.options[this.id].getOptionLabel(this.id);
            }

            this.drawGraphic(this.x + (int)(this.sliderValue * (this.width - 8)), this.y, 0, 46 + graphic * 20, 4, 20);
            this.drawGraphic(this.x + (int)(this.sliderValue * (this.width - 8)) + 4, this.y, 196, 46 + graphic * 20, 4, 20);
            if (hovered && !this.description.isEmpty()) {
                this.drawTooltip(mouseX, mouseY, minecraft.fontRenderer);
            }

            GL11.glPushMatrix();
            int textWidth = minecraft.fontRenderer.getStringWidth(this.label);
            float scale = 1.0F;
            if (textWidth > this.width - 4) {
                scale = (float)(this.width - 4) / textWidth;
            }

            float centerX = this.x + this.width / 2.0F;
            float centerY = this.y + this.height / 2.0F;
            GL11.glTranslatef(centerX, centerY, 0.0F);
            GL11.glScalef(scale, scale, 1.0F);
            this.drawCenteredString(minecraft.fontRenderer, this.label, 0, -4, col);
            GL11.glPopMatrix();
        }
    }
}
