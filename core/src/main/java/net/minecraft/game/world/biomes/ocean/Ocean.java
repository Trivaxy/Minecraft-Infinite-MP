package net.minecraft.game.world.biomes.ocean;

import java.util.Random;
import net.minecraft.game.entity.mob.animal.aquatic.Dolphin;
import net.minecraft.game.entity.mob.animal.aquatic.Fish;
import net.minecraft.game.entity.mob.animal.aquatic.Jellyfish;
import net.minecraft.game.entity.mob.animal.aquatic.Shark;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.generation.features.general.Boulder;
import net.minecraft.game.world.generation.features.general.ClayLump;
import net.minecraft.game.world.generation.features.plants.TallPlants;
import net.minecraft.game.world.mobspawner.SpawnListEntry;

public class Ocean extends Biome {
    public Ocean(int biomeID) {
        super(biomeID);
        this.clayAttempts = 200;
        this.minBeachHeight = 99999;
        this.maxBeachHeight = -15;
        this.caveCeiling = 99999;
        this.copperLumpAttempts = 30;
        this.stoneHeight = -1;
        this.treeAttempts = -3;
        this.hasBeaches = false;
        this.seaCreatures.add(new SpawnListEntry(Jellyfish.class, 10));
        this.seaCreatures.add(new SpawnListEntry(Dolphin.class, 8));
        this.seaCreatures.add(new SpawnListEntry(Shark.class, 6));
        this.seaCreatures.add(new SpawnListEntry(Fish.class, 12));
    }

    @Override
    public void prePopulate(World world, Random rand, int x0, int z0) {
        if (this.getTemperatureEnum() != Biome.Temperature.COLD) {
            for (int i = 0; i < 40; i++) {
                int x = x0 + rand.nextInt(16) + 8;
                int y = rand.nextInt(world.getWorldHeight());
                int z = z0 + rand.nextInt(16) + 8;
                TallPlants.SEAWEED.place(world, rand, x, y, z);
            }
        }
    }

    @Override
    public void postPopulate(World world, Random rand, int x0, int z0) {
        for (int i = 0; i < 20; i++) {
            int x = x0 + rand.nextInt(8) + 4;
            int y = rand.nextInt(world.getSeaLevel() - 10);
            int z = z0 + rand.nextInt(8) + 4;
            ClayLump.sand.place(world, rand, x, y, z);
            x = x0 + rand.nextInt(8) + 4;
            y = rand.nextInt(world.getSeaLevel() - 10);
            z = z0 + rand.nextInt(8) + 4;
            ClayLump.gravel.place(world, rand, x, y, z);
        }

        int x = x0 + rand.nextInt(16) + 8;
        int y = z0 + rand.nextInt(16) + 8;
        int z = rand.nextInt(world.getSeaLevel() - 64) + 64;
        if (rand.nextInt(3) == 0) {
            if (this.temperature == Biome.Temperature.COLD) {
                Boulder.ice.place(world, rand, x, z, y);
            } else {
                Boulder.rock.place(world, rand, x, z, y);
            }
        }
    }
}
