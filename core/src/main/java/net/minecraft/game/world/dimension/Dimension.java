package net.minecraft.game.world.dimension;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.potion.EffectList;
import net.minecraft.game.physics.Vector;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldInfo;
import net.minecraft.game.world.WorldType;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.game.world.biomes.providers.BiomeProvider;
import net.minecraft.game.world.biomes.providers.SingleProvider;
import net.minecraft.game.world.chunk.ChunkLoader;
import net.minecraft.game.world.generation.CavesGenerator;
import net.minecraft.game.world.generation.ClassicGenerator;
import net.minecraft.game.world.generation.FlatGenerator;
import net.minecraft.game.world.generation.FloatingGenerator;
import net.minecraft.game.world.generation.OverworldGenerator;
import net.minecraft.game.world.util.Position;
import net.minecraft.util.math.MathHelper;

public abstract class Dimension {
    public World world;
    public BiomeProvider biomeProvider;
    public Biome singleBiome = BiomeList.biomeDefault;
    public WorldType type = WorldType.DEFAULT;
    public float[] lightValues = new float[16];
    public int dimension = 0;
    float colMapRed = 1.0F;
    float colMapGreen = 1.0F;
    float colMapBlue = 1.0F;

    public void init(World world) {
        this.world = world;
        this.type = world.getSettings().getType();
        this.singleBiome = world.getSettings().getSingleBiome();
        this.registerBiomeProvider();
        this.generateLightTable();
    }

    protected void generateLightTable() {
        float offset = 0.05F;

        for (int level = 0; level <= 15; level++) {
            float brightness = 1.0F - level / 15.0F;
            this.lightValues[level] = (1.0F - brightness) / (brightness * 3.0F + 1.0F) * (1.0F - offset) + offset;
        }
    }

    protected void registerBiomeProvider() {
        switch (this.type) {
            case DEFAULT:
                this.biomeProvider = new BiomeProvider(this.world);
                break;
            case AMPLIFIED:
                this.biomeProvider = new BiomeProvider(this.world);
                break;
            case OCEAN:
                this.biomeProvider = new BiomeProvider(this.world.getSeed(), WorldType.OCEAN);
                break;
            case BIOME:
                this.biomeProvider = new SingleProvider(this.singleBiome);
                break;
            case FLAT:
                this.biomeProvider = new SingleProvider(BiomeList.valleyHills);
                break;
            default:
                this.biomeProvider = new BiomeProvider(this.world.getSeed(), WorldType.INLAND);
        }
    }

    public boolean suitableSpawnLocation(int x, int z) {
        int id = this.world.getTopTile(x, z);
        return id == 0 ? false : BlockList.blocks[id].material.isSolid();
    }

    public Position findSpawnPoint() {
        List<Biome> spawnBiomes = this.biomeProvider.getSpawnBiomes();
        Random random = new Random(this.world.getSeed());
        Position biomepos = this.biomeProvider.findSpawnBiome(0, 0, 256, spawnBiomes, random);
        if (this.type == WorldType.OCEAN) {
            biomepos = this.biomeProvider.findSpawnBiome(0, 0, 4096, spawnBiomes, random);
        }

        int xpos = 0;
        int zpos = 0;
        int spawnAttempts = 0;
        if (biomepos != null && this.world.settings.border == 0) {
            xpos = biomepos.x;
            zpos = biomepos.z;
        } else {
            Logger.getLogger("Minecraft").warning("Unable to find spawn biome");
        }

        while (this.type != WorldType.FLAT && !this.suitableSpawnLocation(xpos, zpos) && spawnAttempts < 1000 && this.world.settings.border == 0) {
            xpos += random.nextInt(64) - random.nextInt(64);
            zpos += random.nextInt(64) - random.nextInt(64);
            spawnAttempts++;
        }

        int ypos = this.world.getHeightmap(xpos, zpos);
        if (this.type == WorldType.FLAT) {
            ypos = this.world.getSettings().topLayer;
        } else if (this.type == WorldType.CAVES) {
            ypos = this.world.getRandomHeight(xpos, zpos);
        }

        return new Position(xpos, ypos, zpos);
    }

    public Position findRespawnLocation() {
        WorldInfo info = this.world.getSettings();
        Random rand = new Random(this.world.getSeed());
        int y = info.getSpawnY();
        if (y <= 0) {
            y = this.world.getSeaLevel();
        }

        int x = info.getSpawnX();
        int i = 0;

        int z;
        for (z = info.getSpawnZ(); this.world.getTopTile(x, z) == 0; z += rand.nextInt(8) - rand.nextInt(8)) {
            x += rand.nextInt(8) - rand.nextInt(8);
            if (++i == 10000) {
                break;
            }
        }

        return new Position(x, y, z);
    }

    public ChunkLoader getChunkProvider() {
        switch (this.type) {
            case FLAT:
                return new FlatGenerator(this.world);
            case OLD:
                return new ClassicGenerator(this.world, this.world.getSeed());
            case CAVES:
                return new CavesGenerator(this.world, this.world.getSeed());
            case FLOATING:
                return new FloatingGenerator(this.world, this.world.getSeed());
            default:
                return new OverworldGenerator(this.world, this.world.getSeed());
        }
    }

    public static Dimension getDimension(int dimension) {
        switch (dimension) {
            case 1:
                return new HellDimension();
            case 2:
                return new SkyDimension();
            case 3:
                return new VoidDimension();
            case 4:
                return new OceanDimension();
            default:
                return new Overworld();
        }
    }

    public int getMoonPhase(long time) {
        return (int)(time / 24000L) % 8;
    }

    public float getSunAngle(long worldTime, float dayLength, float partialTicks) {
        float length;
        if (this.world.getPermaday()) {
            length = (int)(this.world.settings.dayLength / 4.0F % dayLength);
        } else {
            length = (int)((float)worldTime % dayLength);
        }

        float angle = (length + partialTicks) / dayLength - 0.25F;
        if (angle < 0.0F) {
            angle++;
        }

        if (angle > 1.0F) {
            angle--;
        }

        float prevAngle = angle;
        angle = 1.0F - (float)((Math.cos(angle * Math.PI) + 1.0) / 2.0);
        return prevAngle + (angle - prevAngle) / 3.0F;
    }

    public float[] sunsetColors(float sunAngle, float tick, float opacity) {
        float[] colorsSunriseSunset = new float[4];
        float offset = 0.4F;
        float brightness = MathHelper.cos(sunAngle * (float) Math.PI * 2.0F) - 0.0F;
        if (brightness >= -offset && brightness <= offset) {
            float col = brightness / offset * 0.5F + 0.5F;
            float op = 1.0F - (1.0F - MathHelper.sin(col * (float) Math.PI)) * 0.99F;
            op *= op;
            colorsSunriseSunset[0] = col * 0.3F + 0.7F;
            colorsSunriseSunset[1] = col * col * 0.7F + 0.2F;
            colorsSunriseSunset[2] = col * col * 0.0F + 0.2F;
            colorsSunriseSunset[3] = op * opacity;
            return colorsSunriseSunset;
        } else {
            return null;
        }
    }

    public Vector getFogColor(float partialTicks) {
        float sunAngle = this.world.getSunAngle(partialTicks);
        float angle = MathHelper.cos(sunAngle * (float) Math.PI * 2.0F) * 2.0F + 0.5F;
        angle = MathHelper.clamp(angle, 0.0F, 1.0F);
        long fogColor = this.world.getBaseFogColor();
        float red = (float)(fogColor >> 16 & 255L) / 255.0F;
        float green = (float)(fogColor >> 8 & 255L) / 255.0F;
        float blue = (float)(fogColor & 255L) / 255.0F;
        red *= angle * 0.94F + 0.06F;
        green *= angle * 0.94F + 0.06F;
        blue *= angle * 0.91F + 0.09F;
        return Vector.createVector(red, green, blue);
    }

    public Vector getSkyColor(Entity en, float tick, int option) {
        int posX = MathHelper.floor_double(en.posX);
        int posZ = MathHelper.floor_double(en.posZ);
        int red = 0;
        int green = 0;
        int blue = 0;

        for (int z = -8; z < 8; z++) {
            for (int x = -8; x < 8; x++) {
                int col = this.world.getBiomeAt(posX + x, posZ + z).getSkyColor();
                if (option >= 1) {
                    col = this.singleBiome.getSkyColor();
                }

                red += (col & 0xFF0000) >> 16;
                green += (col & 0xFF00) >> 8;
                blue += col & 0xFF;
            }
        }

        float colRed = (red >> 8) / 256.0F;
        float colGreen = (green >> 8) / 256.0F;
        float colBlue = (blue >> 8) / 256.0F;
        return Vector.createVector(colRed, colGreen, colBlue);
    }

    public float getStarBrightness(float partialTicks) {
        float sunAngle = this.world.getSunAngle(partialTicks);
        float opacity = 1.0F - (MathHelper.cos(sunAngle * (float) Math.PI * 2.0F) * 2.0F + 0.75F);
        opacity = MathHelper.clamp(opacity, 0.0F, 1.0F);
        return opacity * opacity * 0.75F;
    }

    public boolean canRespawnHere() {
        return true;
    }

    public int getSkyBrightness(float partialTicks) {
        float sunAngle = this.world.getSunAngle(partialTicks);
        float brightness = 1.0F - (MathHelper.cos(sunAngle * (float) Math.PI * 2.0F) * 2.0F + 0.5F);
        brightness = MathHelper.clamp(brightness, 0.0F, 1.0F);
        brightness = 1.0F - brightness;
        brightness = (float)(brightness * (1.0 - this.world.getRainStrength(partialTicks) * 5.0F / 16.0));
        brightness = (float)(brightness * (1.0 - this.world.getThunderStrength(partialTicks) * 5.0F / 16.0));
        brightness = 1.0F - brightness;
        return (int)(brightness * 11.0F);
    }

    public float getSunBrightness(float partialTicks) {
        float sunAngle = this.world.getSunAngle(partialTicks);
        float brightness = 1.0F - (MathHelper.cos(sunAngle * (float) Math.PI * 2.0F) * 2.0F + 0.5F);
        brightness = MathHelper.clamp(brightness, 0.0F, 1.0F);
        brightness = 1.0F - brightness;
        brightness = (float)(brightness * (1.0 - this.world.getRainStrength(partialTicks) * 5.0F / 16.0));
        brightness = (float)(brightness * (1.0 - this.world.getThunderStrength(partialTicks) * 5.0F / 16.0));
        return brightness * 0.8F + 0.2F;
    }

    public String getSaveFolder() {
        return "";
    }

    public byte[] getLightmapColors(boolean torchFlicker, float torchFlickerX, float gamma, Player thePlayer, float tick, boolean produceSunsetColors) {
        byte[] lightmapColors = new byte[1024];
        World world = this.world;

        for (int col = 0; col < 256; col++) {
            float sunBrightness = this.world.getSunBrightness(1.0F) * 0.95F + 0.05F;
            float sky = this.lightValues[col / 16] * sunBrightness;
            float block = this.lightValues[col % 16] * 1.0F;
            if (torchFlicker) {
                block = this.lightValues[col % 16] * (torchFlickerX * 0.2F + 1.0F);
            }

            if (world.lightningStrikeTime > 0) {
                sky = this.lightValues[col / 16];
            }

            float skynew = sky * (world.getSunBrightness(1.0F) * 0.65F + 0.35F);
            float blocknew = block * ((block * 0.6F + 0.4F) * 0.6F + 0.4F);
            float brightnessTotal = skynew + blocknew;
            brightnessTotal = brightnessTotal * 0.96F + 0.03F;
            if (brightnessTotal > 1.0F) {
                brightnessTotal = 1.0F;
            }

            if (thePlayer.hasEffect(EffectList.nightVision)) {
                gamma = 5.0F;
            } else {
                gamma = MathHelper.clamp(gamma, 0.0F, 1.0F);
            }

            float opacity = 1.0F - brightnessTotal;
            opacity = 1.0F - opacity * opacity * opacity * opacity;
            brightnessTotal = brightnessTotal * (1.0F - gamma) + opacity * gamma;
            brightnessTotal = brightnessTotal * 0.96F + 0.03F;
            brightnessTotal = MathHelper.clamp(brightnessTotal, 0.0F, 1.0F);
            short op = 255;
            int red = (int)(brightnessTotal / this.colMapRed * 255.0F);
            int green = (int)(brightnessTotal / this.colMapGreen * 255.0F);
            int blue = (int)(brightnessTotal / this.colMapBlue * 255.0F);
            lightmapColors[col * 4 + 0] = (byte)red;
            lightmapColors[col * 4 + 1] = (byte)green;
            lightmapColors[col * 4 + 2] = (byte)blue;
            lightmapColors[col * 4 + 3] = (byte)op;
        }

        return lightmapColors;
    }

    public float getCloudHeight() {
        return this.world.getWorldHeight() < 256 ? this.world.getWorldHeight() : this.world.getWorldHeight() - this.world.getWorldHeight() / 3.5F;
    }

    public abstract boolean weatherEnabled();

    public abstract boolean hasClouds();
}
