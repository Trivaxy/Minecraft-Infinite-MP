package net.minecraft.statistics.stat;

import net.minecraft.statistics.Stat;

public class CraftingStat extends Stat {
    private final int itemID;

    public CraftingStat(int value, String name, int id) {
        super(value, name);
        this.itemID = id;
    }

    public int getItemID() {
        return this.itemID;
    }
}
