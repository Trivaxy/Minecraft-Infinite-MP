package net.minecraft.game.world.dimension;

import net.minecraft.game.physics.Vector;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.game.world.biomes.providers.SingleProvider;
import net.minecraft.game.world.biomes.providers.SkyProvider;
import net.minecraft.game.world.chunk.ChunkLoader;
import net.minecraft.game.world.generation.SkyGenerator;

public class SkyDimension extends Dimension {
    @Override
    public void init(World world) {
        super.init(world);
        this.singleBiome = BiomeList.sky;
        this.colMapRed = 1.5F;
        this.colMapGreen = 1.5F;
        this.dimension = 2;
    }

    @Override
    protected void registerBiomeProvider() {
        switch (this.type) {
            case BIOME:
                this.biomeProvider = new SingleProvider(BiomeList.sky);
                break;
            default:
                this.biomeProvider = new SkyProvider(this.world.getSeed());
        }
    }

    @Override
    public ChunkLoader getChunkProvider() {
        return new SkyGenerator(this.world, this.world.getSeed());
    }

    @Override
    public float getSunAngle(long worldTime, float dayLength, float partialTicks) {
        return 1.0F;
    }

    @Override
    public float getStarBrightness(float partialTicks) {
        return 0.3F;
    }

    @Override
    public float[] sunsetColors(float var1, float var2, float opacity) {
        return null;
    }

    @Override
    public Vector getFogColor(float col) {
        return Vector.createVector(0.8, 0.8, 1.0);
    }

    @Override
    public float getCloudHeight() {
        return 8.0F;
    }

    @Override
    public boolean canRespawnHere() {
        return false;
    }

    @Override
    public String getSaveFolder() {
        return "DIM2";
    }

    @Override
    public boolean weatherEnabled() {
        return false;
    }

    @Override
    public boolean hasClouds() {
        return true;
    }
}
