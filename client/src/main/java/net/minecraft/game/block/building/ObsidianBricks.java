package net.minecraft.game.block.building;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.item.ItemStack;

public class ObsidianBricks extends Block {
    public ObsidianBricks(int blockid) {
        super(blockid, MaterialList.rock);
    }

    @Override
    public ItemStack getCarverContents(int id, int dmg) {
        switch (id) {
            case 0:
                return new ItemStack(BlockList.stairObsidianBrick.id, 2, 0);
            case 1:
                return new ItemStack(BlockList.slabObsidian.id, 2, 0);
            case 2:
                return new ItemStack(BlockList.panelObsidian.id, 2, 0);
            case 3:
                return new ItemStack(BlockList.wallObsidian.id, 1, 0);
            default:
                return null;
        }
    }
}
