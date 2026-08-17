package net.minecraft.game.item.block;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.stepsound.StepSound;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;
import net.minecraft.util.math.MathHelper;

public class DoorItem extends Item {
    Block door;

    public DoorItem(int id, Block block) {
        super(id);
        this.door = block;
        this.priorityItem = true;
    }

    @Override
    public boolean useOnBlock(ItemStack stack, Player player, World world, int xCoord, int yCoord, int zCoord, int side) {
        if (side != 1) {
            return false;
        }

        if (!this.door.placeableAt(world, xCoord, ++yCoord, zCoord)) {
            return false;
        }

        int facing = MathHelper.floor_double((player.yaw + 180.0F) * 4.0F / 360.0F - 0.5) & 3;
        placeDoorBlock(world, xCoord, yCoord, zCoord, facing, this.door, player);
        if (player.properties.gamemode != 1) {
            stack.stackSize--;
        }

        player.addStat(StatList.useStats[this.id], 1);
        return true;
    }

    public static void placeDoorBlock(World world, int xCoord, int yCoord, int zCoord, int orientation, Block door) {
        placeDoorBlock(world, xCoord, yCoord, zCoord, orientation, door, null);
    }

    public static void placeDoorBlock(World world, int xCoord, int yCoord, int zCoord, int orientation, Block door, Player player) {
        byte xOffset = 0;
        byte zOffset = 0;
        if (orientation == 0) {
            zOffset = 1;
        }

        if (orientation == 1) {
            xOffset = -1;
        }

        if (orientation == 2) {
            zOffset = -1;
        }

        if (orientation == 3) {
            xOffset = 1;
        }

        int side1 = (world.isBlockFull(xCoord - xOffset, yCoord, zCoord - zOffset) ? 1 : 0)
            + (world.isBlockFull(xCoord - xOffset, yCoord + 1, zCoord - zOffset) ? 1 : 0);
        int side2 = (world.isBlockFull(xCoord + xOffset, yCoord, zCoord + zOffset) ? 1 : 0)
            + (world.isBlockFull(xCoord + xOffset, yCoord + 1, zCoord + zOffset) ? 1 : 0);
        boolean canPlace1 = world.getBlockId(xCoord - xOffset, yCoord, zCoord - zOffset) == door.id
            || world.getBlockId(xCoord - xOffset, yCoord + 1, zCoord - zOffset) == door.id;
        boolean canPlace2 = world.getBlockId(xCoord + xOffset, yCoord, zCoord + zOffset) == door.id
            || world.getBlockId(xCoord + xOffset, yCoord + 1, zCoord + zOffset) == door.id;
        boolean flip = false;
        if (canPlace1 && !canPlace2) {
            flip = true;
        } else if (side2 > side1) {
            flip = true;
        }

        if (flip) {
            orientation = orientation - 1 & 3;
            orientation += 4;
        }

        world.setBlockWithNotify(xCoord, yCoord, zCoord, door.id);
        world.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, orientation);
        world.setBlockWithNotify(xCoord, yCoord + 1, zCoord, door.id);
        world.setBlockMetadataWithNotify(xCoord, yCoord + 1, zCoord, orientation + 8);
        StepSound snd = door.getStepSound(0);
        world.playBlockSound(player, xCoord + 0.5F, yCoord + 0.5F, zCoord + 0.5F, snd.getSound(), (snd.getVolume() + 1.0F) / 2.0F, snd.getPitch() * 0.8F);
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.itemIcon = ir.registerIcon("doors/" + this.internalName);
    }
}
