package net.minecraft.game.entity.mob.monster;

import net.minecraft.game.block.Block;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.flying.FlyAround;
import net.minecraft.game.entity.mob.ai.flying.FlyToTarget;
import net.minecraft.game.entity.mob.ai.flying.LookAroundFlying;
import net.minecraft.game.entity.mob.ai.hostile.Aggro;
import net.minecraft.game.entity.mob.ai.hostile.DetectEnemy;
import net.minecraft.game.entity.mob.ai.util.FlightControl;
import net.minecraft.game.entity.mob.follower.Decoy;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.world.World;

public class Harpy extends Monster {
    public Harpy(World world) {
        super(world);
        this.setHealth(12);
        this.texture = "/mob/harpy/harpy.png";
        this.moveSpeed = 0.1F;
        this.scoreValue = 125;
        this.produceFootsteps = false;
        this.moveControl = new FlightControl(this);
        this.behaviors.addTask(4, new Harpy.ChargeAtTarget(this, this.moveSpeed * 3.0F));
        this.behaviors.addTask(5, new FlyAround(this, 6.0F, this.moveSpeed));
        this.behaviors.addTask(6, new LookAroundFlying(this, false));
        this.detections.addTask(1, new Aggro(this, false));
        this.detections.addTask(2, new Harpy.DetectPlayer(this, Decoy.class, 16.0F, 0, true));
        this.detections.addTask(3, new Harpy.DetectPlayer(this, Player.class, 16.0F, 0, true));
    }

    @Override
    public int getSpawnID() {
        return 64;
    }

    @Override
    public String getSound() {
        return "mob.harpy";
    }

    @Override
    public String getHurtSound() {
        return "mob.harpyhurt";
    }

    @Override
    public String getDeathSound() {
        return "mob.harpyhurt";
    }

    @Override
    public float getSoundVolume() {
        return 0.5F;
    }

    @Override
    public int getDrop() {
        return ItemList.feather.id;
    }

    @Override
    public boolean canClimb(Block block) {
        return false;
    }

    @Override
    protected void fall(float dist) {
    }

    @Override
    public void moveEntityWithHeading(float x, float z) {
        if (this.isDead || this.health <= 0) {
            super.moveEntityWithHeading(x, z);
        } else if (this.isInWater()) {
            this.moveFlying(x, z, 0.02F);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.8F;
            this.motionY *= 0.8F;
            this.motionZ *= 0.8F;
        } else if (this.isInLava()) {
            this.moveFlying(x, z, 0.02F);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.5;
            this.motionY *= 0.5;
            this.motionZ *= 0.5;
        } else {
            float force = 0.91F;
            if (this.onGround) {
                force = 0.54600006F;
            }

            float offset = 0.16277136F / (force * force * force);
            this.moveFlying(x, z, this.onGround ? 0.1F * offset : 0.02F);
            force = 0.91F;
            if (this.onGround) {
                force = 0.54600006F;
            }

            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= force;
            this.motionY *= force;
            this.motionZ *= force;
        }
    }

    @Override
    public boolean canSpawnHere() {
        return this.rand.nextInt(3) == 0
            && this.world.isClear(this.boundingBox)
            && this.world.getBoundingBoxes(this, this.boundingBox).size() == 0
            && !this.world.containsLiquid(this.boundingBox)
            && this.world.settings.getDifficulty() > 0;
    }

    @Override
    public int getMaxSpawns() {
        return 1;
    }

    private class ChargeAtTarget extends FlyToTarget {
        private ChargeAtTarget(Mob harpy, float dist) {
            super(harpy, dist);
        }

        @Override
        protected void attack() {
            super.attack();
            this.mob.attackTarget = this.mob.lastTarget = null;
        }
    }

    private class DetectPlayer extends DetectEnemy {
        public DetectPlayer(Mob target, Class<?> targetClass, float dist, int chance, boolean chase) {
            super(target, targetClass, dist, chance, chase);
        }

        @Override
        protected boolean foundValidTarget(Mob target, boolean attackPlayer) {
            return target != null && this.src.getDistanceSqTo(target) < 128.0 ? false : super.foundValidTarget(target, attackPlayer);
        }
    }
}
