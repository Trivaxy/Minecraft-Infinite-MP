package net.minecraft.client.render.blocks.model.furniture;

import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.blocks.model.BlockModel;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.utility.Stonecutter;
import org.lwjgl.opengl.GL11;

public class SawmillModel extends BlockModel {
    @Override
    public boolean render(Block block, int x, int y, int z) {
        if (this.textureOverride == null) {
            this.setOverrideTexture(Stonecutter.saw);
        }

        if (this.world.getBlockMetadata(x, y, z) != 4 && this.world.getBlockMetadata(x, y, z) != 5) {
            block.setBounds(0.0, 0.5, 0.5, 1.0, 1.0, 0.5);
        } else {
            block.setBounds(0.5, 0.5, 0.0, 0.5, 1.0, 1.0);
        }

        this.renderStandardBlock(block, x, y, z);
        if (this.textureOverride == Stonecutter.saw) {
            this.clearOverrideTexture();
        }

        block.setBounds(0.0, 0.0, 0.0, 1.0, 0.5, 1.0);
        return this.renderStandardBlock(block, x, y, z);
    }

    @Override
    public void renderInGUI(Block block, int dat) {
        Tessellator tess = Tessellator.instance;

        for (int parts = 0; parts < 2; parts++) {
            if (parts == 0) {
                block.setBounds(0.0, 0.0, 0.0, 1.0, 0.5, 1.0);
            }

            if (parts == 1) {
                if (!this.hasOverrideTexture()) {
                    this.setOverrideTexture(Stonecutter.saw);
                }

                block.setBounds(0.0, 0.5, 0.5, 1.0, 1.0, 0.5);
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

        block.setBounds(0.0, 0.0, 0.0, 1.0, 0.5, 1.0);
        this.clearOverrideTexture();
    }
}
