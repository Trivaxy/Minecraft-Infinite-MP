package net.minecraft.game.block.building;

import net.minecraft.game.ParticleType;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;

public class SlowSand extends Block {
    public SlowSand(int blockid, Material material) {
        super(blockid, material);
    }

    @Override
    public BoundingBox getCollisionBox(World world, int x, int y, int z) {
        return BoundingBox.getBoundingBox(x, y, z, x + 1, y + 1 - 0.125, z + 1);
    }

    @Override
    public void onEntityWalking(World world, int xCoord, int yCoord, int zCoord, Entity entity) {
        if (entity.produceFootsteps) {
            world.spawnParticle(
                ParticleType.SOUL,
                xCoord + world.rand.nextDouble(),
                yCoord + this.maxY + 0.01,
                zCoord + world.rand.nextDouble(),
                entity.posX,
                entity.posY,
                entity.posZ
            );
        }
    }

    @Override
    public void onEntityCollision(Entity entity, World world, int x, int y, int z) {
        if (entity.canBeSlowed()) {
            entity.motionX *= 0.4;
            entity.motionZ *= 0.4;
        }
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.blockIcon = ir.registerIcon("sands/" + this.name);
    }
}
