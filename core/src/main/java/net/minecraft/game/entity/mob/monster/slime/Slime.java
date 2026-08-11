package net.minecraft.game.entity.mob.monster.slime;

import com.mojang.nbt.tag.special.CompoundTag;
import net.minecraft.game.ParticleType;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.Action;
import net.minecraft.game.entity.mob.ai.hostile.Aggro;
import net.minecraft.game.entity.mob.ai.hostile.DetectEnemy;
import net.minecraft.game.entity.mob.ai.util.MoveControl;
import net.minecraft.game.entity.mob.follower.Decoy;
import net.minecraft.game.entity.mob.monster.Monster;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.potion.EffectList;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public class Slime extends Monster {
    protected ParticleType particle = ParticleType.SLIMEBALLPOOF;
    public float offsetY;
    public float offsetX;
    private int jumpDelay = 0;

    public Slime(World world) {
        super(world);
        this.setSlimeSize(1 << this.rand.nextInt(3));
        this.yOffset = 0.0F;
        this.produceFootsteps = false;
        this.jumpDelay = this.rand.nextInt(20) + 10;
        this.type = (byte)this.rand.nextInt(1000);
        this.setType();
        this.moveControl = new Slime.SlimeControl(this);
        this.behaviors.addTask(1, new Slime.FloatInWater(this));
        this.behaviors.addTask(2, new Slime.HopTowards(this));
        this.behaviors.addTask(3, new Slime.ChangeFacing(this));
        this.behaviors.addTask(5, new Slime.HopAround(this));
        this.detections.addTask(1, new Aggro(this, false));
        this.detections.addTask(2, new DetectEnemy(this, Decoy.class, 16.0F, 0, true));
        this.detections.addTask(3, new DetectEnemy(this, Player.class, 16.0F, 0, true));
    }

    public Slime(World world, int scale) {
        this(world);
        this.setSlimeSize(scale);
    }

    @Override
    public void setType() {
        if (this.type == 0) {
            this.texture = "/mob/slime/pinky.png";
            this.setSlimeSize(1);
            this.particle = ParticleType.HEART;
            this.setHealth(1000);
            this.scoreValue = 1000;
        } else {
            this.texture = "/mob/slime/slime.png";
        }
    }

    @Override
    protected void fall(float dist) {
        int size = this.getSlimeSize();

        for (int i = 0; i < size * 8; i++) {
            float angle = this.rand.nextFloat() * (float) Math.PI * 2.0F;
            float offset = this.rand.nextFloat() * 0.5F + 0.5F;
            float offX = MathHelper.sin(angle) * size * 0.5F * offset;
            float offZ = MathHelper.cos(angle) * size * 0.5F * offset;
            this.world.spawnParticle(this.particle, this.posX + offX, this.boundingBox.minY, this.posZ + offZ, 0.0, 0.0, 0.0);
        }

        if (size > 2) {
            this.world.playEntitySound(this, "mob.slime", this.getSoundVolume(), ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) / 0.8F);
        }

        this.offsetY = -0.5F;
    }

    @Override
    public int getSpawnID() {
        return 9;
    }

    public void setSlimeSize(int size) {
        this.setSize(0.6F * size, 0.6F * size);
        this.setHealth(size * size);
        this.dataSync.updateObject(16, (byte)size);
        this.scoreValue = 10 * size;
        if (this.health > 100) {
            this.health = 100;
        }

        this.setPosition(this.posX, this.posY, this.posZ);
    }

    @Override
    protected void init() {
        super.init();
        this.dataSync.addObject(16, (byte)1);
    }

    public int getSlimeSize() {
        return this.dataSync.getObjectByte(16);
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        tag.setInteger("Size", this.getSlimeSize());
        tag.setBoolean("Boss", this.boss);
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        this.setSlimeSize(tag.getInteger("Size"));
        this.boss = tag.getBoolean("Boss");
    }

    @Override
    public final void tick() {
        this.offsetX = this.offsetY;
        super.tick();
        this.offsetY *= 0.6F;
    }

    @Override
    public boolean canSpawnHere() {
        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_double(this.boundingBox.minY);
        int z = MathHelper.floor_double(this.posZ);
        return this.posY <= this.world.getSeaLevel() - this.world.getSeaLevel() / 4
            && !this.world.isBlockExposed(x, y, z)
            && this.world.isClear(this.boundingBox)
            && this.world.getBoundingBoxes(this, this.boundingBox).size() == 0
            && !this.world.containsLiquid(this.boundingBox)
            && this.getPathWeight(x, y, z) >= 0.0F;
    }

    @Override
    public final boolean isInWater() {
        return this.getSlimeSize() == 1 ? this.world.handleWaterMovement(this.boundingBox, MaterialList.water, this) : super.isInWater();
    }

    @Override
    public void jump() {
        this.motionY = 0.42;
    }

    public void faceEntity(Entity target, float yaw, float pitch) {
        double distX = target.posX - this.posX;
        double distZ = target.posZ - this.posZ;
        double yOffset;
        if (target instanceof Mob) {
            Mob entityLiving10 = (Mob)target;
            yOffset = this.posY + this.getEyeHeight() - (entityLiving10.posY + entityLiving10.getEyeHeight());
        } else {
            yOffset = (target.boundingBox.minY + target.boundingBox.maxY) / 2.0 - (this.posY + this.getEyeHeight());
        }

        double dist = MathHelper.sqrt_double(distX * distX + distZ * distZ);
        float newYaw = (float)(Math.atan2(distZ, distX) * 180.0 / (float) Math.PI) - 90.0F;
        float newPitch = (float)(-(Math.atan2(yOffset, dist) * 180.0 / (float) Math.PI));
        this.pitch = -this.updateRotation(this.pitch, newPitch, pitch);
        this.yaw = this.updateRotation(this.yaw, newYaw, yaw);
        this.headFacing = this.updateRotation(this.headFacing, newYaw, yaw);
    }

    private float updateRotation(float facing, float angle, float minMax) {
        float newAngle = MathHelper.computeAngle(angle - facing);
        if (newAngle > minMax) {
            newAngle = minMax;
        }

        if (newAngle < -minMax) {
            newAngle = -minMax;
        }

        return facing + newAngle;
    }

    @Override
    public void setDead() {
        int scale = this.getSlimeSize();
        if (!this.world.isOnline && scale > 1 && this.health <= 0) {
            for (int split = 0; split < 4; split++) {
                float offsetX = (split % 2 - 0.5F) * scale / 4.0F;
                float offsetZ = (split / 2 - 0.5F) * scale / 4.0F;
                Slime slime = new Slime(this.world);
                slime.setSlimeSize(scale / 2);
                slime.teleportTo(this.posX + offsetX, this.posY + 0.5 + this.getEyeHeight(), this.posZ + offsetZ, this.rand.nextFloat() * 360.0F, 0.0F);
                this.world.spawnEntity(slime);
            }
        }

        super.setDead();
    }

    @Override
    public void onCollideWith(Entity player) {
        if (player instanceof Player) {
            int size = this.getSlimeSize();
            if (size > 1 && this.getDistanceSqTo(player) < 1.0 * size && player.damageEntity(DamageSource.mobDamage(this), size)) {
                this.world.playEntitySound(this, "mob.slimeattack", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            }
        }
    }

    @Override
    protected String getHurtSound() {
        return "mob.slime";
    }

    @Override
    protected String getDeathSound() {
        return "mob.slime";
    }

    @Override
    public int getDrop() {
        return this.getSlimeSize() == 1 ? ItemList.slimeBall.id : 0;
    }

    @Override
    public float getSoundVolume() {
        return 0.6F;
    }

    static class ChangeFacing extends Action {
        private Slime slime;
        private float angle;
        private int turnTime;

        public ChangeFacing(Slime slimeIn) {
            this.slime = slimeIn;
            this.setMutexBits(2);
        }

        @Override
        public boolean canExecute() {
            return this.slime.attackTarget == null
                && (this.slime.onGround || this.slime.isInWater() || this.slime.isInLava() || this.slime.hasEffect(EffectList.levitation));
        }

        @Override
        public void tick() {
            if (--this.turnTime <= 0) {
                this.turnTime = 40 + this.slime.rand.nextInt(60);
                this.angle = this.slime.rand.nextInt(360);
            }

            this.slime.moveControl.changeFacing(this.angle, false);
        }
    }

    static class FloatInWater extends Action {
        private Slime slime;

        public FloatInWater(Slime slimeIn) {
            this.slime = slimeIn;
            this.setMutexBits(5);
            this.slime.navigator.setCanSwim(true);
        }

        @Override
        public boolean canExecute() {
            return this.slime.isInWater() || this.slime.isInLava();
        }

        @Override
        public void tick() {
            if (this.slime.rand.nextFloat() < 0.8F) {
                this.slime.jumpControl.setJumping();
            }

            this.slime.moveControl.setSpeed(0.7F);
        }
    }

    static class HopAround extends Action {
        private Slime slime;

        public HopAround(Slime slimeIn) {
            this.slime = slimeIn;
            this.setMutexBits(5);
        }

        @Override
        public boolean canExecute() {
            return true;
        }

        @Override
        public void tick() {
            this.slime.moveControl.setSpeed(0.5F);
        }
    }

    static class HopTowards extends Action {
        private Slime slime;
        private int jumpDelay;

        public HopTowards(Slime slimeIn) {
            this.slime = slimeIn;
            this.setMutexBits(2);
        }

        @Override
        public boolean canExecute() {
            return this.slime.attackTarget != null && this.slime.attackTarget.isAlive();
        }

        @Override
        public void start() {
            this.jumpDelay = 300;
            super.start();
        }

        @Override
        public boolean continueExecuting() {
            return this.slime.attackTarget == null ? false : (!this.slime.attackTarget.isAlive() ? false : --this.jumpDelay > 0);
        }

        @Override
        public void tick() {
            this.slime.faceEntity(this.slime.attackTarget, 10.0F, 10.0F);
            this.slime.moveControl.changeFacing(this.slime.yaw, true);
        }
    }

    static class SlimeControl extends MoveControl {
        private Slime slime;
        private int delay;
        private float facingAngle;
        private boolean aggro;

        public SlimeControl(Slime slime) {
            super(slime);
            this.slime = slime;
            this.facingAngle = 180.0F * slime.yaw / (float) Math.PI;
        }

        @Override
        public void changeFacing(float angle, boolean angy) {
            this.facingAngle = angle;
            this.aggro = angy;
        }

        @Override
        public void tick() {
            this.entity.yaw = this.updateRotation(this.entity.yaw, this.facingAngle, 90.0F);
            this.entity.headFacing = this.entity.yaw;
            this.entity.yawOffset = this.entity.yaw;
            if (!this.isMoving) {
                this.entity.moveForward = this.entity.landSpeed = 0.0F;
            } else {
                this.isMoving = false;
                if (this.entity.onGround) {
                    this.entity.moveForward = this.entity.landSpeed = this.speed;
                    if (this.delay-- <= 0) {
                        this.delay = this.slime.jumpDelay;
                        if (this.aggro) {
                            this.delay /= 3;
                        }

                        this.slime.jumpControl.setJumping();
                        if (this.slime.getSlimeSize() > 1) {
                            this.slime
                                .world
                                .playEntitySound(
                                    this.slime,
                                    "mob.slime",
                                    this.slime.getSoundVolume(),
                                    ((this.slime.rand.nextFloat() - this.slime.rand.nextFloat()) * 0.2F + 1.0F) * 0.8F
                                );
                        }
                    } else {
                        this.slime.moveStrafing = this.slime.moveForward = 0.0F;
                        this.entity.moveForward = this.entity.landSpeed = 0.0F;
                    }
                } else {
                    this.entity.moveForward = this.entity.landSpeed = this.speed;
                }
            }
        }
    }
}
