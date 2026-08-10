package net.minecraft.client.gui.screens.stats.slot;

import net.minecraft.client.gui.GuiList;
import net.minecraft.client.gui.screens.stats.StatsScreen;
import net.minecraft.client.render.Tessellator;
import net.minecraft.statistics.Stat;
import net.minecraft.statistics.StatList;

public class GeneralSlot extends GuiList {
    final StatsScreen screen;

    public GeneralSlot(StatsScreen parent) {
        super(StatsScreen.getMinecraft(parent), parent.width, parent.height, 32, parent.height - 64, 10);
        this.screen = parent;
    }

    @Override
    protected int listSize() {
        return StatList.generalStats.size();
    }

    @Override
    protected void elementClicked(int click, boolean state) {
    }

    @Override
    protected boolean isSelected(int id) {
        return false;
    }

    @Override
    protected int contentSize() {
        return this.listSize() * 10;
    }

    @Override
    protected void drawBackground() {
        this.screen.drawBackground();
    }

    @Override
    protected void drawSlot(int id, int x, int y, int offset, Tessellator tess) {
        Stat stat = StatList.generalStats.get(id);
        this.drawString(StatsScreen.getFontRenderer(this.screen), stat.getName(), x + 2, y + 1, id % 2 == 0 ? 16777215 : 9474192);
        String statCount = stat.getTypeName(StatsScreen.getStatsFileWriter(this.screen).getStatCount(stat));
        this.drawString(
            StatsScreen.getFontRenderer(this.screen),
            statCount,
            x + 2 + 213 - StatsScreen.getFontRenderer(this.screen).getStringWidth(statCount),
            y + 1,
            id % 2 == 0 ? 16777215 : 9474192
        );
    }
}
