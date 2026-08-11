package net.minecraft.game.entity.mob.animal.flying;

import com.mojang.nbt.tag.special.CompoundTag;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.Action;
import net.minecraft.game.entity.mob.ai.flying.FlyAround;
import net.minecraft.game.entity.mob.ai.flying.LookAroundFlying;
import net.minecraft.game.entity.mob.ai.util.FlightControl;
import net.minecraft.game.entity.mob.animal.Animal;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public class Bat extends Animal {
    public Bat(World world) {
        super(world);
        this.setSize(0.5F, 0.9F);
        this.setSleeping(true);
        this.setHealth(6);
        this.texture = "/mob/bat.png";
        this.headFacing = this.rand.nextInt(360);
        this.produceFootsteps = false;
        this.setType();
        this.moveControl = new FlightControl(this);
        this.behaviors.addTask(4, new Bat.SpookedByPlayer(this, 16.0F));
        this.behaviors.addTask(5, new Bat.BatFly(this, 6.0F, 0.15F));
        this.behaviors.addTask(6, new LookAroundFlying(this, false));
    }

    @Override
    public int getSpawnID() {
        return 37;
    }

    @Override
    public void onTick() {
        super.onTick();
        if (this.isAsleep()) {
            this.motionX = this.motionY = this.motionZ = 0.0;

            for (int i = MathHelper.floor_double(this.posY); i < this.world.getSeaLevel(); i++) {
                if (this.world.isBlockFull(MathHelper.floor_double(this.posX), i, MathHelper.floor_double(this.posZ))) {
                    this.posY = (double)i - this.height;
                    break;
                }
            }

            this.setPosition(this.posX, this.posY, this.posZ);
        }
    }

    @Override
    public float getPathWeight(int xCoord, int yCoord, int zCoord) {
        return 0.5F - this.world.getBrightness(xCoord, yCoord, zCoord);
    }

    @Override
    public boolean canSpawnHere() {
        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_double(this.boundingBox.minY);
        int z = MathHelper.floor_double(this.posZ);
        return y < this.world.getSeaLevel() - 16
            && this.world.getBlockBrightness(x, y, z) <= this.rand.nextInt(8)
            && this.world.isClear(this.boundingBox)
            && this.world.getBoundingBoxes(this, this.boundingBox).size() == 0
            && !this.world.containsLiquid(this.boundingBox);
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

            float div = 0.16277136F / (force * force * force);
            this.moveFlying(x, z, this.onGround ? 0.1F * div : 0.02F);
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
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        tag.setBoolean("Sleeping", this.sitting);
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        this.sitting = tag.getBoolean("Sleeping");
    }

    @Override
    public float getSoundVolume() {
        return 0.1F;
    }

    @Override
    public String getSound() {
        return this.isAsleep() && this.rand.nextInt(4) != 0 ? null : "mob.bat";
    }

    @Override
    protected String getHurtSound() {
        return "mob.bathurt";
    }

    @Override
    protected String getDeathSound() {
        return "mob.batdeath";
    }

    public boolean isAsleep() {
        return this.sitting;
    }

    public void setSleeping(boolean state) {
        this.sitting = state;
    }

    private class BatFly extends FlyAround {
        private Bat bat;

        private BatFly(Mob parent, float dist, float spd) {
            super(parent, dist, spd);
            this.bat = (Bat)parent;
        }

        @Override
        public boolean canExecute() {
            return !this.bat.isAsleep() && super.canExecute();
        }
    }

    private class SpookedByPlayer extends Action {
        private Bat bat;
        private float range;

        private SpookedByPlayer(Bat bat, float range) {
            this.bat = bat;
            this.range = range;
        }

        @Override
        public boolean canExecute() {
            return this.bat.isAsleep();
        }

        @Override
        public void tick() {
            Player closestPlayer = this.bat.world.getClosestPlayer(this.bat, this.range);
            if (closestPlayer != null) {
                this.bat.setSleeping(false);
            }
        }
    }
}
