package net.minecraft.game.world.biomes.hell;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.mob.monster.Ghast;
import net.minecraft.game.entity.mob.monster.Imp;
import net.minecraft.game.entity.mob.monster.slime.MagmaCube;
import net.minecraft.game.entity.mob.monster.zombie.PigmanZombie;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.game.world.generation.features.trees.misc.ScorchedTree;
import net.minecraft.game.world.generation.noise.OctaveNoise;
import net.minecraft.game.world.mobspawner.SpawnListEntry;

public class HellBiome extends Biome {
    private long noiseSeed;

    public HellBiome(int biomeID) {
        super(biomeID);
        this.lavaAttempts = 4;
        this.topBlock = this.caveBlock1 = (short)BlockList.netherrack.id;
        this.fillerBlock = this.caveBlock2 = (short)BlockList.netherrack.id;
        this.defaultLiquid = BlockList.lavaStill.id;
        this.beachBlock1 = (short)BlockList.slowSand.id;
        this.goldLumpAttempts = 10;
        this.diamondLumpAttempts = 5;
        this.grassMeta = 4;
        this.tallGrassAttempts = 0;
        this.treeAttempts = -3;
        this.animals.clear();
        this.monsters.clear();
        this.seaCreatures.clear();
        this.monsters.add(new SpawnListEntry(Ghast.class, 12));
        this.monsters.add(new SpawnListEntry(PigmanZombie.class, 10));
        this.monsters.add(new SpawnListEntry(MagmaCube.class, 6));
        if (biomeID == 60) {
            this.monsters.add(new SpawnListEntry(Imp.class, 8));
        }
    }

    @Override
    public Tree defaultTreeType(Random rand) {
        return new ScorchedTree();
    }

    @Override
    public int getSkyColor() {
        return 8327698;
    }

    @Override
    public void caveBuilder(World world, Random random, short[] blocks, byte[] metadata, int x, int z, double heightValue) {
        int top = this.topBlock;
        byte topMetadata = (byte)(this.topMeta & 0xFF);
        int filler = this.fillerBlock;
        byte fillerMetadata = (byte)(this.fillerMeta & 0xFF);
        int stoneHeight = -1;
        if (this.noiseSeed != world.getSeed()) {
            this.noiseSeed = world.getSeed();
            Random rand = new Random(this.noiseSeed);
            this.caveNoise = new OctaveNoise(rand, 4);
        }

        boolean generateStones = this.caveNoise.generateNoise2D(x, z) > 0.0;
        int height = (int)(heightValue / 3.0 + 3.0 + random.nextDouble() * 0.25);
        int chunkX = x & 15;
        int chunkZ = z & 15;

        for (int y = 0; y < world.getWorldHeight() - 8; y++) {
            int index = y << 8 | chunkZ << 4 | chunkX;
            short block = blocks[index];
            short meta = metadata[index];
            if (block > 0) {
                if (block == BlockList.netherrack.id && meta == 0) {
                    if (stoneHeight == -1) {
                        stoneHeight = height;
                        short var22;
                        if (generateStones) {
                            var22 = this.caveBlock1;
                            topMetadata = this.caveMeta1;
                            filler = this.caveBlock1;
                            fillerMetadata = this.caveMeta1;
                        } else {
                            var22 = this.caveBlock2;
                            topMetadata = this.caveMeta2;
                            filler = this.caveBlock2;
                            fillerMetadata = this.caveMeta2;
                        }

                        blocks[index] = (short)var22;
                        metadata[index] = topMetadata;
                    } else if (stoneHeight > 0) {
                        stoneHeight--;
                        blocks[index] = (short)filler;
                        metadata[index] = fillerMetadata;
                    }
                }
            } else {
                stoneHeight = -1;
            }
        }

        for (int y = world.getWorldHeight() - 1; y >= 0; y--) {
            int index = y << 8 | chunkZ << 4 | chunkX;
            short block = blocks[index];
            short meta = metadata[index];
            if (block > 0) {
                if (block == BlockList.netherrack.id && meta == 0) {
                    if (stoneHeight == -1) {
                        stoneHeight = height;
                        if (this.biomeCaves) {
                            short var23;
                            if (generateStones) {
                                var23 = this.topBlock;
                                topMetadata = this.topMeta;
                                filler = this.fillerBlock;
                                fillerMetadata = this.fillerMeta;
                            } else {
                                var23 = this.caveBlock2;
                                topMetadata = this.caveMeta2;
                                filler = this.caveBlock2;
                                fillerMetadata = this.caveMeta2;
                            }

                            blocks[index] = (short)var23;
                            metadata[index] = topMetadata;
                        } else {
                            int var24 = this.topBlock;
                            topMetadata = this.topMeta;
                            filler = this.fillerBlock;
                            fillerMetadata = this.fillerMeta;
                            blocks[index] = (short)var24;
                            metadata[index] = topMetadata;
                        }
                    } else if (stoneHeight > 0) {
                        stoneHeight--;
                        blocks[index] = (short)filler;
                        metadata[index] = fillerMetadata;
                    }
                }
            } else {
                stoneHeight = -1;
            }
        }
    }
}
