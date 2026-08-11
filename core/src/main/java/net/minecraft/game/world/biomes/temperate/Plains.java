package net.minecraft.game.world.biomes.temperate;

import java.util.Random;
import net.minecraft.game.entity.mob.animal.Bunny;
import net.minecraft.game.entity.mob.animal.mounts.horse.Horse;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.game.world.generation.features.trees.deciduous.CherryTree;
import net.minecraft.game.world.generation.features.trees.misc.Shrub;
import net.minecraft.game.world.mobspawner.SpawnListEntry;

public class Plains extends Biome {
    public Plains(int id) {
        super(id);
        this.treeAttempts = -3;
        this.flowersAttempts = 8;
        this.reedAttempts = 40;
        this.tallGrassAttempts = 3;
        this.genBeaches = false;
        this.animals.add(new SpawnListEntry(Bunny.class, 10));
        this.animals.add(new SpawnListEntry(Horse.class, 8));
        this.variants.add(BiomeList.cherryGrove);
        this.variants.add(BiomeList.shrubland);
        this.variants.add(BiomeList.meadow);
    }

    @Override
    public void prePopulate(World world, Random rand, int x0, int z0) {
        if (this.id == BiomeList.shrubland.id) {
            for (int i = 0; i < 5; i++) {
                int x = x0 + rand.nextInt(16) + 8;
                int z = z0 + rand.nextInt(16) + 8;
                Shrub.oakShrub.place(world, rand, x, world.getHeightmap(x, z), z);
            }
        }

        int x = x0 + rand.nextInt(16) + 8;
        int z = z0 + rand.nextInt(16) + 8;
        if (rand.nextInt(4) == 0) {
            if (rand.nextInt(10) == 0) {
                this.defaultTreeType(rand).place(world, rand, x, world.getHeightmap(x, z), z);
            } else {
                super.defaultTreeType(rand).place(world, rand, x, world.getHeightmap(x, z), z);
            }
        }
    }

    @Override
    public Tree defaultTreeType(Random rand) {
        return rand.nextInt(10) == 0 ? new CherryTree() : super.defaultTreeType(rand);
    }
}
