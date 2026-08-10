package net.minecraft.client.render.blocks.model.plants;

import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.blocks.model.BlockModel;
import net.minecraft.game.block.Block;
import net.minecraft.game.texture.Icon;

public class VineModel extends BlockModel {
    @Override
    public boolean render(Block block, int x, int y, int z) {
        Tessellator tess = Tessellator.instance;
        Icon icon = block.getIcon(0, 0);
        if (this.hasOverrideTexture()) {
            icon = this.textureOverride;
        }

        float minU = icon.getMinU();
        float minV = icon.getMinV();
        float maxU = icon.getMaxU();
        float maxV = icon.getMaxV();
        double offset = 0.05;
        int meta = this.world.getBlockMetadata(x, y, z);
        boolean render = false;
        this.applyColorCorrection(block, x, y, z, 1.0F);
        if (meta == 5) {
            tess.addTexturedVertex(x + offset, y + 1, z + 1, minU, minV);
            tess.addTexturedVertex(x + offset, y + 0, z + 1, minU, maxV);
            tess.addTexturedVertex(x + offset, y + 0, z + 0, maxU, maxV);
            tess.addTexturedVertex(x + offset, y + 1, z + 0, maxU, minV);
            tess.addTexturedVertex(x + offset, y + 1, z + 0, maxU, minV);
            tess.addTexturedVertex(x + offset, y + 0, z + 0, maxU, maxV);
            tess.addTexturedVertex(x + offset, y + 0, z + 1, minU, maxV);
            tess.addTexturedVertex(x + offset, y + 1, z + 1, minU, minV);
            render = true;
        }

        if (meta == 4) {
            tess.addTexturedVertex(x + 1 - offset, y + 0, z + 1, maxU, maxV);
            tess.addTexturedVertex(x + 1 - offset, y + 1, z + 1, maxU, minV);
            tess.addTexturedVertex(x + 1 - offset, y + 1, z + 0, minU, minV);
            tess.addTexturedVertex(x + 1 - offset, y + 0, z + 0, minU, maxV);
            tess.addTexturedVertex(x + 1 - offset, y + 0, z + 0, minU, maxV);
            tess.addTexturedVertex(x + 1 - offset, y + 1, z + 0, minU, minV);
            tess.addTexturedVertex(x + 1 - offset, y + 1, z + 1, maxU, minV);
            tess.addTexturedVertex(x + 1 - offset, y + 0, z + 1, maxU, maxV);
            render = true;
        }

        if (meta == 3) {
            tess.addTexturedVertex(x + 1, y + 0, z + offset, maxU, maxV);
            tess.addTexturedVertex(x + 1, y + 1, z + offset, maxU, minV);
            tess.addTexturedVertex(x + 0, y + 1, z + offset, minU, minV);
            tess.addTexturedVertex(x + 0, y + 0, z + offset, minU, maxV);
            tess.addTexturedVertex(x + 0, y + 0, z + offset, minU, maxV);
            tess.addTexturedVertex(x + 0, y + 1, z + offset, minU, minV);
            tess.addTexturedVertex(x + 1, y + 1, z + offset, maxU, minV);
            tess.addTexturedVertex(x + 1, y + 0, z + offset, maxU, maxV);
            render = true;
        }

        if (meta == 2) {
            tess.addTexturedVertex(x + 1, y + 1, z + 1 - offset, minU, minV);
            tess.addTexturedVertex(x + 1, y + 0, z + 1 - offset, minU, maxV);
            tess.addTexturedVertex(x + 0, y + 0, z + 1 - offset, maxU, maxV);
            tess.addTexturedVertex(x + 0, y + 1, z + 1 - offset, maxU, minV);
            tess.addTexturedVertex(x + 0, y + 1, z + 1 - offset, maxU, minV);
            tess.addTexturedVertex(x + 0, y + 0, z + 1 - offset, maxU, maxV);
            tess.addTexturedVertex(x + 1, y + 0, z + 1 - offset, minU, maxV);
            tess.addTexturedVertex(x + 1, y + 1, z + 1 - offset, minU, minV);
            render = true;
        }

        if (this.world.isBlockFull(x, y + 1, z)) {
            tess.addTexturedVertex(x + 1, y + 1 - offset, z + 0, minU, minV);
            tess.addTexturedVertex(x + 1, y + 1 - offset, z + 1, minU, maxV);
            tess.addTexturedVertex(x + 0, y + 1 - offset, z + 1, maxU, maxV);
            tess.addTexturedVertex(x + 0, y + 1 - offset, z + 0, maxU, minV);
            render = true;
        }

        return render;
    }

    @Override
    public boolean renderItemIn3d() {
        return false;
    }
}
