package net.minecraft.game.entity.arrow;

import net.minecraft.game.ParticleType;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.misc.PrimedTNT;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.world.World;
import net.minecraft.statistics.achievement.AchievementList;

public class FlameArrow extends Arrow {
    public FlameArrow(World world) {
        super(world);
        this.pickupID = ItemList.arrowFlame.id;
    }

    public FlameArrow(World world, double x, double y, double z) {
        super(world, x, y, z);
        this.pickupID = ItemList.arrowFlame.id;
    }

    public FlameArrow(World world, Mob mob, float tickDelta) {
        super(world, mob, tickDelta);
        this.pickupID = ItemList.arrowFlame.id;
    }

    @Override
    protected void onEntityHit(Entity entityHit) {
        entityHit.fire = 100;
        super.onEntityHit(entityHit);
    }

    @Override
    protected void onBlockHit() {
        if (this.world.getBlockId(this.xTile, this.yTile, this.zTile) == BlockList.tnt.id) {
            this.world.setBlockWithNotify(this.xTile, this.yTile, this.zTile, 0);
            PrimedTNT TNT = new PrimedTNT(this.world, this.xTile + 0.5, this.yTile + 0.5, this.zTile + 0.5);
            TNT.fuse = this.world.rand.nextInt(TNT.fuse / 4) + TNT.fuse / 8;
            this.world.spawnEntity(TNT);
            if (this.shooter instanceof Player) {
                ((Player)this.shooter).triggerAchievement(AchievementList.tnt);
            }

            this.setDead();
        }
    }

    @Override
    protected void produceParticles() {
        this.world
            .spawnParticle(
                ParticleType.FIRE,
                this.posX + this.motionX,
                this.posY + this.motionY,
                this.posZ + this.motionZ,
                -this.motionX,
                -this.motionY + 0.2,
                -this.motionZ
            );
        this.world
            .spawnParticle(
                ParticleType.SMOKE,
                this.posX + this.motionX,
                this.posY + this.motionY,
                this.posZ + this.motionZ,
                -this.motionX,
                -this.motionY + 0.2,
                -this.motionZ
            );
    }
}
