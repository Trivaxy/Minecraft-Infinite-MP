package net.minecraft.game.entity.mob.monster;

import net.minecraft.game.block.Block;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.ai.Action;
import net.minecraft.game.entity.mob.ai.flying.FlyAround;
import net.minecraft.game.entity.mob.ai.flying.LookAroundFlying;
import net.minecraft.game.entity.mob.ai.hostile.Aggro;
import net.minecraft.game.entity.mob.ai.hostile.DetectEnemy;
import net.minecraft.game.entity.mob.ai.util.FlightControl;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.entity.projectile.Fireball;
import net.minecraft.game.entity.projectile.Projectile;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.potion.EffectList;
import net.minecraft.game.physics.Vector;
import net.minecraft.game.world.World;
import net.minecraft.statistics.achievement.AchievementList;

public class Ghast extends Monster {
    public int prevAttackCounter = 0;
    public int attackCounter = 0;

    public Ghast(World world) {
        super(world);
        this.setHealth(10);
        this.setSize(4.0F, 4.0F);
        this.texture = "/mob/ghast/ghast.png";
        this.scoreValue = 250;
        this.produceFootsteps = false;
        this.moveControl = new FlightControl(this);
        this.behaviors.addTask(5, new FlyAround(this, 6.0F, 0.1F));
        this.behaviors.addTask(6, new LookAroundFlying(this, false));
        this.behaviors.addTask(7, new Ghast.FireballAttack(this));
        this.detections.addTask(1, new Aggro(this, false));
        this.detections.addTask(2, new DetectEnemy(this, Player.class, 50.0F, 0, true));
    }

    @Override
    public boolean isInsideBlock() {
        return false;
    }

    @Override
    public boolean canBurn() {
        return false;
    }

    @Override
    public boolean canClimb(Block block) {
        return false;
    }

    @Override
    public int getSpawnID() {
        return 54;
    }

    @Override
    protected void init() {
        super.init();
        this.dataSync.addObject(16, (byte)0);
    }

    @Override
    public void tick() {
        super.tick();
        byte attackState = this.dataSync.getObjectByte(16);
        this.texture = attackState == 1 ? "/mob/ghast/ghast_fire.png" : "/mob/ghast/ghast.png";
        this.prevAttackCounter = this.attackCounter;
        if (!this.world.isOnline) {
            byte state = this.dataSync.getObjectByte(16);
            byte prevState = (byte)(this.attackCounter > 10 ? 1 : 0);
            if (state != prevState) {
                this.dataSync.updateObject(16, prevState);
            }
        }
    }

    @Override
    public String getSound() {
        return "mob.ghast.moan";
    }

    @Override
    public String getHurtSound() {
        return "mob.ghast.scream";
    }

    @Override
    public String getDeathSound() {
        return "mob.ghast.death";
    }

    @Override
    public int getDrop() {
        return ItemList.gunpowder.id;
    }

    @Override
    public int specialDrop() {
        return ItemList.tear.id;
    }

    @Override
    public void dropSpecialItem(DamageSource ds, boolean luck) {
        Entity entity = ds.getEntity();
        int lootModifier = 2;
        if (entity instanceof Player) {
            if (luck) {
                lootModifier = 1;
            } else if (((Player)entity).hasEffect(EffectList.badLuck)) {
                lootModifier = 5;
            }
        }

        if (!this.world.isOnline && this.rand.nextInt(lootModifier) == 0) {
            this.dropItem(this.specialDrop(), 1);
        }
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
            float friction = 0.91F;
            if (this.onGround) {
                friction = 0.54600006F;
            }

            float offset = 0.16277136F / (friction * friction * friction);
            this.moveFlying(x, z, this.onGround ? 0.1F * offset : 0.02F);
            friction = 0.91F;
            if (this.onGround) {
                friction = 0.54600006F;
            }

            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= friction;
            this.motionY *= friction;
            this.motionZ *= friction;
        }
    }

    @Override
    public boolean damageEntity(DamageSource src, int amount) {
        if (src.damageType == "fireball" && src.getEntity() instanceof Player) {
            super.damageEntity(DamageSource.explosion, 1000);
            ((Player)src.getEntity()).triggerAchievement(AchievementList.ghast);
            return true;
        } else {
            return super.damageEntity(src, amount);
        }
    }

    @Override
    public float getSoundVolume() {
        return 10.0F;
    }

    @Override
    public boolean canSpawnHere() {
        return super.canSpawnHere() && this.world.settings.getDifficulty() > 0;
    }

    @Override
    public int getMaxSpawns() {
        return 1;
    }

    private class FireballAttack extends Action {
        private Ghast ghast;

        private FireballAttack(Ghast ghast) {
            this.ghast = ghast;
        }

        @Override
        public boolean canExecute() {
            return this.ghast.attackTarget != null;
        }

        @Override
        public void tick() {
            double maxDIst = 64.0;
            if (this.ghast.attackTarget != null && this.ghast.attackTarget.getDistanceTo(this.ghast) < maxDIst * maxDIst) {
                double distX = this.ghast.attackTarget.posX - this.ghast.posX;
                double distY = this.ghast.attackTarget.boundingBox.minY + this.ghast.attackTarget.height - (this.ghast.posY + this.ghast.height);
                double distZ = this.ghast.attackTarget.posZ - this.ghast.posZ;
                if (this.ghast.isVisible(this.ghast.attackTarget)) {
                    if (this.ghast.attackCounter == 10) {
                        this.ghast.world.playEntitySound(this.ghast, "mob.ghast.charge", 10.0F, 1.0F / (this.ghast.rand.nextFloat() * 0.4F + 0.8F));
                    }

                    this.ghast.attackCounter++;
                    if (this.ghast.attackCounter == 20) {
                        this.ghast.world.playEntitySound(this.ghast, "mob.ghast.shoot", 10.0F, 1.0F / (this.ghast.rand.nextFloat() * 0.4F + 0.8F));
                        Projectile fireball = new Fireball(this.ghast.world, this.ghast, distX, distY, distZ, 10);
                        double verticalOffset = 4.0;
                        Vector lookVec = this.ghast.getLookVector(1.0F);
                        fireball.posX = this.ghast.posX + lookVec.xCoord * verticalOffset;
                        fireball.posY = this.ghast.posY + this.ghast.height / 2.0F + 0.5;
                        fireball.posZ = this.ghast.posZ + lookVec.zCoord * verticalOffset;
                        this.ghast.world.spawnEntity(fireball);
                        this.ghast.attackCounter = -40;
                    }
                } else if (this.ghast.attackCounter > 0) {
                    this.ghast.attackCounter--;
                }
            } else if (this.ghast.attackCounter > 0) {
                this.ghast.attackCounter--;
            }
        }
    }
}
