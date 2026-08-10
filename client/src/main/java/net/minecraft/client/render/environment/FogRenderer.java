package net.minecraft.client.render.environment;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.game.PhotoMode;
import net.minecraft.client.options.OptionValues;
import net.minecraft.client.render.Renderer;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.item.potion.EffectList;
import net.minecraft.game.physics.Vector;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldType;
import net.minecraft.game.world.dimension.Overworld;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

public class FogRenderer {
    private Minecraft mc;
    public float fogColorRed;
    public float fogColorGreen;
    public float fogColorBlue;
    public float prevFogColor;
    public float fogColor;
    private FloatBuffer fogColorBuffer = Renderer.createFloatBuffer(16);

    public FogRenderer(Minecraft mc) {
        this.mc = mc;
    }

    public void updateColor(float tickDelta) {
        World world = this.mc.world;
        Mob player = this.mc.gameRenderer.cameraMob;
        float renderDistance = 1.0F / (33 - OptionValues.renderDistance);
        renderDistance = 1.0F - (float)Math.pow(renderDistance, 0.25);
        Vector skyColor = world.getSkyColor(player, tickDelta, OptionValues.skyStyle);
        float red = (float)skyColor.xCoord;
        float green = (float)skyColor.yCoord;
        float blue = (float)skyColor.zCoord;
        Vector fogColor = world.getFogColor(tickDelta);
        this.fogColorRed = (float)fogColor.xCoord;
        this.fogColorGreen = (float)fogColor.yCoord;
        this.fogColorBlue = (float)fogColor.zCoord;
        Vector sunsetVector = MathHelper.sin(world.getSunsetAngle(tickDelta)) > 0.0F ? Vector.createVector(0.0, 0.0, 1.0) : Vector.createVector(0.0, 0.0, -1.0);
        float playerOrient = (float)player.getLookVector(tickDelta).getLocationCoords(sunsetVector);
        if (playerOrient < 0.0F) {
            playerOrient = 0.0F;
        }

        float[] sunsetColors = world.currDim.sunsetColors(world.getSunAngle(tickDelta), tickDelta, 2.5F);
        if (playerOrient > 0.1F && sunsetColors != null && sunsetColors[3] > 0.2F && OptionValues.fancyGraphics) {
            playerOrient *= sunsetColors[3];
            this.fogColorRed = this.fogColorRed * (1.0F - playerOrient) + sunsetColors[0] * playerOrient;
            this.fogColorGreen = this.fogColorGreen * (1.0F - playerOrient) + sunsetColors[1] * playerOrient;
            this.fogColorBlue = this.fogColorBlue * (1.0F - playerOrient) + sunsetColors[2] * playerOrient;
        }

        this.fogColorRed = this.fogColorRed + (red - this.fogColorRed) * renderDistance;
        this.fogColorGreen = this.fogColorGreen + (green - this.fogColorGreen) * renderDistance;
        this.fogColorBlue = this.fogColorBlue + (blue - this.fogColorBlue) * renderDistance;
        float rainStr = world.getRainStrength(tickDelta);
        if (rainStr > 0.0F && this.mc.world.currDim.weatherEnabled()) {
            playerOrient = 1.0F - rainStr * 0.5F;
            float offset = 1.0F - rainStr * 0.4F;
            this.fogColorRed *= playerOrient;
            this.fogColorGreen *= playerOrient;
            this.fogColorBlue *= offset;
        }

        playerOrient = world.getThunderStrength(tickDelta);
        if (playerOrient > 0.0F && this.mc.world.currDim.weatherEnabled()) {
            float offset = 1.0F - playerOrient * 0.5F;
            this.fogColorRed *= offset;
            this.fogColorGreen *= offset;
            this.fogColorBlue *= offset;
        }

        if (player.isInMaterial(MaterialList.water)) {
            this.fogColorRed = 0.02F;
            this.fogColorGreen = 0.02F;
            this.fogColorBlue = 0.2F;
        } else if (player.isInMaterial(MaterialList.lava)) {
            this.fogColorRed = 0.6F;
            this.fogColorGreen = 0.1F;
            this.fogColorBlue = 0.0F;
        } else if (player.hasEffect(EffectList.blindness)) {
            this.fogColorRed = 0.0F;
            this.fogColorGreen = 0.0F;
            this.fogColorBlue = 0.0F;
        }

        float color = this.prevFogColor + (this.fogColor - this.prevFogColor) * tickDelta;
        this.fogColorRed *= color;
        this.fogColorGreen *= color;
        this.fogColorBlue *= color;
        double height = this.mc.world.getSeaLevel() - 1;
        if (this.mc.world.currDim.type == WorldType.FLAT || !(this.mc.world.currDim instanceof Overworld)) {
            height = this.mc.world.getSettings().topLayer - 1;
        } else if (this.mc.world.currDim.type == WorldType.CAVES) {
            height = this.mc.world.getWorldHeight();
        }

        double cameraDist = (player.lastTickPosY + (player.posY - player.lastTickPosY) * tickDelta) / height;
        if (cameraDist < 1.0) {
            if (cameraDist < 0.0) {
                cameraDist = 0.0;
            }

            cameraDist *= cameraDist;
            this.fogColorRed = (float)(this.fogColorRed * cameraDist);
            this.fogColorGreen = (float)(this.fogColorGreen * cameraDist);
            this.fogColorBlue = (float)(this.fogColorBlue * cameraDist);
        }

        GL11.glClearColor(this.fogColorRed, this.fogColorGreen, this.fogColorBlue, 0.0F);
    }

    public void set(int layer) {
        Mob player = this.mc.gameRenderer.cameraMob;
        GL11.glFog(2918, this.setColorBuffer(this.fogColorRed, this.fogColorGreen, this.fogColorBlue, 1.0F));
        GL11.glNormal3f(0.0F, -1.0F, 0.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (player.isInMaterial(MaterialList.water) && !this.mc.player.hasEffect(EffectList.nightVision)) {
            GL11.glFogi(2917, 2048);
            GL11.glFogf(2914, 0.1F);
        } else if (player.isInMaterial(MaterialList.lava) && !this.mc.player.hasEffect(EffectList.nightVision)) {
            GL11.glFogi(2917, 2048);
            if (player.canBurn()) {
                GL11.glFogf(2914, 2.0F);
            } else {
                GL11.glFogf(2914, 0.1F);
            }
        } else if (player.hasEffect(EffectList.blindness) && !this.mc.player.hasEffect(EffectList.nightVision)) {
            GL11.glFogi(2917, 2048);
            GL11.glFogf(2914, 0.5F);
        } else {
            float renderDist = this.mc.gameRenderer.farPlaneDistance;
            if (this.mc.currentScreen instanceof PhotoMode || !OptionValues.showFog) {
                renderDist = 512.0F;
            }

            GL11.glFogi(2917, 9729);
            GL11.glFogf(2915, renderDist * 0.25F);
            GL11.glFogf(2916, renderDist);
            float fogDist = 0.8F;
            if (this.mc.player.dimension == 1 || this.mc.world.settings.getType() == WorldType.CAVES) {
                fogDist = 0.1F;
            }

            if (layer < 0) {
                GL11.glFogf(2915, 0.0F);
                GL11.glFogf(2916, renderDist * fogDist);
            }

            if (GLContext.getCapabilities().GL_NV_fog_distance) {
                GL11.glFogi(34138, 34139);
            }
        }

        GL11.glEnable(2903);
        GL11.glColorMaterial(1028, 4608);
    }

    private FloatBuffer setColorBuffer(float red, float green, float blue, float alpha) {
        ((Buffer)this.fogColorBuffer).clear();
        this.fogColorBuffer.put(red).put(green).put(blue).put(alpha);
        ((Buffer)this.fogColorBuffer).flip();
        return this.fogColorBuffer;
    }
}
