package net.minecraft.game.item.misc;

import net.minecraft.game.entity.hanging.Painting;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;

public class PaintingItem extends Item {
    public PaintingItem(int id) {
        super(id);
        this.priorityItem = true;
    }

    @Override
    public boolean useOnBlock(ItemStack stack, Player player, World world, int xCoord, int yCoord, int zCoord, int side) {
        if (side != 0 && side != 1) {
            byte dir = 0;
            if (side == 4) {
                dir = 1;
            }

            if (side == 3) {
                dir = 2;
            }

            if (side == 5) {
                dir = 3;
            }

            if (new Painting(world, xCoord, yCoord, zCoord, dir, stack.getItemDamage()).onValidSurface()) {
                player.displayGUIPainting(xCoord, yCoord, zCoord, dir);
            }

            return true;
        } else {
            return false;
        }
    }
}
