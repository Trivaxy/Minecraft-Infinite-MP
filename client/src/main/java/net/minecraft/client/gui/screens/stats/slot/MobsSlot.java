package net.minecraft.client.gui.screens.stats.slot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.gui.screens.stats.StatsScreen;
import net.minecraft.client.render.Tessellator;
import net.minecraft.localization.Translator;
import net.minecraft.statistics.Stat;
import net.minecraft.statistics.StatList;
import net.minecraft.statistics.stat.MobStat;
import org.lwjgl.input.Mouse;

public class MobsSlot extends StatSlot {
    private List<MobStat> list = new ArrayList<>();

    public MobsSlot(StatsScreen screen) {
        super(screen);

        for (MobStat stat : StatList.mobStats) {
            boolean addSlot = false;
            int entityId = stat.getEntity();
            if (StatList.killStats[entityId] != null && StatsScreen.getStatsFileWriter(screen).getStatCount(StatList.killStats[entityId]) > 0) {
                addSlot = true;
            } else if (StatList.deathStats[entityId] != null && StatsScreen.getStatsFileWriter(screen).getStatCount(StatList.deathStats[entityId]) > 0) {
                addSlot = true;
            }

            if (addSlot) {
                this.list.add(stat);
            }
        }

        this.comparator = new MobsSlot.StatsSorter(this, screen);
    }

    protected final MobStat getStatMob(int index) {
        return this.list.get(index);
    }

    @Override
    protected final int listSize() {
        return this.list.size();
    }

    @Override
    protected void mouseOver(int x, int y) {
        if (y >= this.top && y <= this.bottom && this.listSize() > 0) {
            int width = this.getSlotBounds(x, y);
            int centerX = this.screen.width / 2 - 92 - 16;
            if (width >= 0) {
                if (x < centerX + 40 || x > centerX + 40 + 20) {
                    return;
                }
            } else {
                String name = "";
                if (x >= centerX + 115 - 18 && x <= centerX + 115) {
                    name = this.getStatName(0);
                } else if (x >= centerX + 165 - 18 && x <= centerX + 165) {
                    name = this.getStatName(1);
                } else {
                    if (x < centerX + 215 - 18 || x > centerX + 215) {
                        return;
                    }

                    name = this.getStatName(2);
                }

                name = Translator.getInstance().translateKey(name);
                if (name.length() > 0) {
                    int statX = x + 12;
                    int statY = y - 12;
                    int nameWidth = StatsScreen.getFontRenderer(this.screen).getStringWidth(name);
                    this.drawGradient(statX - 3, statY - 3, statX + nameWidth + 3, statY + 8 + 3, 1610941696, -1607454624, 1.5F);
                    StatsScreen.getFontRenderer(this.screen).drawStringWithShadow(name, statX, statY, -1);
                }
            }
        }
    }

    @Override
    protected void onInteraction(int x, int y, Tessellator tess) {
        if (this.listSize() <= 0) {
            this.drawCenteredString(this.mc.fontRenderer, Translator.getInstance().translateKey("stat.none"), this.width / 2, y, 16777215);
        } else {
            if (!Mouse.isButtonDown(0)) {
                this.sortingId = -1;
            }

            if (this.sortingId == 0) {
                StatsScreen.drawSprite(this.screen, x + 115 - 18, y + 1, 0, 0);
            } else {
                StatsScreen.drawSprite(this.screen, x + 115 - 18, y + 1, 0, 18);
            }

            if (this.sortingId == 1) {
                StatsScreen.drawSprite(this.screen, x + 165 - 18, y + 1, 0, 0);
            } else {
                StatsScreen.drawSprite(this.screen, x + 165 - 18, y + 1, 0, 18);
            }

            if (this.clickedState != -1) {
                short texX = 79;
                byte texY = 18;
                if (this.clickedState == 1) {
                    texX = 129;
                } else if (this.clickedState == 2) {
                    texX = 179;
                }

                if (this.selectedStat == 1) {
                    texY = 36;
                }

                StatsScreen.drawSprite(this.screen, x + texX, y + 1, texY, 0);
            }

            if (this.selectedStat == 0) {
                StatsScreen.drawSprite(this.screen, x + 115 - 18 + 1, y + 1, 94, 18);
            } else {
                StatsScreen.drawSprite(this.screen, x + 115 - 18 + 1, y + 1, 94, 18);
            }

            if (this.selectedStat == 1) {
                StatsScreen.drawSprite(this.screen, x + 165 - 18 + 1, y + 1, 110, 18);
            } else {
                StatsScreen.drawSprite(this.screen, x + 165 - 18 + 1, y + 1, 110, 18);
            }
        }
    }

    @Override
    protected void drawSlot(int id, int x, int y, int offset, Tessellator tess) {
        MobStat stat = this.getStatMob(id);
        int entityId = stat.getEntity();
        y += 20;
        this.drawCenteredString(StatsScreen.getFontRenderer(this.screen), StatList.mobStats.get(entityId).getName(), x + 40, y, -1);
        this.displayStatString((MobStat)StatList.killStats[entityId], x + 115, y, id % 2 == 0);
        this.displayStatString((MobStat)StatList.deathStats[entityId], x + 165, y, id % 2 == 0);
    }

    @Override
    protected String getStatName(int category) {
        return category == 0 ? "stat.kills" : (category == 1 ? "stat.killedBy" : "");
    }

    @Override
    protected void onClick(int x, int y) {
        this.sortingId = -1;
        if (x >= 79 && x < 115) {
            this.sortingId = 0;
        } else if (x >= 129 && x < 165) {
            this.sortingId = 1;
        }

        if (this.sortingId >= 0) {
            this.sortSlots(this.sortingId);
            StatsScreen.getMinecraft(this.screen).sndManager.playSoundFX("random.click", 1.0F, 0.25F);
        }
    }

    @Override
    protected void sortSlots(int id) {
        if (id != this.clickedState) {
            this.clickedState = id;
            this.selectedStat = -1;
        } else if (this.selectedStat == -1) {
            this.selectedStat = 1;
        } else {
            this.clickedState = -1;
            this.selectedStat = 0;
        }

        Collections.sort(this.list, this.comparator);
    }

    public class StatsSorter implements Comparator<Object> {
        final StatsScreen statsGUI;
        final MobsSlot statSlot;

        public StatsSorter(MobsSlot slot, StatsScreen screen) {
            this.statSlot = slot;
            this.statsGUI = screen;
        }

        public int readStatList(MobStat slot1, MobStat slot2) {
            int stat1ID = slot1.getEntity();
            int stat2ID = slot2.getEntity();
            Stat stat1 = null;
            Stat stat2 = null;
            if (this.statSlot.clickedState == 0) {
                stat1 = StatList.killStats[stat1ID];
                stat2 = StatList.killStats[stat2ID];
            } else if (this.statSlot.clickedState == 1) {
                stat1 = StatList.deathStats[stat1ID];
                stat2 = StatList.deathStats[stat2ID];
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

            return stat1ID - stat2ID;
        }

        @Override
        public int compare(Object obj1, Object obj2) {
            return this.readStatList((MobStat)obj1, (MobStat)obj2);
        }
    }
}
