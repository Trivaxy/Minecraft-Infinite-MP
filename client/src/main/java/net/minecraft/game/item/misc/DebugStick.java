package net.minecraft.game.item.misc;

import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.game.world.edit.WorldEdit;

public class DebugStick extends Item {
    public DebugStick(int itemid) {
        super(itemid);
    }

    @Override
    public boolean useOnBlock(ItemStack itemStack, Player entityPlayer, World world, int x, int y, int z, int face) {
        if (entityPlayer.sneaking) {
            WorldEdit.setCorner2(x, y, z);
            world.getListener(0).showString("2nd point set to " + x + ", " + y + ", " + z);
        } else {
            WorldEdit.setCorner1(x, y, z);
            world.getListener(0).showString("1st point set to " + x + ", " + y + ", " + z);
        }

        return true;
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}
