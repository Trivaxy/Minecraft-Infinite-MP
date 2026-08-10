package net.minecraft.client.render.entity.misc;

import net.minecraft.client.render.blocks.BlocksRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.building.Stairs;
import net.minecraft.game.block.decoration.Chair;
import net.minecraft.game.block.decoration.Fence;
import net.minecraft.game.block.decoration.FenceGate;
import net.minecraft.game.block.utility.Anvil;
import net.minecraft.game.entity.hanging.Frame;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.world.mapdata.MapData;
import org.lwjgl.opengl.GL11;

public class FrameRenderer extends EntityRenderer<Frame> {
    private final int[] offsetX = new int[]{0, -1, 0, 1, 0, 0};
    private final int[] offsetZ = new int[]{-1, 0, 1, 0, 0, 0};
    public static Icon backdrop;
    public static Icon backdropGlowing;
    private BlocksRenderer blockRenderer = new BlocksRenderer();
    private ItemRenderer itemRenderer = new ItemRenderer();

    private void renderFrameBase(Frame theFrame) {
        GL11.glPushMatrix();
        GL11.glBindTexture(3553, this.renderManager.texLoader.getTexture("/terrain.png"));
        GL11.glRotatef(theFrame.yaw + 90.0F, 0.0F, 1.0F, 0.0F);
        if (theFrame.direction == 4) {
            GL11.glRotatef(theFrame.yaw + 90.0F, 0.0F, 0.0F, 1.0F);
            GL11.glTranslatef(1.0F, 0.0F, 0.0F);
        } else if (theFrame.direction == 5) {
            GL11.glRotatef(theFrame.yaw + 180.0F, 0.0F, 0.0F, 1.0F);
            GL11.glTranslatef(1.0F, 0.0F, 0.0F);
        }

        Block block = BlockList.planks;
        float offset = 0.0625F;
        float addition = 0.375F;
        GL11.glPushMatrix();
        block.setBounds(0.0, 0.5F - addition + offset, 0.5F - addition + offset, offset * 0.5F, 0.5F + addition - offset, 0.5F + addition - offset);
        BlocksRenderer.baseModel.setOverrideTexture(theFrame.canGlow ? backdropGlowing : backdrop);
        BlocksRenderer.baseModel.renderInGUI(block, 0);
        BlocksRenderer.baseModel.clearOverrideTexture();
        GL11.glPopMatrix();
        BlocksRenderer.baseModel.setOverrideTexture(BlockList.trapdoorWood.getIcon(0, 0));
        GL11.glPushMatrix();
        block.setBounds(0.0, 0.5F - addition, 0.5F - addition, offset + 1.0E-4F, offset + 0.5F - addition, 0.5F + addition);
        BlocksRenderer.baseModel.renderInGUI(block, 0);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        block.setBounds(0.0, 0.5F + addition - offset, 0.5F - addition, offset + 1.0E-4F, 0.5F + addition, 0.5F + addition);
        BlocksRenderer.baseModel.renderInGUI(block, 0);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        block.setBounds(0.0, 0.5F - addition, 0.5F - addition, offset, 0.5F + addition, offset + 0.5F - addition);
        BlocksRenderer.baseModel.renderInGUI(block, 0);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        block.setBounds(0.0, 0.5F - addition, 0.5F + addition - offset, offset, 0.5F + addition, 0.5F + addition);
        BlocksRenderer.baseModel.renderInGUI(block, 0);
        GL11.glPopMatrix();
        block.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        GL11.glPopMatrix();
        BlocksRenderer.baseModel.clearOverrideTexture();
    }

    private void renderFrameItem(Frame theFrame) {
        ItemStack displayItem = theFrame.getDisplayedItem();
        if (displayItem != null && displayItem.getItem() != null) {
            DroppedItem item = new DroppedItem(theFrame.world, 0.0, 0.0, 0.0, displayItem);
            item.item.stackSize = 1;
            item.hoverStart = 0.0F;
            GL11.glPushMatrix();
            if (theFrame.direction == 4) {
                GL11.glRotatef(theFrame.yaw + 90.0F, 1.0F, 0.0F, 0.0F);
            } else if (theFrame.direction == 5) {
                GL11.glRotatef(theFrame.yaw + 180.0F, 1.0F, 0.0F, 0.0F);
            }

            if (displayItem.getItem() == ItemList.map) {
                this.renderManager.texLoader.bindTexture("/misc/mapbg.png");
                if (theFrame.direction == 4) {
                    GL11.glRotatef(theFrame.yaw + 180.0F, 1.0F, 0.0F, 0.0F);
                    GL11.glTranslatef(0.0F, 0.0F, 0.56F);
                } else if (theFrame.direction == 5) {
                    GL11.glRotatef(theFrame.yaw + 90.0F, 1.0F, 0.0F, 0.0F);
                    GL11.glTranslatef(0.0F, 0.0F, 0.56F);
                } else {
                    GL11.glTranslatef(-0.453125F * this.offsetX[theFrame.direction], 0.0F, -0.453125F * this.offsetZ[theFrame.direction]);
                    GL11.glRotatef(180.0F + theFrame.yaw, 0.0F, 1.0F, 0.0F);
                }

                GL11.glRotatef(-90 * theFrame.getRotation(), 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScalef(0.0078125F, 0.0078125F, 0.0078125F);
                GL11.glTranslatef(-64.0F, -64.0F, -1.5F);
                GL11.glNormal3f(0.0F, 0.0F, -1.0F);
                GL11.glDisable(2896);
                MapData mapData = ItemList.map.getMapData(displayItem, theFrame.world);
                GL11.glTranslatef(0.0F, 0.0F, -1.0F);
                if (mapData != null) {
                    this.renderManager.itemRenderer.mapRenderer.renderMap(null, this.renderManager.texLoader, mapData, false);
                }

                GL11.glEnable(2896);
            } else if (displayItem.getItem().id < 4096 && this.blockRenderer.isModel3D(BlockList.blocks[displayItem.itemID].getRenderType())) {
                Block block = BlockList.blocks[displayItem.itemID];
                boolean rotateBlock = block instanceof Fence
                    || block instanceof FenceGate
                    || block instanceof Chair
                    || block instanceof Stairs
                    || block instanceof Anvil;
                GL11.glTranslatef(-0.453125F * this.offsetX[theFrame.direction], 0.0F, -0.453125F * this.offsetZ[theFrame.direction]);
                if (theFrame.direction == 4) {
                    GL11.glTranslatef(0.0F, 0.0F, -0.525F);
                } else if (theFrame.direction == 5) {
                    GL11.glTranslatef(0.0F, 0.0F, -0.525F);
                    GL11.glRotatef(theFrame.yaw + 180.0F, 0.0F, 1.0F, 0.0F);
                }

                if (rotateBlock) {
                    GL11.glRotatef(90.0F + theFrame.yaw, 0.0F, 1.0F, 0.0F);
                } else {
                    GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
                }

                if (rotateBlock || theFrame.direction == 4) {
                    GL11.glRotatef(45 * theFrame.getRotation(), 1.0F, 0.0F, 0.0F);
                } else if (theFrame.direction != 2 && theFrame.direction != 0) {
                    GL11.glRotatef(45 * theFrame.getRotation(), 0.0F, 0.0F, 1.0F);
                } else {
                    GL11.glRotatef(45 * theFrame.getRotation(), 1.0F, 0.0F, 0.0F);
                }
            } else {
                if (theFrame.yaw != 180.0F && theFrame.yaw != 0.0F) {
                    GL11.glTranslatef(-0.453125F * this.offsetX[theFrame.direction], -0.18F, -0.223125F * this.offsetX[theFrame.direction]);
                } else {
                    GL11.glTranslatef(0.223125F * this.offsetZ[theFrame.direction], -0.18F, -0.453125F * this.offsetZ[theFrame.direction]);
                }

                if (theFrame.direction == 4) {
                    GL11.glTranslatef(-0.225F, -0.025F, -0.525F);
                } else if (theFrame.direction == 5) {
                    GL11.glTranslatef(-0.225F, -0.025F, -0.525F);
                    GL11.glRotatef(theFrame.yaw + 180.0F, 0.0F, 1.0F, 0.0F);
                }

                GL11.glRotatef(theFrame.yaw, 0.0F, 1.0F, 0.0F);
                GL11.glTranslatef(0.22F, 0.22F, 0.0F);
                GL11.glRotatef(45.0F * theFrame.getRotation(), 0.0F, 0.0F, 1.0F);
                GL11.glTranslatef(-0.22F, -0.22F, 0.0F);
            }

            GL11.glScalef(0.3F, 0.3F, 0.3F);
            this.itemRenderer.renderFramedItem(displayItem);
            GL11.glPopMatrix();
        }
    }

    public void render(Frame theFrame, double x, double y, double z, float yaw, float pitch) {
        GL11.glPushMatrix();
        double posX = theFrame.posX - x - 0.5;
        double posY = theFrame.posY - y - 0.5;
        double posZ = theFrame.posZ - z - 0.5;
        int frameX = theFrame.xPosition + this.offsetX[theFrame.direction];
        int frameY = theFrame.yPosition;
        int frameZ = theFrame.zPosition + this.offsetZ[theFrame.direction];
        GL11.glTranslated(frameX - posX, frameY - posY, frameZ - posZ);
        this.renderFrameBase(theFrame);
        this.renderFrameItem(theFrame);
        GL11.glPopMatrix();
    }
}
