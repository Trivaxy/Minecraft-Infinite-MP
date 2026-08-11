package net.minecraft.game.block.tileentity.container;

import net.minecraft.game.container.inventory.Inventory;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemStack;

public class GraveEntity extends ContainerEntity implements Inventory {
    public GraveEntity() {
        this.inventory = new ItemStack[45];
    }

    @Override
    public int getSize() {
        return 45;
    }

    @Override
    public boolean isInteractible(Player player) {
        return false;
    }

    @Override
    public void invalidate() {
        this.reset();
        super.invalidate();
    }
}
