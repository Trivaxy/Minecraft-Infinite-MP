package net.minecraft.game.world.biomes.providers;

import net.minecraft.game.world.generation.layers.GenLayer;

public class HellProvider extends BiomeProvider {
    public HellProvider(long seed) {
        GenLayer[] initGenLayers = GenLayer.populateHellBiomes(seed);
        this.genLayer = initGenLayers[0];
        this.biomeLayer = initGenLayers[1];
    }
}
