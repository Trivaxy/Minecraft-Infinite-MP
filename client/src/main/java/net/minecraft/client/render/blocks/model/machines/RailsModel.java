package net.minecraft.client.render.blocks.model.machines;

import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.blocks.model.BlockModel;
import net.minecraft.game.block.Block;
import net.minecraft.game.texture.Icon;

public class RailsModel extends BlockModel {
    @Override
    public boolean render(Block block, int x, int y, int z) {
        Tessellator tess = Tessellator.instance;
        int meta = this.world.getBlockMetadata(x, y, z);
        double offset = 0.0625;
        Icon icon = block.getIcon(0, meta);
        if (this.hasOverrideTexture()) {
            icon = this.textureOverride;
        }

        float minU = icon.getMinU();
        float maxU = icon.getMaxU();
        float minV = icon.getMinV();
        float maxV = icon.getMaxV();
        double x1 = x + 1;
        double x2 = x + 1;
        double x3 = x;
        double x4 = x;
        double z1 = z;
        double z2 = z + 1;
        double z3 = z + 1;
        double z4 = z;
        double y1 = y + offset;
        double y2 = y + offset;
        double y3 = y + offset;
        double y4 = y + offset;
        if (meta == 1 || meta == 2 || meta == 3 || meta == 7) {
            x1 = x4 = x + 1;
            x2 = x3 = x;
            z1 = z2 = z + 1;
            z3 = z4 = z;
        } else if (meta == 8) {
            x1 = x2 = x;
            x3 = x4 = x + 1;
            z1 = z4 = z + 1;
            z2 = z3 = z;
        } else if (meta == 9) {
            x1 = x4 = x;
            x2 = x3 = x + 1;
            z1 = z2 = z;
            z3 = z4 = z + 1;
        }

        if (meta == 2 || meta == 4) {
            y1++;
            y4++;
        } else if (meta == 3 || meta == 5) {
            y2++;
            y3++;
        }

        tess.setBrightness(block.getBrightness(this.world, x, y, z));
        tess.setColor(1.0F, 1.0F, 1.0F);
        tess.addTexturedVertex(x1, y1, z1, maxU, minV);
        tess.addTexturedVertex(x2, y2, z2, maxU, maxV);
        tess.addTexturedVertex(x3, y3, z3, minU, maxV);
        tess.addTexturedVertex(x4, y4, z4, minU, minV);
        tess.addTexturedVertex(x4, y4, z4, minU, minV);
        tess.addTexturedVertex(x3, y3, z3, minU, maxV);
        tess.addTexturedVertex(x2, y2, z2, maxU, maxV);
        tess.addTexturedVertex(x1, y1, z1, maxU, minV);
        return true;
    }

    @Override
    public boolean renderItemIn3d() {
        return false;
    }
}
