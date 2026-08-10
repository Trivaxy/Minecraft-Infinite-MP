package net.minecraft.client.render.blocks.model.furniture;

import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.blocks.model.BlockModel;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.decoration.FlowerPot;
import net.minecraft.game.texture.Icon;

public class FlowerPotModel extends BlockModel {
    @Override
    public boolean render(Block block, int x, int y, int z) {
        FlowerPot pot = (FlowerPot)block;
        Tessellator tess = Tessellator.instance;
        Icon icon = pot.getIcon(0, 0);
        float offset = 0.1865F;
        boolean render = this.renderStandardBlock(pot, x, y, z);
        this.applyColorCorrection(block, x, y, z, 1.0F);
        this.renderFace(pot, x - 0.5 + offset, y, z, icon, BlockModel.Face.EAST, true);
        this.renderFace(pot, x + 0.5 - offset, y, z, icon, BlockModel.Face.WEST, true);
        this.renderFace(pot, x, y, z - 0.5 + offset, icon, BlockModel.Face.SOUTH, true);
        this.renderFace(pot, x, y, z + 0.5 - offset, icon, BlockModel.Face.NORTH, true);
        this.renderFace(pot, x, y - 0.5 + offset + 0.1875, z, BlockList.dirt.getIcon(0, 0), BlockModel.Face.TOP, true);
        int metadata = this.world.getBlockMetadata(x, y, z);
        tess.addTranslation(0.0F, 0.25F, 0.0F);
        if (metadata < 128) {
            this.renderPlant(metadata, x, y, z);
        } else {
            this.drawPlant(BlockList.sapling, metadata - 128, x, y, z);
        }

        tess.addTranslation(0.0F, -0.25F, 0.0F);
        return render;
    }

    private boolean renderPlant(int metadata, int x, int y, int z) {
        switch (metadata) {
            case 1:
                return this.drawPlant(BlockList.rose, 0, x, y, z);
            case 2:
                return this.drawPlant(BlockList.dandelion, 0, x, y, z);
            case 3:
                return this.drawPlant(BlockList.lilac, 0, x, y, z);
            case 4:
                return this.drawPlant(BlockList.petunia, 0, x, y, z);
            case 5:
                return this.drawPlant(BlockList.mushroomRed, 0, x, y, z);
            case 6:
                return this.drawPlant(BlockList.mushroomBrown, 0, x, y, z);
            case 7:
                return this.drawPlant(BlockList.mushroomGlowing, 0, x, y, z);
            case 8:
                return this.drawPlant(BlockList.berryBush, 0, x, y, z);
            case 9:
                return this.drawPlant(BlockList.berryBush, 1, x, y, z);
            case 10:
                return this.drawPlant(BlockList.witherRose, 0, x, y, z);
            case 11:
                return this.drawPlant(BlockList.fireFlower, 0, x, y, z);
            case 12:
                return this.drawPlant(BlockList.mushroomCrimson, 0, x, y, z);
            case 13:
                return this.drawPlant(BlockList.mushroomNylium, 0, x, y, z);
            case 14:
                return this.drawPlant(BlockList.tulip, 0, x, y, z);
            case 15:
                return this.drawPlant(BlockList.daisy, 0, x, y, z);
            case 16:
                return this.drawPlant(BlockList.pinkTulip, 0, x, y, z);
            case 17:
                return this.drawPlant(BlockList.whiteTulip, 0, x, y, z);
            case 18:
                return this.drawPlant(BlockList.paeonia, 0, x, y, z);
            case 19:
                return this.renderBlock(BlockList.cactus, x, y, z, 0.125F);
            case 20:
                return this.renderBlock(BlockList.bamboo, x, y, z, 0.0625F);
            case 21:
                return this.drawPlant(BlockList.deadBush, 0, x, y, z);
            case 22:
                return this.drawPlant(BlockList.tallGrass, 7, x, y, z);
            case 23:
                return this.drawPlant(BlockList.tallGrass, 9, x, y, z);
            case 24:
                return this.drawPlant(BlockList.tallGrass, 11, x, y, z);
            case 25:
                return this.drawPlant(BlockList.glowGrass, 1, x, y, z);
            default:
                return false;
        }
    }

    private boolean renderBlock(Block block, int x, int y, int z, float offset) {
        double minX = block.minX;
        double minY = block.minY;
        double minZ = block.minZ;
        double maxX = block.maxX;
        double maxY = block.maxY;
        double maxZ = block.maxZ;
        block.setBounds(0.5F - offset, 0.0, 0.5F - offset, 0.5F + offset, 0.75, 0.5F + offset);
        boolean render = this.renderStandardBlock(block, x, y, z);
        block.setBounds(minX, minY, minZ, maxX, maxY, maxZ);
        return render;
    }

    private boolean drawPlant(Block plant, int meta, int x, int y, int z) {
        this.drawCrossedSquares(plant, meta, x, y, z, 0.75);
        return true;
    }

    public void drawCrossedSquares(Block block, int metadata, double x, double y, double z, double offset) {
        Tessellator tess = Tessellator.instance;
        Icon icon = block.getIcon(0, metadata);
        if (this.hasOverrideTexture()) {
            icon = destroyBlockIcon;
        }

        float minU = icon.getMinU();
        float minV = icon.getMinV();
        float maxU = icon.getMaxU();
        float maxV = icon.getMaxV();
        double finalOffset = 0.45 * offset;
        double startX = x + 0.5 - finalOffset;
        double endX = x + 0.5 + finalOffset;
        double startZ = z + 0.5 - finalOffset;
        double endZ = z + 0.5 + finalOffset;
        tess.addTexturedVertex(startX, y + offset, startZ, minU, minV);
        tess.addTexturedVertex(startX, y + 0.0, startZ, minU, maxV);
        tess.addTexturedVertex(endX, y + 0.0, endZ, maxU, maxV);
        tess.addTexturedVertex(endX, y + offset, endZ, maxU, minV);
        tess.addTexturedVertex(endX, y + offset, endZ, minU, minV);
        tess.addTexturedVertex(endX, y + 0.0, endZ, minU, maxV);
        tess.addTexturedVertex(startX, y + 0.0, startZ, maxU, maxV);
        tess.addTexturedVertex(startX, y + offset, startZ, maxU, minV);
        tess.addTexturedVertex(startX, y + offset, endZ, minU, minV);
        tess.addTexturedVertex(startX, y + 0.0, endZ, minU, maxV);
        tess.addTexturedVertex(endX, y + 0.0, startZ, maxU, maxV);
        tess.addTexturedVertex(endX, y + offset, startZ, maxU, minV);
        tess.addTexturedVertex(endX, y + offset, startZ, minU, minV);
        tess.addTexturedVertex(endX, y + 0.0, startZ, minU, maxV);
        tess.addTexturedVertex(startX, y + 0.0, endZ, maxU, maxV);
        tess.addTexturedVertex(startX, y + offset, endZ, maxU, minV);
    }
}
