package net.minecraft.client.render.blocks.model.furniture;

import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.blocks.model.BlockModel;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.utility.Composter;
import net.minecraft.game.texture.Icon;
import org.lwjgl.opengl.GL11;

public class ComposterModel extends BlockModel {
    @Override
    public boolean render(Block block, int x, int y, int z) {
        Composter a = (Composter)block;
        boolean render = this.renderStandardBlock(a, x, y, z);
        Icon sideTexture = a.getSideIcon(2);
        double offset = 0.125;
        this.applyColorCorrection(block, x, y, z, 0.6F);
        this.renderFace(a, x - 1.0 + offset, y, z, sideTexture, BlockModel.Face.EAST, true);
        this.renderFace(a, x + 1.0 - offset, y, z, sideTexture, BlockModel.Face.WEST, true);
        this.applyColorCorrection(block, x, y, z, 0.8F);
        this.renderFace(a, x, y, z - 1.0 + offset, sideTexture, BlockModel.Face.SOUTH, true);
        this.renderFace(a, x, y, z + 1.0 - offset, sideTexture, BlockModel.Face.NORTH, true);
        Icon inner = a.bottom;
        this.applyColorCorrection(block, x, y, z, 1.0F);
        this.renderFace(a, x, y - 1.0 + 0.25, z, inner, BlockModel.Face.TOP, true);
        this.renderFace(a, x, y + 1.0 - 0.75, z, inner, BlockModel.Face.BOTTOM, true);
        Icon compost = a.juice;
        int compostLvl = this.world.getBlockMetadata(x, y, z) & 3;
        if (compostLvl > 0) {
            this.renderFace(a, x, y - 1.0 + (6.0 + compostLvl * 3.0) / 16.0, z, compost, BlockModel.Face.TOP, true);
        }

        return render;
    }

    @Override
    public void renderInGUI(Block block, int dat) {
        Tessellator tess = Tessellator.instance;
        block.forceBounds(dat);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        tess.begin();
        this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(0, dat), BlockModel.Face.BOTTOM, false);
        this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(1, dat), BlockModel.Face.TOP, false);
        this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(2, dat), BlockModel.Face.NORTH, false);
        this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(3, dat), BlockModel.Face.SOUTH, false);
        this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(4, dat), BlockModel.Face.WEST, false);
        this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(5, dat), BlockModel.Face.EAST, false);
        tess.draw();
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    }
}
