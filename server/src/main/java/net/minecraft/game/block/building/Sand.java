package net.minecraft.game.block.building;

import java.util.Random;
import net.minecraft.game.ParticleType;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.liquid.LiquidBlock;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.misc.FallingBlock;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;

public class Sand extends Block {
    public static boolean can_fall = true;

    public Sand(int blockid, Material mat) {
        super(blockid, mat);
    }

    @Override
    public int dropID(int blockid, Random random) {
        if (this.id == BlockList.susSand.id) {
            return BlockList.sand.id;
        } else if (this.id == BlockList.susGravel.id) {
            return random.nextInt(10) == 0 ? ItemList.flint.id : BlockList.gravel.id;
        } else {
            return this.id;
        }
    }

    @Override
    public BoundingBox getCollisionBox(World world, int xCoord, int yCoord, int zCoord) {
        return this.id == BlockList.quickSand.id ? null : super.getCollisionBox(world, xCoord, yCoord, zCoord);
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        if (can_fall) {
            this.tryToFall(world, x, y, z);
        }
    }

    @Override
    public void onNeighborChange(World world, int xCoord, int yCoord, int zCoord, int side) {
        if (can_fall) {
            this.tryToFall(world, xCoord, yCoord, zCoord);
        }
    }

    private void tryToFall(World world, int x, int y, int z) {
        if (!world.physicsDisabled()) {
            int id = world.getBlockId(x, y - 1, z);
            Block target = BlockList.blocks[id];
            if ((id == 0 || target instanceof LiquidBlock) && !world.isOnline) {
                world.spawnEntity(new FallingBlock(world, x + 0.5, y + 0.5, z + 0.5, this.id, world.getBlockMetadata(x, y, z)));
                world.setBlockWithNotify(x, y, z, 0);
            }
        }
    }

    @Override
    public void onEntityWalking(World world, int xCoord, int yCoord, int zCoord, Entity entity) {
        if (entity.produceFootsteps) {
            world.spawnParticle(
                ParticleType.FOOTSTEP,
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
    public void spawnParticles(World world, int xCoord, int yCoord, int zCoord, Random random) {
        if (random.nextInt(10) == 0 && !world.isSolid(xCoord, yCoord - 1, zCoord) && this.id != BlockList.anvil.id) {
            float xOffset = xCoord + random.nextFloat();
            float yOffset = yCoord - 0.05F;
            float zOffset = zCoord + random.nextFloat();
            float red = 0.7F;
            float green = 0.7F;
            float blue = 0.5F;
            if (this.id == BlockList.silverSand.id) {
                red = 0.8F;
                green = 0.8F;
                blue = 0.8F;
            } else if (this.id == BlockList.redSand.id) {
                red = 0.7F;
                green = 0.4F;
                blue = 0.1F;
            } else if (this.id == BlockList.gravel.id || this.id == BlockList.susGravel.id) {
                red = 0.5F;
                green = 0.45F;
                blue = 0.5F;
            }

            world.spawnParticle(ParticleType.DUST, xOffset, yOffset, zOffset, red, green, blue);
        }
    }

    @Override
    public void onEntityCollision(Entity entity, World world, int x, int y, int z) {
        if (this.material == MaterialList.quicksand) {
            entity.motionX *= 0.2;
            entity.motionY *= 0.2;
            entity.motionZ *= 0.2;
            entity.fallDistance = 0.0F;
        }
    }

    @Override
    public int getRotation(int x, int y, int z, int side) {
        return this.id != BlockList.quickSand.id && this.id != BlockList.silverSand.id && this.id != BlockList.anvil.id ? this.rotationRand(x, y, z, side) : -1;
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.blockIcon = ir.registerIcon("sands/" + this.name);
    }
}
