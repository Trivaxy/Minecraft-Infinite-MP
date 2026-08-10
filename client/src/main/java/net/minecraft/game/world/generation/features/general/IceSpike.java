package net.minecraft.game.world.generation.features.general;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.Feature;
import net.minecraft.util.math.MathHelper;

public class IceSpike extends Feature {
    public static IceSpike INSTANCE = new IceSpike();
    protected int heightmultiplier = 6;
    protected int metadata = 0;

    protected boolean canGrow(Random rng, int shart) {
        return shart > 1 && rng.nextInt(60) == 0;
    }

    @Override
    public boolean place(World world, Random random, int x, int y, int z) {
        while (this.airCheck(world, x, y, z)) {
            y--;
        }

        if (this.snowCheck(world, x, y, z)) {
            return false;
        }

        y += random.nextInt(this.heightmultiplier);
        int height = random.nextInt(this.heightmultiplier) + 7;
        int proposedHeight = height / 4 + random.nextInt(2);
        if (this.canGrow(random, proposedHeight)) {
            y += 10 + random.nextInt(30);
        }

        for (int dY = 0; dY < height; dY++) {
            float yOff = (1.0F - (float)dY / height) * proposedHeight;
            int radius = MathHelper.ceiling_float_int(yOff);

            for (int dX = -radius; dX <= radius; dX++) {
                float xOff = MathHelper.abs_int(dX) - 0.25F;

                for (int dZ = -radius; dZ <= radius; dZ++) {
                    float zOff = MathHelper.abs_int(dZ) - 0.25F;
                    if ((dX == 0 && dZ == 0 || xOff * xOff + zOff * zOff <= yOff * yOff)
                        && (dX != -radius && dX != radius && dZ != -radius && dZ != radius || random.nextFloat() <= 0.75F)) {
                        int blockID = world.getBlockId(x + dX, y + dY, z + dZ);
                        Block block = BlockList.blocks[blockID];
                        if (this.checkBlock(block, blockID)) {
                            int id = this.randomBlockId(random);
                            world.setBlockAndMetadata(x + dX, y + dY, z + dZ, id, id == BlockList.lavaMoving.id ? 0 : this.metadata);
                        }

                        if (dY != 0 && radius > 1) {
                            blockID = world.getBlockId(x + dX, y - dY, z + dZ);
                            block = BlockList.blocks[blockID];
                            if (this.checkBlock(block, blockID)) {
                                int id = this.randomBlockId(random);
                                world.setBlockAndMetadata(x + dX, y - dY, z + dZ, id, id == BlockList.lavaMoving.id ? 0 : this.metadata);
                            }
                        }
                    }
                }
            }
        }

        int radius = proposedHeight - 1;
        if (radius < 0) {
            radius = 0;
        } else if (radius > 1) {
            radius = 1;
        }

        for (int xx = -radius; xx <= radius; xx++) {
            for (int zz = -radius; zz <= radius; zz++) {
                int yy = y - 1;
                int ghtdown = 50;
                if (Math.abs(xx) == 1 && Math.abs(zz) == 1) {
                    ghtdown = random.nextInt(5);
                }

                while (yy > 50) {
                    int blockID = world.getBlockId(x + xx, yy, z + zz);
                    Block block = BlockList.blocks[blockID];
                    if (block != null
                        && block.material != MaterialList.air
                        && blockID != BlockList.snowBlock.id
                        && blockID != BlockList.ice.id
                        && blockID != BlockList.packedIce.id
                        && blockID != BlockList.powderSnow.id
                        && blockID != BlockList.stone.id
                        && blockID != BlockList.dirt.id) {
                        break;
                    }

                    int id = this.randomBlockId(random);
                    world.setBlockAndMetadata(x + xx, yy, z + zz, id, id == BlockList.lavaMoving.id ? 0 : this.metadata);
                    yy--;
                    if (--ghtdown <= 0) {
                        yy -= random.nextInt(5) + 1;
                        ghtdown = random.nextInt(5);
                    }
                }
            }
        }

        return true;
    }

    protected int randomBlockId(Random rand) {
        return rand.nextInt(10) == 0 ? BlockList.snowBlock.id : BlockList.packedIce.id;
    }

    protected boolean airCheck(World level, int x, int y, int z) {
        return (level.isAir(x, y, z) || level.getBlockId(x, y, z) == BlockList.snowLayer.id) && y > 2;
    }

    protected boolean snowCheck(World level, int x, int y, int z) {
        return level.getBlockId(x, y, z) != BlockList.snowBlock.id
            && level.getBlockId(x, y, z) != BlockList.grass.id
            && level.getBlockId(x, y, z) != BlockList.dirt.id
            && level.getBlockId(x, y, z) != BlockList.snowLayer.id;
    }

    protected boolean checkBlock(Block block, int blockID) {
        return block == null
            || block.material == MaterialList.air
            || blockID == BlockList.dirt.id
            || blockID == BlockList.snowBlock.id
            || blockID == BlockList.ice.id
            || blockID == BlockList.packedIce.id
            || blockID == BlockList.powderSnow.id;
    }
}
