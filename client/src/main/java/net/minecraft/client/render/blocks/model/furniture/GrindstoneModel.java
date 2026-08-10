package net.minecraft.client.render.blocks.model.furniture;

import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.blocks.model.BlockModel;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.texture.Icon;
import org.lwjgl.opengl.GL11;

public class GrindstoneModel extends BlockModel {
    @Override
    public boolean render(Block block, int x, int y, int z) {
        if (this.textureOverride == null) {
            this.setOverrideTexture(BlockList.wood.getIcon(2, 0));
        }

        if (this.world.getBlockMetadata(x, y, z) != 4 && this.world.getBlockMetadata(x, y, z) != 5) {
            block.setBounds(0.15F, 0.0, 0.35F, 0.25, 0.6F, 0.65F);
            this.renderStandardBlock(block, x, y, z);
            block.setBounds(0.75, 0.0, 0.35F, 0.85F, 0.6F, 0.65F);
            this.renderStandardBlock(block, x, y, z);
        } else {
            block.setBounds(0.35F, 0.0, 0.15F, 0.65F, 0.6F, 0.25);
            this.renderStandardBlock(block, x, y, z);
            block.setBounds(0.35F, 0.0, 0.75, 0.65F, 0.6F, 0.85F);
            this.renderStandardBlock(block, x, y, z);
        }

        if (this.textureOverride == BlockList.wood.getIcon(2, 0)) {
            this.clearOverrideTexture();
        }

        if (this.world.getBlockMetadata(x, y, z) != 4 && this.world.getBlockMetadata(x, y, z) != 5) {
            block.setBounds(0.25, 0.15F, 0.15F, 0.75, 0.85F, 0.85F);
        } else {
            block.setBounds(0.15F, 0.15F, 0.25, 0.85F, 0.85F, 0.75);
        }

        return this.renderStandardBlock(block, x, y, z);
    }

    @Override
    public void renderInGUI(Block block, int dat) {
        Tessellator tess = Tessellator.instance;
        Icon legs = BlockList.wood.getIcon(2, 0);

        for (int part = 0; part < 3; part++) {
            if (part == 0) {
                block.setBounds(0.15F, 0.15F, 0.25, 0.85F, 0.85F, 0.75);
            } else {
                if (!this.hasOverrideTexture()) {
                    this.setOverrideTexture(legs);
                }

                if (part == 1) {
                    block.setBounds(0.35F, 0.0, 0.15F, 0.65F, 0.6F, 0.25);
                }

                if (part == 2) {
                    block.setBounds(0.35F, 0.0, 0.75, 0.65F, 0.6F, 0.85F);
                }
            }

            GL11.glTranslatef(-0.5F, -0.6F, -0.5F);
            tess.begin();
            this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(0, dat), BlockModel.Face.BOTTOM, false);
            this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(1, dat), BlockModel.Face.TOP, false);
            this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(2, dat), BlockModel.Face.NORTH, false);
            this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(3, dat), BlockModel.Face.SOUTH, false);
            this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(4, dat), BlockModel.Face.WEST, false);
            this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(5, dat), BlockModel.Face.EAST, false);
            tess.draw();
            GL11.glTranslatef(0.5F, 0.6F, 0.5F);
        }

        block.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        this.clearOverrideTexture();
    }
}
