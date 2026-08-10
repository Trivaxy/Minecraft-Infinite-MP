package net.minecraft.client.render.blocks.model.furniture;

import net.minecraft.client.render.blocks.model.BlockModel;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.decoration.WindowPane;
import net.minecraft.game.world.World;
import net.minecraft.game.world.dimension.HellDimension;

public class PaneModel extends BlockModel {
    @Override
    public boolean render(Block block, int x, int y, int z) {
        WindowPane pane = (WindowPane)block;
        float minX = 0.4375F;
        float maxX = 0.5625F;
        float minZ = 0.4375F;
        float maxZ = 0.5625F;
        boolean northConnection = pane.canConnectTo(this.world.getBlockId(x, y, z - 1));
        boolean southConnection = pane.canConnectTo(this.world.getBlockId(x, y, z + 1));
        boolean westConnection = pane.canConnectTo(this.world.getBlockId(x - 1, y, z));
        boolean eastConnection = pane.canConnectTo(this.world.getBlockId(x + 1, y, z));
        if (!this.hasOverrideTexture() && block == BlockList.ironBars && ((World)this.world).currDim instanceof HellDimension) {
            this.setOverrideTexture(WindowPane.altIcon);
        }

        if (westConnection && eastConnection && northConnection && southConnection) {
            this.rotationBottom = this.rotationTop = 1;
            pane.setBounds(0.0, 0.0, 0.4375, 1.0, 1.0, 0.5625);
            boolean rendered = this.renderStandardBlock(pane, x, y, z);
            this.rotationBottom = this.rotationTop = 0;
            pane.setBounds(0.4375, 0.0, 0.0, 0.5625, 1.0, 1.0);
            return this.renderStandardBlock(pane, x, y, z);
        }

        if (northConnection && !southConnection) {
            pane.setBounds(0.4375, 0.0, 0.0, 0.5625, 1.0, 0.4375);
            this.renderStandardBlock(pane, x, y, z);
        } else if (!northConnection && southConnection) {
            pane.setBounds(0.4375, 0.0, 0.5625, 0.5625, 1.0, 1.0);
            this.renderStandardBlock(pane, x, y, z);
        } else if (northConnection && southConnection) {
            minZ = 0.0F;
            maxZ = 1.0F;
        }

        if (westConnection && eastConnection) {
            minX = 0.0F;
            maxX = 1.0F;
            this.rotationBottom = this.rotationTop = 1;
        } else if (westConnection) {
            this.rotationBottom = this.rotationTop = 1;
            pane.setBounds(0.0, 0.0, 0.4375, 0.4375, 1.0, 0.5625);
            this.renderStandardBlock(pane, x, y, z);
            this.rotationBottom = this.rotationTop = 0;
        } else if (eastConnection) {
            this.rotationBottom = this.rotationTop = 1;
            pane.setBounds(0.5625, 0.0, 0.4375, 1.0, 1.0, 0.5625);
            this.renderStandardBlock(pane, x, y, z);
            this.rotationBottom = this.rotationTop = 0;
        }

        pane.setBounds(minX, 0.0, minZ, maxX, 1.0, maxZ);
        boolean render = this.renderStandardBlock(pane, x, y, z);
        this.rotationBottom = this.rotationTop = 0;
        this.clearOverrideTexture();
        pane.setStateBounds((World)this.world, x, y, z);
        return render;
    }

    @Override
    public boolean renderItemIn3d() {
        return false;
    }
}
