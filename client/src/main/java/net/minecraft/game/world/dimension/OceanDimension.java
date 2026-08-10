package net.minecraft.game.world.dimension;

import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldType;
import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.game.world.biomes.providers.BiomeProvider;

public class OceanDimension extends Dimension {
    @Override
    public void init(World world) {
        super.init(world);
        this.singleBiome = BiomeList.ocean;
        this.colMapBlue = 1.5F;
        this.colMapRed = 1.5F;
        this.dimension = 4;
    }

    @Override
    public void registerBiomeProvider() {
        this.biomeProvider = new BiomeProvider(this.world.getSeed(), WorldType.OCEAN);
    }

    @Override
    public boolean canRespawnHere() {
        return false;
    }

    @Override
    public boolean weatherEnabled() {
        return true;
    }

    @Override
    public boolean hasClouds() {
        return true;
    }

    @Override
    public String getSaveFolder() {
        return "DIM4";
    }
}
