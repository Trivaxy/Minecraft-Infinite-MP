package net.minecraft.game.world.biomes.sky;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.generation.features.general.Stump;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.game.world.generation.features.trees.coniferous.CedarTree;
import net.minecraft.game.world.generation.features.trees.coniferous.FirTree;

public class SkyTaiga extends SkyBiome {
    public SkyTaiga(int biomeID, Biome.Temperature temp) {
        super(biomeID);
        this.treeAttempts = 10;
        this.topMeta = 8;
        this.grassMeta = 7;
        this.reedAttempts = 0;
        if (temp == Biome.Temperature.COLD) {
            this.tallGrassAttempts = 0;
            this.caveBlock1 = (short)BlockList.snowBlock.id;
            this.caveBlock2 = (short)BlockList.packedIce.id;
            this.caveMeta1 = 0;
            this.caveMeta2 = 0;
            this.beachBlock1 = (short)BlockList.snowBlock.id;
        }
    }

    @Override
    public void prePopulate(World world, Random rand, int x0, int z0) {
        super.prePopulate(world, rand, x0, z0);
        int x = x0 + rand.nextInt(16) + 8;
        int z = z0 + rand.nextInt(16) + 8;
        int y = world.getHeightmap(x, z);
        Stump.purpur.place(world, rand, x, y, z);
    }

    @Override
    public Tree defaultTreeType(Random rand) {
        if (rand.nextInt(3) == 0) {
            return new FirTree(rand.nextInt(6) + 8, true);
        } else {
            return rand.nextInt(5) == 0 ? new FirTree(rand.nextInt(4) + 4, false) : new CedarTree();
        }
    }

    @Override
    public int getSkyColor() {
        return 11964107;
    }
}
