package net.minecraft.game.block.decoration;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.building.Slab;
import net.minecraft.game.block.building.Stairs;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldSource;

public class Rope extends Block {
    public Rope(int blockID, Material material) {
        super(blockID, material);
        this.setBounds(0.375, 0.0, 0.375, 0.625, 1.0, 0.625);
    }

    @Override
    public boolean placeableAt(World world, int x, int y, int z) {
        return this.canStay(world, x, y, z);
    }

    @Override
    public void onNeighborChange(World world, int x, int y, int z, int id) {
        if (!this.canStay(world, x, y, z)) {
            this.dropBlock(world, x, y, z, world.getBlockMetadata(x, y, z));
            world.playEffect(2001, x, y, z, world.getBlockMetadata(x, y, z) << 12 | this.id);
            world.setBlockWithNotify(x, y, z, 0);
        }
    }

    @Override
    public boolean canStay(World world, int x, int y, int z) {
        Block blockAbove = world.getBlock(x, y + 1, z);
        int meta = world.getBlockMetadata(x, y + 1, z);
        return blockAbove == BlockList.rope
            || world.isSolid(x, y + 1, z)
            || blockAbove instanceof Stairs
            || blockAbove instanceof Wall
            || blockAbove instanceof Fence
            || blockAbove instanceof Slab && meta < 128;
    }

    @Override
    public final BoundingBox getCollisionBox(World world, int xCoord, int yCoord, int zCoord) {
        return null;
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public boolean particles3D() {
        return false;
    }

    @Override
    public boolean renderFace(WorldSource world, int x, int y, int z, int side) {
        return world.getBlock(x, y, z) != this || side != 0 && side != 1 ? super.renderFace(world, x, y, z, side) : false;
    }
}
