package net.minecraft.client.gui.screens.title;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.Tessellator;
import net.minecraft.game.world.dimension.HellDimension;
import net.minecraft.game.world.dimension.SkyDimension;
import net.minecraft.game.world.dimension.VoidDimension;
import net.minecraft.localization.Translator;
import net.minecraft.util.ProgressBar;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class LoadingScreen implements ProgressBar {
    private Minecraft mc;
    private Random rand = new Random();
    private String currentlyDisplayedProgress = "";
    private String currentlyDisplayedText = "";
    private String currentTip = "";
    private ArrayList<String> tips = new ArrayList<>();

    public LoadingScreen(Minecraft mc) {
        this.mc = mc;
    }

    public void updateTips() {
        try {
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(this.mc.langPackList.selectedLanguagePack.getResourceAsStream("/lang/tips.lang"), Charset.forName("UTF-8"))
            );
            String line = "";
            this.tips.clear();

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.startsWith("##")) {
                    line = line.replaceAll("%%", "§");
                    if (line.length() > 0) {
                        this.tips.add(line);
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void displayProgressMessage(String msg) {
        this.currentTip = "§e" + Translator.getInstance().translateKey("tip.tips") + "§f" + ": " + this.tips.get(this.rand.nextInt(this.tips.size()));
        this.currentlyDisplayedText = msg;
    }

    @Override
    public void displayLoadingString(String msg) {
        if (!this.mc.running) {
            throw new RuntimeException("Tried to render while the game is not running!");
        }

        this.currentlyDisplayedProgress = msg;
        this.setLoadingProgress(-1);
    }

    @Override
    public void setLoadingProgress(int progress) {
        if (!this.mc.running) {
            throw new RuntimeException("Tried to render while the game is not running!");
        }

        int width = this.mc.rescaler.getScaledWidth();
        int height = this.mc.rescaler.getScaledHeight();
        GL11.glClear(256);
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0, width, height, 0.0, 100.0, 300.0);
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -200.0F);
        GL11.glClear(16640);
        Tessellator tessellator = Tessellator.instance;
        int texture = this.mc.textureLoader.getTexture("/gui/background/background.png");
        if (this.mc.world != null) {
            if (this.mc.world.currDim instanceof HellDimension) {
                texture = this.mc.textureLoader.getTexture("/gui/background/backgroundNether.png");
            } else if (this.mc.world.currDim instanceof SkyDimension) {
                texture = this.mc.textureLoader.getTexture("/gui/background/backgroundAether.png");
            } else if (this.mc.world.currDim instanceof VoidDimension) {
                texture = this.mc.textureLoader.getTexture("/gui/background/backgroundVoid.png");
            }
        }

        GL11.glBindTexture(3553, texture);
        float offset = 32.0F;
        tessellator.begin();
        tessellator.setColor(4210752);
        tessellator.addTexturedVertex(0.0, height, 0.0, 0.0F, height / offset);
        tessellator.addTexturedVertex(width, height, 0.0, width / offset, height / offset);
        tessellator.addTexturedVertex(width, 0.0, 0.0, width / offset, 0.0F);
        tessellator.addTexturedVertex(0.0, 0.0, 0.0, 0.0F, 0.0F);
        tessellator.draw();
        if (progress >= 0) {
            byte barWidth = 100;
            byte barHeight = 2;
            int x = width / 2 - barWidth / 2;
            int y = height / 2 + 16;
            GL11.glDisable(3553);
            tessellator.begin();
            tessellator.setColor(8421504);
            tessellator.addVertex(x, y, 0.0);
            tessellator.addVertex(x, y + barHeight, 0.0);
            tessellator.addVertex(x + barWidth, y + barHeight, 0.0);
            tessellator.addVertex(x + barWidth, y, 0.0);
            tessellator.setColor(8454016);
            tessellator.addVertex(x, y, 0.0);
            tessellator.addVertex(x, y + barHeight, 0.0);
            tessellator.addVertex(x + progress, y + barHeight, 0.0);
            tessellator.addVertex(x + progress, y, 0.0);
            tessellator.draw();
            GL11.glEnable(3553);
        }

        this.mc
            .fontRenderer
            .drawStringWithShadow(
                this.currentlyDisplayedText, (width - this.mc.fontRenderer.getStringWidth(this.currentlyDisplayedText)) / 2, height / 2 - 4 - 16, 16777215
            );
        this.mc
            .fontRenderer
            .drawStringWithShadow(
                this.currentlyDisplayedProgress,
                (width - this.mc.fontRenderer.getStringWidth(this.currentlyDisplayedProgress)) / 2,
                height / 2 - 4 + 8,
                16777215
            );
        this.mc.fontRenderer.drawStringWithShadow(this.currentTip, (width - this.mc.fontRenderer.getStringWidth(this.currentTip)) / 2, height - 12, 16777215);
        Display.update();

        try {
            Thread.yield();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
