package net.minecraft.client.render.blocks.model.misc;

import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.blocks.model.BlockModel;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.texture.Icon;

public class CrystalModel extends BlockModel {
    @Override
    public boolean render(Block block, int x, int y, int z) {
        this.applyColorCorrection(block, x, y, z, 1.0F);
        return this.renderRotatedSquares(block, this.world.getBlockMetadata(x, y, z), x, y, z);
    }

    public boolean renderRotatedSquares(Block block, int meta, double x, double y, double z) {
        Tessellator tess = Tessellator.instance;
        Icon icon = block.getIcon(0, meta);
        boolean render = false;
        if (this.hasOverrideTexture()) {
            icon = this.textureOverride;
        }

        float minU = icon.getMinU();
        float maxU = icon.getMaxU();
        float minV = icon.getMinV();
        float maxV = icon.getMaxV();
        double xStart = x + 0.5 - 0.45F;
        double xEnd = x + 0.5 + 0.45F;
        double zStart = z + 0.5 - 0.45F;
        double zEnd = z + 0.5 + 0.45F;
        double yStart = y + 1.0;
        double yEnd = y;
        if (this.world.getBlock((int)x + 1, (int)y, (int)z) == BlockList.clusterBlock) {
            tess.addTexturedVertex(xStart, yEnd, zStart, minU, minV);
            tess.addTexturedVertex(xEnd, yEnd, zStart, minU, maxV);
            tess.addTexturedVertex(xEnd, yStart, zEnd, maxU, maxV);
            tess.addTexturedVertex(xStart, yStart, zEnd, maxU, minV);
            tess.addTexturedVertex(xStart, yEnd, zEnd, minU, minV);
            tess.addTexturedVertex(xEnd, yEnd, zEnd, minU, maxV);
            tess.addTexturedVertex(xEnd, yStart, zStart, maxU, maxV);
            tess.addTexturedVertex(xStart, yStart, zStart, maxU, minV);
            tess.addTexturedVertex(xStart, yStart, zEnd, minU, minV);
            tess.addTexturedVertex(xEnd, yStart, zEnd, minU, maxV);
            tess.addTexturedVertex(xEnd, yEnd, zStart, maxU, maxV);
            tess.addTexturedVertex(xStart, yEnd, zStart, maxU, minV);
            tess.addTexturedVertex(xStart, yStart, zStart, minU, minV);
            tess.addTexturedVertex(xEnd, yStart, zStart, minU, maxV);
            tess.addTexturedVertex(xEnd, yEnd, zEnd, maxU, maxV);
            tess.addTexturedVertex(xStart, yEnd, zEnd, maxU, minV);
            render = true;
        } else if (this.world.getBlock((int)x - 1, (int)y, (int)z) == BlockList.clusterBlock) {
            tess.addTexturedVertex(xEnd, yStart, zStart, minU, minV);
            tess.addTexturedVertex(xStart, yStart, zStart, minU, maxV);
            tess.addTexturedVertex(xStart, yEnd, zEnd, maxU, maxV);
            tess.addTexturedVertex(xEnd, yEnd, zEnd, maxU, minV);
            tess.addTexturedVertex(xEnd, yStart, zEnd, minU, minV);
            tess.addTexturedVertex(xStart, yStart, zEnd, minU, maxV);
            tess.addTexturedVertex(xStart, yEnd, zStart, maxU, maxV);
            tess.addTexturedVertex(xEnd, yEnd, zStart, maxU, minV);
            tess.addTexturedVertex(xEnd, yEnd, zEnd, minU, minV);
            tess.addTexturedVertex(xStart, yEnd, zEnd, minU, maxV);
            tess.addTexturedVertex(xStart, yStart, zStart, maxU, maxV);
            tess.addTexturedVertex(xEnd, yStart, zStart, maxU, minV);
            tess.addTexturedVertex(xEnd, yEnd, zStart, minU, minV);
            tess.addTexturedVertex(xStart, yEnd, zStart, minU, maxV);
            tess.addTexturedVertex(xStart, yStart, zEnd, maxU, maxV);
            tess.addTexturedVertex(xEnd, yStart, zEnd, maxU, minV);
            render = true;
        } else if (this.world.getBlock((int)x, (int)y, (int)z + 1) == BlockList.clusterBlock) {
            tess.addTexturedVertex(xStart, yEnd, zStart, minU, minV);
            tess.addTexturedVertex(xStart, yEnd, zEnd, minU, maxV);
            tess.addTexturedVertex(xEnd, yStart, zEnd, maxU, maxV);
            tess.addTexturedVertex(xEnd, yStart, zStart, maxU, minV);
            tess.addTexturedVertex(xEnd, yStart, zStart, minU, minV);
            tess.addTexturedVertex(xEnd, yStart, zEnd, minU, maxV);
            tess.addTexturedVertex(xStart, yEnd, zEnd, maxU, maxV);
            tess.addTexturedVertex(xStart, yEnd, zStart, maxU, minV);
            tess.addTexturedVertex(xStart, yStart, zStart, minU, minV);
            tess.addTexturedVertex(xStart, yStart, zEnd, minU, maxV);
            tess.addTexturedVertex(xEnd, yEnd, zEnd, maxU, maxV);
            tess.addTexturedVertex(xEnd, yEnd, zStart, maxU, minV);
            tess.addTexturedVertex(xEnd, yEnd, zStart, minU, minV);
            tess.addTexturedVertex(xEnd, yEnd, zEnd, minU, maxV);
            tess.addTexturedVertex(xStart, yStart, zEnd, maxU, maxV);
            tess.addTexturedVertex(xStart, yStart, zStart, maxU, minV);
            render = true;
        } else if (this.world.getBlock((int)x, (int)y, (int)z - 1) == BlockList.clusterBlock) {
            tess.addTexturedVertex(xStart, yStart, zEnd, minU, minV);
            tess.addTexturedVertex(xStart, yStart, zStart, minU, maxV);
            tess.addTexturedVertex(xEnd, yEnd, zStart, maxU, maxV);
            tess.addTexturedVertex(xEnd, yEnd, zEnd, maxU, minV);
            tess.addTexturedVertex(xEnd, yStart, zEnd, minU, minV);
            tess.addTexturedVertex(xEnd, yStart, zStart, minU, maxV);
            tess.addTexturedVertex(xStart, yEnd, zStart, maxU, maxV);
            tess.addTexturedVertex(xStart, yEnd, zEnd, maxU, minV);
            tess.addTexturedVertex(xStart, yEnd, zEnd, minU, minV);
            tess.addTexturedVertex(xStart, yEnd, zStart, minU, maxV);
            tess.addTexturedVertex(xEnd, yStart, zStart, maxU, maxV);
            tess.addTexturedVertex(xEnd, yStart, zEnd, maxU, minV);
            tess.addTexturedVertex(xEnd, yEnd, zEnd, minU, minV);
            tess.addTexturedVertex(xEnd, yEnd, zStart, minU, maxV);
            tess.addTexturedVertex(xStart, yStart, zStart, maxU, maxV);
            tess.addTexturedVertex(xStart, yStart, zEnd, maxU, minV);
            render = true;
        } else {
            if (this.world.getBlock((int)x, (int)y + 1, (int)z) == BlockList.clusterBlock) {
                yEnd = y + 1.0;
                yStart = y;
            }

            tess.addTexturedVertex(xStart, yStart, zStart, minU, minV);
            tess.addTexturedVertex(xStart, yEnd, zStart, minU, maxV);
            tess.addTexturedVertex(xEnd, yEnd, zEnd, maxU, maxV);
            tess.addTexturedVertex(xEnd, yStart, zEnd, maxU, minV);
            tess.addTexturedVertex(xEnd, yStart, zEnd, minU, minV);
            tess.addTexturedVertex(xEnd, yEnd, zEnd, minU, maxV);
            tess.addTexturedVertex(xStart, yEnd, zStart, maxU, maxV);
            tess.addTexturedVertex(xStart, yStart, zStart, maxU, minV);
            tess.addTexturedVertex(xStart, yStart, zEnd, minU, minV);
            tess.addTexturedVertex(xStart, yEnd, zEnd, minU, maxV);
            tess.addTexturedVertex(xEnd, yEnd, zStart, maxU, maxV);
            tess.addTexturedVertex(xEnd, yStart, zStart, maxU, minV);
            tess.addTexturedVertex(xEnd, yStart, zStart, minU, minV);
            tess.addTexturedVertex(xEnd, yEnd, zStart, minU, maxV);
            tess.addTexturedVertex(xStart, yEnd, zEnd, maxU, maxV);
            tess.addTexturedVertex(xStart, yStart, zEnd, maxU, minV);
            render = true;
        }

        return render;
    }

    @Override
    public boolean renderItemIn3d() {
        return false;
    }
}
