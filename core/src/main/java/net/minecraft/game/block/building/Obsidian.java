package net.minecraft.game.block.building;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.item.ItemStack;

public class Obsidian extends Block {
    public Obsidian(int blockid, Material material) {
        super(blockid, material);
    }

    @Override
    public ItemStack getCarverContents(int id, int dmg) {
        switch (id) {
            case 0:
                return new ItemStack(BlockList.obsidianBrick.id, 1, 0);
            default:
                return null;
        }
    }
}
