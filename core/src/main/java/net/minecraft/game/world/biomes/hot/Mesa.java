package net.minecraft.game.world.biomes.hot;

import java.util.Arrays;
import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.mob.animal.flying.Vulture;
import net.minecraft.game.entity.mob.monster.creeper.CreeperFire;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.game.world.generation.features.plants.GrassCluster;
import net.minecraft.game.world.generation.features.plants.PumpkinPatch;
import net.minecraft.game.world.generation.features.plants.TallPlants;
import net.minecraft.game.world.generation.features.trees.misc.Shrub;
import net.minecraft.game.world.generation.noise.OctaveNoise;
import net.minecraft.game.world.generation.noise.PerlinNoise;
import net.minecraft.game.world.mobspawner.SpawnListEntry;

public class Mesa extends HotBiome {
    private PerlinNoise layerNoiseGen;
    long randomSeed;
    private Mesa.ClayType[] clayTypes = new Mesa.ClayType[]{
        new Mesa.ClayType(BlockList.hardenedClay.id, 0),
        new Mesa.ClayType(BlockList.hardenedClay.id, 15),
        new Mesa.ClayType(BlockList.hardenedClay.id, 14),
        new Mesa.ClayType(BlockList.hardenedClay.id, 18),
        new Mesa.ClayType(BlockList.hardenedClay.id, 16),
        new Mesa.ClayType(BlockList.hardenedClay.id, 1),
        new Mesa.ClayType(BlockList.hardenedClay.id, 2)
    };
    private byte[] clayLayers;

    public Mesa(int biomeID) {
        super(biomeID);
        this.goldLumpAttempts = 10;
        this.goldLumpMaxHeight = 64;
        this.stoneLayer = -1;
        this.treeAttempts = 3;
        this.tallGrassAttempts = 1;
        this.caveFloor = 32;
        this.genBeaches = false;
        this.caveMeta2 = 2;
        this.topBlock = (short)BlockList.redSand.id;
        this.fillerBlock = (short)BlockList.redSand.id;
        this.hasBeaches = false;
        this.animals.clear();
        this.animals.add(new SpawnListEntry(Vulture.class, 8));
        this.monsters.add(new SpawnListEntry(CreeperFire.class, 4));
    }

    @Override
    public void prePopulate(World world, Random rand, int x0, int z0) {
        for (int i = 0; i < 20; i++) {
            int x = x0 + rand.nextInt(16) + 8;
            int y = rand.nextInt(world.getWorldHeight());
            int z = z0 + rand.nextInt(16) + 8;
            TallPlants.CACTUS.place(world, rand, x, y, z);
        }

        for (int var17 = 0; var17 < 4; var17++) {
            int z = x0 + rand.nextInt(16) + 8;
            int x = rand.nextInt(world.getWorldHeight());
            int y = z0 + rand.nextInt(16) + 8;
            GrassCluster.deadBush.place(world, rand, z, x, y);
        }

        if (this.id == BiomeList.plateau.id) {
            int y = x0 + rand.nextInt(16) + 8;
            int z = rand.nextInt(world.getWorldHeight());
            int x = z0 + rand.nextInt(16) + 8;
            PumpkinPatch.pumpkinPatch.place(world, rand, y, z, x);
            y = x0 + rand.nextInt(16) + 8;
            x = z0 + rand.nextInt(16) + 8;
            Shrub.baobabShrub.place(world, rand, y, world.getHeightmap(y, x), x);
        }
    }

    @Override
    public void surfaceBuilder(
        World world, Random rand, short[] blocks, byte[] metadata, int xAbs, int zAbs, double heightValue, double sandValue, double gravelValue
    ) {
        if (this.randomSeed != world.getSeed() || this.clayLayers == null) {
            this.calculateClayLayers(world.getSeed());
        }

        if (this.randomSeed != world.getSeed() || this.caveNoise == null) {
            this.randomSeed = world.getSeed();
            this.caveNoise = new OctaveNoise(new Random(this.randomSeed), 4);
        }

        int seaLevel = world.getSeaLevel();
        int x = xAbs & 15;
        int z = zAbs & 15;
        this.yOffset = 64;
        if (world.getMaxSections() < 20) {
            this.yOffset = 32;
        }

        int offset = (int)(heightValue / 3.0 + 3.0 + rand.nextDouble() * 0.25);
        int stoneHeight = -1;
        boolean dirtOrGrass = Math.cos(heightValue / 6.0 * Math.PI) > 0.0;
        short topBlock = this.topBlock;
        byte topMeta = this.topMeta;
        short fillerBlock = this.fillerBlock;
        byte fillerMeta = this.fillerMeta;
        boolean overrideFiller = false;

        for (int y = world.getWorldHeight() - 1; y >= 0; y--) {
            if (y < world.getSeaLevel() - 10) {
                super.surfaceBuilder(world, rand, blocks, metadata, xAbs, zAbs, heightValue, sandValue, gravelValue);
                return;
            }

            int index = y << 8 | z << 4 | x;
            short blockID = blocks[index];
            if (blockID == 0) {
                stoneHeight = -1;
            } else if (blockID == BlockList.stone.id) {
                if (stoneHeight == -1) {
                    if (offset <= 0) {
                        topBlock = 0;
                        fillerBlock = (byte)BlockList.stone.id;
                    } else if (y >= seaLevel - this.minBeachHeight && y <= seaLevel + this.maxBeachHeight) {
                        topBlock = (short)this.getTopBlock();
                        topMeta = this.getTopMeta();
                        fillerBlock = (short)this.getFillerBlock();
                        fillerMeta = this.getFillerMeta();
                    }

                    if (y < world.getSeaLevel() - 1 && topBlock == 0) {
                        topBlock = (short)this.defaultLiquid;
                        topMeta = 0;
                    }

                    stoneHeight = offset + Math.max(0, y - (world.getSeaLevel() - 1));
                    if (y >= seaLevel - this.caveCeiling) {
                        if (y >= world.getSeaLevel() + 22 + offset * 2) {
                            if (dirtOrGrass && this.id != BiomeList.plateau.id) {
                                Mesa.ClayType clayType = this.clayTypes[0];
                                if (y >= world.getSeaLevel() && !dirtOrGrass) {
                                    clayType = this.getClayType(xAbs, y, zAbs);
                                }

                                blocks[index] = clayType.blockID;
                                metadata[index] = clayType.metadata;
                            } else {
                                blocks[index] = (short)BlockList.grass.id;
                                metadata[index] = 3;
                            }
                        } else if (y > world.getSeaLevel() + 2 + offset) {
                            Mesa.ClayType clayType = this.clayTypes[0];
                            if (y >= world.getSeaLevel() && !dirtOrGrass) {
                                clayType = this.getClayType(xAbs, y, zAbs);
                            }

                            blocks[index] = clayType.blockID;
                            metadata[index] = clayType.metadata;
                        } else {
                            blocks[index] = topBlock;
                            metadata[index] = topMeta;
                            overrideFiller = true;
                        }
                    } else {
                        blocks[index] = fillerBlock;
                        metadata[index] = fillerMeta;
                    }
                } else if (stoneHeight > 0) {
                    stoneHeight--;
                    if (overrideFiller) {
                        blocks[index] = (short)BlockList.sandStone.id;
                        metadata[index] = 2;
                    } else {
                        Mesa.ClayType clayType = this.getClayType(xAbs, y, zAbs);
                        blocks[index] = clayType.blockID;
                        metadata[index] = clayType.metadata;
                    }
                }
            }
        }
    }

    private void calculateClayLayers(long seed) {
        this.clayLayers = new byte[64];
        Arrays.fill(this.clayLayers, (byte)0);
        Random rand = new Random(seed);
        this.layerNoiseGen = new PerlinNoise(rand);

        for (int layer = 0; layer < 64; layer++) {
            layer += rand.nextInt(5) + 1;
            if (layer < 64) {
                this.clayLayers[layer] = 1;
            }
        }

        int attempts = rand.nextInt(4) + 2;

        for (int i = 0; i < attempts; i++) {
            int totalLayers = rand.nextInt(3) + 1;
            int baseLayer = rand.nextInt(64);

            for (int layer = 0; baseLayer + layer < 64 && layer < totalLayers; layer++) {
                this.clayLayers[baseLayer + layer] = 2;
            }
        }

        attempts = rand.nextInt(4) + 2;

        for (int i = 0; i < attempts; i++) {
            int totalLayers = rand.nextInt(3) + 2;
            int baseLayer = rand.nextInt(64);

            for (int layer = 0; baseLayer + layer < 64 && layer < totalLayers; layer++) {
                this.clayLayers[baseLayer + layer] = 3;
            }
        }

        attempts = rand.nextInt(4) + 2;

        for (int i = 0; i < attempts; i++) {
            int totalLayers = rand.nextInt(3) + 1;
            int baseLayer = rand.nextInt(64);

            for (int layer = 0; baseLayer + layer < 64 && layer < totalLayers; layer++) {
                this.clayLayers[baseLayer + layer] = 4;
            }
        }

        attempts = rand.nextInt(3) + 3;
        int baseLayer = 0;

        for (int i = 0; i < attempts; i++) {
            baseLayer += rand.nextInt(16) + 4;

            for (int layer = 0; baseLayer + layer < 64 && layer < 1; layer++) {
                this.clayLayers[baseLayer + layer] = 5;
                if (baseLayer + layer > 1 && rand.nextBoolean()) {
                    this.clayLayers[baseLayer + layer - 1] = 6;
                }

                if (baseLayer + layer < 63 && rand.nextBoolean()) {
                    this.clayLayers[baseLayer + layer + 1] = 6;
                }
            }
        }
    }

    private Mesa.ClayType getClayType(int x, int y, int z) {
        int fromNoise = (int)Math.round(this.layerNoiseGen.generateNoise(x * 1.0 / 512.0, x * 1.0 / 512.0) * 2.0);
        return this.clayTypes[this.clayLayers[(y + fromNoise + 128) % 64]];
    }

    @Override
    public int getSkyColor() {
        return 14336931;
    }

    private class ClayType {
        protected short blockID;
        protected byte metadata;

        public ClayType(int blockID, int metadata) {
            this.blockID = (short)blockID;
            this.metadata = (byte)metadata;
        }
    }
}
