package net.minecraft.game.item.misc;

import net.minecraft.game.item.Item;

public class WolfArmor extends Item {
    public WolfArmor(int id) {
        super(id);
    }

    @Override
    protected int defaultColor() {
        return 10511680;
    }

    @Override
    public boolean isDyable() {
        return true;
    }
}
