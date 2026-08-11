package net.minecraft.game.block.decoration;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.building.Slab;
import net.minecraft.game.block.building.Stairs;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;

public class Lantern extends Block {
    public Icon altIcon;

    public Lantern(int id) {
        super(id, MaterialList.metal);
        this.setTickable(true);
    }

    @Override
    public void registerIcons(IconRegister r) {
        super.registerIcons(r);
        this.altIcon = r.registerIcon("lanternSide");
    }

    @Override
    public boolean particles3D() {
        return false;
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.LANTERN;
    }

    @Override
    public boolean placeableAt(World world, int x, int y, int z) {
        Block bottomBlock = world.getBlock(x, y - 1, z);
        Block topBlock = world.getBlock(x, y + 1, z);
        return bottomBlock != BlockList.table
                && !(bottomBlock instanceof Wall)
                && bottomBlock != BlockList.fence
                && topBlock != BlockList.chain
                && (!(bottomBlock instanceof Stairs) || (world.getBlockMetadata(x, y + 1, z) & 4) == 0)
                && (!(bottomBlock instanceof Slab) || world.getBlockMetadata(x, y + 1, z) < 128)
                && topBlock != BlockList.fence
                && topBlock != BlockList.rope
                && !(topBlock instanceof Wall)
                && (!(topBlock instanceof Stairs) || (world.getBlockMetadata(x, y + 1, z) & 4) != 0)
                && (!(topBlock instanceof Slab) || world.getBlockMetadata(x, y + 1, z) >= 128)
            ? world.isBlockFull(x, y - 1, z) || world.isBlockFull(x, y + 1, z)
            : true;
    }

    @Override
    public void onPlaced(World world, Player player, int x, int y, int z, int facing) {
        Block topBlock = BlockList.blocks[world.getBlockId(x, y + 1, z)];
        if (world.isBlockFull(x, y + 1, z)
            || topBlock == BlockList.fence
            || topBlock instanceof Wall
            || topBlock == BlockList.rope
            || topBlock == BlockList.chain
            || topBlock instanceof Stairs && (world.getBlockMetadata(x, y + 1, z) & 4) == 0
            || topBlock instanceof Slab && world.getBlockMetadata(x, y + 1, z) < 128) {
            world.setBlockMetadata(x, y, z, 1);
        }
    }

    @Override
    public void onNeighborChange(World world, int x, int y, int z, int id) {
        Block bottomBlock = world.getBlock(x, y - 1, z);
        Block topBlock = world.getBlock(x, y + 1, z);
        boolean drop = true;
        if (world.isBlockFull(x, y - 1, z)
            || bottomBlock == BlockList.table
            || bottomBlock instanceof Wall
            || bottomBlock == BlockList.fence
            || topBlock == BlockList.chain
            || bottomBlock instanceof Stairs && (world.getBlockMetadata(x, y + 1, z) & 4) != 0
            || bottomBlock instanceof Slab && world.getBlockMetadata(x, y + 1, z) >= 128) {
            world.setBlockMetadata(x, y, z, 0);
            drop = false;
        }

        if (world.isBlockFull(x, y + 1, z)
            || topBlock == BlockList.fence
            || topBlock == BlockList.rope
            || topBlock instanceof Wall
            || topBlock instanceof Stairs && (world.getBlockMetadata(x, y + 1, z) & 4) == 0
            || topBlock instanceof Slab && world.getBlockMetadata(x, y + 1, z) < 128) {
            world.setBlockMetadata(x, y, z, 1);
            drop = false;
        }

        if (drop) {
            this.dropBlock(world, x, y, z, world.getBlockMetadata(x, y, z));
            world.setBlockWithNotify(x, y, z, 0);
            world.playEffect(2001, x, y, z, world.getBlockMetadata(x, y, z) << 12 | this.id);
        }
    }

    @Override
    public void setStateBounds(World world, int x, int y, int z) {
        byte metadata = (byte)world.getBlockMetadata(x, y, z);
        if (metadata == 1) {
            this.setBounds(0.2F, 0.375, 0.2F, 0.8F, 1.0, 0.8F);
        } else {
            this.setBounds(0.2F, 0.0, 0.2F, 0.8F, 0.625, 0.8F);
        }
    }
}
