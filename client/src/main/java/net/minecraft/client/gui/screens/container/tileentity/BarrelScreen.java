package net.minecraft.client.gui.screens.container.tileentity;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.gui.button.Button;
import net.minecraft.client.gui.button.InvisibleButton;
import net.minecraft.client.gui.screens.container.ContainerScreen;
import net.minecraft.game.block.tileentity.container.BarrelEntity;
import net.minecraft.game.container.inventory.Inventory;
import net.minecraft.game.container.tileentities.BarrelContainer;
import net.minecraft.game.item.ItemStack;

public final class BarrelScreen extends ContainerScreen {
    private Inventory playerInventory;
    private Inventory containerInventory;
    private boolean alt = false;

    public BarrelScreen(Inventory upperChestInventory, Inventory lowerChestInventory, boolean alt) {
        super(new BarrelContainer(upperChestInventory, lowerChestInventory));
        this.playerInventory = upperChestInventory;
        this.containerInventory = lowerChestInventory;
        this.alt = alt;
    }

    @Override
    public void initGui() {
        super.initGui();
        int guiTop = (this.height - this.contHeight) / 2 + this.contHeight - 92;
        this.buttons.add(new InvisibleButton(1, this.width / 2 + 62, guiTop, 10, 10, "z", "gui.sort"));
        this.buttons.add(new InvisibleButton(2, this.width / 2 + 52, guiTop, 10, 10, "v", "gui.takeall"));
        this.buttons.add(new InvisibleButton(3, this.width / 2 + 42, guiTop + 2, 10, 10, "^", "gui.putall"));
    }

    @Override
    protected void buttonClicked(Button button) {
        super.buttonClicked(button);
        if (button.id == 1 && !this.mc.world.isOnline) {
            List<ItemStack> list = Arrays.asList(((BarrelEntity)this.containerInventory).inventory);
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
        this.containerInventory.close();
    }

    @Override
    protected final void drawForegroundLayer() {
        if (this.alt) {
            this.drawCenteredString(this.fontRenderer, this.containerInventory.getName(), this.contWidth / 2, 6, 16777215);
        } else {
            this.drawCenteredStringNoShadow(this.fontRenderer, this.containerInventory.getName(), this.contWidth / 2, 6, this.txtColor);
        }

        this.drawStringNoShadow(this.fontRenderer, this.playerInventory.getName(), 8, this.contHeight - 91, this.txtColor);
    }

    @Override
    protected void drawBackgroundLayer(float deltaTime) {
        int tex = this.mc.textureLoader.getTexture("/gui/container/trap.png");
        if (this.alt) {
            tex = this.mc.textureLoader.getTexture("/gui/container/barrel.png");
        }

        this.mc.textureLoader.bindTexture(tex);
        int x = (this.width - this.contWidth) / 2;
        int y = (this.height - this.contHeight) / 2;
        this.drawGraphic(x, y, 0, 0, this.contWidth, 205);
    }
}
