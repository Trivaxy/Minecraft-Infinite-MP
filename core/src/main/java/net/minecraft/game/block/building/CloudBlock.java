package net.minecraft.game.block.building;

import net.minecraft.game.block.material.Material;
import net.minecraft.game.block.misc.TransparentBlock;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldSource;

public class CloudBlock extends TransparentBlock {
    public CloudBlock(int blockid, Material material) {
        super(blockid, material, false);
    }

    @Override
    public int renderPass() {
        return 1;
    }

    @Override
    public BoundingBox getCollisionBox(World world, int xCoord, int yCoord, int zCoord) {
        return new BoundingBox(xCoord + this.minX, yCoord + this.minY, zCoord + this.minZ, xCoord + this.maxX, yCoord + 0.1F, zCoord + this.maxZ);
    }

    @Override
    public void onEntityCollision(Entity entity, World world, int x, int y, int z) {
        entity.motionX *= 0.2;
        entity.motionZ *= 0.2;
        entity.fallDistance = 0.0F;
    }

    @Override
    public boolean blocksPathfinder(WorldSource world, int x, int y, int z) {
        return false;
    }
}
