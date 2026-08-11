package net.minecraft.game.block.tileentity.container;

import net.minecraft.game.container.inventory.Inventory;
import net.minecraft.localization.Translator;

public final class BookcaseEntity extends ContainerEntity implements Inventory {
    @Override
    public void onChanged() {
        int countSlots = 0;

        for (int i = 0; i < this.getSize(); i++) {
            if (this.getStackInSlot(i) != null) {
                countSlots++;
            }
        }

        this.world.setBlockMetadataWithNotify(this.x, this.y, this.z, countSlots);
        super.onChanged();
    }

    @Override
    public final String getName() {
        return Translator.getInstance().translateKey("container.bookcase");
    }
}
