package net.minecraft.game.item.block;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.stepsound.StepSound;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;

public class PlaceableItem extends Item {
    private int ID;

    public PlaceableItem(int id, Block block) {
        super(id);
        this.ID = block.id;
        this.priorityItem = true;
    }

    @Override
    public boolean useOnBlock(ItemStack stack, Player player, World world, int x, int y, int z, int side) {
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

        if (stack.stackSize == 0) {
            return false;
        }

        Block block = BlockList.blocks[this.ID];
        if (block.placeableAt(world, x, y, z) && world.canPlace(this.ID, x, y, z, false, side) && world.setBlockWithNotify(x, y, z, this.ID)) {
            block.onPlaced(world, player, x, y, z, side);
            StepSound snd = block.getStepSound(0);
            world.playBlockSound(player, x + 0.5F, y + 0.5F, z + 0.5F, snd.getSound(), (snd.getVolume() + 1.0F) / 2.0F, snd.getPitch() * 0.8F);
            if (player.properties.gamemode != 1) {
                stack.stackSize--;
            }
        }

        player.addStat(StatList.useStats[this.id], 1);
        return true;
    }
}
