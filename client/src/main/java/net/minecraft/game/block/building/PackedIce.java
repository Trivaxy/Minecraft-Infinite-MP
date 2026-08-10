package net.minecraft.game.block.building;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.material.Material;

public class PackedIce extends Block {
    public PackedIce(int blockid, Material material) {
        super(blockid, material);
        this.slipperiness = 1.05F;
    }
}
