package net.minecraft.game.item.misc;

import net.minecraft.game.item.Item;
import net.minecraft.game.texture.IconRegister;

public class Ingredient extends Item {
    public Ingredient(int id) {
        super(id);
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.itemIcon = ir.registerIcon("ingredients/" + this.internalName);
    }
}
