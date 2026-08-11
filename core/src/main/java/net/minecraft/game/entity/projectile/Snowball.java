package net.minecraft.game.entity.projectile;

import net.minecraft.game.ParticleType;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.mob.CreatureType;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.physics.TargetingSystem;
import net.minecraft.game.world.World;
import net.minecraft.statistics.achievement.AchievementList;
import net.minecraft.util.math.MathHelper;

public class Snowball extends Projectile {
    public Snowball(World world) {
        super(world);
        this.itemAssigned = ItemList.snowball;
    }

    public Snowball(World world, Mob thrower, int dmg) {
        super(world, thrower, dmg);
        this.itemAssigned = ItemList.snowball;
    }

    public Snowball(World world, double x, double y, double z) {
        super(world, x, y, z);
        this.itemAssigned = ItemList.snowball;
    }

    @Override
    protected void onImpact(TargetingSystem mop) {
        if (mop.entityHit != null) {
            if (mop.entityHit.getCreatureType() == CreatureType.FLAME) {
                mop.entityHit.damageEntity(DamageSource.projectileDamage(this, this.thrower), 3);
                if (mop.entityHit instanceof Mob && ((Mob)mop.entityHit).health <= 0 && this.thrower instanceof Player) {
                    ((Player)this.thrower).triggerAchievement(AchievementList.impKill);
                }
            } else {
                mop.entityHit.damageEntity(DamageSource.projectileDamage(this, this.thrower), this.damage);
            }
        } else if (this.world.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)) == 0
            && this.world.isSolid(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY - 1.0), MathHelper.floor_double(this.posZ))) {
            this.world
                .setBlockWithNotify(
                    MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ), BlockList.snowLayer.id
                );
        }

        for (int i = 0; i < 8; i++) {
            this.world.spawnParticle(ParticleType.SNOWBALLPOOF, this.posX, this.posY, this.posZ, 0.0, 0.0, 0.0);
        }

        this.setDead();
    }
}
