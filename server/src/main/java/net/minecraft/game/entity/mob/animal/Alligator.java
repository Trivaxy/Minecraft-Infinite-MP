package net.minecraft.game.entity.mob.animal;

import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.flying.FlyToTarget;
import net.minecraft.game.entity.mob.ai.flying.LookAroundFlying;
import net.minecraft.game.entity.mob.ai.flying.SwimAround;
import net.minecraft.game.entity.mob.ai.hostile.Aggro;
import net.minecraft.game.entity.mob.ai.hostile.Attack;
import net.minecraft.game.entity.mob.ai.hostile.DetectEnemy;
import net.minecraft.game.entity.mob.ai.passive.LookAt;
import net.minecraft.game.entity.mob.ai.passive.WanderAround;
import net.minecraft.game.entity.mob.ai.util.FlightControl;
import net.minecraft.game.entity.mob.ai.util.MoveControl;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.world.World;

public class Alligator extends Animal {
    private MoveControl landMovement = new MoveControl(this);
    private FlightControl seaMovement = new FlightControl(this);

    public Alligator(World world) {
        super(world);
        this.setHealth(20);
        this.setSize(2.0F, 0.5F);
        this.attackStrength = 8;
        this.produceFootsteps = false;
        this.moveSpeed = 0.2F;
        this.scoreValue = 75;
        this.type = (byte)this.rand.nextInt(100);
        if (this.type == 1) {
            this.setHealth(30);
        }

        this.setType();
        this.behaviors.addTask(3, new Alligator.SwimToTarget(this, 0.1F));
        this.behaviors.addTask(4, new Attack(this, this.moveSpeed, false, false));
        this.behaviors.addTask(5, new SwimAround(this, 6.0F, 0.1F));
        this.behaviors.addTask(6, new LookAroundFlying(this, false));
        this.behaviors.addTask(7, new WanderAround(this, this.moveSpeed, true));
        this.behaviors.addTask(8, new LookAt(this, Player.class, 6.0F));
        this.detections.addTask(1, new Aggro(this, false));
        this.detections.addTask(2, new DetectEnemy(this, Player.class, 32.0F, 0, true));
        this.detections.addTask(3, new DetectEnemy(this, Turtle.class, 32.0F, 0, true));
    }

    @Override
    public void setType() {
        if (this.type == 1) {
            this.texture = "/mob/alligator/alligator_albino.png";
            this.moveSpeed = 0.23F;
            this.scoreValue = 130;
        } else {
            this.texture = "/mob/alligator/alligator.png";
        }
    }

    @Override
    public void mobTick() {
        super.mobTick();
        if (!this.isInWater() && this.onGround) {
            this.moveControl = this.landMovement;
        } else {
            this.moveControl = this.seaMovement;
        }
    }

    @Override
    public int getSpawnID() {
        return 32;
    }

    @Override
    public String getSound() {
        return "mob.alligator";
    }

    @Override
    protected String getHurtSound() {
        return "mob.alligatorhurt";
    }

    @Override
    protected String getDeathSound() {
        return "mob.alligatordeath";
    }

    @Override
    public int specialDrop() {
        return ItemList.teeth.id;
    }

    @Override
    public float getSoundVolume() {
        return 0.4F;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public boolean isInWater() {
        return this.world.handleWaterMovement(this.boundingBox, MaterialList.water, this);
    }

    private void moveSwimming(float x, float z) {
        float spdFactor = 0.91F;
        if (this.onGround) {
            spdFactor = 0.54600006F;
        }

        float spd = 0.16277136F / (spdFactor * spdFactor * spdFactor);
        this.moveFlying(x, z, this.onGround ? 0.1F * spd : 0.02F);
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= spdFactor;
        this.motionY *= spdFactor;
        this.motionZ *= spdFactor;
    }

    @Override
    public void moveEntityWithHeading(float x, float y) {
        if (this.isDead || this.health <= 0 || !this.isInWater()) {
            super.moveEntityWithHeading(x, y);
        } else if (this.isInWater()) {
            this.moveSwimming(x, y);
        } else if (this.isInLava()) {
            this.moveFlying(x, y, 0.02F);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.5;
            this.motionY *= 0.5;
            this.motionZ *= 0.5;
        } else {
            this.moveSwimming(x, y);
            this.motionY = (float)(this.motionY - 0.8);
        }
    }

    @Override
    public void knockBack(Entity entity, double x, double force) {
        if (!this.inWater) {
            super.knockBack(entity, x, force);
        }
    }

    private class SwimToTarget extends FlyToTarget {
        private SwimToTarget(Mob parent, float dist) {
            super(parent, dist);
        }

        @Override
        public boolean canExecute() {
            return this.mob.isInWater() && super.canExecute();
        }

        @Override
        public void start() {
            this.mob.navigator.clearPath();
        }
    }
}
