package net.minecraft.game.entity.projectile;

import net.minecraft.game.ParticleType;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.physics.TargetingSystem;
import net.minecraft.game.world.World;

public class ScytheHead extends Projectile {
    public ScytheHead(World world) {
        super(world);
        this.bbSize = 3.0;
        this.gravityForce = 0.0;
    }

    public ScytheHead(World world, Mob thrower, int dmg) {
        super(world, thrower, dmg);
        this.bbSize = 3.0;
        this.gravityForce = 0.0;
    }

    @Override
    protected void spawnParticles() {
        for (int i = 0; i < 4; i++) {
            float offset = 0.25F;
            this.world
                .spawnParticle(
                    ParticleType.SMOKE,
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
        if (mop.entityHit != null) {
            mop.entityHit.damageEntity(DamageSource.projectileDamage(this, this.thrower), this.damage);
        }

        for (int i = 0; i < 8; i++) {
            this.world.spawnParticle(ParticleType.EXPLODE, this.posX, this.posY, this.posZ, 0.0, 0.0, 0.0);
        }

        if (mop.entityHit == null) {
            this.setDead();
        }
    }
}
