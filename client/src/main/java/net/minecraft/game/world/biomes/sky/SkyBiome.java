package net.minecraft.game.world.biomes.sky;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.mob.animal.flying.Fae;
import net.minecraft.game.entity.mob.animal.mounts.horse.Pegasus;
import net.minecraft.game.entity.mob.monster.Harpy;
import net.minecraft.game.entity.mob.monster.Pigman;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.generation.features.general.Boulder;
import net.minecraft.game.world.generation.features.plants.FlowerPatch;
import net.minecraft.game.world.generation.features.plants.HangingPlants;
import net.minecraft.game.world.generation.features.plants.TallPlants;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.game.world.generation.features.trees.coniferous.CedarTree;
import net.minecraft.game.world.generation.features.trees.coniferous.FirTree;
import net.minecraft.game.world.generation.features.trees.tropical.WillowTree;
import net.minecraft.game.world.generation.noise.OctaveNoise;
import net.minecraft.game.world.mobspawner.SpawnListEntry;

public class SkyBiome extends Biome {
    protected OctaveNoise cloudNoise;
    protected OctaveNoise mainNoise;
    private long seed;

    public SkyBiome(int biomeID) {
        super(biomeID);
        this.defaultStoneType = 6;
        this.topMeta = 7;
        this.fillerMeta = 7;
        this.waterAttempts = 4;
        this.waterFallAttempts = 50;
        this.tallGrassAttempts = 1;
        this.grassMeta = 6;
        this.mushroomChance = 8;
        this.beachBlock1 = (short)BlockList.quickSoil.id;
        this.biomeCaves = true;
        this.caveBlock1 = (short)BlockList.moss.id;
        this.caveMeta1 = 1;
        this.caveMeta2 = 8;
        this.diamondLumpAttempts = 5;
        this.goldLumpAttempts = 10;
        this.pebbleAttempts = 8;
        this.reedAttempts = 5;
        this.monsters.clear();
        this.animals.clear();
        this.seaCreatures.clear();
        this.monsters.add(new SpawnListEntry(Harpy.class, 6));
        this.monsters.add(new SpawnListEntry(Pigman.class, 8));
        this.animals.add(new SpawnListEntry(Pegasus.class, 10));
        this.animals.add(new SpawnListEntry(Fae.class, 12));
    }

    @Override
    public void prePopulate(World world, Random rand, int x0, int z0) {
        if (this.hasSnow()) {
            if (rand.nextInt(2) == 0) {
                int z = x0 + rand.nextInt(16) + 8;
                int x = z0 + rand.nextInt(16) + 8;
                Boulder.ice.place(world, rand, z, rand.nextInt(world.getWorldHeight()), x);
            }

            for (int i = 0; i < 100; i++) {
                int z = x0 + rand.nextInt(16) + 8;
                int x = z0 + rand.nextInt(16) + 8;
                int y = rand.nextInt(world.getSeaLevel());
                if (world.getBlock(z, y, x) == BlockList.packedIce) {
                    HangingPlants.ICICLES.place(world, rand, z, y, x);
                }
            }
        } else {
            if (this.caveBlock1 == BlockList.moss.id) {
                for (int i = 0; i < 100; i++) {
                    int x = x0 + rand.nextInt(16) + 8;
                    int z = z0 + rand.nextInt(16) + 8;
                    int y = rand.nextInt(world.getSeaLevel());
                    if (world.getBlock(x, y, z) == BlockList.moss) {
                        HangingPlants.GLOWING.place(world, rand, x, y, z);
                    }
                }
            }

            if (rand.nextInt(2) == 0) {
                int x = x0 + rand.nextInt(16) + 8;
                int z = z0 + rand.nextInt(16) + 8;
                FlowerPatch.waterLily.place(world, rand, x, world.getSeaLevel() + 1, z);
            }

            for (int i = 0; i < 10; i++) {
                int x = x0 + rand.nextInt(16) + 8;
                int z = z0 + rand.nextInt(16) + 8;
                TallPlants.SEAGRASS.place(world, rand, x, rand.nextInt(world.getSeaLevel()), z);
                x = x0 + rand.nextInt(16) + 8;
                z = z0 + rand.nextInt(16) + 8;
                TallPlants.SEAFERN.place(world, rand, x, rand.nextInt(world.getSeaLevel()), z);
            }
        }
    }

    @Override
    public Tree defaultTreeType(Random rand) {
        if (rand.nextInt(20) == 0) {
            return new WillowTree(rand.nextInt(4) + 6);
        } else {
            return rand.nextInt(5) == 0 ? new FirTree(rand.nextInt(4) + 4, false) : new CedarTree();
        }
    }

    @Override
    public void surfaceBuilder(
        World world, Random random, short[] blocks, byte[] metadata, int x, int z, double heightValue, double sandValue, double gravelValue
    ) {
        if (world.currDim.dimension == 0) {
            super.surfaceBuilder(world, random, blocks, metadata, x, z, heightValue, sandValue, gravelValue);
        } else {
            if (this.seed != world.getSeed() || this.seed == 0L) {
                this.seed = world.getSeed();
                Random rand = new Random(this.seed);
                this.mainNoise = new OctaveNoise(rand, 8);
                this.cloudNoise = new OctaveNoise(rand, 8);
            }

            int seaLevel = world.getSeaLevel() + 1;
            int seaLevelLower = seaLevel - 16;
            int seaLevelLower2 = seaLevel - 8;
            int xAbs = x & 15;
            int zAbs = z & 15;
            double packedIceTop = 0.0;
            double packedIceBottom = 0.0;
            double packedIceBottom2 = 0.0;
            double density = Math.min(Math.abs(heightValue), this.mainNoise.generateNoise2D(x, z));
            double densityLower = Math.sqrt(Math.abs(density) / 12.0) * (1.0 - density) + density * 2.0;
            if (density > 1.5) {
                packedIceTop = seaLevel;
                packedIceBottom = densityLower * (densityLower / 2.0);
                packedIceBottom = seaLevelLower - packedIceBottom + 14.0;
                packedIceBottom2 = densityLower * (densityLower / 2.0);
                packedIceBottom2 = seaLevelLower2 - packedIceBottom2 + 14.0;
            }

            for (int y = seaLevel; y >= 0; y--) {
                int index = y << 8 | zAbs << 4 | xAbs;
                short blockID = blocks[index];
                if (blockID == 0 && y < (int)packedIceTop && y > (int)packedIceBottom) {
                    if (y < (int)packedIceTop && y > (int)packedIceBottom2) {
                        blocks[index] = (short)BlockList.waterStill.id;
                        metadata[index] = 0;
                    } else {
                        blocks[index] = (short)BlockList.stone.id;
                        metadata[index] = 8;
                    }
                }
            }

            int top = this.topBlock;
            byte topMetadata = (byte)(this.topMeta & 0xFF);
            int filler = this.fillerBlock;
            byte fillerMetadata = (byte)(this.fillerMeta & 0xFF);
            int stoneHeight = -1;
            boolean generateSand = sandValue + random.nextDouble() * 0.2 > 0.0;
            boolean generateClouds = this.cloudNoise.generateNoise2D(x, z) > 42.0;
            boolean generateCloudsLayer2 = this.cloudNoise.generateNoise2D(x, z) > 32.0;
            boolean generateCloudsLayer3 = this.cloudNoise.generateNoise2D(x, z) > 22.0;
            int height = (int)(heightValue / 3.0 + 3.0 + random.nextDouble() * 0.25);
            int chunkX = x & 15;
            int chunkZ = z & 15;
            this.yOffset = 64;
            if (world.getMaxSections() < 20) {
                this.yOffset = 32;
            }

            for (int y = world.getWorldHeight() - 1; y >= 0; y--) {
                int index = y << 8 | chunkZ << 4 | chunkX;
                int index2 = y + 1 << 8 | chunkZ << 4 | chunkX;
                short block = blocks[index];
                short meta = metadata[index];
                if (block == 0 && y == 30 && generateClouds) {
                    blocks[index] = (short)BlockList.cloud.id;
                } else if (block == 0 && y == 29 && generateCloudsLayer2) {
                    blocks[index] = (short)BlockList.cloud.id;
                } else if (block == 0 && y == 28 && generateCloudsLayer3) {
                    blocks[index] = (short)BlockList.cloud.id;
                }

                if (block > 0) {
                    if (block == BlockList.stone.id && meta == 8) {
                        if (stoneHeight == -1) {
                            short var49;
                            if (height <= this.stoneHeight) {
                                var49 = 0;
                                topMetadata = 0;
                                filler = BlockList.stone.id;
                                fillerMetadata = 8;
                            } else if (y >= world.getSeaLevel() - this.minBeachHeight && y <= world.getSeaLevel() + this.maxBeachHeight) {
                                var49 = (short)this.getTopBlock();
                                topMetadata = (byte)(this.getTopMeta() & 0xFF);
                                filler = this.getFillerBlock();
                                fillerMetadata = (byte)(this.getFillerMeta() & 0xFF);
                                if (generateSand) {
                                    var49 = this.beachBlock1;
                                    topMetadata = this.beachMeta1;
                                    filler = this.beachBlock1;
                                    fillerMetadata = this.beachMeta1;
                                } else {
                                    var49 = this.topBlock;
                                    topMetadata = this.topMeta;
                                    filler = this.fillerBlock;
                                    fillerMetadata = this.fillerMeta;
                                }
                            } else {
                                var49 = this.topBlock;
                                topMetadata = this.topMeta;
                                filler = this.fillerBlock;
                                fillerMetadata = this.fillerMeta;
                            }

                            stoneHeight = height;
                            blocks[index] = (short)var49;
                            metadata[index] = topMetadata;
                            if (this.hasSnow()
                                && height > this.stoneHeight
                                && var49 != BlockList.packedIce.id
                                && var49 != BlockList.powderSnow.id
                                && blocks[index2] == 0) {
                                blocks[index2] = (short)BlockList.snowLayer.id;
                                metadata[index2] = 0;
                            } else if (blocks[index2] == BlockList.ice.id
                                || blocks[index2] == BlockList.waterMoving.id
                                || blocks[index2] == BlockList.waterStill.id) {
                                blocks[index] = (short)filler;
                                metadata[index] = fillerMetadata;
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

    @Override
    public void caveBuilder(World world, Random random, short[] blocks, byte[] metadata, int x, int z, double heightValue) {
        int top = this.topBlock;
        byte topMetadata = (byte)(this.topMeta & 0xFF);
        int filler = this.fillerBlock;
        byte fillerMetadata = (byte)(this.fillerMeta & 0xFF);
        int stoneHeight = -1;
        if (this.caveNoise == null) {
            this.caveNoise = new OctaveNoise(random, 4);
        }

        boolean generateStones = this.caveNoise.generateNoise2D(x, z) > 0.0;
        int height = (int)(heightValue / 3.0 + 3.0 + random.nextDouble() * 0.25);
        int chunkX = x & 15;
        int chunkZ = z & 15;
        int ymax = world.getSeaLevel();
        if (this instanceof GoldenForest) {
            ymax = world.getWorldHeight();
        }

        int ystart = this.yOffset;

        for (int y = ystart; y < ymax; y++) {
            int index = y << 8 | chunkZ << 4 | chunkX;
            short block = blocks[index];
            short meta = metadata[index];
            if (block > 0) {
                if (block == BlockList.stone.id && meta == 8) {
                    if (stoneHeight == -1) {
                        stoneHeight = height;
                        if (y < ymax) {
                            short var24;
                            if (generateStones) {
                                var24 = this.caveBlock1;
                                topMetadata = this.caveMeta1;
                                filler = this.caveBlock1;
                                fillerMetadata = this.caveMeta1;
                            } else {
                                var24 = this.caveBlock2;
                                topMetadata = this.caveMeta2;
                                filler = this.caveBlock2;
                                fillerMetadata = this.caveMeta2;
                            }

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

    @Override
    public int getSkyColor() {
        return 11648743;
    }
}
