package net.minecraft.game.world.generation.layers;

import net.minecraft.game.world.WorldType;

public abstract class GenLayer {
    public final long seed;
    private long worldGenSeed;
    public GenLayer parent;
    protected long chunkSeed;
    private long baseSeed;

    public static GenLayer[] populateHellBiomes(long worldSeed) {
        ClimateLayer climate = new ClimateLayer(100L);
        climate.initWorldSeed(worldSeed);
        GenLayer mainLayer = new HellLayer(200L, climate);
        mainLayer = ZoomLayer.magnify(1000L, mainLayer, 2);

        for (int layer = 0; layer < 4; layer++) {
            mainLayer = new ZoomLayer(1000L + layer, mainLayer);
            mainLayer = new SmoothLayer(3000L + layer, mainLayer);
        }

        VoronoiZoomLayer zoom = new VoronoiZoomLayer(10L, mainLayer);
        mainLayer.initWorldSeed(worldSeed);
        zoom.initWorldSeed(worldSeed);
        return new GenLayer[]{mainLayer, zoom};
    }

    public static GenLayer[] populateSkyBiomes(long worldSeed) {
        ClimateLayer climate = new ClimateLayer(100L);
        climate.initWorldSeed(worldSeed);
        GenLayer mainLayer = new SkyLayer(200L, climate);
        mainLayer = ZoomLayer.magnify(1000L, mainLayer, 2);

        for (int layer = 0; layer < 4; layer++) {
            mainLayer = new ZoomLayer(1000L + layer, mainLayer);
            mainLayer = new SmoothLayer(3000L + layer, mainLayer);
        }

        VoronoiZoomLayer zoom = new VoronoiZoomLayer(10L, mainLayer);
        mainLayer.initWorldSeed(worldSeed);
        zoom.initWorldSeed(worldSeed);
        return new GenLayer[]{mainLayer, zoom};
    }

    public static GenLayer[] populateBiomes(long worldSeed, WorldType type) {
        if (type == WorldType.OCEAN) {
            return oceanOnly(worldSeed);
        }

        if (type == WorldType.INLAND) {
            return landOnly(worldSeed);
        }

        LandLayer land = new LandLayer(1L);
        FuzzyZoomLayer fuzzyZoom = new FuzzyZoomLayer(2000L, land);
        IslandsLayer islands = new IslandsLayer(1L, fuzzyZoom);
        ZoomLayer zoom = new ZoomLayer(2001L, islands);
        islands = new IslandsLayer(2L, zoom);
        islands = new IslandsLayer(25L, islands);
        islands = new IslandsLayer(50L, islands);
        islands = new IslandsLayer(75L, islands);
        islands = new IslandsLayer(100L, islands);

        for (int i = 2; i <= 4; i++) {
            zoom = new ZoomLayer(2000L + i, islands);
            islands = new IslandsLayer(3L, zoom);
        }

        islands = new TropicalIslandsLayer(5L, islands);
        ShoreLayer shore = new ShoreLayer(4L, islands);
        ClimateLayer climate = new ClimateLayer(100L);
        climate.initWorldSeed(worldSeed);
        BiomesLayer biomesLayer = new BiomesLayer(200L, shore, climate);
        GenLayer variantsLayer = new MutateBiomesLayer(1000L, biomesLayer, climate);
        variantsLayer = ZoomLayer.magnify(1000L, variantsLayer, 2);

        for (int layer = 0; layer < 4; layer++) {
            variantsLayer = new ZoomLayer(1000L + layer, variantsLayer);
            variantsLayer = new SmoothLayer(3000L + layer, variantsLayer);
            if (layer == 0) {
                variantsLayer = new BeachesLayer(1000L, variantsLayer);
            }
        }

        VoronoiZoomLayer voronoiZoom = new VoronoiZoomLayer(10L, variantsLayer);
        variantsLayer.initWorldSeed(worldSeed);
        voronoiZoom.initWorldSeed(worldSeed);
        return new GenLayer[]{variantsLayer, voronoiZoom};
    }

    private static GenLayer[] landOnly(long worldSeed) {
        ClimateLayer climate = new ClimateLayer(100L);
        climate.initWorldSeed(worldSeed);
        BiomesLayer biomes = new BiomesLayer(200L, climate, climate, true);
        GenLayer variants = new MutateBiomesLayer(1000L, biomes, climate);
        variants = ZoomLayer.magnify(1000L, variants, 2);

        for (int layer = 0; layer < 4; layer++) {
            variants = new ZoomLayer(1000L + layer, variants);
            variants = new SmoothLayer(3000L + layer, variants);
        }

        VoronoiZoomLayer zoom = new VoronoiZoomLayer(10L, variants);
        variants.initWorldSeed(worldSeed);
        zoom.initWorldSeed(worldSeed);
        return new GenLayer[]{variants, zoom};
    }

    private static GenLayer[] oceanOnly(long worldSeed) {
        OceanLayer oceans = new OceanLayer(1L);
        TropicalIslandsLayer tropics = new TropicalIslandsLayer(5L, oceans);
        ShoreLayer shore = new ShoreLayer(4L, tropics);
        ClimateLayer climate = new ClimateLayer(100L);
        climate.initWorldSeed(worldSeed);
        BiomesLayer biomes = new BiomesLayer(200L, shore, climate);
        GenLayer variants = new MutateBiomesLayer(1000L, biomes, climate);
        variants = ZoomLayer.magnify(1000L, variants, 2);

        for (int layer = 0; layer < 4; layer++) {
            variants = new ZoomLayer(1000L + layer, variants);
            variants = new SmoothLayer(3000L + layer, variants);
            if (layer == 1) {
                variants = new BeachesLayer(1000L, variants);
            }
        }

        VoronoiZoomLayer zoom = new VoronoiZoomLayer(10L, variants);
        variants.initWorldSeed(worldSeed);
        zoom.initWorldSeed(worldSeed);
        return new GenLayer[]{variants, zoom};
    }

    public GenLayer(long baseSeed) {
        this.seed = baseSeed;
        this.baseSeed = baseSeed;
        this.baseSeed = this.baseSeed * (this.baseSeed * 6364136223846793005L + 1442695040888963407L);
        this.baseSeed += baseSeed;
        this.baseSeed = this.baseSeed * (this.baseSeed * 6364136223846793005L + 1442695040888963407L);
        this.baseSeed += baseSeed;
        this.baseSeed = this.baseSeed * (this.baseSeed * 6364136223846793005L + 1442695040888963407L);
        this.baseSeed += baseSeed;
    }

    public void initWorldSeed(long worldSeed) {
        this.worldGenSeed = worldSeed;
        if (this.parent != null) {
            this.parent.initWorldSeed(worldSeed);
        }

        this.worldGenSeed = this.worldGenSeed * (this.worldGenSeed * 6364136223846793005L + 1442695040888963407L);
        this.worldGenSeed = this.worldGenSeed + this.baseSeed;
        this.worldGenSeed = this.worldGenSeed * (this.worldGenSeed * 6364136223846793005L + 1442695040888963407L);
        this.worldGenSeed = this.worldGenSeed + this.baseSeed;
        this.worldGenSeed = this.worldGenSeed * (this.worldGenSeed * 6364136223846793005L + 1442695040888963407L);
        this.worldGenSeed = this.worldGenSeed + this.baseSeed;
    }

    public void initChunkSeed(long packedX, long packedZ) {
        this.chunkSeed = this.worldGenSeed;
        this.chunkSeed = this.chunkSeed * (this.chunkSeed * 6364136223846793005L + 1442695040888963407L);
        this.chunkSeed += packedX;
        this.chunkSeed = this.chunkSeed * (this.chunkSeed * 6364136223846793005L + 1442695040888963407L);
        this.chunkSeed += packedZ;
        this.chunkSeed = this.chunkSeed * (this.chunkSeed * 6364136223846793005L + 1442695040888963407L);
        this.chunkSeed += packedX;
        this.chunkSeed = this.chunkSeed * (this.chunkSeed * 6364136223846793005L + 1442695040888963407L);
        this.chunkSeed += packedZ;
    }

    protected int nextInt(int seed) {
        int i = (int)((this.chunkSeed >> 24) % seed);
        if (i < 0) {
            i += seed;
        }

        this.chunkSeed = this.chunkSeed * (this.chunkSeed * 6364136223846793005L + 1442695040888963407L);
        this.chunkSeed = this.chunkSeed + this.worldGenSeed;
        return i;
    }

    public abstract int[] getInts(int var1, int var2, int var3, int var4);
}
