package net.minecraft.client.render.blocks.model.plants;

import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.blocks.model.BlockModel;
import net.minecraft.game.block.Block;
import net.minecraft.game.texture.Icon;

public class CropsModel extends BlockModel {
    @Override
    public boolean render(Block block, int x, int y, int z) {
        this.applyColorCorrection(block, x, y, z, 1.0F);
        this.renderCrops(block, this.world.getBlockMetadata(x, y, z), x, y - 0.0625F, z);
        return true;
    }

    public void renderCrops(Block block, int state, double x, double y, double z) {
        Tessellator tess = Tessellator.instance;
        Icon icon = block.getIcon(0, state);
        if (this.hasOverrideTexture()) {
            icon = this.textureOverride;
        }

        float minU = icon.getMinU();
        float maxU = icon.getMaxU();
        float minV = icon.getMinV();
        float maxV = icon.getMaxV();
        double startX = x + 0.5 - 0.25;
        double endX = x + 0.5 + 0.25;
        double startZ = z + 0.5 - 0.5;
        double endZ = z + 0.5 + 0.5;
        tess.addTexturedVertex(startX, y + 1.0, startZ, minU, minV);
        tess.addTexturedVertex(startX, y + 0.0, startZ, minU, maxV);
        tess.addTexturedVertex(startX, y + 0.0, endZ, maxU, maxV);
        tess.addTexturedVertex(startX, y + 1.0, endZ, maxU, minV);
        tess.addTexturedVertex(startX, y + 1.0, endZ, minU, minV);
        tess.addTexturedVertex(startX, y + 0.0, endZ, minU, maxV);
        tess.addTexturedVertex(startX, y + 0.0, startZ, maxU, maxV);
        tess.addTexturedVertex(startX, y + 1.0, startZ, maxU, minV);
        tess.addTexturedVertex(endX, y + 1.0, endZ, minU, minV);
        tess.addTexturedVertex(endX, y + 0.0, endZ, minU, maxV);
        tess.addTexturedVertex(endX, y + 0.0, startZ, maxU, maxV);
        tess.addTexturedVertex(endX, y + 1.0, startZ, maxU, minV);
        tess.addTexturedVertex(endX, y + 1.0, startZ, minU, minV);
        tess.addTexturedVertex(endX, y + 0.0, startZ, minU, maxV);
        tess.addTexturedVertex(endX, y + 0.0, endZ, maxU, maxV);
        tess.addTexturedVertex(endX, y + 1.0, endZ, maxU, minV);
        startX = x + 0.5 - 0.5;
        endX = x + 0.5 + 0.5;
        startZ = z + 0.5 - 0.25;
        endZ = z + 0.5 + 0.25;
        tess.addTexturedVertex(startX, y + 1.0, startZ, minU, minV);
        tess.addTexturedVertex(startX, y + 0.0, startZ, minU, maxV);
        tess.addTexturedVertex(endX, y + 0.0, startZ, maxU, maxV);
        tess.addTexturedVertex(endX, y + 1.0, startZ, maxU, minV);
        tess.addTexturedVertex(endX, y + 1.0, startZ, minU, minV);
        tess.addTexturedVertex(endX, y + 0.0, startZ, minU, maxV);
        tess.addTexturedVertex(startX, y + 0.0, startZ, maxU, maxV);
        tess.addTexturedVertex(startX, y + 1.0, startZ, maxU, minV);
        tess.addTexturedVertex(endX, y + 1.0, endZ, minU, minV);
        tess.addTexturedVertex(endX, y + 0.0, endZ, minU, maxV);
        tess.addTexturedVertex(startX, y + 0.0, endZ, maxU, maxV);
        tess.addTexturedVertex(startX, y + 1.0, endZ, maxU, minV);
        tess.addTexturedVertex(startX, y + 1.0, endZ, minU, minV);
        tess.addTexturedVertex(startX, y + 0.0, endZ, minU, maxV);
        tess.addTexturedVertex(endX, y + 0.0, endZ, maxU, maxV);
        tess.addTexturedVertex(endX, y + 1.0, endZ, maxU, minV);
    }

    @Override
    public void renderInGUI(Block block, int dat) {
        Tessellator tess = Tessellator.instance;
        tess.begin();
        tess.setNormals(0.0F, -1.0F, 0.0F);
        this.renderCrops(block, -1, -0.5, -0.5, -0.5);
        tess.draw();
    }

    @Override
    public boolean renderItemIn3d() {
        return false;
    }
}
