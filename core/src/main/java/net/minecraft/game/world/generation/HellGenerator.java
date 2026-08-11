package net.minecraft.game.world.generation;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.building.Sand;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.game.world.biomes.hell.HellDesert;
import net.minecraft.game.world.chunk.BlockPosition;
import net.minecraft.game.world.chunk.Chunk;
import net.minecraft.game.world.chunk.ChunkLoader;
import net.minecraft.game.world.generation.features.carvers.CaveCarver;
import net.minecraft.game.world.generation.features.carvers.RavineCarver;
import net.minecraft.game.world.generation.features.general.Pond;
import net.minecraft.game.world.generation.features.general.Waterfall;
import net.minecraft.game.world.generation.features.hell.FireCluster;
import net.minecraft.game.world.generation.features.hell.Glowstone;
import net.minecraft.game.world.generation.features.hell.Graveyard;
import net.minecraft.game.world.generation.features.plants.GrassCluster;
import net.minecraft.game.world.generation.features.structure.fortress.Fortress;
import net.minecraft.game.world.generation.features.underground.OreLump;
import net.minecraft.game.world.generation.noise.OctaveNoise;
import net.minecraft.game.world.generation.structures.Structure;
import net.minecraft.game.world.generation.structures.StructureList;
import net.minecraft.util.ProgressBar;
import net.minecraft.util.math.MathHelper;

public class HellGenerator implements ChunkLoader {
    private World world;
    private Random random;
    public StructureList featureProvider;
    protected Biome[] biomesForGeneration;
    private OctaveNoise minNoise;
    private OctaveNoise maxNoise;
    private OctaveNoise mainNoise;
    private OctaveNoise beachNoiseGen;
    private OctaveNoise heightNoiseGen;
    private CaveCarver caveGenerator = new CaveCarver();
    private RavineCarver ravineGenerator = new RavineCarver();
    private Fortress fortressGenerator = new Fortress();
    private double[] noiseArray;
    private double[] beachNoise = new double[256];
    private double[] heightNoise = new double[256];
    private double[] mainNoiseSample;
    private double[] minLimitNoise;
    private double[] maxLimitNoise;
    private boolean isDecorating;

    public HellGenerator(World world, long seed) {
        this.world = world;
        this.random = new Random(seed);
        this.minNoise = new OctaveNoise(this.random, 16);
        this.maxNoise = new OctaveNoise(this.random, 16);
        this.mainNoise = new OctaveNoise(this.random, 8);
        this.beachNoiseGen = new OctaveNoise(this.random, 4);
        this.heightNoiseGen = new OctaveNoise(this.random, 4);
        this.featureProvider = new StructureList(this.world, this);
    }

    public void generateTerrain(int chunkX, int chunkZ, short[] blocks) {
        byte samples = 4;
        byte height = (byte)(this.world.getWorldHeight() / 8);
        int offset = samples + 1;
        byte heightPlusOne = (byte)(this.world.getWorldHeight() / 8 + 1);
        int lavaOffset = 1;
        if (this.world.getWorldHeight() < 256) {
            lavaOffset = 2;
        }

        this.noiseArray = this.initializeNoise(this.noiseArray, chunkX * samples, 0, chunkZ * samples, offset, heightPlusOne, offset);

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
                        double div1 = (upper3 - upper1) * innerMul;
                        double div2 = (upper4 - upper2) * innerMul;

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
                                    id = BlockList.netherrack.id;
                                }

                                blocks[index] = (short)id;
                                index += chunkSize;
                                maxHeight += scale;
                            }

                            high1 += div1;
                            high2 += div2;
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

    private double[] initializeNoise(double[] density, int startX, int startY, int startZ, int xSamples, int ySamples, int zSamples) {
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
        this.minLimitNoise = this.minNoise
            .generateNoiseOctaves(this.minLimitNoise, startX, startY, startZ, xSamples, ySamples, zSamples, horizontalScale, verticalScale, horizontalScale);
        this.maxLimitNoise = this.maxNoise
            .generateNoiseOctaves(this.maxLimitNoise, startX, startY, startZ, xSamples, ySamples, zSamples, horizontalScale, verticalScale, horizontalScale);
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
                    double min = this.minLimitNoise[index] / 512.0;
                    double max = this.maxLimitNoise[index] / 512.0;
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

    public void placeBlocks(int chunkX, int chunkZ, short[] blocks, byte[] metadata) {
        byte height = (byte)this.world.getWorldHeight();
        double offset = 0.03125;
        this.beachNoise = this.beachNoiseGen.generateNoiseOctaves(this.beachNoise, chunkX << 4, chunkZ << 4, 0, 16, 16, 1, offset, offset, 1.0);
        this.heightNoise = this.heightNoiseGen
            .generateNoiseOctaves(this.heightNoise, chunkX << 4, chunkZ << 4, 0, 16, 16, 1, offset * 2.0, offset * 2.0, offset * 2.0);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                boolean beaches = this.beachNoise[z << 4 | x] + this.random.nextDouble() * 0.2 > 3.0;
                int currHeight = (int)(this.heightNoise[z << 4 | x] / 3.0 + 3.0 + this.random.nextDouble() * 0.25);
                int currY = -1;
                short top = (short)BlockList.netherrack.id;
                short filler = (short)BlockList.netherrack.id;
                byte meta = 0;
                Biome biome = this.world.getBiomeAt(chunkX << 4 | x, chunkZ << 4 | z);

                for (int y = this.world.getWorldHeight() - 1; y >= 0; y--) {
                    int index = y << 8 | x << 4 | z;
                    if (y >= this.world.getWorldHeight() - 1 - this.random.nextInt(5)) {
                        blocks[index] = (short)BlockList.bedrock.id;
                    } else if (y <= 0 + this.random.nextInt(5)) {
                        blocks[index] = (short)BlockList.bedrock.id;
                    } else {
                        if (beaches && biome == BiomeList.hell && y <= this.world.getSeaLevel()) {
                            top = (short)BlockList.slowSand.id;
                            filler = (short)BlockList.slowSand.id;
                        }

                        short id = blocks[index];
                        if (id == 0) {
                            currY = -1;
                        } else if (id == BlockList.netherrack.id) {
                            if (currY == -1) {
                                if (currHeight <= 0) {
                                    top = 0;
                                    filler = (short)BlockList.netherrack.id;
                                } else if (y >= 32 && y < height - 16) {
                                    top = (short)BlockList.netherrack.id;
                                    filler = (short)BlockList.netherrack.id;
                                }

                                if (y < height && top == 0) {
                                    top = (short)BlockList.lavaStill.id;
                                }

                                currY = currHeight;
                                if (y >= height - 1) {
                                    blocks[index] = top;
                                    metadata[index] = meta;
                                } else {
                                    blocks[index] = filler;
                                    metadata[index] = meta;
                                }
                            } else if (currY > 0) {
                                currY--;
                                blocks[index] = filler;
                                metadata[index] = meta;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public Chunk generateChunk(int x, int z) {
        Sand.can_fall = false;
        this.random.setSeed(x * 341873128712L + z * 132897987541L);
        short[] blocks = new short[256 * this.world.getWorldHeight()];
        byte[] metadata = new byte[256 * this.world.getWorldHeight()];
        this.biomesForGeneration = this.world.getBiomeProvider().loadBiomes(this.biomesForGeneration, x << 4, z << 4, 16, 16);
        this.generateTerrain(x, z, blocks);
        this.placeBlocks(x, z, blocks, metadata);
        this.replaceBlocks(x, z, blocks, metadata, this.biomesForGeneration);
        this.caveGenerator.generate(this, this.world, x, z, blocks, metadata);
        this.ravineGenerator.generate(this, this.world, x, z, blocks, metadata);
        if (this.world.getSettings().generateStructures()) {
            this.fortressGenerator.generate(this, this.world, x, z, blocks, metadata);
            this.featureProvider.generateNearestFeatures(x, z, blocks, metadata, this.biomesForGeneration);
        }

        Chunk chunk = new Chunk(this.world, blocks, metadata, x, z);
        chunk.generateSkylightMap();
        byte[] biomeArray = chunk.getBiomes();

        for (int i = 0; i < biomeArray.length; i++) {
            biomeArray[i] = (byte)this.biomesForGeneration[i].id;
        }

        chunk.setBiomes(biomeArray);
        Sand.can_fall = true;
        return chunk;
    }

    public void replaceBlocks(int chunkX, int chunkZ, short[] blocks, byte[] metadata, Biome[] biomes) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                Biome biomeGen = biomes[z << 4 | x];
                int xx = chunkX << 4 | x;
                int zz = chunkZ << 4 | z;
                double offset = 0.03125;
                double heightValue = this.heightNoiseGen.generateNoise2D(xx * offset * 2.0, zz * offset * 2.0);
                this.random.setSeed(this.world.getSeed());
                biomeGen.caveBuilder(this.world, this.random, blocks, metadata, xx, zz, heightValue);
            }
        }
    }

    @Override
    public Chunk justGenerateForHeight(int chunkX, int chunkZ) {
        this.random.setSeed(chunkX * 341873128712L + chunkZ * 132897987541L);
        short[] blockArray = new short[256 * this.world.getWorldHeight()];
        byte[] metadataArray = new byte[256 * this.world.getWorldHeight()];
        this.generateTerrain(chunkX, chunkZ, blockArray);
        return new Chunk(this.world, blockArray, metadataArray, chunkX, chunkZ);
    }

    @Override
    public boolean chunkExists(int par1, int par2) {
        return true;
    }

    @Override
    public void populate(ChunkLoader loader, int chunkX, int chunkZ) {
        Biome biomeGen = this.world.getBiomeProvider().getBiomeAt((chunkX << 4) + 16, (chunkZ << 4) + 16);
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
        biomeGen.prePopulate(this.world, this.random, x0, z0);
        if (this.world.settings.generateStructures()) {
            this.fortressGenerator.generateStructuresInChunk(this.world, this.random, chunkX, chunkZ);
            this.featureProvider.populateFeatures(this.world, this.random, chunkX, chunkZ);
        }

        if (biomeGen.generateLava) {
            for (int index = 0; index < biomeGen.lavaFallAttempts; index++) {
                int x = x0 + this.random.nextInt(16) + 8;
                int y = this.random.nextInt(this.world.getWorldHeight());
                int z = z0 + this.random.nextInt(16) + 8;
                Waterfall.HELLAVA.place(this.world, this.random, x, y, z);
            }

            int y = x0 + this.random.nextInt(16) + 8;
            int z = this.random.nextInt(this.world.getWorldHeight());
            int x = z0 + this.random.nextInt(16) + 8;
            Pond.LAVA.place(this.world, this.random, y, z, x);

            for (int var15 = 0; var15 < 20; var15++) {
                y = x0 + this.random.nextInt(16) + 8;
                z = this.random.nextInt(this.world.getWorldHeight());
                x = z0 + this.random.nextInt(16) + 8;
                Glowstone.INSTANCE.place(this.world, this.random, y, z, x);
            }
        }

        for (int index = 0; index < 10; index++) {
            int y = x0 + this.random.nextInt(16) + 8;
            int z = this.random.nextInt(this.world.getWorldHeight());
            int x = z0 + this.random.nextInt(16) + 8;
            FireCluster.INSTANCE.place(this.world, this.random, y, z, x);
        }

        for (int var17 = 0; var17 < biomeGen.marbleLumpAttempts; var17++) {
            int y = x0 + this.random.nextInt(16);
            int z = this.random.nextInt(this.world.getSeaLevel() - 16);
            int x = z0 + this.random.nextInt(16);
            OreLump.netherMagma.place(this.world, this.random, y, z, x);
        }

        for (int var18 = 0; var18 < biomeGen.ironLumpAttempts; var18++) {
            int y = x0 + this.random.nextInt(16);
            int z = this.random.nextInt(this.world.getWorldHeight());
            int x = z0 + this.random.nextInt(16);
            OreLump.netherGold.place(this.world, this.random, y, z, x);
        }

        for (int var19 = 0; var19 < biomeGen.goldLumpAttempts; var19++) {
            int y = x0 + this.random.nextInt(16);
            int z = this.random.nextInt(this.world.getWorldHeight());
            int x = z0 + this.random.nextInt(16);
            OreLump.amethyst.place(this.world, this.random, y, z, x);
        }

        for (int var20 = 0; var20 < biomeGen.diamondLumpAttempts; var20++) {
            int y = x0 + this.random.nextInt(16);
            int z = this.random.nextInt(this.world.getWorldHeight());
            int x = z0 + this.random.nextInt(16);
            OreLump.damascus.place(this.world, this.random, y, z, x);
        }

        if (biomeGen.tallGrassAttempts > 0) {
            for (int var21 = 0; var21 < biomeGen.tallGrassAttempts; var21++) {
                int y = x0 + this.random.nextInt(16) + 8;
                int z = this.random.nextInt(this.world.getWorldHeight());
                int x = z0 + this.random.nextInt(16) + 8;
                new GrassCluster(BlockList.tallGrass.id, biomeGen.grassMeta).place(this.world, this.random, y, z, x);
            }
        }

        if (biomeGen instanceof HellDesert) {
            int x = x0 + 8;
            int z = z0 + 8;
            int y = this.world.getRandomHeight(x, z);
            Graveyard.INSTANCE.place(this.world, this.random, x, y, z);
        }

        biomeGen.postPopulate(this.world, this.random, x0, z0);
        this.isDecorating = false;
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
        return "HellGenerator";
    }

    @Override
    public BlockPosition findClosestStructure(World world, Class<? extends Structure> target, int x, int y, int z) {
        return this.featureProvider != null ? this.featureProvider.getNearestFeatures(target, x >> 4, z >> 4) : null;
    }
}
