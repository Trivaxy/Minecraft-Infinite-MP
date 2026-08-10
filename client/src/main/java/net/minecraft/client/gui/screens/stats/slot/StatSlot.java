package net.minecraft.client.gui.screens.stats.slot;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.gui.GuiList;
import net.minecraft.client.gui.screens.stats.StatsScreen;
import net.minecraft.client.render.Tessellator;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.localization.Translator;
import net.minecraft.statistics.Stat;
import net.minecraft.statistics.stat.CraftingStat;
import org.lwjgl.input.Mouse;

abstract class StatSlot extends GuiList {
    protected List<CraftingStat> statsList;
    protected Comparator<Object> comparator;
    public final StatsScreen screen;
    public int selectedStat;
    public int clickedState;
    protected int sortingId;

    protected StatSlot(StatsScreen parent) {
        super(StatsScreen.getMinecraft(parent), parent.width, parent.height, 32, parent.height - 64, 20);
        this.screen = parent;
        this.sortingId = -1;
        this.clickedState = -1;
        this.selectedStat = 0;
        this.canInteract = true;
        this.listOffset = 20;
    }

    @Override
    protected void elementClicked(int click, boolean state) {
    }

    @Override
    protected boolean isSelected(int id) {
        return false;
    }

    @Override
    protected void drawBackground() {
        this.screen.drawBackground();
    }

    @Override
    protected void onInteraction(int x, int y, Tessellator tess) {
        if (this.listSize() > 0) {
            if (!Mouse.isButtonDown(0)) {
                this.sortingId = -1;
            }

            if (this.sortingId == 0) {
                StatsScreen.drawSprite(this.screen, x + 55 - 18, y + 1, 0, 0);
            } else {
                StatsScreen.drawSprite(this.screen, x + 55 - 18, y + 1, 0, 18);
            }

            if (this.sortingId == 1) {
                StatsScreen.drawSprite(this.screen, x + 95 - 18, y + 1, 0, 0);
            } else {
                StatsScreen.drawSprite(this.screen, x + 95 - 18, y + 1, 0, 18);
            }

            if (this.sortingId == 2) {
                StatsScreen.drawSprite(this.screen, x + 135 - 18, y + 1, 0, 0);
            } else {
                StatsScreen.drawSprite(this.screen, x + 135 - 18, y + 1, 0, 18);
            }

            if (this.sortingId == 3) {
                StatsScreen.drawSprite(this.screen, x + 175 - 18, y + 1, 0, 0);
            } else {
                StatsScreen.drawSprite(this.screen, x + 175 - 18, y + 1, 0, 18);
            }

            if (this.sortingId == 4) {
                StatsScreen.drawSprite(this.screen, x + 215 - 18, y + 1, 0, 0);
            } else {
                StatsScreen.drawSprite(this.screen, x + 215 - 18, y + 1, 0, 18);
            }

            if (this.clickedState != -1) {
                short imgX = 19;
                byte imgY = 18;
                if (this.clickedState == 1) {
                    imgX = 59;
                } else if (this.clickedState == 2) {
                    imgX = 99;
                } else if (this.clickedState == 3) {
                    imgX = 139;
                } else if (this.clickedState == 4) {
                    imgX = 179;
                }

                if (this.selectedStat == 1) {
                    imgY = 36;
                }

                StatsScreen.drawSprite(this.screen, x + imgX, y + 1, imgY, 0);
            }
        }
    }

    @Override
    protected void onClick(int x, int y) {
        this.sortingId = -1;
        if (x >= 25 && x < 55) {
            this.sortingId = 0;
        } else if (x >= 60 && x < 95) {
            this.sortingId = 1;
        } else if (x >= 110 && x < 135) {
            this.sortingId = 2;
        } else if (x >= 160 && x < 175) {
            this.sortingId = 3;
        } else if (x >= 190 && x < 215) {
            this.sortingId = 4;
        }

        if (this.sortingId >= 0 && this.listSize() > 0) {
            this.sortSlots(this.sortingId);
            StatsScreen.getMinecraft(this.screen).sndManager.playSoundFX("random.click", 1.0F, 0.25F);
        }
    }

    @Override
    protected int listSize() {
        return this.statsList.size();
    }

    protected final CraftingStat getStat(int index) {
        return this.statsList.get(index);
    }

    protected abstract String getStatName(int var1);

    protected void displayStatString(Stat stat, int x, int y, boolean shadow) {
        if (this.listSize() > 0) {
            if (stat != null) {
                String statName = stat.getTypeName(StatsScreen.getStatsFileWriter(this.screen).getStatCount(stat));
                this.drawString(
                    StatsScreen.getFontRenderer(this.screen),
                    statName,
                    x - StatsScreen.getFontRenderer(this.screen).getStringWidth(statName),
                    y + 5,
                    shadow ? 16777215 : 9474192
                );
            } else {
                String statName = "-";
                this.drawString(
                    StatsScreen.getFontRenderer(this.screen),
                    statName,
                    x - StatsScreen.getFontRenderer(this.screen).getStringWidth(statName),
                    y + 5,
                    shadow ? 16777215 : 9474192
                );
            }
        }
    }

    @Override
    protected void mouseOver(int x, int y) {
        if (y >= this.top && y <= this.bottom && this.listSize() > 0) {
            int slotWidth = this.getSlotBounds(x, y);
            int centerWidth = this.screen.width / 2 - 92 - 16;
            if (slotWidth >= 0) {
                if (x < centerWidth || x > centerWidth + 20) {
                    return;
                }

                CraftingStat stat = this.getStat(slotWidth);
                this.drawSlot(stat, x, y);
            } else {
                String statName = "";
                if (x >= centerWidth + 55 - 18 && x <= centerWidth + 55) {
                    statName = this.getStatName(0);
                } else if (x >= centerWidth + 95 - 18 && x <= centerWidth + 95) {
                    statName = this.getStatName(1);
                } else if (x >= centerWidth + 135 - 18 && x <= centerWidth + 135) {
                    statName = this.getStatName(2);
                } else if (x >= centerWidth + 175 - 18 && x <= centerWidth + 175) {
                    statName = this.getStatName(3);
                } else {
                    if (x < centerWidth + 215 - 18 || x > centerWidth + 215) {
                        return;
                    }

                    statName = this.getStatName(4);
                }

                statName = Translator.getInstance().translateKey(statName);
                if (statName.length() > 0) {
                    int statX = x + 12;
                    int statY = y - 12;
                    int nameWidth = StatsScreen.getFontRenderer(this.screen).getStringWidth(statName);
                    this.drawGradient(statX - 3, statY - 3, statX + nameWidth + 3, statY + 8 + 3, 1610941696, -1607454624, 1.5F);
                    StatsScreen.getFontRenderer(this.screen).drawStringWithShadow(statName, statX, statY, -1);
                }
            }
        }
    }

    protected void drawSlot(CraftingStat stat, int x, int y) {
        if (stat != null && this.listSize() > 0) {
            Item item = ItemList.items[stat.getItemID()];
            String itemName = Translator.getInstance().translateNamedKey(item.fullName);
            if (stat.getItemID() < 4096) {
                Block block = BlockList.blocks[stat.getItemID()];
                if (block.hasStates) {
                    itemName = Translator.getInstance().translateNamedKey("tile." + block.names[0]);
                } else {
                    itemName = Translator.getInstance().translateNamedKey("tile." + block.name);
                }
            }

            if (itemName.length() > 0) {
                int slotX = x + 12;
                int slotY = y - 12;
                int nameWidth = StatsScreen.getFontRenderer(this.screen).getStringWidth(itemName);
                this.drawGradient(slotX - 3, slotY - 3, slotX + nameWidth + 3, slotY + 8 + 3, 1610941696, -1607454624, 1.5F);
                StatsScreen.getFontRenderer(this.screen).drawStringWithShadow(itemName, slotX, slotY, -1);
            }
        }
    }

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

        Collections.sort(this.statsList, this.comparator);
    }
}
