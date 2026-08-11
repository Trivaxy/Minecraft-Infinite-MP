package net.minecraft.game.world.biomes.temperate;

import java.util.Random;
import net.minecraft.game.entity.mob.animal.flying.Bird;
import net.minecraft.game.entity.mob.animal.tameable.Wolf;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.game.world.generation.features.plants.LeafPiles;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.game.world.generation.features.trees.deciduous.BasicTree;
import net.minecraft.game.world.generation.features.trees.deciduous.BigTree;
import net.minecraft.game.world.generation.features.trees.deciduous.MapleTree;
import net.minecraft.game.world.generation.features.trees.fungal.BigMushroom;
import net.minecraft.game.world.mobspawner.SpawnListEntry;

public class Woodland extends Biome {
    public Woodland(int biomeID) {
        super(biomeID);
        this.mushroomChance = 8;
        this.treeAttempts = 5;
        this.caveFloor = 32;
        this.hasBeaches = false;
        this.animals.add(new SpawnListEntry(Bird.class, 12));
        this.animals.add(new SpawnListEntry(Wolf.class, 8));
        this.variants.add(BiomeList.mapleHills);
    }

    @Override
    public void prePopulate(World world, Random rand, int x0, int z0) {
        if (!this.hasSnow()) {
            int z = x0 + rand.nextInt(16) + 8;
            int x = rand.nextInt(world.getWorldHeight());
            int y = z0 + rand.nextInt(16) + 8;
            LeafPiles.ANCIENT.place(world, rand, z, x, y);
        }
    }

    @Override
    public Tree defaultTreeType(Random rand) {
        if (this.hasSnow() && rand.nextInt(5) == 0) {
            return new MapleTree();
        } else if (rand.nextInt(25) == 0 && !this.hasSnow()) {
            return new BigMushroom(rand.nextInt(2) + 1);
        } else {
            return !rand.nextBoolean() && !this.hasSnow() ? new BasicTree(this.fillerMeta) : new BigTree();
        }
    }
}
