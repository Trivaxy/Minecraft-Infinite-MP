package net.minecraft.client.render.blocks.model.furniture;

import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.blocks.model.BlockModel;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.utility.Beacon;
import org.lwjgl.opengl.GL11;

public class BeaconModel extends BlockModel {
    @Override
    public boolean render(Block block, int x, int y, int z) {
        Beacon beacon = (Beacon)block;
        boolean rendered = false;
        if (this.textureOverride == null) {
            this.setOverrideTexture(BlockList.obsidian.blockIcon);
        }

        beacon.setBounds(0.125, 0.0, 0.125, 0.875, 0.1875, 0.875);
        rendered = this.renderStandardBlock(beacon, x, y, z);
        if (this.textureOverride == BlockList.obsidian.blockIcon
            || this.textureOverride == beacon.getIcon(1, 0)
            || this.textureOverride == beacon.getIcon(0, 0)) {
            this.clearOverrideTexture();
        }

        if (this.textureOverride == null) {
            this.setOverrideTexture(beacon.getIcon(0, 0));
        }

        beacon.setBounds(0.1875, 0.1875, 0.1875, 0.8125, 0.875, 0.8125);
        rendered = this.renderStandardBlock(beacon, x, y, z);
        if (this.textureOverride == BlockList.obsidian.blockIcon
            || this.textureOverride == beacon.getIcon(1, 0)
            || this.textureOverride == beacon.getIcon(0, 0)) {
            this.clearOverrideTexture();
        }

        if (this.textureOverride == null) {
            this.setOverrideTexture(beacon.getIcon(1, 0));
        }

        beacon.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        rendered = this.renderStandardBlock(beacon, x, y, z);
        if (this.textureOverride == BlockList.obsidian.blockIcon
            || this.textureOverride == beacon.getIcon(1, 0)
            || this.textureOverride == beacon.getIcon(0, 0)) {
            this.clearOverrideTexture();
        }

        return rendered;
    }

    @Override
    public void renderInGUI(Block block, int dat) {
        Tessellator tess = Tessellator.instance;

        for (int part = 0; part < 3; part++) {
            if (part == 0) {
                block.setBounds(0.125, 0.0, 0.125, 0.875, 0.1875, 0.875);
                this.setOverrideTexture(BlockList.obsidian.blockIcon);
            } else if (part == 1) {
                block.setBounds(0.1875, 0.1875, 0.1875, 0.8125, 0.875, 0.8125);
                this.setOverrideTexture(BlockList.beacon.getIcon(0, 0));
            } else if (part == 2) {
                block.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
                this.setOverrideTexture(BlockList.beacon.getIcon(1, 0));
            }

            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
            tess.begin();
            this.renderFace(block, 0.0, 0.0, 0.0, block.getSideIcon(0), BlockModel.Face.BOTTOM, false);
            this.renderFace(block, 0.0, 0.0, 0.0, block.getSideIcon(1), BlockModel.Face.TOP, false);
            this.renderFace(block, 0.0, 0.0, 0.0, block.getSideIcon(2), BlockModel.Face.NORTH, false);
            this.renderFace(block, 0.0, 0.0, 0.0, block.getSideIcon(3), BlockModel.Face.SOUTH, false);
            this.renderFace(block, 0.0, 0.0, 0.0, block.getSideIcon(4), BlockModel.Face.WEST, false);
            this.renderFace(block, 0.0, 0.0, 0.0, block.getSideIcon(5), BlockModel.Face.EAST, false);
            tess.draw();
            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        }

        block.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        this.clearOverrideTexture();
    }
}
