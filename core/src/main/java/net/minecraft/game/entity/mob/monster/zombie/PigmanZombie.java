package net.minecraft.game.entity.mob.monster.zombie;

import com.mojang.nbt.tag.special.CompoundTag;
import java.util.List;
import net.minecraft.game.ParticleType;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.CreatureType;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.hostile.Aggro;
import net.minecraft.game.entity.mob.ai.hostile.Attack;
import net.minecraft.game.entity.mob.ai.hostile.ChangeCourse;
import net.minecraft.game.entity.mob.ai.hostile.DetectEnemy;
import net.minecraft.game.entity.mob.ai.passive.LookAround;
import net.minecraft.game.entity.mob.ai.passive.LookAt;
import net.minecraft.game.entity.mob.ai.passive.Swim;
import net.minecraft.game.entity.mob.ai.passive.WanderAround;
import net.minecraft.game.entity.mob.follower.Decoy;
import net.minecraft.game.entity.mob.monster.Monster;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.statistics.achievement.AchievementList;

public class PigmanZombie extends Monster {
    public int angryTime = 0;
    private int randomSoundDelay;

    public PigmanZombie(World world) {
        super(world);
        this.heldItem = ItemList.swordGold;
        this.moveSpeed = 0.23F;
        this.attackStrength = 5;
        this.scoreValue = 125;
        this.type = (byte)this.rand.nextInt(2);
        this.setType();
        this.behaviors.addTask(0, new Swim(this));
        this.behaviors.addTask(2, new Attack(this, this.moveSpeed + this.moveSpeed / 2.0F, false, false));
        this.behaviors.addTask(4, new ChangeCourse(this, this.moveSpeed));
        this.behaviors.addTask(6, new WanderAround(this, this.moveSpeed, true));
        this.behaviors.addTask(7, new LookAt(this, Player.class, 8.0F));
        this.behaviors.addTask(7, new LookAround(this));
        this.detections.addTask(1, new Aggro(this, true));
        this.detections.addTask(2, new PigmanZombie.DetectPlayer(this, Decoy.class, 16.0F, 0, true));
        this.detections.addTask(3, new PigmanZombie.DetectPlayer(this, Player.class, 16.0F, 0, true));
    }

    @Override
    public void setType() {
        if (this.type == 1) {
            this.texture = "/mob/pigman/pigzombie_bloody.png";
        } else {
            this.texture = "/mob/pigman/pigzombie.png";
        }
    }

    @Override
    public void mobTick() {
        super.mobTick();
        if (this.angryTime > 0) {
            this.angryTime--;
        }

        if (this.randomSoundDelay > 0 && --this.randomSoundDelay == 0) {
            this.world
                .playEntitySound(
                    this, "mob.zombiepig.zpigangry", this.getSoundVolume() * 2.0F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) * 1.8F
                );
        }
    }

    @Override
    public boolean canBurn() {
        return false;
    }

    @Override
    public int getSpawnID() {
        return 55;
    }

    @Override
    public CreatureType getCreatureType() {
        return CreatureType.UNDEAD;
    }

    @Override
    public boolean canSpawnHere() {
        return this.world.settings.getDifficulty() > 0
            && this.world.isClear(this.boundingBox)
            && this.world.getBoundingBoxes(this, this.boundingBox).size() == 0
            && !this.world.containsLiquid(this.boundingBox);
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        tag.setShort("Anger", (short)this.angryTime);
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        this.angryTime = tag.getShort("Anger");
    }

    @Override
    public boolean damageEntity(DamageSource src, int amount) {
        Entity target = src.getEntity();
        if (target instanceof Player) {
            List<Entity> entities = this.world.getEntities(this, this.boundingBox.expand(32.0, 32.0, 32.0));

            for (int i = 0; i < entities.size(); i++) {
                Entity pigman = entities.get(i);
                if (pigman instanceof PigmanZombie) {
                    ((PigmanZombie)pigman).angryTime = 400 + this.rand.nextInt(400);
                    ((PigmanZombie)pigman).randomSoundDelay = this.rand.nextInt(40);
                }
            }

            this.angryTime = 400 + this.rand.nextInt(400);
            this.randomSoundDelay = this.rand.nextInt(40);
        }

        return super.damageEntity(src, amount);
    }

    @Override
    public boolean interact(Player player) {
        ItemStack heldItem = player.inventory.getCurrentItem();
        if (player.getUsing()) {
            heldItem = player.inventory.getOffHandItem();
        }

        if (heldItem != null && heldItem.itemID == ItemList.beetroot.id) {
            if (player.properties.gamemode != 1) {
                heldItem.stackSize--;
            }

            if (heldItem.stackSize <= 0) {
                if (player.getUsing()) {
                    player.inventory.setSlotContents(44, null);
                } else {
                    player.inventory.setSlotContents(player.inventory.currentSlot, null);
                }
            }

            for (int i = 0; i < 7; i++) {
                this.world
                    .spawnParticle(
                        ParticleType.SMOKE,
                        this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width,
                        this.posY + 0.5 + this.rand.nextFloat() * this.height,
                        this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width,
                        this.rand.nextGaussian() * 0.02,
                        this.rand.nextGaussian() * 0.02,
                        this.rand.nextGaussian() * 0.02
                    );
            }

            this.attackTarget = this.lastTarget = null;
            this.angryTime = 0;
            player.triggerAchievement(AchievementList.beetroot);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getSound() {
        return this.attackTarget != null ? "mob.zombiepig.zpigangry" : "mob.zombiepig.zpig";
    }

    @Override
    protected String getHurtSound() {
        return "mob.zombiepig.zpighurt";
    }

    @Override
    protected String getDeathSound() {
        return "mob.zombiepig.zpigdeath";
    }

    @Override
    public void playStepSound() {
        this.world.playEntitySound(this, "mob.zombiestep", 0.15F, 1.0F);
    }

    @Override
    public int getDrop() {
        return this.type == 1 ? ItemList.porkCooked.id : ItemList.rottenFlesh.id;
    }

    private class DetectPlayer extends DetectEnemy {
        private PigmanZombie pig;

        public DetectPlayer(Mob target, Class<?> targetClass, float dist, int chance, boolean chase) {
            super(target, targetClass, dist, chance, chase);
            this.pig = (PigmanZombie)target;
        }

        @Override
        public boolean canExecute() {
            return this.pig.angryTime > 0 && super.canExecute();
        }
    }
}
