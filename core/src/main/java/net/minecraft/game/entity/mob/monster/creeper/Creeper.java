package net.minecraft.game.entity.mob.monster.creeper;

import com.mojang.nbt.tag.special.CompoundTag;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.misc.LightningBolt;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.Action;
import net.minecraft.game.entity.mob.ai.hostile.Aggro;
import net.minecraft.game.entity.mob.ai.hostile.Attack;
import net.minecraft.game.entity.mob.ai.hostile.DetectEnemy;
import net.minecraft.game.entity.mob.ai.passive.FleeFrom;
import net.minecraft.game.entity.mob.ai.passive.LookAround;
import net.minecraft.game.entity.mob.ai.passive.LookAt;
import net.minecraft.game.entity.mob.ai.passive.Swim;
import net.minecraft.game.entity.mob.ai.passive.WanderAround;
import net.minecraft.game.entity.mob.animal.tameable.Ocelot;
import net.minecraft.game.entity.mob.follower.Decoy;
import net.minecraft.game.entity.mob.monster.Monster;
import net.minecraft.game.entity.mob.monster.skeleton.Skeleton;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public class Creeper extends Monster {
    int fuseTime;
    int lastFuseTime;
    Item[] records = new Item[]{
        ItemList.recordGold,
        ItemList.recordGreen,
        ItemList.recordRed,
        ItemList.recordBlue,
        ItemList.recordPurple,
        ItemList.recordOrange,
        ItemList.recordWhite,
        ItemList.recordBlack,
        ItemList.recordTeal
    };

    public Creeper(World world) {
        super(world);
        this.texture = "/mob/creeper/creeper.png";
        this.scoreValue = 200;
        this.moveSpeed = 0.25F;
        this.behaviors.addTask(1, new Swim(this));
        this.behaviors.addTask(2, new Creeper.SwellUp(this));
        this.behaviors.addTask(3, new FleeFrom(this, Ocelot.class, 6.0F, this.moveSpeed, this.moveSpeed + this.moveSpeed / 2.0F));
        this.behaviors.addTask(4, new Attack(this, this.moveSpeed, false, false));
        this.behaviors.addTask(5, new WanderAround(this, this.moveSpeed, true));
        this.behaviors.addTask(7, new LookAt(this, Player.class, 8.0F));
        this.behaviors.addTask(8, new LookAround(this));
        this.detections.addTask(1, new Aggro(this, false));
        this.detections.addTask(2, new DetectEnemy(this, Decoy.class, 16.0F, 0, true));
        this.detections.addTask(3, new DetectEnemy(this, Player.class, 16.0F, 0, true));
    }

    @Override
    public int getSpawnID() {
        return 7;
    }

    @Override
    protected void init() {
        super.init();
        this.dataSync.addObject(16, (byte)-1);
        this.dataSync.addObject(17, (byte)0);
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        if (this.dataSync.getObjectByte(17) == 1) {
            tag.setBoolean("powered", true);
        }
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        this.dataSync.updateObject(17, (byte)(tag.getBoolean("powered") ? 1 : 0));
    }

    @Override
    public void onTick() {
        if (this.isAlive()) {
            this.lastFuseTime = this.fuseTime;
            int fuseTime = this.getCreeperState();
            if (fuseTime > 0 && this.fuseTime == 0) {
                this.world.playEntitySound(this, "random.fuse", 1.0F, 0.5F);
            }

            this.fuseTime += fuseTime;
            if (this.fuseTime < 0) {
                this.fuseTime = 0;
            }

            if (this.fuseTime >= 30) {
                this.fuseTime = 30;
                if (!this.world.isOnline) {
                    this.explode();
                    this.setDead();
                }
            }
        }

        super.onTick();
    }

    protected void explode() {
        if (this.getPowered()) {
            this.world.createFireExplosion(this, this.posX, this.posY, this.posZ, 6.0F);
        } else {
            this.world.createExplosion(this, this.posX, this.posY, this.posZ, 3.0F);
        }
    }

    @Override
    public void onStruckByLightning(LightningBolt bolt) {
        super.onStruckByLightning(bolt);
        this.setPowered();
    }

    public boolean getPowered() {
        return this.dataSync.getObjectByte(17) == 1;
    }

    public void setPowered() {
        this.dataSync.updateObject(17, (byte)1);
    }

    @Override
    public void onDeath(DamageSource ds) {
        super.onDeath(ds);
        Entity attacker = ds.getEntity();
        if (attacker instanceof Skeleton) {
            this.dropItem(this.records[this.rand.nextInt(this.records.length)].id, 1);
        }
    }

    public float getFlashTimer(float time) {
        return (this.lastFuseTime + (this.fuseTime - this.lastFuseTime) * time) / 28.0F;
    }

    @Override
    public int getDrop() {
        return ItemList.gunpowder.id;
    }

    @Override
    protected final String getHurtSound() {
        return "mob.creeper";
    }

    @Override
    protected final String getDeathSound() {
        return "mob.creeperdeath";
    }

    private int getCreeperState() {
        return this.dataSync.getObjectByte(16);
    }

    private void setCreeperState(int state) {
        this.dataSync.updateObject(16, (byte)state);
    }

    public class SwellUp extends Action {
        private Creeper source;
        private Mob target;
        private int direction;

        public SwellUp(Creeper creeper) {
            this.source = creeper;
            this.setMutexBits(1);
        }

        @Override
        public boolean canExecute() {
            return this.source.getCreeperState() > 0 || this.source.attackTarget != null && this.source.getDistanceSqTo(this.source.attackTarget) < 9.0;
        }

        @Override
        public void start() {
            this.source.navigator.clearPath();
            this.target = this.source.attackTarget;
            if (this.source.world.settings.getDifficulty() > 1) {
                this.source
                    .navigator
                    .setPath(this.source.navigator.pathToLocation(this.target.posX, this.target.posY, this.target.posZ), this.source.moveSpeed);
                this.direction = this.source.rand.nextBoolean() ? 1 : -1;
            }
        }

        @Override
        public void stop() {
            this.target = null;
        }

        @Override
        public void tick() {
            if (this.target != null && !(this.source.getDistanceSqTo(this.target) > 49.0) && this.source.senses.canSee(this.target)) {
                this.source.setCreeperState(1);
                if (this.source.world.settings.getDifficulty() > 1) {
                    double distX = this.target.posX - this.source.posX;
                    double distZ = this.target.posZ - this.source.posZ;
                    float facing = this.source.headFacing;
                    this.source.headFacing = (float)(Math.atan2(distZ, distX) * 180.0 / (float) Math.PI) - 90.0F;
                    float angle = (facing - this.source.headFacing + 90.0F) * (float) Math.PI / 180.0F;
                    this.source.moveStrafing = this.direction * MathHelper.sin(angle) * this.source.moveForward;
                    this.source.lookControl.faceEntity(this.target, 30.0F, 30.0F);
                }
            } else {
                this.source.setCreeperState(-1);
                this.source.moveStrafing = 0.0F;
            }
        }
    }
}
