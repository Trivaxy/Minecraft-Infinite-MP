package net.minecraft.client.gui.screens.title;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.ScreenRescaler;
import net.minecraft.client.render.Tessellator;
import net.minecraft.util.ProgressBar;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class SplashScreen extends GuiComponent implements ProgressBar {
    private Minecraft mc;

    public SplashScreen(Minecraft mc) {
        this.mc = mc;
    }

    public void displaySplashScreen() throws LWJGLException {
        ScreenRescaler scaler = this.mc.rescaler;
        GL11.glClear(16640);
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0, scaler.getScaledWidth(), scaler.getScaledHeight(), 0.0, 1000.0, 3000.0);
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
        GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
        Tessellator tessellator = Tessellator.instance;
        GL11.glDisable(2896);
        GL11.glEnable(3553);
        GL11.glDisable(2912);
        GL11.glBindTexture(3553, this.mc.textureLoader.getTexture("/title/splash.png"));
        tessellator.begin();
        tessellator.setColor(16777215);
        tessellator.addTexturedVertex(0.0, this.mc.displayHeight, 0.0, 0.0F, 0.0F);
        tessellator.addTexturedVertex(this.mc.displayWidth, this.mc.displayHeight, 0.0, 0.0F, 0.0F);
        tessellator.addTexturedVertex(this.mc.displayWidth, 0.0, 0.0, 0.0F, 0.0F);
        tessellator.addTexturedVertex(0.0, 0.0, 0.0, 0.0F, 0.0F);
        tessellator.draw();
        short sizeX = 256;
        short sizeY = 256;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        tessellator.setColor(16777215);
        this.drawGraphic((scaler.getScaledWidth() - sizeX) / 2, (scaler.getScaledHeight() - sizeY) / 2, 0, 0, sizeX, sizeY);
        GL11.glDisable(2896);
        GL11.glDisable(2912);
        GL11.glEnable(3008);
        GL11.glAlphaFunc(516, 0.1F);
        Display.swapBuffers();
    }

    @Override
    public void displayProgressMessage(String msg) {
    }

    @Override
    public void displayLoadingString(String msg) {
    }

    @Override
    public void setLoadingProgress(int progress) {
        if (!this.mc.running) {
            throw new RuntimeException("Tried to render while the game is not running!");
        }

        long elapsedTime = System.currentTimeMillis();
        if (elapsedTime - this.mc.systemTime >= 20L) {
            this.mc.systemTime = elapsedTime;
            int screenWidth = this.mc.rescaler.getScaledWidth();
            int screenHeight = this.mc.rescaler.getScaledHeight();
            GL11.glClear(256);
            GL11.glMatrixMode(5889);
            GL11.glLoadIdentity();
            GL11.glOrtho(0.0, screenWidth, screenHeight, 0.0, 100.0, 300.0);
            GL11.glMatrixMode(5888);
            GL11.glLoadIdentity();
            GL11.glTranslatef(0.0F, 0.0F, -200.0F);
            GL11.glClear(16640);
            Tessellator tess = Tessellator.instance;

            try {
                this.displaySplashScreen();
            } catch (LWJGLException e) {
                e.printStackTrace();
            }

            if (progress >= 0) {
                int width = 250;
                int height = 10;
                int x = screenWidth / 2 - width / 2;
                int y = screenHeight / 2 + 90;
                GL11.glDisable(3553);
                tess.begin();
                tess.setColor(16777215);
                tess.addVertex(x - 3, y - 3, 0.0);
                tess.addVertex(x - 3, y + height + 3, 0.0);
                tess.addVertex(x + width + 3, y + height + 3, 0.0);
                tess.addVertex(x + width + 3, y - 3, 0.0);
                tess.setColor(15741502);
                tess.addVertex(x, y, 0.0);
                tess.addVertex(x, y + height, 0.0);
                tess.addVertex(x + progress, y + height, 0.0);
                tess.addVertex(x + progress, y, 0.0);
                tess.draw();
                GL11.glEnable(3553);
            }

            Display.update();

            try {
                Thread.yield();
            } catch (Exception var11) {
            }
        }
    }
}
