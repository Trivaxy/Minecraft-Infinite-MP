package net.minecraft.game.world.generation.features.corals;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.Feature;

public abstract class CoralFeature extends Feature {
    protected static final int NORTH = 0;
    protected static final int SOUTH = 1;
    protected static final int WEST = 2;
    protected static final int EAST = 3;
    protected static final int UP = 4;

    @Override
    public boolean place(World world, Random random, int x, int y, int z) {
        int coralMeta = random.nextInt(4);
        return this.generateCoral(world, random, x, y, z, BlockList.coral.id, coralMeta);
    }

    protected abstract boolean generateCoral(World var1, Random var2, int var3, int var4, int var5, int var6, int var7);

    protected boolean placeCoralBlock(World world, Random random, int x, int y, int z, int coralId, int coralMeta) {
        int blockAt = world.getBlockId(x, y, z);
        int blockAbove = world.getBlockId(x, y + 1, z);
        if ((this.isWater(blockAt) || this.isCoral(blockAt)) && this.isWater(blockAbove)) {
            world.setBlockAndMetadata(x, y, z, coralId, coralMeta);
            float r = random.nextFloat();
            if (r < 0.25F) {
                int topCoral = BlockList.coralFan.id;
                world.setBlockAndMetadata(x, y + 1, z, topCoral, coralMeta);
            }

            return true;
        } else {
            return false;
        }
    }

    protected boolean isWater(int blockId) {
        return blockId == BlockList.waterStill.id || blockId == BlockList.waterMoving.id;
    }

    protected boolean isCoral(int blockId) {
        return blockId == BlockList.coral.id;
    }

    protected int dx(int dir) {
        if (dir == 2) {
            return -1;
        } else {
            return dir == 3 ? 1 : 0;
        }
    }

    protected int dz(int dir) {
        if (dir == 0) {
            return -1;
        } else {
            return dir == 1 ? 1 : 0;
        }
    }

    protected int rotateLeft(int dir) {
        if (dir == 0) {
            return 2;
        } else if (dir == 1) {
            return 3;
        } else {
            return dir == 2 ? 1 : 0;
        }
    }

    protected int rotateRight(int dir) {
        if (dir == 0) {
            return 3;
        } else if (dir == 1) {
            return 2;
        } else {
            return dir == 2 ? 0 : 1;
        }
    }

    protected void shuffleArray(int[] array, Random random) {
        for (int i = array.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int tmp = array[i];
            array[i] = array[j];
            array[j] = tmp;
        }
    }
}
