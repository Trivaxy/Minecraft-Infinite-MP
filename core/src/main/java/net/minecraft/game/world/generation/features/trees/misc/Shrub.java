package net.minecraft.game.world.generation.features.trees.misc;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.trees.Tree;

public class Shrub extends Tree {
    public static final Shrub oakShrub = new Shrub();
    public static final Shrub jungleShrub = new Shrub(5, 6);
    public static final Shrub mangroveShrub = new Shrub(6, 7);
    public static final Shrub mahoganyShrub = new Shrub(11, 13);
    public static final Shrub baobabShrub = new Shrub(7, 8);
    public static final Shrub acaciaShrub = new Shrub(4, 5);
    public static final Shrub spruceShrub = new Shrub(2, 3);
    public static final Shrub willowShrub = new Shrub(16, 15);
    public static final Shrub purpurShrub = new Shrub(15, 14);
    public static final Shrub cherryShrub = new Shrub(10, 10);
    private int woodMeta;
    private int leavesMeta;

    public Shrub() {
        this(0, 0);
    }

    public Shrub(int woodmeta, int leavesmeta) {
        this.woodMeta = woodmeta;
        this.leavesMeta = leavesmeta;
    }

    @Override
    public boolean place(World world, Random random, int x, int y, int z) {
        for (int i = y; i > 0; i--) {
            int id = world.getBlockId(x, y, z);
            if (id == 0 || id == BlockList.leaves.id || id == BlockList.snowLayer.id) {
                y--;
            }
        }

        int placeID = world.getBlockId(x, y, z);
        if (placeID != BlockList.dirt.id && placeID != BlockList.grass.id) {
            return false;
        }

        world.setBlockAndMetadata(x, ++y, z, BlockList.wood.id, this.woodMeta);

        for (int y0 = y; y0 <= y + 2; y0++) {
            int heightOff = y0 - y;
            int size = 2 - heightOff;

            for (int x0 = x - size; x0 <= x + size; x0++) {
                int width = x0 - x;

                for (int z0 = z - size; z0 <= z + size; z0++) {
                    int length = z0 - z;
                    if ((Math.abs(width) != size || Math.abs(length) != size || random.nextInt(2) != 0) && !Block.isOpaque.get(world.getBlockId(x0, y0, z0))) {
                        world.setBlockAndMetadata(x0, y0, z0, BlockList.leaves.id, this.leavesMeta);
                    }
                }
            }
        }

        return true;
    }
}
