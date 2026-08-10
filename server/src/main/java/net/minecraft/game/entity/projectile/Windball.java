package net.minecraft.game.entity.projectile;

import net.minecraft.game.ParticleType;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.potion.EffectData;
import net.minecraft.game.item.potion.EffectList;
import net.minecraft.game.physics.TargetingSystem;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public class Windball extends Projectile {
    public Windball(World world) {
        super(world);
        this.itemAssigned = ItemList.windCharge;
        this.gravityForce = 0.0;
    }

    public Windball(World world, Mob thrower, int dmg) {
        super(world, thrower, dmg);
        this.itemAssigned = ItemList.windCharge;
        this.gravityForce = 0.0;
        this.setHeading(this.motionX, this.motionY, this.motionZ, 1.0F, 1.0F);
    }

    public Windball(World world, double x, double y, double z) {
        super(world, x, y, z);
        this.itemAssigned = ItemList.windCharge;
        this.gravityForce = 0.0;
        this.setHeading(this.motionX, this.motionY, this.motionZ, 1.0F, 1.0F);
    }

    public Windball(World world, Mob thrower, double x, double y, double z, int dmg) {
        super(world, thrower, x, y, z, dmg);
        this.itemAssigned = ItemList.windCharge;
        this.gravityForce = 0.0;
        this.setHeading(this.motionX, this.motionY, this.motionZ, 1.0F, 1.0F);
    }

    @Override
    public boolean isSelectable() {
        return true;
    }

    @Override
    protected void spawnParticles() {
        for (int i = 0; i < 4; i++) {
            float offset = 0.25F;
            this.world
                .spawnParticle(
                    ParticleType.EXPLODE,
                    this.posX - this.motionX * offset,
                    this.posY - this.motionY * offset,
                    this.posZ - this.motionZ * offset,
                    this.motionX,
                    this.motionY,
                    this.motionZ
                );
        }
    }

    @Override
    protected void onImpact(TargetingSystem mop) {
        if (mop.entityHit != null && mop.entityHit instanceof Mob) {
            if (this.damage == 4) {
                ((Mob)mop.entityHit).addEffect(new EffectData(EffectList.levitation.id, 300, 0));
            } else {
                this.world.createWindExplosion(this, this.posX, this.posY, this.posZ, 3.0F);
            }

            mop.entityHit.damageEntity(DamageSource.projectileDamage(this, this.thrower), this.damage);
        } else {
            if (this.damage == 4) {
                float offset = 0.4F;
                this.motionX = -(-MathHelper.sin(this.yaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.pitch / 180.0F * (float) Math.PI) * offset);
                this.motionZ = -(MathHelper.cos(this.yaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.pitch / 180.0F * (float) Math.PI) * offset);
                this.motionY *= -offset;
                return;
            }

            this.world.createWindExplosion(this, this.posX, this.posY, this.posZ, 3.0F);
        }

        for (int i = 0; i < 8; i++) {
            this.world.spawnParticle(ParticleType.EXPLODE, this.posX, this.posY, this.posZ, 0.0, 0.0, 0.0);
        }

        this.setDead();
    }
}
