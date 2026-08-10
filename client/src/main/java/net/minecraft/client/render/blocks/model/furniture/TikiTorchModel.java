package net.minecraft.client.render.blocks.model.furniture;

import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.blocks.model.BlockModel;
import net.minecraft.game.block.Block;

public class TikiTorchModel extends BlockModel {
    @Override
    public boolean render(Block block, int x, int y, int z) {
        int methdata = this.world.getBlockMetadata(x, y, z);
        boolean render = false;
        Tessellator tess = Tessellator.instance;
        int bright = block.getBrightness(this.world, x, y, z);
        tess.setBrightness(bright);
        tess.setColor(1.0F, 1.0F, 1.0F);
        if (methdata == 1) {
            float offset = 0.06F;
            block.setBounds(0.5F - offset, 0.0, 0.5F - offset, 0.5F + offset, 0.92F, 0.5F + offset);
            render = this.renderStandardBlock(block, x, y, z);
            block.setBounds(0.375, 0.625, 0.375, 0.625, 0.875, 0.625);
            render = this.renderStandardBlock(block, x, y, z);
            block.setBounds(0.5F - offset, 0.0, 0.5F - offset, 0.5F + offset, 1.0, 0.5F + offset);
        } else {
            render = this.renderStandardBlock(block, x, y, z);
        }

        return render;
    }

    @Override
    public boolean renderItemIn3d() {
        return false;
    }
}
