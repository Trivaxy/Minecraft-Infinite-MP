package net.minecraft.game.entity.projectile;

import net.minecraft.game.ParticleType;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.monster.boss.Inferno;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.physics.TargetingSystem;
import net.minecraft.game.world.World;
import net.minecraft.statistics.achievement.AchievementList;

public class Bullet extends Projectile {
    public Bullet(World world) {
        super(world);
        this.itemAssigned = ItemList.musketRound;
        this.gravityForce = 0.0;
    }

    public Bullet(World world, Mob thrower, int dmg) {
        super(world, thrower, dmg);
        this.itemAssigned = ItemList.musketRound;
        this.gravityForce = 0.0;
        this.setHeading(this.motionX, this.motionY, this.motionZ, 3.5F, 0.5F);
    }

    @Override
    protected void onImpact(TargetingSystem mop) {
        if (mop.entityHit != null) {
            mop.entityHit.damageEntity(DamageSource.projectileDamage(this, this.thrower), this.damage);
            if (mop.entityHit instanceof Inferno && this.thrower instanceof Player) {
                ((Player)this.thrower).triggerAchievement(AchievementList.musket);
            }
        }

        for (int i = 0; i < 8; i++) {
            this.world.spawnParticle(ParticleType.BULLETPOOF, this.posX, this.posY, this.posZ, 0.0, 0.0, 0.0);
        }

        this.setDead();
    }
}
