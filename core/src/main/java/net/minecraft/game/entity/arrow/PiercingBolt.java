package net.minecraft.game.entity.arrow;

import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.world.World;
import net.minecraft.statistics.achievement.AchievementList;

public class PiercingBolt extends Arrow {
    private int hits;

    public PiercingBolt(World world) {
        super(world);
        this.pickupID = ItemList.arrowPiercing.id;
        this.arrowDamage = 8;
    }

    public PiercingBolt(World world, double x, double y, double z) {
        super(world, x, y, z);
        this.pickupID = ItemList.arrowPiercing.id;
        this.arrowDamage = 8;
    }

    public PiercingBolt(World world, Mob mob, float tickDelta) {
        super(world, mob, tickDelta);
        this.pickupID = ItemList.arrowPiercing.id;
        this.arrowDamage = 8;
    }

    @Override
    protected void onEntityHit(Entity entityHit) {
        this.hits++;
        if (this.shooter instanceof Player) {
            ((Player)this.shooter).triggerAchievement(AchievementList.bow);
            if (this.hits >= 5) {
                ((Player)this.shooter).triggerAchievement(AchievementList.pierce);
            }
        }
    }

    @Override
    protected void onBlockHit() {
        this.hits = 0;
    }
}
