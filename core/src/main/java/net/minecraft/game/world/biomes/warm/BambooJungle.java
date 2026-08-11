package net.minecraft.game.world.biomes.warm;

import java.util.Random;
import net.minecraft.game.entity.mob.animal.Bear;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.game.world.generation.features.trees.tropical.BambooTree;
import net.minecraft.game.world.mobspawner.SpawnListEntry;

public class BambooJungle extends Jungle {
    public BambooJungle(int biomeID) {
        super(biomeID);
        this.treeAttempts = 50;
        this.reedAttempts = 60;
        this.waterAttempts = 4;
        this.generateLava = false;
        this.animals.clear();
        this.animals.add(new SpawnListEntry(Bear.class, 8));
    }

    @Override
    public Tree defaultTreeType(Random rand) {
        return new BambooTree();
    }
}
