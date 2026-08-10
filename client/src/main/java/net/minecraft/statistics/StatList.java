package net.minecraft.statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.crafting.CraftingManager;
import net.minecraft.game.item.crafting.Recipe;
import net.minecraft.game.item.crafting.special.FurnaceRecipes;
import net.minecraft.game.item.misc.SpawnEgg;
import net.minecraft.statistics.achievement.AchievementList;
import net.minecraft.statistics.stat.CraftingStat;
import net.minecraft.statistics.stat.GeneralStat;
import net.minecraft.statistics.stat.MobStat;

public class StatList {
    protected static Map<Integer, Stat> oneShotStats = new HashMap<>();
    public static List<Stat> allStats = new ArrayList<>();
    public static List<GeneralStat> generalStats = new ArrayList<>();
    public static List<CraftingStat> itemStats = new ArrayList<>();
    public static List<CraftingStat> blockStats = new ArrayList<>();
    public static List<MobStat> mobStats = new ArrayList<>();
    public static Stat minutesPlayedStat = new GeneralStat(1100, "stat.playTime", Stat.timeStat).registerStat();
    public static Stat distanceWalkedStat = new GeneralStat(2000, "stat.dstWalk", Stat.distanceStat).registerStat();
    public static Stat distanceRanStat = new GeneralStat(2008, "stat.dstSprint", Stat.distanceStat).registerStat();
    public static Stat distanceSneakedStat = new GeneralStat(2005, "stat.dstSneak", Stat.distanceStat).registerStat();
    public static Stat distanceCrawledStat = new GeneralStat(2009, "stat.dstCrawl", Stat.distanceStat).registerStat();
    public static Stat distanceSwumStat = new GeneralStat(2001, "stat.dstSwim", Stat.distanceStat).registerStat();
    public static Stat distanceFallenStat = new GeneralStat(2002, "stat.dstFall", Stat.distanceStat).registerStat();
    public static Stat distanceClimbedStat = new GeneralStat(2003, "stat.dstClimb", Stat.distanceStat).registerStat();
    public static Stat distanceFlownStat = new GeneralStat(2004, "stat.dstFly", Stat.distanceStat).registerStat();
    public static Stat distanceByMinecartStat = new GeneralStat(2006, "stat.dstCart", Stat.distanceStat).registerStat();
    public static Stat distanceByBoatStat = new GeneralStat(2042, "stat.dstBoat", Stat.distanceStat).registerStat();
    public static Stat distanceByHorseStat = new GeneralStat(2098, "stat.dstHorse", Stat.distanceStat).registerStat();
    public static Stat distanceByPigStat = new GeneralStat(2099, "stat.dstPig", Stat.distanceStat).registerStat();
    public static Stat jumpStat = new GeneralStat(2010, "stat.jump").registerStat();
    public static Stat dropStat = new GeneralStat(2011, "stat.drop").registerStat();
    public static Stat collectStat = new GeneralStat(2014, "stat.collect").registerStat();
    public static Stat damageDealtStat = new GeneralStat(2020, "stat.dmg").registerStat();
    public static Stat damageTakenStat = new GeneralStat(2021, "stat.hurt").registerStat();
    public static Stat deathsStat = new GeneralStat(2022, "stat.death").registerStat();
    public static Stat mobKillsStat = new GeneralStat(2023, "stat.kill").registerStat();
    public static Stat bossKillsStat = new GeneralStat(2097, "stat.killBoss").registerStat();
    public static Stat mobScore = new GeneralStat(2024, "stat.score").registerStat();
    public static Stat blocksBrokenStat = new GeneralStat(2026, "stat.mine").registerStat();
    public static Stat blocksPlacedStat = new GeneralStat(2027, "stat.placed").registerStat();
    public static Stat itemsBrokenStat = new GeneralStat(2012, "stat.broken").registerStat();
    public static Stat itemsCraftedStat = new GeneralStat(2013, "stat.craft").registerStat();
    public static Stat itemsEnchantedStat = new GeneralStat(2043, "stat.enchant").registerStat();
    public static Stat itemsReducedStat = new GeneralStat(2045, "stat.reduce").registerStat();
    public static Stat arrows = new GeneralStat(2041, "stat.arrowBlock").registerStat();
    public static Stat potionsBrewed = new GeneralStat(2092, "stat.potion").registerStat();
    public static Stat fishCaughtStat = new GeneralStat(2025, "stat.fish").registerStat();
    public static Stat lightningStrike = new GeneralStat(2076, "stat.lightning").registerStat();
    public static Stat benchUse = new GeneralStat(2028, "stat.workbench").registerStat();
    public static Stat chestUse = new GeneralStat(2029, "stat.chest").registerStat();
    public static Stat furnaceUse = new GeneralStat(2030, "stat.furnace").registerStat();
    public static Stat barrelUse = new GeneralStat(2031, "stat.barrel").registerStat();
    public static Stat blastUse = new GeneralStat(2032, "stat.blast").registerStat();
    public static Stat lecternUse = new GeneralStat(2019, "stat.lectern").registerStat();
    public static Stat crafterUse = new GeneralStat(2018, "stat.crafter").registerStat();
    public static Stat smokerUse = new GeneralStat(2017, "stat.smoker").registerStat();
    public static Stat stonecutterUse = new GeneralStat(2016, "stat.stonecutter").registerStat();
    public static Stat anvilUse = new GeneralStat(2015, "stat.anvil").registerStat();
    public static Stat generatorUse = new GeneralStat(2033, "stat.generator").registerStat();
    public static Stat vacuumUse = new GeneralStat(2046, "stat.vacuum").registerStat();
    public static Stat dispenserUse = new GeneralStat(2047, "stat.dispenser").registerStat();
    public static Stat shelfUse = new GeneralStat(2034, "stat.bookshelf").registerStat();
    public static Stat bedUse = new GeneralStat(2035, "stat.sleep").registerStat();
    public static Stat chairUse = new GeneralStat(2079, "stat.chair").registerStat();
    public static Stat tntUse = new GeneralStat(2080, "stat.tnt").registerStat();
    public static Stat potUse = new GeneralStat(2036, "stat.pot").registerStat();
    public static Stat frameUse = new GeneralStat(2085, "stat.frame").registerStat();
    public static Stat signUse = new GeneralStat(2037, "stat.sign").registerStat();
    public static Stat doorUse = new GeneralStat(2038, "stat.door").registerStat();
    public static Stat pieUse = new GeneralStat(2039, "stat.pie").registerStat();
    public static Stat jukeboxUse = new GeneralStat(2074, "stat.record").registerStat();
    public static Stat pistonUse = new GeneralStat(2075, "stat.pulley").registerStat();
    public static Stat endChestUse = new GeneralStat(2077, "stat.endChest").registerStat();
    public static Stat noteUse = new GeneralStat(2082, "stat.note").registerStat();
    public static Stat hellforgeUse = new GeneralStat(2086, "stat.hellforge").registerStat();
    public static Stat teleporterUse = new GeneralStat(2087, "stat.teleporter").registerStat();
    public static Stat cauldronUse = new GeneralStat(2040, "stat.cauldron").registerStat();
    public static Stat beaconUse = new GeneralStat(2095, "stat.beaconUse").registerStat();
    public static Stat grindstoneUse = new GeneralStat(2096, "stat.grindstoneUse").registerStat();
    public static Stat berryGathered = new GeneralStat(2078, "stat.berry").registerStat();
    public static Stat treeGrowth = new GeneralStat(2083, "stat.tree").registerStat();
    public static Stat mushroomGrowth = new GeneralStat(2084, "stat.shroom").registerStat();
    public static Stat flowerGrowth = new GeneralStat(2090, "stat.flower").registerStat();
    public static Stat sheepShear = new GeneralStat(2091, "stat.shears").registerStat();
    public static Stat slipVisits = new GeneralStat(2093, "stat.slipVisits").registerStat();
    public static Stat skyVisits = new GeneralStat(2094, "stat.skyVisits").registerStat();
    public static Stat voidVisits = new GeneralStat(2044, "stat.voidVisits").registerStat();
    public static Stat[] mineStats = initMinableStats("stat.block", 16777216);
    public static Stat[] craftStats;
    public static Stat[] useStats;
    public static Stat[] breakStats;
    public static Stat[] pickupStats;
    public static Stat[] dropStats;
    public static Stat[] killStats = initKillStats(StatList.killStats, "stat.kills", 2240, 0, 100);
    public static Stat[] deathStats = initKillStats(StatList.deathStats, "stat.deaths", 2340, 0, 100);
    private static boolean blockStatsInitialized = false;
    private static boolean itemStatsInitialized = false;

    public static void init() {
    }

    public static void initBlockStats() {
        useStats = initUsableStats(useStats, "stat.place", 16908288, 0, 4096);
        breakStats = initBreakStats(breakStats, "stat.break", 16973824, 0, 4096);
        pickupStats = initPickupStats(pickupStats, "stat.obtain", 17908288, 0, 4096);
        dropStats = initPickupStats(dropStats, "stat.dispose", 17973824, 0, 4096);
        blockStatsInitialized = true;
        initCraftableStats();
    }

    public static void initItemStats() {
        useStats = initUsableStats(useStats, "stat.use", 16908288, 4096, 10240);
        breakStats = initBreakStats(breakStats, "stat.break", 16973824, 4096, 10240);
        pickupStats = initPickupStats(pickupStats, "stat.obtain", 17908288, 4096, 10240);
        dropStats = initPickupStats(dropStats, "stat.dispose", 17973824, 4096, 10240);
        itemStatsInitialized = true;
        initCraftableStats();
    }

    public static void initCraftableStats() {
        if (blockStatsInitialized && itemStatsInitialized) {
            HashSet<Integer> set = new HashSet<>();

            for (Recipe recipe : CraftingManager.getInstance().getRecipeList()) {
                if (recipe.getRecipeOutput() != null) {
                    set.add(recipe.getRecipeOutput().itemID);
                }
            }

            for (ItemStack stack : FurnaceRecipes.smelting().getSmeltingList().values()) {
                set.add(stack.itemID);
            }

            craftStats = new Stat[10240];

            for (Integer id : set) {
                if (ItemList.items[id] != null) {
                    String var3 = ItemList.items[id].getNameFromMeta(0);
                    craftStats[id] = new CraftingStat(16842752 + id, var3, id).registerStat();
                }
            }

            replaceAllSimilarBlocks(craftStats);
        }
    }

    private static Stat[] initMinableStats(String name, int id) {
        Stat[] stats = new Stat[4096];

        for (int index = 0; index < 4096; index++) {
            if (BlockList.blocks[index] != null && BlockList.blocks[index].tracksStats()) {
                String blockName = BlockList.blocks[index].name;
                stats[index] = new CraftingStat(id + index, blockName, index).registerStat();
                blockStats.add((CraftingStat)stats[index]);
            }
        }

        replaceAllSimilarBlocks(stats);
        return stats;
    }

    private static Stat[] initUsableStats(Stat[] stats, String name, int id, int min, int max) {
        if (stats == null) {
            stats = new Stat[32000];
        }

        for (int index = min; index < max; index++) {
            if (ItemList.items[index] != null) {
                String itemName = ItemList.items[index].getNameFromMeta(0);
                stats[index] = new CraftingStat(id + index, itemName, index).registerStat();
                if (index >= 4096) {
                    itemStats.add((CraftingStat)stats[index]);
                }
            }
        }

        replaceAllSimilarBlocks(stats);
        return stats;
    }

    private static Stat[] initBreakStats(Stat[] stats, String name, int id, int min, int max) {
        if (stats == null) {
            stats = new Stat[32000];
        }

        for (int index = min; index < max; index++) {
            if (ItemList.items[index] != null && ItemList.items[index].isDamageable()) {
                String itemName = ItemList.items[index].getNameFromMeta(0);
                stats[index] = new CraftingStat(id + index, itemName, index).registerStat();
            }
        }

        replaceAllSimilarBlocks(stats);
        return stats;
    }

    private static Stat[] initPickupStats(Stat[] stats, String name, int id, int min, int max) {
        if (stats == null) {
            stats = new Stat[32000];
        }

        for (int index = min; index < max; index++) {
            if (ItemList.items[index] != null) {
                String itemName = ItemList.items[index].getNameFromMeta(0);
                stats[index] = new CraftingStat(id + index, itemName, index).registerStat();
            }
        }

        replaceAllSimilarBlocks(stats);
        return stats;
    }

    public static Stat[] initKillStats(Stat[] stats, String name, int id, int min, int max) {
        if (stats == null) {
            stats = new Stat[256];
        }

        for (int index = min; index < max; index++) {
            if (index < SpawnEgg.spawnedEntities.length && SpawnEgg.spawnedEntities[index] != null) {
                String mobName = SpawnEgg.spawnedEntities[index];
                stats[index] = new MobStat(id + index, mobName, index).registerStat();
            }
        }

        return stats;
    }

    private static Stat[] initMobStats(String name, int id) {
        Stat[] stats = new Stat[256];

        for (int index = 0; index < 256; index++) {
            if (index < SpawnEgg.spawnedEntities.length) {
                String mobName = SpawnEgg.spawnedEntities[index];
                stats[index] = new MobStat(id + index, mobName, index).registerStat();
                mobStats.add((MobStat)stats[index]);
            }
        }

        return stats;
    }

    private static void replaceAllSimilarBlocks(Stat[] stats) {
        replaceSimilarBlocks(stats, BlockList.waterStill.id, BlockList.waterMoving.id);
        replaceSimilarBlocks(stats, BlockList.lavaStill.id, BlockList.lavaMoving.id);
        replaceSimilarBlocks(stats, BlockList.stoneOvenActive.id, BlockList.stoneOvenIdle.id);
        replaceSimilarBlocks(stats, BlockList.steelOvenActive.id, BlockList.steelOvenIdle.id);
        replaceSimilarBlocks(stats, BlockList.smokerActive.id, BlockList.smokerIdle.id);
        replaceSimilarBlocks(stats, BlockList.generatorActive.id, BlockList.generator.id);
        replaceSimilarBlocks(stats, BlockList.doubleSlab.id, BlockList.slabStone.id);
        replaceSimilarBlocks(stats, BlockList.doubleSlabCobble.id, BlockList.slabCobble.id);
        replaceSimilarBlocks(stats, BlockList.doubleSlabStoneBrick.id, BlockList.slabStoneBrick.id);
        replaceSimilarBlocks(stats, BlockList.doubleSlabSandstone.id, BlockList.slabSandstone.id);
        replaceSimilarBlocks(stats, BlockList.doubleBrick.id, BlockList.slabBrick.id);
        replaceSimilarBlocks(stats, BlockList.doubleSlabMoss.id, BlockList.slabMoss.id);
        replaceSimilarBlocks(stats, BlockList.doubleSlabObsidian.id, BlockList.slabObsidian.id);
        replaceSimilarBlocks(stats, BlockList.doubleSlabWood.id, BlockList.slabWood.id);
        replaceSimilarBlocks(stats, BlockList.pulleyBaseActive.id, BlockList.pulleyBase.id);
        replaceSimilarBlocks(stats, BlockList.pulleyStickyBaseActive.id, BlockList.pulleyStickyBase.id);
        replaceSimilarBlocks(stats, BlockList.pulleyExtension.id, BlockList.pulleyBase.id);
        replaceSimilarBlocks(stats, BlockList.pulleyMoving.id, BlockList.pulleyBase.id);
        replaceSimilarBlocks(stats, BlockList.lampLit.id, BlockList.lamp.id);
        replaceSimilarBlocks(stats, BlockList.pebble.id, BlockList.stone.id);
    }

    private static void replaceSimilarBlocks(Stat[] stats, int id, int replaceID) {
        if (stats[id] != null && stats[replaceID] == null) {
            stats[replaceID] = stats[id];
        } else {
            allStats.remove(stats[id]);
            blockStats.remove(stats[id]);
            generalStats.remove(stats[id]);
            stats[id] = stats[replaceID];
        }
    }

    public static Stat getOneShotStat(int id) {
        return oneShotStats.get(id);
    }

    static {
        AchievementList.init();
        initMobStats("stat.totalKills", 2140);
    }
}
