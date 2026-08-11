package net.minecraft.game.block.building;

import java.util.Random;
import net.minecraft.game.ParticleType;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.world.World;

public class Magma extends Block {
    public Magma(int blockID, Material material) {
        super(blockID, material);
    }

    @Override
    public final void spawnParticles(World world, int xCoord, int yCoord, int zCoord, Random random) {
        byte id = (byte)world.getBlockId(xCoord, yCoord + 1, zCoord);
        double xOffset = xCoord + random.nextDouble();
        double yOffset = (double)yCoord + (float)this.maxY;
        double zOffset = zCoord + random.nextDouble();
        if (id == 0) {
            world.spawnParticle(ParticleType.SMOKE, xOffset, yOffset, zOffset, 0.0, 0.0, 0.0);
        }

        if (id == BlockList.waterStill.id || id == BlockList.waterMoving.id) {
            world.spawnParticle(ParticleType.BUBBLECOLUMN, xOffset, yOffset, zOffset, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public void onEntityCollision(Entity entity, World world, int x, int y, int z) {
        if (!(entity instanceof DroppedItem)) {
            entity.dealFireDamage(1);
        }
    }
}
