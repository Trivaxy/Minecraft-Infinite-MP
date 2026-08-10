package net.minecraft.game.world.generation.structures.misc;

import java.util.Arrays;
import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.game.world.chunk.ChunkLoader;
import net.minecraft.game.world.generation.structures.SchematicStructure;
import net.minecraft.game.world.generation.structures.StructureList;

public class Volcano extends SchematicStructure {
    private static final float chanceToExpandVolcano = 0.3F;
    private static final float chanceToExpandHollow = 0.2F;
    private int volcanoHeight = 64;
    private int width;

    public Volcano(World world, int originChunkX, int originChunkZ, StructureList featureProvider) {
        super(world, originChunkX, originChunkZ, featureProvider);
    }

    @Override
    public int getHeight() {
        return this.volcanoHeight;
    }

    @Override
    public void generateSchematic(World world, Random rand, Biome biome, int chunkX, int chunkZ) {
        int volcanoMaterial = BlockList.stone.id | 8192;
        int empty = -1;
        int fill = 1;
        int hollow = 0;
        int hollowFromTop = 12;
        this.width = (1 + this.getRadius() * 2) * 16;

        for (int[][] arr1 : this.schematic) {
            for (int[] arr2 : arr1) {
                Arrays.fill(arr2, empty);
            }
        }

        int iniSqSize = 1 + 2 * this.getRadius();
        int iniSqPos = this.width / 2 - iniSqSize / 2;

        for (int x = 0; x < iniSqSize; x++) {
            for (int z = 0; z < iniSqSize; z++) {
                this.schematic[iniSqPos + x][iniSqPos + z][this.volcanoHeight - 2] = fill;
            }
        }

        this.growVolcanoFrom(this.volcanoHeight - 2, rand, 0.3F, fill);
        int holeSqSize = 5 + 2 * this.getRadius();
        int holeSqPos = this.width / 2 - holeSqSize / 2;

        for (int x = 0; x < holeSqSize; x++) {
            for (int z = 0; z < holeSqSize; z++) {
                this.schematic[holeSqPos + x][holeSqPos + z][this.volcanoHeight - hollowFromTop] = hollow;
            }
        }

        this.growVolcanoFrom(this.volcanoHeight - hollowFromTop, rand, 0.2F, hollow);

        for (int y = this.volcanoHeight - hollowFromTop; y < this.volcanoHeight; y++) {
            for (int x = 0; x < this.width; x++) {
                for (int z = 0; z < this.width; z++) {
                    this.schematic[x][z][y] = empty;
                }
            }
        }

        int radius = iniSqSize + 3;
        int radiusSq = radius * radius;
        int x0 = this.width / 2;
        int z0 = this.width / 2;
        int y0 = this.volcanoHeight - hollowFromTop;

        for (int x = 0; x < radius; x++) {
            int xx = x * x;

            for (int z = 0; z < radius; z++) {
                int zz = z * z;

                for (int y = 0; y < radius; y++) {
                    if (xx + zz + y * y < radiusSq) {
                        this.schematic[x0 + x][z0 + z][y0 - y] = hollow;
                        this.schematic[x0 - x][z0 + z][y0 - y] = hollow;
                        this.schematic[x0 + x][z0 - z][y0 - y] = hollow;
                        this.schematic[x0 - x][z0 - z][y0 - y] = hollow;
                    }
                }
            }
        }

        for (int x = 0; x < this.width; x++) {
            for (int z = 0; z < this.width; z++) {
                boolean first = true;
                boolean doReplace = false;

                for (int y = this.volcanoHeight - 1; y > 0; y--) {
                    if (first && this.schematic[x][z][y] == fill) {
                        first = false;
                        doReplace = true;
                        if (rand.nextInt(200) == 0) {
                            this.schematic[x][z][y + 1] = (short)BlockList.lavaMoving.id;
                        }
                    }

                    if (doReplace) {
                        if (this.schematic[x][z][y] == hollow) {
                            doReplace = false;
                        }

                        this.schematic[x][z][y] = volcanoMaterial;
                    }
                }

                if (this.schematic[x][z][0] == hollow) {
                    this.schematic[x][z][0] = (short)BlockList.lavaStill.id;
                }
            }
        }
    }

    private void growVolcanoFrom(int topY, Random rand, float chance, int value) {
        for (int y = topY - 1; y >= 0; y--) {
            for (int x = 1; x < this.width - 1; x++) {
                for (int z = 1; z < this.width - 1; z++) {
                    if (this.schematic[x][z][y + 1] == value) {
                        this.schematic[x][z][y] = value;
                    } else {
                        int expand = 0;
                        if (this.schematic[x - 1][z][y + 1] == value) {
                            expand++;
                        }

                        if (this.schematic[x + 1][z][y + 1] == value) {
                            expand++;
                        }

                        if (this.schematic[x][z - 1][y + 1] == value) {
                            expand++;
                        }

                        if (this.schematic[x][z + 1][y + 1] == value) {
                            expand++;
                        }

                        if (expand > 0 && (expand > 1 || rand.nextFloat() < chance)) {
                            this.schematic[x][z][y] = value;
                        }
                    }
                }
            }
        }
    }

    @Override
    public int getY0() {
        return this.world.getSeaLevel();
    }

    @Override
    public int getRadius() {
        return 5;
    }

    @Override
    public int getSpawnChance() {
        return 10;
    }

    @Override
    public boolean canSpawnHere(ChunkLoader chunkProvider, World world, Random rand, Biome biome, int chunkX, int chunkZ) {
        return biome == BiomeList.volcano || biome == BiomeList.island;
    }

    @Override
    public int minimumSeparation() {
        return 32;
    }

    @Override
    public void generate(int chunkX, int chunkZ, short[] blockArray, byte[] metadata, Biome[] biomesForGeneration) {
        this.drawPieceOnGeneration(chunkX, chunkZ, blockArray, metadata);
    }

    @Override
    public void populate(World world, Random rand, int chunkX, int chunkZ) {
    }
}
