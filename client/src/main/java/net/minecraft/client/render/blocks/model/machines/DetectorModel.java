package net.minecraft.client.render.blocks.model.machines;

import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.blocks.model.BlockModel;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.machines.Detector;
import net.minecraft.game.texture.Icon;
import org.lwjgl.opengl.GL11;

public class DetectorModel extends BlockModel {
    @Override
    public boolean render(Block block, int x, int y, int z) {
        Detector detector = (Detector)block;
        if (!this.hasOverrideTexture()) {
            if (this.world.getBlockMetadata(x, y, z) == 4) {
                this.setOverrideTexture(detector.onState);
            } else {
                this.setOverrideTexture(detector.offState);
            }
        }

        detector.setBounds(0.2F, 0.2F, 0.2F, 0.8F, 0.8F, 0.8F);
        this.renderStandardBlock(detector, x, y, z);
        detector.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        if (this.hasOverrideTexture() && (this.textureOverride == detector.onState || this.textureOverride == detector.offState)) {
            this.clearOverrideTexture();
        }

        return this.renderStandardBlock(detector, x, y, z);
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
                block.setBounds(0.2F, 0.2F, 0.2F, 0.8F, 0.8F, 0.8F);
                icon = ((Detector)block).offState;
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
