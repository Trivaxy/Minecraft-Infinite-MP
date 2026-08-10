package net.minecraft.client.render.environment;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.options.OptionValues;
import net.minecraft.client.particle.environment.RaindropParticle;
import net.minecraft.client.particle.environment.SmokeParticle;
import net.minecraft.client.render.Tessellator;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.game.world.biomes.hot.Mesa;
import net.minecraft.game.world.biomes.hot.Outback;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class WeatherRenderer {
    private Minecraft mc;
    private Random random = new Random();
    float[] rainWidthValue;
    float[] rainHeightValue;
    public int rainTexID;
    private int rainSoundCounter = 0;

    public WeatherRenderer(Minecraft mc) {
        this.mc = mc;
    }

    public void renderWeather(float deltaTick) {
        if (this.mc.world.currDim.weatherEnabled()) {
            float rainStr = this.mc.world.getRainStrength(deltaTick);
            if (rainStr > 0.0F) {
                this.setup();
                if (this.rainWidthValue == null) {
                    this.rainWidthValue = new float[1024];
                    this.rainHeightValue = new float[1024];

                    for (int width = 0; width < 32; width++) {
                        for (int height = 0; height < 32; height++) {
                            float x = height - 16;
                            float z = width - 16;
                            float divider = MathHelper.sqrt_float(x * x + z * z);
                            this.rainWidthValue[width << 5 | height] = -z / divider;
                            this.rainHeightValue[width << 5 | height] = x / divider;
                        }
                    }
                }

                Mob player = this.mc.gameRenderer.cameraMob;
                World world = this.mc.world;
                int x = MathHelper.floor_double(player.posX);
                int y = MathHelper.floor_double(player.posY);
                int z = MathHelper.floor_double(player.posZ);
                GL11.glDisable(2884);
                GL11.glNormal3f(0.0F, 1.0F, 0.0F);
                GL11.glEnable(3042);
                GL11.glBlendFunc(770, 771);
                GL11.glAlphaFunc(516, 0.01F);
                double cameraX = player.lastTickPosX + (player.posX - player.lastTickPosX) * deltaTick;
                double cameraY = player.lastTickPosY + (player.posY - player.lastTickPosY) * deltaTick;
                double cameraZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * deltaTick;
                int height = MathHelper.floor_double(cameraY);
                byte range = 5;
                if (OptionValues.fancyGraphics) {
                    range = (byte)(range * 2);
                }

                float counter = this.mc.gameRenderer.rendererUpdateCount + deltaTick;
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

                for (int finalX = x - range; finalX <= x + range; finalX++) {
                    for (int finalZ = z - range; finalZ <= z + range; finalZ++) {
                        int index = (finalZ - z + 16) * 32 + finalX - x + 16;
                        float wdthVal = this.rainWidthValue[index] * 0.5F;
                        float hghtVal = this.rainHeightValue[index] * 0.5F;
                        Biome biome = world.getBiomeAt(finalX, finalZ);
                        if (!(biome instanceof Mesa) && !(biome instanceof Outback)) {
                            int highestTile = world.isSpaceObstructed(finalX, finalZ);
                            int minY = y - range;
                            int maxY = y + range;
                            if (minY < highestTile) {
                                minY = highestTile;
                            }

                            if (maxY < highestTile) {
                                maxY = highestTile;
                            }

                            int finalHeight = highestTile;
                            if (highestTile < height) {
                                finalHeight = height;
                            }

                            if (minY != maxY) {
                                this.random.setSeed(finalX * finalX * 3121 + finalX * 45238971 ^ finalZ * finalZ * 418711 + finalZ * 13761);
                                if (biome.canThunder() && !world.canSnowAt(finalX, minY, finalZ, true)) {
                                    this.renderRain(
                                        world,
                                        player,
                                        finalX,
                                        finalZ,
                                        range,
                                        finalHeight,
                                        rainStr,
                                        cameraX,
                                        cameraY,
                                        cameraZ,
                                        wdthVal,
                                        hghtVal,
                                        minY,
                                        maxY,
                                        deltaTick
                                    );
                                } else {
                                    this.renderSnow(
                                        world,
                                        player,
                                        biome,
                                        finalX,
                                        finalZ,
                                        range,
                                        finalHeight,
                                        rainStr,
                                        cameraX,
                                        cameraY,
                                        cameraZ,
                                        wdthVal,
                                        hghtVal,
                                        minY,
                                        maxY,
                                        counter,
                                        deltaTick
                                    );
                                }
                            }
                        }
                    }
                }

                GL11.glEnable(2884);
                GL11.glDisable(3042);
                GL11.glAlphaFunc(516, 0.1F);
                GL13.glClientActiveTexture(33985);
                GL13.glActiveTexture(33985);
                GL11.glDisable(3553);
                GL13.glClientActiveTexture(33984);
                GL13.glActiveTexture(33984);
            }
        }
    }

    private void renderRain(
        World world,
        Mob player,
        int finalX,
        int finalZ,
        int range,
        int finalHeight,
        float rainStr,
        double cameraX,
        double cameraY,
        double cameraZ,
        float wdthVal,
        float hghtVal,
        int minY,
        int maxY,
        float deltaTick
    ) {
        Tessellator tess = Tessellator.instance;
        GL11.glBindTexture(3553, this.mc.textureLoader.getTexture("/environment/rain.png"));
        GL11.glDepthMask(false);
        float updateCount = (
                (this.mc.gameRenderer.rendererUpdateCount + finalX * finalX * 3121 + finalX * 45238971 + finalZ * finalZ * 418711 + finalZ * 13761 & 31)
                    + deltaTick
            )
            / 32.0F
            * (3.0F + this.random.nextFloat());
        double xOffset = finalX + 0.5F - player.posX;
        double zOffset = finalZ + 0.5F - player.posZ;
        float alpha = MathHelper.sqrt_double(xOffset * xOffset + zOffset * zOffset) / range;
        float color = world.getBrightness(finalX, finalHeight, finalZ);
        tess.begin();
        tess.setBrightness(world.getBrightness(finalX, finalHeight, finalZ, 0));
        tess.setColorAlpha(color, color, color, ((1.0F - alpha * alpha) * 0.5F + 0.5F) * rainStr);
        tess.setTranslation(-cameraX * 1.0, -cameraY * 1.0, -cameraZ * 1.0);
        tess.addTexturedVertex(finalX - wdthVal + 0.5, minY, finalZ - hghtVal + 0.5, 0.0F, minY / 4.0F + updateCount);
        tess.addTexturedVertex(finalX + wdthVal + 0.5, minY, finalZ + hghtVal + 0.5, 1.0F, minY / 4.0F + updateCount);
        tess.addTexturedVertex(finalX + wdthVal + 0.5, maxY, finalZ + hghtVal + 0.5, 1.0F, maxY / 4.0F + updateCount);
        tess.addTexturedVertex(finalX - wdthVal + 0.5, maxY, finalZ - hghtVal + 0.5, 0.0F, maxY / 4.0F + updateCount);
        tess.setTranslation(0.0, 0.0, 0.0);
        tess.draw();
        GL11.glDepthMask(true);
    }

    private void renderSnow(
        World world,
        Mob player,
        Biome biome,
        int finalX,
        int finalZ,
        int range,
        int finalHeight,
        float rainStr,
        double cameraX,
        double cameraY,
        double cameraZ,
        float wdthVal,
        float hghtVal,
        int minY,
        int maxY,
        float counter,
        float deltaTick
    ) {
        Tessellator tess = Tessellator.instance;
        if (biome.hasNoRain()) {
            if (biome == BiomeList.volcano) {
                GL11.glBindTexture(3553, this.mc.textureLoader.getTexture("/environment/ashfall.png"));
            } else {
                GL11.glBindTexture(3553, this.mc.textureLoader.getTexture("/environment/sandstorm.png"));
            }
        } else {
            GL11.glBindTexture(3553, this.mc.textureLoader.getTexture("/environment/snow.png"));
        }

        float updateSpdVertical = 0.01F;
        float updateSpdHorizontal = 0.001F;
        if (biome.hasNoRain()) {
            updateSpdVertical = 0.1F;
            updateSpdHorizontal = 0.01F;
        }

        float updateCount = ((this.mc.gameRenderer.rendererUpdateCount & 511) + deltaTick) / 512.0F;
        float swayX = this.random.nextFloat() + counter * updateSpdVertical * (float)this.random.nextGaussian();
        float swayZ = this.random.nextFloat() + counter * (float)this.random.nextGaussian() * updateSpdHorizontal;
        double zOffset = finalX + 0.5F - player.posX;
        double xOffset = finalZ + 0.5F - player.posZ;
        float alpha = MathHelper.sqrt_double(zOffset * zOffset + xOffset * xOffset) / range;
        float color = world.getBrightness(finalX, finalHeight, finalZ);
        tess.begin();
        tess.setBrightness((world.getBrightness(finalX, finalHeight, finalZ, 0) * 3 + 15728880) / 4);
        tess.setColorAlpha(color, color, color, ((1.0F - alpha * alpha) * 0.3F + 0.5F) * rainStr);
        tess.setTranslation(-cameraX * 1.0, -cameraY * 1.0, -cameraZ * 1.0);
        tess.addTexturedVertex(finalX - wdthVal + 0.5, minY, finalZ - hghtVal + 0.5, 0.0F + swayX, minY / 4.0F + updateCount + swayZ);
        tess.addTexturedVertex(finalX + wdthVal + 0.5, minY, finalZ + hghtVal + 0.5, 1.0F + swayX, minY / 4.0F + updateCount + swayZ);
        tess.addTexturedVertex(finalX + wdthVal + 0.5, maxY, finalZ + hghtVal + 0.5, 1.0F + swayX, maxY / 4.0F + updateCount + swayZ);
        tess.addTexturedVertex(finalX - wdthVal + 0.5, maxY, finalZ - hghtVal + 0.5, 0.0F + swayX, maxY / 4.0F + updateCount + swayZ);
        tess.setTranslation(0.0, 0.0, 0.0);
        tess.draw();
    }

    public void addRainParticles() {
        float rainStr = this.mc.world.getRainStrength(1.0F);
        if (!OptionValues.fancyGraphics) {
            rainStr /= 2.0F;
        }

        if (rainStr != 0.0F) {
            this.random.setSeed(this.mc.gameRenderer.rendererUpdateCount * 312987231L);
            Mob player = this.mc.gameRenderer.cameraMob;
            World world = this.mc.world;
            int x = MathHelper.floor_double(player.posX);
            int y = MathHelper.floor_double(player.posY);
            int z = MathHelper.floor_double(player.posZ);
            byte amount = 10;
            double particleX = 0.0;
            double particleY = 0.0;
            double particleZ = 0.0;
            int count = 0;

            for (int i = 0; i < (int)(100.0F * rainStr * rainStr); i++) {
                int xOffset = x + this.random.nextInt(amount) - this.random.nextInt(amount);
                int zOffset = z + this.random.nextInt(amount) - this.random.nextInt(amount);
                int ground = world.isSpaceObstructed(xOffset, zOffset);
                int highestTile = world.getBlockId(xOffset, ground - 1, zOffset);
                if (ground <= y + amount && ground >= y - amount && world.getBiomeAt(xOffset, zOffset).canThunder() && !world.canSnowAt(x, y, z, true)) {
                    float randomX = this.random.nextFloat();
                    float randomZ = this.random.nextFloat();
                    if (highestTile > 0) {
                        if (BlockList.blocks[highestTile].material == MaterialList.lava) {
                            this.mc
                                .particleRenderer
                                .addParticle(
                                    new SmokeParticle(world, xOffset + randomX, ground + 0.1F - BlockList.blocks[highestTile].minY, zOffset + randomZ, 1.0F)
                                );
                        } else {
                            if (this.random.nextInt(++count) == 0) {
                                particleX = xOffset + randomX;
                                particleY = ground + 0.1F - BlockList.blocks[highestTile].minY;
                                particleZ = zOffset + randomZ;
                            }

                            this.mc
                                .particleRenderer
                                .addParticle(
                                    new RaindropParticle(world, xOffset + randomX, ground + 0.1F - BlockList.blocks[highestTile].minY, zOffset + randomZ)
                                );
                        }
                    }
                }
            }

            if (count > 0 && this.random.nextInt(3) < this.rainSoundCounter++) {
                this.rainSoundCounter = 0;
                if (particleY > player.posY + 1.0
                    && world.isSpaceObstructed(MathHelper.floor_double(player.posX), MathHelper.floor_double(player.posZ))
                        > MathHelper.floor_double(player.posY)) {
                    this.mc
                        .world
                        .playSoundEffect(particleX, particleY, particleZ, "loops.waterfall", 0.01F * (OptionValues.ambienceVol * OptionValues.masterVol), 0.5F);
                } else {
                    this.mc
                        .world
                        .playSoundEffect(particleX, particleY, particleZ, "loops.waterfall", 0.05F * (OptionValues.ambienceVol * OptionValues.masterVol), 1.0F);
                }
            }
        }
    }

    public void setup() {
        GL13.glClientActiveTexture(33985);
        GL13.glActiveTexture(33985);
        GL11.glMatrixMode(5890);
        GL11.glLoadIdentity();
        float scale = 0.00390625F;
        GL11.glScalef(scale, scale, scale);
        GL11.glTranslatef(8.0F, 8.0F, 8.0F);
        GL11.glMatrixMode(5888);
        this.mc.textureLoader.bindTexture(this.rainTexID);
        GL11.glTexParameteri(3553, 10241, 9729);
        GL11.glTexParameteri(3553, 10240, 9729);
        GL11.glTexParameteri(3553, 10241, 9729);
        GL11.glTexParameteri(3553, 10240, 9729);
        GL11.glTexParameteri(3553, 10242, 10496);
        GL11.glTexParameteri(3553, 10243, 10496);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(3553);
        GL13.glClientActiveTexture(33984);
        GL13.glActiveTexture(33984);
    }
}
