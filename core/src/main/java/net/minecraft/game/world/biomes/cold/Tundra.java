package net.minecraft.game.world.biomes.cold;

import java.util.Random;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.game.world.generation.features.general.Igloo;
import net.minecraft.game.world.generation.features.plants.GrassCluster;
import net.minecraft.game.world.generation.features.trees.misc.Shrub;

public class Tundra extends ColdBiome {
    public Tundra(int id) {
        super(id);
        this.treeAttempts = -3;
        this.tallGrassAttempts = 3;
        this.genBeaches = false;
        this.variants.add(BiomeList.frozenShrubland);
        this.variants.add(BiomeList.field);
        this.variants.add(BiomeList.muskeg);
    }

    @Override
    public void prePopulate(World world, Random rand, int x0, int z0) {
        if (rand.nextInt(250) == 0) {
            int x = x0 + rand.nextInt(16) + 8;
            int z = z0 + rand.nextInt(16) + 8;
            Igloo.INSTANCE.place(world, rand, x, world.getHeightmap(x, z) + 1, z);
        }

        if (this.id == BiomeList.frozenShrubland.id) {
            for (int i = 0; i < 5; i++) {
                int x = x0 + rand.nextInt(16) + 8;
                int z = z0 + rand.nextInt(16) + 8;
                Shrub.spruceShrub.place(world, rand, x, world.getHeightmap(x, z), z);
            }
        }

        if (this.id == BiomeList.field.id) {
            for (int i = 0; i < 20; i++) {
                int x = x0 + rand.nextInt(16) + 8;
                int y = rand.nextInt(world.getWorldHeight());
                int z = z0 + rand.nextInt(16) + 8;
                if (y < world.getSeaLevel() + 16) {
                    GrassCluster.flowersPurple.place(world, rand, x, y, z);
                }

                x = x0 + rand.nextInt(16) + 8;
                y = rand.nextInt(world.getWorldHeight());
                z = z0 + rand.nextInt(16) + 8;
                if (y < world.getSeaLevel() + 16) {
                    GrassCluster.frostedGrass.place(world, rand, x, y, z);
                }
            }
        }

        int x = x0 + rand.nextInt(16) + 8;
        int z = z0 + rand.nextInt(16) + 8;
        if (rand.nextInt(4) == 0) {
            this.defaultTreeType(rand).place(world, rand, x, world.getHeightmap(x, z), z);
        }
    }
}
