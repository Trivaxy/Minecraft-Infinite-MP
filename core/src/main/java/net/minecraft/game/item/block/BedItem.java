package net.minecraft.game.item.block;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.decoration.Bed;
import net.minecraft.game.block.stepsound.StepSound;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;
import net.minecraft.util.math.MathHelper;

public class BedItem extends Item {
    private Block bed;

    public BedItem(int id, Block block) {
        super(id);
        this.priorityItem = true;
        this.bed = block;
    }

    @Override
    public boolean useOnBlock(ItemStack stack, Player player, World world, int x, int y, int z, int side) {
        if (side != 1) {
            return false;
        }

        y++;
        Bed bed = (Bed)this.bed;
        int playerFacing = MathHelper.floor_double(player.yaw * 4.0F / 360.0F + 0.5) & 3;
        byte xOffset = 0;
        byte zOffset = 0;
        if (playerFacing == 0) {
            zOffset = 1;
        }

        if (playerFacing == 1) {
            xOffset = -1;
        }

        if (playerFacing == 2) {
            zOffset = -1;
        }

        if (playerFacing == 3) {
            xOffset = 1;
        }

        if (world.isAir(x, y, z)
            && world.isAir(x + xOffset, y, z + zOffset)
            && world.isBlockFull(x, y - 1, z)
            && world.isBlockFull(x + xOffset, y - 1, z + zOffset)) {
            world.setBlockAndMetadataWithNotify(x, y, z, bed.id, playerFacing);
            world.setBlockAndMetadataWithNotify(x + xOffset, y, z + zOffset, bed.id, playerFacing + 8);
            StepSound snd = bed.getStepSound(0);
            world.playBlockSound(x + 0.5F, y + 0.5F, z + 0.5F, snd.getSound(), (snd.getVolume() + 1.0F) / 2.0F, snd.getPitch() * 0.8F);
            if (player.properties.gamemode != 1) {
                stack.stackSize--;
            }

            player.addStat(StatList.useStats[this.id], 1);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.itemIcon = ir.registerIcon("beds/" + this.internalName);
    }
}
