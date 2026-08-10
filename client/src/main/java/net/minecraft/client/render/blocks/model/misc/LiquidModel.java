package net.minecraft.client.render.blocks.model.misc;

import net.minecraft.client.options.OptionValues;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.blocks.model.BlockModel;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.liquid.LiquidBlock;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.texture.Icon;
import net.minecraft.util.math.MathHelper;

public class LiquidModel extends BlockModel {
    @Override
    public boolean render(Block block, int x, int y, int z) {
        Tessellator tess = Tessellator.instance;
        int color = block.getRenderColor(this.world, x, y, z);
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        boolean renderTop = block.renderFace(this.world, x, y + 1, z, 1);
        boolean renderBottom = block.renderFace(this.world, x, y - 1, z, 0);
        boolean renderNorth = block.renderFace(this.world, x, y, z - 1, 2);
        boolean renderSouth = block.renderFace(this.world, x, y, z + 1, 3);
        boolean renderWest = block.renderFace(this.world, x - 1, y, z, 4);
        boolean renderEast = block.renderFace(this.world, x + 1, y, z, 5);
        if (!renderTop && !renderBottom && !renderNorth && !renderSouth && !renderWest && !renderEast) {
            return false;
        }

        boolean render = false;
        float brightNorth = 0.5F;
        float brightSouth = 1.0F;
        float brightWest = 0.8F;
        float brightEast = 0.6F;
        double minY = 0.0;
        double maxY = 1.0;
        Material mat = block.material;
        int meta = this.world.getBlockMetadata(x, y, z);
        double height = this.getFluidHeight(x, y, z, mat);
        double heightRight = this.getFluidHeight(x, y, z + 1, mat);
        double heightAdjacent = this.getFluidHeight(x + 1, y, z + 1, mat);
        double heightLeft = this.getFluidHeight(x + 1, y, z, mat);
        double heightOffset = 0.001F;
        if (renderTop) {
            render = true;
            Icon icon = block.getIcon(1, meta);
            float flowDirection = (float)LiquidBlock.getFlowDirection(this.world, x, y, z, mat);
            if (flowDirection > -999.0F) {
                icon = block.getIcon(2, meta);
            }

            height -= heightOffset;
            heightRight -= heightOffset;
            heightAdjacent -= heightOffset;
            heightLeft -= heightOffset;
            float minU;
            float u1;
            float maxU;
            float u2;
            float minV;
            float maxV;
            float v1;
            float v2;
            if (flowDirection < -999.0F) {
                minU = icon.getInterpolatedU(0.0);
                minV = icon.getInterpolatedV(0.0);
                u1 = minU;
                maxV = icon.getInterpolatedV(16.0);
                maxU = icon.getInterpolatedU(16.0);
                v1 = maxV;
                u2 = maxU;
                v2 = minV;
            } else {
                float offset1 = MathHelper.sin(flowDirection) * 0.25F;
                float offset2 = MathHelper.cos(flowDirection) * 0.25F;
                minU = icon.getInterpolatedU(8.0F + (-offset2 - offset1) * 16.0F);
                minV = icon.getInterpolatedV(8.0F + (-offset2 + offset1) * 16.0F);
                u1 = icon.getInterpolatedU(8.0F + (-offset2 + offset1) * 16.0F);
                maxV = icon.getInterpolatedV(8.0F + (offset2 + offset1) * 16.0F);
                maxU = icon.getInterpolatedU(8.0F + (offset2 + offset1) * 16.0F);
                v1 = icon.getInterpolatedV(8.0F + (offset2 - offset1) * 16.0F);
                u2 = icon.getInterpolatedU(8.0F + (offset2 - offset1) * 16.0F);
                v2 = icon.getInterpolatedV(8.0F + (-offset2 - offset1) * 16.0F);
            }

            tess.setBrightness(block.getBrightness(this.world, x, y, z));
            tess.setColor(brightSouth * red, brightSouth * green, brightSouth * blue);
            tess.addTexturedVertex(x, y + height, z, minU, minV);
            tess.addTexturedVertex(x, y + heightRight, z + 1, u1, maxV);
            tess.addTexturedVertex(x + 1, y + heightAdjacent, z + 1, maxU, v1);
            tess.addTexturedVertex(x + 1, y + heightLeft, z, u2, v2);
            if (this.world.getBlock(x, y + 1, z) == null
                || this.world.getBlockMetadata(x, y, z) > 0 && this.world.getBlockMetadata(x, y, z) < 8
                || !this.world.getBlock(x, y + 1, z).fullBlock()) {
                tess.addTexturedVertex(x + 1, y + heightLeft, z, u2, v2);
                tess.addTexturedVertex(x + 1, y + heightAdjacent, z + 1, maxU, v1);
                tess.addTexturedVertex(x, y + heightRight, z + 1, u1, maxV);
                tess.addTexturedVertex(x, y + height, z, minU, minV);
            }
        }

        if (renderBottom) {
            tess.setBrightness(block.getBrightness(this.world, x, y - 1, z));
            tess.setColor(brightNorth, brightNorth, brightNorth);
            this.renderFace(block, x, y + heightOffset, z, block.getSideIcon(0), BlockModel.Face.BOTTOM, false);
            render = true;
        }

        boolean[] renderFaces = new boolean[]{renderNorth, renderSouth, renderWest, renderEast};

        for (int face = 0; face < 4; face++) {
            int newX = x;
            int newZ = z;
            if (face == 0) {
                newZ = z - 1;
            }

            if (face == 1) {
                newZ++;
            }

            if (face == 2) {
                newX = x - 1;
            }

            if (face == 3) {
                newX++;
            }

            Icon icon = block.getIcon(face + 2, meta);
            if (renderFaces[face]) {
                double z1;
                double x2;
                double z2;
                double y1;
                double y2;
                double x1;
                if (face == 0) {
                    y1 = height;
                    y2 = heightLeft;
                    x1 = x;
                    x2 = x + 1;
                    z1 = z + heightOffset;
                    z2 = z + heightOffset;
                } else if (face == 1) {
                    y1 = heightAdjacent;
                    y2 = heightRight;
                    x1 = x + 1;
                    x2 = x;
                    z1 = z + 1 - heightOffset;
                    z2 = z + 1 - heightOffset;
                } else if (face == 2) {
                    y1 = heightRight;
                    y2 = height;
                    x1 = x + heightOffset;
                    x2 = x + heightOffset;
                    z1 = z + 1;
                    z2 = z;
                } else {
                    y1 = heightLeft;
                    y2 = heightAdjacent;
                    x1 = x + 1 - heightOffset;
                    x2 = x + 1 - heightOffset;
                    z1 = z;
                    z2 = z + 1;
                }

                render = true;
                float minU = icon.getInterpolatedU(0.0);
                float offset1 = icon.getInterpolatedU(8.0);
                float offset2 = icon.getInterpolatedV((1.0 - y1) * 16.0 * 0.5);
                float maxV = icon.getInterpolatedV((1.0 - y2) * 16.0 * 0.5);
                float minV = icon.getInterpolatedV(8.0);
                int bright = block.getBrightness(this.world, newX, y, newZ);
                tess.setBrightness(bright);
                float finalColor = 1.0F;
                if (face < 2) {
                    finalColor *= brightWest;
                } else {
                    finalColor *= brightEast;
                }

                tess.setColor(brightSouth * finalColor * red, brightSouth * finalColor * green, brightSouth * finalColor * blue);
                tess.addTexturedVertex(x1, y + y1, z1, minU, offset2);
                tess.addTexturedVertex(x2, y + y2, z2, offset1, maxV);
                tess.addTexturedVertex(x2, y, z2, offset1, minV);
                tess.addTexturedVertex(x1, y, z1, minU, minV);
                tess.addTexturedVertex(x1, y, z1, minU, minV);
                tess.addTexturedVertex(x2, y, z2, offset1, minV);
                tess.addTexturedVertex(x2, y + y2, z2, offset1, maxV);
                tess.addTexturedVertex(x1, y + y1, z1, minU, offset2);
            }
        }

        block.minY = minY;
        block.maxY = maxY;
        return render;
    }

    private float getFluidHeight(int x, int y, int z, Material mat) {
        int totalOffset = 0;
        float minOffset = 0.0F;
        float maxOffset = 1.0F;

        for (int face = 0; face < 4; face++) {
            int newX = x - (face & 1);
            int newZ = z - (face >> 1 & 1);
            if (this.world.getBlockMaterial(newX, y + 1, newZ) == mat) {
                return 1.0F;
            }

            Material adjacentMat = this.world.getBlockMaterial(newX, y, newZ);
            if (adjacentMat != mat) {
                if (!adjacentMat.isSolid()) {
                    minOffset++;
                    totalOffset++;
                }
            } else {
                int meta = this.world.getBlockMetadata(newX, y, newZ);
                if (meta >= 8 || meta == 0) {
                    minOffset += LiquidBlock.getHeight(meta) * 10.0F;
                    totalOffset += 10;
                }

                minOffset += LiquidBlock.getHeight(meta);
                totalOffset++;
                if (this.world.getBlockId(newX, y + 1, newZ) != 0
                    && this.world.isSolid(newX, y + 1, newZ)
                    && this.world.getBlockMaterial(newX, y + 1, newZ) != mat) {
                    maxOffset = 1.12F;
                }
            }
        }

        float offset = 0.0F;
        if (OptionValues.offsetAmount > 0.0F) {
            int var13;
            offset = ((var13 = x * 3111 + y * 31827) * var13 * 31287123 + var13 * 318127 & 7) / 7.0F / 9.0F;
        }

        return maxOffset - minOffset / totalOffset + offset;
    }
}
