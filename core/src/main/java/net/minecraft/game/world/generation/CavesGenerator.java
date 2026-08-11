package net.minecraft.game.world.generation;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.building.Sand;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.chunk.Chunk;
import net.minecraft.game.world.chunk.ChunkLoader;
import net.minecraft.game.world.generation.noise.OctaveNoise;
import net.minecraft.util.math.MathHelper;

public class CavesGenerator extends OverworldGenerator implements ChunkLoader {
    public CavesGenerator(World world, long seed) {
        super(world, seed);
    }

    @Override
    public void generateTerrain(int chunkX, int chunkZ, short[] blocks) {
        byte samples = 4;
        byte height = (byte)(this.world.getWorldHeight() / 8);
        int offset = samples + 1;
        byte heightPlusOne = (byte)(this.world.getWorldHeight() / 8 + 1);
        int lavaOffset = 1;
        if (this.world.getWorldHeight() < 256) {
            lavaOffset = 2;
        }

        this.noiseArray = this.initializeNoiseField(this.noiseArray, chunkX * samples, 0, chunkZ * samples, offset, heightPlusOne, offset);

        for (int x = 0; x < samples; x++) {
            for (int z = 0; z < samples; z++) {
                for (int y = 0; y < height; y++) {
                    double mul = 0.125;
                    double upper1 = this.noiseArray[((x + 0) * offset + z + 0) * heightPlusOne + y + 0];
                    double upper2 = this.noiseArray[((x + 0) * offset + z + 1) * heightPlusOne + y + 0];
                    double upper3 = this.noiseArray[((x + 1) * offset + z + 0) * heightPlusOne + y + 0];
                    double upper4 = this.noiseArray[((x + 1) * offset + z + 1) * heightPlusOne + y + 0];
                    double lower1 = (this.noiseArray[((x + 0) * offset + z + 0) * heightPlusOne + y + 1] - upper1) * mul;
                    double lower2 = (this.noiseArray[((x + 0) * offset + z + 1) * heightPlusOne + y + 1] - upper2) * mul;
                    double lower3 = (this.noiseArray[((x + 1) * offset + z + 0) * heightPlusOne + y + 1] - upper3) * mul;
                    double lower4 = (this.noiseArray[((x + 1) * offset + z + 1) * heightPlusOne + y + 1] - upper4) * mul;

                    for (int ySamples = 0; ySamples < 8; ySamples++) {
                        double innerMul = 0.25;
                        double high1 = upper1;
                        double high2 = upper2;
                        double diff1 = (upper3 - upper1) * innerMul;
                        double diff2 = (upper4 - upper2) * innerMul;

                        for (int xSamples = 0; xSamples < 4; xSamples++) {
                            int index = (y << 3) + ySamples << 8 | 0 + (z << 2) << 4 | xSamples + (x << 2);
                            int chunkSize = 16;
                            double maxHeight = high1;
                            double scale = (high2 - high1) * innerMul;

                            for (int zSamples = 0; zSamples < 4; zSamples++) {
                                int id = 0;
                                if ((y << 3) + ySamples < height * lavaOffset) {
                                    id = BlockList.lavaStill.id;
                                }

                                if (maxHeight > 0.0) {
                                    id = BlockList.stone.id;
                                }

                                blocks[index] = (byte)id;
                                index += chunkSize;
                                maxHeight += scale;
                            }

                            high1 += diff1;
                            high2 += diff2;
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

    private double[] initializeNoiseField(double[] density, int startX, int startY, int startZ, int xSamples, int ySamples, int zSamples) {
        if (density == null) {
            density = new double[xSamples * ySamples * zSamples];
        }

        double horizontalScale = 684.412;
        double verticalScale = 684.412;
        double heightOffset = 60.0;
        if (this.world.getWorldHeight() >= 256) {
            heightOffset = 10.0;
        }

        this.mainNoiseSample = this.mainNoise
            .generateNoiseOctaves(
                this.mainNoiseSample,
                startX,
                startY,
                startZ,
                xSamples,
                ySamples,
                zSamples,
                horizontalScale / 80.0,
                verticalScale / heightOffset,
                horizontalScale / 80.0
            );
        this.minLimitNoiseSample = this.minLimitNoise
            .generateNoiseOctaves(
                this.minLimitNoiseSample, startX, startY, startZ, xSamples, ySamples, zSamples, horizontalScale, verticalScale, horizontalScale
            );
        this.maxLimitNoiseSample = this.maxLimitNoise
            .generateNoiseOctaves(
                this.maxLimitNoiseSample, startX, startY, startZ, xSamples, ySamples, zSamples, horizontalScale, verticalScale, horizontalScale
            );
        int index = 0;
        double[] scale = new double[ySamples];

        for (int y = 0; y < ySamples; y++) {
            scale[y] = Math.cos(y * Math.PI * 6.0 / ySamples) * 2.0;
            double height = y;
            if (y > ySamples / 2) {
                height = ySamples - 1 - y;
            }

            if (height < 4.0) {
                height = 4.0 - height;
                scale[y] -= height * height * height * 10.0;
            }
        }

        for (int x = 0; x < xSamples; x++) {
            for (int z = 0; z < zSamples; z++) {
                double off = 0.0;

                for (int y = 0; y < ySamples; y++) {
                    double mul = 0.0;
                    double curr = scale[y];
                    double min = this.minLimitNoiseSample[index] / 512.0;
                    double max = this.maxLimitNoiseSample[index] / 512.0;
                    double terrain = (this.mainNoiseSample[index] / 10.0 + 1.0) / 2.0;
                    if (terrain < 0.0) {
                        mul = min;
                    } else if (terrain > 1.0) {
                        mul = max;
                    } else {
                        mul = min + (max - min) * terrain;
                    }

                    mul -= curr;
                    if (y > ySamples - 4) {
                        double d = (y - (ySamples - 4)) / 3.0F;
                        mul = mul * (1.0 - d) + -10.0 * d;
                    }

                    if (y < off) {
                        double multiplier = (off - y) / 4.0;
                        multiplier = MathHelper.clamp(multiplier, 0.0, 1.0);
                        mul = mul * (1.0 - multiplier) + -10.0 * multiplier;
                    }

                    density[index] = mul;
                    index++;
                }
            }
        }

        return density;
    }

    @Override
    public Chunk generateChunk(int x, int z) {
        Sand.can_fall = false;
        this.rand.setSeed(x * 341873128712L + z * 132897987541L);
        short[] blocks = new short[256 * this.world.getWorldHeight()];
        byte[] metadataArray = new byte[256 * this.world.getWorldHeight()];
        this.generateTerrain(x, z, blocks);
        this.biomes = this.world.getBiomeProvider().loadBiomes(this.biomes, x << 4, z << 4, 16, 16);
        this.replaceBlocks(x, z, blocks, metadataArray, this.biomes);
        if (this.world.getSettings().generateStructures()) {
            this.featureProvider.generateNearestFeatures(x, z, blocks, metadataArray, this.biomes);
        }

        Chunk chunk = new Chunk(this.world, blocks, metadataArray, x, z);
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
    public void replaceBlocks(int chunkX, int chunkZ, short[] blocks, byte[] metadata, Biome[] biomes) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                Biome biomeGen = biomes[z << 4 | x];
                int xx = chunkX << 4 | x;
                int zz = chunkZ << 4 | z;
                double offset = 0.03125;
                double heightValue = this.surfaceHeightNoise.generateNoise2D(xx * offset * 2.0, zz * offset * 2.0);
                this.buildCaveSurface(biomeGen, this.world, this.rand, blocks, metadata, xx, zz, heightValue);
            }
        }
    }

    public void buildCaveSurface(Biome biomeGen, World world, Random random, short[] blocks, byte[] metadata, int x, int z, double heightValue) {
        int top = biomeGen.topBlock;
        byte topMetadata = (byte)(biomeGen.topMeta & 0xFF);
        int filler = biomeGen.fillerBlock;
        byte fillerMetadata = (byte)(biomeGen.fillerMeta & 0xFF);
        int stoneHeight = -1;
        if (biomeGen.caveNoise == null) {
            biomeGen.caveNoise = new OctaveNoise(random, 4);
        }

        if (biomeGen.stoneNoise == null) {
            biomeGen.stoneNoise = new OctaveNoise(random, 6);
        }

        boolean generateStones = biomeGen.caveNoise.generateNoise2D(x, z) > 0.0;
        boolean generateOres = biomeGen.stoneNoise.generateNoise2D(x, z) > 8.0;
        int height = (int)(heightValue / 3.0 + 3.0 + random.nextDouble() * 0.25);
        int chunkX = x & 15;
        int chunkZ = z & 15;
        int ymax = world.getSeaLevel();
        int ystart = biomeGen.yOffset;
        ymax = world.getWorldHeight();
        int var33 = 0;

        for (int y = world.getWorldHeight() - 1; y >= 0; y--) {
            int index = y << 8 | chunkZ << 4 | chunkX;
            short block = blocks[index];
            short meta = metadata[index];
            if (y <= random.nextInt(6) - 1 || y == 0) {
                blocks[index] = (byte)BlockList.bedrock.id;
            } else if (block <= 0 || block != BlockList.stone.id || meta != 0) {
                stoneHeight = -1;
            } else if (stoneHeight == -1) {
                stoneHeight = height;
                if (biomeGen.biomeCaves) {
                    if (generateStones) {
                        top = biomeGen.caveBlock1;
                        topMetadata = biomeGen.caveMeta1;
                        filler = biomeGen.caveBlock1;
                        fillerMetadata = biomeGen.caveMeta1;
                    } else {
                        top = biomeGen.caveBlock2;
                        topMetadata = biomeGen.caveMeta2;
                        filler = biomeGen.caveBlock2;
                        fillerMetadata = biomeGen.caveMeta2;
                    }

                    if (generateOres) {
                        top = BlockList.stone.id;
                        topMetadata = 0;
                        filler = BlockList.stone.id;
                        fillerMetadata = 0;
                    }

                    blocks[index] = (short)top;
                    metadata[index] = topMetadata;
                } else {
                    int var27 = 0;
                    filler = BlockList.stone.id;
                    fillerMetadata = 0;
                    blocks[index] = (short)filler;
                    metadata[index] = fillerMetadata;
                }
            } else if (stoneHeight > 0) {
                stoneHeight--;
                blocks[index] = (short)filler;
                metadata[index] = fillerMetadata;
            }
        }

        for (int y = var33; y < ymax; y++) {
            int index = y << 8 | chunkZ << 4 | chunkX;
            short block = blocks[index];
            short meta = metadata[index];
            if (y >= world.getWorldHeight() - random.nextInt(6) || y == world.getWorldHeight() - 1) {
                blocks[index] = (byte)BlockList.bedrock.id;
            } else if (block <= 0 || block != BlockList.stone.id || meta != 0) {
                stoneHeight = -1;
            } else if (stoneHeight == -1) {
                stoneHeight = height;
                if (biomeGen.biomeCaves) {
                    if (generateStones) {
                        top = biomeGen.caveBlock1;
                        topMetadata = biomeGen.caveMeta1;
                        filler = biomeGen.caveBlock1;
                        fillerMetadata = biomeGen.caveMeta1;
                    } else {
                        top = biomeGen.caveBlock2;
                        topMetadata = biomeGen.caveMeta2;
                        filler = biomeGen.caveBlock2;
                        fillerMetadata = biomeGen.caveMeta2;
                    }

                    if (generateOres) {
                        top = BlockList.stone.id;
                        topMetadata = 0;
                        filler = BlockList.stone.id;
                        fillerMetadata = 0;
                    }

                    blocks[index] = (short)top;
                    metadata[index] = topMetadata;
                } else {
                    int var29 = 0;
                    filler = BlockList.stone.id;
                    fillerMetadata = 0;
                    blocks[index] = (short)filler;
                    metadata[index] = fillerMetadata;
                }
            } else if (stoneHeight > 0) {
                stoneHeight--;
                blocks[index] = (short)filler;
                metadata[index] = fillerMetadata;
            }
        }
    }

    @Override
    public String loaderName() {
        return "CavesGenerator";
    }
}
