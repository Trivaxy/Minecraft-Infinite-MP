package net.minecraft.game.item.misc;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;

public class Scrap extends Item {
    public static List<Icon> icons = new ArrayList<>();
    public static final String[] signs = new String[]{"scrap", "scrap_red", "scrap_green", "scrap_blue", "scrap_purple", "scrap_orange"};

    public Scrap(int itemid) {
        super(itemid);
        this.setHasSubtypes(true);
    }

    @Override
    public int getMaxDamage() {
        return 6;
    }

    @Override
    public void getSubItems(int id, CreativeTabs ct, List<ItemStack> ls) {
        for (int i = 0; i < this.getMaxDamage(); i++) {
            ls.add(new ItemStack(id, 1, i));
        }
    }

    @Override
    public String getNameFromMeta(int damage) {
        return "item." + signs[damage];
    }

    @Override
    public Icon getIconFromMeta(int meta) {
        return icons.get(meta);
    }

    @Override
    public void registerIcons(IconRegister ir) {
        for (int i = 0; i < this.getMaxDamage(); i++) {
            icons.add(ir.registerIcon("scraps/" + signs[i]));
        }
    }
}
