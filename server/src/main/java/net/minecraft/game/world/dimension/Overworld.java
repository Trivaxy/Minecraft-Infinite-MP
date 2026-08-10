package net.minecraft.game.world.dimension;

import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.potion.EffectList;
import net.minecraft.game.physics.Vector;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldType;
import net.minecraft.game.world.util.LightLayer;
import net.minecraft.util.math.MathHelper;

public class Overworld extends Dimension {
    @Override
    public boolean weatherEnabled() {
        return true;
    }

    @Override
    public boolean hasClouds() {
        return true;
    }

    @Override
    public Vector getSkyColor(Entity entity, float partialTicks, int option) {
        float sunAngle = this.world.getSunAngle(partialTicks);
        float angle = MathHelper.cos(sunAngle * (float) Math.PI * 2.0F) * 2.0F + 0.5F;
        angle = MathHelper.clamp(angle, 0.0F, 1.0F);
        long skyColor = this.world.getBaseSkyColor();
        int x = MathHelper.floor_double(entity.posX);
        int z = MathHelper.floor_double(entity.posZ);
        int red = 0;
        int green = 0;
        int blue = 0;

        for (int offX = -8; offX < 8; offX++) {
            for (int offZ = -8; offZ < 8; offZ++) {
                skyColor = this.world.getBiomeAt(x + offX, z + offZ).getSkyColor();
                if (this.world.settings.getType() == WorldType.CAVES) {
                    red = 0;
                    green = 0;
                    blue = 0;
                } else if (option == 1) {
                    skyColor = this.world.getBaseSkyColor();
                    red = (int)(red + ((skyColor & 16711680L) >> 16));
                    green = (int)(green + ((skyColor & 65280L) >> 8));
                    blue = (int)(blue + (skyColor & 255L));
                } else if (option == 2) {
                    red += 0;
                    green += 0;
                    blue += 255;
                } else {
                    red = (int)(red + ((skyColor & 16711680L) >> 16));
                    green = (int)(green + ((skyColor & 65280L) >> 8));
                    blue = (int)(blue + (skyColor & 255L));
                }
            }
        }

        float colRed = (red >> 8) / 256.0F;
        float colGreen = (green >> 8) / 256.0F;
        float colBlue = (blue >> 8) / 256.0F;
        if (this.world.bloodMoon) {
            colRed *= angle + colRed / 2.0F;
        } else {
            colRed *= angle;
        }

        colGreen *= angle;
        if (this.world.blueMoon) {
            colBlue *= angle + colBlue / 2.0F;
        } else {
            colBlue *= angle;
        }

        float rainStr = this.world.getRainStrength(sunAngle);
        if (rainStr > 0.0F) {
            float brightness = (colRed * 0.3F + colGreen * 0.59F + colBlue * 0.11F) * 0.6F;
            float opacity = 1.0F - rainStr * 0.75F;
            colRed = colRed * opacity + brightness * (1.0F - opacity);
            colGreen = colGreen * opacity + brightness * (1.0F - opacity);
            colBlue = colBlue * opacity + brightness * (1.0F - opacity);
        }

        float thunderStr = this.world.getThunderStrength(sunAngle);
        if (thunderStr > 0.0F) {
            float brightness = (colRed * 0.3F + colGreen * 0.59F + colBlue * 0.11F) * 0.2F;
            float opacity = 1.0F - thunderStr * 0.75F;
            colRed = colRed * opacity + brightness * (1.0F - opacity);
            colGreen = colGreen * opacity + brightness * (1.0F - opacity);
            colBlue = colBlue * opacity + brightness * (1.0F - opacity);
        }

        if (this.world.lightningStrikeTime > 0) {
            float flashStr = this.world.lightningStrikeTime - sunAngle;
            if (flashStr > 1.0F) {
                flashStr = 1.0F;
            }

            flashStr *= 0.45F;
            colRed = colRed * (1.0F - flashStr) + 0.8F * flashStr;
            colGreen = colGreen * (1.0F - flashStr) + 0.8F * flashStr;
            colBlue = colBlue * (1.0F - flashStr) + 1.0F * flashStr;
        }

        return Vector.createVector(colRed, colGreen, colBlue);
    }

    @Override
    public byte[] getLightmapColors(boolean torchFlicker, float torchFlickerX, float gamma, Player thePlayer, float tick, boolean produceSunsetColors) {
        byte[] lightmapColors = new byte[1024];
        World world = this.world;
        if (thePlayer.hasEffect(EffectList.nightVision)) {
            gamma = 5.0F;
        } else {
            gamma = MathHelper.clamp(gamma, 0.0F, 1.0F);
        }

        for (int i = 0; i < 256; i++) {
            float skyBrightness = this.world.getSunBrightness(1.0F) * 0.95F + 0.05F;
            float sky = this.lightValues[i / 16] * skyBrightness;
            if (this.world.badSun) {
                sky = this.lightValues[i / 16] * skyBrightness * (torchFlickerX * 0.2F + 1.0F);
            }

            float block = this.lightValues[i % 16] * 1.0F;
            if (torchFlicker) {
                block = this.lightValues[i % 16] * (torchFlickerX * 0.2F + 1.0F);
            }

            if (world.lightningStrikeTime > 0) {
                sky = this.lightValues[i / 16];
            }

            float[] sunsetColors = this.sunsetColors(this.world.getSunAngle(tick), tick, 1.0F);
            float brightness = 0.7F;
            float skyLight = world.getLayerBrightness(
                    LightLayer.Sky, MathHelper.floor_double(thePlayer.posX), MathHelper.floor_double(thePlayer.posY), MathHelper.floor_double(thePlayer.posZ)
                )
                / 15.0F;
            skyLight = MathHelper.clamp_float(skyLight, 0.0F, 1.0F);
            float skyR = sky * (world.getSunBrightness(1.0F) * 0.65F + 0.35F);
            float skyG = sky * (world.getSunBrightness(1.0F) * 0.65F + 0.35F);
            float skyB = sky * (world.getSunBrightness(1.0F) * 0.65F + 0.35F);
            if (sunsetColors != null && produceSunsetColors) {
                brightness *= sunsetColors[3];
                skyR = skyR * 1.0F + sunsetColors[0] * brightness * skyLight;
                skyG = skyG * 1.0F + sunsetColors[1] * brightness * skyLight;
                skyB = skyB * 1.0F + sunsetColors[2] * brightness * skyLight;
            }

            if (this.world.badSun) {
                skyB /= 2.0F;
            }

            float blockR = block * ((block * 0.6F + 0.4F) * 0.6F + 0.4F);
            float blockG = block * ((block * 0.6F + 0.4F) * 0.6F + 0.4F);
            float blockB = block * ((block * 0.6F + 0.4F) * 0.6F + 0.4F);
            float colR = skyR + blockR;
            float colG = skyG + blockG;
            float colB = skyB + blockB;
            colR = Math.min(colR * 0.96F + 0.03F, 1.0F);
            colG = Math.min(colG * 0.96F + 0.03F, 1.0F);
            colB = Math.min(colB * 0.96F + 0.03F, 1.0F);
            float colRt = 1.0F - colR;
            colRt = 1.0F - colRt * colRt * colRt * colRt;
            colR = colR * (1.0F - gamma) + colRt * gamma;
            colR = Math.min(Math.max(colR * 0.96F + 0.03F, 0.0F), 1.0F);
            float colGt = 1.0F - colG;
            colGt = 1.0F - colGt * colGt * colGt * colGt;
            colG = colG * (1.0F - gamma) + colGt * gamma;
            colG = Math.min(Math.max(colG * 0.96F + 0.03F, 0.0F), 1.0F);
            float colBt = 1.0F - colB;
            colBt = 1.0F - colBt * colBt * colBt * colBt;
            colB = colB * (1.0F - gamma) + colBt * gamma;
            colB = Math.min(Math.max(colB * 0.96F + 0.03F, 0.0F), 1.0F);
            if (this.world.bloodMoon) {
                colB /= 2.0F;
                colG /= 2.0F;
            }

            if (this.world.blueMoon) {
                colR /= 2.0F;
                colG /= 2.0F;
            }

            short colAi = 255;
            int colRi = (int)(colR * 255.0F);
            int colGi = (int)(colG * 255.0F);
            int colBi = (int)(colB * 255.0F);
            lightmapColors[i * 4 + 0] = (byte)colRi;
            lightmapColors[i * 4 + 1] = (byte)colGi;
            lightmapColors[i * 4 + 2] = (byte)colBi;
            lightmapColors[i * 4 + 3] = (byte)colAi;
        }

        return lightmapColors;
    }
}
