package net.minecraft.game.world.generation.features.hell;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.Feature;

public class BasaltSpire extends Feature {
    public static final BasaltSpire INSTANCE = new BasaltSpire(BlockList.stone.id, 2);
    public static final BasaltSpire INVERSE = new BasaltSpire(BlockList.stone.id, 2, true);
    public static final BasaltSpire VOID = new BasaltSpire(BlockList.voidRock.id, 0);
    private int block;
    private int meta;
    private boolean inverse = false;

    public BasaltSpire(int id, int meta) {
        this.block = id;
        this.meta = meta;
    }

    public BasaltSpire(int id, int meta, boolean inverse) {
        this(id, meta);
        this.inverse = inverse;
    }

    @Override
    public boolean place(World world, Random rand, int x0, int y0, int z0) {
        int height = rand.nextInt(12) + 4;
        if (y0 > 0 && y0 + height + 1 <= world.getWorldHeight() && world.isSolid(x0, y0 - 1, z0)) {
            int size = 1;

            for (int dx = -size; dx <= size; dx++) {
                for (int dz = -size; dz <= size; dz++) {
                    if (this.inverse) {
                        int depth = rand.nextInt(12) + 4;

                        for (int d = 0; d < depth; d++) {
                            world.setBlockAndMetadataWithNotify(x0 + dx, y0 - 1 - d, z0 + dz, this.block, this.meta);
                        }
                    } else {
                        this.placeSpike(world, x0 + dx, y0, z0 + dz, rand.nextInt(12) + 4);
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    private void placeSpike(World world, int x, int yBase, int z, int height) {
        for (int y = 0; y < height; y++) {
            int yPos = yBase + y;
            if (!Block.isOpaque.get(world.getBlockId(x, yPos, z))) {
                world.setBlockAndMetadataWithNotify(x, yPos, z, this.block, this.meta);
            }
        }
    }
}
