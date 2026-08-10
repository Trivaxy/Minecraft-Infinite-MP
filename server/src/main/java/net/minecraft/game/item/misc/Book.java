package net.minecraft.game.item.misc;

import com.mojang.nbt.tag.special.CompoundTag;
import com.mojang.nbt.tag.special.ListTag;
import com.mojang.nbt.tag.special.StringTag;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;

public class Book extends Item {
    public Book(int id) {
        super(id);
        this.maxStackSize = 1;
        this.priorityItem = true;
    }

    @Override
    public ItemStack use(ItemStack stack, World world, Player player) {
        player.displayGUIBook(stack);
        player.addStat(StatList.useStats[this.id], 1);
        return stack;
    }

    public static boolean validBookTagPages(CompoundTag tag) {
        if (tag == null) {
            return false;
        }

        if (!tag.hasKey("pages")) {
            return false;
        }

        ListTag<StringTag> pages = (ListTag<StringTag>)tag.getTag("pages");

        for (int index = 0; index < pages.size(); index++) {
            StringTag page = pages.get(index);
            if (page.string == null) {
                return false;
            }

            if (page.string.length() > 256) {
                return false;
            }
        }

        return true;
    }
}
