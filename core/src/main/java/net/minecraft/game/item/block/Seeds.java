package net.minecraft.game.item.block;

import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.stepsound.StepSound;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;
import net.minecraft.statistics.achievement.AchievementList;

public class Seeds extends Item {
    private int blockType;

    public Seeds(int itemid, int blockType) {
        super(itemid);
        this.blockType = blockType;
        this.priorityItem = true;
    }

    @Override
    public boolean useOnBlock(ItemStack stack, Player player, World world, int xCoord, int yCoord, int zCoord, int side) {
        if (side != 1) {
            return false;
        }

        int id = world.getBlockId(xCoord, yCoord, zCoord);
        if (id == BlockList.tilledField.id && world.getBlockId(xCoord, yCoord + 1, zCoord) == 0) {
            world.setBlockWithNotify(xCoord, yCoord + 1, zCoord, this.blockType);
            StepSound snd = BlockList.susGravel.getStepSound(0);
            world.playBlockSound(xCoord + 0.5F, yCoord + 0.5F, zCoord + 0.5F, snd.getSound(), (snd.getVolume() + 1.0F) / 2.0F, snd.getPitch() * 0.8F);
            if (player.properties.gamemode != 1) {
                stack.stackSize--;
            }

            player.addStat(StatList.useStats[this.id], 1);
            player.triggerAchievement(AchievementList.plantSeed);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.itemIcon = ir.registerIcon("seeds/" + this.internalName);
    }
}
