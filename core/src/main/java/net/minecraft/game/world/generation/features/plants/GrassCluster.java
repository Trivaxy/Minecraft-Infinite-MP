package net.minecraft.game.world.generation.features.plants;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.game.world.generation.features.Feature;

public class GrassCluster extends Feature {
    public static final GrassCluster grass;
    public static final GrassCluster frostedGrass;
    public static final GrassCluster dryGrass;
    public static final GrassCluster lushGrass;
    public static final GrassCluster duneGrass;
    public static final GrassCluster flowersBlue;
    public static final GrassCluster flowersYellow;
    public static final GrassCluster flowersRed;
    public static final GrassCluster flowersPurple;
    public static final GrassCluster flowersOrange;
    public static final GrassCluster flowersWhite;
    public static final GrassCluster spores;
    public static final GrassCluster roots;
    public static final GrassCluster glowingGrass;
    public static final GrassCluster glowingFern;
    public static final GrassCluster deadBush;
    private int id;
    private int mdata;
    int attempts = 128;

    public GrassCluster(int id, int mdata) {
        this.id = id;
        this.mdata = mdata;
    }

    private GrassCluster setAttempts(int i) {
        this.attempts = i;
        return this;
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

        for (int attempts = 0; attempts < this.attempts; attempts++) {
            int x0 = x + random.nextInt(8) - random.nextInt(8);
            int y0 = y + random.nextInt(4) - random.nextInt(4);
            int z0 = z + random.nextInt(8) - random.nextInt(8);
            if (world.isAir(x0, y0, z0) && BlockList.blocks[this.id].canStay(world, x0, y0, z0)) {
                if (world.getBiomeAt(x0, z0) == BiomeList.skyPlains) {
                    world.setBlockAndMetadata(x0, y0, z0, this.id, world.getBlockMetadata(x0, y0 - 1, z0) - 1);
                } else {
                    world.setBlockAndMetadata(x0, y0, z0, this.id, this.mdata);
                }
            }
        }

        return true;
    }

    static {
        grass = new GrassCluster(BlockList.tallGrass.id, 0);
        frostedGrass = new GrassCluster(BlockList.tallGrass.id, 1);
        dryGrass = new GrassCluster(BlockList.tallGrass.id, 3);
        lushGrass = new GrassCluster(BlockList.tallGrass.id, 2);
        duneGrass = new GrassCluster(BlockList.duneGrass.id, 0);
        flowersBlue = new GrassCluster(BlockList.lilac.id, 0);
        flowersYellow = new GrassCluster(BlockList.dandelion.id, 0);
        flowersRed = new GrassCluster(BlockList.rose.id, 0);
        flowersPurple = new GrassCluster(BlockList.petunia.id, 0);
        flowersOrange = new GrassCluster(BlockList.tulip.id, 0);
        flowersWhite = new GrassCluster(BlockList.daisy.id, 0);
        spores = new GrassCluster(BlockList.tallGrass.id, 5);
        roots = new GrassCluster(BlockList.tallGrass.id, 4);
        glowingGrass = new GrassCluster(BlockList.glowGrass.id, 0);
        glowingFern = new GrassCluster(BlockList.glowGrass.id, 1);
        deadBush = new GrassCluster(BlockList.deadBush.id, 1).setAttempts(4);
    }
}
