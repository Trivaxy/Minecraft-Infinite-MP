package net.minecraft.game.world.biomes.sky;

import java.util.Random;
import net.minecraft.game.entity.mob.monster.Wisp;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.game.world.generation.features.trees.coniferous.CedarTree;
import net.minecraft.game.world.generation.features.trees.coniferous.FirTree;
import net.minecraft.game.world.generation.features.trees.misc.Shrub;
import net.minecraft.game.world.generation.noise.OctaveNoise;
import net.minecraft.game.world.mobspawner.SpawnListEntry;

public class SkyPlains extends SkyBiome {
    private OctaveNoise grassNoise;
    private long seed;

    public SkyPlains(int biomeID) {
        super(biomeID);
        this.treeAttempts = -10;
        this.flowersAttempts = 64;
        this.tallGrassAttempts = 5;
        this.animals.clear();
        this.monsters.add(new SpawnListEntry(Wisp.class, 10));
    }

    @Override
    public void prePopulate(World world, Random rand, int x0, int z0) {
        super.prePopulate(world, rand, x0, z0);
        int x = x0 + rand.nextInt(16) + 8;
        int z = z0 + rand.nextInt(16) + 8;
        Shrub.purpurShrub.place(world, rand, x, world.getHeightmap(x, z), z);
        x = x0 + rand.nextInt(16) + 8;
        z = z0 + rand.nextInt(16) + 8;
        if (rand.nextInt(5) == 0) {
            this.defaultTreeType(rand).place(world, rand, x, world.getHeightmap(x, z), z);
        }
    }

    @Override
    public Tree defaultTreeType(Random rand) {
        return rand.nextInt(5) == 0 ? new FirTree(rand.nextInt(4) + 4, false) : new CedarTree();
    }

    @Override
    public void surfaceBuilder(
        World world, Random random, short[] blocks, byte[] metadata, int x, int z, double heightValue, double sandValue, double gravelValue
    ) {
        if (this.seed != world.getSeed() || this.grassNoise == null) {
            this.grassNoise = new OctaveNoise(random, 8);
            this.seed = world.getSeed();
        }

        boolean generateGrass = this.grassNoise.generateNoise2D(x, z) > 56.0;
        boolean generateGrass2 = Math.abs(this.grassNoise.generateNoise2D(x, z)) < 12.0;
        if (generateGrass) {
            this.topMeta = 9;
        } else if (generateGrass2) {
            this.topMeta = 8;
        } else {
            this.topMeta = 7;
        }

        super.surfaceBuilder(world, random, blocks, metadata, x, z, heightValue, -900.0, -900.0);
    }
}
