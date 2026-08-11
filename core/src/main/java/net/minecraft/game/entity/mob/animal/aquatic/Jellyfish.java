package net.minecraft.game.entity.mob.animal.aquatic;

import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.ai.Action;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public class Jellyfish extends AquaticMob {
    public float bodyPitch = 0.0F;
    public float prevBodyPitch = 0.0F;
    public float bodyYaw = 0.0F;
    public float prevBodyYaw = 0.0F;
    public float swim = 0.0F;
    public float prevSwim = 0.0F;
    public float pulse = 0.0F;
    public float prevPulse = 0.0F;
    private float swimVelocity = 0.0F;
    private float pulseStrength = 0.0F;
    private float swimSpd = 0.0F;
    private float swimDirX = 0.0F;
    private float swimDirY = 0.0F;
    private float swimDirZ = 0.0F;

    public Jellyfish(World world) {
        super(world);
        this.setSize(0.8F, 0.8F);
        this.swimVelocity = 1.0F / (this.rand.nextFloat() + 1.0F) * 0.2F;
        this.canGlow = true;
        this.type = (byte)this.rand.nextInt(4);
        this.setType();
        this.behaviors.tasks.clear();
        this.behaviors.addTask(0, new Jellyfish.MoveAround(this));
    }

    @Override
    public void setType() {
        switch (this.type) {
            case 1:
                this.texture = "/mob/jellyfish/jellyfish_pink.png";
                break;
            case 2:
                this.texture = "/mob/jellyfish/jellyfish_green.png";
                break;
            case 3:
                this.texture = "/mob/jellyfish/jellyfish_orange.png";
                break;
            default:
                this.texture = "/mob/jellyfish/jellyfish.png";
        }
    }

    @Override
    public int getBrightnessInt(float brightness) {
        return 15728880;
    }

    @Override
    public int getSpawnID() {
        return 14;
    }

    @Override
    public int getDrop() {
        return ItemList.glodust.id;
    }

    @Override
    public int specialDrop() {
        return ItemList.tentacle.id;
    }

    @Override
    public void mobTick() {
        super.mobTick();
        this.prevBodyPitch = this.bodyPitch;
        this.prevBodyYaw = this.bodyYaw;
        this.prevSwim = this.swim;
        this.prevPulse = this.pulse;
        this.swim = this.swim + this.swimVelocity;
        if (this.swim > (float) (Math.PI * 2)) {
            this.swim -= (float) (Math.PI * 2);
            if (this.rand.nextInt(10) == 0) {
                this.swimVelocity = 1.0F / (this.rand.nextFloat() + 1.0F) * 0.2F;
            }
        }

        if (this.isInWater()) {
            if (this.swim < (float) Math.PI) {
                float force = this.swim / (float) Math.PI;
                this.pulse = MathHelper.sin(force * force * (float) Math.PI) * (float) Math.PI * 0.25F;
                if (force > 0.75) {
                    this.swimSpd = 1.0F;
                    this.pulseStrength = 1.0F;
                } else {
                    this.pulseStrength *= 0.8F;
                }
            } else {
                this.pulse = 0.0F;
                this.swimSpd *= 0.9F;
                this.pulseStrength *= 0.99F;
            }

            this.motionX = this.swimDirX * this.swimSpd;
            this.motionY = this.swimDirY * this.swimSpd;
            this.motionZ = this.swimDirZ * this.swimSpd;
            float force = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.yawOffset = this.yawOffset + (-((float)Math.atan2(this.motionX, this.motionZ)) * 180.0F / (float) Math.PI - this.yawOffset) * 0.1F;
            this.yaw = this.yawOffset;
            this.bodyYaw = this.bodyYaw + (float) Math.PI * this.pulseStrength * 1.5F;
            this.bodyPitch = this.bodyPitch + (-((float)Math.atan2(force, this.motionY)) * 180.0F / (float) Math.PI - this.bodyPitch) * 0.1F;
        } else {
            this.pulse = MathHelper.abs(MathHelper.sin(this.swim)) * (float) Math.PI * 0.25F;
            this.motionX = 0.0;
            this.motionY -= 0.08F;
            this.motionY *= 0.98F;
            this.motionZ = 0.0;
        }
    }

    @Override
    public void moveEntityWithHeading(float x, float z) {
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
    }

    public void move(float x, float y, float z) {
        this.swimDirX = x;
        this.swimDirY = y;
        this.swimDirZ = z;
    }

    public boolean isMoving() {
        return this.swimDirX != 0.0F || this.swimDirY != 0.0F || this.swimDirZ != 0.0F;
    }

    @Override
    public void onCollideWith(Entity entity) {
        if (entity instanceof Player) {
            entity.damageEntity(DamageSource.generic, 2);
        }
    }

    private class MoveAround extends Action {
        private Jellyfish jellyfish;

        private MoveAround(Jellyfish mob) {
            this.jellyfish = mob;
        }

        @Override
        public boolean canExecute() {
            return true;
        }

        @Override
        public void tick() {
            if (this.jellyfish.rand.nextInt(50) == 0 || !this.jellyfish.inWater || !this.jellyfish.isMoving()) {
                float angle = this.jellyfish.rand.nextFloat() * (float) (Math.PI * 2);
                float motionX = MathHelper.cos(angle) * 0.2F;
                float motionY = -0.1F + this.jellyfish.rand.nextFloat() * 0.2F;
                float motionZ = MathHelper.sin(angle) * 0.2F;
                this.jellyfish.move(motionX, motionY, motionZ);
            }
        }
    }
}
