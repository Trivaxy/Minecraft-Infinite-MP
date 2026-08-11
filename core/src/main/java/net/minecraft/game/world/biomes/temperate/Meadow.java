package net.minecraft.game.world.biomes.temperate;

import java.util.Random;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.trees.bigflower.BigDandelion;
import net.minecraft.game.world.generation.features.trees.bigflower.BigLilac;
import net.minecraft.game.world.generation.features.trees.bigflower.BigPetunia;
import net.minecraft.game.world.generation.features.trees.bigflower.BigRose;
import net.minecraft.game.world.generation.features.trees.bigflower.BigTulip;

public class Meadow extends Plains {
    public Meadow(int id) {
        super(id);
        this.flowersAttempts = 64;
        this.hasBeaches = false;
    }

    @Override
    public void prePopulate(World world, Random rand, int x0, int z0) {
        super.prePopulate(world, rand, x0, z0);
        int x = x0 + rand.nextInt(16) + 8;
        int z = z0 + rand.nextInt(16) + 8;
        if (rand.nextInt(10) == 0) {
            switch (rand.nextInt(6)) {
                case 0:
                    new BigRose().place(world, rand, x, world.getHeightmap(x, z), z);
                    break;
                case 1:
                    new BigDandelion().place(world, rand, x, world.getHeightmap(x, z), z);
                    break;
                case 2:
                    new BigLilac().place(world, rand, x, world.getHeightmap(x, z), z);
                    break;
                case 3:
                    new BigPetunia().place(world, rand, x, world.getHeightmap(x, z), z);
                case 4:
                    new BigTulip().place(world, rand, x, world.getHeightmap(x, z), z);
                case 5:
                    new BigLilac(5, 0).place(world, rand, x, world.getHeightmap(x, z), z);
            }
        }
    }
}
