package net.minecraft.client.render.blocks.model.furniture;

import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.blocks.model.BlockModel;
import net.minecraft.game.block.Block;

public class CandleModel extends BlockModel {
    @Override
    public boolean render(Block block, int x, int y, int z) {
        Tessellator tess = Tessellator.instance;
        int bright = block.getBrightness(this.world, x, y, z);
        boolean render = false;
        tess.setBrightness(bright);
        tess.setColor(1.0F, 1.0F, 1.0F);
        block.setBounds(0.44F, 0.0, 0.44F, 0.62F, 0.5, 0.62F);
        render = this.renderStandardBlock(block, x, y, z);
        block.setBounds(0.5, 0.6F, 0.5, 0.55F, 0.675F, 0.55F);
        tess.addTranslation(0.0F, -0.1F, 0.0F);
        render = this.renderStandardBlock(block, x, y, z);
        tess.addTranslation(0.0F, 0.1F, 0.0F);
        block.setBounds(0.44F, 0.0, 0.44F, 0.62F, 0.5, 0.62F);
        return render;
    }

    @Override
    public boolean renderItemIn3d() {
        return false;
    }
}
