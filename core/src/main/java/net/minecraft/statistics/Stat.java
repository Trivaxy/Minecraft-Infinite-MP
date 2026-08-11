package net.minecraft.statistics;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import net.minecraft.localization.Translator;
import net.minecraft.statistics.stat.DistanceStat;
import net.minecraft.statistics.stat.SimpleStat;
import net.minecraft.statistics.stat.StatType;
import net.minecraft.statistics.stat.TimeStat;

public class Stat {
    private static NumberFormat numberFormat = NumberFormat.getIntegerInstance(Locale.US);
    private static DecimalFormat decimalFormat = new DecimalFormat("########0.00");
    private final StatType type;
    public static StatType simpleStat = new SimpleStat();
    public static StatType timeStat = new TimeStat();
    public static StatType distanceStat = new DistanceStat();
    public final String statName;
    public final int statId;
    public boolean isIndependent = false;

    public Stat(int id, String name, StatType type) {
        this.statId = id;
        this.statName = name;
        this.type = type;
    }

    public Stat(int id, String name) {
        this(id, name, simpleStat);
    }

    public Stat registerStat() {
        if (StatList.oneShotStats.containsKey(this.statId)) {
            throw new RuntimeException(
                "Duplicate stat id: \"" + StatList.oneShotStats.get(this.statId).statName + "\" and \"" + this.statName + "\" at id " + this.statId
            );
        }

        StatList.allStats.add(this);
        StatList.oneShotStats.put(this.statId, this);
        return this;
    }

    public boolean isAchievement() {
        return false;
    }

    public String getTypeName(int id) {
        return this.type.format(id);
    }

    public String getName() {
        return Translator.getInstance().translateKey(this.statName);
    }

    public static NumberFormat getNumberFormat() {
        return numberFormat;
    }

    public static DecimalFormat getDecimalFormat() {
        return decimalFormat;
    }
}
