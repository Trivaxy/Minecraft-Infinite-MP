package net.minecraft.game.block.building;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.world.World;

public class PowderSnow extends Block {
    public PowderSnow(int blockid, Material material) {
        super(blockid, material);
    }

    @Override
    public BoundingBox getCollisionBox(World world, int xCoord, int yCoord, int zCoord) {
        return null;
    }

    @Override
    public void onEntityCollision(Entity entity, World world, int x, int y, int z) {
        entity.motionX *= 0.2;
        entity.motionY *= 0.2;
        entity.motionZ *= 0.2;
        entity.fallDistance = 0.0F;
    }
}
