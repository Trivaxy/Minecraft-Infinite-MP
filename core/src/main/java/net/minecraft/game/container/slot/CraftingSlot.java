package net.minecraft.game.container.slot;

import net.minecraft.game.container.inventory.Inventory;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.statistics.StatList;
import net.minecraft.statistics.achievement.AchievementList;

public class CraftingSlot extends Slot {
    private final Inventory craftMatrix;
    private Player player;
    private int amountCrafted;

    public CraftingSlot(Player player, Inventory craftingInv, Inventory inv, int index, int x, int y) {
        super(inv, index, x, y);
        this.player = player;
        this.craftMatrix = craftingInv;
    }

    @Override
    public boolean validItem(ItemStack stack) {
        return false;
    }

    @Override
    public void onPickup(ItemStack stack) {
        stack.onCrafting(this.player.world, this.player, this.amountCrafted);
        this.onCrafting(stack);

        for (int slot = 0; slot < this.craftMatrix.getSize(); slot++) {
            ItemStack slotStack = this.craftMatrix.getStackInSlot(slot);
            if (slotStack != null) {
                this.craftMatrix.decrStackSize(slot, 1);
            }
        }
    }

    protected void onCrafting(ItemStack stack) {
        this.player.addStat(StatList.craftStats[stack.itemID], stack.stackSize);
        this.player.addStat(StatList.itemsCraftedStat, 1);
        if (stack.getItem() == ItemList.recordVoid) {
            this.player.triggerAchievement(AchievementList.disc);
        }
    }
}
