package net.minecraft.client.render.blocks.model.furniture;

import net.minecraft.client.render.blocks.model.BlockModel;
import net.minecraft.game.block.Block;

public class BarrelModel extends BlockModel {
    @Override
    public boolean render(Block block, int x, int y, int z) {
        int meta = this.world.getBlockMetadata(x, y, z);
        if (meta == 4 || meta == 2) {
            this.rotationNorth = 1;
            this.rotationSouth = 1;
            this.rotationTop = 1;
            this.rotationBottom = 1;
        } else if (meta == 8 || meta == 6) {
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
