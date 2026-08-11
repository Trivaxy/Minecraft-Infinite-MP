package net.minecraft.game.world.generation.features.general;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.dimension.Overworld;
import net.minecraft.game.world.generation.features.trees.Tree;

public class Stump extends Tree {
    public static final Stump log = new Stump(0);
    public static final Stump birch = new Stump(1);
    public static final Stump spruce = new Stump(2);
    public static final Stump oldOak = new Stump(12);
    public static final Stump purpur = new Stump(15);
    int meta;

    public Stump(int m) {
        this.meta = m;
    }

    @Override
    public boolean place(World world, Random random, int x, int y, int z) {
        int length = 3 + random.nextInt(4);
        boolean axisX = random.nextBoolean();

        for (int i = 0; i < length; i++) {
            int xx = axisX ? x + i : x;
            int zz = axisX ? z : z + i;
            Block block = BlockList.blocks[world.getBlockId(xx, y, zz)];
            Block blockBeneath = BlockList.blocks[world.getBlockId(xx, y - 1, zz)];
            if (block != null && (block.fullBlock() || block.material == MaterialList.water)
                || blockBeneath == null
                || blockBeneath != BlockList.grass && blockBeneath != BlockList.dirt) {
                return false;
            }

            world.setBlockAndMetadata(xx, y, zz, BlockList.wood.id, axisX ? this.meta + 64 : this.meta + 128);
            world.setBlockAndMetadata(xx, y - 1, zz, BlockList.dirt.id, world.getBiomeAt(xx, zz).fillerMeta);
            if (random.nextInt(6) == 0 && world.currDim instanceof Overworld) {
                world.setBlock(xx, y + 1, zz, random.nextInt(2) == 0 ? BlockList.mushroomBrown.id : BlockList.mushroomRed.id);
            }
        }

        return true;
    }
}
