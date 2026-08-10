package net.minecraft.statistics.stat;

import net.minecraft.statistics.Stat;

public final class SimpleStat implements StatType {
    @Override
    public String format(int amount) {
        return Stat.getNumberFormat().format(amount);
    }
}
