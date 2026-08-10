package net.minecraft.client.render.blocks.model.furniture;

import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.blocks.model.BlockModel;
import net.minecraft.game.block.Block;
import net.minecraft.game.texture.Icon;

public class TorchModel extends BlockModel {
    @Override
    public boolean render(Block block, int x, int y, int z) {
        Tessellator tess = Tessellator.instance;
        int metadata = this.world.getBlockMetadata(x, y, z);
        double offset = 0.4;
        double offsetX = 0.5 - offset;
        double offsetY = 0.2;
        int bright = block.getBrightness(this.world, x, y, z);
        tess.setBrightness(bright);
        tess.setColor(1.0F, 1.0F, 1.0F);
        if (metadata == 1) {
            this.renderTorch(block, x - offsetX, y + offsetY, z, -offset, 0.0);
        } else if (metadata == 2) {
            this.renderTorch(block, x + offsetX, y + offsetY, z, offset, 0.0);
        } else if (metadata == 3) {
            this.renderTorch(block, x, y + offsetY, z - offsetX, 0.0, -offset);
        } else if (metadata == 4) {
            this.renderTorch(block, x, y + offsetY, z + offsetX, 0.0, offset);
        } else {
            this.renderTorch(block, x, y, z, 0.0, 0.0);
        }

        return true;
    }

    public void renderTorch(Block block, double x, double y, double z, double u, double v) {
        Tessellator tess = Tessellator.instance;
        Icon icon = block.getSideIcon(0);
        if (this.hasOverrideTexture()) {
            icon = this.textureOverride;
        }

        float minU = icon.getMinU();
        float minV = icon.getMinV();
        float maxU = icon.getMaxU();
        float maxV = icon.getMaxV();
        float u0 = icon.getInterpolatedU(7.0);
        float v0 = icon.getInterpolatedV(6.0);
        float u1 = icon.getInterpolatedU(9.0);
        float v1 = icon.getInterpolatedV(8.0);
        float u2 = icon.getInterpolatedU(7.0);
        float v2 = icon.getInterpolatedV(13.0);
        float u3 = icon.getInterpolatedU(9.0);
        float v3 = icon.getInterpolatedV(15.0);
        x += 0.5;
        z += 0.5;
        double startX = x - 0.5;
        double endX = x + 0.5;
        double startZ = z - 0.5;
        double endZ = z + 0.5;
        double xOff = 0.0625;
        double zOff = 0.625;
        tess.addTexturedVertex(x + u * (1.0 - zOff) - xOff, y + zOff, z + v * (1.0 - zOff) - xOff, u0, v0);
        tess.addTexturedVertex(x + u * (1.0 - zOff) - xOff, y + zOff, z + v * (1.0 - zOff) + xOff, u0, v1);
        tess.addTexturedVertex(x + u * (1.0 - zOff) + xOff, y + zOff, z + v * (1.0 - zOff) + xOff, u1, v1);
        tess.addTexturedVertex(x + u * (1.0 - zOff) + xOff, y + zOff, z + v * (1.0 - zOff) - xOff, u1, v0);
        tess.addTexturedVertex(x + xOff + u, y, z - xOff + v, u3, v2);
        tess.addTexturedVertex(x + xOff + u, y, z + xOff + v, u3, v3);
        tess.addTexturedVertex(x - xOff + u, y, z + xOff + v, u2, v3);
        tess.addTexturedVertex(x - xOff + u, y, z - xOff + v, u2, v2);
        tess.addTexturedVertex(x - xOff, y + 1.0, startZ, minU, minV);
        tess.addTexturedVertex(x - xOff + u, y + 0.0, startZ + v, minU, maxV);
        tess.addTexturedVertex(x - xOff + u, y + 0.0, endZ + v, maxU, maxV);
        tess.addTexturedVertex(x - xOff, y + 1.0, endZ, maxU, minV);
        tess.addTexturedVertex(x + xOff, y + 1.0, endZ, minU, minV);
        tess.addTexturedVertex(x + u + xOff, y + 0.0, endZ + v, minU, maxV);
        tess.addTexturedVertex(x + u + xOff, y + 0.0, startZ + v, maxU, maxV);
        tess.addTexturedVertex(x + xOff, y + 1.0, startZ, maxU, minV);
        tess.addTexturedVertex(startX, y + 1.0, z + xOff, minU, minV);
        tess.addTexturedVertex(startX + u, y + 0.0, z + xOff + v, minU, maxV);
        tess.addTexturedVertex(endX + u, y + 0.0, z + xOff + v, maxU, maxV);
        tess.addTexturedVertex(endX, y + 1.0, z + xOff, maxU, minV);
        tess.addTexturedVertex(endX, y + 1.0, z - xOff, minU, minV);
        tess.addTexturedVertex(endX + u, y + 0.0, z - xOff + v, minU, maxV);
        tess.addTexturedVertex(startX + u, y + 0.0, z - xOff + v, maxU, maxV);
        tess.addTexturedVertex(startX, y + 1.0, z - xOff, maxU, minV);
    }

    @Override
    public void renderInGUI(Block block, int dat) {
        Tessellator tess = Tessellator.instance;
        tess.begin();
        tess.setNormals(0.0F, -1.0F, 0.0F);
        this.renderTorch(block, -0.5, -0.5, -0.5, 0.0, 0.0);
        tess.draw();
    }

    @Override
    public boolean renderItemIn3d() {
        return false;
    }
}
