package net.minecraft.client.render.blocks.model.plants;

import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.blocks.model.BlockModel;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.building.Log;
import net.minecraft.game.texture.Icon;
import org.lwjgl.opengl.GL11;

public class BranchModel extends BlockModel {
    @Override
    public boolean render(Block block, int x, int y, int z) {
        if (!this.hasOverrideTexture()) {
            this.setOverrideTexture(((Log)BlockList.wood).front[0]);
        }

        if (this.world.getBlockMetadata(x, y, z) == 64) {
            block.setBounds(0.25, 0.25, 0.01F, 0.75, 0.75, 0.99F);
        } else if (this.world.getBlockMetadata(x, y, z) == 32) {
            block.setBounds(0.01F, 0.25, 0.25, 0.99F, 0.75, 0.75);
        } else {
            block.setBounds(0.25, 0.01F, 0.25, 0.75, 0.99F, 0.75);
        }

        this.renderStandardBlock(block, x, y, z);
        block.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        if (!this.hasOverrideTexture() || this.textureOverride == ((Log)BlockList.wood).front[0]) {
            this.clearOverrideTexture();
        }

        this.renderStandardBlock(block, x, y, z);
        return true;
    }

    @Override
    public void renderInGUI(Block block, int dat) {
        Icon icon = block.getIcon(0, dat);
        Tessellator tess = Tessellator.instance;

        for (int part = 0; part < 2; part++) {
            if (part == 0) {
                block.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
            }

            if (part == 1) {
                block.setBounds(0.01F, 0.25, 0.25, 0.99F, 0.75, 0.75);
                icon = ((Log)BlockList.wood).front[0];
            }

            block.forceBounds(dat);
            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
            tess.begin();
            this.renderFace(block, 0.0, 0.0, 0.0, icon, BlockModel.Face.BOTTOM, false);
            this.renderFace(block, 0.0, 0.0, 0.0, icon, BlockModel.Face.TOP, false);
            this.renderFace(block, 0.0, 0.0, 0.0, icon, BlockModel.Face.NORTH, false);
            this.renderFace(block, 0.0, 0.0, 0.0, icon, BlockModel.Face.SOUTH, false);
            this.renderFace(block, 0.0, 0.0, 0.0, icon, BlockModel.Face.WEST, false);
            this.renderFace(block, 0.0, 0.0, 0.0, icon, BlockModel.Face.EAST, false);
            tess.draw();
            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        }

        block.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
    }
}
