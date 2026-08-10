package net.minecraft.game.block.misc;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.world.WorldSource;

public class TransparentBlock extends Block {
    protected boolean opacity;

    public TransparentBlock(int blockid, Material material, boolean op) {
        super(blockid, material);
        this.opacity = op;
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public boolean renderFace(WorldSource world, int x, int y, int z, int side) {
        int currID = world.getBlockId(x, y, z);
        return !this.opacity && currID == this.id ? false : super.renderFace(world, x, y, z, side);
    }
}
