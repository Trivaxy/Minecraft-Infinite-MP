package net.minecraft.client.render.blocks.model.plants;

import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.blocks.model.BlockModel;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.plants.Stem;
import net.minecraft.game.texture.Icon;

public class StemModel extends BlockModel {
    @Override
    public boolean render(Block block, int x, int y, int z) {
        Stem stem = (Stem)block;
        stem.modifyOutline(this.world, x, y, z);
        int state = stem.getState(this.world, x, y, z);
        float maxY = this.world.getBlockMetadata(x, y, z) / 10.0F + 0.3F;
        this.applyColorCorrection(block, x, y, z, 1.0F);
        if (state < 0) {
            this.renderSmallStem(stem, this.world.getBlockMetadata(x, y, z), maxY, x, y - 0.0625F, z);
        } else {
            this.renderSmallStem(stem, this.world.getBlockMetadata(x, y, z), 0.5F, x, y - 0.0625F, z);
            this.renderLargeStem(stem, this.world.getBlockMetadata(x, y, z), state, maxY, x, y - 0.0625F, z);
        }

        return true;
    }

    private void renderSmallStem(Block block, int state, float maxHeight, float x, float y, float z) {
        Tessellator tess = Tessellator.instance;
        Icon icon = block.getIcon(0, state);
        if (this.textureOverride != null) {
            icon = this.textureOverride;
        }

        float minU = icon.getMinU();
        float maxU = icon.getMaxU();
        float minV = icon.getMinV();
        float maxV = icon.getInterpolatedV(maxHeight * 16.0F);
        float startX = x + 0.5F - 0.45F;
        float endX = x + 0.5F + 0.45F;
        float startZ = z + 0.5F - 0.45F;
        float endZ = z + 0.5F + 0.45F;
        tess.addTexturedVertex(startX, y + maxHeight, startZ, minU, minV);
        tess.addTexturedVertex(startX, y + 0.0F, startZ, minU, maxV);
        tess.addTexturedVertex(endX, y + 0.0F, endZ, maxU, maxV);
        tess.addTexturedVertex(endX, y + maxHeight, endZ, maxU, minV);
        tess.addTexturedVertex(endX, y + maxHeight, endZ, minU, minV);
        tess.addTexturedVertex(endX, y + 0.0F, endZ, minU, maxV);
        tess.addTexturedVertex(startX, y + 0.0F, startZ, maxU, maxV);
        tess.addTexturedVertex(startX, y + maxHeight, startZ, maxU, minV);
        tess.addTexturedVertex(startX, y + maxHeight, endZ, minU, minV);
        tess.addTexturedVertex(startX, y + 0.0F, endZ, minU, maxV);
        tess.addTexturedVertex(endX, y + 0.0F, startZ, maxU, maxV);
        tess.addTexturedVertex(endX, y + maxHeight, startZ, maxU, minV);
        tess.addTexturedVertex(endX, y + maxHeight, startZ, minU, minV);
        tess.addTexturedVertex(endX, y + 0.0F, startZ, minU, maxV);
        tess.addTexturedVertex(startX, y + 0.0F, endZ, maxU, maxV);
        tess.addTexturedVertex(startX, y + maxHeight, endZ, maxU, minV);
    }

    private void renderLargeStem(Block block, int state, int meta, float maxHeight, float x, float y, float z) {
        Tessellator tess = Tessellator.instance;
        Icon icon = ((Stem)block).bentStem;
        if (this.textureOverride != null) {
            icon = this.textureOverride;
        }

        float minU = icon.getMinU();
        float maxU = icon.getMaxU();
        float minV = icon.getMinV();
        float maxV = icon.getMaxV();
        float startX = x + 0.5F - 0.5F;
        float endX = x + 0.5F + 0.5F;
        float startZ = z + 0.5F - 0.5F;
        float endZ = z + 0.5F + 0.5F;
        float offX = x + 0.5F;
        float offZ = z + 0.5F;
        if ((meta + 1) / 2 % 2 == 1) {
            float backU = maxU;
            maxU = minU;
            minU = backU;
        }

        if (meta < 2) {
            tess.addTexturedVertex(startX, y + maxHeight, offZ, minU, minV);
            tess.addTexturedVertex(startX, y + 0.0F, offZ, minU, maxV);
            tess.addTexturedVertex(endX, y + 0.0F, offZ, maxU, maxV);
            tess.addTexturedVertex(endX, y + maxHeight, offZ, maxU, minV);
            tess.addTexturedVertex(endX, y + maxHeight, offZ, maxU, minV);
            tess.addTexturedVertex(endX, y + 0.0F, offZ, maxU, maxV);
            tess.addTexturedVertex(startX, y + 0.0F, offZ, minU, maxV);
            tess.addTexturedVertex(startX, y + maxHeight, offZ, minU, minV);
        } else {
            tess.addTexturedVertex(offX, y + maxHeight, endZ, minU, minV);
            tess.addTexturedVertex(offX, y + 0.0F, endZ, minU, maxV);
            tess.addTexturedVertex(offX, y + 0.0F, startZ, maxU, maxV);
            tess.addTexturedVertex(offX, y + maxHeight, startZ, maxU, minV);
            tess.addTexturedVertex(offX, y + maxHeight, startZ, maxU, minV);
            tess.addTexturedVertex(offX, y + 0.0F, startZ, maxU, maxV);
            tess.addTexturedVertex(offX, y + 0.0F, endZ, minU, maxV);
            tess.addTexturedVertex(offX, y + maxHeight, endZ, minU, minV);
        }
    }

    @Override
    public boolean renderItemIn3d() {
        return false;
    }
}
