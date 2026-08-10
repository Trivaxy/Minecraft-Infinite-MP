package net.minecraft.client.render.blocks.model.plants;

import net.minecraft.client.render.blocks.model.BlockModel;
import net.minecraft.game.block.Block;

public class LogModel extends BlockModel {
    @Override
    public boolean render(Block block, int x, int y, int z) {
        int meta = this.world.getBlockMetadata(x, y, z);
        int rotation = meta & 240;
        if (rotation >= 64 && rotation < 128) {
            this.rotationNorth = 1;
            this.rotationSouth = 1;
            this.rotationTop = 1;
            this.rotationBottom = 1;
        } else if (rotation >= 128) {
            this.rotationEast = 1;
            this.rotationWest = 1;
        }

        boolean render = this.renderStandardBlock(block, x, y, z);
        this.rotationEast = 0;
        this.rotationNorth = 0;
        this.rotationSouth = 0;
        this.rotationWest = 0;
        this.rotationTop = 0;
        this.rotationBottom = 0;
        return render;
    }
}
