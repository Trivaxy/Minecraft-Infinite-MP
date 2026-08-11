package net.minecraft.game.world.biomes.hot;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.general.Boulder;
import net.minecraft.game.world.generation.features.general.StoneSpike;
import net.minecraft.game.world.generation.features.hell.AshCluster;
import net.minecraft.game.world.generation.features.plants.GrassCluster;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.game.world.generation.features.trees.misc.ScorchedTree;

public class VolcanicWasteland extends Wasteland {
    public VolcanicWasteland(int biomeID) {
        super(biomeID);
        this.beachBlock1 = (short)BlockList.magma.id;
        this.beachBlock2 = (short)BlockList.magma.id;
        this.topBlock = (short)BlockList.dirt.id;
        this.fillerBlock = (short)BlockList.dirt.id;
        this.topMeta = 5;
        this.fillerMeta = 5;
        this.defaultLiquid = BlockList.lavaStill.id;
        this.defaultStoneType = 2;
        this.diamondLumpAttempts = 3;
        this.diamondLumpMaxHeight = 16;
        this.flowersAttempts = -1;
    }

    @Override
    public void prePopulate(World world, Random rand, int x0, int z0) {
        int x = x0 + rand.nextInt(16) + 8;
        int y = rand.nextInt(world.getWorldHeight());
        int z = z0 + rand.nextInt(16) + 8;

        for (int i = 0; i < 5; i++) {
            x = x0 + rand.nextInt(16) + 8;
            y = rand.nextInt(world.getWorldHeight());
            z = z0 + rand.nextInt(16) + 8;
            GrassCluster.deadBush.place(world, rand, x, y, z);
        }

        if (rand.nextInt(10) == 0) {
            y = x0 + rand.nextInt(16) + 8;
            z = z0 + rand.nextInt(16) + 8;
            StoneSpike.INSTANCE.place(world, rand, y, world.getHeightmap(y, z) + 1, z);
        }

        if (rand.nextInt(5) == 0) {
            x = x0 + rand.nextInt(16) + 8;
            y = rand.nextInt(world.getWorldHeight());
            z = z0 + rand.nextInt(16) + 8;
            Boulder.volcanic.place(world, rand, x, y, z);
        }

        x = x0 + 16;
        y = rand.nextInt(world.getWorldHeight());
        z = z0 + 16;
        AshCluster.INSTANCE.place(world, rand, x, y, z);
    }

    @Override
    public Tree defaultTreeType(Random rand) {
        return new ScorchedTree();
    }

    @Override
    public int getSkyColor() {
        return 5658198;
    }
}
