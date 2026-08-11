package net.minecraft.game.world.generation.structures;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.Random;
import java.util.logging.Logger;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.cold.Tundra;
import net.minecraft.game.world.biomes.hell.HellBiome;
import net.minecraft.game.world.biomes.hot.Desert;
import net.minecraft.game.world.biomes.hot.VolcanicWasteland;
import net.minecraft.game.world.biomes.hot.Wasteland;
import net.minecraft.game.world.biomes.ocean.Ocean;
import net.minecraft.game.world.biomes.ocean.TropicalIsland;
import net.minecraft.game.world.biomes.sky.SkyBiome;
import net.minecraft.game.world.biomes.temperate.Forest;
import net.minecraft.game.world.biomes.temperate.Plains;
import net.minecraft.game.world.biomes.warm.Jungle;
import net.minecraft.game.world.biomes.warm.Rainforest;
import net.minecraft.game.world.biomes.warm.Swamp;
import net.minecraft.game.world.chunk.BlockPosition;
import net.minecraft.game.world.chunk.ChunkLoader;
import net.minecraft.game.world.chunk.ChunkUtils;
import net.minecraft.game.world.generation.structures.labyrinth.Labyrinth;
import net.minecraft.game.world.generation.structures.misc.Airship;
import net.minecraft.game.world.generation.structures.misc.Castle;
import net.minecraft.game.world.generation.structures.misc.Cathedral;
import net.minecraft.game.world.generation.structures.misc.Fossil;
import net.minecraft.game.world.generation.structures.misc.OceanMonument;
import net.minecraft.game.world.generation.structures.misc.Pyramid;
import net.minecraft.game.world.generation.structures.misc.Shipwreck;
import net.minecraft.game.world.generation.structures.misc.SlimeLair;
import net.minecraft.game.world.generation.structures.misc.Slipgate;
import net.minecraft.game.world.generation.structures.misc.Tower;
import net.minecraft.game.world.generation.structures.misc.Volcano;
import net.minecraft.game.world.generation.structures.oceanruins.OceanRuins;
import net.minecraft.game.world.generation.structures.palace.Palace;
import net.minecraft.game.world.generation.structures.tree.TreeStructure;
import net.minecraft.game.world.generation.structures.village.AbandonedVillage;
import net.minecraft.util.logging.LoggingUtils;

public class StructureList {
    private static final Logger logger = Logger.getLogger("Minecraft");
    public World world;
    public ChunkLoader chunkProvider;
    private Random rand = new Random();
    private final Long2ObjectOpenHashMap<Structure> featureList;
    private final LongOpenHashSet generatedChunks;

    public StructureList(World world, ChunkLoader chunkProvider) {
        this.world = world;
        this.chunkProvider = chunkProvider;
        this.featureList = new Long2ObjectOpenHashMap<>();
        this.generatedChunks = new LongOpenHashSet();
    }

    public Structure getFeatureForChunkCoords(int chunkX, int chunkZ) {
        Structure feature = null;
        long seed = this.world.getSeed() + chunkX * 25117L + chunkZ * 151121L;
        this.rand.setSeed(seed);
        Biome biome = this.world.getBiomeProvider().getBiomeAt((chunkX << 4) + 8, (chunkZ << 4) + 8);
        if (this.rand.nextInt(5) == 0) {
            feature = new Slipgate(this.world, chunkX, chunkZ, this);
        } else if (biome instanceof HellBiome) {
            feature = new Palace(this.world, chunkX, chunkZ, this);
        } else if (biome instanceof SkyBiome) {
            switch (this.rand.nextInt(3)) {
                case 0:
                    feature = new Airship(this.world, chunkX, chunkZ, this);
                    break;
                case 1:
                    feature = new Tower(this.world, chunkX, chunkZ, this);
                    break;
                case 2:
                    feature = new Cathedral(this.world, chunkX, chunkZ, this);
            }
        } else if (biome instanceof Plains) {
            switch (this.rand.nextInt(2)) {
                case 1:
                    feature = new AbandonedVillage(this.world, chunkX, chunkZ, this);
                    break;
                default:
                    feature = new Labyrinth(this.world, chunkX, chunkZ, this);
            }
        } else if (biome instanceof Tundra) {
            switch (this.rand.nextInt(2)) {
                case 1:
                    feature = new Castle(this.world, chunkX, chunkZ, this);
                    break;
                default:
                    feature = new Labyrinth(this.world, chunkX, chunkZ, this);
            }
        } else if (biome instanceof Forest) {
            switch (this.rand.nextInt(2)) {
                case 1:
                    feature = new TreeStructure(this.world, chunkX, chunkZ, this);
                    break;
                default:
                    feature = new Labyrinth(this.world, chunkX, chunkZ, this);
            }
        } else if (biome instanceof Desert || biome instanceof Wasteland) {
            switch (this.rand.nextInt(3)) {
                case 1:
                    feature = new Fossil(this.world, chunkX, chunkZ, this);
                    break;
                case 2:
                    feature = new Pyramid(this.world, chunkX, chunkZ, this);
                    break;
                default:
                    feature = new Labyrinth(this.world, chunkX, chunkZ, this);
            }
        } else if (biome instanceof Jungle || biome instanceof Rainforest || biome instanceof Swamp) {
            switch (this.rand.nextInt(2)) {
                case 1:
                    feature = new SlimeLair(this.world, chunkX, chunkZ, this);
                    break;
                default:
                    feature = new Labyrinth(this.world, chunkX, chunkZ, this);
            }
        } else if (biome instanceof Ocean) {
            switch (this.rand.nextInt(3)) {
                case 1:
                    feature = new OceanRuins(this.world, chunkX, chunkZ, this);
                    break;
                case 2:
                    feature = new OceanMonument(this.world, chunkX, chunkZ, this);
                    break;
                default:
                    feature = new Shipwreck(this.world, chunkX, chunkZ, this);
            }
        } else if (!(biome instanceof TropicalIsland) && !(biome instanceof VolcanicWasteland)) {
            feature = new Labyrinth(this.world, chunkX, chunkZ, this);
        } else {
            feature = new Volcano(this.world, chunkX, chunkZ, this);
        }

        if (!feature.shouldSpawn(this.chunkProvider, this.world, new Random(seed), biome, chunkX, chunkZ)) {
            return null;
        }

        int separation = feature.minimumSeparation();

        for (Structure otherFeature : this.featureList.values()) {
            if (Math.abs(feature.centerX - otherFeature.centerX) <= separation || Math.abs(feature.centerZ - otherFeature.centerZ) <= separation) {
                return null;
            }
        }

        feature.setup(this.world, this.rand, biome, chunkX, chunkZ);
        return feature;
    }

    public boolean generateNearestFeatures(int chunkX, int chunkZ, short[] blockArray, byte[] metadata, Biome[] biomesForGeneration) {
        boolean featureInChunk = false;
        LongOpenHashSet featureHashes = new LongOpenHashSet();

        for (int i = 0; i <= 3; i++) {
            for (int x = chunkX - i; x <= chunkX + i; x++) {
                for (int z = chunkZ - i; z <= chunkZ + i; z++) {
                    long chunkHash = ChunkUtils.getPackedCoords(x, z);
                    Structure feature = this.featureList.get(chunkHash);
                    if (feature == null) {
                        feature = this.getFeatureForChunkCoords(x, z);
                        if (feature != null) {
                            this.featureList.put(chunkHash, feature);
                        }
                    }

                    if (feature != null && feature.getRadius() >= i) {
                        featureHashes.add(chunkHash);
                    }
                }
            }
        }

        for (long featureHash : featureHashes) {
            this.featureList.get(featureHash).generate(chunkX, chunkZ, blockArray, metadata, biomesForGeneration);
            featureInChunk = true;
        }

        return featureInChunk;
    }

    public BlockPosition getNearestFeatures(Class<? extends Structure> target, int chunkX, int chunkZ) {
        LongOpenHashSet featureHashes = new LongOpenHashSet();

        for (int x = chunkX - 10; x <= chunkX + 10; x++) {
            for (int z = chunkZ - 10; z <= chunkZ + 10; z++) {
                long seed = this.world.getSeed() + x * 25117L + z * 151121L;
                Biome biome = this.world.getBiomeProvider().getBiomeAt((x << 4) + 8, (z << 4) + 8);
                long chunkHash = ChunkUtils.getPackedCoords(x, z);
                Structure feature = this.featureList.get(chunkHash);
                if (feature == null) {
                    feature = this.getFeatureForChunkCoords(x, z);
                    if (feature != null && target.isInstance(feature) && feature.shouldSpawn(this.chunkProvider, this.world, new Random(seed), biome, x, z)) {
                        this.featureList.put(chunkHash, feature);
                        return new BlockPosition(feature.centerX, 0, feature.centerZ);
                    }
                }

                if (feature != null && target.isInstance(feature) && feature.shouldSpawn(this.chunkProvider, this.world, new Random(seed), biome, x, z)) {
                    featureHashes.add(chunkHash);
                    return new BlockPosition(feature.centerX, 0, feature.centerZ);
                }
            }
        }

        return null;
    }

    public void populateFeatures(World world, Random rand, int chunkX, int chunkZ) {
        long thisChunkHash = ChunkUtils.getPackedCoords(chunkX, chunkZ);
        if (this.generatedChunks.contains(thisChunkHash)) {
            LoggingUtils.warnf(logger, "Already calculated / calculating %d, %d", chunkX, chunkZ);
        } else {
            this.generatedChunks.add(thisChunkHash);
            int i = 3;

            for (int x = chunkX - i; x <= chunkX + i; x++) {
                for (int z = chunkZ - i; z <= chunkZ + i; z++) {
                    long chunkHash = ChunkUtils.getPackedCoords(x, z);
                    Structure feature = this.featureList.get(chunkHash);
                    if (feature != null
                        && Math.abs(feature.originChunkX - x) <= feature.getRadius()
                        && Math.abs(feature.originChunkZ - z) <= feature.getRadius()) {
                        feature.populate(world, rand, chunkX, chunkZ);
                    }
                }
            }
        }
    }
}
