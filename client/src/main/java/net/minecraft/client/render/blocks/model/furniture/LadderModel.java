package net.minecraft.client.render.blocks.model.furniture;

import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.blocks.model.BlockModel;
import net.minecraft.game.block.Block;
import net.minecraft.game.texture.Icon;

public class LadderModel extends BlockModel {
    @Override
    public boolean render(Block block, int x, int y, int z) {
        Tessellator tess = Tessellator.instance;
        Icon icon = block.getSideIcon(0);
        boolean render = false;
        if (this.hasOverrideTexture()) {
            icon = this.textureOverride;
        }

        int brightness = block.getBrightness(this.world, x, y, z);
        tess.setBrightness(brightness);
        tess.setColor(1.0F, 1.0F, 1.0F);
        float minU = icon.getMinU();
        float minV = icon.getMinV();
        float maxU = icon.getMaxU();
        float maxV = icon.getMaxV();
        int meta = this.world.getBlockMetadata(x, y, z);
        double offset = 0.05;
        if (meta == 5) {
            tess.addTexturedVertex(x + offset, y + 1, z + 1, minU, minV);
            tess.addTexturedVertex(x + offset, y, z + 1, minU, maxV);
            tess.addTexturedVertex(x + offset, y, z, maxU, maxV);
            tess.addTexturedVertex(x + offset, y + 1, z, maxU, minV);
            render = true;
        }

        if (meta == 4) {
            tess.addTexturedVertex(x + 1 - offset, y, z + 1, maxU, maxV);
            tess.addTexturedVertex(x + 1 - offset, y + 1, z + 1, maxU, minV);
            tess.addTexturedVertex(x + 1 - offset, y + 1, z, minU, minV);
            tess.addTexturedVertex(x + 1 - offset, y, z, minU, maxV);
            render = true;
        }

        if (meta == 3) {
            tess.addTexturedVertex(x + 1, y, z + offset, maxU, maxV);
            tess.addTexturedVertex(x + 1, y + 1, z + offset, maxU, minV);
            tess.addTexturedVertex(x, y + 1, z + offset, minU, minV);
            tess.addTexturedVertex(x, y, z + offset, minU, maxV);
            render = true;
        }

        if (meta == 2) {
            tess.addTexturedVertex(x + 1, y + 1, z + 1 - offset, minU, minV);
            tess.addTexturedVertex(x + 1, y, z + 1 - offset, minU, maxV);
            tess.addTexturedVertex(x, y, z + 1 - offset, maxU, maxV);
            tess.addTexturedVertex(x, y + 1, z + 1 - offset, maxU, minV);
            render = true;
        }

        return render;
    }

    @Override
    public boolean renderItemIn3d() {
        return false;
    }
}
