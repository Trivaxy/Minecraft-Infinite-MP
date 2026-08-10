package net.minecraft.client.gui.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.localization.Font;
import net.minecraft.client.render.Lighting;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.game.block.Block;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemStack;
import net.minecraft.localization.Translator;
import org.lwjgl.opengl.GL11;

public class CategoryButton extends Button {
    private ItemRenderer itemRenderer = new ItemRenderer();
    private Block blockRender;
    private Item item;
    private int meta;
    public boolean selected;
    private boolean flipped;

    public CategoryButton(int id, int x, int y, String label, Block block, int meta, boolean flip) {
        super(id, x, y, 28, 30, "");
        this.blockRender = block;
        this.meta = meta;
        this.flipped = flip;
        this.description = Translator.getInstance().translateKey(label);
    }

    public CategoryButton(int id, int x, int y, String str, Item item, int meta, boolean flip) {
        super(id, x, y, 28, 30, "");
        this.item = item;
        this.meta = meta;
        this.flipped = flip;
        this.description = Translator.getInstance().translateKey(str);
    }

    @Override
    protected void drawBg(Minecraft mc, byte state, boolean hovered) {
        mc.textureLoader.bindTexture(mc.textureLoader.getTexture("/gui/achievement.png"));
        int imgX = 96;
        int imgY = 167;
        int iconheight = 7;
        if (this.flipped) {
            imgX += 56;
        }

        if (!this.selected) {
            imgX += 28;
            if (this.flipped) {
                iconheight--;
            } else {
                iconheight++;
            }
        }

        this.drawGraphic(this.x, this.y, imgX, imgY, 28, 30);
        GL11.glPushMatrix();
        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        Lighting.enable();
        GL11.glPopMatrix();
        GL11.glDisable(2896);
        GL11.glEnable(32826);
        GL11.glEnable(2903);
        GL11.glEnable(2896);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (this.blockRender != null) {
            this.itemRenderer
                .renderItemSprite(mc.fontRenderer, mc.textureLoader, new ItemStack(this.blockRender, 1, this.meta), this.x + 6, this.y + iconheight);
        } else if (this.item != null) {
            this.itemRenderer.zLevel = 100.0F;
            this.itemRenderer.renderItemSprite(mc.fontRenderer, mc.textureLoader, new ItemStack(this.item, 1, this.meta), this.x + 6, this.y + iconheight);
            this.itemRenderer.zLevel = 0.0F;
        }

        GL11.glDisable(2896);
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
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
