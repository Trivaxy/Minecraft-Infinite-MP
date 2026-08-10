package net.minecraft.statistics.stat;

import net.minecraft.statistics.Stat;

public final class TimeStat implements StatType {
    @Override
    public String format(int tick) {
        double second = tick / 20.0;
        double minute = second / 60.0;
        double hour = minute / 60.0;
        double day = hour / 24.0;
        double year = day / 365.0;
        return year > 0.5
            ? Stat.getDecimalFormat().format(year) + " y"
            : (
                day > 0.5
                    ? Stat.getDecimalFormat().format(day) + " d"
                    : (
                        hour > 0.5
                            ? Stat.getDecimalFormat().format(hour) + " h"
                            : (minute > 0.5 ? Stat.getDecimalFormat().format(minute) + " m" : second + " s")
                    )
            );
    }
}
