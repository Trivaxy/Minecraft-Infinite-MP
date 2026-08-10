package net.minecraft.statistics.stat;

import net.minecraft.statistics.Stat;

public class MobStat extends Stat {
    private final int entityID;

    public MobStat(int amount, String name, int id) {
        super(amount, name);
        this.entityID = id;
    }

    public int getEntity() {
        return this.entityID;
    }
}
