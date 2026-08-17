package net.minecraft.game.item.misc;

import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;

public class DebugStick extends Item {
    public DebugStick(int itemid) {
        super(itemid);
    }

    @Override
    public boolean useOnBlock(ItemStack itemStack, Player entityPlayer, World world, int x, int y, int z, int face) {
        world.getListener(0).showString(x + ", " + y + ", " + z + " (id " + world.getBlockId(x, y, z) + ":" + world.getBlockMetadata(x, y, z) + ")");
        return true;
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}
