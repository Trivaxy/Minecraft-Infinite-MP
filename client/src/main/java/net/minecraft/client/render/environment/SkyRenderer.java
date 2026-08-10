package net.minecraft.client.render.environment;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.game.PhotoMode;
import net.minecraft.client.options.OptionValues;
import net.minecraft.client.render.Renderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.texture.TextureLoader;
import net.minecraft.game.physics.Vector;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldType;
import net.minecraft.game.world.dimension.HellDimension;
import net.minecraft.game.world.dimension.SkyDimension;
import net.minecraft.game.world.dimension.VoidDimension;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class SkyRenderer {
    private Minecraft game;
    private World world;
    private TextureLoader texLoader;
    private FloatBuffer cloudBuffer = BufferUtils.createFloatBuffer(16);
    private Random starRandomizer = new Random(10842L);
    private int starList;
    private int skyList;
    private int groundList;
    private int cloudTicks = 0;
    private int moonPhase = 0;
    private int starTex = 0;
    private int[] starColors = new int[]{16777172, 16766164, 13948159, 16777215};

    public SkyRenderer(Minecraft mc, TextureLoader loader) {
        this.game = mc;
        this.texLoader = loader;
        this.createStarTexture();
        this.rebuildStarsList();
        this.buildSkyLists();
    }

    public void setWorld(World world) {
        this.world = world;
        this.starRandomizer.setSeed(world.getSeed());
        this.rebuildStarsList();
    }

    public void updateClouds() {
        this.cloudTicks++;
    }

    private void buildSkyLists() {
        Tessellator tess = Tessellator.instance;
        byte upperBound = 64;
        int multiplier = 256 / upperBound + 2;
        float yOffset = 16.0F;
        int x = 0;
        int z = 0;
        this.skyList = this.starList + 1;
        GL11.glNewList(this.skyList, 4864);

        for (int var8 = -upperBound * multiplier; var8 <= upperBound * multiplier; var8 += upperBound) {
            for (int var10 = -upperBound * multiplier; var10 <= upperBound * multiplier; var10 += upperBound) {
                tess.begin();
                tess.addVertex(var8, yOffset, var10);
                tess.addVertex(var8 + upperBound, yOffset, var10);
                tess.addVertex(var8 + upperBound, yOffset, var10 + upperBound);
                tess.addVertex(var8, yOffset, var10 + upperBound);
                tess.draw();
            }
        }

        GL11.glEndList();
        this.groundList = this.starList + 2;
        GL11.glNewList(this.groundList, 4864);
        yOffset = -16.0F;
        tess.begin();

        for (int var9 = -upperBound * multiplier; var9 <= upperBound * multiplier; var9 += upperBound) {
            for (int var11 = -upperBound * multiplier; var11 <= upperBound * multiplier; var11 += upperBound) {
                tess.addVertex(var9 + upperBound, yOffset, var11);
                tess.addVertex(var9, yOffset, var11);
                tess.addVertex(var9, yOffset, var11 + upperBound);
                tess.addVertex(var9 + upperBound, yOffset, var11 + upperBound);
            }
        }

        tess.draw();
        GL11.glEndList();
    }

    public void renderSky(float tick) {
        Tessellator tess = Tessellator.instance;
        GL11.glDisable(3553);
        Vector skyColor = this.world.getSkyColor(this.game.gameRenderer.cameraMob, tick, OptionValues.skyStyle);
        float red = (float)skyColor.xCoord;
        float green = (float)skyColor.yCoord;
        float blue = (float)skyColor.zCoord;
        GL11.glColor3f(red, green, blue);
        GL11.glDepthMask(false);
        GL11.glEnable(2912);
        GL11.glColor3f(red, green, blue);
        GL11.glCallList(this.skyList);
        GL11.glEnable(3553);
        GL11.glDisable(2912);
        GL11.glDisable(3008);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        this.applySunsetColors(tess, tick);
        GL11.glEnable(3553);
        GL11.glBlendFunc(770, 1);
        GL11.glPushMatrix();
        float rain = 1.0F - this.world.getRainStrength(tick);
        GL11.glColor4f(rain, rain, rain, rain);
        this.renderSunAndMoon(tess, tick);
        float starVisibility = this.world.getStarBrightness(tick) * rain;
        if (starVisibility > 0.0F && OptionValues.showStars) {
            this.updateStarBrightnes(starVisibility);
            GL11.glCallList(this.starList);
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(3042);
        GL11.glEnable(3008);
        GL11.glEnable(2912);
        GL11.glPopMatrix();
        double minY = this.game.gameRenderer.cameraMob.posY - (this.game.world.getSeaLevel() - 16);
        if (this.game.world.currDim instanceof SkyDimension) {
            GL11.glColor3f(red, green, blue);
        } else if (!(minY < 0.0) && !(this.game.world.currDim instanceof HellDimension) && this.game.world.settings.getType() != WorldType.CAVES) {
            GL11.glColor3f(red * 0.2F + 0.04F, green * 0.2F + 0.04F, blue * 0.6F + 0.1F);
        } else {
            GL11.glColor3f(0.0F, 0.0F, 0.0F);
        }

        GL11.glDisable(3553);
        GL11.glCallList(this.groundList);
        GL11.glEnable(3553);
        GL11.glDepthMask(true);
    }

    private void applySunsetColors(Tessellator tess, float tick) {
        if (OptionValues.fancyGraphics) {
            float[] sunsetColors = this.world.currDim.sunsetColors(this.world.getSunAngle(tick), tick, 1.0F);
            if (sunsetColors != null) {
                GL11.glDisable(3553);
                GL11.glShadeModel(7425);
                GL11.glPushMatrix();
                GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(MathHelper.sin(this.world.getSunAngleRadians(tick)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
                tess.begin(6);
                tess.setColorAlpha(sunsetColors[0], sunsetColors[1], sunsetColors[2], sunsetColors[3]);
                tess.addVertex(0.0, 100.0, 0.0);
                tess.setColorAlpha(sunsetColors[0], sunsetColors[1], sunsetColors[2], 0.0F);
                byte passes = 16;

                for (int pass = 0; pass <= passes; pass++) {
                    float angle = pass * (float) Math.PI * 2.0F / passes;
                    float low = MathHelper.sin(angle);
                    float high = MathHelper.cos(angle);
                    tess.addVertex(low * 120.0F, high * 120.0F, -high * 40.0F * sunsetColors[3]);
                }

                tess.draw();
                GL11.glPopMatrix();
                GL11.glShadeModel(7424);
            }
        }
    }

    private void renderSunAndMoon(Tessellator tess, float tick) {
        GL11.glRotatef(this.world.getSunAngle(tick) * 360.0F, 1.0F, 0.0F, 0.0F);
        float moonOffset = 30.0F;
        if (this.world.badSun) {
            GL11.glBindTexture(3553, this.texLoader.getTexture("/environment/badSun.png"));
        } else {
            GL11.glBindTexture(3553, this.texLoader.getTexture("/environment/sun.png"));
        }

        if (!(this.world.currDim instanceof VoidDimension)) {
            tess.begin();
            tess.addTexturedVertex(-moonOffset, 100.0, -moonOffset, 0.0F, 0.0F);
            tess.addTexturedVertex(moonOffset, 100.0, -moonOffset, 1.0F, 0.0F);
            tess.addTexturedVertex(moonOffset, 100.0, moonOffset, 1.0F, 1.0F);
            tess.addTexturedVertex(-moonOffset, 100.0, moonOffset, 0.0F, 1.0F);
            tess.draw();
        }

        moonOffset = 20.0F;
        if (this.world.currDim instanceof HellDimension) {
            GL11.glBindTexture(3553, this.texLoader.getTexture("/environment/blood_moon.png"));
            if (this.world.rand.nextInt(100) == 0) {
                this.moonPhase = this.world.rand.nextInt(8);
            }
        } else {
            GL11.glBindTexture(3553, this.texLoader.getTexture("/environment/moon_phases.png"));
            this.moonPhase = this.world.getMoonPhase();
        }

        int currPhase = this.moonPhase % 4;
        int nextPhase = this.moonPhase / 4 % 2;
        float startU = (currPhase + 0) / 4.0F;
        float endU = (nextPhase + 0) / 2.0F;
        float startV = (currPhase + 1) / 4.0F;
        float endV = (nextPhase + 1) / 2.0F;
        if (!(this.world.currDim instanceof VoidDimension)) {
            tess.begin();
            tess.addTexturedVertex(-moonOffset, -100.0, moonOffset, startV, endV);
            tess.addTexturedVertex(moonOffset, -100.0, moonOffset, startU, endV);
            tess.addTexturedVertex(moonOffset, -100.0, -moonOffset, startU, endU);
            tess.addTexturedVertex(-moonOffset, -100.0, -moonOffset, startV, endU);
            tess.draw();
        }
    }

    private void rebuildStarsList() {
        this.starList = Renderer.generateDisplayLists(3);
        GL11.glPushMatrix();
        GL11.glNewList(this.starList, 4864);
        this.renderStars();
        GL11.glEndList();
        GL11.glPopMatrix();
    }

    private void renderStars() {
        Tessellator tess = Tessellator.instance;
        tess.begin();

        for (int star = 0; star < 1500; star++) {
            double starX = this.starRandomizer.nextFloat() * 2.0F - 1.0F;
            double starY = this.starRandomizer.nextFloat() * 2.0F - 1.0F;
            double starZ = this.starRandomizer.nextFloat() * 2.0F - 1.0F;
            double starSize = 0.25F + this.starRandomizer.nextFloat() * 0.25F;
            double totalSize = starX * starX + starY * starY + starZ * starZ;
            if (totalSize < 1.0 && totalSize > 0.01) {
                totalSize = 1.0 / Math.sqrt(totalSize);
                starX *= totalSize;
                starY *= totalSize;
                starZ *= totalSize;
                double pointX = starX * 100.0;
                double pointY = starY * 100.0;
                double pointZ = starZ * 100.0;
                double upperLeft = Math.atan2(starX, starZ);
                double leftCorner1 = Math.sin(upperLeft);
                double leftCorner2 = Math.cos(upperLeft);
                double upperRight = Math.atan2(Math.sqrt(starX * starX + starZ * starZ), starY);
                double rightCorner1 = Math.sin(upperRight);
                double rightCorner2 = Math.cos(upperRight);
                double center = this.starRandomizer.nextDouble() * Math.PI * 2.0;
                double center1 = Math.sin(center);
                double center2 = Math.cos(center);

                for (int corner = 0; corner < 4; corner++) {
                    double startX = ((corner & 2) - 1) * starSize;
                    double endX = ((corner + 1 & 2) - 1) * starSize;
                    double u1 = startX * center2 - endX * center1;
                    double u2 = endX * center2 + startX * center1;
                    double lengthY = u1 * rightCorner1;
                    double w = -u1 * rightCorner2;
                    double lengthX = w * leftCorner1 - u2 * leftCorner2;
                    double lengthZ = u2 * leftCorner1 + w * leftCorner2;
                    tess.setColor(this.starColors[this.starRandomizer.nextInt(this.starColors.length)]);
                    tess.addTexturedVertex(pointX + lengthX, pointY + lengthY, pointZ + lengthZ, 0.0F, 0.0F);
                }
            }
        }

        tess.draw();
    }

    private void createStarTexture() {
        this.starTex = GL11.glGenTextures();
        GL11.glBindTexture(3553, this.starTex);
        GL11.glTexParameteri(3553, 10241, 9728);
        GL11.glTexParameteri(3553, 10240, 9728);
        ByteBuffer buf = BufferUtils.createByteBuffer(4);
        ((Buffer)buf.put((byte)-1).put((byte)-1).put((byte)-1).put((byte)-1)).flip();
        GL11.glTexImage2D(3553, 0, 6408, 1, 1, 0, 6408, 5121, buf);
    }

    private void updateStarBrightnes(float brightness) {
        int b = Math.max(0, Math.min(255, (int)(brightness * 255.0F)));
        ByteBuffer buf = BufferUtils.createByteBuffer(4);
        ((Buffer)buf.put((byte)b).put((byte)b).put((byte)b).put((byte)b)).flip();
        GL11.glBindTexture(3553, this.starTex);
        GL11.glTexSubImage2D(3553, 0, 0, 0, 1, 1, 6408, 5121, buf);
    }

    public void renderClouds(float tick) {
        if (!(this.game.currentScreen instanceof PhotoMode)) {
            if (OptionValues.cloudStyle != 3) {
                GL11.glDisable(2884);
                float playerPosY = (float)(
                    this.game.gameRenderer.cameraMob.lastTickPosY
                        + (this.game.gameRenderer.cameraMob.posY - this.game.gameRenderer.cameraMob.lastTickPosY) * tick
                );
                byte size = 32;
                int dist = (byte)OptionValues.renderDistance;
                Tessellator tess = Tessellator.instance;
                GL11.glBindTexture(3553, this.texLoader.getTexture("/environment/clouds.png"));
                GL11.glEnable(3042);
                GL11.glBlendFunc(770, 771);
                Vector color = this.world.getCloudColor(tick);
                float red = (float)color.xCoord;
                float green = (float)color.yCoord;
                float blue = (float)color.zCoord;
                float offset = 4.8828125E-4F;
                float heightOffset = 1.5F;
                if (this.world.currDim instanceof SkyDimension) {
                    heightOffset = 0.5F;
                }

                double playerPosX = this.game.gameRenderer.cameraMob.prevX
                    + (this.game.gameRenderer.cameraMob.posX - this.game.gameRenderer.cameraMob.prevX) * tick
                    + (this.cloudTicks + tick) * 0.03F;
                double playerPosZ = this.game.gameRenderer.cameraMob.prevZ
                    + (this.game.gameRenderer.cameraMob.posZ - this.game.gameRenderer.cameraMob.prevZ) * tick;
                int offsetX = MathHelper.floor_double(playerPosX / 2048.0);
                int offsetZ = MathHelper.floor_double(playerPosZ / 2048.0);
                playerPosX -= offsetX * 2048;
                playerPosZ -= offsetZ * 2048;
                float cloudHeight = this.world.currDim.getCloudHeight() * heightOffset - playerPosY + 0.33F;
                float cloudX = (float)(playerPosX * offset);
                float cloudZ = (float)(playerPosZ * offset);
                tess.begin();
                tess.setColorAlpha(red, green, blue, 0.8F);

                for (int x = -size * dist; x < size * dist; x += size) {
                    for (int z = -size * dist; z < size * dist; z += size) {
                        tess.addTexturedVertex(x, cloudHeight, z + size, x * offset + cloudX, (z + size) * offset + cloudZ);
                        tess.addTexturedVertex(x + size, cloudHeight, z + size, (x + size) * offset + cloudX, (z + size) * offset + cloudZ);
                        tess.addTexturedVertex(x + size, cloudHeight, z, (x + size) * offset + cloudX, z * offset + cloudZ);
                        tess.addTexturedVertex(x, cloudHeight, z, x * offset + cloudX, z * offset + cloudZ);
                    }
                }

                tess.draw();
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glDisable(3042);
                GL11.glEnable(2884);
            }

            if (OptionValues.cloudStyle <= 1) {
                this.renderClouds3D(tick);
            }
        }
    }

    public void renderClouds3D(float tick) {
        GL11.glDisable(2884);
        float playerPosY = (float)(
            this.game.gameRenderer.cameraMob.lastTickPosY + (this.game.gameRenderer.cameraMob.posY - this.game.gameRenderer.cameraMob.lastTickPosY) * tick
        );
        Tessellator tess = Tessellator.instance;
        float scaleOff = 12.0F;
        float cloudWidth = 4.0F;
        double playerPosX = (
                this.game.gameRenderer.cameraMob.prevX
                    + (this.game.gameRenderer.cameraMob.posX - this.game.gameRenderer.cameraMob.prevX) * tick
                    + (this.cloudTicks + tick) * 0.06F
            )
            / scaleOff;
        double playerPosZ = (this.game.gameRenderer.cameraMob.prevZ + (this.game.gameRenderer.cameraMob.posZ - this.game.gameRenderer.cameraMob.prevZ) * tick)
                / scaleOff
            + 0.33F;
        float cloudHeight = this.world.currDim.getCloudHeight() - playerPosY + 0.33F;
        int xOffset = MathHelper.floor_double(playerPosX / 2048.0);
        int zOffset = MathHelper.floor_double(playerPosZ / 2048.0);
        playerPosX -= xOffset * 2048;
        playerPosZ -= zOffset * 2048;
        if (OptionValues.cloudStyle == 0) {
            GL13.glActiveTexture(33986);
            GL11.glBindTexture(3553, this.game.textureLoader.getTexture("/environment/fluff.png"));
            GL11.glTexGeni(8192, 9472, 9217);
            GL11.glTexGen(8192, 9473, this.cloudBuffer(1.0F, 0.0F));
            GL11.glTexGeni(8193, 9472, 9217);
            GL11.glTexGen(8193, 9473, this.cloudBuffer(0.0F, 1.0F));
            GL11.glEnable(3168);
            GL11.glEnable(3169);
            GL11.glEnable(3553);
            GL11.glMatrixMode(5890);
            GL11.glLoadIdentity();
            GL11.glScalef(0.25F, 0.25F, 0.25F);
            GL11.glTranslatef((float)playerPosX - 0.1F, (float)playerPosZ - 0.1F, 0.0F);
            GL11.glMatrixMode(5888);
            GL13.glActiveTexture(33984);
        }

        GL11.glEnable(3553);
        GL11.glBindTexture(3553, this.texLoader.getTexture("/environment/clouds.png"));
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        Vector color = this.world.getCloudColor(tick);
        float red = (float)color.xCoord;
        float green = (float)color.yCoord;
        float blue = (float)color.zCoord;
        float offset = 0.00390625F;
        float cloudX = MathHelper.floor_double(playerPosX) * offset;
        float cloudZ = MathHelper.floor_double(playerPosZ) * offset;
        float finalX = (float)(playerPosX - MathHelper.floor_double(playerPosX));
        float finalZ = (float)(playerPosZ - MathHelper.floor_double(playerPosZ));
        byte sides = 8;
        byte dist = (byte)(OptionValues.renderDistance / 2.5F);
        float off = 9.765625E-4F;
        GL11.glScalef(scaleOff, 1.0F, scaleOff);

        for (int pass = 0; pass < 2; pass++) {
            if (pass == 0) {
                GL11.glColorMask(false, false, false, false);
            } else {
                GL11.glColorMask(true, true, true, true);
            }

            for (int x = -dist; x <= dist; x++) {
                for (int z = -dist; z <= dist; z++) {
                    tess.begin();
                    float startU = x * sides;
                    float startV = z * sides;
                    float endU = startU - finalX;
                    float endV = startV - finalZ;
                    if (cloudHeight > -cloudWidth - 1.0F) {
                        tess.setColorAlpha(red * 0.7F, green * 0.7F, blue * 0.7F, 0.8F);
                        tess.setNormals(0.0F, -1.0F, 0.0F);
                        tess.addTexturedVertex(
                            endU + 0.0F, cloudHeight + 0.0F, endV + sides, (startU + 0.0F) * offset + cloudX, (startV + sides) * offset + cloudZ
                        );
                        tess.addTexturedVertex(
                            endU + sides, cloudHeight + 0.0F, endV + sides, (startU + sides) * offset + cloudX, (startV + sides) * offset + cloudZ
                        );
                        tess.addTexturedVertex(
                            endU + sides, cloudHeight + 0.0F, endV + 0.0F, (startU + sides) * offset + cloudX, (startV + 0.0F) * offset + cloudZ
                        );
                        tess.addTexturedVertex(
                            endU + 0.0F, cloudHeight + 0.0F, endV + 0.0F, (startU + 0.0F) * offset + cloudX, (startV + 0.0F) * offset + cloudZ
                        );
                    }

                    if (cloudHeight <= cloudWidth + 1.0F) {
                        tess.setColorAlpha(red, green, blue, 0.8F);
                        tess.setNormals(0.0F, 1.0F, 0.0F);
                        tess.addTexturedVertex(
                            endU + 0.0F, cloudHeight + cloudWidth - off, endV + sides, (startU + 0.0F) * offset + cloudX, (startV + sides) * offset + cloudZ
                        );
                        tess.addTexturedVertex(
                            endU + sides, cloudHeight + cloudWidth - off, endV + sides, (startU + sides) * offset + cloudX, (startV + sides) * offset + cloudZ
                        );
                        tess.addTexturedVertex(
                            endU + sides, cloudHeight + cloudWidth - off, endV + 0.0F, (startU + sides) * offset + cloudX, (startV + 0.0F) * offset + cloudZ
                        );
                        tess.addTexturedVertex(
                            endU + 0.0F, cloudHeight + cloudWidth - off, endV + 0.0F, (startU + 0.0F) * offset + cloudX, (startV + 0.0F) * offset + cloudZ
                        );
                    }

                    tess.setColorAlpha(red * 0.9F, green * 0.9F, blue * 0.9F, 0.8F);
                    if (x > -1) {
                        tess.setNormals(-1.0F, 0.0F, 0.0F);

                        for (int face = 0; face < sides; face++) {
                            tess.addTexturedVertex(
                                endU + face, cloudHeight, endV + sides, (startU + face + 0.5F) * offset + cloudX, (startV + sides) * offset + cloudZ
                            );
                            tess.addTexturedVertex(
                                endU + face,
                                cloudHeight + cloudWidth,
                                endV + sides,
                                (startU + face + 0.5F) * offset + cloudX,
                                (startV + sides) * offset + cloudZ
                            );
                            tess.addTexturedVertex(
                                endU + face, cloudHeight + cloudWidth, endV, (startU + face + 0.5F) * offset + cloudX, startV * offset + cloudZ
                            );
                            tess.addTexturedVertex(endU + face, cloudHeight, endV, (startU + face + 0.5F) * offset + cloudX, startV * offset + cloudZ);
                        }
                    }

                    if (x <= 1) {
                        tess.setNormals(1.0F, 0.0F, 0.0F);

                        for (int face = 0; face < sides; face++) {
                            tess.addTexturedVertex(
                                endU + face + 1.0F - off,
                                cloudHeight,
                                endV + sides,
                                (startU + face + 0.5F) * offset + cloudX,
                                (startV + sides) * offset + cloudZ
                            );
                            tess.addTexturedVertex(
                                endU + face + 1.0F - off,
                                cloudHeight + cloudWidth,
                                endV + sides,
                                (startU + face + 0.5F) * offset + cloudX,
                                (startV + sides) * offset + cloudZ
                            );
                            tess.addTexturedVertex(
                                endU + face + 1.0F - off, cloudHeight + cloudWidth, endV, (startU + face + 0.5F) * offset + cloudX, startV * offset + cloudZ
                            );
                            tess.addTexturedVertex(
                                endU + face + 1.0F - off, cloudHeight, endV, (startU + face + 0.5F) * offset + cloudX, startV * offset + cloudZ
                            );
                        }
                    }

                    tess.setColorAlpha(red * 0.8F, green * 0.8F, blue * 0.8F, 0.8F);
                    if (z > -1) {
                        tess.setNormals(0.0F, 0.0F, -1.0F);

                        for (int face = 0; face < sides; face++) {
                            tess.addTexturedVertex(
                                endU, cloudHeight + cloudWidth, endV + face, startU * offset + cloudX, (startV + face + 0.5F) * offset + cloudZ
                            );
                            tess.addTexturedVertex(
                                endU + sides,
                                cloudHeight + cloudWidth,
                                endV + face,
                                (startU + sides) * offset + cloudX,
                                (startV + face + 0.5F) * offset + cloudZ
                            );
                            tess.addTexturedVertex(
                                endU + sides, cloudHeight, endV + face, (startU + sides) * offset + cloudX, (startV + face + 0.5F) * offset + cloudZ
                            );
                            tess.addTexturedVertex(endU, cloudHeight, endV + face, startU * offset + cloudX, (startV + face + 0.5F) * offset + cloudZ);
                        }
                    }

                    if (z <= 1) {
                        tess.setNormals(0.0F, 0.0F, 1.0F);

                        for (int face = 0; face < sides; face++) {
                            tess.addTexturedVertex(
                                endU, cloudHeight + cloudWidth, endV + face + 1.0F - off, startU * offset + cloudX, (startV + face + 0.5F) * offset + cloudZ
                            );
                            tess.addTexturedVertex(
                                endU + sides,
                                cloudHeight + cloudWidth,
                                endV + face + 1.0F - off,
                                (startU + sides) * offset + cloudX,
                                (startV + face + 0.5F) * offset + cloudZ
                            );
                            tess.addTexturedVertex(
                                endU + sides,
                                cloudHeight,
                                endV + face + 1.0F - off,
                                (startU + sides) * offset + cloudX,
                                (startV + face + 0.5F) * offset + cloudZ
                            );
                            tess.addTexturedVertex(
                                endU, cloudHeight, endV + face + 1.0F - off, startU * offset + cloudX, (startV + face + 0.5F) * offset + cloudZ
                            );
                        }
                    }

                    tess.draw();
                }
            }
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(3042);
        GL11.glEnable(2884);
        GL13.glActiveTexture(33986);
        GL11.glDisable(3553);
        GL13.glActiveTexture(33984);
    }

    private FloatBuffer cloudBuffer(float u, float v) {
        ((Buffer)this.cloudBuffer).clear();
        this.cloudBuffer.put(u).put(0.0F).put(v).put(0.0F);
        ((Buffer)this.cloudBuffer).flip();
        return this.cloudBuffer;
    }
}
