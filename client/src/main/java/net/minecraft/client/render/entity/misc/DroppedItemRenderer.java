package net.minecraft.client.render.entity.misc;

import java.util.Random;
import net.minecraft.client.options.OptionValues;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.blocks.BlocksRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.block.BlockItem;
import net.minecraft.game.texture.Icon;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class DroppedItemRenderer extends EntityRenderer<DroppedItem> {
    private BlocksRenderer blockRenderer = new BlocksRenderer();
    private ItemRenderer itemRenderer = new ItemRenderer();
    private Random random = new Random();

    public DroppedItemRenderer() {
        this.setShadowSize(0.15F);
        this.shadowOpaque = 0.75F;
    }

    public void render(DroppedItem item, double x, double y, double z, float pitch, float yaw) {
        this.random.setSeed(187L);
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        ItemStack stack = item.item;
        float bobSpeed = MathHelper.sin((item.age + yaw) / 10.0F + item.hoverStart) * 0.1F + 0.1F;
        if (!OptionValues.fancyItems) {
            bobSpeed = 0.0F;
        }

        yaw = ((item.age + yaw) / 20.0F + item.hoverStart) * (180.0F / (float)Math.PI);
        int stacks = 1 + item.item.stackSize / 16;
        if (item.item.stackSize > 1 && item.item.stackSize < 16) {
            stacks = 2;
        }

        GL11.glTranslated(x, y + bobSpeed, z);
        GL11.glEnable(2977);
        if (stack.getItem() instanceof BlockItem && this.blockRenderer.isModel3D(BlockList.blocks[stack.itemID].getRenderType())) {
            if (OptionValues.fancyItems) {
                GL11.glRotatef(yaw, 0.0F, 1.0F, 0.0F);
            }

            this.loadTexture("/terrain.png");
            x = 0.25;
            if (!BlockList.blocks[stack.itemID].fullBlock()) {
                x = 0.5;
            }

            GL11.glScaled(x, x, x);

            for (int theStack = 0; theStack < stacks; theStack++) {
                GL11.glPushMatrix();
                if (theStack > 0) {
                    z = (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F / x;
                    pitch = (float)((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F / x);
                    yaw = (float)((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F / x);
                    GL11.glTranslated(z, pitch, yaw);
                }

                if (!BlockList.blocks[stack.itemID].fullBlock()) {
                    GL11.glScalef(0.5F, 0.5F, 0.5F);
                }

                this.blockRenderer.renderBlockGUI(BlockList.blocks[stack.itemID], stack.itemDmg);
                GL11.glPopMatrix();
            }
        } else {
            Tessellator tess = Tessellator.instance;
            Icon icon = stack.getItem().getIconFromMeta(stack.getItemDamage());
            if (stack.getItem() instanceof BlockItem) {
                this.loadTexture("/terrain.png");
            } else {
                this.loadTexture("/gui/items.png");
            }

            if (OptionValues.fancyItems) {
                this.itemRenderer.renderStacks(stack, yaw, stacks);
            } else {
                GL11.glScalef(0.5F, 0.5F, 0.5F);
                float minU = icon.getMinU();
                float maxU = icon.getMaxU();
                float minV = icon.getMinV();
                float maxV = icon.getMaxV();
                float start = 1.0F;
                float offset = 0.5F;
                float end = 0.25F;

                for (int theStack = 0; theStack < stacks; theStack++) {
                    GL11.glPushMatrix();
                    GL11.glDisable(2896);
                    if (theStack > 0) {
                        float offsetX = (this.random.nextFloat() * 2.0F - 1.0F) * 0.3F;
                        float offsetY = (this.random.nextFloat() * 2.0F - 1.0F) * 0.3F;
                        float offsetZ = (this.random.nextFloat() * 2.0F - 1.0F) * 0.3F;
                        GL11.glTranslatef(offsetX, offsetY, offsetZ);
                    }

                    GL11.glRotatef(180.0F - this.renderManager.yaw, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(-this.renderManager.pitch, 1.0F, 0.0F, 0.0F);
                    tess.begin();
                    tess.setColor(stack.getItem().getColorTint(stack));
                    tess.setNormals(0.0F, 1.0F, 0.0F);
                    tess.addTexturedVertex(0.0F - offset, 0.0F - end, 0.0, minU, maxV);
                    tess.addTexturedVertex(start - offset, 0.0F - end, 0.0, maxU, maxV);
                    tess.addTexturedVertex(start - offset, 1.0F - end, 0.0, maxU, minV);
                    tess.addTexturedVertex(0.0F - offset, 1.0F - end, 0.0, minU, minV);
                    tess.draw();
                    GL11.glEnable(2896);
                    GL11.glPopMatrix();
                }
            }
        }

        GL11.glDisable(2977);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }
}
