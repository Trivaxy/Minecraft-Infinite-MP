package net.minecraft.game.entity.projectile;

import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.physics.TargetingSystem;
import net.minecraft.game.world.World;

public class Spit extends Projectile {
    public Spit(World world) {
        super(world);
        this.itemAssigned = ItemList.windCharge;
        this.gravityForce = 0.04;
    }

    public Spit(World world, Mob thrower, int dmg) {
        super(world, thrower, dmg);
        this.itemAssigned = ItemList.windCharge;
        this.gravityForce = 0.04;
    }

    @Override
    protected void onImpact(TargetingSystem mop) {
        if (mop.entityHit != null) {
            if (mop.entityHit == this.thrower || mop.entityHit == this.thrower.rider) {
                return;
            }

            mop.entityHit.damageEntity(DamageSource.projectileDamage(this, this.thrower), this.damage);
        }

        this.setDead();
    }
}
