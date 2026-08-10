package net.minecraft.game.world.generation.features.plants;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.Feature;

public class LeafPiles extends Feature {
    public static final LeafPiles INSTANCE;
    public static final LeafPiles APPLE;
    public static final LeafPiles BIRCH;
    public static final LeafPiles SPRUCE;
    public static final LeafPiles PALM;
    public static final LeafPiles ACACIA;
    public static final LeafPiles JUNGLE;
    public static final LeafPiles MANGROVE;
    public static final LeafPiles BAOBAB;
    public static final LeafPiles MAPLE;
    public static final LeafPiles CHERRY;
    public static final LeafPiles BAMBOO;
    public static final LeafPiles ANCIENT;
    public static final LeafPiles MAHOGANY;
    public static final LeafPiles VIOLARCH;
    public static final LeafPiles WILLOW;
    public static final LeafPiles GOLD;
    private int id;
    private int mdata;

    public LeafPiles(int id, int mdata) {
        this.id = id;
        this.mdata = mdata;
    }

    @Override
    public boolean place(World world, Random random, int x, int y, int z) {
        while (y > 0) {
            int id = world.getBlockId(x, y, z);
            if (id == 0 || id == BlockList.leaves.id) {
                y--;
                continue;
            }
            break;
        }

        for (int attempts = 0; attempts < 128; attempts++) {
            int x0 = x + random.nextInt(8) - random.nextInt(8);
            int y0 = y + random.nextInt(4) - random.nextInt(4);
            int z0 = z + random.nextInt(8) - random.nextInt(8);
            if (world.isAir(x0, y0, z0) && world.getBlockId(x0, y0 - 1, z0) == BlockList.grass.id) {
                world.setBlockAndMetadata(x0, y0, z0, this.id, this.mdata);
            }
        }

        return true;
    }

    static {
        INSTANCE = new LeafPiles(BlockList.leafPile.id, 0);
        APPLE = new LeafPiles(BlockList.leafPile.id, 8);
        BIRCH = new LeafPiles(BlockList.leafPile.id, 16);
        SPRUCE = new LeafPiles(BlockList.leafPile.id, 24);
        PALM = new LeafPiles(BlockList.leafPile.id, 32);
        ACACIA = new LeafPiles(BlockList.leafPile.id, 40);
        JUNGLE = new LeafPiles(BlockList.leafPile.id, 48);
        MANGROVE = new LeafPiles(BlockList.leafPile.id, 56);
        BAOBAB = new LeafPiles(BlockList.leafPile.id, 64);
        MAPLE = new LeafPiles(BlockList.leafPile.id, 72);
        CHERRY = new LeafPiles(BlockList.leafPile.id, 80);
        BAMBOO = new LeafPiles(BlockList.leafPile.id, 88);
        ANCIENT = new LeafPiles(BlockList.leafPile.id, 96);
        MAHOGANY = new LeafPiles(BlockList.leafPile.id, 104);
        VIOLARCH = new LeafPiles(BlockList.leafPile.id, 112);
        WILLOW = new LeafPiles(BlockList.leafPile.id, 120);
        GOLD = new LeafPiles(BlockList.leafPile.id, 128);
    }
}
