package net.minecraft.game.block.decoration;

import java.util.Random;
import net.minecraft.game.ParticleType;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.building.Slab;
import net.minecraft.game.block.building.Stairs;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.world.World;

public class Candle extends Block {
    public Candle(int blockid) {
        super(blockid, MaterialList.circuits);
        this.setTickable(true);
        this.setBounds(0.44F, 0.0, 0.44F, 0.62F, 0.5, 0.62F);
    }

    @Override
    public BoundingBox getCollisionBox(World world, int xCoord, int yCoord, int zCoord) {
        return null;
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.CANDLE;
    }

    @Override
    public boolean placeableAt(World world, int xCoord, int yCoord, int zCoord) {
        Block blockBelow = world.getBlock(xCoord, yCoord - 1, zCoord);
        int meta = world.getBlockMetadata(xCoord, yCoord - 1, zCoord);
        return blockBelow != BlockList.fence
                && !(blockBelow instanceof Wall)
                && blockBelow != BlockList.table
                && (!(blockBelow instanceof Slab) || meta < 128)
                && (!(blockBelow instanceof Stairs) || (meta & 4) == 0)
            ? world.isBlockFull(xCoord, yCoord - 1, zCoord)
            : true;
    }

    @Override
    public void onNeighborChange(World world, int xCoord, int yCoord, int zCoord, int id) {
        if (!this.placeableAt(world, xCoord, yCoord, zCoord)) {
            this.dropBlock(world, xCoord, yCoord, zCoord, world.getBlockMetadata(xCoord, yCoord, zCoord));
            world.setBlockWithNotify(xCoord, yCoord, zCoord, 0);
            world.playEffect(2001, xCoord, yCoord, zCoord, world.getBlockMetadata(xCoord, yCoord, zCoord) << 12 | this.id);
        }
    }

    @Override
    public void spawnParticles(World world, int xCoord, int yCoord, int zCoord, Random random) {
        double xOffset = xCoord + 0.52;
        double yOffset = yCoord + 0.7;
        double zOffset = zCoord + 0.52;
        world.spawnParticle(ParticleType.SMOKE, xOffset, yOffset, zOffset, 0.0, 0.0, 0.0);
        world.spawnParticle(ParticleType.FLAME, xOffset, yOffset, zOffset, 0.0, 0.0, 0.0);
    }

    @Override
    public boolean particles3D() {
        return false;
    }
}
