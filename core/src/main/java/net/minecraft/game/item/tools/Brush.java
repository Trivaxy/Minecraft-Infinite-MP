package net.minecraft.game.item.tools;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;
import net.minecraft.statistics.achievement.AchievementList;
import net.minecraft.util.random.WeightedRandom;

public class Brush extends Item {
    private static Brush.LootHolder[] loots;
    private static Brush.LootHolder[] treasures;
    private int xCoord;
    private int yCoord;
    private int zCoord;

    public Brush(int itemid) {
        super(itemid);
        this.maxDamage = 64;
        this.info.add(this.maxDamage + " item.desc.durability");
        this.outputId = 4363;
        this.outputCount = 1;
        this.priorityItem = true;
        this.canSwing = false;
    }

    @Override
    public boolean isRepairable(ItemStack stack1, ItemStack stack2) {
        return stack2 != null && (stack2.itemID == ItemList.ingotCopper.id || stack2.itemID == this.id);
    }

    @Override
    public boolean isEnchantable(ItemStack stack1, ItemStack stack2) {
        return stack2 != null && stack2.getItem() == ItemList.scroll && stack2.itemDmg == 3;
    }

    @Override
    public int repairCost() {
        return 100;
    }

    @Override
    public int getMaxUseDuration(ItemStack stack) {
        return 30;
    }

    @Override
    public int getDamageVsEntity(Entity entity) {
        return 0;
    }

    @Override
    public Item.Action getItemUseAction(ItemStack stack) {
        return Item.Action.dig;
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, Player player, int itemInUseCount) {
        int id = world.getBlockId(this.xCoord, this.yCoord, this.zCoord);
        boolean validBlock = id == BlockList.susSand.id || id == BlockList.susGravel.id || id == BlockList.susDirt.id;
        if (player.itemAnimFrames <= 1 && validBlock) {
            int outputId = id == BlockList.susSand.id ? BlockList.sand.id : (id == BlockList.susGravel.id ? BlockList.gravel.id : BlockList.dirt.id);
            if (validBlock) {
                world.setBlockWithNotify(this.xCoord, this.yCoord, this.zCoord, outputId);
            }

            if (player.inventory.getOffHandItem() != null && player.itemInUse == player.inventory.getOffHandItem()) {
                player.inventory.getOffHandItem().damageItem(1, player);
            } else {
                player.getCurrentEquippedItem().damageItem(1, player);
            }

            Brush.LootHolder randomItem = WeightedRandom.getRandomItem(world.rand, getLoots());
            int chance = player.getLuckModifier() ? 2 : 5;
            if (rand.nextInt(chance) == 0) {
                randomItem = WeightedRandom.getRandomItem(world.rand, getTreasures());
            }

            ItemStack drop = new ItemStack(randomItem.itemId, 1);
            if (randomItem.itemId == ItemList.scroll.id || randomItem.itemId == ItemList.scrap.id) {
                drop = new ItemStack(randomItem.itemId, 1, world.rand.nextInt(6));
            }

            DroppedItem item = new DroppedItem(world, this.xCoord, this.yCoord + 1.5, this.zCoord, drop);
            world.spawnEntity(item);
            player.addStat(StatList.useStats[this.id], 1);
            player.triggerAchievement(AchievementList.archeology);
        }
    }

    @Override
    public boolean useOnBlock(ItemStack stack, Player player, World world, int xCoord, int yCoord, int zCoord, int side) {
        player.setItemInUse(stack, this.getMaxUseDuration(stack));
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.zCoord = zCoord;
        return false;
    }

    public static Brush.LootHolder[] getLoots() {
        if (loots == null) {
            loots = new Brush.LootHolder[]{
                new Brush.LootHolder(20, ItemList.coal),
                new Brush.LootHolder(15, BlockList.skull),
                new Brush.LootHolder(25, ItemList.bone),
                new Brush.LootHolder(20, ItemList.book),
                new Brush.LootHolder(15, ItemList.glassBottle),
                new Brush.LootHolder(20, ItemList.arrow),
                new Brush.LootHolder(30, ItemList.brick),
                new Brush.LootHolder(35, ItemList.rock),
                new Brush.LootHolder(15, ItemList.flowerPot),
                new Brush.LootHolder(10, BlockList.pot),
                new Brush.LootHolder(15, ItemList.flint),
                new Brush.LootHolder(30, BlockList.deadBush)
            };
        }

        return loots;
    }

    public static Brush.LootHolder[] getTreasures() {
        if (treasures == null) {
            treasures = new Brush.LootHolder[]{
                new Brush.LootHolder(1, ItemList.recordCopper),
                new Brush.LootHolder(5, ItemList.scroll),
                new Brush.LootHolder(1, ItemList.scrap),
                new Brush.LootHolder(1, ItemList.diamond),
                new Brush.LootHolder(3, ItemList.ruby),
                new Brush.LootHolder(3, ItemList.sapphire),
                new Brush.LootHolder(3, ItemList.emerald),
                new Brush.LootHolder(5, ItemList.ingotGold),
                new Brush.LootHolder(10, ItemList.ingotIron),
                new Brush.LootHolder(20, ItemList.ingotCopper)
            };
        }

        return treasures;
    }

    public static class LootHolder extends WeightedRandom.Choice {
        public final int itemId;

        public LootHolder(int weight, Item item) {
            super(weight);
            this.itemId = item.id;
        }

        public LootHolder(int weight, Block cock) {
            super(weight);
            this.itemId = cock.id;
        }
    }
}
