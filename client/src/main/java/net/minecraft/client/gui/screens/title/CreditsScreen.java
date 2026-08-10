package net.minecraft.client.gui.screens.title;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.client.gui.button.Button;
import net.minecraft.client.gui.screens.GuiScreen;
import net.minecraft.client.render.Tessellator;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class CreditsScreen extends GuiScreen {
    private List<String> lines = new ArrayList<>();
    private float prevElapsedTicks = 0.1F;
    private float elapsedTicks = 0.1F;
    private float prevScrollValue = 0.0F;
    private float scrollValue = 0.0F;
    private int creditsLength = 0;
    private int updateCounter;
    private boolean showPoem = false;

    public CreditsScreen(boolean poem) {
        this.showPoem = poem;
    }

    @Override
    public void update() {
        this.prevElapsedTicks = this.elapsedTicks;
        this.prevScrollValue = this.scrollValue;
        int totalHeight = this.creditsLength * -8 * this.mc.rescaler.scaleFactor;
        int finalTicks = totalHeight - -totalHeight % this.mc.rescaler.getScaledHeight();
        if (this.elapsedTicks <= finalTicks) {
            this.prevElapsedTicks = 0.0F;
            this.elapsedTicks = 0.0F;
        }

        this.elapsedTicks -= 0.5F;
        this.scrollValue += 0.01F;
        this.updateCounter++;
        float totalTicks = (this.creditsLength + this.height + this.height + 24) / 0.5F;
        if (this.updateCounter > totalTicks) {
            this.mc.setScreen(null);
        }

        if (this.mc.world != null && this.mc.player != null) {
            this.mc
                .world
                .playRecord(
                    "Afterlife",
                    MathHelper.floor_double(this.mc.player.posX),
                    MathHelper.floor_double(this.mc.player.posY),
                    MathHelper.floor_double(this.mc.player.posZ),
                    false
                );
        }
    }

    @Override
    protected void keyTyped(char character, int key) {
        if (key == 1) {
            this.mc.sndManager.sndSystem.stop("streaming");
            this.mc.setScreen(null);
        }
    }

    @Override
    public boolean shouldPause() {
        return true;
    }

    @Override
    public void initGui() {
        try {
            String tempString = "";
            String unicodeValues = "§f§k§a§b";
            short wrapLength = 274;
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(CreditsScreen.class.getResourceAsStream("/title/credits.txt"), Charset.forName("UTF-8"))
            );
            Random randomizer = new Random(8124371L);
            boolean skippedToCredits = false;

            while ((tempString = reader.readLine()) != null) {
                if (this.showPoem) {
                    tempString = tempString.replaceAll("PLAYERNAME", this.mc.player.username);

                    while (tempString.contains(unicodeValues)) {
                        int idx = tempString.indexOf(unicodeValues);
                        String before = tempString.substring(0, idx);
                        String after = tempString.substring(idx + unicodeValues.length());
                        tempString = before + "§f§k" + "XXXXXXXX".substring(0, randomizer.nextInt(4) + 3) + after;
                    }

                    this.lines.addAll(this.mc.fontRenderer.getStringAsList(tempString, wrapLength));
                } else {
                    if (!skippedToCredits) {
                        if (!tempString.contains("Minecraft Infinite Credits")) {
                            continue;
                        }

                        skippedToCredits = true;
                    }

                    this.lines.addAll(this.mc.fontRenderer.getStringAsList(tempString, wrapLength));
                }
            }

            if (!this.showPoem) {
                int originalSize = this.lines.size();

                for (int i = originalSize - 1; i >= originalSize - 4 && i >= 0; i--) {
                    this.lines.remove(i);
                }
            } else {
                for (int strIndex = 0; strIndex < 10; strIndex++) {
                    this.lines.add("");
                }
            }

            this.creditsLength = this.lines.size() * 12;
        } catch (Exception exception9) {
            exception9.printStackTrace();
        }
    }

    @Override
    protected void buttonClicked(Button button) {
    }

    private void renderDynamicBackground(float pTicks) {
        float bgOffset = this.prevScrollValue + (this.scrollValue - this.prevScrollValue) * pTicks;
        int width = this.mc.rescaler.getScaledWidth();
        int height = this.mc.rescaler.getScaledHeight();
        GL11.glClear(16640);
        Tessellator tess = Tessellator.instance;
        this.mc.textureLoader.bindTexture(this.mc.textureLoader.getTexture("/gui/background/background.png"));
        float offset = 32.0F;
        float alpha = (this.updateCounter + pTicks - 0.0F) * 0.02F;
        float alphaOffset = (this.creditsLength + this.height + this.height + 24) / 0.5F;
        float minAlpha = (alphaOffset - 20.0F - (this.updateCounter + pTicks)) * 0.005F;
        if (minAlpha < alpha) {
            alpha = minAlpha;
        }

        if (alpha > 1.0F) {
            alpha = 1.0F;
        }

        alpha *= alpha;
        alpha = alpha * 96.0F / 255.0F;
        tess.begin();
        tess.setColor(alpha, alpha, alpha);
        tess.addTexturedVertex(0.0, height, 0.0, 0.0F, height / offset + bgOffset);
        tess.addTexturedVertex(width, height, 0.0, width / offset, height / offset + bgOffset);
        tess.addTexturedVertex(width, 0.0, 0.0, width / offset, 0.0F + bgOffset);
        tess.addTexturedVertex(0.0, 0.0, 0.0, 0.0F, 0.0F + bgOffset);
        tess.draw();
    }

    @Override
    public void drawScreen(int width, int height, float tickDelta) {
        this.renderDynamicBackground(tickDelta);
        float ticks = this.prevElapsedTicks + (this.elapsedTicks - this.prevElapsedTicks) * tickDelta;
        Tessellator tess = Tessellator.instance;
        short maximum = 300;
        int x = this.width / 2 - maximum / 2;
        int y = this.height + 50;
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, ticks, 0.0F);
        this.mc.textureLoader.bindTexture(this.mc.textureLoader.getTexture("/title/mclogo.png"));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.drawGraphic(x + 20, y, 0, 0, 256, 96);
        tess.setColor(16777215);
        int yOffset = y + 200;

        for (int index = 0; index < this.lines.size(); index++) {
            if (index == this.lines.size() - 1) {
                float maxHeight = yOffset + ticks - (this.height / 2 - 6);
                if (maxHeight < 0.0F) {
                    GL11.glTranslatef(0.0F, -maxHeight, 0.0F);
                }
            }

            if (yOffset + ticks + 12.0F + 8.0F > 0.0F && yOffset + ticks < this.height) {
                String line = this.lines.get(index);
                if (line.startsWith("[C]")) {
                    this.drawString(
                        this.fontRenderer, line.substring(3), x + (maximum - this.fontRenderer.getStringWidth(line.substring(3))) / 2, yOffset, 16777215
                    );
                } else if (!line.startsWith("Â§3")) {
                    this.drawCenteredString(this.fontRenderer, line, this.width / 2, yOffset, 16777215);
                } else {
                    this.drawString(this.fontRenderer, line, x, yOffset, 16777215);
                }
            }

            yOffset += 12;
        }

        GL11.glPopMatrix();
        this.mc.textureLoader.bindTexture(this.mc.textureLoader.getTexture("/misc/vignette.png"));
        GL11.glEnable(3042);
        GL11.glBlendFunc(0, 769);
        tess.begin();
        tess.setColorAlpha(1.0F, 1.0F, 1.0F, 1.0F);
        int wdth = this.width;
        int hght = this.height;
        tess.addTexturedVertex(0.0, hght, this.zLevel, 0.0F, 1.0F);
        tess.addTexturedVertex(wdth, hght, this.zLevel, 1.0F, 1.0F);
        tess.addTexturedVertex(wdth, 0.0, this.zLevel, 1.0F, 0.0F);
        tess.addTexturedVertex(0.0, 0.0, this.zLevel, 0.0F, 0.0F);
        tess.draw();
        GL11.glDisable(3042);
        super.drawScreen(width, height, tickDelta);
    }
}
