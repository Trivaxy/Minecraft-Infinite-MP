package net.minecraft.client.gui.screens.container;

import com.mojang.nbt.tag.special.CompoundTag;
import com.mojang.nbt.tag.special.ListTag;
import java.util.Collection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.button.Button;
import net.minecraft.client.gui.button.InvisibleButton;
import net.minecraft.client.gui.screens.GuiScreen;
import net.minecraft.client.gui.screens.container.inventory.CreativeScreen;
import net.minecraft.client.gui.screens.container.inventory.InventoryScreen;
import net.minecraft.client.options.OptionValues;
import net.minecraft.client.options.OptionsList;
import net.minecraft.client.render.Lighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.game.container.Container;
import net.minecraft.game.container.inventory.PlayerInventory;
import net.minecraft.game.container.slot.Slot;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.item.misc.WrittenBook;
import net.minecraft.game.item.potion.Effect;
import net.minecraft.game.item.potion.EffectData;
import net.minecraft.game.item.potion.EffectList;
import net.minecraft.game.texture.Icon;
import net.minecraft.localization.Translator;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public abstract class ContainerScreen extends GuiScreen {
    protected static ItemRenderer itemRenderer = new ItemRenderer(Minecraft.getMinecraft());
    public Container container;
    protected Button closeButton;
    protected int contWidth = 176;
    protected int contHeight = 180;
    protected int leftBound;
    protected int topBound;
    protected int txtColor = 4210752;
    protected int counter = -1;
    protected int lastAvailableSlot;
    protected int horizontalOffset = 0;
    protected int verticalOffset = 0;
    private boolean keepContainerOpen = false;

    public ContainerScreen(Container container) {
        this.container = container;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.mc.player.openContainer = this.container;
        this.leftBound = (this.width - this.contWidth) / 2;
        this.topBound = (this.height - this.contHeight) / 2;
        int guiTop = this.topBound + this.contHeight - 92;
        this.buttons.clear();
        this.buttons.add(this.closeButton = new InvisibleButton(0, this.width / 2 + 72, guiTop, 10, 10, "x", "gui.close"));
    }

    @Override
    protected void buttonClicked(Button button) {
        if (button.enabled && button == this.closeButton) {
            this.mc.setScreen(null);
        }
    }

    @Override
    public void drawScreen(int width, int height, float tickDelta) {
        this.drawBackground();
        int x = (this.width - this.contWidth) / 2;
        int y = (this.height - this.contHeight) / 2;
        this.drawBackgroundLayer(tickDelta);
        GL11.glPushMatrix();
        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        Lighting.enable();
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 0.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(32826);
        Slot lastSlot = null;

        for (int slotIndex = 0; slotIndex < this.container.slots.size(); slotIndex++) {
            Slot slot = this.container.slots.get(slotIndex);
            this.drawSlot(slot);
            if (this.mouseOverSlot(slot, width, height) && (!(this instanceof InventoryScreen) || !this.skipSlot(slot))) {
                lastSlot = slot;
                this.highlightSlot(slot);
                this.lastAvailableSlot = slot.slotNumber;
            }
        }

        GL11.glDisable(32826);
        Lighting.disable();
        GL11.glDisable(2896);
        GL11.glDisable(2929);
        this.drawForegroundLayer();
        GL11.glPushMatrix();
        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        Lighting.enable();
        GL11.glPopMatrix();
        GL11.glEnable(32826);
        GL11.glEnable(2896);
        GL11.glEnable(2929);
        this.drawStack(x, y, width, height);
        this.animateShiftClick();
        GL11.glDisable(32826);
        Lighting.disable();
        GL11.glPopMatrix();
        super.drawScreen(width, height, tickDelta);
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 0.0F);
        GL11.glDisable(2896);
        GL11.glDisable(2929);
        this.renderTooltip(width, height, x, y, lastSlot);
        GL11.glEnable(2929);
        GL11.glPopMatrix();
    }

    protected void renderTooltip(int width, int height, int x, int y, Slot lastSlot) {
        if (this.mc.player.inventory.getItemStack() == null && lastSlot != null && lastSlot.hasStack()) {
            String itemName = Translator.getInstance().translateNamedKey(lastSlot.getStack().getItemName(lastSlot.getStack().getItemDamage())).trim();
            if (lastSlot.getStack().itemID == ItemList.writtenBook.id) {
                itemName = ((WrittenBook)lastSlot.getStack().getItem()).getItemName(lastSlot.getStack());
            }

            if (lastSlot.getStack().hasDisplayName()) {
                itemName = lastSlot.getStack().getDisplayName();
            }

            if (OptionValues.showFPS) {
                itemName = itemName + " " + lastSlot.getStack().itemID + ":" + lastSlot.getStack().itemDmg;
            }

            if (itemName.length() > 0) {
                int slotX = width - x + 12;
                int slotY = height - y - 12;
                int nameWidth = this.fontRenderer.getStringWidth(itemName);
                int stringHeight = -11;
                int boxStart = 0;

                for (int i = 0; i < lastSlot.getStack().getItemInfoLength(); i++) {
                    String moreinfo = lastSlot.getStack().getItemInformation(i);
                    String translatedString = Translator.getInstance().translateKey(moreinfo.replaceAll("\\d", "").trim());
                    translatedString = translatedString.replaceAll(":", "");
                    String numbersOnly = moreinfo.replaceAll("[^\\d:]", "") + " ";
                    int descLength = this.mc.fontRenderer.getStringWidth(numbersOnly + translatedString);
                    if (!moreinfo.isEmpty()) {
                        boxStart += 10;
                        if (nameWidth < descLength) {
                            nameWidth = descLength;
                        }
                    }
                }

                ItemStack stackInSlot = lastSlot.getStack();
                if ((stackInSlot.itemID == ItemList.backpack.id || stackInSlot.itemID == ItemList.quiver.id)
                    && stackInSlot.hasTag()
                    && stackInSlot.getTag().getTagList("Items").size() > 0) {
                    this.renderBackPackPreview(stackInSlot, itemName, width, height, nameWidth);
                } else {
                    this.drawGradient(slotX - 3, slotY - 3, slotX + nameWidth + 3, slotY + boxStart + 11, 1610941696, -1607454624, 1.5F);
                    this.drawString(this.fontRenderer, itemName, slotX, slotY, lastSlot.getStack().getItem().getRarity().getColor());

                    for (int i = 0; i < lastSlot.getStack().getItemInfoLength(); i++) {
                        int color = 5635925;
                        if (i >= lastSlot.getStack().getItemInfoLength()) {
                            color = 5592575;
                        }

                        String moreinfo = lastSlot.getStack().getItemInformation(i);
                        String translatedString = Translator.getInstance().translateKey(moreinfo.replaceAll("\\d", "").trim());
                        translatedString = translatedString.replaceAll(":", "");
                        String numbersOnly = moreinfo.replaceAll("[^\\d:]", "") + " ";
                        stringHeight += 10;
                        if (!moreinfo.isEmpty() && moreinfo.matches(".*\\d.*")) {
                            this.drawString(this.fontRenderer, numbersOnly + translatedString, slotX, slotY + stringHeight + 11, color);
                        } else {
                            this.drawString(this.fontRenderer, translatedString, slotX, slotY + stringHeight + 11, color);
                        }
                    }
                }
            }
        }
    }

    protected void highlightSlot(Slot slot) {
        GL11.glDisable(2896);
        GL11.glDisable(2929);
        int slotX = slot.x;
        int slotY = slot.y;
        this.drawGradient(slotX, slotY, slotX + 16, slotY + 16, -2130706433, -2130706433, 1.0F);
        GL11.glEnable(2896);
        GL11.glEnable(2929);
    }

    private void renderBackPackPreview(ItemStack stackInSlot, String itemName, int width, int height, int nameWidth) {
        int x = (this.width - this.contWidth) / 2;
        int y = (this.height - this.contHeight) / 2;
        boolean isQuiver = stackInSlot.getItem() == ItemList.quiver;
        int slotX = width - x;
        int slotY = height - y;
        int bgWidth = 64;
        int bgHeight = 56;
        if (isQuiver) {
            bgWidth = 10;
            bgHeight = 20;
        }

        this.drawGradient(
            slotX + bgWidth + (nameWidth > 52 ? nameWidth : 52), slotY - 1 - bgHeight, slotX + 6, slotY - 13 - bgHeight, -802148256, -1878719232, 1.0F
        );
        this.drawGradient(slotX + bgWidth + (nameWidth > 52 ? nameWidth : 52), slotY - 1, slotX + 6, slotY - 1 - bgHeight, -802148256, -1878719232, 1.0F);
        int offx = 2 + (nameWidth > 52 ? nameWidth / 2 - 26 : 0);
        int offy = -bgHeight + 2;

        for (int i = 0; i < (isQuiver ? 3 : 18); i++) {
            this.drawGradient(
                slotX + 22 + offx + i % 6 * 18,
                slotY + 15 + offy + i / 6 * 18,
                slotX + 6 + offx + i % 6 * 18,
                slotY - 1 + offy + i / 6 * 18,
                -1871021415,
                -1599888436,
                1.0F
            );
        }

        CompoundTag tag = stackInSlot.getTag();
        ListTag<CompoundTag> items = tag.getTagList("Items");
        GL11.glPushMatrix();
        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        Lighting.enable();
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(2977);
        GL11.glEnable(2929);

        for (int i = 0; i < items.size(); i++) {
            CompoundTag itemtag = items.get(i);
            int slot = itemtag.getByte("Slot") & 255;
            if (slot >= 21) {
                slot -= 18;
            }

            ItemStack subStack = new ItemStack(itemtag);
            itemRenderer.zLevel = 200.0F;
            itemRenderer.renderItemSprite(
                this.fontRenderer, this.mc.textureLoader, subStack, slotX + 6 + offx + (slot - 3) % 6 * 18, slotY - 1 + offy + (slot - 3) / 6 * 18
            );
            itemRenderer.renderGUIOverlays(
                this.fontRenderer, this.mc.textureLoader, subStack, slotX + 6 + offx + (slot - 3) % 6 * 18, slotY - 1 + offy + (slot - 3) / 6 * 18
            );
            itemRenderer.zLevel = 0.0F;
        }

        GL11.glDisable(2929);
        GL11.glDisable(2977);
        GL11.glPopMatrix();
        Lighting.disable();
        this.drawString(this.mc.fontRenderer, "§b" + itemName, slotX + 8, slotY - 11 - bgHeight, 14737632);
    }

    private void drawStack(int x, int y, int width, int height) {
        PlayerInventory playerInventory = this.mc.player.inventory;
        if (playerInventory.getItemStack() != null) {
            GL11.glPushMatrix();
            float scale = 1.2F;
            int drawX = width - x - 8;
            int drawY = height - y - 8;
            GL11.glTranslatef(drawX, drawY, 50.0F);
            GL11.glScalef(scale, scale, 1.0F);
            itemRenderer.zLevel = 200.0F;
            itemRenderer.renderItemSprite(this.fontRenderer, this.mc.textureLoader, playerInventory.getItemStack(), 0, 0);
            itemRenderer.renderGUIOverlays(this.fontRenderer, this.mc.textureLoader, playerInventory.getItemStack(), 0, 0);
            itemRenderer.zLevel = 0.0F;
            GL11.glPopMatrix();
        }
    }

    protected void animateShiftClick() {
        if (this.container.movingStack != null) {
            float progress = Math.min(1.0F, (float)(System.currentTimeMillis() - this.container.currentTime) / 150.0F);
            int startX = this.leftBound + this.container.clickedSlot.x;
            int startY = this.topBound + this.container.clickedSlot.y;
            int endX = this.leftBound + this.container.lastClickedSlot.x;
            int endY = this.topBound + this.container.lastClickedSlot.y;
            int currentX = (int)(startX + (endX - startX) * progress) - this.leftBound;
            int currentY = (int)(startY + (endY - startY) * progress) - this.topBound;
            GL11.glPushMatrix();
            GL11.glTranslatef(currentX, currentY, 100.0F);
            itemRenderer.zLevel = 200.0F;
            itemRenderer.renderItemSprite(this.fontRenderer, this.mc.textureLoader, this.container.movingStack, 0, 0);
            itemRenderer.renderGUIOverlays(this.fontRenderer, this.mc.textureLoader, this.container.movingStack, 0, 0);
            itemRenderer.zLevel = 0.0F;
            GL11.glPopMatrix();
            if (progress >= 1.0F) {
                this.container.movingStack = null;
                this.container.clickedSlot = null;
                this.container.lastClickedSlot = null;
            }
        }
    }

    protected abstract void drawForegroundLayer();

    protected abstract void drawBackgroundLayer(float var1);

    protected void drawSlot(Slot slot) {
        int x = slot.x;
        int y = slot.y;
        ItemStack slotStack = slot.getStack();
        itemRenderer.zLevel = 100.0F;
        if (slotStack == null && !this.skipSlot(slot)) {
            Icon slotIcon = slot.backgroundIcon();
            if (slotIcon != null) {
                GL11.glDisable(2896);
                this.mc.textureLoader.bindTexture("/gui/items.png");
                this.drawGraphicFromIcon(x, y, slotIcon, 16, 16);
                GL11.glEnable(2896);
                return;
            }
        }

        itemRenderer.renderItemSprite(this.fontRenderer, this.mc.textureLoader, slotStack, x, y);
        itemRenderer.renderGUIOverlays(this.fontRenderer, this.mc.textureLoader, slotStack, x, y);
        itemRenderer.zLevel = 0.0F;
    }

    public void drawGraphicFromIcon(int x, int y, Icon icon, int xOff, int yOff) {
        Tessellator tess = Tessellator.instance;
        tess.begin();
        tess.addTexturedVertex(x, y + yOff, this.zLevel, icon.getMinU(), icon.getMaxV());
        tess.addTexturedVertex(x + xOff, y + yOff, this.zLevel, icon.getMaxU(), icon.getMaxV());
        tess.addTexturedVertex(x + xOff, y, this.zLevel, icon.getMaxU(), icon.getMinV());
        tess.addTexturedVertex(x, y, this.zLevel, icon.getMinU(), icon.getMinV());
        tess.draw();
    }

    public Slot getSlot(int x, int y) {
        for (int index = 0; index < this.container.slots.size(); index++) {
            Slot slot = this.container.slots.get(index);
            if (this.mouseOverSlot(slot, x, y)) {
                return slot;
            }
        }

        return null;
    }

    protected boolean mouseOverSlot(Slot slot, int mouseX, int mouseY) {
        int xOff = (this.width - this.contWidth) / 2;
        int yOff = (this.height - this.contHeight) / 2;
        mouseX -= xOff;
        mouseY -= yOff;
        return mouseX >= slot.x - 1 && mouseX < slot.x + 16 + 1 && mouseY >= slot.y - 1 && mouseY < slot.y + 16 + 1;
    }

    private boolean skipSlot(Slot slot) {
        int slotNumber = slot.slotNumber;
        ItemStack accessory = this.mc.player.inventory.accessorySlots[2];
        if (slotNumber >= 51 && slotNumber < 69) {
            return accessory == null || accessory.getItem() != ItemList.backpack;
        } else {
            return slotNumber >= 69 && slotNumber < 72
                ? accessory == null || accessory.getItem() != ItemList.quiver
                : this.mc.player.properties.gamemode == 2 && slotNumber <= 4;
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int click) {
        super.mouseClicked(mouseX, mouseY, click);
        if (click == 0 || click == 1) {
            Slot slot = this.getSlot(mouseX, mouseY);
            int x = (this.width - this.contWidth) / 2;
            int y = (this.height - this.contHeight) / 2;
            boolean isMouseOutOfBounds = mouseX < x - this.horizontalOffset
                || mouseY < y - this.verticalOffset
                || mouseX >= x + this.contWidth + this.horizontalOffset
                || mouseY >= y + this.contHeight + this.verticalOffset;
            if (this instanceof InventoryScreen && this.mc.player.inventory.accessorySlots[2] != null) {
                if (this.mc.player.inventory.accessorySlots[2].getItem() == ItemList.backpack) {
                    isMouseOutOfBounds = mouseX < x || mouseY < y || mouseX >= x + 256 || mouseY >= y + this.contHeight;
                } else if (this.mc.player.inventory.accessorySlots[2].getItem() == ItemList.quiver) {
                    isMouseOutOfBounds = mouseX < x - 16 || mouseY < y || mouseX >= x + this.contWidth || mouseY >= y + this.contHeight;
                }
            }

            int selectedSlotIndex = -1;
            if (slot != null) {
                selectedSlotIndex = slot.slotNumber;
            }

            if (this instanceof InventoryScreen && this.mc.player.properties.gamemode == 2 && selectedSlotIndex <= 4) {
                selectedSlotIndex = -1;
            }

            if (isMouseOutOfBounds) {
                selectedSlotIndex = -999;
            }

            if (selectedSlotIndex != -1) {
                boolean shift = selectedSlotIndex != -999
                    && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54) && this.mc.player.inventory.getItemStack() == null);
                if (!this.slotClicked(slot, selectedSlotIndex, click, shift)) {
                    this.mc.gamemode.windowClick(this.container.windowId, selectedSlotIndex, click, shift, this.mc.player);
                }
            }
        }
    }

    public void keepContainerOpen() {
        this.keepContainerOpen = true;
    }

    protected boolean slotClicked(Slot slot, int slotIndex, int click, boolean shift) {
        return false;
    }

    @Override
    protected void mouseReleased(int x, int y, int click) {
    }

    @Override
    protected void keyTyped(char character, int key) {
        if (key == 1 || key == this.mc.options.inventoryKey.key) {
            this.mc.setScreen(null);
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        if (this.mc.player != null && !this.keepContainerOpen) {
            this.mc.gamemode.closeScreen(this.container.windowId, this.mc.player);
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void update() {
        if (!this.mc.player.isAlive() || this.mc.player.isDead) {
            this.mc.player.closeScreen();
        }

        int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int y = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        boolean shiftClick = Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
        Slot slot = this.getSlot(x, y);
        if (slot != null && !this.skipSlot(slot)) {
            if (this.counter > -1) {
                this.counter--;
            }

            if (Mouse.isButtonDown(1) && shiftClick) {
                if (slot.slotNumber != this.lastAvailableSlot
                    && this.mc.player.inventory.getItemStack() != null
                    && (
                        !slot.hasStack()
                            || slot.getStack().isItemEqual(this.mc.player.inventory.getItemStack())
                                && slot.getStack().getStackLimit() > slot.getStack().stackSize
                    )) {
                    this.mc.gamemode.windowClick(this.container.windowId, this.lastAvailableSlot, 1, false, this.mc.player);
                }
            } else if (Mouse.isButtonDown(0)
                && shiftClick
                && this.mc.player.inventory.getItemStack() != null
                && slot.hasStack()
                && slot.getStack().isItemEqual(this.mc.player.inventory.getItemStack())) {
                this.mc.gamemode.windowClick(this.container.windowId, slot.slotNumber, 0, true, this.mc.player);
            }

            if (this.mc.options.dropKey.key >= 0
                && Keyboard.isKeyDown(this.mc.options.dropKey.key)
                && (this.counter == -1 || shiftClick)
                && this.mc.player.inventory.getItemStack() == null
                && slot.hasStack()) {
                this.counter = 5;
                this.mc.gamemode.windowClick(this.container.windowId, slot.slotNumber, 0, false, this.mc.player);
                this.mc.gamemode.windowClick(this.container.windowId, -999, shiftClick ? 0 : 1, false, this.mc.player);
                this.mc.gamemode.windowClick(this.container.windowId, slot.slotNumber, 0, false, this.mc.player);
            }

            for (int index = 0; index < 9; index++) {
                if (OptionsList.allKeys[15 + index].key >= 0 && Keyboard.isKeyDown(OptionsList.allKeys[15 + index].key) && this.counter == -1) {
                    this.counter = 5;
                    if (this instanceof CreativeScreen) {
                        // the creative container only exists on the client, so its
                        // slot numbers mean nothing to the server - swap it here
                        ItemStack backup = this.mc.player.inventory.getStackInSlot(index);
                        if (slot.validItem(backup)) {
                            ItemStack stack = slot.getStack();
                            if (stack != null) {
                                stack = stack.copy();
                            }

                            this.mc.player.inventory.setSlotContents(index, stack);
                            if (CreativeScreen.selectedTabIndex == CreativeTabs.tabInventory.getTabIndex() || slot.slotNumber >= 45) {
                                slot.putStack(backup);
                            }
                        }
                    } else {
                        this.mc.gamemode.containerSwapWithHotbar(this.container.windowId, slot.slotNumber, index, this.mc.player);
                    }
                }
            }
        }
    }

    protected void transferItems(int index) {
        Slot slot = this.container.slots.get(index);
        ItemStack heldStack = this.mc.player.inventory.getItemStack();
        ItemStack targetStack = slot.getStack();
        if (heldStack != null) {
            if (targetStack != null && targetStack.getItem() == heldStack.getItem() && targetStack.getItemDamage() == heldStack.getItemDamage()) {
                this.mc.gamemode.windowClick(this.container.windowId, slot.slotNumber, 0, true, this.mc.player);
            }
        } else {
            this.mc.gamemode.windowClick(this.container.windowId, slot.slotNumber, 0, true, this.mc.player);
        }
    }

    protected void displayDebuffEffects() {
        int x = this.leftBound - 124;
        int y = this.topBound;
        int tex = this.mc.textureLoader.getTexture("/gui/inventory/inventory.png");
        Collection<EffectData> effects = this.mc.player.getEffects();
        if (this instanceof InventoryScreen
            && this.mc.player.inventory.accessorySlots[2] != null
            && this.mc.player.inventory.accessorySlots[2].getItem() == ItemList.quiver) {
            x -= 20;
        }

        if (!effects.isEmpty()) {
            int effectHeight = 33;
            if (effects.size() > 5) {
                effectHeight = 132 / (effects.size() - 1);
            }

            for (EffectData activeEffect : effects) {
                Effect effect = EffectList.effects[activeEffect.getPotionID()];
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                this.mc.textureLoader.bindTexture(tex);
                this.drawGraphic(x, y, 0, 166, 140, 32);
                if (effect.hasIcon()) {
                    int i9 = effect.getIconIndex();
                    this.drawGraphic(x + 6, y + 7, 0 + i9 % 10 * 18, 198 + i9 / 10 * 18, 18, 18);
                }

                String effectName = Translator.getInstance().translateKey(effect.getPotionName());
                if (activeEffect.getPotionAmplifier() == 1) {
                    effectName = effectName + " II";
                } else if (activeEffect.getPotionAmplifier() == 2) {
                    effectName = effectName + " III";
                } else if (activeEffect.getPotionAmplifier() == 3) {
                    effectName = effectName + " IV";
                }

                this.drawString(this.fontRenderer, effectName, x + 10 + 18, y + 6, 16777215);
                String timeLeft = Effect.getDurationString(activeEffect);
                this.drawString(this.fontRenderer, timeLeft, x + 10 + 18, y + 6 + 10, 8355711);
                y += effectHeight;
            }
        }
    }
}
