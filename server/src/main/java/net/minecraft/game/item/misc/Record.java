package net.minecraft.game.item.misc;

import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.utility.Jukebox;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.Rarity;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;
import net.minecraft.statistics.achievement.AchievementList;

public class Record extends Item {
    public final String recordName;

    public Record(int id, String song, String artist) {
        super(id);
        this.recordName = song;
        this.maxStackSize = 1;
        this.info.add(artist);
        this.info.add(song);
        this.rarity = Rarity.RARE;
        this.priorityItem = true;
    }

    @Override
    public boolean useOnBlock(ItemStack stack, Player player, World world, int x, int y, int z, int side) {
        if (world.getBlockId(x, y, z) != BlockList.jukeBox.id || world.getBlockMetadata(x, y, z) != 0) {
            return false;
        }

        if (world.isOnline) {
            return true;
        }

        ((Jukebox)BlockList.jukeBox).playMusic(world, x, y, z, this.id);
        world.playRecord(this.recordName, x, y, z, false);
        player.triggerAchievement(AchievementList.music);
        player.addStat(StatList.useStats[stack.itemID], 1);
        player.addStat(StatList.jukeboxUse, 1);
        if (player.properties.gamemode != 1) {
            stack.stackSize--;
        }

        return true;
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.itemIcon = ir.registerIcon("records/" + this.internalName);
    }
}
