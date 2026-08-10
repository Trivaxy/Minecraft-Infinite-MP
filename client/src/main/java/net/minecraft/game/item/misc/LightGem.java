package net.minecraft.game.item.misc;

import net.minecraft.game.item.Item;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;

public class LightGem extends Item {
    private Icon[] icons = new Icon[16];

    public LightGem(int itemid) {
        super(itemid);
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.itemIcon = ir.registerIcon(this.internalName);

        for (int i = 0; i < this.icons.length; i++) {
            this.icons[i] = ir.registerIcon("lightGem/" + this.internalName + "_" + i);
        }
    }

    @Override
    public Icon getIconFromMeta(int meta) {
        return this.icons[meta];
    }
}
