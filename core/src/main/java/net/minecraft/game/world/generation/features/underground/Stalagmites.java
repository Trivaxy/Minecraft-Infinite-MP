package net.minecraft.game.world.generation.features.underground;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.Feature;

public class Stalagmites extends Feature {
    public static final Stalagmites dripstone;
    public static final Stalagmites icicle;
    int meta;
    int id;

    public Stalagmites(int id, int meta) {
        this.id = id;
        this.meta = meta;
    }

    @Override
    public boolean place(World world, Random random, int x, int y, int z) {
        int length = random.nextInt(5) + 1;
        if (world.getBlockId(x, y, z) == 0
            && world.getBlockId(x, y - 1, z) == BlockList.stone.id
            && world.getBlockMetadata(x, y - 1, z) == this.meta
            && y < world.getSeaLevel() - length - 1) {
            if (world.getBlockId(x, y - 1, z) == BlockList.stone.id && world.getBlockMetadata(x, y - 1, z) == this.meta) {
                for (int height = 0; height < length; height++) {
                    world.setBlock(x, y + height, z, this.id);
                    if (world.isSolid(x, y - 1, z) && length > 1) {
                        world.setBlockMetadata(x, y, z, 0);
                    } else {
                        world.setBlockMetadata(x, y, z, 2);
                    }
                }
            }

            for (int height = 0; height < length; height++) {
                if (world.getBlockId(x, y + height - 1, z) == this.id && world.getBlockId(x, y + height + 1, z) == this.id && length > 2) {
                    world.setBlockMetadata(x, y + height, z, 1);
                }
            }

            return true;
        } else {
            return false;
        }
    }

    static {
        dripstone = new Stalagmites(BlockList.spike.id, 7);
        icicle = new Stalagmites(BlockList.icicle.id, 6);
    }
}
