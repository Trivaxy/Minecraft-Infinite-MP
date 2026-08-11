package net.minecraft.game.world.biomes.cold;

import java.util.Random;
import net.minecraft.game.entity.mob.animal.tameable.Fox;
import net.minecraft.game.entity.mob.monster.skeleton.Stray;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.game.world.generation.features.general.IceSpike;
import net.minecraft.game.world.generation.features.plants.FlowerPatch;
import net.minecraft.game.world.mobspawner.SpawnListEntry;

public class Fjord extends ColdBiome {
    public Fjord(int biomeID) {
        super(biomeID);
        this.genBeaches = false;
        this.animals.add(new SpawnListEntry(Fox.class, 8));
        this.monsters.add(new SpawnListEntry(Stray.class, 6));
        this.variants.add(BiomeList.glacier);
    }

    @Override
    public void prePopulate(World world, Random rand, int x0, int z0) {
        int x = x0 + rand.nextInt(16) + 8;
        int z = z0 + rand.nextInt(16) + 8;
        int y = world.getHeightmap(x, z) + 1;
        if (y >= world.getWorldHeight() / 2 + 16 || this == BiomeList.glacier) {
            IceSpike.INSTANCE.place(world, rand, x, y, z);
        }

        for (int i = 0; i < 8; i++) {
            x = x0 + rand.nextInt(16) + 8;
            y = rand.nextInt(world.getWorldHeight());
            z = z0 + rand.nextInt(16) + 8;
            FlowerPatch.berryBush.place(world, rand, x, y, z);
        }

        for (int var15 = 0; var15 < 10; var15++) {
            x = x0 + rand.nextInt(16) + 8;
            y = rand.nextInt(world.getWorldHeight());
            z = z0 + rand.nextInt(16) + 8;
            FlowerPatch.purpleFlower.place(world, rand, x, y, z);
        }
    }

    @Override
    public int getSkyColor() {
        return 9803519;
    }
}
