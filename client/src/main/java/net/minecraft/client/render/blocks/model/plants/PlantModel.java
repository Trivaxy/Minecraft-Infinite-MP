package net.minecraft.client.render.blocks.model.plants;

import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.blocks.model.BlockModel;
import net.minecraft.game.block.Block;

public class PlantModel extends BlockModel {
    @Override
    public boolean render(Block block, int x, int y, int z) {
        this.applyColorCorrection(block, x, y, z, 1.0F);
        this.renderCross(block, this.world.getBlockMetadata(x, y, z), x, y, z);
        return true;
    }

    @Override
    public void renderInGUI(Block block, int dat) {
        Tessellator tess = Tessellator.instance;
        tess.begin();
        this.renderCross(block, -1, -0.5, -0.5, -0.5);
        tess.draw();
    }

    @Override
    public boolean renderItemIn3d() {
        return false;
    }
}
