package net.minecraft.client.render.blocks.model.furniture;

import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.blocks.model.BlockModel;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.building.Log;
import org.lwjgl.opengl.GL11;

public class ChairModel extends BlockModel {
    @Override
    public boolean render(Block block, int x, int y, int z) {
        Tessellator tess = Tessellator.instance;
        float brightness = block.getBrightness(this.world, x, y, z);
        int metadata = this.world.getBlockMetadata(x, y, z);
        int rotation = metadata & 3;
        boolean isStool = (metadata & 4) > 0;
        boolean render = false;
        tess.setColor(brightness, brightness, brightness);
        block.setBounds(0.125, 0.375, 0.125, 0.875, 0.5, 0.875);
        render = this.renderStandardBlock(block, x, y, z);
        if (!isStool) {
            float xPoint1 = 0.125F;
            float xPoint2 = 0.875F;
            float zPoint1 = 0.125F;
            float zPoint2 = 0.25F;
            if (rotation == 0) {
                zPoint1 = 0.75F;
                zPoint2 = 0.875F;
            }

            if (rotation == 1) {
                xPoint2 = 0.25F;
                zPoint2 = 0.875F;
            }

            if (rotation == 3) {
                zPoint2 = 0.875F;
                xPoint1 = 0.75F;
                xPoint2 = 0.875F;
            }

            block.setBounds(xPoint1, 0.5, zPoint1, xPoint2, 1.0, zPoint2);
            render = this.renderStandardBlock(block, x, y, z);
        }

        if (!this.hasOverrideTexture()) {
            this.setOverrideTexture(((Log)BlockList.wood).front[metadata >> 3]);
        }

        block.setBounds(0.125, 0.0, 0.125, 0.25, 0.375, 0.25);
        render = this.renderStandardBlock(block, x, y, z);
        block.setBounds(0.75, 0.0, 0.75, 0.875, 0.375, 0.875);
        render = this.renderStandardBlock(block, x, y, z);
        block.setBounds(0.125, 0.0, 0.75, 0.25, 0.375, 0.875);
        render = this.renderStandardBlock(block, x, y, z);
        block.setBounds(0.75, 0.0, 0.125, 0.875, 0.375, 0.25);
        render = this.renderStandardBlock(block, x, y, z);
        this.clearOverrideTexture();
        block.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        return render;
    }

    @Override
    public void renderInGUI(Block block, int dat) {
        Tessellator tess = Tessellator.instance;

        for (int part = 0; part < 6; part++) {
            if (part <= 1) {
                if (part == 0) {
                    block.setBounds(0.125, 0.375, 0.125, 0.875, 0.5, 0.875);
                }

                if (part == 1) {
                    block.setBounds(0.125, 0.5, 0.125, 0.875, 1.0, 0.25);
                }
            } else {
                if (!this.hasOverrideTexture()) {
                    this.setOverrideTexture(((Log)BlockList.wood).front[dat >> 3]);
                }

                if (part == 2) {
                    block.setBounds(0.125, 0.0, 0.125, 0.25, 0.375, 0.25);
                }

                if (part == 3) {
                    block.setBounds(0.75, 0.0, 0.75, 0.875, 0.375, 0.875);
                }

                if (part == 4) {
                    block.setBounds(0.125, 0.0, 0.75, 0.25, 0.375, 0.875);
                }

                if (part == 5) {
                    block.setBounds(0.75, 0.0, 0.125, 0.875, 0.375, 0.25);
                }
            }

            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
            tess.begin();
            this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(0, dat), BlockModel.Face.BOTTOM, false);
            this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(1, dat), BlockModel.Face.TOP, false);
            this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(2, dat), BlockModel.Face.NORTH, false);
            this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(3, dat), BlockModel.Face.SOUTH, false);
            this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(4, dat), BlockModel.Face.WEST, false);
            this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(5, dat), BlockModel.Face.EAST, false);
            tess.draw();
            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        }

        block.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        this.clearOverrideTexture();
    }
}
