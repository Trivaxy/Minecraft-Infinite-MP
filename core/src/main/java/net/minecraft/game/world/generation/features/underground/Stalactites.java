package net.minecraft.game.world.generation.features.underground;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.Feature;

public class Stalactites extends Feature {
    public static final Stalactites dripstone;
    public static final Stalactites icicle;
    int meta;
    int id;

    public Stalactites(int id, int meta) {
        this.id = id;
        this.meta = meta;
    }

    @Override
    public boolean place(World world, Random random, int x, int y, int z) {
        int height = random.nextInt(5) + 1;
        if (world.getBlockId(x, y + 1, z) == BlockList.stone.id && world.getBlockMetadata(x, y + 1, z) == this.meta && y < world.getSeaLevel() - height - 1) {
            if (world.getBlockId(x, y + 1, z) == BlockList.stone.id && world.getBlockMetadata(x, y + 1, z) == this.meta) {
                for (int heightOffset = 0; heightOffset < height; heightOffset++) {
                    if (world.getBlockId(x, y - heightOffset, z) == 0 && world.getBlockId(x, y - heightOffset - 1, z) == 0) {
                        world.setBlock(x, y - heightOffset, z, this.id);
                        world.setBlockMetadata(x, y - heightOffset, z, 4);
                    }
                }
            }

            for (int heightOffset = 0; heightOffset < height; heightOffset++) {
                if (world.getBlockId(x, y - heightOffset + 1, z) == this.id && world.getBlockId(x, y - heightOffset - 1, z) == this.id && height > 2) {
                    world.setBlockMetadata(x, y - heightOffset, z, 5);
                }

                if (world.isSolid(x, y - heightOffset + 1, z)
                    && world.getBlockId(x, y - heightOffset - 1, z) == this.id
                    && world.getBlockId(x, y - heightOffset, z) == this.id) {
                    world.setBlockMetadata(x, y - heightOffset, z, 3);
                }
            }

            return true;
        } else {
            return false;
        }
    }

    static {
        dripstone = new Stalactites(BlockList.spike.id, 7);
        icicle = new Stalactites(BlockList.icicle.id, 6);
    }
}
