package net.minecraft.game.item.misc;

import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.misc.VoidCrystal;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;

public class CrystalItem extends Item {
    public CrystalItem(int itemid) {
        super(itemid);
        this.priorityItem = true;
    }

    @Override
    public final boolean useOnBlock(ItemStack stack, Player player, World world, int x, int y, int z, int side) {
        if (side == 0) {
            y--;
        }

        if (side == 1) {
            y++;
        }

        if (side == 2) {
            z--;
        }

        if (side == 3) {
            z++;
        }

        if (side == 4) {
            x--;
        }

        if (side == 5) {
            x++;
        }

        if (world.getBlockId(x, y, z) == 0 || world.getBlockMaterial(x, y, z) == MaterialList.water) {
            VoidCrystal crystal = new VoidCrystal(world);
            if (!world.isOnline && world.isSolid(x, y - 1, z)) {
                crystal.teleportTo(x + 0.5, y, z + 0.5, 0.0F, 0.0F);
                crystal.playerPlaced = true;
                world.spawnEntity(crystal);
                if (player.properties.gamemode != 1) {
                    stack.stackSize--;
                }

                player.addStat(StatList.useStats[this.id], 1);
            }
        }

        return true;
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}
