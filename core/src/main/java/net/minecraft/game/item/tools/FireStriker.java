package net.minecraft.game.item.tools;

import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;

public class FireStriker extends Item {
    public FireStriker(int id) {
        super(id);
        this.maxStackSize = 1;
        this.maxDamage = 64;
        this.info.add(this.maxDamage + " item.desc.durability");
        this.priorityItem = true;
    }

    @Override
    public boolean useOnBlock(ItemStack stack, Player player, World world, int xCoord, int yCoord, int zCoord, int side) {
        if (side == 0) {
            yCoord--;
        }

        if (side == 1) {
            yCoord++;
        }

        if (side == 2) {
            zCoord--;
        }

        if (side == 3) {
            zCoord++;
        }

        if (side == 4) {
            xCoord--;
        }

        if (side == 5) {
            xCoord++;
        }

        int id = world.getBlockId(xCoord, yCoord, zCoord);
        if (id != 0) {
            return false;
        }

        world.playSoundEffect(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, "fire.ignite", 1.0F, rand.nextFloat() * 0.4F + 0.8F);
        if (world.getBlock(xCoord, yCoord - 1, zCoord) != BlockList.slowSand && world.getBlock(xCoord, yCoord - 1, zCoord) != BlockList.soulSoil) {
            world.setBlockWithNotify(xCoord, yCoord, zCoord, BlockList.fire.id);
        } else {
            world.setBlockWithNotify(xCoord, yCoord, zCoord, BlockList.hellfire.id);
        }

        player.addStat(StatList.useStats[this.id], 1);
        stack.damageItem(1, player);
        return true;
    }

    @Override
    public boolean isRepairable(ItemStack stack1, ItemStack stack2) {
        return stack2 == null
            ? super.isRepairable(stack1, stack2)
            : stack2.itemID == ItemList.ingotIron.id || stack2.itemID == ItemList.flint.id || stack2.itemID == this.id;
    }

    @Override
    public boolean isEnchantable(ItemStack stack1, ItemStack stack2) {
        return stack2 != null && stack2.getItem() == ItemList.scroll && stack2.itemDmg == 3;
    }

    @Override
    public int repairCost() {
        return 100;
    }
}
