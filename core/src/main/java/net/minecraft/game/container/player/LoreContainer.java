package net.minecraft.game.container.player;

import net.minecraft.game.container.Container;
import net.minecraft.game.container.inventory.BasicInventory;
import net.minecraft.game.container.inventory.Inventory;
import net.minecraft.game.container.inventory.PlayerInventory;
import net.minecraft.game.container.slot.RecipeSlot;
import net.minecraft.game.container.slot.Slot;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemStack;

public class LoreContainer extends Container {
    public Inventory loreSlot = new BasicInventory("Lore", 1);

    public LoreContainer(PlayerInventory inventoryplayer) {
        this.addSlot(new RecipeSlot(this.loreSlot, 0, 82, 66));

        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 9; k++) {
                this.addSlot(new Slot(inventoryplayer, k + i * 9 + 9, 48 + k * 18, 129 + i * 18));
            }
        }

        for (int j = 0; j < 9; j++) {
            this.addSlot(new Slot(inventoryplayer, j, 48 + j * 18, 187));
        }
    }

    public void setDisplayStack(ItemStack itemstack) {
        this.slots.get(0).putStack(itemstack == null ? null : itemstack.copy());
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
