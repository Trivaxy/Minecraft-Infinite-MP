package net.minecraft.game.container.slot;

import net.minecraft.game.container.inventory.Inventory;
import net.minecraft.game.item.misc.ArrowItem;
import net.minecraft.game.texture.Icon;

public class TrashSlot extends Slot {
    public TrashSlot(Inventory inv, int index, int x, int y) {
        super(inv, index, x, y);
    }

    @Override
    public Icon backgroundIcon() {
        return ArrowItem.trashSlot;
    }
}
