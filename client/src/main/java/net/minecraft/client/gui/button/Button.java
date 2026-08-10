package net.minecraft.client.gui.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.localization.Font;
import net.minecraft.localization.Translator;
import org.lwjgl.opengl.GL11;

public class Button extends GuiComponent {
    public int width;
    public int height;
    public int x;
    public int y;
    public int id;
    public int icnWidth = -1;
    public int icnHeight = -1;
    public boolean enabled;
    public boolean visible;
    public String label;
    public String description = "";
    protected String sndFile = "random.click";

    public Button(int id, int x, int y, int width, int height, String name) {
        this.enabled = true;
        this.visible = true;
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.label = Translator.getInstance().translateKey(name);
    }

    public Button(int id, int x, int y, int width, int height, String name, String desc) {
        this(id, x, y, width, height, name);
        this.description = Translator.getInstance().translateKey(desc);
    }

    public Button(int id, int x, int y, int width, int height, String name, String desc, int icnX, int icnY) {
        this(id, x, y, width, height, name, desc);
        this.icnWidth = icnX;
        this.icnHeight = icnY;
    }

    public Button(int id, int x, int y, int width, int height, String name, int icnX, int icnY) {
        this(id, x, y, width, height, name);
        this.icnWidth = icnX;
        this.icnHeight = icnY;
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            Font font = mc.fontRenderer;
            GL11.glBindTexture(3553, mc.textureLoader.getTexture("/gui/gui.png"));
            GL11.glColor3f(1.0F, 1.0F, 1.0F);
            byte state = 1;
            int col = 14737632;
            boolean hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            if (!this.enabled) {
                state = 0;
                col = -6250336;
            } else if (hovered) {
                state = 2;
                col = 16777120;
            }

            this.drawBg(mc, state, hovered);
            if (hovered && !this.description.isEmpty()) {
                this.drawTooltip(mouseX, mouseY, font);
            }

            GL11.glPushMatrix();
            int textWidth = font.getStringWidth(this.label);
            float scale = 1.0F;
            if (textWidth > this.width - 4) {
                scale = (float)(this.width - 4) / textWidth;
            }

            float centerX = this.x + this.width / 2.0F;
            float centerY = this.y + this.height / 2.0F;
            GL11.glTranslatef(centerX, centerY, 0.0F);
            GL11.glScalef(scale, scale, 1.0F);
            this.drawCenteredString(font, this.label, 0, -4, col);
            GL11.glPopMatrix();
        }
    }

    protected void drawBg(Minecraft mc, byte state, boolean hovered) {
        this.drawGraphic(this.x, this.y, 0, 46 + state * 20, this.width / 2, this.height);
        this.drawGraphic(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + state * 20, this.width / 2, this.height);
        if (this.icnWidth >= 0 && this.icnHeight >= 0) {
            if (state == 0) {
                GL11.glColor3f(0.4F, 0.4F, 0.4F);
            } else {
                GL11.glColor3f(1.0F, 1.0F, 1.0F);
            }

            this.drawGraphic(this.x + 2, this.y + 2, this.icnWidth, hovered && state > 0 ? this.icnHeight + 16 : this.icnHeight, 16, 16);
        }
    }

    protected void drawTooltip(int mouseX, int mouseY, Font font) {
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, 0.0F, 250.0F);
        int offset = 50;
        if (font.getStringWidth(this.description) <= 75) {
            offset = 10;
        }

        this.drawSplitStringBg(font, this.description, mouseX - offset, mouseY + 15, 150, 16777215, false, true, 1);
        GL11.glPopMatrix();
    }

    public boolean mousePressed(Minecraft minecraft, int mouseX, int mouseY) {
        boolean pressed = this.enabled && mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        if (pressed) {
            minecraft.sndManager.playSoundFX(this.sndFile, 1.0F, 0.25F);
        }

        return pressed;
    }

    public void mouseReleased(int mouseX, int mouseY) {
    }
}
