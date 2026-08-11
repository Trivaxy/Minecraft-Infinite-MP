package net.minecraft.statistics.stat;

import net.minecraft.statistics.Stat;

public final class DistanceStat implements StatType {
    @Override
    public String format(int cm) {
        double m = cm / 100.0;
        double km = m / 1000.0;
        return km > 0.5 ? Stat.getDecimalFormat().format(km) + " km" : (m > 0.5 ? Stat.getDecimalFormat().format(m) + " m" : cm + " cm");
    }
}
