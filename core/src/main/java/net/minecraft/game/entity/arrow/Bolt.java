package net.minecraft.game.entity.arrow;

import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.world.World;

public class Bolt extends Arrow {
    public Bolt(World world) {
        super(world);
        this.pickupID = ItemList.arrow.id;
        this.arrowDamage = 8;
    }

    public Bolt(World world, double x, double y, double z) {
        super(world, x, y, z);
        this.pickupID = ItemList.arrow.id;
        this.arrowDamage = 8;
    }

    public Bolt(World world, Mob mob, float tickDelta) {
        super(world, mob, tickDelta);
        this.pickupID = ItemList.arrow.id;
        this.arrowDamage = 8;
    }
}
