package net.minecraft.game.world.biomes.ocean;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.game.world.generation.features.general.Iceberg;
import net.minecraft.game.world.generation.noise.SegmentedNoise;
import net.minecraft.util.random.SkippableRandom;

public class ColdOcean extends Ocean {
    private static final short PACKED_ICE = (short)BlockList.packedIce.id;
    private static final short SNOW_BLOCK = (short)BlockList.snowBlock.id;
    private static final short AIR = 0;
    private static final short GRAVEL = (short)BlockList.gravel.id;
    private static final short ICE = (short)BlockList.ice.id;
    private static final short WATER = (byte)BlockList.waterStill.id;
    private static final short DEFAULT_BLOCK = (short)BlockList.stone.id;
    private static final IntList octaves1 = new IntArrayList();
    private static final IntList octaves2 = new IntArrayList();
    private SegmentedNoise noiseGen1;
    private SegmentedNoise noiseGen2;
    private long randomSeed;

    public ColdOcean(int biomeID) {
        super(biomeID);
        this.generateWater = false;
    }

    @Override
    public void prePopulate(World world, Random rand, int x0, int z0) {
        if (rand.nextInt(8) == 0) {
            int x = x0 + 16;
            int z = z0 + 16;
            Iceberg.INSTANCE.place(world, rand, x, 0, z);
        }
    }

    @Override
    public void surfaceBuilder(
        World world, Random rand, short[] blocks, byte[] metadata, int xAbs, int zAbs, double stoneNoise, double sandValue, double gravelValue
    ) {
        super.surfaceBuilder(world, rand, blocks, metadata, xAbs, zAbs, stoneNoise, sandValue, gravelValue);
        if (this.id == BiomeList.frozenOcean.id) {
            short defaultBlock = DEFAULT_BLOCK;
            if (this.randomSeed != world.getSeed() || this.noiseGen1 == null || this.noiseGen2 == null) {
                this.randomSeed = world.getSeed();
                SkippableRandom randIce = new SkippableRandom(this.randomSeed);
                this.noiseGen1 = new SegmentedNoise(randIce, octaves1);
                this.noiseGen2 = new SegmentedNoise(randIce, octaves2);
            }

            int seaLevel = world.getSeaLevel();
            int x = xAbs & 15;
            int z = zAbs & 15;
            double packedIceTop = 0.0;
            double packedIceBottom = 0.0;
            double density = Math.min(Math.abs(stoneNoise), this.noiseGen1.noiseAt(xAbs * 0.1, zAbs * 0.1, false) * 15.0);
            if (density > 1.8) {
                double d4 = Math.abs(this.noiseGen2.noiseAt(xAbs * 0.09765625, zAbs * 0.09765625, false));
                packedIceTop = density * density * 1.2;
                double d5 = Math.ceil(d4 * 40.0) + 14.0;
                if (packedIceTop > d5) {
                    packedIceTop = d5;
                }

                if (packedIceTop > 2.0) {
                    packedIceBottom = seaLevel - packedIceTop - 7.0;
                    packedIceTop += seaLevel;
                } else {
                    packedIceTop = 0.0;
                }
            }

            short biomeFillBlock = this.fillerBlock;
            short biomeTopBlock = this.topBlock;
            short fillBlock = biomeFillBlock;
            short topBlock = biomeTopBlock;
            int height = (int)(stoneNoise / 3.0 + 3.0 + rand.nextDouble() * 0.25);
            int stoneHeight = -1;
            int snowCounter = 0;
            int snowMax = 2 + rand.nextInt(4);
            int minSnowHeight = seaLevel + 18 + rand.nextInt(10);
            int beginning = 256;
            if (world.getMaxSections() < 16) {
                beginning = 128;
            }

            for (int y = beginning; y >= 0; y--) {
                int index = y << 8 | z << 4 | x;
                if (y <= 0 + rand.nextInt(5)) {
                    blocks[index] = (byte)BlockList.bedrock.id;
                } else {
                    short blockID = blocks[index];
                    if (blockID == 0 && y < (int)packedIceTop && rand.nextDouble() > 0.01) {
                        blockID = blocks[index] = PACKED_ICE;
                    } else if ((blockID == WATER || blockID == ICE)
                        && y > (int)packedIceBottom
                        && y < seaLevel
                        && packedIceBottom != 0.0
                        && rand.nextDouble() > 0.15) {
                        blockID = blocks[index] = PACKED_ICE;
                    }

                    if (blockID == 0) {
                        stoneHeight = -1;
                    } else if (blockID != defaultBlock) {
                        if (blockID == PACKED_ICE && snowCounter <= snowMax && y > minSnowHeight) {
                            blocks[index] = SNOW_BLOCK;
                            snowCounter++;
                        }
                    } else if (stoneHeight == -1) {
                        if (height <= 0) {
                            topBlock = 0;
                            fillBlock = defaultBlock;
                        } else if (y >= seaLevel - 4 && y <= seaLevel + 1) {
                            topBlock = biomeTopBlock;
                            fillBlock = biomeFillBlock;
                        }

                        if (y < seaLevel && topBlock == 0) {
                            topBlock = ICE;
                        }

                        stoneHeight = height;
                        if (y >= seaLevel - 1) {
                            blocks[index] = topBlock;
                        } else if (y < seaLevel - 7 - height) {
                            topBlock = WATER;
                            fillBlock = defaultBlock;
                            blocks[index] = GRAVEL;
                        } else {
                            blocks[index] = fillBlock;
                        }
                    } else if (stoneHeight > 0) {
                        stoneHeight--;
                        blocks[index] = fillBlock;
                    }
                }
            }
        }
    }

    @Override
    public int getSkyColor() {
        return 9803519;
    }

    static {
        for (int i = -3; i <= 0; i++) {
            octaves1.add(i);
        }

        octaves2.add(0);
    }
}
