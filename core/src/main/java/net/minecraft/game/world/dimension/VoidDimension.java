package net.minecraft.game.world.dimension;

import net.minecraft.game.entity.Entity;
import net.minecraft.game.physics.Vector;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.game.world.biomes.providers.SingleProvider;
import net.minecraft.game.world.chunk.ChunkLoader;
import net.minecraft.game.world.generation.VoidGenerator;

public class VoidDimension extends Dimension {
    @Override
    public void init(World world) {
        super.init(world);
        this.singleBiome = BiomeList.biomeVoid;
        this.colMapBlue = 1.5F;
        this.colMapGreen = 1.5F;
        this.colMapRed = 1.5F;
        this.dimension = 3;
    }

    @Override
    public void registerBiomeProvider() {
        this.biomeProvider = new SingleProvider(BiomeList.biomeVoid);
    }

    @Override
    public ChunkLoader getChunkProvider() {
        return new VoidGenerator(this.world, this.world.getSeed());
    }

    @Override
    public float getSunAngle(long worldTime, float dayLength, float partialTicks) {
        return 0.5F;
    }

    @Override
    public float getStarBrightness(float partialTicks) {
        return 0.0F;
    }

    @Override
    public float[] sunsetColors(float var1, float var2, float opacity) {
        return null;
    }

    @Override
    public Vector getSkyColor(Entity en, float var2, int sky) {
        return Vector.createVector(0.0, 0.0, 0.0);
    }

    @Override
    public Vector getFogColor(float partialTicks) {
        return Vector.createVector(0.0, 0.0, 0.0);
    }

    @Override
    public float getCloudHeight() {
        return 0.0F;
    }

    @Override
    public String getSaveFolder() {
        return "DIM3";
    }

    @Override
    public boolean canRespawnHere() {
        return false;
    }

    @Override
    public boolean weatherEnabled() {
        return false;
    }

    @Override
    public boolean hasClouds() {
        return false;
    }
}
