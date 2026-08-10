package net.minecraft.client.render;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import net.minecraft.game.physics.Vector;
import org.lwjgl.opengl.GL11;

public class Lighting {
    private static FloatBuffer colorBuffer = Renderer.createFloatBuffer(16);

    public static void disable() {
        GL11.glDisable(2896);
        GL11.glDisable(16384);
        GL11.glDisable(16385);
        GL11.glDisable(2903);
    }

    public static void enable() {
        GL11.glEnable(2896);
        GL11.glEnable(16384);
        GL11.glEnable(16385);
        GL11.glEnable(2903);
        GL11.glColorMaterial(1032, 5634);
        float red = 0.4F;
        float green = 0.6F;
        float blue = 0.0F;
        Vector lightAngle = Vector.createVector(0.7F, 1.0, -0.2).normalize();
        GL11.glLight(16384, 4611, setColorBuffer((float)lightAngle.xCoord, (float)lightAngle.yCoord, (float)lightAngle.zCoord, 0.0F));
        GL11.glLight(16384, 4609, setColorBuffer(green, green, green, 1.0F));
        GL11.glLight(16384, 4608, setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
        GL11.glLight(16384, 4610, setColorBuffer(blue, blue, blue, 1.0F));
        lightAngle = Vector.createVector(-0.7, 1.0, 0.2F).normalize();
        GL11.glLight(16385, 4611, setColorBuffer((float)lightAngle.xCoord, (float)lightAngle.yCoord, (float)lightAngle.zCoord, 0.0F));
        GL11.glLight(16385, 4609, setColorBuffer(green, green, green, 1.0F));
        GL11.glLight(16385, 4608, setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
        GL11.glLight(16385, 4610, setColorBuffer(blue, blue, blue, 1.0F));
        GL11.glShadeModel(7424);
        GL11.glLightModel(2899, setColorBuffer(red, red, red, 1.0F));
    }

    private static FloatBuffer setColorBuffer(float red, float green, float blue, float alpha) {
        ((Buffer)colorBuffer).clear();
        colorBuffer.put(red).put(green).put(blue).put(alpha);
        ((Buffer)colorBuffer).flip();
        return colorBuffer;
    }
}
