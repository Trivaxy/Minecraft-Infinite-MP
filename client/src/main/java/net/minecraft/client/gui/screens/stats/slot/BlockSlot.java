package net.minecraft.client.gui.screens.stats.slot;

import java.util.ArrayList;
import java.util.Comparator;
import net.minecraft.client.gui.screens.stats.StatsScreen;
import net.minecraft.client.render.Tessellator;
import net.minecraft.localization.Translator;
import net.minecraft.statistics.Stat;
import net.minecraft.statistics.StatList;
import net.minecraft.statistics.stat.CraftingStat;

public class BlockSlot extends StatSlot {
    public BlockSlot(StatsScreen screen) {
        super(screen);
        this.statsList = new ArrayList<>();

        for (CraftingStat stat : StatList.blockStats) {
            boolean addSlot = false;
            int itemId = stat.getItemID();
            if (StatsScreen.getStatsFileWriter(screen).getStatCount(stat) > 0) {
                addSlot = true;
            } else if (StatList.useStats[itemId] != null && StatsScreen.getStatsFileWriter(screen).getStatCount(StatList.useStats[itemId]) > 0) {
                addSlot = true;
            } else if (StatList.craftStats[itemId] != null && StatsScreen.getStatsFileWriter(screen).getStatCount(StatList.craftStats[itemId]) > 0) {
                addSlot = true;
            } else if (StatList.pickupStats[itemId] != null && StatsScreen.getStatsFileWriter(screen).getStatCount(StatList.pickupStats[itemId]) > 0) {
                addSlot = true;
            } else if (StatList.dropStats[itemId] != null && StatsScreen.getStatsFileWriter(screen).getStatCount(StatList.dropStats[itemId]) > 0) {
                addSlot = true;
            }

            if (addSlot) {
                this.statsList.add(stat);
            }
        }

        this.comparator = new BlockSlot.StatsSorter(this, screen);
    }

    @Override
    protected void onInteraction(int x, int y, Tessellator tess) {
        super.onInteraction(x, y, tess);
        if (this.listSize() <= 0) {
            this.drawCenteredString(this.mc.fontRenderer, Translator.getInstance().translateKey("stat.none"), this.width / 2, y, 16777215);
        } else {
            if (this.sortingId == 0) {
                StatsScreen.drawSprite(this.screen, x + 55 - 18 + 1, y + 1 + 1, 18, 18);
            } else {
                StatsScreen.drawSprite(this.screen, x + 55 - 18, y + 1, 18, 18);
            }

            if (this.sortingId == 1) {
                StatsScreen.drawSprite(this.screen, x + 95 - 18 + 1, y + 1 + 1, 36, 18);
            } else {
                StatsScreen.drawSprite(this.screen, x + 95 - 18, y + 1, 36, 18);
            }

            if (this.sortingId == 2) {
                StatsScreen.drawSprite(this.screen, x + 135 - 18 + 1, y + 1 + 1, 54, 18);
            } else {
                StatsScreen.drawSprite(this.screen, x + 135 - 18, y + 1, 54, 18);
            }

            if (this.sortingId == 3) {
                StatsScreen.drawSprite(this.screen, x + 175 - 18 + 1, y + 1 + 1, 0, 36);
            } else {
                StatsScreen.drawSprite(this.screen, x + 175 - 18, y + 1, 0, 36);
            }

            if (this.sortingId == 4) {
                StatsScreen.drawSprite(this.screen, x + 215 - 18 + 1, y + 1 + 1, 18, 36);
            } else {
                StatsScreen.drawSprite(this.screen, x + 215 - 18, y + 1, 18, 36);
            }
        }
    }

    @Override
    protected void drawSlot(int id, int x, int y, int offset, Tessellator tess) {
        CraftingStat stat = this.getStat(id);
        int itemId = stat.getItemID();
        y += 20;
        StatsScreen.drawItemSprite(this.screen, x, y, itemId);
        this.displayStatString((CraftingStat)StatList.craftStats[itemId], x + 55, y, id % 2 == 0);
        this.displayStatString((CraftingStat)StatList.useStats[itemId], x + 95, y, id % 2 == 0);
        this.displayStatString(stat, x + 135, y, id % 2 == 0);
        this.displayStatString((CraftingStat)StatList.pickupStats[itemId], x + 175, y, id % 2 == 0);
        this.displayStatString((CraftingStat)StatList.dropStats[itemId], x + 215, y, id % 2 == 0);
    }

    @Override
    protected String getStatName(int category) {
        return category == 0 ? "stat.crafted" : (category == 1 ? "stat.place" : (category == 2 ? "stat.mine" : (category == 3 ? "stat.collect" : "stat.drop")));
    }

    public class StatsSorter implements Comparator<Object> {
        final StatsScreen statsGUI;
        final BlockSlot statSlot;

        public StatsSorter(BlockSlot slot, StatsScreen screen) {
            this.statSlot = slot;
            this.statsGUI = screen;
        }

        public int readStatList(CraftingStat slot1, CraftingStat slot2) {
            int slot1ID = slot1.getItemID();
            int slot2ID = slot2.getItemID();
            Stat stat1 = null;
            Stat stat2 = null;
            if (this.statSlot.clickedState == 2) {
                stat1 = StatList.mineStats[slot1ID];
                stat2 = StatList.mineStats[slot2ID];
            } else if (this.statSlot.clickedState == 0) {
                stat1 = StatList.craftStats[slot1ID];
                stat2 = StatList.craftStats[slot2ID];
            } else if (this.statSlot.clickedState == 1) {
                stat1 = StatList.useStats[slot1ID];
                stat2 = StatList.useStats[slot2ID];
            } else if (this.statSlot.clickedState == 3) {
                stat1 = StatList.pickupStats[slot1ID];
                stat2 = StatList.pickupStats[slot2ID];
            } else if (this.statSlot.clickedState == 4) {
                stat1 = StatList.dropStats[slot1ID];
                stat2 = StatList.dropStats[slot2ID];
            }

            if (stat1 != null || stat2 != null) {
                if (stat1 == null) {
                    return 1;
                }

                if (stat2 == null) {
                    return -1;
                }

                int count1 = StatsScreen.getStatsFileWriter(this.statSlot.screen).getStatCount(stat1);
                int count2 = StatsScreen.getStatsFileWriter(this.statSlot.screen).getStatCount(stat2);
                if (count1 != count2) {
                    return (count1 - count2) * this.statSlot.selectedStat;
                }
            }

            return slot1ID - slot2ID;
        }

        @Override
        public int compare(Object obj1, Object obj2) {
            return this.readStatList((CraftingStat)obj1, (CraftingStat)obj2);
        }
    }
}
