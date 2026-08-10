package net.minecraft.client.render.blocks.model.furniture;

import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.blocks.model.BlockModel;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.utility.Pot;
import org.lwjgl.opengl.GL11;

public class PotModel extends BlockModel {
    @Override
    public boolean render(Block block, int x, int y, int z) {
        if (this.textureOverride == null) {
            this.setOverrideTexture(Pot.top);
        }

        block.setBounds(0.2625, 0.1, 0.2625, 0.7375, 0.25, 0.7375);
        boolean render = this.renderStandardBlock(block, x, y + 1, z);
        block.setBounds(0.3625, 0.0, 0.3625, 0.6375, 0.1, 0.6375);
        render = this.renderStandardBlock(block, x, y + 1, z);
        if (this.textureOverride == Pot.top) {
            this.clearOverrideTexture();
        }

        block.setBounds(0.0625, 0.0, 0.0625, 0.9375, 1.0, 0.9375);
        return this.renderStandardBlock(block, x, y, z);
    }

    @Override
    public void renderInGUI(Block block, int dat) {
        Tessellator tess = Tessellator.instance;

        for (int parts = 0; parts < 3; parts++) {
            if (parts == 0) {
                block.setBounds(0.0625, 0.0, 0.0625, 0.9375, 0.8, 0.9375);
            }

            if (parts == 1) {
                if (this.textureOverride == null) {
                    this.setOverrideTexture(Pot.top);
                }

                block.setBounds(0.3625, 0.8, 0.3625, 0.6375, 0.875, 0.6375);
            }

            if (parts == 2) {
                if (this.textureOverride == null) {
                    this.setOverrideTexture(Pot.top);
                }

                block.setBounds(0.2625, 0.875, 0.2625, 0.7375, 1.0, 0.7375);
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

        block.setBounds(0.0625, 0.0, 0.0625, 0.9375, 1.0, 0.9375);
        this.clearOverrideTexture();
    }
}
