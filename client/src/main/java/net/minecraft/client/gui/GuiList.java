package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.Tessellator;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public abstract class GuiList extends GuiComponent {
    protected final Minecraft mc;
    public int width;
    public int height;
    public final int top;
    public final int bottom;
    public int slotHeight;
    protected int listOffset;
    protected int endWidth = 110;
    protected int startWidth = 110;
    protected int slotOffset = 4;
    protected int selectedElement = -1;
    private long lastClicked = 0L;
    protected float initialClickY = -2.0F;
    private float scrollMultiplier;
    protected float amountScrolled;
    protected boolean canInteract;

    public GuiList(Minecraft mc, int width, int height, int top, int bottom, int slot) {
        this.mc = mc;
        this.width = width;
        this.height = height;
        this.top = top;
        this.bottom = bottom;
        this.slotHeight = slot;
    }

    protected abstract int listSize();

    protected abstract void elementClicked(int var1, boolean var2);

    protected abstract boolean isSelected(int var1);

    protected int contentSize() {
        return this.listSize() * this.slotHeight + this.listOffset;
    }

    protected abstract void drawBackground();

    protected abstract void drawSlot(int var1, int var2, int var3, int var4, Tessellator var5);

    protected void onInteraction(int x, int y, Tessellator tessellator) {
    }

    protected void onClick(int x, int y) {
    }

    public void rightClick(int btn, boolean selected) {
    }

    protected void mouseOver(int x, int y) {
    }

    public int getSlotBounds(int x, int y) {
        int minX = this.width / 2 - 110;
        int maxX = this.width / 2 + 110;
        int maxY = y - this.top - this.listOffset + (int)this.amountScrolled - 4;
        int halfY = maxY / this.slotHeight;
        return x >= minX && x <= maxX && halfY >= 0 && maxY >= 0 && halfY < this.listSize() ? halfY : -1;
    }

    private void bindAmountScrolled() {
        int height = this.contentSize() - (this.bottom - this.top - 4);
        if (height < 0) {
            height /= 2;
        }

        if (this.amountScrolled < 0.0F) {
            this.amountScrolled = 0.0F;
        }

        if (this.amountScrolled > height) {
            this.amountScrolled = height;
        }
    }

    public void draw(int width, int height, float tickDelta) {
        this.drawBackground();
        if (!Mouse.isButtonDown(0) && !Mouse.isButtonDown(1)) {
            this.initialClickY = -1.0F;
        } else {
            this.clickScreen(width, height, Mouse.isButtonDown(1));
        }

        this.bindAmountScrolled();
        GL11.glDisable(2896);
        GL11.glDisable(2912);
        Tessellator tess = Tessellator.instance;
        GL11.glBindTexture(3553, this.mc.textureLoader.getTexture("/gui/background/background.png"));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float size = 32.0F;
        if (this.mc.world == null) {
            tess.begin();
            tess.setColor(2105376);
            tess.addTexturedVertex(0.0, this.bottom, 0.0, 0.0F / size, (this.bottom + (int)this.amountScrolled) / size);
            tess.addTexturedVertex(this.width, this.bottom, 0.0, this.width / size, (this.bottom + (int)this.amountScrolled) / size);
            tess.addTexturedVertex(this.width, this.top, 0.0, this.width / size, (this.top + (int)this.amountScrolled) / size);
            tess.addTexturedVertex(0.0, this.top, 0.0, 0.0F / size, (this.top + (int)this.amountScrolled) / size);
            tess.draw();
        }

        int x = this.width / 2 - 92 - 16;
        int y = this.top + 4 - (int)this.amountScrolled;
        if (this.canInteract) {
            this.onInteraction(x, y, Tessellator.instance);
        }

        this.renderSlots(tess);
        this.drawScrollbar();
        this.overlayBackground(0, this.top, 255, 255);
        this.overlayBackground(this.bottom, this.height, 255, 255);
        this.drawEdges(tess);
        this.mouseOver(width, height);
        GL11.glEnable(3553);
        GL11.glShadeModel(7424);
        GL11.glEnable(3008);
        GL11.glDisable(3042);
    }

    protected void clickScreen(int width, int height, boolean rightClick) {
        int minWidth = this.width - 13;
        int maxWidth = minWidth + 6;
        if (this.initialClickY == -1.0F) {
            boolean scrollToTop = true;
            if (height >= this.top && height <= this.bottom) {
                int leftBound = this.width / 2 - this.startWidth;
                int rightBound = this.width / 2 + this.endWidth;
                int minHeight = (height - this.top + (int)this.amountScrolled - 2) / this.slotHeight;
                int heightOffset = height - this.top - this.listOffset + (int)this.amountScrolled - 4;
                if (width >= leftBound && width <= rightBound && minHeight >= 0 && minHeight < this.listSize()) {
                    boolean doubleClick = minHeight == this.selectedElement && System.currentTimeMillis() - this.lastClicked < 250L;
                    if (rightClick) {
                        this.rightClick(minHeight, doubleClick);
                    } else {
                        this.elementClicked(minHeight, doubleClick);
                    }

                    this.selectedElement = minHeight;
                    this.lastClicked = System.currentTimeMillis();
                } else if (width >= leftBound && width <= rightBound && heightOffset < 0) {
                    scrollToTop = false;
                }

                this.onClick(width - leftBound, height - this.top + (int)this.amountScrolled - 4);
                if (width >= minWidth && width <= maxWidth && height >= this.top + 6 && height <= this.bottom - 6) {
                    this.scrollMultiplier = -1.0F;
                    int minY = this.contentSize() - (this.bottom - this.top - 4);
                    if (minY < 1) {
                        minY = 1;
                    }

                    int maxY = (this.bottom - this.top) * (this.bottom - this.top) / this.contentSize();
                    if (maxY < 32) {
                        maxY = 32;
                    }

                    if (maxY > this.bottom - this.top - 8) {
                        maxY = this.bottom - this.top - 8;
                    }

                    this.scrollMultiplier = this.scrollMultiplier / ((float)(this.bottom - this.top - maxY) / minY);
                } else {
                    this.scrollMultiplier = 1.0F;
                }

                if (scrollToTop) {
                    this.initialClickY = height;
                } else {
                    this.initialClickY = -2.0F;
                }
            } else {
                this.initialClickY = -2.0F;
            }
        } else if (this.initialClickY >= 0.0F) {
            this.amountScrolled = this.amountScrolled - (height - this.initialClickY) * this.scrollMultiplier;
            this.initialClickY = height;
        }
    }

    public void scrollMouse() {
        int scroll = Mouse.getEventDWheel();
        if (scroll != 0) {
            if (scroll > 0) {
                scroll = -1;
            } else if (scroll < 0) {
                scroll = 1;
            }

            this.amountScrolled = this.amountScrolled + scroll * this.slotHeight / 2;
        }
    }

    private void drawEdges(Tessellator tess) {
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3008);
        GL11.glShadeModel(7425);
        GL11.glDisable(3553);
        tess.begin();
        tess.setColorAlpha(0, 0);
        tess.addTexturedVertex(0.0, this.top + 4, 0.0, 0.0F, 1.0F);
        tess.addTexturedVertex(this.width, this.top + 4, 0.0, 1.0F, 1.0F);
        tess.setColorAlpha(0, 255);
        tess.addTexturedVertex(this.width, this.top, 0.0, 1.0F, 0.0F);
        tess.addTexturedVertex(0.0, this.top, 0.0, 0.0F, 0.0F);
        tess.setColorAlpha(0, 255);
        tess.addTexturedVertex(0.0, this.bottom, 0.0, 0.0F, 1.0F);
        tess.addTexturedVertex(this.width, this.bottom, 0.0, 1.0F, 1.0F);
        tess.setColorAlpha(0, 0);
        tess.addTexturedVertex(this.width, this.bottom - 4, 0.0, 1.0F, 0.0F);
        tess.addTexturedVertex(0.0, this.bottom - 4, 0.0, 0.0F, 0.0F);
        tess.draw();
    }

    private void renderSlots(Tessellator tess) {
        for (int minHeight = 0; minHeight < this.listSize(); minHeight++) {
            int minY = this.width / 2 - 92 - 16;
            int maxY = this.top + this.slotOffset + minHeight * this.slotHeight - (int)this.amountScrolled;
            int heightOffset = this.slotHeight - 4;
            if (maxY > this.top - 20 && maxY < this.bottom && this.mc.currentScreen != null) {
                if (this.isSelected(minHeight)) {
                    this.drawSelectionBox(maxY, tess);
                }

                this.drawSlot(minHeight, minY, maxY, heightOffset, tess);
            }
        }
    }

    protected void drawSelectionBox(int maxY, Tessellator tess) {
        int minX = this.width / 2 - this.startWidth;
        int maxX = this.width / 2 + this.endWidth;
        int heightOffset = this.slotHeight - 4;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(3553);
        tess.begin();
        tess.setColor(8421504);
        tess.addTexturedVertex(minX, maxY + heightOffset + 2, 0.0, 0.0F, 1.0F);
        tess.addTexturedVertex(maxX, maxY + heightOffset + 2, 0.0, 1.0F, 1.0F);
        tess.addTexturedVertex(maxX, maxY - 2, 0.0, 1.0F, 0.0F);
        tess.addTexturedVertex(minX, maxY - 2, 0.0, 0.0F, 0.0F);
        tess.setColor(0);
        tess.addTexturedVertex(minX + 1, maxY + heightOffset + 1, 0.0, 0.0F, 1.0F);
        tess.addTexturedVertex(maxX - 1, maxY + heightOffset + 1, 0.0, 1.0F, 1.0F);
        tess.addTexturedVertex(maxX - 1, maxY - 1, 0.0, 1.0F, 0.0F);
        tess.addTexturedVertex(minX + 1, maxY - 1, 0.0, 0.0F, 0.0F);
        tess.draw();
        GL11.glEnable(3553);
    }

    private void drawScrollbar() {
        int minY = this.contentSize() - (this.bottom - this.top - 4);
        if (minY > 0) {
            int topp = this.top + 6;
            int bottomm = this.bottom - 6;
            int tall = bottomm - topp;
            int maxHeight = tall * tall / this.contentSize();
            if (maxHeight < 32) {
                maxHeight = 32;
            }

            if (maxHeight > tall) {
                maxHeight = tall;
            }

            int minHeight = (int)this.amountScrolled * (tall - maxHeight) / minY + topp;
            if (minHeight < topp) {
                minHeight = topp;
            }

            if (minHeight > bottomm) {
                minHeight = bottomm;
            }

            int x = this.width - 13;
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glBindTexture(3553, this.mc.textureLoader.getTexture("/gui/gui.png"));
            this.drawGraphic(x + 2, topp, 240, 0, 9, 4);
            int imgheight = 248;
            int position = 0;

            for (int i1 = tall - 8; i1 > 0; i1 -= imgheight) {
                this.drawGraphic(x + 2, topp + position * imgheight + 4, 240, 4, 9, Math.min(i1, imgheight));
                position++;
            }

            this.drawGraphic(x + 2, topp + tall - 5, 240, 252, 9, 4);
            this.drawGraphic(x, minHeight, 247, 0, 9, 4);
            int var11 = 13;
            position = 0;

            for (int i1 = maxHeight - 8; i1 > 0; i1 -= var11) {
                this.drawGraphic(x, minHeight + position * var11 + 4, 247, 4, 9, Math.min(i1, var11));
                position++;
            }

            this.drawGraphic(x, minHeight + maxHeight - 4, 247, 17, 9, 4);
        }
    }

    private void overlayBackground(int startX, int startY, int endX, int endY) {
        Tessellator tess = Tessellator.instance;
        GL11.glBindTexture(3553, this.mc.textureLoader.getTexture("/gui/background/background.png"));
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
}
