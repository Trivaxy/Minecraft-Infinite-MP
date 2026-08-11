package net.minecraft.game.entity.arrow;

import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.monster.skeleton.Skeleton;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.world.World;
import net.minecraft.statistics.achievement.AchievementList;

public class BoneArrow extends Arrow {
    public BoneArrow(World world) {
        super(world);
        this.pickupID = ItemList.bone.id;
        this.arrowDamage = 8;
    }

    public BoneArrow(World world, double x, double y, double z) {
        super(world, x, y, z);
        this.pickupID = ItemList.bone.id;
        this.arrowDamage = 8;
    }

    public BoneArrow(World world, Mob mob, float tickDelta) {
        super(world, mob, tickDelta);
        this.pickupID = ItemList.bone.id;
        this.arrowDamage = 8;
    }

    @Override
    protected void onEntityHit(Entity entityHit) {
        if (this.shooter instanceof Player && entityHit instanceof Skeleton) {
            ((Player)this.shooter).triggerAchievement(AchievementList.crossbone);
        }

        this.setDead();
    }

    @Override
    protected void onBlockHit() {
        this.setDead();
    }
}
