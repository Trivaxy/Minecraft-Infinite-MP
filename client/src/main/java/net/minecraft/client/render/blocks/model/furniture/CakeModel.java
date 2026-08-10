package net.minecraft.client.render.blocks.model.furniture;

import net.minecraft.client.render.blocks.model.BlockModel;
import net.minecraft.game.block.Block;

public class CakeModel extends BlockModel {
    @Override
    public boolean render(Block block, int x, int y, int z) {
        this.rotationTop = 1;
        boolean render = this.renderStandardBlock(block, x, y, z);
        this.rotationTop = 0;
        return render;
    }
}
