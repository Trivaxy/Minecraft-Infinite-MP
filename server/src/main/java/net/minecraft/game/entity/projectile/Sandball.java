package net.minecraft.game.entity.projectile;

import net.minecraft.game.ParticleType;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.physics.TargetingSystem;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public class Sandball extends Projectile {
    public Sandball(World world) {
        super(world);
        this.itemAssigned = ItemList.sandball;
    }

    public Sandball(World world, Mob thrower, int dmg) {
        super(world, thrower, dmg);
        this.itemAssigned = ItemList.sandball;
    }

    public Sandball(World world, double x, double y, double z) {
        super(world, x, y, z);
        this.itemAssigned = ItemList.sandball;
    }

    @Override
    protected void onImpact(TargetingSystem mop) {
        if (mop.entityHit != null) {
            mop.entityHit.damageEntity(DamageSource.projectileDamage(this, this.thrower), this.damage);
        } else if (this.world.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)) == 0
            && this.world.isSolid(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY - 1.0), MathHelper.floor_double(this.posZ))) {
            this.world
                .setBlockWithNotify(
                    MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ), BlockList.sandLayer.id
                );
        }

        for (int i = 0; i < 8; i++) {
            this.world.spawnParticle(ParticleType.SANDBALLPOOF, this.posX, this.posY, this.posZ, 0.0, 0.0, 0.0);
        }

        this.setDead();
    }
}
