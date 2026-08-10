package net.minecraft.game.world.generation;

import java.util.Arrays;
import java.util.Random;
import java.util.logging.Logger;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.building.Sand;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldType;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.game.world.biomes.cold.Fjord;
import net.minecraft.game.world.biomes.cold.Glacier;
import net.minecraft.game.world.biomes.hot.Mesa;
import net.minecraft.game.world.biomes.hot.VolcanicWasteland;
import net.minecraft.game.world.biomes.hot.Wasteland;
import net.minecraft.game.world.biomes.ocean.Beach;
import net.minecraft.game.world.biomes.ocean.FloatingIsland;
import net.minecraft.game.world.biomes.ocean.Ocean;
import net.minecraft.game.world.biomes.temperate.Woodland;
import net.minecraft.game.world.chunk.BlockPosition;
import net.minecraft.game.world.chunk.Chunk;
import net.minecraft.game.world.chunk.ChunkLoader;
import net.minecraft.game.world.generation.features.carvers.CaveCarver;
import net.minecraft.game.world.generation.features.carvers.RavineCarver;
import net.minecraft.game.world.generation.features.general.ClayLump;
import net.minecraft.game.world.generation.features.general.Pond;
import net.minecraft.game.world.generation.features.general.Puddles;
import net.minecraft.game.world.generation.features.general.Waterfall;
import net.minecraft.game.world.generation.features.plants.FlowerPatch;
import net.minecraft.game.world.generation.features.plants.GrassCluster;
import net.minecraft.game.world.generation.features.plants.TallPlants;
import net.minecraft.game.world.generation.features.structure.mineshaft.Mineshaft;
import net.minecraft.game.world.generation.features.structure.stronghold.Stronghold;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.game.world.generation.features.trees.fungal.BigMushroom;
import net.minecraft.game.world.generation.features.underground.Dungeon;
import net.minecraft.game.world.generation.features.underground.Geode;
import net.minecraft.game.world.generation.features.underground.OreLump;
import net.minecraft.game.world.generation.features.underground.Ruins;
import net.minecraft.game.world.generation.features.underground.SpikeFeature;
import net.minecraft.game.world.generation.noise.OctaveNoise;
import net.minecraft.game.world.generation.noise.PerlinNoise;
import net.minecraft.game.world.generation.structures.Structure;
import net.minecraft.game.world.generation.structures.StructureList;
import net.minecraft.game.world.util.LightLayer;
import net.minecraft.util.ProgressBar;
import net.minecraft.util.math.MathHelper;

public class OverworldGenerator implements ChunkLoader {
    protected static Logger logger = Logger.getLogger("Minecraft");
    protected final Random rand;
    protected final World world;
    public StructureList featureProvider;
    protected OctaveNoise minLimitNoise;
    protected OctaveNoise maxLimitNoise;
    protected OctaveNoise mainNoise;
    protected OctaveNoise beachNoise;
    protected OctaveNoise surfaceHeightNoise;
    protected OctaveNoise scaleNoise;
    protected OctaveNoise depthNoise;
    protected OctaveNoise treeDensityNoise;
    protected OctaveNoise caveNoise;
    protected PerlinNoise riverNoise;
    protected final CaveCarver caveGenerator = new CaveCarver();
    protected final RavineCarver ravineGenerator = new RavineCarver();
    protected final Mineshaft mineshaftGenerator = new Mineshaft();
    protected final Stronghold strongholdGenerator;
    protected Biome[] biomes;
    protected final float[] parabolicField;
    protected double[] noiseArray;
    protected double[] mainNoiseSample;
    protected double[] minLimitNoiseSample;
    protected double[] maxLimitNoiseSample;
    protected double[] scaleSample;
    protected double[] depthSample;
    protected boolean[] hitSolid = new boolean[16];
    protected boolean[] doWater = new boolean[16];
    protected double scale = 0.5;
    protected int caveHeight = 4;
    protected int waterDepth = 7;
    protected double caveScale = 2.0;
    private boolean isDecorating;

    public OverworldGenerator(World world, long seed) {
        this.world = world;
        this.rand = new Random(seed);
        this.minLimitNoise = new OctaveNoise(this.rand, 16);
        this.maxLimitNoise = new OctaveNoise(this.rand, 16);
        this.mainNoise = new OctaveNoise(this.rand, 8);
        this.beachNoise = new OctaveNoise(this.rand, 4);
        this.surfaceHeightNoise = new OctaveNoise(this.rand, 4);
        this.scaleNoise = new OctaveNoise(this.rand, 10);
        this.depthNoise = new OctaveNoise(this.rand, 16);
        this.treeDensityNoise = new OctaveNoise(this.rand, 8);
        this.caveNoise = new OctaveNoise(this.rand, 4);
        this.riverNoise = new PerlinNoise(this.rand);
        this.featureProvider = new StructureList(this.world, this);
        this.strongholdGenerator = new Stronghold(this.world);
        this.parabolicField = new float[25];

        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                this.parabolicField[(z + 2) * 5 + x + 2] = 10.0F / MathHelper.sqrt_float(x * x * z * z + 0.2F);
            }
        }
    }

    public void generateTerrain(int x, int z, short[] blocks) {
        int worldRealHeight = this.world.getWorldHeight();
        int y0 = 0;
        if (worldRealHeight > 256) {
            worldRealHeight = 256;
            y0 = 64;
        } else if (worldRealHeight > 164) {
            worldRealHeight = 164;
            y0 = 32;
        }

        byte offset = 4;
        int sectorHeight = worldRealHeight / 8;
        int seaLvl = this.world.getSeaLevel();
        int offAdjust = offset + 1;
        int heightAdjust = sectorHeight + 1;
        this.biomes = this.world.getBiomeProvider().getBiomes(this.biomes, x * offset - 2, z * offset - 2, offAdjust + 5, offAdjust + 5);
        this.noiseArray = this.initializeNoise(this.noiseArray, x * offset, 0, z * offset, offAdjust, heightAdjust, offAdjust);
        double[] minDensityMap = new double[256];

        for (int dx = 0; dx < 16; dx++) {
            for (int dz = 0; dz < 16; dz++) {
                double densityMinimum = this.getRiverDensity(x << 4 | dx, z << 4 | dz, 0.006, this.riverNoise) * 20.0;
                densityMinimum += this.getRiverDensity(x << 4 | dx, z << 4 | dz, 0.012, this.riverNoise) * 40.0;
                densityMinimum += this.getRiverDensity(x << 4 | dx, z << 4 | dz, 0.024, this.riverNoise) * 40.0;
                densityMinimum += this.getRiverDensity(x << 4 | dx, z << 4 | dz, 0.036, this.riverNoise) * 60.0;
                densityMinimum += this.getRiverDensity(x << 4 | dx, z << 4 | dz, 0.048, this.riverNoise) * 60.0;
                minDensityMap[dz << 4 | dx] = densityMinimum;
                int exceedLimit = 120;
                Biome biome = this.world.getBiomeAt(x << 4 | dx, z << 4 | dz);
                if (biome instanceof Woodland || biome instanceof Mesa || biome instanceof Glacier || biome instanceof Fjord || biome == BiomeList.crag) {
                    exceedLimit /= 2;
                }

                if (minDensityMap[dz << 4 | dx] > exceedLimit) {
                    minDensityMap[dz << 4 | dx] = exceedLimit;
                }
            }
        }

        for (int blobX = 0; blobX < offset; blobX++) {
            int xio0 = blobX * offAdjust;
            int xio1 = (blobX + 1) * offAdjust;

            for (int blobZ = 0; blobZ < offset; blobZ++) {
                int ziox0z0 = (xio0 + blobZ) * heightAdjust;
                int ziox1z0 = (xio1 + blobZ) * heightAdjust;
                int ziox0z1 = (xio0 + blobZ + 1) * heightAdjust;
                int ziox1z1 = (xio1 + blobZ + 1) * heightAdjust;
                int liquidID = BlockList.waterStill.id;
                Arrays.fill(this.hitSolid, false);

                for (int blobY = sectorHeight - 1; blobY >= 0; blobY--) {
                    for (int i = 0; i < 16; i++) {
                        this.doWater[i] = !this.hitSolid[i];
                    }

                    double multiplier = 0.125;
                    double upper1 = this.noiseArray[ziox0z0 + blobY];
                    double upper2 = this.noiseArray[ziox0z1 + blobY];
                    double upper3 = this.noiseArray[ziox1z0 + blobY];
                    double upper4 = this.noiseArray[ziox1z1 + blobY];
                    double lower1 = (this.noiseArray[ziox0z0 + blobY + 1] - upper1) * multiplier;
                    double lower2 = (this.noiseArray[ziox0z1 + blobY + 1] - upper2) * multiplier;
                    double lower3 = (this.noiseArray[ziox1z0 + blobY + 1] - upper3) * multiplier;
                    double lower4 = (this.noiseArray[ziox1z1 + blobY + 1] - upper4) * multiplier;

                    for (int blobPosY = 0; blobPosY < 8; blobPosY++) {
                        double adj = 0.25;
                        double xldmiz = upper1;
                        double xldmxz = upper2;
                        double upperN = (upper3 - upper1) * adj;
                        double upperS = (upper4 - upper2) * adj;

                        for (int blobPosX = 0; blobPosX < 4; blobPosX++) {
                            double zli = 0.25;
                            double zld = xldmiz;
                            double zla = (xldmxz - xldmiz) * zli;

                            for (int blobPosZ = 0; blobPosZ < 4; blobPosZ++) {
                                int waterCheck = blobPosX << 2 | blobPosZ;
                                int blockX = blobX << 2 | blobPosX;
                                int blockY = y0 + (blobY << 3 | blobPosY);
                                int blockZ = blobZ << 2 | blobPosZ;
                                int index = blockY << 8 | blockZ << 4 | blockX;
                                int id = 0;
                                if (blockY < seaLvl && this.doWater[waterCheck]) {
                                    id = liquidID;
                                }

                                if (zld > minDensityMap[blockZ << 4 | blockX]) {
                                    id = BlockList.stone.id;
                                    Biome biome = this.world.getBiomeAt(x << 4 | blobX, z << 4 | blobZ);
                                    if (blobY < this.waterDepth
                                        && blobPosY == 0
                                        && biome != BiomeList.ocean
                                        && biome != BiomeList.frozenOcean
                                        && biome != BiomeList.warmOcean
                                        && biome != BiomeList.crag
                                        && !(biome instanceof FloatingIsland)) {
                                        this.hitSolid[waterCheck] = true;
                                    }
                                }

                                blocks[index] = (byte)id;
                                zld += zla;
                            }

                            xldmiz += upperN;
                            xldmxz += upperS;
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

    protected double[] initializeNoise(double[] densityMap, int xStart, int yStart, int zStart, int xSamples, int ySamples, int zSamples) {
        if (densityMap == null) {
            densityMap = new double[xSamples * ySamples * zSamples];
        }

        double horizontalCoordinateScale = 684.412;
        double verticalCoordinateScale = 684.412;
        this.scaleSample = this.scaleNoise.generateNoiseOctaves(this.scaleSample, xStart, yStart, zStart, xSamples, 1, zSamples, 1.0, 0.0, 1.0);
        this.depthSample = this.depthNoise.generateNoiseOctaves(this.depthSample, xStart, yStart, zStart, xSamples, 1, zSamples, 100.0, 0.0, 100.0);
        this.mainNoiseSample = this.mainNoise
            .generateNoiseOctaves(
                this.mainNoiseSample,
                xStart,
                yStart,
                zStart,
                xSamples,
                ySamples,
                zSamples,
                horizontalCoordinateScale / 80.0,
                verticalCoordinateScale / 160.0,
                horizontalCoordinateScale / 80.0
            );
        this.minLimitNoiseSample = this.minLimitNoise
            .generateNoiseOctaves(
                this.minLimitNoiseSample,
                xStart,
                yStart,
                zStart,
                xSamples,
                ySamples,
                zSamples,
                horizontalCoordinateScale,
                verticalCoordinateScale,
                horizontalCoordinateScale
            );
        this.maxLimitNoiseSample = this.maxLimitNoise
            .generateNoiseOctaves(
                this.maxLimitNoiseSample,
                xStart,
                yStart,
                zStart,
                xSamples,
                ySamples,
                zSamples,
                horizontalCoordinateScale,
                verticalCoordinateScale,
                horizontalCoordinateScale
            );
        int index = 0;
        int scaleDepthIndex = 0;
        int multiplier = 1;
        if (this.world.getSettings().amplified()) {
            multiplier = 4;
        }

        for (int x = 0; x < xSamples; x++) {
            for (int z = 0; z < zSamples; z++) {
                double smoothScale = 0.0;
                double smoothDepth = 0.0;
                double sumParabola = 0.0;
                byte biomeSmootheningRadius = 2;
                Biome sampleBiome = this.biomes[(z + 2) * (5 + xSamples) + x + 2];

                for (int bx = -biomeSmootheningRadius; bx <= biomeSmootheningRadius; bx++) {
                    for (int bz = -biomeSmootheningRadius; bz <= biomeSmootheningRadius; bz++) {
                        Biome smoothBiome = this.biomes[(z + bz + 2) * (5 + xSamples) + x + bx + 2];
                        double biomeScale = this.parabolicField[(bz + 2) * 5 + bx + 2] / (smoothBiome.depth + 2.0);
                        if (smoothBiome.depth > sampleBiome.depth) {
                            biomeScale /= 2.0;
                        }

                        smoothScale += smoothBiome.scale * biomeScale;
                        smoothDepth += smoothBiome.depth * biomeScale;
                        sumParabola += biomeScale;
                    }
                }

                smoothScale /= sumParabola;
                smoothDepth /= sumParabola;
                smoothScale = smoothScale * 0.9F + 0.1F;
                smoothDepth = (smoothDepth * 4.0 - 1.0) / 8.0;
                double scale = (this.scaleSample[scaleDepthIndex] + 256.0) / 512.0;
                if (scale > 1.0) {
                    scale = 1.0;
                }

                double depth = this.depthSample[scaleDepthIndex] / 8000.0;
                if (depth < 0.0) {
                    depth = -depth;
                }

                depth = depth * 3.0 - 2.0;
                if (depth < 0.0) {
                    depth /= 2.0;
                    if (depth < -1.0) {
                        depth = -1.0;
                    }

                    depth /= 1.4;
                    depth /= 2.0;
                } else {
                    if (depth > 1.0) {
                        depth = 1.0;
                    }

                    depth /= 6.0;
                }

                scale += this.scale;
                scaleDepthIndex++;
                int stretch = Math.min(ySamples, 17);

                for (int y = 0; y < ySamples; y++) {
                    double multipliedDepth = smoothDepth;
                    double multipliedScale = smoothScale;
                    multipliedScale += scale * 0.2;
                    multipliedDepth += depth * 0.2;
                    multipliedDepth = multipliedDepth * stretch / 16.0;
                    double processedDensity = stretch / 2.0 + multipliedDepth * 4.0;
                    double density = 0.0;
                    double offset = (y - processedDensity) * 12.0 / (multipliedScale * multiplier);
                    if (offset < 0.0) {
                        offset *= 4.0;
                    }

                    double min = this.minLimitNoiseSample[index] / 512.0;
                    double max = this.maxLimitNoiseSample[index] / 512.0;
                    double main = (this.mainNoiseSample[index] / 10.0 + 1.0) / 2.0;
                    if (main < 0.0) {
                        density = min;
                    } else if (main > 1.0) {
                        density = max;
                    } else {
                        density = min + (max - min) * main;
                    }

                    density -= offset;
                    if (y > ySamples - 4) {
                        double d = (y - (ySamples - 4)) / 3.0;
                        density = density * (1.0 - d) + -10.0 * d;
                    }

                    if (!(sampleBiome instanceof Beach)
                        && sampleBiome != BiomeList.shore
                        && sampleBiome != BiomeList.frozenShore
                        && sampleBiome != BiomeList.warmShore) {
                        double cave = this.caveNoise.generateNoise3D((xStart + x) * 0.5, (yStart + y) * 1.5, (zStart + z) * 0.5) - this.caveScale;
                        if (cave > 0.0) {
                            int caveCutoff = sampleBiome.depth <= 0.5F && sampleBiome.scale >= 1.0F ? 2 : this.caveHeight;
                            if (y < 1) {
                                cave -= cave * ((3 - y) / 2.0);
                            }

                            if (y > caveCutoff) {
                                cave -= cave * ((y - caveCutoff) / 2.0);
                            }
                        }

                        if (density > 0.0 && cave > 0.0) {
                            density *= -cave;
                        }
                    }

                    densityMap[index] = density;
                    index++;
                }
            }
        }

        return densityMap;
    }

    protected double getRiverDensity(int blockX, int blockZ, double riverRadius, PerlinNoise noise) {
        double riverLevel = 0.0;
        double sample = noise.generateNoise(blockX / 1600.0, blockZ / 1600.0);
        sample += noise.generateNoise(blockX / 80.0, blockZ / 80.0) * 0.04;
        sample += noise.generateNoise(blockX / 16.0, blockZ / 16.0) * 0.004;
        if (!(sample < riverLevel - riverRadius) && !(sample > riverLevel + riverRadius)) {
            sample -= riverLevel;
            sample /= riverRadius;
            sample = Math.abs(sample);
            sample = -sample;
            return sample + 1.0;
        } else {
            return 0.0;
        }
    }

    public void replaceBlocks(int chunkX, int chunkZ, short[] blocks, byte[] metadata, Biome[] biomes) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                Biome biomeGen = biomes[z << 4 | x];
                int xx = chunkX << 4 | x;
                int zz = chunkZ << 4 | z;
                double off = 0.03125;
                double sandValue = this.beachNoise.generateNoise3D(xx * off, zz * off, 0.0);
                double gravelValue = this.beachNoise.generateNoise3D(xx * off, 109.0134, zz * off);
                double heightValue = this.surfaceHeightNoise.generateNoise2D(xx * off * 2.0, zz * off * 2.0);
                biomeGen.surfaceBuilder(this.world, this.rand, blocks, metadata, xx, zz, heightValue, sandValue, gravelValue);
                biomeGen.caveBuilder(this.world, this.rand, blocks, metadata, xx, zz, heightValue);
            }
        }
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

    @Override
    public Chunk generateChunk(int chunkX, int chunkZ) {
        Sand.can_fall = false;
        this.rand.setSeed(chunkX * 341873128712L + chunkZ * 132897987541L);
        short[] blockArray = new short[256 * this.world.getWorldHeight()];
        byte[] metadataArray = new byte[256 * this.world.getWorldHeight()];
        this.generateTerrain(chunkX, chunkZ, blockArray);
        this.biomes = this.world.getBiomeProvider().loadBiomes(this.biomes, chunkX << 4, chunkZ << 4, 16, 16);
        int[] landSurfaceHeightMap = this.generateLandSurfaceHeightMap(blockArray);
        this.replaceBlocks(chunkX, chunkZ, blockArray, metadataArray, this.biomes);
        this.caveGenerator.generate(this, this.world, chunkX, chunkZ, blockArray, metadataArray);
        this.ravineGenerator.generate(this, this.world, chunkX, chunkZ, blockArray, metadataArray);
        if (this.world.getSettings().generateStructures()) {
            this.featureProvider.generateNearestFeatures(chunkX, chunkZ, blockArray, metadataArray, this.biomes);
            this.mineshaftGenerator.generate(this, this.world, chunkX, chunkZ, blockArray, metadataArray);
            this.strongholdGenerator.generate(this, this.world, chunkX, chunkZ, blockArray, metadataArray);
        }

        Chunk chunk = new Chunk(this.world, blockArray, metadataArray, chunkX, chunkZ);
        chunk.generateHeightMap();
        chunk.generateSkylightMap();
        byte[] biomeArray = chunk.getBiomes();

        for (int i = 0; i < biomeArray.length; i++) {
            biomeArray[i] = (byte)this.biomes[i].id;
        }

        chunk.setBiomes(biomeArray);
        chunk.landSurfaceHeightMap(landSurfaceHeightMap);
        Sand.can_fall = true;
        return chunk;
    }

    @Override
    public Chunk justGenerateForHeight(int chunkX, int chunkZ) {
        this.rand.setSeed(chunkX * 341873128712L + chunkZ * 132897987541L);
        short[] blockArray = new short[256 * this.world.getWorldHeight()];
        byte[] metadataArray = new byte[256 * this.world.getWorldHeight()];
        this.generateTerrain(chunkX, chunkZ, blockArray);
        int[] landSurfaceHeightMap = this.generateLandSurfaceHeightMap(blockArray);
        Chunk chunk = new Chunk(this.world, blockArray, metadataArray, chunkX, chunkZ);
        chunk.landSurfaceHeightMap(landSurfaceHeightMap);
        return chunk;
    }

    @Override
    public boolean chunkExists(int x, int z) {
        return true;
    }

    @Override
    public void populate(ChunkLoader chunkProvider, int chunkX, int chunkZ) {
        Sand.can_fall = false;
        int x0 = chunkX * 16;
        int z0 = chunkZ * 16;
        Biome biomeGen = this.world.getBiomeProvider().getBiomeAt((chunkX << 4) + 16, (chunkZ << 4) + 16);
        this.rand.setSeed(this.world.getSeed());
        long rn1 = this.rand.nextLong() / 2L * 2L + 1L;
        long rn2 = this.rand.nextLong() / 2L * 2L + 1L;
        this.rand.setSeed(chunkX * rn1 + chunkZ * rn2 ^ this.world.getSeed());
        if (this.isDecorating) {
            System.out.println("Chunk overflow at: " + chunkX + "x " + chunkZ + "z");
        }

        this.isDecorating = true;
        biomeGen.prePopulate(this.world, this.rand, x0, z0);
        this.populateFeatures(biomeGen, x0, z0, chunkX, chunkZ);
        this.populateOres(biomeGen, x0, z0);
        this.populateFluids(biomeGen, x0, z0);
        this.populatePlants(biomeGen, x0, z0);
        this.populateCaves(biomeGen, x0, z0);
        this.populateSnow(biomeGen, x0, z0);
        biomeGen.postPopulate(this.world, this.rand, x0, z0);
        this.isDecorating = false;
        Sand.can_fall = true;
    }

    private void populateSnow(Biome biomeGen, int x0, int z0) {
        for (int x = x0 + 8; x < x0 + 8 + 16; x++) {
            for (int z = z0 + 8; z < z0 + 8 + 16; z++) {
                int top = this.world.getTopSolidTile(x, z);
                if (top > 0 && top < this.world.getWorldHeight() && this.world.getBrightness(LightLayer.Block, x, top, z) < 10) {
                    if (this.world.getBlockMaterial(x, top - 1, z) != MaterialList.water || this.world.getBlockMetadata(x, top - 1, z) != 0) {
                        Material material = this.world.getBlockMaterial(x, top - 1, z);
                        if (this.world.isAir(x, top, z)
                            && this.world.getBlock(x, top - 1, z) != BlockList.snowLayer
                            && BlockList.snowLayer.placeableAt(this.world, x, top, z)
                            && material != MaterialList.air
                            && material != MaterialList.ice
                            && material.isSolid()
                            && this.world.canSnowAt(x, top, z, false)) {
                            int idToPlace = BlockList.snowLayer.id;
                            this.world.setBlock(x, top, z, idToPlace);
                            if (material == MaterialList.leaves) {
                                boolean stop = false;

                                for (int var11 = top - 2; !stop && var11 > 0; var11--) {
                                    int block = this.world.getBlockId(x, var11 - 1, z);
                                    if (this.world.getBrightness(LightLayer.Block, x, var11, z) < 11 && block != 0) {
                                        if (BlockList.blocks[block].material == MaterialList.water) {
                                            if (this.world.isWater(x, var11 - 1, z)) {
                                                this.world.setBlockWithNotify(x, var11 - 1, z, BlockList.ice.id);
                                            }

                                            stop = true;
                                        } else if (BlockList.blocks[block].material != MaterialList.leaves && block != BlockList.wood.id) {
                                            if (this.world.isAir(x, var11, z)
                                                && BlockList.snowLayer.placeableAt(this.world, x, var11, z)
                                                && BlockList.blocks[block].material != MaterialList.ice
                                                && BlockList.blocks[block].material.isSolid()
                                                && this.world.canSnowAt(x, var11, z, false)) {
                                                this.world.setBlock(x, var11, z, BlockList.snowLayer.id);
                                            }

                                            stop = true;
                                        }
                                    }
                                }
                            }
                        }
                    } else if (this.world.isWater(x, top - 1, z)) {
                        this.world.setBlockWithNotify(x, top - 1, z, BlockList.ice.id);
                    }
                }
            }
        }
    }

    private void populateCaves(Biome biomeGen, int x0, int z0) {
        if (this.rand.nextInt(64) == 0) {
            int x = x0 + 8;
            int z = z0 + 8;
            int y = this.rand.nextInt(this.world.getSeaLevel());
            new Geode(this.rand.nextInt(3)).place(this.world, this.rand, x, y, z);
        }

        if (!biomeGen.biomeCaves && !(biomeGen instanceof Ocean)) {
            for (int i = 0; i < 3; i++) {
                int x = x0 + this.rand.nextInt(16) + 8;
                int z = z0 + this.rand.nextInt(16) + 8;
                int y = this.rand.nextInt(24) + biomeGen.yOffset;
                SpikeFeature.UNDERGROUND.place(this.world, this.rand, x, y, z);
            }
        }

        if (biomeGen != BiomeList.shore && biomeGen != BiomeList.frozenShore) {
            if (this.rand.nextInt(biomeGen.pebbleAttempts) == 0) {
                int x = x0 + this.rand.nextInt(16) + 8;
                int z = z0 + this.rand.nextInt(16) + 8;
                int y;
                if (biomeGen instanceof FloatingIsland) {
                    y = this.rand.nextInt(this.world.getWorldHeight());
                } else {
                    y = this.world.getHeightmap(x, z);
                }

                FlowerPatch.pebbles.place(this.world, this.rand, x, y, z);
            }

            for (int i = 0; i < 20; i++) {
                int x = x0 + this.rand.nextInt(16) + 8;
                int z = z0 + this.rand.nextInt(16) + 8;
                int y = this.rand.nextInt(this.world.getSeaLevel());
                if (y < biomeGen.yOffset) {
                    FlowerPatch.pebblesDEEP.place(this.world, this.rand, x, y, z);
                } else {
                    FlowerPatch.pebbles.place(this.world, this.rand, x, y, z);
                }
            }
        }
    }

    private void populateFeatures(Biome biomeGen, int x0, int z0, int chunkX, int chunkZ) {
        if (this.world.settings.generateStructures()) {
            this.featureProvider.populateFeatures(this.world, this.rand, chunkX, chunkZ);
            this.mineshaftGenerator.generateStructuresInChunk(this.world, this.rand, chunkX, chunkZ);
            this.strongholdGenerator.generateStructuresInChunk(this.world, this.rand, chunkX, chunkZ);

            for (int i = 0; i < 8; i++) {
                int x = x0 + this.rand.nextInt(16) + 8;
                int y = this.rand.nextInt(this.world.getWorldHeight());
                int z = z0 + this.rand.nextInt(16) + 8;
                Dungeon.INSTANCE.place(this.world, this.rand, x, y, z);
            }

            if (this.rand.nextInt(50) == 0) {
                int x = x0 + 8;
                int z = z0 + 8;
                int y = biomeGen.yOffset + this.rand.nextInt(3);
                Ruins.INSTANCE.place(this.world, this.rand, x, y, z);
            }
        }
    }

    private void populateFluids(Biome biomeGen, int x0, int z0) {
        if (this.rand.nextInt(biomeGen.waterAttempts) == 0 && biomeGen.generateWater) {
            int x = x0 + this.rand.nextInt(16) + 8;
            int y = this.rand.nextInt(this.world.getWorldHeight());
            int z = z0 + this.rand.nextInt(16) + 8;
            Pond.WATER.place(this.world, this.rand, x, y, z);
        }

        if (this.rand.nextInt(biomeGen.lavaAttempts) == 0 && biomeGen.generateLava) {
            int x = x0 + this.rand.nextInt(16) + 8;
            int y = this.rand.nextInt(this.rand.nextInt(140) + 8);
            if (biomeGen instanceof Wasteland || biomeGen instanceof VolcanicWasteland) {
                y = this.rand.nextInt(this.world.getWorldHeight());
            }

            int z = z0 + this.rand.nextInt(16) + 8;
            if (y <= this.world.getSeaLevel() + 16) {
                Pond.LAVA.place(this.world, this.rand, x, y, z);
            }
        }

        if (this.rand.nextInt(8) == 0) {
            int x = x0 + 9;
            int z = z0 + 9;
            int y = this.rand.nextInt(biomeGen.yOffset);
            new Puddles(BlockList.lavaMoving.id, 0).place(this.world, this.rand, x, y, z);
        }

        if (biomeGen.generateWater) {
            for (int i = 0; i < biomeGen.waterFallAttempts; i++) {
                int x = x0 + this.rand.nextInt(16) + 8;
                int y = this.rand.nextInt(biomeGen.waterFallMaxHeight) + biomeGen.waterFallMinHeight;
                int z = z0 + this.rand.nextInt(16) + 8;
                Waterfall.WATERFALL.place(this.world, this.rand, x, y, z);
            }
        }

        if (biomeGen.generateLava) {
            for (int i = 0; i < biomeGen.lavaFallAttempts; i++) {
                int x = x0 + this.rand.nextInt(16) + 8;
                int y = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(this.world.getWorldHeight() - 16) + 8) + 8);
                int z = z0 + this.rand.nextInt(16) + 8;
                Waterfall.LAVAFALL.place(this.world, this.rand, x, y, z);
            }
        }
    }

    private void populatePlants(Biome biomeGen, int x0, int z0) {
        double limiter = 0.5;
        int treeAttempts = (int)((this.treeDensityNoise.generateNoise2D(x0 * limiter, z0 * limiter) / 8.0 + this.rand.nextDouble() * 4.0 + 4.0) / 3.0);
        if (treeAttempts < 0) {
            treeAttempts = 0;
        }

        if (this.rand.nextInt(10) == 0) {
            treeAttempts++;
        }

        treeAttempts += biomeGen.treeAttempts;

        for (int i = 0; i < treeAttempts; i++) {
            int x = x0 + this.rand.nextInt(16) + 8;
            int z = z0 + this.rand.nextInt(16) + 8;
            int y = this.world.getHeightmap(x, z);
            Tree tre = biomeGen.getRandomTreeGenerator(this.rand, y, this.world.getWorldHeight());
            if (this.world.getSettings().getType() == WorldType.CAVES) {
                tre = new BigMushroom(this.rand.nextInt(3) + 1);
            }

            if (tre != null) {
                tre.place(this.world, this.rand, x, y, z);
            }
        }

        if (biomeGen.flowersAttempts >= 0) {
            for (int var24 = 0; var24 < biomeGen.flowersAttempts; var24++) {
                int x = x0 + this.rand.nextInt(16) + 8;
                int y = this.rand.nextInt(this.world.getWorldHeight());
                int z = z0 + this.rand.nextInt(16) + 8;
                FlowerPatch instance = FlowerPatch.yellowFlower;
                switch (this.rand.nextInt(6)) {
                    case 1:
                        instance = FlowerPatch.redFlower;
                        break;
                    case 2:
                        instance = FlowerPatch.blueFlower;
                        break;
                    case 3:
                        instance = FlowerPatch.purpleFlower;
                        break;
                    case 4:
                        instance = FlowerPatch.tulip;
                        break;
                    case 5:
                        instance = FlowerPatch.daisy;
                }

                instance.place(this.world, this.rand, x, y, z);
            }
        }

        for (int var25 = 0; var25 < biomeGen.mushroomChance; var25++) {
            int x = x0 + this.rand.nextInt(16) + 8;
            int y = this.rand.nextInt(this.world.getWorldHeight());
            int z = z0 + this.rand.nextInt(16) + 8;
            FlowerPatch instance = FlowerPatch.mushrooms;
            if (this.rand.nextBoolean()) {
                instance = FlowerPatch.redshrooms;
            }

            instance.place(this.world, this.rand, x, y, z);
        }

        if (biomeGen.tallGrassAttempts > 0) {
            for (int var26 = 0; var26 < biomeGen.tallGrassAttempts; var26++) {
                int x = x0 + this.rand.nextInt(16) + 8;
                int y = this.rand.nextInt(this.world.getWorldHeight());
                int z = z0 + this.rand.nextInt(16) + 8;
                new GrassCluster(BlockList.tallGrass.id, biomeGen.grassMeta).place(this.world, this.rand, x, y, z);
            }
        }

        for (int var27 = 0; var27 < biomeGen.reedAttempts; var27++) {
            int x = x0 + this.rand.nextInt(16) + 8;
            int y = this.rand.nextInt(this.world.getWorldHeight());
            int z = z0 + this.rand.nextInt(16) + 8;
            TallPlants.REEDS.place(this.world, this.rand, x, y, z);
        }
    }

    private void populateOres(Biome biomeGen, int x0, int z0) {
        for (int i = 0; i < biomeGen.clayAttempts; i++) {
            int x = x0 + this.rand.nextInt(16);
            int y = this.rand.nextInt(this.world.getWorldHeight());
            int z = z0 + this.rand.nextInt(16);
            ClayLump.clay.place(this.world, this.rand, x, y, z);
        }

        for (int var47 = 0; var47 < biomeGen.dirtLumpAttempts; var47++) {
            int x = x0 + this.rand.nextInt(16);
            int y = this.rand.nextInt(this.world.getWorldHeight());
            int z = z0 + this.rand.nextInt(16);
            if (y <= biomeGen.yOffset) {
                OreLump.volcanicSoil.place(this.world, this.rand, x, y, z);
            } else {
                new OreLump(BlockList.dirt.id, 32, BlockList.stone.id, biomeGen.fillerMeta).place(this.world, this.rand, x, y, z);
            }
        }

        for (int var48 = 0; var48 < biomeGen.gravelLumpAttempts; var48++) {
            int x = x0 + this.rand.nextInt(16);
            int y = this.rand.nextInt(this.world.getWorldHeight());
            int z = z0 + this.rand.nextInt(16);
            if (this.rand.nextInt(biomeGen.gravelLumpAttempts) == 0) {
                OreLump.susGravel.place(this.world, this.rand, x, y, z);
            } else {
                OreLump.gravel.place(this.world, this.rand, x, y, z);
            }
        }

        for (int var49 = 0; var49 < biomeGen.marbleLumpAttempts; var49++) {
            int x = x0 + this.rand.nextInt(8) + 4;
            int y = this.rand.nextInt(this.world.getWorldHeight());
            int z = z0 + this.rand.nextInt(8) + 4;
            new OreLump(BlockList.stone.id, 48, BlockList.stone.id, biomeGen.defaultStoneType).place(this.world, this.rand, x, y, z);
        }

        int x = x0 + this.rand.nextInt(16);
        int y = this.rand.nextInt(64);
        int z = z0 + this.rand.nextInt(16);
        OreLump.magma.place(this.world, this.rand, x, y, z);
        x = x0 + this.rand.nextInt(16);
        y = this.rand.nextInt(this.world.getSeaLevel());
        z = z0 + this.rand.nextInt(16);
        if (y <= biomeGen.yOffset) {
            OreLump.silverfishDeep.place(this.world, this.rand, x, y, z);
        } else {
            OreLump.silverfish.place(this.world, this.rand, x, y, z);
        }

        for (int var50 = 0; var50 < biomeGen.coalLumpAttempts; var50++) {
            x = x0 + this.rand.nextInt(16);
            y = this.rand.nextInt(this.world.getWorldHeight());
            z = z0 + this.rand.nextInt(16);
            if (y <= biomeGen.yOffset) {
                OreLump.coalDeep.place(this.world, this.rand, x, y, z);
            } else {
                OreLump.coal.place(this.world, this.rand, x, y, z);
            }
        }

        for (int var51 = 0; var51 < biomeGen.copperLumpAttempts; var51++) {
            x = x0 + this.rand.nextInt(16);
            y = this.rand.nextInt(biomeGen.copperLumpMaxHeight + biomeGen.yOffset);
            z = z0 + this.rand.nextInt(16);
            if (y <= biomeGen.yOffset) {
                OreLump.copperDeep.place(this.world, this.rand, x, y, z);
            } else {
                OreLump.copper.place(this.world, this.rand, x, y, z);
            }
        }

        for (int var52 = 0; var52 < biomeGen.ironLumpAttempts; var52++) {
            x = x0 + this.rand.nextInt(16);
            y = this.rand.nextInt(biomeGen.ironLumpMaxHeight + biomeGen.yOffset);
            z = z0 + this.rand.nextInt(16);
            if (y <= biomeGen.yOffset) {
                OreLump.ironDeep.place(this.world, this.rand, x, y, z);
            } else {
                OreLump.iron.place(this.world, this.rand, x, y, z);
            }
        }

        for (int var53 = 0; var53 < biomeGen.goldLumpAttempts; var53++) {
            x = x0 + this.rand.nextInt(16);
            y = this.rand.nextInt(biomeGen.goldLumpMaxHeight + biomeGen.yOffset);
            z = z0 + this.rand.nextInt(16);
            if (y <= biomeGen.yOffset) {
                OreLump.goldDeep.place(this.world, this.rand, x, y, z);
            } else {
                OreLump.gold.place(this.world, this.rand, x, y, z);
            }
        }

        for (int var54 = 0; var54 < biomeGen.diamondLumpAttempts; var54++) {
            x = x0 + this.rand.nextInt(16);
            y = this.rand.nextInt(biomeGen.diamondLumpMaxHeight + biomeGen.yOffset);
            z = z0 + this.rand.nextInt(16);
            if (y <= biomeGen.yOffset) {
                OreLump.diamondDeep.place(this.world, this.rand, x, y, z);
            } else {
                OreLump.diamond.place(this.world, this.rand, x, y, z);
            }
        }

        for (int var55 = 0; var55 < biomeGen.rubyLumpAttempts; var55++) {
            x = x0 + this.rand.nextInt(16);
            y = this.rand.nextInt(biomeGen.rubyLumpMaxHeight + biomeGen.yOffset);
            z = z0 + this.rand.nextInt(16);
            if (y <= biomeGen.yOffset) {
                OreLump.rubyDeep.place(this.world, this.rand, x, y, z);
            } else {
                OreLump.ruby.place(this.world, this.rand, x, y, z);
            }
        }

        for (int var56 = 0; var56 < biomeGen.sapphireLumpAttempts; var56++) {
            x = x0 + this.rand.nextInt(16);
            y = this.rand.nextInt(biomeGen.sapphireLumpMaxHeight + biomeGen.yOffset);
            z = z0 + this.rand.nextInt(16);
            if (y <= biomeGen.yOffset) {
                OreLump.sapphireDeep.place(this.world, this.rand, x, y, z);
            } else {
                OreLump.sapphire.place(this.world, this.rand, x, y, z);
            }
        }

        for (int var57 = 0; var57 < biomeGen.emeraldLumpAttempts; var57++) {
            x = x0 + this.rand.nextInt(16);
            y = this.rand.nextInt(biomeGen.emeraldLumpMaxHeight + biomeGen.yOffset);
            z = z0 + this.rand.nextInt(16);
            if (y <= biomeGen.yOffset) {
                OreLump.emeraldDeep.place(this.world, this.rand, x, y, z);
            } else {
                OreLump.emerald.place(this.world, this.rand, x, y, z);
            }
        }
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
        return "OverworldGenerator";
    }

    @Override
    public BlockPosition findClosestStructure(World world, Class<? extends Structure> target, int x, int y, int z) {
        return this.featureProvider != null ? this.featureProvider.getNearestFeatures(target, x >> 4, z >> 4) : null;
    }
}
