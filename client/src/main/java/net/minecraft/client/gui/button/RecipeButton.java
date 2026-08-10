package net.minecraft.client.gui.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.localization.Font;
import net.minecraft.client.render.Lighting;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.game.item.ItemStack;
import net.minecraft.localization.Translator;
import org.lwjgl.opengl.GL11;

public class RecipeButton extends Button {
    protected static ItemRenderer itemRenderer = new ItemRenderer();
    int itemID;

    public RecipeButton(int id, int x, int y, int width, int height, String name, String desc, int item) {
        super(id, x, y, width, height, name, desc);
        this.itemID = item;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            boolean hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            byte state = 1;
            if (!this.enabled) {
                state = 0;
            } else if (hovered) {
                state = 2;
            }

            this.drawBg(mc, state, hovered);
            if (hovered && !this.description.isEmpty()) {
                this.drawTooltip(mouseX, mouseY, mc.fontRenderer);
            }
        }
    }

    @Override
    protected void drawBg(Minecraft mc, byte state, boolean hovered) {
        mc.textureLoader.bindTexture(mc.textureLoader.getTexture("/gui/inventory/inventory.png"));
        if (this.enabled) {
            if (hovered) {
                this.drawGraphic(this.x, this.y, 211, 185, 20, 19);
            } else {
                int texY = 0;
                if (!this.label.isEmpty()) {
                    texY = 38;
                }

                this.drawGraphic(this.x, this.y, 211, 166 + texY, 20, 19);
            }

            GL11.glPushMatrix();
            GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
            Lighting.enable();
            GL11.glPopMatrix();
            GL11.glDisable(2896);
            GL11.glEnable(32826);
            GL11.glEnable(2903);
            GL11.glEnable(2896);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            itemRenderer.renderItemSprite(mc.fontRenderer, mc.textureLoader, new ItemStack(this.itemID), this.x + 2, this.y + 2);
            Lighting.disable();
            GL11.glDisable(2896);
            GL11.glDepthMask(true);
            GL11.glEnable(2929);
        }
    }

    @Override
    protected void drawTooltip(int mouseX, int mouseY, Font font) {
        GL11.glPushMatrix();
        int newX = mouseX + 12;
        int newY = mouseY - 12;
        int strWidth = font.getStringWidth(Translator.getInstance().translateKey(this.description));
        GL11.glTranslatef(0.0F, 0.0F, 500.0F);
        this.drawGradient(newX - 3, newY - 3, newX + strWidth + 3, newY + 11, 1610941696, -1607454624, 1.5F);
        this.drawString(font, Translator.getInstance().translateKey(this.description), newX, newY, -1);
        GL11.glPopMatrix();
    }
}
