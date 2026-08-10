package net.minecraft.client.render.blocks.model.machines;

import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.blocks.model.BlockModel;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.machines.Piston;
import net.minecraft.game.block.machines.PistonHead;
import net.minecraft.game.texture.Icon;

public class PistonHeadModel extends BlockModel {
    public boolean isGold = false;

    @Override
    public boolean render(Block block, int x, int y, int z) {
        return this.renderPistonHead(block, x, y, z, true);
    }

    public boolean renderPistonHead(Block block, int x, int y, int z, boolean extended) {
        int meta = this.world.getBlockMetadata(x, y, z);
        int facing = PistonHead.getDirectionMeta(meta);
        int brightness = block.getBrightness(this.world, x, y, z);
        boolean sticky = (meta & 8) != 0;
        double offset = extended ? 1.0 : 0.5;
        double maxU = extended ? 16.0 : 8.0;
        boolean render = false;
        switch (facing) {
            case 0:
                this.rotationNorth = 2;
                this.rotationSouth = 2;
                this.rotationEast = 2;
                this.rotationWest = 2;
                block.setBounds(0.0, 0.0, 0.0, 1.0, 0.25, 1.0);
                render = this.renderStandardBlock(block, x, y, z);
                this.renderPistonRodUD(x + 0.375, x + 0.625, y + 0.25, y + 0.25 + offset, z + 0.625, z + 0.625, 0.8F, brightness, maxU, sticky);
                this.renderPistonRodUD(x + 0.625, x + 0.375, y + 0.25, y + 0.25 + offset, z + 0.375, z + 0.375, 0.8F, brightness, maxU, sticky);
                this.renderPistonRodUD(x + 0.375, x + 0.375, y + 0.25, y + 0.25 + offset, z + 0.375, z + 0.625, 0.6F, brightness, maxU, sticky);
                this.renderPistonRodUD(x + 0.625, x + 0.625, y + 0.25, y + 0.25 + offset, z + 0.625, z + 0.375, 0.6F, brightness, maxU, sticky);
                break;
            case 1:
                block.setBounds(0.0, 0.75, 0.0, 1.0, 1.0, 1.0);
                render = this.renderStandardBlock(block, x, y, z);
                this.renderPistonRodUD(x + 0.375, x + 0.625, y - 0.25 + 1.0 - offset, y - 0.25 + 1.0, z + 0.625, z + 0.625, 0.8F, brightness, maxU, sticky);
                this.renderPistonRodUD(x + 0.625, x + 0.375, y - 0.25 + 1.0 - offset, y - 0.25 + 1.0, z + 0.375, z + 0.375, 0.8F, brightness, maxU, sticky);
                this.renderPistonRodUD(x + 0.375, x + 0.375, y - 0.25 + 1.0 - offset, y - 0.25 + 1.0, z + 0.375, z + 0.625, 0.6F, brightness, maxU, sticky);
                this.renderPistonRodUD(x + 0.625, x + 0.625, y - 0.25 + 1.0 - offset, y - 0.25 + 1.0, z + 0.625, z + 0.375, 0.6F, brightness, maxU, sticky);
                break;
            case 2:
                this.rotationEast = 3;
                this.rotationWest = 1;
                this.rotationBottom = 2;
                block.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 0.25);
                render = this.renderStandardBlock(block, x, y, z);
                this.renderPistonRodSN(x + 0.375, x + 0.375, y + 0.625, y + 0.375, z + 0.25, z + 0.25 + offset, 0.6F, brightness, maxU, sticky);
                this.renderPistonRodSN(x + 0.625, x + 0.625, y + 0.375, y + 0.625, z + 0.25, z + 0.25 + offset, 0.6F, brightness, maxU, sticky);
                this.renderPistonRodSN(x + 0.375, x + 0.625, y + 0.375, y + 0.375, z + 0.25, z + 0.25 + offset, 0.5F, brightness, maxU, sticky);
                this.renderPistonRodSN(x + 0.625, x + 0.375, y + 0.625, y + 0.625, z + 0.25, z + 0.25 + offset, 1.0F, brightness, maxU, sticky);
                break;
            case 3:
                this.rotationEast = 1;
                this.rotationWest = 3;
                this.rotationTop = 2;
                block.setBounds(0.0, 0.0, 0.75, 1.0, 1.0, 1.0);
                render = this.renderStandardBlock(block, x, y, z);
                this.renderPistonRodSN(x + 0.375, x + 0.375, y + 0.625, y + 0.375, z - 0.25 + 1.0 - offset, z - 0.25 + 1.0, 0.6F, brightness, maxU, sticky);
                this.renderPistonRodSN(x + 0.625, x + 0.625, y + 0.375, y + 0.625, z - 0.25 + 1.0 - offset, z - 0.25 + 1.0, 0.6F, brightness, maxU, sticky);
                this.renderPistonRodSN(x + 0.375, x + 0.625, y + 0.375, y + 0.375, z - 0.25 + 1.0 - offset, z - 0.25 + 1.0, 0.5F, brightness, maxU, sticky);
                this.renderPistonRodSN(x + 0.625, x + 0.375, y + 0.625, y + 0.625, z - 0.25 + 1.0 - offset, z - 0.25 + 1.0, 1.0F, brightness, maxU, sticky);
                break;
            case 4:
                this.rotationNorth = 3;
                this.rotationSouth = 1;
                this.rotationTop = 3;
                this.rotationBottom = 3;
                block.setBounds(0.0, 0.0, 0.0, 0.25, 1.0, 1.0);
                render = this.renderStandardBlock(block, x, y, z);
                this.renderPistonRodEW(x + 0.25, x + 0.25 + offset, y + 0.375, y + 0.375, z + 0.625, z + 0.375, 0.5F, brightness, maxU, sticky);
                this.renderPistonRodEW(x + 0.25, x + 0.25 + offset, y + 0.625, y + 0.625, z + 0.375, z + 0.625, 1.0F, brightness, maxU, sticky);
                this.renderPistonRodEW(x + 0.25, x + 0.25 + offset, y + 0.375, y + 0.625, z + 0.375, z + 0.375, 0.6F, brightness, maxU, sticky);
                this.renderPistonRodEW(x + 0.25, x + 0.25 + offset, y + 0.625, y + 0.375, z + 0.625, z + 0.625, 0.6F, brightness, maxU, sticky);
                break;
            case 5:
                this.rotationNorth = 1;
                this.rotationSouth = 3;
                this.rotationTop = 1;
                this.rotationBottom = 1;
                block.setBounds(0.75, 0.0, 0.0, 1.0, 1.0, 1.0);
                render = this.renderStandardBlock(block, x, y, z);
                this.renderPistonRodEW(x - 0.25 + 1.0 - offset, x - 0.25 + 1.0, y + 0.375, y + 0.375, z + 0.625, z + 0.375, 0.5F, brightness, maxU, sticky);
                this.renderPistonRodEW(x - 0.25 + 1.0 - offset, x - 0.25 + 1.0, y + 0.625, y + 0.625, z + 0.375, z + 0.625, 1.0F, brightness, maxU, sticky);
                this.renderPistonRodEW(x - 0.25 + 1.0 - offset, x - 0.25 + 1.0, y + 0.375, y + 0.625, z + 0.375, z + 0.375, 0.6F, brightness, maxU, sticky);
                this.renderPistonRodEW(x - 0.25 + 1.0 - offset, x - 0.25 + 1.0, y + 0.625, y + 0.375, z + 0.625, z + 0.625, 0.6F, brightness, maxU, sticky);
        }

        this.rotationNorth = 0;
        this.rotationSouth = 0;
        this.rotationEast = 0;
        this.rotationWest = 0;
        this.rotationTop = 0;
        this.rotationBottom = 0;
        block.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        return render;
    }

    private void renderPistonRodUD(double x1, double x2, double y1, double y2, double z1, double z2, float color, int brightness, double offset, boolean sticky) {
        Icon icon = !sticky && !this.isGold ? Piston.registerIcon("pistonSide") : Piston.registerIcon("pistonGoldenSide");
        if (this.hasOverrideTexture()) {
            icon = this.textureOverride;
        }

        Tessellator tess = Tessellator.instance;
        float minU = icon.getMinU();
        float minV = icon.getMinV();
        float maxU = icon.getInterpolatedU(offset);
        float maxV = icon.getInterpolatedV(4.0);
        tess.setBrightness(brightness);
        tess.setColor(color, color, color);
        tess.addTexturedVertex(x1, y2, z1, maxU, minV);
        tess.addTexturedVertex(x1, y1, z1, minU, minV);
        tess.addTexturedVertex(x2, y1, z2, minU, maxV);
        tess.addTexturedVertex(x2, y2, z2, maxU, maxV);
    }

    private void renderPistonRodSN(double x1, double x2, double y1, double y2, double z1, double z2, float color, int brightness, double offset, boolean sticky) {
        Icon icon = !sticky && !this.isGold ? Piston.registerIcon("pistonSide") : Piston.registerIcon("pistonGoldenSide");
        if (this.hasOverrideTexture()) {
            icon = this.textureOverride;
        }

        Tessellator tess = Tessellator.instance;
        float minU = icon.getMinU();
        float minV = icon.getMinV();
        float maxU = icon.getInterpolatedU(offset);
        float maxV = icon.getInterpolatedV(4.0);
        tess.setBrightness(brightness);
        tess.setColor(color, color, color);
        tess.addTexturedVertex(x1, y1, z2, maxU, minV);
        tess.addTexturedVertex(x1, y1, z1, minU, minV);
        tess.addTexturedVertex(x2, y2, z1, minU, maxV);
        tess.addTexturedVertex(x2, y2, z2, maxU, maxV);
    }

    private void renderPistonRodEW(double x1, double x2, double y1, double y2, double z1, double z2, float color, int brightness, double offset, boolean sticky) {
        Icon icon = !sticky && !this.isGold ? Piston.registerIcon("pistonSide") : Piston.registerIcon("pistonGoldenSide");
        if (this.hasOverrideTexture()) {
            icon = this.textureOverride;
        }

        Tessellator tess = Tessellator.instance;
        float minU = icon.getMinU();
        float minV = icon.getMinV();
        float maxU = icon.getInterpolatedU(offset);
        float maxV = icon.getInterpolatedV(4.0);
        tess.setBrightness(brightness);
        tess.setColor(color, color, color);
        tess.addTexturedVertex(x2, y1, z1, maxU, minV);
        tess.addTexturedVertex(x1, y1, z1, minU, minV);
        tess.addTexturedVertex(x1, y2, z2, minU, maxV);
        tess.addTexturedVertex(x2, y2, z2, maxU, maxV);
    }
}
