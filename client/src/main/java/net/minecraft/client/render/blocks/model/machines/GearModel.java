package net.minecraft.client.render.blocks.model.machines;

import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.blocks.model.BlockModel;
import net.minecraft.game.block.Block;
import net.minecraft.game.texture.Icon;

public class GearModel extends BlockModel {
    @Override
    public boolean render(Block block, int x, int y, int z) {
        Tessellator tess = Tessellator.instance;
        Icon idle = block.getSideIcon(0);
        Icon spinClockwise = block.getSideIcon(1);
        Icon spinCounterClockwise = block.getSideIcon(2);
        if (this.hasOverrideTexture()) {
            idle = this.textureOverride;
            spinClockwise = this.textureOverride;
            spinCounterClockwise = this.textureOverride;
        }

        int brightness = block.getBrightness(this.world, x, y, z);
        tess.setBrightness(brightness);
        tess.setColor(brightness, brightness, brightness);
        boolean render = false;
        float minU = idle.getMinU();
        float maxU = idle.getMaxU();
        float minV = idle.getMinV();
        float maxV = idle.getMaxV();
        float spinMinU = spinClockwise.getMinU();
        float spinMaxU = spinClockwise.getMaxU();
        float spinMinV = spinClockwise.getMinV();
        float spinMaxV = spinClockwise.getMaxV();
        if ((x + y + z & 1) == 1) {
            minU = spinClockwise.getMinU();
            maxU = spinClockwise.getMaxU();
            minV = spinClockwise.getMinV();
            maxV = spinClockwise.getMaxV();
            spinMinU = idle.getMinU();
            spinMaxU = idle.getMaxU();
            spinMinV = idle.getMinV();
            spinMaxV = idle.getMaxV();
        }

        if (this.world.getBlockMetadata(x, y, z) == 0) {
            minU = spinCounterClockwise.getMinU();
            maxU = spinCounterClockwise.getMaxU();
            minV = spinCounterClockwise.getMinV();
            maxV = spinCounterClockwise.getMaxV();
            spinMinU = spinCounterClockwise.getMinU();
            spinMaxU = spinCounterClockwise.getMaxU();
            spinMinV = spinCounterClockwise.getMinV();
            spinMaxV = spinCounterClockwise.getMaxV();
        }

        double offsetY = 0.125;
        double offsetX = 0.05;
        if (this.world.isBlockFull(x - 1, y, z)) {
            tess.addTexturedVertex(x + offsetX, y + 1 + offsetY, z + 1 + offsetY, minU, minV);
            tess.addTexturedVertex(x + offsetX, y - offsetY, z + 1 + offsetY, minU, maxV);
            tess.addTexturedVertex(x + offsetX, y - offsetY, z - offsetY, maxU, maxV);
            tess.addTexturedVertex(x + offsetX, y + 1 + offsetY, z - offsetY, maxU, minV);
            render = true;
        }

        if (this.world.isBlockFull(x + 1, y, z)) {
            tess.addTexturedVertex(x + 1 - offsetX, y - offsetY, z + 1 + offsetY, maxU, maxV);
            tess.addTexturedVertex(x + 1 - offsetX, y + 1 + offsetY, z + 1 + offsetY, maxU, minV);
            tess.addTexturedVertex(x + 1 - offsetX, y + 1 + offsetY, z - offsetY, minU, minV);
            tess.addTexturedVertex(x + 1 - offsetX, y - offsetY, z - offsetY, minU, maxV);
            render = true;
        }

        if (this.world.isBlockFull(x, y, z - 1)) {
            tess.addTexturedVertex(x + 1 + offsetY, y - offsetY, z + offsetX, spinMaxU, spinMaxV);
            tess.addTexturedVertex(x + 1 + offsetY, y + 1 + offsetY, z + offsetX, spinMaxU, spinMinV);
            tess.addTexturedVertex(x - offsetY, y + 1 + offsetY, z + offsetX, spinMinU, spinMinV);
            tess.addTexturedVertex(x - offsetY, y - offsetY, z + offsetX, spinMinU, spinMaxV);
            render = true;
        }

        if (this.world.isBlockFull(x, y, z + 1)) {
            tess.addTexturedVertex(x + 1 + offsetY, y + 1 + offsetY, z + 1 - offsetX, spinMinU, spinMinV);
            tess.addTexturedVertex(x + 1 + offsetY, y - offsetY, z + 1 - offsetX, spinMinU, spinMaxV);
            tess.addTexturedVertex(x - offsetY, y - offsetY, z + 1 - offsetX, spinMaxU, spinMaxV);
            tess.addTexturedVertex(x - offsetY, y + 1 + offsetY, z + 1 - offsetX, spinMaxU, spinMinV);
            render = true;
        }

        if (this.world.isBlockFull(x, y + 1, z)) {
            tess.addTexturedVertex(x - offsetY, y + 1 - offsetX, z - offsetY, spinMinU, spinMinV);
            tess.addTexturedVertex(x + 1 + offsetY, y + 1 - offsetX, z - offsetY, spinMaxU, spinMinV);
            tess.addTexturedVertex(x + 1 + offsetY, y + 1 - offsetX, z + 1 + offsetY, spinMaxU, spinMaxV);
            tess.addTexturedVertex(x - offsetY, y + 1 - offsetX, z + 1 + offsetY, spinMinU, spinMaxV);
            render = true;
        }

        if (this.world.isBlockFull(x, y - 1, z)) {
            tess.addTexturedVertex(x - offsetY, y + offsetX, z + 1 + offsetY, spinMinU, spinMaxV);
            tess.addTexturedVertex(x + 1 + offsetY, y + offsetX, z + 1 + offsetY, spinMaxU, spinMaxV);
            tess.addTexturedVertex(x + 1 + offsetY, y + offsetX, z - offsetY, spinMaxU, spinMinV);
            tess.addTexturedVertex(x - offsetY, y + offsetX, z - offsetY, spinMinU, spinMinV);
            render = true;
        }

        return render;
    }

    @Override
    public boolean renderItemIn3d() {
        return false;
    }
}
