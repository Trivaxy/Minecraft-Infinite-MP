package net.minecraft.client.gui.screens.container.tileentity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.gui.button.Button;
import net.minecraft.client.gui.button.InvisibleButton;
import net.minecraft.client.gui.screens.container.ContainerScreen;
import net.minecraft.game.block.tileentity.container.ChestEntity;
import net.minecraft.game.container.inventory.GlobalInventory;
import net.minecraft.game.container.inventory.Inventory;
import net.minecraft.game.container.inventory.LargeChestInventory;
import net.minecraft.game.container.tileentities.BookshelfContainer;
import net.minecraft.game.container.tileentities.ChestContainer;
import net.minecraft.game.item.ItemStack;

public class ChestScreen extends ContainerScreen {
    private Inventory playerInventory;
    private Inventory chestInventory;
    private Inventory endChest;
    private String texName = "container";
    private int inventoryRows = 0;

    public ChestScreen(Inventory playerInv, Inventory chestInv, boolean locked) {
        super(new ChestContainer(playerInv, chestInv));
        this.playerInventory = playerInv;
        this.chestInventory = chestInv;
        this.inventoryRows = chestInv.getSize() / 9;
        this.contHeight = 126 + this.inventoryRows * 18;
        this.texName = locked ? "lockedChest" : "container";
    }

    public ChestScreen(Inventory playerInv, Inventory chestInv) {
        super(new BookshelfContainer(playerInv, chestInv));
        this.playerInventory = playerInv;
        this.chestInventory = chestInv;
        this.inventoryRows = chestInv.getSize() / 9;
        this.contHeight = 126 + this.inventoryRows * 18;
        this.texName = "bookshelf";
    }

    public ChestScreen(Inventory playerInv, Inventory globalInv, Inventory endChest) {
        super(new ChestContainer(playerInv, globalInv));
        this.playerInventory = playerInv;
        this.chestInventory = globalInv;
        this.endChest = endChest;
        this.inventoryRows = globalInv.getSize() / 9;
        this.contHeight = 126 + this.inventoryRows * 18;
        this.texName = "obsidianChest";
    }

    @Override
    public void initGui() {
        super.initGui();
        int guiTop = (this.height - this.contHeight) / 2 + this.contHeight - 92;
        this.buttons.get(0).y = guiTop;
        this.buttons.add(new InvisibleButton(1, this.width / 2 + 62, guiTop, 10, 10, "z", "gui.sort"));
        this.buttons.add(new InvisibleButton(2, this.width / 2 + 52, guiTop, 10, 10, "v", "gui.takeall"));
        this.buttons.add(new InvisibleButton(3, this.width / 2 + 42, guiTop + 2, 10, 10, "^", "gui.putall"));
    }

    @Override
    protected void buttonClicked(Button button) {
        super.buttonClicked(button);
        if (button.id == 1 && !this.mc.world.isOnline) {
            List<ItemStack> list = null;
            if (this.chestInventory instanceof ChestEntity) {
                list = Arrays.asList(((ChestEntity)this.chestInventory).inventory);
            } else if (this.chestInventory instanceof GlobalInventory) {
                list = Arrays.asList(((GlobalInventory)this.chestInventory).stacks);
            } else if (this.chestInventory instanceof LargeChestInventory) {
                LargeChestInventory large = (LargeChestInventory)this.chestInventory;
                Inventory left = large.upperChest;
                Inventory right = large.lowerChest;
                List<ItemStack> combined = new ArrayList<>();

                for (int i = 0; i < left.getSize(); i++) {
                    combined.add(left.getStackInSlot(i));
                }

                for (int i = 0; i < right.getSize(); i++) {
                    combined.add(right.getStackInSlot(i));
                }

                combined.sort(Comparator.comparingInt(stack -> stack != null && stack.getItem() != null ? stack.getItem().id : Integer.MAX_VALUE));
                int index = 0;

                for (int i = 0; i < left.getSize(); i++) {
                    left.setSlotContents(i, combined.get(index++));
                }

                for (int i = 0; i < right.getSize(); i++) {
                    right.setSlotContents(i, combined.get(index++));
                }
            }

            if (list != null) {
                Collections.sort(list, Comparator.comparingInt(stack -> stack != null && stack.getItem() != null ? stack.getItem().id : Integer.MAX_VALUE));
            }
        }

        if (button.id == 2) {
            for (int index = 0; index < this.container.slots.size() - 36; index++) {
                this.transferItems(index);
            }
        }

        if (button.id == 3) {
            for (int index = this.container.slots.size() - 36; index < this.container.slots.size() - 9; index++) {
                this.transferItems(index);
            }
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        this.chestInventory.close();
        if (this.endChest != null) {
            this.endChest.close();
        }
    }

    @Override
    protected void drawForegroundLayer() {
        this.drawString(this.fontRenderer, this.chestInventory.getName(), 8, 6, 16777215);
        this.drawStringNoShadow(this.fontRenderer, this.playerInventory.getName(), 8, this.contHeight - 91, this.txtColor);
    }

    @Override
    protected void drawBackgroundLayer(float deltaTicks) {
        int tex = this.mc.textureLoader.getTexture("/gui/container/" + this.texName + ".png");
        this.mc.textureLoader.bindTexture(tex);
        int x = (this.width - this.contWidth) / 2;
        int y = (this.height - this.contHeight) / 2;
        this.drawGraphic(x, y, 0, 0, this.contWidth, this.inventoryRows * 18 + 17);
        this.drawGraphic(x, y + this.inventoryRows * 18 + 17, 0, 126, 256, 126);
    }
}
