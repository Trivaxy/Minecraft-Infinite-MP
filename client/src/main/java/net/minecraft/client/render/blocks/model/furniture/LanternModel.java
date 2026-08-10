package net.minecraft.client.render.blocks.model.furniture;

import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.blocks.model.BlockModel;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.decoration.Lantern;
import net.minecraft.game.texture.Icon;

public class LanternModel extends BlockModel {
    @Override
    public boolean render(Block block, int x, int y, int z) {
        Icon icon = block.blockIcon;
        if (this.textureOverride != null) {
            icon = this.textureOverride;
        }

        Tessellator tess = Tessellator.instance;
        int bright = block.getBrightness(this.world, x, y, z);
        tess.setBrightness(bright);
        tess.setColor(1.0F, 1.0F, 1.0F);
        double yd = 0.0;
        if (this.world.getBlockMetadata(x, y, z) == 1) {
            yd = 0.375;
        }

        block.setBounds(0.3125, 0.0, 0.375, 0.6875, 0.4375, 0.75);
        tess.addTranslation(0.0F, 0.0F, -0.0625F);
        this.renderFace(block, x, y + yd, z, icon, BlockModel.Face.BOTTOM, true);
        this.renderFace(block, x, y + yd, z, icon, BlockModel.Face.TOP, true);
        tess.addTranslation(0.0F, 0.0F, 0.0625F);
        block.setBounds(0.3125, 0.0, 0.3125, 0.6875, 0.4375, 0.6875);
        this.renderFace(block, x, y + yd, z, ((Lantern)block).altIcon, BlockModel.Face.NORTH, true);
        this.renderFace(block, x, y + yd, z, ((Lantern)block).altIcon, BlockModel.Face.SOUTH, true);
        this.renderFace(block, x, y + yd, z, ((Lantern)block).altIcon, BlockModel.Face.WEST, true);
        this.renderFace(block, x, y + yd, z, ((Lantern)block).altIcon, BlockModel.Face.EAST, true);
        if (this.world.getBlockMetadata(x, y, z) == 1) {
            tess.addTranslation(0.0F, 0.175F, 0.0F);
        } else {
            tess.addTranslation(0.0F, -0.2F, 0.0F);
        }

        block.setBounds(0.375, 0.625, 0.375, 0.625, 0.6875, 0.625);
        boolean render = this.renderStandardBlock(block, x, y, z);
        if (this.world.getBlockMetadata(x, y, z) == 1) {
            tess.addTranslation(0.0F, -0.175F, 0.0F);
        } else {
            tess.addTranslation(0.0F, 0.2F, 0.0F);
        }

        if (this.world.getBlockMetadata(x, y, z) == 1) {
            tess.addTranslation(0.375F, 0.865F, 0.375F);
        } else {
            tess.addTranslation(0.375F, 0.475F, 0.375F);
        }

        block.setBounds(0.0625, 0.0, 0.0625, 0.184375F, 0.125, 0.184375F);
        render = this.renderStandardBlock(block, x, y, z);
        if (this.world.getBlockMetadata(x, y, z) == 1) {
            tess.addTranslation(-0.375F, -0.865F, -0.375F);
        } else {
            tess.addTranslation(-0.375F, -0.475F, -0.375F);
        }

        block.modifyOutline(this.world, x, y, z);
        return render;
    }

    @Override
    public boolean renderItemIn3d() {
        return false;
    }
}
