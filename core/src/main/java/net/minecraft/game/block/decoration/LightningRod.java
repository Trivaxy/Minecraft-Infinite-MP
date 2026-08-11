package net.minecraft.game.block.decoration;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.world.World;

public class LightningRod extends Block {
    public LightningRod(int blockID, Material material) {
        super(blockID, material);
        this.setBounds(0.41666666F, 0.0, 0.41666666F, 0.5833333F, 1.0, 0.5833333F);
    }

    @Override
    public boolean placeableAt(World world, int x, int y, int z) {
        if (world.isBlockFull(x - 1, y, z)) {
            return true;
        } else if (world.isBlockFull(x + 1, y, z)) {
            return true;
        } else if (world.isBlockFull(x, y - 1, z)) {
            return true;
        } else if (world.isBlockFull(x, y + 1, z)) {
            return true;
        } else {
            return world.isBlockFull(x, y, z - 1) ? true : world.isBlockFull(x, y, z + 1);
        }
    }

    @Override
    public void onNeighborChange(World world, int x, int y, int z, int id) {
        if (!this.placeableAt(world, x, y, z)) {
            this.dropBlock(world, x, y, z, 0);
            world.setBlockWithNotify(x, y, z, 0);
        }
    }

    @Override
    public void onPlaced(World world, Player player, int x, int y, int z, int side) {
        super.onPlaced(world, player, x, y, z, side);
        world.setBlockMetadata(x, y, z, side);
    }

    @Override
    public void setStateBounds(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        float min = 0.41666666F;
        float max = 0.5833333F;
        float minX = min;
        float minY = 0.0F;
        float minZ = min;
        float maxX = max;
        float maxY = 1.0F;
        float maxZ = max;
        if (meta == 2 || meta == 3) {
            minY = min;
            maxY = max;
            minZ = 0.0F;
            maxZ = 1.0F;
        }

        if (meta == 4 || meta == 5) {
            minY = min;
            maxY = max;
            minX = 0.0F;
            maxX = 1.0F;
        }

        this.setBounds(minX, minY, minZ, maxX, maxY, maxZ);
    }

    @Override
    public Icon getIcon(int side, int mdata) {
        return BlockList.blockCopper.getIcon(side, mdata);
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public final Block.RenderType getRenderType() {
        return Block.RenderType.ROD;
    }
}
