package net.minecraft.client.render.blocks.model.furniture;

import net.minecraft.client.render.blocks.model.BlockModel;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.building.Panel;
import net.minecraft.game.world.WorldSource;

public class PanelModel extends BlockModel {
    @Override
    public boolean render(Block block, int x, int y, int z) {
        boolean render = this.renderStandardBlock(block, x, y, z);
        this.setCornerBounds(this.world, block, x, y, z);
        this.renderStandardBlock(block, x, y, z);
        return render;
    }

    public void setCornerBounds(WorldSource world, Block block, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z) & 3;
        if (meta == 3 && world.getBlock(x + 1, y, z) instanceof Panel) {
            int neighborMeta = world.getBlockMetadata(x + 1, y, z) & 3;
            if (neighborMeta == 1) {
                block.setBounds(0.5, 0.0, 0.0, 1.0, 1.0, 0.5);
            } else if (neighborMeta == 0) {
                block.setBounds(0.5, 0.0, 0.5, 1.0, 1.0, 1.0);
            }
        } else if (meta == 2 && world.getBlock(x - 1, y, z) instanceof Panel) {
            int neighborMeta = world.getBlockMetadata(x - 1, y, z) & 3;
            if (neighborMeta == 0) {
                block.setBounds(0.0, 0.0, 0.5, 0.5, 1.0, 1.0);
            } else if (neighborMeta == 1) {
                block.setBounds(0.0, 0.0, 0.0, 0.5, 1.0, 0.5);
            }
        } else if (meta == 0 && world.getBlock(x, y, z - 1) instanceof Panel) {
            int neighborMeta = world.getBlockMetadata(x, y, z - 1) & 3;
            if (neighborMeta == 3) {
                block.setBounds(0.0, 0.0, 0.0, 0.5, 1.0, 0.5);
            } else if (neighborMeta == 2) {
                block.setBounds(0.5, 0.0, 0.0, 1.0, 1.0, 0.5);
            }
        } else if (meta == 1 && world.getBlock(x, y, z + 1) instanceof Panel) {
            int neighborMeta = world.getBlockMetadata(x, y, z + 1) & 3;
            if (neighborMeta == 3) {
                block.setBounds(0.0, 0.0, 0.5, 0.5, 1.0, 1.0);
            } else if (neighborMeta == 2) {
                block.setBounds(0.5, 0.0, 0.5, 1.0, 1.0, 1.0);
            }
        }
    }
}
