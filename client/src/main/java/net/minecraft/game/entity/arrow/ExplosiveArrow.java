package net.minecraft.game.entity.arrow;

import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.world.World;

public class ExplosiveArrow extends Arrow {
    public ExplosiveArrow(World world) {
        super(world);
        this.pickupID = ItemList.arrowExplosive.id;
        this.arrowDamage = 8;
    }

    public ExplosiveArrow(World world, double x, double y, double z) {
        super(world, x, y, z);
        this.pickupID = ItemList.arrowExplosive.id;
        this.arrowDamage = 8;
    }

    public ExplosiveArrow(World world, Mob mob, float tickDelta) {
        super(world, mob, tickDelta);
        this.pickupID = ItemList.arrowExplosive.id;
        this.arrowDamage = 8;
    }

    @Override
    protected void onEntityHit(Entity entityHit) {
        this.world.createSmallExplosion(this, this.posX, this.posY, this.posZ, 2.0F);
        super.onEntityHit(entityHit);
    }

    @Override
    protected void onBlockHit() {
        this.world.createExplosion(this, this.xTile, this.yTile, this.zTile, 2.0F);
        this.setDead();
    }
}
