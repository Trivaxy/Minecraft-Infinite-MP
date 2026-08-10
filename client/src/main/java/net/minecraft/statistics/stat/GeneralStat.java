package net.minecraft.statistics.stat;

import net.minecraft.statistics.Stat;
import net.minecraft.statistics.StatList;

public class GeneralStat extends Stat {
    public GeneralStat(int amount, String name, StatType type) {
        super(amount, name, type);
    }

    public GeneralStat(int amount, String name) {
        super(amount, name);
    }

    @Override
    public Stat registerStat() {
        super.registerStat();
        StatList.generalStats.add(this);
        return this;
    }
}
