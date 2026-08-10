package net.minecraft.game.world.biomes.hot;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldType;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.generation.features.general.Boulder;
import net.minecraft.game.world.generation.features.general.Pond;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.game.world.generation.features.trees.tropical.AcaciaTree;

public abstract class HotBiome extends Biome {
    protected HotBiome(int biomeID) {
        super(biomeID);
        this.biomeCaves = true;
        this.caveBlock1 = (short)BlockList.dirt.id;
        this.caveBlock2 = (short)BlockList.sandStone.id;
        this.topMeta = this.fillerMeta = this.grassMeta = this.caveMeta1 = 3;
        this.defaultStoneType = 4;
        this.rubyLumpAttempts = 8;
    }

    @Override
    public void postPopulate(World world, Random rand, int x0, int z0) {
        if (this.biomeCaves) {
            int y = x0 + rand.nextInt(16) + 8;
            int z = rand.nextInt(48) + this.yOffset;
            if (world.settings.getType() == WorldType.CAVES) {
                z = rand.nextInt(world.getWorldHeight());
            }

            int x = z0 + rand.nextInt(16) + 8;
            Boulder.granite.place(world, rand, y, z, x);
        }

        if (world.rand.nextInt(4) == 0) {
            int y = x0 + rand.nextInt(16) + 8;
            int z = rand.nextInt(32) + this.yOffset;
            int x = z0 + rand.nextInt(16) + 8;
            Pond.LAVA.place(world, rand, y, z, x);
        }
    }

    @Override
    public Tree defaultTreeType(Random rand) {
        return new AcaciaTree();
    }
}
