package net.minecraft.game.item.misc;

import net.minecraft.game.item.Item;
import net.minecraft.game.texture.IconRegister;

public class OreChunk extends Item {
    public OreChunk(int id) {
        super(id);
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.itemIcon = ir.registerIcon("ores/" + this.internalName);
    }
}
