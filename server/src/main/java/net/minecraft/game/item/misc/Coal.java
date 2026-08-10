package net.minecraft.game.item.misc;

import java.util.List;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;

public class Coal extends Item {
    Icon altIcon;

    public Coal(int id) {
        super(id);
        this.setHasSubtypes(true);
        this.maxDamage = 0;
    }

    @Override
    public Icon getIconFromMeta(int id) {
        return id == 1 ? this.altIcon : this.itemIcon;
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.itemIcon = ir.registerIcon(this.internalName);
        this.altIcon = ir.registerIcon("charcoal");
    }

    @Override
    public int getMaxDamage() {
        return 1;
    }

    @Override
    public void getSubItems(int id, CreativeTabs ct, List<ItemStack> ls) {
        ls.add(new ItemStack(id, 1, 0));
        ls.add(new ItemStack(id, 1, 1));
    }

    @Override
    public String getNameFromMeta(int damage) {
        return damage == 1 ? "item.charcoal" : this.fullName;
    }
}
