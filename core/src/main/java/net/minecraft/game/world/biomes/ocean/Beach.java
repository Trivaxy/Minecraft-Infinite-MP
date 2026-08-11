package net.minecraft.game.world.biomes.ocean;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.mob.animal.Crab;
import net.minecraft.game.entity.mob.animal.Seal;
import net.minecraft.game.entity.mob.monster.creeper.CreeperFrost;
import net.minecraft.game.entity.mob.monster.creeper.CreeperWind;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.game.world.generation.features.general.Boulder;
import net.minecraft.game.world.generation.features.plants.GrassCluster;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.game.world.generation.features.trees.tropical.PalmTree;
import net.minecraft.game.world.generation.features.trees.tropical.SmallJungleTree;
import net.minecraft.game.world.mobspawner.SpawnListEntry;

public class Beach extends Biome {
    public Beach(int id, Biome.Temperature temp) {
        super(id);
        this.animals.clear();
        if (temp == null) {
            this.topBlock = (short)BlockList.stone.id;
            this.fillerBlock = (short)BlockList.stone.id;
            this.beachBlock1 = (short)BlockList.stone.id;
            this.treeAttempts = -999;
        } else if (temp == Biome.Temperature.COLD) {
            this.topBlock = (short)BlockList.silverSand.id;
            this.fillerBlock = (short)BlockList.silverSand.id;
            this.beachBlock1 = (short)BlockList.silverSand.id;
            this.defaultStoneType = 6;
            this.treeAttempts = -999;
            this.animals.add(new SpawnListEntry(Seal.class, 6));
            this.monsters.add(new SpawnListEntry(CreeperFrost.class, 4));
        } else if (temp == Biome.Temperature.WARM) {
            this.topBlock = (short)BlockList.sand.id;
            this.fillerBlock = (short)BlockList.sand.id;
            this.tallGrassAttempts = 3;
            this.defaultStoneType = 3;
            this.grassMeta = this.topMeta = this.fillerMeta = 2;
            this.animals.add(new SpawnListEntry(Crab.class, 6));
            this.monsters.add(new SpawnListEntry(CreeperWind.class, 4));
        } else {
            this.topBlock = (short)BlockList.sand.id;
            this.fillerBlock = (short)BlockList.sand.id;
            this.defaultStoneType = 3;
            this.tallGrassAttempts = 1;
            if (temp == Biome.Temperature.HOT) {
                this.treeAttempts = -999;
                this.grassMeta = 3;
            } else {
                this.grassMeta = 0;
                this.treeAttempts = 3;
            }

            this.animals.add(new SpawnListEntry(Crab.class, 6));
            this.monsters.add(new SpawnListEntry(CreeperWind.class, 4));
        }

        this.waterAttempts = 64;
        this.flowersAttempts = 8;
    }

    @Override
    public void prePopulate(World world, Random rand, int x0, int z0) {
        int x = x0 + rand.nextInt(16) + 8;
        int y = rand.nextInt(world.getWorldHeight());
        int z = z0 + rand.nextInt(16) + 8;
        if (rand.nextInt(2) == 0 && this.id == BiomeList.duneBeach.id) {
            GrassCluster.duneGrass.place(world, rand, x, y, z);
        }

        if (rand.nextInt(5) == 0 && this.id == BiomeList.snowyBeach.id) {
            y = x0 + rand.nextInt(16) + 8;
            z = z0 + rand.nextInt(16) + 8;
            x = world.getHeightmap(y, z);
            Boulder.ice.place(world, rand, y, x, z);
        }
    }

    @Override
    public Tree defaultTreeType(Random rand) {
        return this.id == BiomeList.lushBeach.id ? new SmallJungleTree() : new PalmTree();
    }

    @Override
    public int getSkyColor() {
        if (this.id == BiomeList.snowyBeach.id) {
            return 9803519;
        } else if (this.id == BiomeList.duneBeach.id) {
            return 13949649;
        } else {
            return this.id == BiomeList.lushBeach.id ? 7002584 : super.getSkyColor();
        }
    }
}
