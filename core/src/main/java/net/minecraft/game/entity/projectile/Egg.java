package net.minecraft.game.entity.projectile;

import net.minecraft.game.ParticleType;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.animal.flying.Chicken;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.physics.TargetingSystem;
import net.minecraft.game.world.World;
import net.minecraft.statistics.achievement.AchievementList;

public class Egg extends Projectile {
    public Egg(World world) {
        super(world);
        this.itemAssigned = ItemList.egg;
    }

    public Egg(World world, Mob thrower, int dmg) {
        super(world, thrower, dmg);
        this.itemAssigned = ItemList.egg;
    }

    public Egg(World world, double x, double y, double z) {
        super(world, x, y, z);
        this.itemAssigned = ItemList.egg;
    }

    @Override
    protected void onImpact(TargetingSystem mop) {
        if (mop.entityHit != null) {
            mop.entityHit.damageEntity(DamageSource.projectileDamage(this, this.thrower), this.damage);
        }

        if (!this.world.isOnline && this.rand.nextInt(8) == 0) {
            byte chickens = 1;
            if (this.rand.nextInt(32) == 0) {
                chickens = 4;
            }

            if (this.thrower instanceof Player) {
                ((Player)this.thrower).triggerAchievement(AchievementList.chicken);
            }

            for (int i = 0; i < chickens; i++) {
                Chicken chicken = new Chicken(this.world);
                chicken.setPosAndRot(this.posX, this.posY, this.posZ, this.yaw, 0.0F);
                this.world.spawnEntity(chicken);
            }
        }

        for (int i = 0; i < 8; i++) {
            this.world.spawnParticle(ParticleType.EGGPOOF, this.posX, this.posY, this.posZ, 0.0, 0.0, 0.0);
        }

        this.setDead();
    }
}
