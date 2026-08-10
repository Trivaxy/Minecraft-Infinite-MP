package net.minecraft.client.gui.screens;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.button.Button;
import net.minecraft.client.localization.Font;
import net.minecraft.client.options.OptionValues;
import net.minecraft.client.render.Tessellator;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiScreen extends GuiComponent {
    public Minecraft mc;
    protected List<Button> buttons = new ArrayList<>();
    protected Font fontRenderer;
    protected Button selectedButton = null;
    public int width;
    public int height;
    private int viewportTexture;

    public void drawScreen(int width, int height, float tickDelta) {
        for (int id = 0; id < this.buttons.size(); id++) {
            this.buttons.get(id).drawButton(this.mc, width, height);
        }
    }

    protected void keyTyped(char character, int key) {
        if (key == 1) {
            this.mc.setScreen(null);
            this.mc.setFocus();
        }
    }

    protected void mouseClicked(int x, int y, int click) {
        if (click == 0) {
            for (int id = 0; id < this.buttons.size(); id++) {
                Button button = this.buttons.get(id);
                if (button.mousePressed(this.mc, x, y)) {
                    this.selectedButton = button;
                    this.buttonClicked(button);
                }
            }
        }

        if (click == 1) {
            for (int id = 0; id < this.buttons.size(); id++) {
                Button button = this.buttons.get(id);
                if (button.mousePressed(this.mc, x, y)) {
                    this.selectedButton = button;
                    this.altClick(button);
                }
            }
        }
    }

    protected void buttonClicked(Button button) {
    }

    protected void altClick(Button button) {
        this.buttonClicked(button);
    }

    protected void mouseReleased(int x, int y, int click) {
        if (this.selectedButton != null && click == 0) {
            this.selectedButton.mouseReleased(x, y);
            this.selectedButton = null;
        }
    }

    public void setResolution(Minecraft mc, int wdth, int hght) {
        this.mc = mc;
        this.fontRenderer = mc.fontRenderer;
        this.width = wdth;
        this.height = hght;
        this.buttons.clear();
        this.initGui();
        int screenWidth = mc.displayWidth;
        int screenHeight = mc.displayHeight;
        IntBuffer buffer = BufferUtils.createIntBuffer(1);
        GL11.glGenTextures(buffer);
        this.viewportTexture = buffer.get(0);
        this.mc.textureLoader.bindTexture(this.viewportTexture);
        GL11.glTexImage2D(3553, 0, 6407, screenWidth, screenHeight, 0, 6407, 5121, (ByteBuffer)null);
        GL11.glTexParameteri(3553, 10241, 9729);
        GL11.glTexParameteri(3553, 10240, 9729);
    }

    public void initGui() {
    }

    public void handleInput() {
        while (Mouse.next()) {
            this.handleMouseInput();
        }

        while (Keyboard.next()) {
            this.handleKeyboardInput();
        }
    }

    public void handleMouseInput() {
        if (Mouse.getEventButtonState()) {
            int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
            int y = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
            this.mouseClicked(x, y, Mouse.getEventButton());
        } else {
            int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
            int y = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
            this.mouseReleased(x, y, Mouse.getEventButton());
        }

        this.scroll(Mouse.getDWheel());
    }

    public void scroll(int scrollValue) {
    }

    public void handleKeyboardInput() {
        if (Keyboard.getEventKeyState()) {
            if (Keyboard.getEventKey() == 87) {
                this.mc.toggleFullscreen();
                return;
            }

            this.keyTyped(Keyboard.getEventCharacter(), Keyboard.getEventKey());
        }
    }

    public void update() {
    }

    public void onClose() {
        GL11.glDeleteTextures(this.viewportTexture);
    }

    public void drawBackground() {
        if (this.mc.world != null) {
            if (OptionValues.blurAmount > 0.0F) {
                this.applyBlur();
            }

            this.drawGradient(0, 0, this.width, this.height, 1610941696, -1607454656, 1.0F);
        } else {
            this.renderDirtBackground();
        }
    }

    protected void renderDirtBackground() {
        GL11.glDisable(2896);
        GL11.glDisable(2912);
        Tessellator tess = Tessellator.instance;
        this.mc.textureLoader.bindTexture(this.mc.textureLoader.getTexture("/gui/background/background.png"));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float offset = 32.0F;
        tess.begin();
        tess.setColor(4210752);
        tess.addTexturedVertex(0.0, this.height, 0.0, 0.0F, this.height / offset);
        tess.addTexturedVertex(this.width, this.height, 0.0, this.width / offset, this.height / offset);
        tess.addTexturedVertex(this.width, 0.0, 0.0, this.width / offset, 0.0F);
        tess.addTexturedVertex(0.0, 0.0, 0.0, 0.0F, 0.0F);
        tess.draw();
    }

    protected void applyBlur() {
        this.mc.textureLoader.bindTexture(this.viewportTexture);
        GL11.glTexParameteri(3553, 10242, 33071);
        GL11.glTexParameteri(3553, 10243, 33071);
        GL11.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, this.mc.displayWidth, this.mc.displayHeight);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glColorMask(true, true, true, false);
        Tessellator tessellator = Tessellator.instance;
        int blurPasses = 8;
        float spread = OptionValues.blurAmount * 4.0F;
        this.renderBlurPass(tessellator, blurPasses, spread / this.mc.displayWidth, true);
        GL11.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, this.mc.displayWidth, this.mc.displayHeight);
        this.renderBlurPass(tessellator, blurPasses, spread / this.mc.displayHeight, false);
        GL11.glColorMask(true, true, true, true);
    }

    private void renderBlurPass(Tessellator tess, int passes, float texelOffset, boolean horizontal) {
        for (int i = 0; i < passes; i++) {
            float alpha = 1.0F / (i + 1);
            float offset = (i - passes / 2) * texelOffset;
            float uOffset = horizontal ? offset : 0.0F;
            float vOffset = horizontal ? 0.0F : offset;
            tess.begin();
            tess.setColorAlpha(1.0F, 1.0F, 1.0F, alpha);
            tess.addTexturedVertex(this.width, this.height, this.zLevel, 1.0F + uOffset, 0.0F + vOffset);
            tess.addTexturedVertex(this.width, 0.0, this.zLevel, 1.0F + uOffset, 1.0F + vOffset);
            tess.addTexturedVertex(0.0, 0.0, this.zLevel, 0.0F + uOffset, 1.0F + vOffset);
            tess.addTexturedVertex(0.0, this.height, this.zLevel, 0.0F + uOffset, 0.0F + vOffset);
            tess.draw();
        }
    }

    public boolean shouldPause() {
        return true;
    }

    public void confirmClicked(boolean check, int id) {
    }

    public static String getClipboardString() {
        try {
            Transferable transfer = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            if (transfer != null && transfer.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return (String)transfer.getTransferData(DataFlavor.stringFlavor);
            }
        } catch (Exception var2) {
        }

        return "";
    }

    public static void setClipboardString(String str) {
        try {
            StringSelection selection = new StringSelection(str);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
        } catch (Exception var2) {
        }
    }
}
