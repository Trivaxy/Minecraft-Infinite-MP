package net.minecraft.game.world.generation;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.building.Sand;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.game.world.chunk.BlockPosition;
import net.minecraft.game.world.chunk.Chunk;
import net.minecraft.game.world.chunk.ChunkLoader;
import net.minecraft.game.world.generation.features.carvers.CaveCarver;
import net.minecraft.game.world.generation.features.carvers.RavineCarver;
import net.minecraft.game.world.generation.features.general.Pond;
import net.minecraft.game.world.generation.features.general.Waterfall;
import net.minecraft.game.world.generation.features.plants.FlowerPatch;
import net.minecraft.game.world.generation.features.plants.GrassCluster;
import net.minecraft.game.world.generation.features.sky.Cloud;
import net.minecraft.game.world.generation.features.sky.Disk;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.game.world.generation.features.trees.fungal.BigMushroom;
import net.minecraft.game.world.generation.features.underground.OreLump;
import net.minecraft.game.world.generation.noise.OctaveNoise;
import net.minecraft.game.world.generation.structures.Structure;
import net.minecraft.game.world.generation.structures.StructureList;
import net.minecraft.game.world.util.LightLayer;
import net.minecraft.util.ProgressBar;

public class SkyGenerator implements ChunkLoader {
    private World world;
    private Random random;
    public StructureList featureProvider;
    protected Biome[] biomes;
    private OctaveNoise minLimitNoise;
    private OctaveNoise maxLimitNoise;
    protected OctaveNoise beachNoise;
    protected OctaveNoise surfaceHeightNoise;
    private OctaveNoise mainNoise;
    public OctaveNoise scaleNoise;
    public OctaveNoise depthNoise;
    public OctaveNoise treeDensityNoise;
    private CaveCarver caveGenerator = new CaveCarver();
    private RavineCarver ravineGenerator = new RavineCarver();
    private double[] mainNoiseSample;
    double[] noiseArray;
    double[] minLimitNoiseSample;
    double[] maxLimitNoiseSample;
    double[] scaleSample;
    double[] depthSample;
    private boolean isDecorating;

    public SkyGenerator(World w, long s) {
        this.world = w;
        this.random = new Random(s);
        this.beachNoise = new OctaveNoise(this.random, 4);
        this.surfaceHeightNoise = new OctaveNoise(this.random, 4);
        this.minLimitNoise = new OctaveNoise(this.random, 16);
        this.maxLimitNoise = new OctaveNoise(this.random, 16);
        this.mainNoise = new OctaveNoise(this.random, 8);
        this.scaleNoise = new OctaveNoise(this.random, 10);
        this.depthNoise = new OctaveNoise(this.random, 16);
        this.treeDensityNoise = new OctaveNoise(this.random, 8);
        this.featureProvider = new StructureList(this.world, this);
    }

    public void generateTerrain(int chunkX, int chunkZ, short[] blocks, byte[] metadata) {
        byte offset = 2;
        int offsetPlusOne = offset + 1;
        byte hghtMedian = (byte)(this.world.getWorldHeight() / 4 + 2);
        this.mainNoiseSample = this.initializeNoise(this.mainNoiseSample, chunkX * offset, 0, chunkZ * offset, offsetPlusOne, hghtMedian, offsetPlusOne);

        for (int x = 0; x < offset; x++) {
            for (int z = 0; z < offset; z++) {
                for (int y = 0; y < this.world.getWorldHeight() / 4; y++) {
                    double mul = 0.25;
                    double upper1 = this.mainNoiseSample[((x + 0) * offsetPlusOne + z + 0) * hghtMedian + y + 0];
                    double upper2 = this.mainNoiseSample[((x + 0) * offsetPlusOne + z + 1) * hghtMedian + y + 0];
                    double upper3 = this.mainNoiseSample[((x + 1) * offsetPlusOne + z + 0) * hghtMedian + y + 0];
                    double upper4 = this.mainNoiseSample[((x + 1) * offsetPlusOne + z + 1) * hghtMedian + y + 0];
                    double lower1 = (this.mainNoiseSample[((x + 0) * offsetPlusOne + z + 0) * hghtMedian + y + 1] - upper1) * mul;
                    double lower2 = (this.mainNoiseSample[((x + 0) * offsetPlusOne + z + 1) * hghtMedian + y + 1] - upper2) * mul;
                    double lower3 = (this.mainNoiseSample[((x + 1) * offsetPlusOne + z + 0) * hghtMedian + y + 1] - upper3) * mul;
                    double lower4 = (this.mainNoiseSample[((x + 1) * offsetPlusOne + z + 1) * hghtMedian + y + 1] - upper4) * mul;

                    for (int blobY = 0; blobY < 4; blobY++) {
                        double innerMul = 0.125;
                        double hi1 = upper1;
                        double hi2 = upper2;
                        double diff1 = (upper3 - upper1) * innerMul;
                        double diff2 = (upper4 - upper2) * innerMul;

                        for (int blobX = 0; blobX < 8; blobX++) {
                            int index = (y << 2) + blobY << 8 | 0 + (z << 3) << 4 | blobX + (x << 3);
                            int chunkSize = 16;
                            double height = hi1;
                            double scale = (hi2 - hi1) * innerMul;

                            for (int blobZ = 0; blobZ < 8; blobZ++) {
                                int id = 0;
                                if (height > 0.0) {
                                    id = BlockList.stone.id;
                                }

                                blocks[index] = (byte)id;
                                metadata[index] = 8;
                                index += chunkSize;
                                height += scale;
                            }

                            hi1 += diff1;
                            hi2 += diff2;
                        }

                        upper1 += lower1;
                        upper2 += lower2;
                        upper3 += lower3;
                        upper4 += lower4;
                    }
                }
            }
        }
    }

    protected double[] initializeNoise(double[] density, int xStart, int yStart, int zStart, int xSamples, int ySamples, int zSamples) {
        if (density == null) {
            density = new double[xSamples * ySamples * zSamples];
        }

        double horizontalScale = 684.412;
        double verticalScale = 684.412;
        this.scaleSample = this.scaleNoise.generateNoise(this.scaleSample, xStart, zStart, xSamples, zSamples, 1.121, 1.121, 0.5);
        this.depthSample = this.depthNoise.generateNoise(this.depthSample, xStart, zStart, xSamples, zSamples, 200.0, 200.0, 0.5);
        horizontalScale *= 2.0;
        double yOffset = 80.0;
        if (this.world.getWorldHeight() < 256) {
            yOffset = 160.0;
        }

        this.noiseArray = this.mainNoise
            .generateNoiseOctaves(
                this.noiseArray,
                (double)xStart,
                (double)yStart,
                (double)zStart,
                xSamples,
                ySamples,
                zSamples,
                horizontalScale / 80.0,
                verticalScale / yOffset,
                horizontalScale / 80.0
            );
        this.minLimitNoiseSample = this.minLimitNoise
            .generateNoiseOctaves(
                this.minLimitNoiseSample,
                (double)xStart,
                (double)yStart,
                (double)zStart,
                xSamples,
                ySamples,
                zSamples,
                horizontalScale,
                verticalScale,
                horizontalScale
            );
        this.maxLimitNoiseSample = this.maxLimitNoise
            .generateNoiseOctaves(
                this.maxLimitNoiseSample,
                (double)xStart,
                (double)yStart,
                (double)zStart,
                xSamples,
                ySamples,
                zSamples,
                horizontalScale,
                verticalScale,
                horizontalScale
            );
        int index = 0;
        int scaleDepth = 0;

        for (int x = 0; x < xSamples; x++) {
            for (int z = 0; z < zSamples; z++) {
                double smoothScale = (this.scaleSample[scaleDepth] + 256.0) / 512.0;
                if (smoothScale > 1.0) {
                    smoothScale = 1.0;
                }

                double depthScale = this.depthSample[scaleDepth] / 8000.0;
                if (depthScale < 0.0) {
                    depthScale = -depthScale * 0.3;
                }

                depthScale = depthScale * 3.0 - 2.0;
                if (depthScale > 1.0) {
                    depthScale = 1.0;
                }

                depthScale /= 8.0;
                depthScale = 0.0;
                if (smoothScale < 0.0) {
                    smoothScale = 0.0;
                }

                smoothScale += 0.5;
                depthScale = depthScale * ySamples / 16.0;
                scaleDepth++;
                double halfHeight = ySamples / 2.0;

                for (int y = 0; y < ySamples; y++) {
                    double scale = 0.0;
                    double height = (y - halfHeight) * 8.0 / smoothScale;
                    if (height < 0.0) {
                        height *= -1.0;
                    }

                    double min = this.minLimitNoiseSample[index] / 512.0;
                    double max = this.maxLimitNoiseSample[index] / 512.0;
                    double noiseValue = (this.noiseArray[index] / 10.0 + 1.0) / 2.0;
                    if (noiseValue < 0.0) {
                        scale = min;
                    } else if (noiseValue > 1.0) {
                        scale = max;
                    } else {
                        scale = min + (max - min) * noiseValue;
                    }

                    scale -= 8.0;
                    byte bits = 32;
                    if (y > ySamples - bits) {
                        double multiplier = (y - (ySamples - bits)) / (bits - 1.0F);
                        scale = scale * (1.0 - multiplier) + -30.0 * multiplier;
                    }

                    bits = 8;
                    if (y < bits) {
                        double multiplier = (bits - y) / (bits - 1.0F);
                        scale = scale * (1.0 - multiplier) + -30.0 * multiplier;
                    }

                    density[index] = scale;
                    index++;
                }
            }
        }

        return density;
    }

    public int[] generateLandSurfaceHeightMap(short[] blockArray) {
        int index = 0;
        int[] landSurfaceHeightMap = new int[256];

        for (int z = 0; z < 16; z++) {
            for (int x = 0; x < 16; x++) {
                int y = this.world.getWorldHeight() - 1;

                for (int layerIndex = y << 8; Block.lightOpacity[blockArray[index | layerIndex] & 4095] < 255 && y > 0; layerIndex -= 256) {
                    y--;
                }

                landSurfaceHeightMap[index] = y;
                index++;
            }
        }

        return landSurfaceHeightMap;
    }

    public void replaceBlocks(int chunkX, int chunkZ, short[] blocks, byte[] metadata, Biome[] biomes) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                Biome biomeGen = biomes[z << 4 | x];
                int xx = chunkX << 4 | x;
                int zz = chunkZ << 4 | z;
                double offset = 0.03125;
                double sandValue = this.beachNoise.generateNoise3D(xx * offset, zz * offset, 0.0);
                double gravelValue = this.beachNoise.generateNoise3D(xx * offset, 109.0134, zz * offset);
                double heightValue = this.surfaceHeightNoise.generateNoise2D(xx * offset * 2.0, zz * offset * 2.0);
                biomeGen.surfaceBuilder(this.world, this.random, blocks, metadata, xx, zz, heightValue, sandValue, gravelValue);
                biomeGen.caveBuilder(this.world, this.random, blocks, metadata, xx, zz, heightValue);
            }
        }
    }

    @Override
    public Chunk generateChunk(int chunkX, int chunkZ) {
        Sand.can_fall = false;
        this.random.setSeed(chunkX * 341873128712L + chunkZ * 132897987541L);
        short[] blockArray = new short[256 * this.world.getWorldHeight()];
        byte[] metadataArray = new byte[256 * this.world.getWorldHeight()];
        this.biomes = this.world.getBiomeProvider().loadBiomes(this.biomes, chunkX << 4, chunkZ << 4, 16, 16);
        this.generateTerrain(chunkX, chunkZ, blockArray, metadataArray);
        this.replaceBlocks(chunkX, chunkZ, blockArray, metadataArray, this.biomes);
        this.caveGenerator.generate(this, this.world, chunkX, chunkZ, blockArray, metadataArray);
        this.ravineGenerator.generate(this, this.world, chunkX, chunkZ, blockArray, metadataArray);
        if (this.world.getSettings().generateStructures()) {
            this.featureProvider.generateNearestFeatures(chunkX, chunkZ, blockArray, metadataArray, this.biomes);
        }

        Chunk chunk = new Chunk(this.world, blockArray, metadataArray, chunkX, chunkZ);
        chunk.generateHeightMap();
        chunk.generateSkylightMap();
        byte[] biomeArray = chunk.getBiomes();

        for (int i = 0; i < biomeArray.length; i++) {
            biomeArray[i] = (byte)this.biomes[i].id;
        }

        chunk.setBiomes(biomeArray);
        Sand.can_fall = true;
        return chunk;
    }

    @Override
    public Chunk justGenerateForHeight(int chunkX, int chunkZ) {
        this.random.setSeed(chunkX * 341873128712L + chunkZ * 132897987541L);
        short[] blockArray = new short[256 * this.world.getWorldHeight()];
        byte[] metadataArray = new byte[256 * this.world.getWorldHeight()];
        this.generateTerrain(chunkX, chunkZ, blockArray, metadataArray);
        int[] landSurfaceHeightMap = this.generateLandSurfaceHeightMap(blockArray);
        Chunk chunk = new Chunk(this.world, blockArray, metadataArray, chunkX, chunkZ);
        chunk.landSurfaceHeightMap(landSurfaceHeightMap);
        return chunk;
    }

    @Override
    public void populate(ChunkLoader chunkProvider, int chunkX, int chunkZ) {
        Biome biome = this.world.getBiomeProvider().getBiomeAt((chunkX << 4) + 16, (chunkZ << 4) + 16);
        int x0 = chunkX * 16;
        int z0 = chunkZ * 16;
        this.random.setSeed(this.world.getSeed());
        long rn1 = this.random.nextLong() / 2L * 2L + 1L;
        long rn2 = this.random.nextLong() / 2L * 2L + 1L;
        this.random.setSeed(chunkX * rn1 + chunkZ * rn2 ^ this.world.getSeed());
        if (this.isDecorating) {
            System.out.println(chunkX + "x " + chunkZ + "z");
        }

        this.isDecorating = true;
        biome.prePopulate(this.world, this.random, x0, z0);
        if (this.world.settings.generateStructures()) {
            this.featureProvider.populateFeatures(this.world, this.random, chunkX, chunkZ);
        }

        this.populateLiquids(biome, x0, z0);
        this.populateOres(biome, x0, z0);
        this.populatePlants(biome, x0, z0);
        if (this.random.nextInt(3) == 0) {
            int x = x0 + this.random.nextInt(16) + 8;
            int y = this.random.nextInt(this.world.getWorldHeight());
            int z = z0 + this.random.nextInt(16) + 8;
            Cloud.SMALL_CLOUD.place(this.world, this.random, x, y, z);
        }

        for (int i = 0; i < 500; i++) {
            int x = x0 + this.random.nextInt(16) + 8;
            int y = this.random.nextInt(this.world.getWorldHeight());
            int z = z0 + this.random.nextInt(16) + 8;
            if (this.world.getBlockId(x, y, z) == 0 && this.world.getBlockId(x, y + 1, z) == BlockList.grass.id && this.world.getBlockId(x, y + 2, z) == 0) {
                Disk.INSTANCE.place(this.world, this.random, x, y, z);
            }
        }

        this.populateSnow(biome, x0, z0);
        biome.postPopulate(this.world, this.random, x0, z0);
        this.isDecorating = false;
    }

    private void populateLiquids(Biome biome, int x0, int z0) {
        if (biome.generateWater) {
            if (this.random.nextInt(biome.waterAttempts) == 0) {
                int x = x0 + this.random.nextInt(16) + 8;
                int y = this.random.nextInt(this.world.getWorldHeight());
                int z = z0 + this.random.nextInt(16) + 8;
                Pond.WATER.place(this.world, this.random, x, y, z);
            }

            for (int i = 0; i < biome.waterFallAttempts; i++) {
                int x = x0 + this.random.nextInt(16) + 8;
                int y = this.random.nextInt(this.world.getWorldHeight());
                int z = z0 + this.random.nextInt(16) + 8;
                Waterfall.WATERFALL.place(this.world, this.random, x, y, z);
            }
        }
    }

    private void populateOres(Biome biomeGen, int x0, int z0) {
        for (int i = 0; i < biomeGen.dirtLumpAttempts; i++) {
            int x = x0 + this.random.nextInt(16);
            int y = this.random.nextInt(this.world.getWorldHeight());
            int z = z0 + this.random.nextInt(16);
            OreLump.aerDirt.place(this.world, this.random, x, y, z);
        }

        for (int var20 = 0; var20 < biomeGen.marbleLumpAttempts; var20++) {
            int x = x0 + this.random.nextInt(8) + 4;
            int y = this.random.nextInt(this.world.getWorldHeight());
            int z = z0 + this.random.nextInt(8) + 4;
            new OreLump(BlockList.stone.id, 48, BlockList.stone.id, biomeGen.defaultStoneType, 8).place(this.world, this.random, x, y, z);
        }

        for (int var21 = 0; var21 < biomeGen.ironLumpAttempts; var21++) {
            int x = x0 + this.random.nextInt(16);
            int y = this.random.nextInt(this.world.getWorldHeight());
            int z = z0 + this.random.nextInt(16);
            OreLump.aerIron.place(this.world, this.random, x, y, z);
        }

        for (int var22 = 0; var22 < biomeGen.goldLumpAttempts; var22++) {
            int x = x0 + this.random.nextInt(16);
            int y = this.random.nextInt(this.world.getWorldHeight());
            int z = z0 + this.random.nextInt(16);
            OreLump.topaz.place(this.world, this.random, x, y, z);
        }

        for (int var23 = 0; var23 < biomeGen.diamondLumpAttempts; var23++) {
            int x = x0 + this.random.nextInt(16);
            int y = this.random.nextInt(this.world.getWorldHeight());
            int z = z0 + this.random.nextInt(16);
            OreLump.adamantine.place(this.world, this.random, x, y, z);
        }
    }

    private void populatePlants(Biome biomeGen, int x0, int z0) {
        double div = 0.5;
        int treeAttempts = (int)((this.treeDensityNoise.generateNoise2D(x0 * div, z0 * div) / 8.0 + this.random.nextDouble() * 4.0 + 4.0) / 3.0);
        if (treeAttempts < 0) {
            treeAttempts = 0;
        }

        if (this.random.nextInt(10) == 0) {
            treeAttempts++;
        }

        treeAttempts += biomeGen.treeAttempts;

        for (int i = 0; i < treeAttempts; i++) {
            int x = x0 + this.random.nextInt(16) + 8;
            int z = z0 + this.random.nextInt(16) + 8;
            int y = this.world.getHeightmap(x, z);
            Tree tre = biomeGen.getRandomTreeGenerator(this.random, y, this.world.getWorldHeight());
            if (tre != null) {
                tre.place(this.world, this.random, x, y, z);
            }
        }

        for (int var12 = 0; var12 < biomeGen.flowersAttempts; var12++) {
            int x = x0 + this.random.nextInt(16) + 8;
            int y = this.random.nextInt(this.world.getWorldHeight());
            int z = z0 + this.random.nextInt(16) + 8;
            FlowerPatch instance = FlowerPatch.cyanFlower;
            switch (this.random.nextInt(3)) {
                case 1:
                    instance = FlowerPatch.pinkFlower;
                    break;
                case 2:
                    instance = FlowerPatch.whiteFlower;
            }

            instance.place(this.world, this.random, x, y, z);
        }

        for (int var13 = 0; var13 < biomeGen.mushroomChance; var13++) {
            int x = x0 + this.random.nextInt(16) + 8;
            int y = this.random.nextInt(this.world.getWorldHeight());
            int z = z0 + this.random.nextInt(16) + 8;
            FlowerPatch.glowshrooms.place(this.world, this.random, x, y, z);
        }

        if (biomeGen.reedAttempts > 0) {
            for (int var14 = 0; var14 < biomeGen.reedAttempts; var14++) {
                int x = x0 + this.random.nextInt(16) + 8;
                int y = this.random.nextInt(this.world.getWorldHeight());
                int z = z0 + this.random.nextInt(16) + 8;
                FlowerPatch.jumpPad.place(this.world, this.random, x, y, z);
            }
        }

        for (int var15 = 0; var15 < 64; var15++) {
            int x = x0 + this.random.nextInt(16) + 8;
            int y = this.random.nextInt(this.world.getWorldHeight());
            int z = z0 + this.random.nextInt(16) + 8;
            new BigMushroom(3).place(this.world, this.random, x, y, z);
        }

        for (int var16 = 0; var16 < 8; var16++) {
            int x = x0 + this.random.nextInt(16) + 8;
            int y = this.random.nextInt(this.world.getWorldHeight());
            int z = z0 + this.random.nextInt(16) + 8;
            FlowerPatch.berryBush.place(this.world, this.random, x, y, z);
        }

        for (int var17 = 0; var17 < biomeGen.pebbleAttempts; var17++) {
            int x = x0 + this.random.nextInt(16) + 8;
            int z = z0 + this.random.nextInt(16) + 8;
            int y = this.random.nextInt(this.world.getWorldHeight());
            FlowerPatch.pebbles.place(this.world, this.random, x, y, z);
        }

        if (biomeGen.tallGrassAttempts > 0) {
            for (int var18 = 0; var18 < biomeGen.tallGrassAttempts; var18++) {
                int x = x0 + this.random.nextInt(16) + 8;
                int y = this.random.nextInt(this.world.getWorldHeight());
                int z = z0 + this.random.nextInt(16) + 8;
                new GrassCluster(BlockList.tallGrass.id, biomeGen.grassMeta).place(this.world, this.random, x, y, z);
            }

            if (biomeGen != BiomeList.skyGold) {
                for (int var19 = 0; var19 < biomeGen.tallGrassAttempts; var19++) {
                    int x = x0 + this.random.nextInt(16) + 8;
                    int y = this.random.nextInt(this.world.getWorldHeight());
                    int z = z0 + this.random.nextInt(16) + 8;
                    new GrassCluster(BlockList.tallGrass.id, biomeGen.grassMeta + 3).place(this.world, this.random, x, y, z);
                }
            }
        }
    }

    private void populateSnow(Biome biome, int x0, int z0) {
        for (int x = x0 + 8; x < x0 + 8 + 16; x++) {
            for (int z = z0 + 8; z < z0 + 8 + 16; z++) {
                int y = this.world.getTopSolidTile(x, z);
                if (y > 0 && y < this.world.getWorldHeight() && this.world.getBrightness(LightLayer.Block, x, y, z) < 10) {
                    if (this.world.getBlockMaterial(x, y - 1, z) != MaterialList.water || this.world.getBlockMetadata(x, y - 1, z) != 0) {
                        Material material = this.world.getBlockMaterial(x, y - 1, z);
                        if (this.world.isAir(x, y, z)
                            && this.world.getBlock(x, y - 1, z) != BlockList.snowLayer
                            && BlockList.snowLayer.placeableAt(this.world, x, y, z)
                            && material != MaterialList.air
                            && material != MaterialList.ice
                            && material.isSolid()
                            && this.world.canSnowAt(x, y, z, false)) {
                            int idToPlace = BlockList.snowLayer.id;
                            this.world.setBlock(x, y, z, idToPlace);
                            if (material == MaterialList.leaves) {
                                boolean stop = false;

                                for (int var11 = y - 2; !stop && var11 > 0; var11--) {
                                    int block = this.world.getBlockId(x, var11 - 1, z);
                                    if (this.world.getBrightness(LightLayer.Block, x, var11, z) < 11
                                        && block != 0
                                        && BlockList.blocks[block].material == MaterialList.water) {
                                        if (this.world.isWater(x, var11 - 1, z)) {
                                            this.world.setBlockWithNotify(x, var11 - 1, z, BlockList.ice.id);
                                        }

                                        stop = true;
                                    }
                                }
                            }
                        }
                    } else if (this.world.isWater(x, y - 1, z)) {
                        this.world.setBlockWithNotify(x, y - 1, z, BlockList.ice.id);
                    }
                }
            }
        }
    }

    @Override
    public boolean chunkExists(int x, int z) {
        return true;
    }

    @Override
    public boolean saveChunks(boolean flag, ProgressBar progress) {
        return true;
    }

    @Override
    public boolean unloadOldChunks() {
        return false;
    }

    @Override
    public boolean canSave() {
        return true;
    }

    @Override
    public String loaderName() {
        return "SkyGenerator";
    }

    @Override
    public BlockPosition findClosestStructure(World world, Class<? extends Structure> target, int x, int y, int z) {
        return this.featureProvider != null ? this.featureProvider.getNearestFeatures(target, x >> 4, z >> 4) : null;
    }
}
