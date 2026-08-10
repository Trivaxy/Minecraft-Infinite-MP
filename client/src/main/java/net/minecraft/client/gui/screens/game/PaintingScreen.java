package net.minecraft.client.gui.screens.game;

import net.minecraft.client.gui.button.Button;
import net.minecraft.client.gui.screens.GuiScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.game.entity.hanging.EnumArt;
import net.minecraft.game.entity.hanging.Painting;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.localization.Translator;
import net.minecraft.statistics.StatList;

public final class PaintingScreen extends GuiScreen {
    private int selectedPainting = 1;
    private int xPos;
    private int yPos;
    private int zPos;
    private byte face;

    public PaintingScreen(int x, int y, int z, byte side) {
        this.xPos = x;
        this.yPos = y;
        this.zPos = z;
        this.face = side;
    }

    @Override
    public final void initGui() {
        this.buttons.clear();
        this.buttons
            .add(new Button(0, this.width / 2 - 100, this.height / 4 + 110, 50, 20, "<- " + Translator.getInstance().translateKey("menu.painting.prev")));
        this.buttons
            .add(new Button(1, this.width / 2 + 50, this.height / 4 + 110, 50, 20, Translator.getInstance().translateKey("menu.painting.next") + " ->"));
        this.buttons.add(new Button(2, this.width / 2 - 100, this.height / 4 + 130 + 12, 65, 20, "menu.painting.select"));
        this.buttons.add(new Button(3, this.width / 2 + 36, this.height / 4 + 130 + 12, 65, 20, "gui.cancel"));
        this.buttons.add(new Button(4, this.width / 2 - 34, this.height / 4 + 130 + 12, 68, 20, "menu.painting.random"));
    }

    @Override
    protected final void buttonClicked(Button button) {
        if (button.id == 0) {
            this.selectedPainting--;
            if (this.selectedPainting < 1) {
                this.selectedPainting = EnumArt.values().length;
            }
        }

        if (button.id == 1) {
            this.selectedPainting++;
            if (this.selectedPainting > EnumArt.values().length) {
                this.selectedPainting = 1;
            }
        }

        if (button.id == 2) {
            this.placePainting(this.selectedPainting);
        }

        if (button.id == 3) {
            this.mc.setScreen(null);
            this.mc.setFocus();
        }

        if (button.id == 4) {
            this.placePainting(0);
        }
    }

    @Override
    public final void drawScreen(int x, int y, float tickDelta) {
        this.drawBackground();
        this.drawCenteredString(this.fontRenderer, Translator.getInstance().translateKey("menu.painting"), this.width / 2, 20, 16777215);
        this.drawCenteredString(this.fontRenderer, this.getPaintingName(this.selectedPainting), this.width / 2, this.height / 4 + 115, 16777215);
        this.renderPainting(this.selectedPainting);
        super.drawScreen(x, y, tickDelta);
    }

    private void placePainting(int id) {
        Painting paintingToPlace = new Painting(this.mc.world, this.xPos, this.yPos, this.zPos, this.face, id);
        if (!this.mc.world.isOnline && paintingToPlace.onValidSurface()) {
            this.mc.world.spawnEntity(paintingToPlace);
            LocalPlayer player = this.mc.player;
            ItemStack currItem = player.inventory.getCurrentItem();
            int slot = player.inventory.currentSlot;
            if (player.inventory.getOffHandItem() != null && player.inventory.getOffHandItem().getItem() == ItemList.painting) {
                currItem = player.inventory.getOffHandItem();
                slot = 44;
            }

            if (player.properties.gamemode != 1) {
                currItem.stackSize--;
                if (currItem.stackSize <= 0) {
                    player.inventory.setSlotContents(slot, null);
                }
            }

            player.addStat(StatList.useStats[currItem.itemID], 1);
        }

        this.mc.setScreen(null);
        this.mc.setFocus();
    }

    public String getPaintingName(int id) {
        EnumArt[] availableArts = EnumArt.values();
        return "''" + Translator.getInstance().translateKey("painting." + availableArts[id - 1].title) + "''";
    }

    private void renderPainting(int painting) {
        EnumArt[] paintingList = EnumArt.values();
        this.mc.textureLoader.bindTexture(this.mc.textureLoader.getTexture("/art/" + paintingList[painting - 1].title + ".png"));
        int scale = this.mc.rescaler.scaleFactor - 1;
        int offset = 8;
        if (paintingList[painting - 1].height >= 48) {
            scale = this.mc.rescaler.scaleFactor / 2;
            offset = 4;
        }

        int yOffset = (paintingList[painting - 1].height / 2 + 2) * scale - 44;
        int xOffset = (paintingList[painting - 1].width / 2 + 2) * scale - offset;
        this.drawPaintingTexture(
            this.width / 2 - xOffset,
            this.height / 4 - yOffset,
            paintingList[painting - 1].width * scale,
            paintingList[painting - 1].height * scale,
            paintingList[painting - 1].width,
            paintingList[painting - 1].height
        );
    }

    private void drawPaintingTexture(float x, float y, int startX, int startY, int endX, int endY) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.begin();
        tessellator.setColor(1.0F, 1.0F, 1.0F);
        tessellator.addTexturedVertex(x, y + startY, this.zLevel, 0.0F, 1.0F);
        tessellator.addTexturedVertex(x + startX, y + startY, this.zLevel, 1.0F, 1.0F);
        tessellator.addTexturedVertex(x + startX, y, this.zLevel, 1.0F, 0.0F);
        tessellator.addTexturedVertex(x, y, this.zLevel, 0.0F, 0.0F);
        tessellator.draw();
    }
}
