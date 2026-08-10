package net.minecraft.game.world.biomes.ocean;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.mob.monster.creeper.CreeperWind;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.game.world.generation.features.plants.BigPumpkin;
import net.minecraft.game.world.generation.features.plants.FlowerPatch;
import net.minecraft.game.world.generation.features.plants.HangingPlants;
import net.minecraft.game.world.generation.features.plants.PumpkinPatch;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.game.world.generation.features.trees.coniferous.PineTree;
import net.minecraft.game.world.generation.features.trees.coniferous.SpruceTree;
import net.minecraft.game.world.generation.features.trees.deciduous.AspenTree;
import net.minecraft.game.world.generation.features.trees.deciduous.CherryTree;
import net.minecraft.game.world.generation.features.trees.deciduous.MapleTree;
import net.minecraft.game.world.generation.features.trees.misc.Shrub;
import net.minecraft.game.world.generation.features.trees.tropical.JungleTree;
import net.minecraft.game.world.generation.features.trees.tropical.RainforestTree;
import net.minecraft.game.world.generation.noise.OctaveNoise;
import net.minecraft.game.world.mobspawner.SpawnListEntry;

public class FloatingIsland extends Ocean {
    private static final short AIR = 0;
    private static final short DEFAULT_BLOCK = (short)BlockList.stone.id;
    private static final short SNOW = (short)BlockList.snowBlock.id;
    private static final short ICE = (short)BlockList.packedIce.id;
    private static final short MOSS = (short)BlockList.moss.id;
    private OctaveNoise noiseGen1;
    private OctaveNoise mossNoise;
    private long noiseSeed;

    public FloatingIsland(int biomeID, Biome.Temperature temp) {
        super(biomeID);
        this.treeAttempts = 3;
        this.diamondLumpMaxHeight = 256;
        this.emeraldLumpMaxHeight = 256;
        this.sapphireLumpMaxHeight = 256;
        this.rubyLumpMaxHeight = 256;
        this.goldLumpMaxHeight = 256;
        this.ironLumpMaxHeight = 256;
        this.coalLumpAttempts = 40;
        this.ironLumpAttempts = 40;
        this.copperLumpAttempts = 40;
        this.goldLumpAttempts = 20;
        this.emeraldLumpAttempts = 10;
        this.sapphireLumpAttempts = 10;
        this.rubyLumpAttempts = 10;
        this.diamondLumpAttempts = 5;
        this.waterAttempts = 1;
        this.flowersAttempts = 16;
        this.tallGrassAttempts = 1;
        this.stoneHeight = -1;
        this.defaultStoneType = 1;
        if (temp == Biome.Temperature.COLD) {
            this.topMeta = this.fillerMeta = this.grassMeta = 1;
            this.defaultStoneType = 6;
        } else if (temp == Biome.Temperature.HOT) {
            this.topMeta = this.fillerMeta = this.grassMeta = 3;
            this.defaultStoneType = 4;
        } else if (temp == Biome.Temperature.WARM) {
            this.topMeta = this.fillerMeta = this.grassMeta = 2;
            this.defaultStoneType = 5;
        }

        this.monsters.add(new SpawnListEntry(CreeperWind.class, 4));
    }

    @Override
    public void prePopulate(World world, Random rand, int x0, int z0) {
        super.prePopulate(world, rand, x0, z0);
        if (this.id != BiomeList.winterFloating.id) {
            for (int i = 0; i < 3; i++) {
                int x = x0 + rand.nextInt(16) + 8;
                int y = rand.nextInt(world.getSeaLevel()) + world.getSeaLevel();
                int z = z0 + rand.nextInt(16) + 8;
                FlowerPatch.glowLychen.place(world, rand, x, y, z);
            }

            for (int var32 = 0; var32 < 100; var32++) {
                int x = x0 + rand.nextInt(16) + 8;
                int y = rand.nextInt(world.getSeaLevel()) + world.getSeaLevel();
                int z = z0 + rand.nextInt(16) + 8;
                if (world.getBlock(x, y, z) == BlockList.moss) {
                    HangingPlants.VINES.place(world, rand, x, y, z);
                }
            }
        } else {
            for (int i = 0; i < 3; i++) {
                int x = x0 + rand.nextInt(16) + 8;
                int y = rand.nextInt(world.getSeaLevel()) + world.getSeaLevel();
                int z = z0 + rand.nextInt(16) + 8;
                FlowerPatch.berryBush.place(world, rand, x, y, z);
            }

            for (int var34 = 0; var34 < 100; var34++) {
                int x = x0 + rand.nextInt(16) + 8;
                int y = rand.nextInt(world.getSeaLevel()) + world.getSeaLevel();
                int z = z0 + rand.nextInt(16) + 8;
                if (world.getBlock(x, y, z) == BlockList.packedIce) {
                    HangingPlants.ICICLES.place(world, rand, x, y, z);
                }
            }
        }

        if (this.id == BiomeList.autumnFloating.id) {
            int y = x0 + rand.nextInt(16) + 8;
            int z = rand.nextInt(world.getSeaLevel()) + world.getSeaLevel();
            int x = z0 + rand.nextInt(16) + 8;
            PumpkinPatch.pumpkinPatch.place(world, rand, y, z, x);

            for (int var35 = 0; var35 < 16; var35++) {
                x = x0 + rand.nextInt(16) + 8;
                z = z0 + rand.nextInt(16) + 8;
                y = rand.nextInt(world.getSeaLevel()) + world.getSeaLevel();
                BigPumpkin.PUMPKIN.place(world, rand, x, y, z);
            }
        } else if (this.id == BiomeList.summerFloating.id) {
            int y = x0 + rand.nextInt(16) + 8;
            int z = rand.nextInt(world.getSeaLevel()) + world.getSeaLevel();
            int x = z0 + rand.nextInt(16) + 8;
            PumpkinPatch.melonPatch.place(world, rand, y, z, x);

            for (int var36 = 0; var36 < 5; var36++) {
                y = x0 + rand.nextInt(16) + 8;
                x = z0 + rand.nextInt(16) + 8;
                Shrub.jungleShrub.place(world, rand, y, world.getHeightmap(y, x), x);
            }

            for (int var37 = 0; var37 < 16; var37++) {
                x = x0 + rand.nextInt(16) + 8;
                z = z0 + rand.nextInt(16) + 8;
                y = rand.nextInt(world.getSeaLevel()) + world.getSeaLevel();
                BigPumpkin.MELONE.place(world, rand, x, y, z);
            }
        }
    }

    @Override
    public Tree defaultTreeType(Random rand) {
        if (this.id == BiomeList.winterFloating.id) {
            return rand.nextInt(5) == 0 ? new SpruceTree() : new PineTree();
        } else if (this.id == BiomeList.autumnFloating.id) {
            return rand.nextInt(5) == 0 ? new MapleTree() : new AspenTree();
        } else if (this.id == BiomeList.summerFloating.id) {
            return rand.nextInt(5) == 0 ? new JungleTree() : new RainforestTree(rand.nextInt(3));
        } else {
            return rand.nextInt(5) == 0 ? new CherryTree() : super.defaultTreeType(rand);
        }
    }

    @Override
    public void surfaceBuilder(
        World world, Random rand, short[] blocks, byte[] metadata, int xAbs, int zAbs, double stoneNoise, double sandValue, double gravelValue
    ) {
        if (this.noiseSeed != world.getSeed() || this.noiseSeed == 0L) {
            this.noiseSeed = world.getSeed();
            Random random = new Random(this.noiseSeed);
            this.noiseGen1 = new OctaveNoise(random, 6);
            this.mossNoise = new OctaveNoise(random, 6);
        }

        boolean generateGrass = this.mossNoise.generateNoise2D(xAbs, zAbs) > 1.0;
        int seaLevel = world.getSeaLevel() + world.getSeaLevel() / 2 + world.getSeaLevel() / 3;
        int seaLevelLower = world.getSeaLevel() + world.getSeaLevel() / 2 + world.getSeaLevel() / 4;
        int x = xAbs & 15;
        int z = zAbs & 15;
        double packedIceTop = 0.0;
        double packedIceBottom = 0.0;
        double density = Math.min(Math.abs(stoneNoise), this.noiseGen1.generateNoise2D(xAbs * 0.25, zAbs * 0.25));
        double densityLower = Math.sqrt(Math.abs(density) / 24.0) * (1.0 - density) + density * 2.0;
        if (density > 1.2) {
            packedIceTop = density * (density / 2.0);
            packedIceTop += seaLevel;
            packedIceBottom = densityLower * (densityLower / 2.0);
            packedIceBottom = seaLevelLower - packedIceBottom + 7.0;
        }

        int beginning = world.getWorldHeight() - 1;

        for (int y = beginning; y >= 0; y--) {
            int index = y << 8 | z << 4 | x;
            short blockID = blocks[index];
            if (blockID == 0 && y < (int)packedIceTop && y > (int)packedIceBottom) {
                if (y >= seaLevelLower - 10 - rand.nextInt(10) || !generateGrass) {
                    if (y < (int)packedIceTop - 10 - rand.nextInt(10) && this.id == BiomeList.winterFloating.id) {
                        blockID = blocks[index] = SNOW;
                    } else {
                        blockID = blocks[index] = DEFAULT_BLOCK;
                    }
                } else if (this.id == BiomeList.winterFloating.id) {
                    blockID = blocks[index] = ICE;
                } else {
                    blockID = blocks[index] = MOSS;
                }
            }
        }

        super.surfaceBuilder(world, rand, blocks, metadata, xAbs, zAbs, stoneNoise, sandValue, gravelValue);
    }
}
