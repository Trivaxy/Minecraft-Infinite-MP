package net.minecraft.game.block.decoration;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.physics.TargetingSystem;
import net.minecraft.game.physics.Vector;
import net.minecraft.game.world.World;

public class Ladder extends Block {
    public Ladder(int blockid) {
        super(blockid, MaterialList.circuits);
    }

    @Override
    public BoundingBox getCollisionBox(World world, int xCoord, int yCoord, int zCoord) {
        this.setStateBounds(world, xCoord, yCoord, zCoord);
        return super.getCollisionBox(world, xCoord, yCoord, zCoord);
    }

    @Override
    public BoundingBox getOutline(World world, int xCoord, int yCoord, int zCoord) {
        this.setStateBounds(world, xCoord, yCoord, zCoord);
        return super.getOutline(world, xCoord, yCoord, zCoord);
    }

    @Override
    public TargetingSystem rayTrace(World world, int x, int y, int z, Vector source, Vector target) {
        this.setStateBounds(world, x, y, z);
        return super.rayTrace(world, x, y, z, source, target);
    }

    @Override
    public void setStateBounds(World world, int xCoord, int yCoord, int zCoord) {
        int meta = world.getBlockMetadata(xCoord, yCoord, zCoord);
        float offset = 0.125F;
        if (meta == 2) {
            this.setBounds(0.0, 0.0, 1.0F - offset, 1.0, 1.0, 1.0);
        }

        if (meta == 3) {
            this.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, offset);
        }

        if (meta == 4) {
            this.setBounds(1.0F - offset, 0.0, 0.0, 1.0, 1.0, 1.0);
        }

        if (meta == 5) {
            this.setBounds(0.0, 0.0, 0.0, offset, 1.0, 1.0);
        }
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.LADDER;
    }

    @Override
    public boolean placeableAt(World world, int xCoord, int yCoord, int zCoord) {
        return world.isBlockFull(xCoord - 1, yCoord, zCoord)
            ? true
            : (
                world.isBlockFull(xCoord + 1, yCoord, zCoord)
                    ? true
                    : (world.isBlockFull(xCoord, yCoord, zCoord - 1) ? true : world.isBlockFull(xCoord, yCoord, zCoord + 1))
            );
    }

    @Override
    public void onPlaced(World world, Player player, int xCoord, int yCoord, int zCoord, int side) {
        super.onPlaced(world, player, xCoord, yCoord, zCoord, side);
        int meta = world.getBlockMetadata(xCoord, yCoord, zCoord);
        if ((meta == 0 || side == 2) && world.isBlockFull(xCoord, yCoord, zCoord + 1)) {
            meta = 2;
        }

        if ((meta == 0 || side == 3) && world.isBlockFull(xCoord, yCoord, zCoord - 1)) {
            meta = 3;
        }

        if ((meta == 0 || side == 4) && world.isBlockFull(xCoord + 1, yCoord, zCoord)) {
            meta = 4;
        }

        if ((meta == 0 || side == 5) && world.isBlockFull(xCoord - 1, yCoord, zCoord)) {
            meta = 5;
        }

        world.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, meta);
    }

    @Override
    public void onNeighborChange(World world, int xCoord, int yCoord, int zCoord, int id) {
        int meta = world.getBlockMetadata(xCoord, yCoord, zCoord);
        boolean drop = false;
        if (meta == 2 && world.isBlockFull(xCoord, yCoord, zCoord + 1)) {
            drop = true;
        }

        if (meta == 3 && world.isBlockFull(xCoord, yCoord, zCoord - 1)) {
            drop = true;
        }

        if (meta == 4 && world.isBlockFull(xCoord + 1, yCoord, zCoord)) {
            drop = true;
        }

        if (meta == 5 && world.isBlockFull(xCoord - 1, yCoord, zCoord)) {
            drop = true;
        }

        if (!drop) {
            this.dropBlock(world, xCoord, yCoord, zCoord, meta);
            world.setBlockWithNotify(xCoord, yCoord, zCoord, 0);
        }
    }

    @Override
    public int quantityDropped(Random rand) {
        return 1;
    }

    @Override
    public boolean particles3D() {
        return false;
    }
}
