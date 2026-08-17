package net.minecraft.game.item.misc;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.plants.Crops;
import net.minecraft.game.block.plants.Flower;
import net.minecraft.game.block.plants.Stem;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.staff.NatureStaff;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;
import net.minecraft.statistics.achievement.AchievementList;

public class Fertilizer extends Item {
    private Random itemRand = new Random();

    public Fertilizer(int itemID) {
        super(itemID);
        this.priorityItem = true;
    }

    @Override
    public boolean useOnBlock(ItemStack stack, Player player, World world, int x, int y, int z, int side) {
        int id = world.getBlockId(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        boolean isWorldSurvival = player.properties.gamemode != 1;
        boolean condition = false;
        if (BlockList.blocks[id] instanceof Flower) {
            if (rand.nextInt(3) == 0) {
                ((Flower)BlockList.blocks[id]).growTree(world, player, x, y, z, world.rand);
            }

            condition = true;
        }

        if (BlockList.blocks[id] instanceof Stem || BlockList.blocks[id] instanceof Crops) {
            meta += rand.nextInt(5) + 1;
            BlockList.blocks[id].fertilize(world, x, y, z, meta);
            condition = true;
        }

        if (id == BlockList.grass.id) {
            ((NatureStaff)ItemList.natureStaff).growPlants(world, stack, player, x, y, z, id, meta);
            condition = true;
        } else if (id == BlockList.voidGrass.id) {
            ((NatureStaff)ItemList.natureStaff).growSpecificPlant(world, stack, player, x, y, z, id, rand.nextInt(2), BlockList.glowGrass.id);
            condition = true;
        } else if (id == BlockList.sand.id) {
            ((NatureStaff)ItemList.natureStaff).growSpecificPlant(world, stack, player, x, y, z, id, 0, BlockList.duneGrass.id);
            condition = true;
        } else if (id == BlockList.moss.id) {
            ((NatureStaff)ItemList.natureStaff).growSpecificPlant(world, stack, player, x, y, z, id, 2, BlockList.tallGrass.id);
            condition = true;
        }

        if (condition) {
            if (isWorldSurvival) {
                stack.stackSize--;
            }

            player.addStat(StatList.useStats[this.id], 1);
            player.triggerAchievement(AchievementList.fertilizer);
            world.playBlockSound(player, x, y, z, "random.bonemeal", 1.0F, 1.0F / (this.itemRand.nextFloat() * 0.4F + 1.2F));
        }

        return condition;
    }
}
