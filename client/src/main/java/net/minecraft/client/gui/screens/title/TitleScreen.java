package net.minecraft.client.gui.screens.title;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.client.gui.button.Button;
import net.minecraft.client.gui.screens.GuiScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectScreen;
import net.minecraft.client.gui.screens.options.GeneralScreen;
import net.minecraft.client.gui.screens.options.LanguagePackScreen;
import net.minecraft.client.gui.screens.worldselect.WorldSelectScreen;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.blocks.BlocksRenderer;
import net.minecraft.game.block.BlockList;
import net.minecraft.localization.Translator;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class TitleScreen extends GuiScreen {
    private static final Random random = new Random();
    private TitleScreen.LogoEffectRandomizer[][] logoEffects;
    private static final BlocksRenderer renderBlocks = new BlocksRenderer();
    private String splash = "missingno.";
    public static String resource = "";
    private String[] logo = new String[]{
        " *   * * *   * *** *** *** *** *** ***",
        " ** ** * **  * *   *   * * * * *    * ",
        " * * * * * * * **  *   **  *** **   * ",
        " *   * * *  ** *   *   * * * * *    * ",
        " *   * * *   * *** *** * * * * *    * "
    };
    private String[] subtitle = new String[]{
        " * *   * *** * *   * * *** ***",
        " * **  * *   * **  * *  *  *  ",
        " * * * * **  * * * * *  *  ** ",
        " * *  ** *   * *  ** *  *  *  ",
        " * *   * *   * *   * *  *  ***"
    };
    private int panoramaTimer = 0;

    public TitleScreen() {
        this.loadSplashes();
    }

    @Override
    public void update() {
        if (this.logoEffects != null) {
            for (int x = 0; x < this.logoEffects.length; x++) {
                for (int y = 0; y < this.logoEffects[x].length; y++) {
                    this.logoEffects[x][y].updateLogoEffects();
                }
            }
        }

        this.mc.sndManager.playRandomMusic(4);
        this.panoramaTimer++;
    }

    @Override
    protected void keyTyped(char character, int key) {
    }

    @Override
    public void initGui() {
        this.buttons.clear();
        this.buttons.add(new Button(1, this.width / 2 - 100, this.height / 4 + 48, 200, 20, "menu.singleplayer", 64, 128));
        this.buttons.add(new Button(2, this.width / 2 - 100, this.height / 4 + 72, 200, 20, "menu.multiplayer", 80, 128));
        this.buttons.add(new Button(3, this.width / 2 - 100, this.height / 4 + 96, 200, 20, "menu.changelog", 0, 128));
        this.buttons.add(new Button(0, this.width / 2 - 100, this.height / 4 + 120 + 12, 100, 20, "menu.options", 48, 128));
        this.buttons.add(new Button(4, this.width / 2 + 4, this.height / 4 + 120 + 12, 97, 20, "menu.quit", 96, 128));
        this.buttons.add(new Button(5, this.width / 2 + 105, this.height / 4 + 120 + 12, 20, 20, "", "title.credits", 16, 128));
        this.buttons.add(new Button(6, this.width / 2 - 125, this.height / 4 + 120 + 12, 20, 20, "", "options.languages", 0, 160));
        if (this.mc.session == null) {
            this.buttons.get(1).enabled = false;
        }

        this.mc.sndManager.ticksBeforeMusic = 1;
    }

    @Override
    protected void buttonClicked(Button btn) {
        if (btn.id == 0) {
            this.mc.setScreen(new GeneralScreen(this.mc.options));
        }

        if (btn.id == 1) {
            this.mc.setScreen(new WorldSelectScreen(this));
        }

        if (btn.id == 2) {
            this.mc.setScreen(new ServerSelectScreen(this));
        }

        if (btn.id == 3) {
            this.mc.setScreen(new ChangelogScreen(this));
        }

        if (btn.id == 4) {
            this.mc.gameShutdown();
            System.exit(0);
        }

        if (btn.id == 5) {
            this.mc.setScreen(new CreditsScreen(false));
        }

        if (btn.id == 6) {
            this.mc.setScreen(new LanguagePackScreen(this.mc.options));
        }
    }

    private void drawPanorama(int x, int y, float tickDelta) {
        Tessellator tess = Tessellator.instance;
        GL11.glMatrixMode(5889);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GLU.gluPerspective(85.0F, (float)this.width / this.height, 0.05F, 10.0F);
        GL11.glMatrixMode(5888);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(2884);
        GL11.glDepthMask(false);
        GL11.glRotatef(-(this.panoramaTimer + tickDelta) * 0.1F, 0.0F, 1.0F, 0.0F);

        for (int side = 0; side < 6; side++) {
            GL11.glPushMatrix();
            if (side == 1) {
                GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
            }

            if (side == 2) {
                GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
            }

            if (side == 3) {
                GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
            }

            if (side == 4) {
                GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            }

            if (side == 5) {
                GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
            }

            this.mc.textureLoader.bindTexture(this.mc.textureLoader.getTexture("/title/bg/panorama" + side + ".png"));
            tess.begin();
            tess.addTexturedVertex(-1.0, -1.0, 1.0, 0.0F, 0.0F);
            tess.addTexturedVertex(1.0, -1.0, 1.0, 1.0F, 0.0F);
            tess.addTexturedVertex(1.0, 1.0, 1.0, 1.0F, 1.0F);
            tess.addTexturedVertex(-1.0, 1.0, 1.0, 0.0F, 1.0F);
            tess.draw();
            GL11.glPopMatrix();
        }

        GL11.glColorMask(true, true, true, true);
        GL11.glMatrixMode(5889);
        GL11.glPopMatrix();
        GL11.glMatrixMode(5888);
        GL11.glPopMatrix();
        GL11.glEnable(2884);
    }

    public void drawLogo(float tickDelta, boolean sub) {
        String[] arr = sub ? this.subtitle : this.logo;
        if (this.logoEffects == null) {
            this.logoEffects = new TitleScreen.LogoEffectRandomizer[arr[0].length()][arr.length];

            for (int x = 0; x < this.logoEffects.length; x++) {
                for (int y = 0; y < this.logoEffects[x].length; y++) {
                    this.logoEffects[x][y] = new TitleScreen.LogoEffectRandomizer(x, y);
                }
            }
        }

        GL11.glMatrixMode(5889);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        int scaleOffset = 120 * this.mc.rescaler.scaleFactor;
        GLU.gluPerspective(70.0F, (float)this.mc.displayWidth / scaleOffset, 0.05F, 100.0F);
        GL11.glViewport(0, this.mc.displayHeight - scaleOffset, this.mc.displayWidth, scaleOffset);
        GL11.glMatrixMode(5888);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glDisable(2884);
        GL11.glCullFace(1029);
        GL11.glDepthMask(true);

        for (int pass = 0; pass < 3; pass++) {
            GL11.glPushMatrix();
            GL11.glTranslatef(0.4F, 0.6F, -13.0F);
            if (pass == 0) {
                GL11.glClear(256);
                GL11.glTranslatef(0.0F, -0.4F, 0.0F);
                GL11.glScalef(0.98F, 1.0F, 1.0F);
                GL11.glEnable(3042);
                GL11.glBlendFunc(770, 771);
            }

            if (pass == 1) {
                GL11.glDisable(3042);
                GL11.glClear(256);
            }

            if (pass == 2) {
                GL11.glEnable(3042);
                GL11.glBlendFunc(768, 1);
            }

            if (sub) {
                GL11.glScalef(0.5F, -0.5F, 0.5F);
            } else {
                GL11.glScalef(1.0F, -1.0F, 1.0F);
            }

            GL11.glRotatef(15.0F, 1.0F, 0.0F, 0.0F);
            GL11.glScalef(0.89F, 1.0F, 0.4F);
            GL11.glTranslatef(-arr[0].length() * 0.5F, -arr.length * 0.5F + (sub ? 9.0F : 0.0F), 0.0F);
            this.mc.textureLoader.bindTexture(this.mc.textureLoader.getTexture("/terrain.png"));
            if (pass == 0) {
                this.mc.textureLoader.bindTexture(this.mc.textureLoader.getTexture("/misc/black.png"));
            }

            for (int y = 0; y < arr.length; y++) {
                for (int x = 0; x < arr[y].length(); x++) {
                    char character = arr[y].charAt(x);
                    if (character != ' ') {
                        GL11.glPushMatrix();
                        TitleScreen.LogoEffectRandomizer randomizer = this.logoEffects[x][y];
                        float z = (float)(randomizer.prevHeight + (randomizer.height - randomizer.prevHeight) * tickDelta);
                        float scaleFactor = 1.0F;
                        float alpha = 1.0F;
                        float angle = 0.0F;
                        if (pass == 0) {
                            scaleFactor = z * 0.04F + 1.0F;
                            alpha = 1.0F / scaleFactor;
                            z = 0.0F;
                        }

                        GL11.glTranslatef(x, y, z);
                        GL11.glScalef(scaleFactor, scaleFactor, scaleFactor);
                        GL11.glRotatef(angle, 0.0F, 1.0F, 0.0F);
                        if (pass == 0) {
                            renderBlocks.renderShadow(BlockList.stone, alpha);
                        } else {
                            renderBlocks.renderBlocksForLogo(sub ? BlockList.brick : BlockList.stone, alpha);
                        }

                        GL11.glPopMatrix();
                    }
                }
            }

            GL11.glPopMatrix();
        }

        GL11.glDisable(3042);
        GL11.glMatrixMode(5889);
        GL11.glPopMatrix();
        GL11.glMatrixMode(5888);
        GL11.glPopMatrix();
        GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        GL11.glEnable(2884);
    }

    @Override
    public void drawScreen(int x, int y, float tickDelta) {
        this.drawPanorama(x, y, tickDelta);
        this.drawGradient(0, 0, this.width, this.height, 1610941696, -1607454656, 0.3F);
        this.drawLogo(tickDelta, false);
        this.drawLogo(tickDelta, true);
        GL11.glPushMatrix();
        GL11.glTranslatef(this.width / 2 + 90, 70.0F, 0.0F);
        GL11.glRotatef(-20.0F, 0.0F, 0.0F, 1.0F);
        float splashScale = 1.8F - MathHelper.abs(MathHelper.sin((float)(System.currentTimeMillis() % 1000L) / 1000.0F * (float) Math.PI * 2.0F) * 0.1F);
        splashScale = splashScale * 100.0F / (this.fontRenderer.getStringWidth(this.splash) + 32);
        GL11.glScalef(splashScale, splashScale, splashScale);
        this.drawCenteredString(this.fontRenderer, this.splash, 0, -8, 16776960);
        GL11.glPopMatrix();
        String disclaimer = Translator.getInstance().translateKey("gui.credits");
        this.drawString(this.fontRenderer, disclaimer, this.width - this.fontRenderer.getStringWidth(disclaimer) - 2, this.height - 10, 16777215);
        this.drawString(this.fontRenderer, "Minecraft Infinite v1.0", 2, this.height - 10, 16777215);
        this.drawCenteredString(this.fontRenderer, resource, this.width / 2, 10, 16777215);
        super.drawScreen(x, y, tickDelta);
    }

    private void loadSplashes() {
        try {
            ArrayList<String> strings = new ArrayList<>();
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(TitleScreen.class.getResourceAsStream("/title/splashes.txt"), Charset.forName("UTF-8"))
            );
            String line = "";

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.startsWith("##")) {
                    line = line.replaceAll("%%", "§");
                    if (line.length() > 0) {
                        strings.add(line);
                    }
                }
            }

            this.splash = strings.get(random.nextInt(strings.size()));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static class LogoEffectRandomizer {
        public double height;
        public double prevHeight;
        public double dropSpeed;

        public LogoEffectRandomizer(int x, int y) {
            this.height = this.prevHeight = 10 + y + TitleScreen.random.nextDouble() * 32.0 + x;
        }

        public void updateLogoEffects() {
            this.prevHeight = this.height;
            if (this.height > 0.0) {
                this.dropSpeed -= 0.6;
            }

            this.height = this.height + this.dropSpeed;
            this.dropSpeed *= 0.9;
            if (this.height < 0.0) {
                this.height = 0.0;
                this.dropSpeed = 0.0;
            }
        }
    }
}
