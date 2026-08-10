package net.minecraft.client.render.blocks.model.furniture;

import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.blocks.model.BlockModel;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.decoration.FenceGate;
import org.lwjgl.opengl.GL11;

public class GateModel extends BlockModel {
    @Override
    public boolean render(Block block, int x, int y, int z) {
        boolean render = false;
        int metadata = this.world.getBlockMetadata(x, y, z);
        boolean open = FenceGate.isOpen(metadata);
        int facing = FenceGate.getOpenRotation(metadata);
        if (facing != 3 && facing != 1) {
            float startX = 0.0F;
            float endX = 0.125F;
            float startZ = 0.4375F;
            float endZ = 0.5625F;
            block.setBounds(startX, 0.3125, startZ, endX, 1.0, endZ);
            render = this.renderStandardBlock(block, x, y, z);
            startX = 0.875F;
            endX = 1.0F;
            block.setBounds(startX, 0.3125, startZ, endX, 1.0, endZ);
            render = this.renderStandardBlock(block, x, y, z);
        } else {
            float startX = 0.4375F;
            float endX = 0.5625F;
            float startZ = 0.0F;
            float endZ = 0.125F;
            block.setBounds(startX, 0.3125, startZ, endX, 1.0, endZ);
            render = this.renderStandardBlock(block, x, y, z);
            startZ = 0.875F;
            endZ = 1.0F;
            block.setBounds(startX, 0.3125, startZ, endX, 1.0, endZ);
            render = this.renderStandardBlock(block, x, y, z);
        }

        if (!open) {
            if (facing != 3 && facing != 1) {
                float var49 = 0.375F;
                float var56 = 0.5F;
                float var66 = 0.4375F;
                float var73 = 0.5625F;
                block.setBounds(var49, 0.375, var66, var56, 0.9375, var73);
                render = this.renderStandardBlock(block, x, y, z);
                var49 = 0.5F;
                var56 = 0.625F;
                block.setBounds(var49, 0.375, var66, var56, 0.9375, var73);
                render = this.renderStandardBlock(block, x, y, z);
                var49 = 0.625F;
                var56 = 0.875F;
                block.setBounds(var49, 0.375, var66, var56, 0.5625, var73);
                render = this.renderStandardBlock(block, x, y, z);
                block.setBounds(var49, 0.75, var66, var56, 0.9375, var73);
                render = this.renderStandardBlock(block, x, y, z);
                var49 = 0.125F;
                var56 = 0.375F;
                block.setBounds(var49, 0.375, var66, var56, 0.5625, var73);
                render = this.renderStandardBlock(block, x, y, z);
                block.setBounds(var49, 0.75, var66, var56, 0.9375, var73);
                render = this.renderStandardBlock(block, x, y, z);
            } else {
                float var48 = 0.4375F;
                float var55 = 0.5625F;
                float var62 = 0.375F;
                float var69 = 0.5F;
                block.setBounds(var48, 0.375, var62, var55, 0.9375, var69);
                render = this.renderStandardBlock(block, x, y, z);
                var62 = 0.5F;
                var69 = 0.625F;
                block.setBounds(var48, 0.375, var62, var55, 0.9375, var69);
                render = this.renderStandardBlock(block, x, y, z);
                var62 = 0.625F;
                var69 = 0.875F;
                block.setBounds(var48, 0.375, var62, var55, 0.5625, var69);
                render = this.renderStandardBlock(block, x, y, z);
                block.setBounds(var48, 0.75, var62, var55, 0.9375, var69);
                render = this.renderStandardBlock(block, x, y, z);
                var62 = 0.125F;
                var69 = 0.375F;
                block.setBounds(var48, 0.375, var62, var55, 0.5625, var69);
                render = this.renderStandardBlock(block, x, y, z);
                block.setBounds(var48, 0.75, var62, var55, 0.9375, var69);
                render = this.renderStandardBlock(block, x, y, z);
            }
        } else if (facing == 3) {
            block.setBounds(0.8125, 0.375, 0.0, 0.9375, 0.9375, 0.125);
            render = this.renderStandardBlock(block, x, y, z);
            block.setBounds(0.8125, 0.375, 0.875, 0.9375, 0.9375, 1.0);
            render = this.renderStandardBlock(block, x, y, z);
            block.setBounds(0.5625, 0.375, 0.0, 0.8125, 0.5625, 0.125);
            render = this.renderStandardBlock(block, x, y, z);
            block.setBounds(0.5625, 0.375, 0.875, 0.8125, 0.5625, 1.0);
            render = this.renderStandardBlock(block, x, y, z);
            block.setBounds(0.5625, 0.75, 0.0, 0.8125, 0.9375, 0.125);
            render = this.renderStandardBlock(block, x, y, z);
            block.setBounds(0.5625, 0.75, 0.875, 0.8125, 0.9375, 1.0);
            render = this.renderStandardBlock(block, x, y, z);
        } else if (facing == 1) {
            block.setBounds(0.0625, 0.375, 0.0, 0.1875, 0.9375, 0.125);
            render = this.renderStandardBlock(block, x, y, z);
            block.setBounds(0.0625, 0.375, 0.875, 0.1875, 0.9375, 1.0);
            render = this.renderStandardBlock(block, x, y, z);
            block.setBounds(0.1875, 0.375, 0.0, 0.4375, 0.5625, 0.125);
            render = this.renderStandardBlock(block, x, y, z);
            block.setBounds(0.1875, 0.375, 0.875, 0.4375, 0.5625, 1.0);
            render = this.renderStandardBlock(block, x, y, z);
            block.setBounds(0.1875, 0.75, 0.0, 0.4375, 0.9375, 0.125);
            render = this.renderStandardBlock(block, x, y, z);
            block.setBounds(0.1875, 0.75, 0.875, 0.4375, 0.9375, 1.0);
            render = this.renderStandardBlock(block, x, y, z);
        } else if (facing == 0) {
            block.setBounds(0.0, 0.375, 0.8125, 0.125, 0.9375, 0.9375);
            render = this.renderStandardBlock(block, x, y, z);
            block.setBounds(0.875, 0.375, 0.8125, 1.0, 0.9375, 0.9375);
            render = this.renderStandardBlock(block, x, y, z);
            block.setBounds(0.0, 0.375, 0.5625, 0.125, 0.5625, 0.8125);
            render = this.renderStandardBlock(block, x, y, z);
            block.setBounds(0.875, 0.375, 0.5625, 1.0, 0.5625, 0.8125);
            render = this.renderStandardBlock(block, x, y, z);
            block.setBounds(0.0, 0.75, 0.5625, 0.125, 0.9375, 0.8125);
            render = this.renderStandardBlock(block, x, y, z);
            block.setBounds(0.875, 0.75, 0.5625, 1.0, 0.9375, 0.8125);
            render = this.renderStandardBlock(block, x, y, z);
        } else if (facing == 2) {
            block.setBounds(0.0, 0.375, 0.0625, 0.125, 0.9375, 0.1875);
            render = this.renderStandardBlock(block, x, y, z);
            block.setBounds(0.875, 0.375, 0.0625, 1.0, 0.9375, 0.1875);
            render = this.renderStandardBlock(block, x, y, z);
            block.setBounds(0.0, 0.375, 0.1875, 0.125, 0.5625, 0.4375);
            render = this.renderStandardBlock(block, x, y, z);
            block.setBounds(0.875, 0.375, 0.1875, 1.0, 0.5625, 0.4375);
            render = this.renderStandardBlock(block, x, y, z);
            block.setBounds(0.0, 0.75, 0.1875, 0.125, 0.9375, 0.4375);
            render = this.renderStandardBlock(block, x, y, z);
            block.setBounds(0.875, 0.75, 0.1875, 1.0, 0.9375, 0.4375);
            render = this.renderStandardBlock(block, x, y, z);
        }

        block.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        return render;
    }

    @Override
    public void renderInGUI(Block block, int dat) {
        Tessellator tess = Tessellator.instance;

        for (int part = 0; part < 3; part++) {
            float pixelOffset = 0.0625F;
            if (part == 0) {
                block.setBounds(0.5F - pixelOffset, 0.3F, 0.0, 0.5F + pixelOffset, 1.0, pixelOffset * 2.0F);
            }

            if (part == 1) {
                block.setBounds(0.5F - pixelOffset, 0.3F, 1.0F - pixelOffset * 2.0F, 0.5F + pixelOffset, 1.0, 1.0);
            }

            if (part == 2) {
                block.setBounds(0.5F - pixelOffset, 0.5, 0.125, 0.5F + pixelOffset, 0.987F - pixelOffset, 0.875);
            }

            GL11.glTranslatef(-0.5F, -0.7F, -0.5F);
            tess.begin();
            this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(0, dat), BlockModel.Face.BOTTOM, false);
            this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(1, dat), BlockModel.Face.TOP, false);
            this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(2, dat), BlockModel.Face.NORTH, false);
            this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(3, dat), BlockModel.Face.SOUTH, false);
            this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(4, dat), BlockModel.Face.WEST, false);
            this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(5, dat), BlockModel.Face.EAST, false);
            tess.draw();
            GL11.glTranslatef(0.5F, 0.7F, 0.5F);
        }

        block.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
    }
}
