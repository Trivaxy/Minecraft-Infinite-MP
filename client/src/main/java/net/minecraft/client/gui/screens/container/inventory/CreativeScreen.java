package net.minecraft.client.gui.screens.container.inventory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.gui.button.Button;
import net.minecraft.client.gui.button.CategoryButton;
import net.minecraft.client.gui.button.InvisibleButton;
import net.minecraft.client.gui.button.TextField;
import net.minecraft.client.gui.screens.container.ContainerScreen;
import net.minecraft.game.container.Container;
import net.minecraft.game.container.inventory.BasicInventory;
import net.minecraft.game.container.player.CreativeContainer;
import net.minecraft.game.container.slot.CreativeSlot;
import net.minecraft.game.container.slot.Slot;
import net.minecraft.game.container.slot.TrashSlot;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.localization.Translator;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class CreativeScreen extends ContainerScreen {
    private static final BasicInventory inventory = new BasicInventory("creative", 45);
    private List<Slot> backupContainerSlots;
    private Slot binSlot = null;
    private ItemStack[] lastSent;
    private TextField searchField;
    public static int selectedTabIndex = CreativeTabs.tabNature.getTabIndex();
    private float rotX;
    private float rotY;
    private float currentScroll = 0.0F;
    private boolean isScrolling = false;

    public CreativeScreen(Player player) {
        super(new CreativeContainer(player, inventory));
        player.openContainer = this.container;
        this.contHeight = 136;
        this.contWidth = 195;
        this.verticalOffset = 26;
    }

    @Override
    public void update() {
        super.update();
        if (!this.mc.player.properties.isCreative()) {
            this.mc.setScreen(new InventoryScreen(this.mc.player));
        }

        this.searchField.updateCursor();
        this.syncCreativeInventory();
    }

    @Override
    public void initGui() {
        if (this.mc.player.properties.isCreative()) {
            super.initGui();
            this.buttons.clear();
            Keyboard.enableRepeatEvents(true);
            this.searchField = new TextField(this.fontRenderer, this.leftBound + 82, this.topBound + 6, 89, this.fontRenderer.fontHeight);
            this.searchField.setMaxStringLength(16);
            this.searchField.setRenderBackground(false);
            this.searchField.setTextColor(16777215);
            int max = CreativeTabs.creativeTabArray.length;

            for (int i = 0; i < max / 2; i++) {
                CreativeTabs tab = CreativeTabs.creativeTabArray[i];
                if (i == max / 2 - 1) {
                    this.buttons
                        .add(
                            new CategoryButton(
                                i, this.leftBound + 22 + 29 * i, this.height / 2 - 94, tab.getTranslatedLabel(), tab.getIcon(), tab.getMetadata(), false
                            )
                        );
                } else {
                    this.buttons
                        .add(
                            new CategoryButton(
                                i, this.leftBound + 29 * i, this.height / 2 - 94, tab.getTranslatedLabel(), tab.getIcon(), tab.getMetadata(), false
                            )
                        );
                }
            }

            for (int i = max / 2; i < max; i++) {
                CreativeTabs tab = CreativeTabs.creativeTabArray[i];
                if (i == max - 1) {
                    this.buttons
                        .add(
                            new CategoryButton(
                                i,
                                this.leftBound + 22 + 29 * (i - max / 2),
                                this.height / 2 + 64,
                                tab.getTranslatedLabel(),
                                tab.getIcon(),
                                tab.getMetadata(),
                                true
                            )
                        );
                } else {
                    this.buttons
                        .add(
                            new CategoryButton(
                                i, this.leftBound + 29 * (i - max / 2), this.height / 2 + 64, tab.getTranslatedLabel(), tab.getIcon(), tab.getMetadata(), true
                            )
                        );
                }
            }

            this.setTab(CreativeTabs.creativeTabArray[selectedTabIndex]);
            ((CreativeContainer)this.container).scrollTo(this.currentScroll);
            this.buttons.add(this.closeButton = new InvisibleButton(20, this.width / 2 + 82, this.height / 2 - 64, 10, 10, "x", "gui.close"));
        } else {
            this.mc.setScreen(new InventoryScreen(this.mc.player));
        }
    }

    protected void updateSurvivalSlot(Slot slot, int slotIndex, int stackSize, boolean shift) {
        if (slot == this.binSlot) {
            if (shift) {
                for (int i = 0; i < this.mc.player.inventory.getSize(); i++) {
                    this.mc.player.inventory.setSlotContents(i, null);
                }

                this.mc.player.inventory.offhandSlot[0] = null;
            } else {
                this.mc.player.inventory.setItemStack(null);
            }
        } else {
            this.mc
                .player
                .container
                .windowClick(slot == null ? slotIndex : CreativeSlot.getSlot((CreativeSlot)slot).slotNumber, stackSize, shift, this.mc.player);
        }
    }

    private void syncCreativeInventory() {
        List<Slot> slots = this.mc.player.container.slots;
        if (this.lastSent == null || this.lastSent.length != slots.size()) {
            this.lastSent = new ItemStack[slots.size()];

            for (int index = 0; index < slots.size(); index++) {
                ItemStack held = slots.get(index).getStack();
                this.lastSent[index] = held == null ? null : held.copy();
            }

            return;
        }

        for (int index = 0; index < slots.size(); index++) {
            ItemStack held = slots.get(index).getStack();
            if (!ItemStack.areItemStacksEqual(this.lastSent[index], held)) {
                this.mc.gamemode.setCreativeSlot(index, held);
                this.lastSent[index] = held == null ? null : held.copy();
            }
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        this.syncCreativeInventory();
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void keyTyped(char character, int key) {
        if (selectedTabIndex != CreativeTabs.tabAllSearch.getTabIndex()) {
            if (key == this.mc.options.chatKey.key) {
                this.setTab(CreativeTabs.tabAllSearch);
            } else {
                super.keyTyped(character, key);
            }
        } else if (this.searchField.textboxKeyTyped(character, key)) {
            this.updateCreativeSearch();
        } else {
            super.keyTyped(character, key);
        }
    }

    private void updateCreativeSearch() {
        CreativeContainer container = (CreativeContainer)this.container;
        container.itemList.clear();

        for (Item item : ItemList.items) {
            if (item != null && item.creativeCategory() != null) {
                item.getSubItems(item.id, null, container.itemList);
            }
        }

        String searchText = this.searchField.getText().toLowerCase();
        Iterator<ItemStack> itemsList = container.itemList.iterator();

        while (itemsList.hasNext()) {
            ItemStack stack = itemsList.next();
            String itemName = Translator.getInstance().translateNamedKey(stack.getItemName(stack.itemDmg));
            if (!itemName.toLowerCase().contains(searchText)) {
                itemsList.remove();
            }
        }

        this.currentScroll = 0.0F;
        container.scrollTo(0.0F);
    }

    @Override
    protected void drawForegroundLayer() {
        CreativeTabs currentTab = CreativeTabs.creativeTabArray[selectedTabIndex];
        if (currentTab.hasTitle()) {
            this.drawStringNoShadow(this.fontRenderer, Translator.getInstance().translateKey(currentTab.getTranslatedLabel()), 8, 6, this.txtColor);
        }
    }

    @Override
    protected void buttonClicked(Button button) {
        super.buttonClicked(button);
        if (button.id < CreativeTabs.creativeTabArray.length) {
            this.setTab(CreativeTabs.creativeTabArray[button.id]);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int click) {
        if (selectedTabIndex == CreativeTabs.tabInventory.getTabIndex()) {
            Slot slot = this.getSlot(mouseX, mouseY);
            int width = this.leftBound;
            int height = this.topBound;
            boolean mouseNotOnSlot = mouseX < width - this.horizontalOffset
                || mouseY < height - this.verticalOffset
                || mouseX >= width + this.contWidth + this.horizontalOffset
                || mouseY >= height + this.contHeight + this.horizontalOffset;
            int slotIndex = -1;
            if (slot != null) {
                slotIndex = slot.slotNumber;
            }

            if (mouseNotOnSlot) {
                slotIndex = -999;
            }

            if (slotIndex != -1) {
                boolean shift = slotIndex != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));
                this.updateSurvivalSlot(slot, slotIndex, click, shift);
            }
        }

        super.mouseClicked(mouseX, mouseY, click);
    }

    private boolean canScroll() {
        return selectedTabIndex != CreativeTabs.tabInventory.getTabIndex()
            && CreativeTabs.creativeTabArray[selectedTabIndex].hideInventory()
            && ((CreativeContainer)this.container).scrollable();
    }

    private void setTab(CreativeTabs tab) {
        int prevSelectedSlot = selectedTabIndex;
        selectedTabIndex = tab.getTabIndex();

        for (int i = 0; i < CreativeTabs.creativeTabArray.length; i++) {
            ((CategoryButton)this.buttons.get(i)).selected = false;
        }

        ((CategoryButton)this.buttons.get(tab.getTabIndex())).selected = true;
        ((CreativeContainer)this.container).selectedTab = tab.getTabIndex();
        CreativeContainer container = (CreativeContainer)this.container;
        container.itemList.clear();
        tab.displayAllItems(container.itemList);
        if (tab == CreativeTabs.tabInventory) {
            Container playerInventory = this.mc.player.container;
            if (this.backupContainerSlots == null) {
                this.backupContainerSlots = container.slots;
            }

            container.slots = new ArrayList<>();

            for (int slotId = 5; slotId < 50; slotId++) {
                Slot slot = new CreativeSlot(playerInventory.slots.get(slotId), playerInventory.slots.get(slotId).slotIndex);
                container.slots.add(slot);
                int index = playerInventory.slots.get(slotId).slotIndex;
                if (slotId == 49) {
                    slot.x = 126;
                    slot.y = 20;
                } else if (index >= 36 && index < 40) {
                    if (index == 39) {
                        index = 36;
                    } else if (index == 36) {
                        index = 39;
                    }

                    int slotPos = index - 36;
                    int x = slotPos / 2;
                    int y = slotPos % 2;
                    slot.x = 9 + x * 54;
                    slot.y = 6 + y * 27;
                } else if (index >= 40 && index < 44) {
                    int slotPos = index - 40;
                    int x = slotPos / 2;
                    int y = slotPos % 2;
                    slot.x = 99 + x * 54;
                    slot.y = 6 + y * 27;
                } else if (index < 36) {
                    int slotPos = index;
                    int x = slotPos % 9;
                    int y = slotPos / 9;
                    slot.x = 9 + x * 18;
                    if (index < 9) {
                        slot.y = 112;
                    } else {
                        slot.y = 36 + y * 18;
                    }
                }
            }

            this.binSlot = new TrashSlot(inventory, 44, 173, 112);
            container.slots.add(this.binSlot);
        } else if (prevSelectedSlot == CreativeTabs.tabInventory.getTabIndex()) {
            container.slots = this.backupContainerSlots;
            this.backupContainerSlots = null;
        }

        if (this.searchField != null) {
            if (tab == CreativeTabs.tabAllSearch) {
                this.searchField.setVisible(true);
                this.searchField.setFocused(true);
                this.searchField.setText("");
                this.updateCreativeSearch();
            } else {
                this.searchField.setVisible(false);
                this.searchField.setFocused(false);
            }
        }

        this.currentScroll = 0.0F;
        container.scrollTo(0.0F);
    }

    @Override
    public void scroll(int scrollValue) {
        if (scrollValue != 0 && this.canScroll()) {
            int currPage = ((CreativeContainer)this.container).itemList.size() / 9 - 5 + 1;
            if (scrollValue > 0) {
                scrollValue = 1;
            }

            if (scrollValue < 0) {
                scrollValue = -1;
            }

            this.currentScroll = (float)(this.currentScroll - (double)scrollValue / currPage);
            if (this.currentScroll < 0.0F) {
                this.currentScroll = 0.0F;
            }

            if (this.currentScroll > 1.0F) {
                this.currentScroll = 1.0F;
            }

            ((CreativeContainer)this.container).scrollTo(this.currentScroll);
        }
    }

    @Override
    public void drawScreen(int x, int y, float deltaTick) {
        boolean isClicking = Mouse.isButtonDown(0);
        int minWidth = this.leftBound;
        int minHeight = this.topBound;
        int maxWidth = minWidth + 175;
        int maxHeight = minHeight + 18;
        int widthOff = maxWidth + 14;
        int heightOff = maxHeight + 112;
        this.rotX = x;
        this.rotY = y;
        if (isClicking && x >= maxWidth && y >= maxHeight && x < widthOff && y < heightOff) {
            this.isScrolling = this.canScroll();
        }

        if (!isClicking) {
            this.isScrolling = false;
        }

        if (this.isScrolling) {
            this.currentScroll = (y - maxHeight - 7.5F) / (heightOff - maxHeight - 15.0F);
            if (this.currentScroll < 0.0F) {
                this.currentScroll = 0.0F;
            }

            if (this.currentScroll > 1.0F) {
                this.currentScroll = 1.0F;
            }

            ((CreativeContainer)this.container).scrollTo(this.currentScroll);
        }

        super.drawScreen(x, y, deltaTick);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(2896);
    }

    @Override
    protected void drawBackgroundLayer(float deltaTick) {
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        CreativeTabs currentTab = CreativeTabs.creativeTabArray[selectedTabIndex];
        this.mc.textureLoader.bindTexture("/gui/inventory/" + currentTab.getBackgroundImage() + ".png");
        this.drawGraphic(this.leftBound, this.topBound, 0, 0, this.contWidth, this.contHeight);
        int maxLength = this.leftBound + 175;
        int minHeight = this.topBound + 18;
        int maxHeight = minHeight + 112;
        if (currentTab.hideInventory()) {
            this.drawGraphic(maxLength, minHeight + (int)((maxHeight - minHeight - 17) * this.currentScroll), 232 + (this.canScroll() ? 0 : 12), 0, 12, 15);
        }

        this.searchField.drawTextBox();
        this.displayDebuffEffects();
        if (currentTab == CreativeTabs.tabInventory) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            InventoryScreen.drawPlayerOnGui(
                this.mc, this.leftBound + 43, this.topBound + 48, 20, this.leftBound + 43 - this.rotX, this.topBound + 45 - 30 - this.rotY
            );
        }
    }

    @Override
    protected void animateShiftClick() {
        if (this.container.movingStack != null) {
            float progress = Math.min(1.0F, (float)(System.currentTimeMillis() - this.container.currentTime) / 150.0F);
            CreativeScreen.Point start = this.getSlotRenderPosition(this.container.clickedSlot);
            CreativeScreen.Point end = this.getSlotRenderPosition(this.container.lastClickedSlot);
            int currentX = (int)(start.x + (end.x - start.x) * progress) - this.leftBound;
            int currentY = (int)(start.y + (end.y - start.y) * progress) - this.topBound;
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

    private CreativeScreen.Point getSlotRenderPosition(Slot slot) {
        int x = this.leftBound + slot.x + 1;
        int y = this.topBound + slot.y - 30;
        switch (slot.slotNumber) {
            case 5:
                y += 28;
                break;
            case 6:
                y += 10;
                x += 54;
                break;
            case 7:
                y += 19;
                break;
            case 8:
                x += 54;
                y++;
                break;
            case 9:
                y += 55;
                x += 72;
                break;
            case 10:
                y += 10;
                x += 72;
                break;
            case 11:
                y += 19;
                x += 18;
                break;
            case 12:
                y -= 26;
                x += 18;
        }

        return new CreativeScreen.Point(x, y);
    }

    private static class Point {
        int x;
        int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
