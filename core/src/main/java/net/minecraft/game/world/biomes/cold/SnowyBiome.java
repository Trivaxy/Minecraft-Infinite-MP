package net.minecraft.game.world.biomes.cold;

import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.BiomeList;

public class SnowyBiome extends Biome {
    public SnowyBiome(int biomeID) {
        super(biomeID);
        this.variants.add(BiomeList.snowyWoodland);
    }
}
