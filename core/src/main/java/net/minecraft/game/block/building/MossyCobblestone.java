package net.minecraft.game.block.building;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.item.ItemStack;

public class MossyCobblestone extends Block {
    public MossyCobblestone(int blockid, Material material) {
        super(blockid, material);
    }

    @Override
    public ItemStack getCarverContents(int id, int dmg) {
        switch (id) {
            case 0:
                return new ItemStack(BlockList.stairMoss.id, 2, 0);
            case 1:
                return new ItemStack(BlockList.slabMoss.id, 2, 0);
            case 2:
                return new ItemStack(BlockList.panelMoss.id, 2, 0);
            case 3:
                return new ItemStack(BlockList.wallMoss.id, 1, 0);
            default:
                return null;
        }
    }
}
