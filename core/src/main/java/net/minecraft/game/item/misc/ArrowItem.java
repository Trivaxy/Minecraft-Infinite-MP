package net.minecraft.game.item.misc;

import net.minecraft.game.item.Item;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;

public class ArrowItem extends Item {
    public static Icon arrowSlot;
    public static Icon trashSlot;
    public static Icon handSlot;
    public boolean bolt = false;

    public ArrowItem(int itemid, boolean bolt) {
        super(itemid);
        this.bolt = bolt;
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.itemIcon = ir.registerIcon("arrows/" + this.internalName);
        arrowSlot = ir.registerIcon("slots/slot_empty_arrow");
        trashSlot = ir.registerIcon("slots/slot_trash");
        handSlot = ir.registerIcon("slots/slot_hand");
    }
}
