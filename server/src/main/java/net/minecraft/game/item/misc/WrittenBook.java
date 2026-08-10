package net.minecraft.game.item.misc;

import com.mojang.nbt.tag.special.CompoundTag;
import com.mojang.nbt.tag.special.StringTag;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.localization.Translator;
import net.minecraft.statistics.StatList;

public class WrittenBook extends Item {
    public WrittenBook(int id) {
        super(id);
        this.maxStackSize = 1;
        this.priorityItem = true;
    }

    public static boolean validBookContents(CompoundTag tag) {
        if (!Book.validBookTagPages(tag)) {
            return false;
        }

        if (!tag.hasKey("title")) {
            return false;
        }

        String title = tag.getString("title");
        return title != null && title.length() <= 16 ? tag.hasKey("author") : false;
    }

    public String getItemName(ItemStack stack) {
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            StringTag title = (StringTag)tag.getTag("title");
            if (title != null) {
                return title.toString();
            }
        }

        return Translator.getInstance().translateNamedKey(this.fullName);
    }

    @Override
    public String getInfoLine(ItemStack stack, int i) {
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            StringTag author = (StringTag)tag.getTag("author");
            if (author != null) {
                return author.toString();
            }
        }

        return Translator.getInstance().translateKey("misc.unknown");
    }

    @Override
    public int getInfoLines(ItemStack stack) {
        return stack.hasTag() ? 1 : 0;
    }

    @Override
    public ItemStack use(ItemStack stack, World world, Player player) {
        player.displayGUIBook(stack);
        player.addStat(StatList.useStats[this.id], 1);
        return stack;
    }
}
