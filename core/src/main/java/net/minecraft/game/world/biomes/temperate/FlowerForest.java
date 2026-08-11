package net.minecraft.game.world.biomes.temperate;

import java.util.Random;
import net.minecraft.game.entity.mob.animal.cow.Moobloom;
import net.minecraft.game.entity.mob.monster.creeper.CreeperMagic;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.game.world.generation.features.trees.bigflower.BigDandelion;
import net.minecraft.game.world.generation.features.trees.bigflower.BigLilac;
import net.minecraft.game.world.generation.features.trees.bigflower.BigPetunia;
import net.minecraft.game.world.generation.features.trees.bigflower.BigRose;
import net.minecraft.game.world.generation.features.trees.bigflower.BigTulip;
import net.minecraft.game.world.generation.features.trees.deciduous.AppleTree;
import net.minecraft.game.world.generation.features.trees.deciduous.BasicTree;
import net.minecraft.game.world.mobspawner.SpawnListEntry;

public class FlowerForest extends Forest {
    public FlowerForest(int biomeID) {
        super(biomeID);
        this.flowersAttempts = 64;
        this.animals.add(new SpawnListEntry(Moobloom.class, 10));
        this.monsters.add(new SpawnListEntry(CreeperMagic.class, 4));
    }

    @Override
    public Tree defaultTreeType(Random rand) {
        switch (rand.nextInt(10)) {
            case 0:
                return new BigRose();
            case 1:
                return new BigDandelion();
            case 2:
                return new BigLilac();
            case 3:
                return new BigPetunia();
            case 4:
                return new BigTulip();
            case 5:
                return new BigLilac(5, 0);
            default:
                return rand.nextInt(20) == 0 ? new AppleTree() : new BasicTree(this.fillerMeta);
        }
    }
}
