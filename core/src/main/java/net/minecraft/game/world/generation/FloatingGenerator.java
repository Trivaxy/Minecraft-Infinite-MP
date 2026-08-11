package net.minecraft.game.world.generation;

import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.building.Sand;
import net.minecraft.game.world.World;
import net.minecraft.game.world.chunk.Chunk;
import net.minecraft.game.world.chunk.ChunkLoader;

public class FloatingGenerator extends OverworldGenerator implements ChunkLoader {
    public FloatingGenerator(World w, long s) {
        super(w, s);
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
    public void generateTerrain(int chunkX, int chunkZ, short[] blocks) {
        byte offset = 2;
        int i = offset + 1;
        byte maxHeight = (byte)(this.world.getWorldHeight() / 4 + 2);
        this.mainNoiseSample = this.initializeNoise(this.mainNoiseSample, chunkX * offset, 0, chunkZ * offset, i, maxHeight, i);

        for (int k = 0; k < offset; k++) {
            for (int l = 0; l < offset; l++) {
                for (int yValue = 0; yValue < this.world.getWorldHeight() / 4; yValue++) {
                    double multiplier = 0.25;
                    double upper1 = this.mainNoiseSample[((k + 0) * i + l + 0) * maxHeight + yValue + 0];
                    double upper2 = this.mainNoiseSample[((k + 0) * i + l + 1) * maxHeight + yValue + 0];
                    double upper3 = this.mainNoiseSample[((k + 1) * i + l + 0) * maxHeight + yValue + 0];
                    double upper4 = this.mainNoiseSample[((k + 1) * i + l + 1) * maxHeight + yValue + 0];
                    double lower1 = (this.mainNoiseSample[((k + 0) * i + l + 0) * maxHeight + yValue + 1] - upper1) * multiplier;
                    double lower2 = (this.mainNoiseSample[((k + 0) * i + l + 1) * maxHeight + yValue + 1] - upper2) * multiplier;
                    double lower3 = (this.mainNoiseSample[((k + 1) * i + l + 0) * maxHeight + yValue + 1] - upper3) * multiplier;
                    double lower4 = (this.mainNoiseSample[((k + 1) * i + l + 1) * maxHeight + yValue + 1] - upper4) * multiplier;

                    for (int blobY = 0; blobY < 4; blobY++) {
                        double mul = 0.125;
                        double high1 = upper1;
                        double high2 = upper2;
                        double diff1 = (upper3 - upper1) * mul;
                        double diff2 = (upper4 - upper2) * mul;

                        for (int blobX = 0; blobX < 8; blobX++) {
                            int index = (yValue << 2) + blobY << 8 | 0 + (l << 3) << 4 | blobX + (k << 3);
                            int chunkSize = 16;
                            double next = high1;
                            double nextIndex = (high2 - high1) * mul;

                            for (int blobZ = 0; blobZ < 8; blobZ++) {
                                int placeID = 0;
                                if (next > 0.0) {
                                    placeID = BlockList.stone.id;
                                }

                                blocks[index] = (byte)placeID;
                                index += chunkSize;
                                next += nextIndex;
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

    @Override
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

    @Override
    public String loaderName() {
        return "FloatingGenerator";
    }
}
