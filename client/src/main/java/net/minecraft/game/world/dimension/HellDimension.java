package net.minecraft.game.world.dimension;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.physics.Vector;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.game.world.biomes.providers.HellProvider;
import net.minecraft.game.world.biomes.providers.SingleProvider;
import net.minecraft.game.world.chunk.ChunkLoader;
import net.minecraft.game.world.generation.HellGenerator;

public class HellDimension extends Dimension {
    @Override
    public void init(World world) {
        super.init(world);
        this.singleBiome = BiomeList.hell;
        this.colMapBlue = 1.5F;
        this.colMapGreen = 1.5F;
        this.dimension = 1;
    }

    @Override
    protected void registerBiomeProvider() {
        switch (this.type) {
            case BIOME:
                this.biomeProvider = new SingleProvider(BiomeList.hell);
                break;
            default:
                this.biomeProvider = new HellProvider(this.world.getSeed());
        }
    }

    @Override
    public Vector getFogColor(float col) {
        return Vector.createVector(0.2, 0.03, 0.03);
    }

    @Override
    protected void generateLightTable() {
        float offset = 0.1F;

        for (int level = 0; level <= 15; level++) {
            float brightness = 1.0F - level / 15.0F;
            this.lightValues[level] = (1.0F - brightness) / (brightness * 3.0F + 1.0F) * (1.0F - offset) + offset;
        }
    }

    @Override
    public ChunkLoader getChunkProvider() {
        return new HellGenerator(this.world, this.world.getSeed());
    }

    @Override
    public boolean suitableSpawnLocation(int x, int z) {
        int id = this.world.getTopTile(x, z);
        return id != BlockList.bedrock.id && id != 0 ? Block.isOpaque.get(id) : false;
    }

    @Override
    public float getSunAngle(long worldTime, float dayLength, float partialTicks) {
        return 0.5F;
    }

    @Override
    public float[] sunsetColors(float var1, float var2, float opacity) {
        return null;
    }

    @Override
    public boolean canRespawnHere() {
        return false;
    }

    @Override
    public float getStarBrightness(float partialTicks) {
        return 0.0F;
    }

    @Override
    public int getMoonPhase(long var1) {
        return 0;
    }

    @Override
    public String getSaveFolder() {
        return "DIM1";
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
