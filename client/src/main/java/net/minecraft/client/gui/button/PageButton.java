package net.minecraft.client.gui.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.container.inventory.RecipeScreen;
import org.lwjgl.opengl.GL11;

public class PageButton extends Button {
    private final boolean nextPage;

    public PageButton(int id, int x, int y, boolean alt) {
        super(id, x, y, 23, 13, "");
        this.nextPage = alt;
        this.sndFile = "random.page";
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            boolean hover = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            if (mc.currentScreen instanceof RecipeScreen) {
                GL11.glColor4f(0.6235F, 0.5412F, 0.4627F, 1.0F);
            }

            mc.textureLoader.bindTexture(mc.textureLoader.getTexture("/gui/book.png"));
            int width = 0;
            int height = 192;
            if (hover) {
                width += 23;
            }

            if (!this.nextPage) {
                height += 13;
            }

            this.drawGraphic(this.x, this.y, width, height, 23, 13);
        }
    }
}
