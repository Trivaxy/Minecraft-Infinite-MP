package net.minecraft.statistics.achievement;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.game.block.Block;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemStack;
import net.minecraft.localization.Translator;
import net.minecraft.statistics.Stat;

public class Achievement extends Stat {
    public static List<Achievement> globalList = new ArrayList<>();
    public final Achievement parentAchievement;
    public final ItemStack renderItem;
    public final int displayColumn;
    public final int displayRow;
    private boolean isChallenge;
    private boolean isGoal;

    public Achievement(int id, String name, int column, int row, Item item, Achievement parent) {
        this(id, name, column, row, new ItemStack(item), parent);
    }

    public Achievement(int id, String name, int column, int row, Item item, int meta, Achievement parent) {
        this(id, name, column, row, new ItemStack(item, 1, meta), parent);
    }

    public Achievement(int id, String name, int column, int row, Block block, Achievement parent) {
        this(id, name, column, row, new ItemStack(block), parent);
    }

    public Achievement(int id, String name, int column, int row, Block block, int meta, Achievement parent) {
        this(id, name, column, row, new ItemStack(block, 1, meta), parent);
    }

    public Achievement(int id, String name, int column, int row, ItemStack renderStack, Achievement parent) {
        super(5242880 + id, name);
        this.renderItem = renderStack;
        this.displayColumn = column;
        this.displayRow = row;
        if (column < AchievementList.minDisplayColumn) {
            AchievementList.minDisplayColumn = column;
        }

        if (row < AchievementList.minDisplayRow) {
            AchievementList.minDisplayRow = row;
        }

        if (column > AchievementList.maxDisplayColumn) {
            AchievementList.maxDisplayColumn = column;
        }

        if (row > AchievementList.maxDisplayRow) {
            AchievementList.maxDisplayRow = row;
        }

        this.parentAchievement = parent;
    }

    @Override
    public String getName() {
        return Translator.getInstance().translateKey("achievement." + this.statName);
    }

    public Achievement setIndependent() {
        this.isIndependent = true;
        return this;
    }

    public Achievement setSpecial() {
        this.isChallenge = true;
        return this;
    }

    public Achievement setGoal() {
        this.isGoal = true;
        return this;
    }

    public Achievement registerAchievement() {
        super.registerStat();
        AchievementList.achievementList.add(this);
        globalList.add(this);
        return this;
    }

    public Achievement registerAchievementFarming() {
        super.registerStat();
        AchievementList.husbandryList.add(this);
        globalList.add(this);
        return this;
    }

    public Achievement registerAchievementAdventure() {
        super.registerStat();
        AchievementList.adventureList.add(this);
        globalList.add(this);
        return this;
    }

    public Achievement registerAchievementNether() {
        super.registerStat();
        AchievementList.netherList.add(this);
        globalList.add(this);
        return this;
    }

    public Achievement registerAchievementAether() {
        super.registerStat();
        AchievementList.aetherList.add(this);
        globalList.add(this);
        return this;
    }

    public Achievement registerAchievementVoid() {
        super.registerStat();
        AchievementList.voidList.add(this);
        globalList.add(this);
        return this;
    }

    public Achievement registerAchievementOcean() {
        super.registerStat();
        AchievementList.oceanList.add(this);
        globalList.add(this);
        return this;
    }

    @Override
    public boolean isAchievement() {
        return true;
    }

    public String getDescription() {
        return Translator.getInstance().translateKey("achievement." + this.statName + ".desc");
    }

    public boolean getSpecial() {
        return this.isChallenge;
    }

    public boolean getGoal() {
        return this.isGoal;
    }

    @Override
    public Stat registerStat() {
        return this.registerAchievement();
    }
}
