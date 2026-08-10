package net.minecraft.client.render.blocks.model.misc;

import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.blocks.model.BlockModel;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.texture.Icon;

public class FireModel extends BlockModel {
    @Override
    public boolean render(Block block, int x, int y, int z) {
        Tessellator tess = Tessellator.instance;
        Icon sideIcon1 = block.getSideIcon(0);
        Icon sideIcon2 = block.getSideIcon(1);
        Icon icon = sideIcon1;
        if (this.hasOverrideTexture()) {
            icon = this.textureOverride;
        }

        int bright = block.getBrightness(this.world, x, y, z);
        tess.setColor(1.0F, 1.0F, 1.0F);
        tess.setBrightness(bright);
        float minu = icon.getMinU();
        float minv = icon.getMaxU();
        float maxu = icon.getMinV();
        float maxv = icon.getMaxV();
        double flameHeight = 1.4;
        boolean render = false;
        if (!this.world.isBlockFull(x, y - 1, z) && !BlockList.fire.canSpreadTo(this.world, x, y - 1, z)) {
            float xOffset = 0.2F;
            float yOffset = 0.0625F;
            if ((x + y + z & 1) == 1) {
                minu = sideIcon2.getMinU();
                minv = sideIcon2.getMaxU();
                maxu = sideIcon2.getMinV();
                maxv = sideIcon2.getMaxV();
            }

            if ((x / 2 + y / 2 + z / 2 & 1) == 1) {
                double x1 = minv;
                minv = minu;
                minu = (float)x1;
            }

            if (BlockList.fire.canSpreadTo(this.world, x - 1, y, z)) {
                tess.addTexturedVertex(x + xOffset, y + flameHeight + yOffset, z + 1, minv, maxu);
                tess.addTexturedVertex(x + 0, y + 0 + yOffset, z + 1, minv, maxv);
                tess.addTexturedVertex(x + 0, y + 0 + yOffset, z + 0, minu, maxv);
                tess.addTexturedVertex(x + xOffset, y + flameHeight + yOffset, z + 0, minu, maxu);
                tess.addTexturedVertex(x + xOffset, y + flameHeight + yOffset, z + 0, minu, maxu);
                tess.addTexturedVertex(x + 0, y + 0 + yOffset, z + 0, minu, maxv);
                tess.addTexturedVertex(x + 0, y + 0 + yOffset, z + 1, minv, maxv);
                tess.addTexturedVertex(x + xOffset, y + flameHeight + yOffset, z + 1, minv, maxu);
                render = true;
            }

            if (BlockList.fire.canSpreadTo(this.world, x + 1, y, z)) {
                tess.addTexturedVertex(x + 1 - xOffset, y + flameHeight + yOffset, z + 0, minu, maxu);
                tess.addTexturedVertex(x + 1 - 0, y + 0 + yOffset, z + 0, minu, maxv);
                tess.addTexturedVertex(x + 1 - 0, y + 0 + yOffset, z + 1, minv, maxv);
                tess.addTexturedVertex(x + 1 - xOffset, y + flameHeight + yOffset, z + 1, minv, maxu);
                tess.addTexturedVertex(x + 1 - xOffset, y + flameHeight + yOffset, z + 1, minv, maxu);
                tess.addTexturedVertex(x + 1 - 0, y + 0 + yOffset, z + 1, minv, maxv);
                tess.addTexturedVertex(x + 1 - 0, y + 0 + yOffset, z + 0, minu, maxv);
                tess.addTexturedVertex(x + 1 - xOffset, y + flameHeight + yOffset, z + 0, minu, maxu);
                render = true;
            }

            if (BlockList.fire.canSpreadTo(this.world, x, y, z - 1)) {
                tess.addTexturedVertex(x + 0, y + flameHeight + yOffset, z + xOffset, minv, maxu);
                tess.addTexturedVertex(x + 0, y + 0 + yOffset, z + 0, minv, maxv);
                tess.addTexturedVertex(x + 1, y + 0 + yOffset, z + 0, minu, maxv);
                tess.addTexturedVertex(x + 1, y + flameHeight + yOffset, z + xOffset, minu, maxu);
                tess.addTexturedVertex(x + 1, y + flameHeight + yOffset, z + xOffset, minu, maxu);
                tess.addTexturedVertex(x + 1, y + 0 + yOffset, z + 0, minu, maxv);
                tess.addTexturedVertex(x + 0, y + 0 + yOffset, z + 0, minv, maxv);
                tess.addTexturedVertex(x + 0, y + flameHeight + yOffset, z + xOffset, minv, maxu);
                render = true;
            }

            if (BlockList.fire.canSpreadTo(this.world, x, y, z + 1)) {
                tess.addTexturedVertex(x + 1, y + flameHeight + yOffset, z + 1 - xOffset, minu, maxu);
                tess.addTexturedVertex(x + 1, y + 0 + yOffset, z + 1 - 0, minu, maxv);
                tess.addTexturedVertex(x + 0, y + 0 + yOffset, z + 1 - 0, minv, maxv);
                tess.addTexturedVertex(x + 0, y + flameHeight + yOffset, z + 1 - xOffset, minv, maxu);
                tess.addTexturedVertex(x + 0, y + flameHeight + yOffset, z + 1 - xOffset, minv, maxu);
                tess.addTexturedVertex(x + 0, y + 0 + yOffset, z + 1 - 0, minv, maxv);
                tess.addTexturedVertex(x + 1, y + 0 + yOffset, z + 1 - 0, minu, maxv);
                tess.addTexturedVertex(x + 1, y + flameHeight + yOffset, z + 1 - xOffset, minu, maxu);
                render = true;
            }

            if (BlockList.fire.canSpreadTo(this.world, x, y + 1, z)) {
                double x1 = x + 0.5 + 0.5;
                double x2 = x + 0.5 - 0.5;
                double y1 = z + 0.5 + 0.5;
                double y2 = z + 0.5 - 0.5;
                double z1 = x + 0.5 - 0.5;
                double z2 = x + 0.5 + 0.5;
                double z3 = z + 0.5 - 0.5;
                double d35 = z + 0.5 + 0.5;
                minu = sideIcon1.getMinU();
                minv = sideIcon1.getMaxU();
                maxu = sideIcon1.getMinV();
                maxv = sideIcon1.getMaxV();
                y++;
                flameHeight = -0.2;
                if ((x + y + z & 1) == 0) {
                    tess.addTexturedVertex(z1, y + flameHeight, z + 0, minv, maxu);
                    tess.addTexturedVertex(x1, y + 0, z + 0, minv, maxv);
                    tess.addTexturedVertex(x1, y + 0, z + 1, minu, maxv);
                    tess.addTexturedVertex(z1, y + flameHeight, z + 1, minu, maxu);
                    minu = sideIcon2.getMinU();
                    minv = sideIcon2.getMaxU();
                    maxu = sideIcon2.getMinV();
                    maxv = sideIcon2.getMaxV();
                    tess.addTexturedVertex(z2, y + flameHeight, z + 1, minv, maxu);
                    tess.addTexturedVertex(x2, y + 0, z + 1, minv, maxv);
                    tess.addTexturedVertex(x2, y + 0, z + 0, minu, maxv);
                    tess.addTexturedVertex(z2, y + flameHeight, z + 0, minu, maxu);
                    render = true;
                } else {
                    tess.addTexturedVertex(x + 0, y + flameHeight, d35, minv, maxu);
                    tess.addTexturedVertex(x + 0, y + 0, y2, minv, maxv);
                    tess.addTexturedVertex(x + 1, y + 0, y2, minu, maxv);
                    tess.addTexturedVertex(x + 1, y + flameHeight, d35, minu, maxu);
                    minu = sideIcon2.getMinU();
                    minv = sideIcon2.getMaxU();
                    maxu = sideIcon2.getMinV();
                    maxv = sideIcon2.getMaxV();
                    tess.addTexturedVertex(x + 1, y + flameHeight, z3, minv, maxu);
                    tess.addTexturedVertex(x + 1, y + 0, y1, minv, maxv);
                    tess.addTexturedVertex(x + 0, y + 0, y1, minu, maxv);
                    tess.addTexturedVertex(x + 0, y + flameHeight, z3, minu, maxu);
                    render = true;
                }
            }
        } else {
            double x3 = x + 0.5 + 0.2;
            double x1 = x + 0.5 - 0.2;
            double x2 = z + 0.5 + 0.2;
            double y1 = z + 0.5 - 0.2;
            double y2 = x + 0.5 - 0.3;
            double z1 = x + 0.5 + 0.3;
            double z2 = z + 0.5 - 0.3;
            double z3 = z + 0.5 + 0.3;
            tess.addTexturedVertex(y2, y + flameHeight, z + 1, minv, maxu);
            tess.addTexturedVertex(x3, y + 0, z + 1, minv, maxv);
            tess.addTexturedVertex(x3, y + 0, z + 0, minu, maxv);
            tess.addTexturedVertex(y2, y + flameHeight, z + 0, minu, maxu);
            tess.addTexturedVertex(z1, y + flameHeight, z + 0, minv, maxu);
            tess.addTexturedVertex(x1, y + 0, z + 0, minv, maxv);
            tess.addTexturedVertex(x1, y + 0, z + 1, minu, maxv);
            tess.addTexturedVertex(z1, y + flameHeight, z + 1, minu, maxu);
            minu = sideIcon2.getMinU();
            minv = sideIcon2.getMaxU();
            maxu = sideIcon2.getMinV();
            maxv = sideIcon2.getMaxV();
            tess.addTexturedVertex(x + 1, y + flameHeight, z3, minv, maxu);
            tess.addTexturedVertex(x + 1, y + 0, y1, minv, maxv);
            tess.addTexturedVertex(x + 0, y + 0, y1, minu, maxv);
            tess.addTexturedVertex(x + 0, y + flameHeight, z3, minu, maxu);
            tess.addTexturedVertex(x + 0, y + flameHeight, z2, minv, maxu);
            tess.addTexturedVertex(x + 0, y + 0, x2, minv, maxv);
            tess.addTexturedVertex(x + 1, y + 0, x2, minu, maxv);
            tess.addTexturedVertex(x + 1, y + flameHeight, z2, minu, maxu);
            x3 = x + 0.5 - 0.5;
            x1 = x + 0.5 + 0.5;
            x2 = z + 0.5 - 0.5;
            y1 = z + 0.5 + 0.5;
            y2 = x + 0.5 - 0.4;
            z1 = x + 0.5 + 0.4;
            z2 = z + 0.5 - 0.4;
            z3 = z + 0.5 + 0.4;
            tess.addTexturedVertex(y2, y + flameHeight, z + 0, minu, maxu);
            tess.addTexturedVertex(x3, y + 0, z + 0, minu, maxv);
            tess.addTexturedVertex(x3, y + 0, z + 1, minv, maxv);
            tess.addTexturedVertex(y2, y + flameHeight, z + 1, minv, maxu);
            tess.addTexturedVertex(z1, y + flameHeight, z + 1, minu, maxu);
            tess.addTexturedVertex(x1, y + 0, z + 1, minu, maxv);
            tess.addTexturedVertex(x1, y + 0, z + 0, minv, maxv);
            tess.addTexturedVertex(z1, y + flameHeight, z + 0, minv, maxu);
            minu = sideIcon1.getMinU();
            minv = sideIcon1.getMaxU();
            maxu = sideIcon1.getMinV();
            maxv = sideIcon1.getMaxV();
            tess.addTexturedVertex(x + 0, y + flameHeight, z3, minu, maxu);
            tess.addTexturedVertex(x + 0, y + 0, y1, minu, maxv);
            tess.addTexturedVertex(x + 1, y + 0, y1, minv, maxv);
            tess.addTexturedVertex(x + 1, y + flameHeight, z3, minv, maxu);
            tess.addTexturedVertex(x + 1, y + flameHeight, z2, minu, maxu);
            tess.addTexturedVertex(x + 1, y + 0, x2, minu, maxv);
            tess.addTexturedVertex(x + 0, y + 0, x2, minv, maxv);
            tess.addTexturedVertex(x + 0, y + flameHeight, z2, minv, maxu);
            render = true;
        }

        return render;
    }

    @Override
    public boolean renderItemIn3d() {
        return false;
    }
}
