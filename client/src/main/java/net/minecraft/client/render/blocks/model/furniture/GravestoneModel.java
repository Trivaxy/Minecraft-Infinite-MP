package net.minecraft.client.render.blocks.model.furniture;

import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.blocks.model.BlockModel;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import org.lwjgl.opengl.GL11;

public class GravestoneModel extends BlockModel {
    @Override
    public boolean render(Block block, int x, int y, int z) {
        int meta = this.world.getBlockMetadata(x, y, z);
        block.setBounds(0.125, 0.0, 0.125, 0.875, 0.05F, 0.875);
        boolean render = this.renderStandardBlock(block, x, y, z);
        if (this.textureOverride == null) {
            if (block == BlockList.playerGrave) {
                this.setOverrideTexture(BlockList.stone.getIcon(0, 0));
            } else {
                this.setOverrideTexture(BlockList.stone.getIcon(0, 2));
            }
        }

        switch (meta) {
            case 2:
                block.setBounds(0.125, 0.0, 0.875, 0.875, 0.875, 1.0);
                break;
            case 3:
                block.setBounds(0.125, 0.0, 0.0, 0.875, 0.875, 0.125);
                break;
            case 4:
                block.setBounds(0.875, 0.0, 0.125, 1.0, 0.875, 0.875);
                break;
            case 5:
                block.setBounds(0.0, 0.0, 0.125, 0.125, 0.875, 0.875);
        }

        this.renderStandardBlock(block, x, y, z);
        block.setBounds(0.0, 0.0, 0.0, 1.0, 0.675F, 1.0);
        this.clearOverrideTexture();
        return render;
    }

    @Override
    public void renderInGUI(Block block, int dat) {
        Tessellator tess = Tessellator.instance;

        for (int part = 0; part < 2; part++) {
            if (part == 0) {
                block.setBounds(0.125, 0.0, 0.125, 0.875, 0.125, 0.875);
            }

            if (part == 1) {
                if (!this.hasOverrideTexture()) {
                    this.setOverrideTexture(BlockList.stone.getIcon(0, 2));
                }

                block.setBounds(0.125, 0.0, 0.0, 0.875, 0.875, 0.125);
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
