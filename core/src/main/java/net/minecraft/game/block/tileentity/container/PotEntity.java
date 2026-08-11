package net.minecraft.game.block.tileentity.container;

import net.minecraft.game.container.inventory.Inventory;
import net.minecraft.localization.Translator;

public class PotEntity extends VacuumEntity implements Inventory {
    @Override
    public int getSize() {
        return 4;
    }

    @Override
    public String getName() {
        return Translator.getInstance().translateKey("container.pot");
    }
}
