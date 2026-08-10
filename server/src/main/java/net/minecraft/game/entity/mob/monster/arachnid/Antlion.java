package net.minecraft.game.entity.mob.monster.arachnid;

import com.mojang.nbt.tag.special.CompoundTag;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.CreatureType;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.Action;
import net.minecraft.game.entity.mob.ai.hostile.Aggro;
import net.minecraft.game.entity.mob.ai.hostile.Attack;
import net.minecraft.game.entity.mob.ai.hostile.DetectEnemy;
import net.minecraft.game.entity.mob.ai.hostile.LeapAt;
import net.minecraft.game.entity.mob.ai.hostile.RangedAttack;
import net.minecraft.game.entity.mob.ai.passive.FleeFrom;
import net.minecraft.game.entity.mob.ai.passive.LookAround;
import net.minecraft.game.entity.mob.ai.passive.LookAt;
import net.minecraft.game.entity.mob.ai.passive.Swim;
import net.minecraft.game.entity.mob.ai.passive.WanderAround;
import net.minecraft.game.entity.mob.animal.tameable.Fox;
import net.minecraft.game.entity.mob.follower.Decoy;
import net.minecraft.game.entity.mob.monster.Monster;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public class Antlion extends Monster {
    private int buryTime;
    private int stayBuried;

    public Antlion(World world) {
        super(world);
        this.setSize(0.9F, 0.7F);
        this.setHealth(12);
        this.texture = "/mob/spider/antlion.png";
        this.moveSpeed = 0.3F;
        this.scoreValue = 160;
        this.behaviors.addTask(0, new Swim(this));
        this.behaviors.addTask(1, new FleeFrom(this, Fox.class, 8.0F, this.moveSpeed, this.moveSpeed * 2.0F));
        this.behaviors.addTask(2, new Antlion.ShootProjectile(this, 0.0F, -6, 30));
        this.behaviors.addTask(3, new LeapAt(this, 0.4F));
        this.behaviors.addTask(4, new Attack(this, this.moveSpeed, true, false));
        this.behaviors.addTask(5, new Antlion.BuryInSand(this));
        this.behaviors.addTask(6, new WanderAround(this, this.moveSpeed, true));
        this.behaviors.addTask(7, new LookAt(this, Player.class, 8.0F));
        this.behaviors.addTask(8, new LookAround(this));
        this.detections.addTask(1, new Aggro(this, false));
        this.detections.addTask(2, new DetectEnemy(this, Decoy.class, 16.0F, 0, true));
        this.detections.addTask(3, new DetectEnemy(this, Player.class, 16.0F, 0, true));
    }

    @Override
    public int getSpawnID() {
        return 16;
    }

    @Override
    public CreatureType getCreatureType() {
        return CreatureType.ARTHROPOD;
    }

    @Override
    public boolean isInWater() {
        return this.world.handleWaterMovement(this.boundingBox, MaterialList.water, this);
    }

    @Override
    public boolean damageEntity(DamageSource src, int amount) {
        if (this.sitting) {
            this.teleport();
        }

        return super.damageEntity(src, amount);
    }

    @Override
    public void knockBack(Entity entity, double forceX, double forceZ) {
        if (!this.sitting) {
            super.knockBack(entity, forceX, forceZ);
        }
    }

    @Override
    public void onTick() {
        super.onTick();
        if (!this.onGround) {
            this.sitting = false;
            this.buryTime = this.stayBuried = 0;
        }
    }

    private void teleport() {
        if (this.sitting) {
            int newX = MathHelper.floor_double(this.posX) + (this.rand.nextInt(20) - 10);
            int newZ = MathHelper.floor_double(this.posZ) + (this.rand.nextInt(20) - 10);
            int highestTile = this.world.getTopTileSafe(newX, newZ) - 1;
            Block blockUnder = BlockList.blocks[this.world.getBlockId(newX, highestTile, newZ)];
            if (blockUnder != null && blockUnder.material == MaterialList.sand) {
                int metadata = this.world.getBlockMetadata(newX, highestTile, newZ);
                this.world
                    .playEffect(
                        2001,
                        MathHelper.floor_double(this.posX),
                        MathHelper.floor_double(this.posY) - 1,
                        MathHelper.floor_double(this.posZ),
                        metadata << 12 | blockUnder.id
                    );
                this.setPosition(newX + 0.5F, highestTile + 1, newZ + 0.5F);
            } else {
                this.sitting = false;
            }
        }
    }

    private void emergeFromSand() {
        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_double(this.posY - 1.0);
        int z = MathHelper.floor_double(this.posZ);
        Block blockUnder = BlockList.blocks[this.world.getBlockId(x, y, z)];
        if (blockUnder != null) {
            int metadata = this.world.getBlockMetadata(x, y, z);
            this.world.playEffect(2001, x, y, z, metadata << 12 | blockUnder.id);
        }

        this.sitting = false;
        this.buryTime = this.stayBuried = 0;
    }

    @Override
    public boolean isSelectable() {
        return !this.isDead;
    }

    @Override
    public boolean isPushable() {
        return !this.sitting;
    }

    @Override
    public boolean canSpawnHere() {
        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_double(this.boundingBox.minY);
        int z = MathHelper.floor_double(this.posZ);
        return (this.world.getBlockId(x, y - 1, z) == BlockList.sand.id || this.world.getBlockId(x, y - 1, z) == BlockList.sandStone.id)
            && this.world.isClear(this.boundingBox)
            && this.world.getBoundingBoxes(this, this.boundingBox).size() == 0
            && !this.world.containsLiquid(this.boundingBox);
    }

    @Override
    public float getPathWeight(int xCoord, int yCoord, int zCoord) {
        return this.world.getBlockId(xCoord, yCoord - 1, zCoord) != BlockList.sand.id
                && this.world.getBlockId(xCoord, yCoord - 1, zCoord) != BlockList.sandStone.id
            ? 0.0F
            : 10.0F;
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        tag.setInteger("BuryTime", this.buryTime);
        tag.setInteger("StayBuried", this.stayBuried);
        tag.setBoolean("Buried", this.sitting);
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        this.buryTime = tag.getInteger("BuryTime");
        this.stayBuried = tag.getInteger("StayBuried");
        this.sitting = tag.getBoolean("Buried");
    }

    @Override
    public String getSound() {
        return "mob.spider";
    }

    @Override
    protected String getHurtSound() {
        return "mob.spider";
    }

    @Override
    protected String getDeathSound() {
        return "mob.spiderdeath";
    }

    @Override
    public void playStepSound() {
        this.world.playEntitySound(this, "mob.spiderstep", 0.15F, 1.0F);
    }

    @Override
    public float getSoundVolume() {
        return 0.8F;
    }

    @Override
    public int getDrop() {
        return ItemList.sandball.id;
    }

    @Override
    public int specialDrop() {
        return ItemList.pincer.id;
    }

    private class BuryInSand extends Action {
        private Antlion mob;
        private double yaw;
        private double pitch;

        private BuryInSand(Antlion mob) {
            this.mob = mob;
            this.setMutexBits(3);
        }

        @Override
        public boolean canExecute() {
            Block blockUnder = BlockList.blocks[this.mob.world.getBlockId((int)this.mob.posX, (int)this.mob.posY - 1, (int)this.mob.posZ)];
            return this.mob.buryTime < this.mob.stayBuried
                ? true
                : !this.mob.sitting
                    && this.mob.navigator.noPath()
                    && this.mob.onGround
                    && this.mob.rand.nextInt(250) == 0
                    && blockUnder != null
                    && blockUnder.material == MaterialList.sand;
        }

        @Override
        public boolean continueExecuting() {
            return this.mob.sitting && this.mob.buryTime <= this.mob.stayBuried;
        }

        @Override
        public void start() {
            this.mob.sitting = true;
            if (this.mob.stayBuried <= 0) {
                this.mob.stayBuried = this.mob.rand.nextInt(2500) + 300;
            }
        }

        @Override
        public void tick() {
            if (this.mob.sitting) {
                ++this.mob.buryTime;
                this.mob.jumping = false;
                if (this.mob.buryTime >= this.mob.stayBuried) {
                    this.mob.emergeFromSand();
                } else if (this.mob.buryTime % 40 == 0) {
                    double angle = (Math.PI * 2) * this.mob.rand.nextDouble();
                    this.yaw = Math.cos(angle);
                    this.pitch = Math.sin(angle);
                }

                this.mob
                    .lookControl
                    .setLookPosition(
                        this.mob.posX + this.yaw, this.mob.posY + this.mob.getEyeHeight(), this.mob.posZ + this.pitch, 10.0F, this.mob.getFaceSpeed()
                    );
            }
        }
    }

    private class ShootProjectile extends RangedAttack {
        private Antlion antlion;

        public ShootProjectile(Mob src, float friction, int arrow, int timer) {
            super(src, friction, arrow, timer);
            this.antlion = (Antlion)src;
        }

        @Override
        public boolean canExecute() {
            return this.antlion.sitting && super.canExecute();
        }

        @Override
        public void tick() {
            double maxDist = 200.0;
            double dist = this.antlion.getDistanceSq(this.target.posX, this.target.boundingBox.minY, this.target.posZ);
            boolean canSeeTarget = this.antlion.senses.canSee(this.target);
            this.antlion.lookControl.faceEntity(this.target, 30.0F, 30.0F);
            this.shootTimer = Math.max(this.shootTimer - 1, 0);
            if (this.shootTimer <= 0) {
                if (dist <= maxDist && canSeeTarget) {
                    this.shootProjectile();
                    this.shootTimer = this.maxWaitTime;
                } else {
                    this.antlion.emergeFromSand();
                }
            }
        }
    }
}
