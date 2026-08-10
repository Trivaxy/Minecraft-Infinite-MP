package net.minecraft.game.entity.arrow;

import net.minecraft.game.ParticleType;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.monster.Monster;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.world.World;
import net.minecraft.statistics.achievement.AchievementList;

public class CalmingArrow extends Arrow {
    public CalmingArrow(World world) {
        super(world);
        this.pickupID = ItemList.arrowCalming.id;
    }

    public CalmingArrow(World world, double x, double y, double z) {
        super(world, x, y, z);
        this.pickupID = ItemList.arrowCalming.id;
    }

    public CalmingArrow(World world, Mob mob, float tickDelta) {
        super(world, mob, tickDelta);
        this.pickupID = ItemList.arrowCalming.id;
    }

    @Override
    protected void onEntityHit(Entity entityHit) {
        if (entityHit instanceof Monster) {
            ((Monster)entityHit).calmTime = 100;
        }

        if (this.shooter instanceof Player) {
            ((Player)this.shooter).triggerAchievement(AchievementList.charmMob);
        }

        super.onEntityHit(entityHit);
    }

    @Override
    protected void produceParticles() {
        this.world.spawnParticle(ParticleType.HEART, this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ, 1.0, 1.0, 1.0);
    }
}
