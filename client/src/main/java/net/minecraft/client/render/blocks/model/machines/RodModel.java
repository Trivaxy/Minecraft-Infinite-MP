package net.minecraft.client.render.blocks.model.machines;

import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.blocks.model.BlockModel;
import net.minecraft.game.block.Block;
import org.lwjgl.opengl.GL11;

public class RodModel extends BlockModel {
    @Override
    public boolean render(Block block, int x, int y, int z) {
        int meta = this.world.getBlockMetadata(x, y, z);
        boolean rendered = this.renderRod(block, x, y, z, meta);
        float baseMin = 0.325F;
        float baseMax = 1.0F - baseMin;
        float baseDepth = 0.1F;
        switch (meta) {
            case 0:
                block.setBounds(baseMin, 1.0F - baseDepth, baseMin, baseMax, 1.0, baseMax);
                break;
            case 1:
                block.setBounds(baseMin, 0.0, baseMin, baseMax, baseDepth, baseMax);
                break;
            case 2:
                block.setBounds(baseMin, baseMin, 1.0F - baseDepth, baseMax, baseMax, 1.0);
                break;
            case 3:
                block.setBounds(baseMin, baseMin, 0.0, baseMax, baseMax, baseDepth);
                break;
            case 4:
                block.setBounds(1.0F - baseDepth, baseMin, baseMin, 1.0, baseMax, baseMax);
                break;
            case 5:
                block.setBounds(0.0, baseMin, baseMin, baseDepth, baseMax, baseMax);
        }

        return this.renderStandardBlock(block, x, y, z);
    }

    public boolean renderRod(Block block, int x, int y, int z, int meta) {
        float min = 0.41666666F;
        float max = 0.5833333F;
        if (meta == 2 || meta == 3) {
            block.setBounds(min, min, 0.0, max, max, 1.0);
        } else if (meta != 4 && meta != 5) {
            block.setBounds(min, 0.0, min, max, 1.0, max);
        } else {
            block.setBounds(0.0, min, min, 1.0, max, max);
        }

        return this.renderStandardBlock(block, x, y, z);
    }

    @Override
    public void renderInGUI(Block block, int dat) {
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        Tessellator tess = Tessellator.instance;

        for (int part = 0; part < 2; part++) {
            if (part == 0) {
                block.setBounds(0.325F, 0.0, 0.325F, 0.675F, 0.1F, 0.675F);
            }

            if (part == 1) {
                block.setBounds(0.41666666F, 0.0, 0.41666666F, 0.5833333F, 1.0, 0.5833333F);
            }

            tess.begin();
            this.renderFace(block, 0.0, 0.0, 0.0, block.getSideIcon(0), BlockModel.Face.BOTTOM, false);
            this.renderFace(block, 0.0, 0.0, 0.0, block.getSideIcon(1), BlockModel.Face.TOP, false);
            this.renderFace(block, 0.0, 0.0, 0.0, block.getSideIcon(2), BlockModel.Face.NORTH, false);
            this.renderFace(block, 0.0, 0.0, 0.0, block.getSideIcon(3), BlockModel.Face.SOUTH, false);
            this.renderFace(block, 0.0, 0.0, 0.0, block.getSideIcon(4), BlockModel.Face.WEST, false);
            this.renderFace(block, 0.0, 0.0, 0.0, block.getSideIcon(5), BlockModel.Face.EAST, false);
            tess.draw();
        }
    }
}
