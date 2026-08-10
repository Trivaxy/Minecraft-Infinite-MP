package net.minecraft.client.render.item;

import java.awt.image.BufferedImage;
import net.minecraft.client.localization.Font;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.texture.TextureLoader;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.mapdata.MapColor;
import net.minecraft.game.world.mapdata.MapCoords;
import net.minecraft.game.world.mapdata.MapData;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class MapRenderer {
    private final int[] mapsArray = new int[16384];
    private final int bufferedImage;
    private final Font fontRenderer;
    public static final float[][] flagColors = new float[][]{
        {1.0F, 1.0F, 1.0F},
        {0.6F, 0.6F, 0.6F},
        {0.3F, 0.3F, 0.3F},
        {1.0F, 0.25F, 0.625F},
        {1.0F, 0.25F, 1.0F},
        {0.75F, 0.375F, 1.0F},
        {0.643F, 0.318F, 1.0F},
        {0.5F, 0.5F, 1.0F},
        {0.0F, 0.75F, 1.0F},
        {0.25F, 1.0F, 1.0F},
        {0.25F, 1.0F, 0.625F},
        {0.25F, 1.0F, 0.25F},
        {0.625F, 1.0F, 0.25F},
        {1.0F, 1.0F, 0.25F},
        {1.0F, 0.625F, 0.25F},
        {1.0F, 0.25F, 0.25F},
        {0.1F, 0.1F, 0.1F},
        {0.3F, 0.2F, 0.1F}
    };

    public MapRenderer(Font font, TextureLoader texLoader) {
        this.fontRenderer = font;
        this.bufferedImage = texLoader.allocateTexture(new BufferedImage(128, 128, 2));

        for (int id = 0; id < 16384; id++) {
            this.mapsArray[id] = 0;
        }
    }

    public void renderMap(Player player, TextureLoader texLoader, MapData data, boolean renderPlayers) {
        for (int id = 0; id < 16384; id++) {
            byte color = data.colors[id];
            if (color / 4 == 0) {
                this.mapsArray[id] = (id + id / 128 & 1) * 8 + 16 << 24;
            } else {
                int colorVal = MapColor.mapColors[color / 4].value;
                int alphaChannel = color & 3;
                short transparency = 220;
                if (alphaChannel == 2) {
                    transparency = 255;
                }

                if (alphaChannel == 0) {
                    transparency = 180;
                }

                int red = (colorVal >> 16 & 0xFF) * transparency / 255;
                int green = (colorVal >> 8 & 0xFF) * transparency / 255;
                int blue = (colorVal & 0xFF) * transparency / 255;
                this.mapsArray[id] = 0xFF000000 | red << 16 | green << 8 | blue;
            }
        }

        texLoader.createColoredTexture(this.mapsArray, 128, 128, this.bufferedImage);
        Tessellator tess = Tessellator.instance;
        GL11.glBindTexture(3553, this.bufferedImage);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3008);
        tess.begin();
        tess.addTexturedVertex(0.0, 128.0, -0.01, 0.0F, 1.0F);
        tess.addTexturedVertex(128.0, 128.0, -0.01, 1.0F, 1.0F);
        tess.addTexturedVertex(128.0, 0.0, -0.01, 1.0F, 0.0F);
        tess.addTexturedVertex(0.0, 0.0, -0.01, 0.0F, 0.0F);
        tess.draw();
        GL11.glEnable(3008);
        GL11.glDisable(3042);
        texLoader.bindTexture("/misc/mapicons.png");

        for (MapCoords coordinates : data.flags) {
            GL11.glPushMatrix();
            GL11.glTranslatef(coordinates.centerX + 64.0F, coordinates.centerZ + 64.0F, -0.02F);
            GL11.glColor3f(flagColors[coordinates.iconRotation][0], flagColors[coordinates.iconRotation][1], flagColors[coordinates.iconRotation][2]);
            GL11.glScalef(4.0F, 4.0F, 3.0F);
            GL11.glTranslatef(0.25F, 0.0F, 0.0F);
            float minU = coordinates.iconSize % 4 / 4.0F;
            float maxU = coordinates.iconSize / 4.0F / 4.0F;
            float minV = (coordinates.iconSize % 4 + 1) / 4.0F;
            float maxV = (coordinates.iconSize / 4.0F + 1.0F) / 4.0F;
            tess.begin();
            tess.addTexturedVertex(-1.0, 1.0, 0.001, minU, maxU);
            tess.addTexturedVertex(1.0, 1.0, 0.001, minV, maxU);
            tess.addTexturedVertex(1.0, -1.0, 0.001, minV, maxV);
            tess.addTexturedVertex(-1.0, -1.0, 0.001, minU, maxV);
            tess.draw();
            GL11.glPopMatrix();
        }

        GL11.glDisable(2929);
        if (renderPlayers) {
            for (MapCoords coords : data.playersVisible.values()) {
                GL11.glPushMatrix();
                GL11.glTranslatef(coords.centerX / 2.0F + 64.0F, coords.centerZ / 2.0F + 64.0F, -0.02F);
                GL11.glRotatef(coords.iconRotation * 360 / 16.0F, 0.0F, 0.0F, 1.0F);
                GL11.glColor3f(1.0F, 1.0F, 1.0F);
                GL11.glScalef(4.0F, 4.0F, 3.0F);
                GL11.glTranslatef(-0.125F, 0.125F, 0.0F);
                float minU = coords.iconSize % 4 / 4.0F;
                float maxU = coords.iconSize / 4.0F / 4.0F;
                float minV = (coords.iconSize % 4 + 1) / 4.0F;
                float maxV = (coords.iconSize / 4.0F + 1.0F) / 4.0F;
                tess.begin();
                tess.addTexturedVertex(-1.0, 1.0, 0.001, minU, maxU);
                tess.addTexturedVertex(1.0, 1.0, 0.001, minV, maxU);
                tess.addTexturedVertex(1.0, -1.0, 0.001, minV, maxV);
                tess.addTexturedVertex(-1.0, -1.0, 0.001, minU, maxV);
                tess.draw();
                GL11.glPopMatrix();
            }
        }

        GL11.glPushMatrix();
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        if (player != null) {
            this.fontRenderer.drawStringWithShadow(data.mapName, 0, 0, 16777215);
            this.fontRenderer.drawStringWithShadow("x: " + MathHelper.floor(player.posX), 0, 10, 16777215);
            this.fontRenderer.drawStringWithShadow("y: " + MathHelper.floor(player.posY), 0, 20, 16777215);
            this.fontRenderer.drawStringWithShadow("z: " + MathHelper.floor(player.posZ), 0, 30, 16777215);
            Biome biome = player.world.getBiomeAt((int)player.posX, (int)player.posZ);
            String biomeName = "unknown";
            if (biome != null) {
                biomeName = biome.getName();
            }

            this.fontRenderer.drawStringWithShadow("biome: " + biomeName, 0, 40, 16777215);
        }

        GL11.glPopMatrix();
        GL11.glEnable(2929);
    }
}
