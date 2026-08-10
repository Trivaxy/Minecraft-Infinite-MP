package net.minecraft.game.item.misc;

import net.minecraft.game.entity.hanging.Frame;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;

public class ItemFrame extends Item {
    public ItemFrame(int id) {
        super(id);
        this.priorityItem = true;
    }

    @Override
    public final boolean useOnBlock(ItemStack stack, Player player, World world, int x, int y, int z, int side) {
        if (world.isOnline) {
            return false;
        }

        byte direction = 0;
        if (side == 4) {
            direction = 1;
        }

        if (side == 3) {
            direction = 2;
        }

        if (side == 5) {
            direction = 3;
        }

        if (side == 1) {
            direction = 4;
        }

        if (side == 0) {
            direction = 5;
        }

        Frame entityFrame = new Frame(world, x, y, z, direction, this == ItemList.itemFrameGlowing);
        if (entityFrame.onValidSurface()) {
            world.spawnEntity(entityFrame);
            if (player.properties.gamemode != 1) {
                stack.stackSize--;
            }

            player.addStat(StatList.useStats[this.id], 1);
        }

        return true;
    }
}
