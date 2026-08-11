package net.minecraft.game.world.generation.features.plants;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.Feature;

public class TallPlants extends Feature {
    public static final TallPlants CACTUS = new TallPlants(BlockList.cactus, 10, 3).setHeightVariation(true);
    public static final TallPlants REEDS = new TallPlants(BlockList.reeds, 20, 3);
    public static final TallPlants GLOWROOT = new TallPlants(BlockList.glowRoots);
    public static final TallPlants ROOT = new TallPlants(BlockList.nyliumRoots);
    public static final TallPlants SEAWEED = new TallPlants(BlockList.seaweed).setAmphibious(true);
    public static final TallPlants SEAGRASS = new TallPlants(BlockList.seaGrass).setAmphibious(true);
    public static final TallPlants SEAFERN = new TallPlants(BlockList.seaFern).setAmphibious(true);
    private Block block;
    int attempts = 20;
    int maxHeight = 10;
    boolean heightVariation = false;
    boolean amphibious = false;

    public TallPlants(Block block) {
        this.block = block;
    }

    public TallPlants(Block block, int attempts, int hght) {
        this.block = block;
        this.attempts = attempts;
        this.maxHeight = hght;
    }

    private TallPlants setHeightVariation(boolean b) {
        this.heightVariation = b;
        return this;
    }

    private TallPlants setAmphibious(boolean b) {
        this.amphibious = b;
        return this;
    }

    @Override
    public boolean place(World world, Random random, int x, int y, int z) {
        for (int attempts = 0; attempts < this.attempts; attempts++) {
            int x0 = x + random.nextInt(8) - random.nextInt(8);
            int y0 = y + random.nextInt(4) - random.nextInt(4);
            int z0 = z + random.nextInt(8) - random.nextInt(8);
            if (!this.heightVariation) {
                y0 = y;
            }

            if (this.canPlaceHere(world, x0, y0, z0)) {
                int height = 1 + random.nextInt(this.maxHeight);

                for (int posy = 0; posy < height; posy++) {
                    if (this.canPlaceHere(world, x0, y0 + posy, z0) && this.block.placeableAt(world, x0, y0 + posy, z0)) {
                        world.setBlock(x0, y0 + posy, z0, this.block.id);
                    }
                }
            }
        }

        return true;
    }

    private boolean canPlaceHere(World world, int x0, int y0, int z0) {
        return world.isAir(x0, y0, z0) || this.amphibious;
    }
}
