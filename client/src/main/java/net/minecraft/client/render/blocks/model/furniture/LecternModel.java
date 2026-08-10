package net.minecraft.client.render.blocks.model.furniture;

import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.blocks.model.BlockModel;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.texture.Icon;
import org.lwjgl.opengl.GL11;

public class LecternModel extends BlockModel {
    @Override
    public boolean render(Block block, int x, int y, int z) {
        Tessellator tessellator = Tessellator.instance;
        float brightness = block.getBrightness(this.world, x, y, z);
        tessellator.setColor(brightness, brightness, brightness);
        block.setBounds(0.0, 0.75, 0.0, 1.0, 1.0, 1.0);
        boolean render = this.renderStandardBlock(block, x, y, z);
        if (this.textureOverride == null) {
            this.setOverrideTexture(BlockList.planks.getIcon(0, 0));
        }

        block.setBounds(0.35F, 0.0, 0.35F, 0.65F, 0.75, 0.65F);
        render = this.renderStandardBlock(block, x, y, z);
        block.setBounds(0.1F, 0.0, 0.1F, 0.9F, 0.125, 0.9F);
        render = this.renderStandardBlock(block, x, y, z);
        if (!this.hasOverrideTexture() || this.textureOverride == BlockList.planks.getIcon(0, 0)) {
            this.clearOverrideTexture();
        }

        block.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        return render;
    }

    @Override
    public void renderInGUI(Block block, int dat) {
        Tessellator tess = Tessellator.instance;
        Icon legs = BlockList.planks.getIcon(0, 0);

        for (int part = 0; part < 3; part++) {
            if (part == 0) {
                block.setBounds(0.0, 0.75, 0.0, 1.0, 1.0, 1.0);
            } else {
                if (!this.hasOverrideTexture()) {
                    this.setOverrideTexture(legs);
                }

                if (part == 1) {
                    block.setBounds(0.35F, 0.0, 0.35F, 0.65F, 0.75, 0.65F);
                }

                if (part == 2) {
                    block.setBounds(0.1F, 0.0, 0.1F, 0.9F, 0.125, 0.9F);
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
