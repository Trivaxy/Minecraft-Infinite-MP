package net.minecraft.game.world.biomes.temperate;

import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.BiomeList;

public class DefaultBiome extends Biome {
    public DefaultBiome(int biomeID) {
        super(biomeID);
        this.variants.add(BiomeList.orchard);
        this.variants.add(BiomeList.shroomland);
    }
}
