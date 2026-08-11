package net.minecraft.game.block.tileentity.container;

import java.util.Random;
import net.minecraft.game.container.inventory.Inventory;
import net.minecraft.game.item.ItemStack;
import net.minecraft.localization.Translator;

public class DispenserEntity extends ContainerEntity implements Inventory {
    private Random random = new Random();

    public ItemStack getRandomStack() {
        int slot = -1;
        int nextSlot = 1;

        for (int currSlot = 0; currSlot < this.inventory.length; currSlot++) {
            if (this.inventory[currSlot] != null && this.random.nextInt(nextSlot++) == 0) {
                slot = currSlot;
            }
        }

        return slot >= 0 ? this.decrStackSize(slot, 1) : null;
    }

    @Override
    public String getName() {
        return Translator.getInstance().translateKey("container.dispenser");
    }
}
