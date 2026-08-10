package net.minecraft.client.render.blocks.model.plants;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;

public class WaterLilyModel extends LilyPadModel {
    @Override
    public boolean render(Block block, int x, int y, int z) {
        super.render(block, x, y, z);
        this.applyColorCorrection(block, x, y, z, 1.0F);
        this.renderCross(BlockList.waterLily, this.world.getBlockMetadata(x, y, z), x, y, z);
        return true;
    }

    @Override
    public boolean renderItemIn3d() {
        return false;
    }
}
