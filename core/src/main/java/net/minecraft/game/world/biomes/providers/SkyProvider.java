package net.minecraft.game.world.biomes.providers;

import net.minecraft.game.world.generation.layers.GenLayer;

public class SkyProvider extends BiomeProvider {
    public SkyProvider(long seed) {
        GenLayer[] initGenLayers = GenLayer.populateSkyBiomes(seed);
        this.genLayer = initGenLayers[0];
        this.biomeLayer = initGenLayers[1];
    }
}
