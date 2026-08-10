package net.minecraft.client.render.blocks.model.plants;

import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.blocks.model.BlockModel;
import net.minecraft.game.block.Block;
import net.minecraft.game.texture.Icon;

public class LilyPadModel extends BlockModel {
    @Override
    public boolean render(Block block, int x, int y, int z) {
        Tessellator tess = Tessellator.instance;
        Icon icon = block.getIcon(1, 0);
        if (this.hasOverrideTexture()) {
            icon = this.textureOverride;
        }

        int color = block.getRenderColor(this.world, x, y, z);
        float offset = 0.015625F;
        float minU = icon.getMinU();
        float minV = icon.getMinV();
        float maxU = icon.getMaxU();
        float maxV = icon.getMaxV();
        long seed = x * 3129871 ^ z * 116129781L ^ y;
        seed = seed * seed * 42317861L + seed * 11L;
        int rotation = (int)(seed >> 16 & 3L);
        double startX = x + 0.5;
        double startZ = z + 0.5;
        double endX = (rotation & 1) * 0.5 * (1 - rotation / 2 % 2 * 2);
        double endZ = (rotation + 1 & 1) * 0.5 * (1 - (rotation + 1) / 2 % 2 * 2);
        tess.setBrightness(block.getBrightness(this.world, x, y, z));
        tess.setColor(color);
        tess.addTexturedVertex(startX + endX - endZ, y + offset, startZ + endX + endZ, minU, minV);
        tess.addTexturedVertex(startX + endX + endZ, y + offset, startZ - endX + endZ, maxU, minV);
        tess.addTexturedVertex(startX - endX + endZ, y + offset, startZ - endX - endZ, maxU, maxV);
        tess.addTexturedVertex(startX - endX - endZ, y + offset, startZ + endX - endZ, minU, maxV);
        tess.setColor((color & 16711422) >> 1);
        tess.addTexturedVertex(startX - endX - endZ, y + offset, startZ + endX - endZ, minU, maxV);
        tess.addTexturedVertex(startX - endX + endZ, y + offset, startZ - endX - endZ, maxU, maxV);
        tess.addTexturedVertex(startX + endX + endZ, y + offset, startZ - endX + endZ, maxU, minV);
        tess.addTexturedVertex(startX + endX - endZ, y + offset, startZ + endX + endZ, minU, minV);
        return true;
    }

    @Override
    public boolean renderItemIn3d() {
        return false;
    }
}
