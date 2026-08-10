package net.minecraft.client.gui.screens.game;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshotter;
import net.minecraft.client.gui.button.Button;
import net.minecraft.client.gui.screens.GuiScreen;
import net.minecraft.client.render.Tessellator;
import net.minecraft.localization.Translator;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class PhotoMode extends GuiScreen {
    private Button rotateLeft;
    private Button rotateRight;
    private Button centerScreen;
    private Button takeScreenshot;
    private Button exit;
    private boolean shouldScreenshot = false;
    private boolean showHelpText = true;
    private float rotation = 0.0F;
    private float lastRotation = 0.0F;
    private float lastEndRotation;
    private float rotationGoal = 0.0F;
    private float tilt = 30.0F;
    private float lastTilt = 30.0F;
    private float tiltGoal = 30.0F;
    private float lastEndTilt;
    private float zoom = 1.0F;
    private float lastZoom = 1.0F;
    private float zoomGoal = 1.0F;
    private float panX = 0.0F;
    private float lastPanX = 0.0F;
    private float lastEndPanX = 0.0F;
    private float panXGoal = 0.0F;
    private float panY = 0.0F;
    private float lastPanY = 0.0F;
    private float lastEndPanY = 0.0F;
    private float panYGoal = 0.0F;
    private int mouseButton;
    private int mouseDownX;
    private int mouseDownY;
    private boolean isMouseDown = false;
    private long lastUpdateTime = 0L;

    public float getRotation(float renderPartialTicks) {
        return this.lastRotation + (this.rotation - this.lastRotation) * renderPartialTicks;
    }

    public float getTilt(float renderPartialTicks) {
        return this.lastTilt + (this.tilt - this.lastTilt) * renderPartialTicks;
    }

    public float getZoom(float renderPartialTicks) {
        return this.lastZoom + (this.zoom - this.lastZoom) * renderPartialTicks;
    }

    public float getPanX(float renderPartialTicks) {
        return this.lastPanX + (this.panX - this.lastPanX) * renderPartialTicks;
    }

    public float getPanY(float renderPartialTicks) {
        return this.lastPanY + (this.panY - this.lastPanY) * renderPartialTicks;
    }

    @Override
    public void initGui() {
        this.exit = new Button(5, this.width - 130, this.height - 25, 120, 20, "gui.exit");
        this.rotateLeft = new Button(0, this.width / 2 - 60 - 2 - 20, this.height - 25, 20, 20, "<");
        this.rotateRight = new Button(1, this.width / 2 + 60 + 2, this.height - 25, 20, 20, ">");
        this.takeScreenshot = new Button(4, 10, this.height - 25, 120, 20, "gui.photomode.screenshot");
        this.centerScreen = new Button(10, this.width / 2 - 60, this.height - 25, 120, 20, "gui.photomode.center");
        this.buttons.add(this.centerScreen);
        this.buttons.add(this.rotateLeft);
        this.buttons.add(this.rotateRight);
        this.buttons.add(this.takeScreenshot);
        this.buttons.add(this.exit);
        this.mc.world.saveAll();
    }

    @Override
    protected void buttonClicked(Button guibutton) {
        if (guibutton == this.rotateLeft) {
            this.rotationGoal += 0.5F;
        } else if (guibutton == this.rotateRight) {
            this.rotationGoal -= 0.5F;
        } else if (guibutton == this.takeScreenshot) {
            this.shouldScreenshot = true;
        } else if (guibutton == this.exit) {
            this.mc.setScreen(null);
        } else if (guibutton == this.centerScreen) {
            this.rotationGoal = 0.0F;
            this.tiltGoal = 30.0F;
            this.panXGoal = 0.0F;
            this.panYGoal = 0.0F;
        }
    }

    @Override
    public void drawScreen(int x, int y, float renderPartialTicks) {
        GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        GL11.glClear(256);
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0, this.mc.rescaler.getScaledWidth(), this.mc.rescaler.getScaledHeight(), 0.0, 1000.0, 3000.0);
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
        long currentTime = System.currentTimeMillis();
        this.lastRotation = this.rotation;
        if (this.rotation != this.rotationGoal) {
            this.rotation = this.rotation + ((this.rotationGoal - this.rotation) * 0.02F + (this.rotationGoal - this.rotation) * 0.02F * renderPartialTicks);
            if (Math.abs(this.rotation - this.rotationGoal) < 5.0E-4F) {
                this.rotation = this.rotationGoal;
            }
        }

        this.lastTilt = this.tilt;
        if (this.tilt != this.tiltGoal) {
            this.tilt = this.tilt + ((this.tiltGoal - this.tilt) * 0.02F + (this.tiltGoal - this.tilt) * 0.02F * renderPartialTicks);
            if (Math.abs(this.tilt - this.tiltGoal) < 0.01F) {
                this.tilt = this.tiltGoal;
            }
        }

        if (currentTime > this.lastUpdateTime + 10L) {
            this.lastZoom = this.zoom;
            if (this.zoom != this.zoomGoal) {
                this.zoom = this.zoom + ((this.zoomGoal - this.zoom) * 0.02F + (this.zoomGoal - this.zoom) * 0.02F * renderPartialTicks);
                if (Math.abs(this.zoom - this.zoomGoal) < 5.0E-4F) {
                    this.zoom = this.zoomGoal;
                }
            }

            this.lastPanX = this.panX;
            if (this.panX != this.panXGoal) {
                this.panX = this.panX + (this.panXGoal - this.panX) * 0.4F;
                if (Math.abs(this.panX - this.panXGoal) < 0.01F) {
                    this.panX = this.panXGoal;
                }
            }

            this.lastPanY = this.panY;
            if (this.panY != this.panYGoal) {
                this.panY = this.panY + (this.panYGoal - this.panY) * 0.4F;
                if (Math.abs(this.panY - this.panYGoal) < 0.01F) {
                    this.panY = this.panYGoal;
                }
            }

            this.lastUpdateTime = currentTime;
        }

        if (!this.shouldScreenshot) {
            byte offset = 32;
            this.overlayBackground(this.height - offset, this.height, 255, 255);
            this.renderGradient(offset);
            super.drawScreen(x, y, renderPartialTicks);
            if (this.showHelpText) {
                this.drawCenteredString(this.fontRenderer, Translator.getInstance().translateKey("gui.photomode.help"), this.width / 2, 2, 16777215);
            }
        } else {
            Screenshotter.takeScreenshot(Minecraft.getMinecraftDir(), this.mc.displayWidth, this.mc.displayHeight);
            this.shouldScreenshot = false;
        }
    }

    @Override
    public void scroll(int scrollValue) {
        if (scrollValue < 0) {
            this.zoomGoal -= 0.25F;
        } else if (scrollValue > 0) {
            this.zoomGoal += 0.25F;
        }

        this.zoomGoal = MathHelper.clamp(this.zoomGoal, 0.0F, 10.0F);
    }

    @Override
    public void mouseClicked(int x, int y, int button) {
        super.mouseClicked(x, y, button);
        if (!this.isMouseDown && this.selectedButton == null && y < this.height - 32) {
            this.isMouseDown = true;
            this.mouseDownX = x;
            this.mouseDownY = y;
            this.lastEndRotation = this.rotation;
            this.lastEndTilt = this.tilt;
            this.lastEndPanX = this.panX;
            this.lastEndPanY = this.panY;
            this.mouseButton = button;
        }
    }

    @Override
    public void mouseReleased(int x, int y, int mouseButton) {
        super.mouseReleased(x, y, mouseButton);
        if (this.isMouseDown && mouseButton != -1) {
            this.isMouseDown = false;
        } else if (this.isMouseDown) {
            switch (this.mouseButton) {
                case 0:
                    float div = (float)Math.pow(2.0, this.zoom) / this.zoom;
                    this.panXGoal = this.lastEndPanX + (x - this.mouseDownX) / div;
                    this.panYGoal = this.lastEndPanY + (y - this.mouseDownY) / div;
                    break;
                case 1:
                    this.rotationGoal = this.lastEndRotation + (x - this.mouseDownX) / 128.0F;
                    this.tiltGoal = Math.max(0.0F, Math.min(this.lastEndTilt + (y - this.mouseDownY), 90.0F));
            }

            this.showHelpText = false;
        }
    }

    @Override
    protected void keyTyped(char character, int key) {
        if (key == 1 || key == this.mc.options.isometricKey.key) {
            this.mc.setScreen(null);
            this.mc.setFocus();
        }
    }

    private void overlayBackground(int startX, int startY, int endX, int endY) {
        Tessellator tess = Tessellator.instance;
        this.mc.textureLoader.bindTexture(this.mc.textureLoader.getTexture("/gui/background/background.png"));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glTranslatef(0.0F, 0.0F, 100.0F);
        float offset = 32.0F;
        tess.begin();
        tess.setColorAlpha(4210752, endY);
        tess.addTexturedVertex(0.0, startY, 0.0, 0.0F, startY / offset);
        tess.addTexturedVertex(this.width, startY, 0.0, this.width / offset, startY / offset);
        tess.setColorAlpha(4210752, endX);
        tess.addTexturedVertex(this.width, startX, 0.0, this.width / offset, startX / offset);
        tess.addTexturedVertex(0.0, startX, 0.0, 0.0F, startX / offset);
        tess.draw();
    }

    private void renderGradient(float offset) {
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3008);
        GL11.glShadeModel(7425);
        GL11.glDisable(3553);
        Tessellator tess = Tessellator.instance;
        tess.begin();
        tess.setColorAlpha(0, 255);
        tess.addTexturedVertex(0.0, this.height - offset, 0.0, 0.0F, 1.0F);
        tess.addTexturedVertex(this.width, this.height - offset, 0.0, 1.0F, 1.0F);
        tess.setColorAlpha(0, 0);
        tess.addTexturedVertex(this.width, this.height - offset - 4.0F, 0.0, 1.0F, 0.0F);
        tess.addTexturedVertex(0.0, this.height - offset - 4.0F, 0.0, 0.0F, 0.0F);
        tess.draw();
        GL11.glEnable(3553);
        GL11.glShadeModel(7424);
        GL11.glEnable(3008);
        GL11.glDisable(3042);
    }
}
