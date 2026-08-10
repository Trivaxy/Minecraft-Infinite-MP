package net.minecraft.game.entity.mob.monster;

import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.ai.Action;
import net.minecraft.game.entity.mob.ai.hostile.Aggro;
import net.minecraft.game.entity.mob.ai.hostile.Attack;
import net.minecraft.game.entity.mob.ai.hostile.ChangeCourse;
import net.minecraft.game.entity.mob.ai.hostile.DetectEnemy;
import net.minecraft.game.entity.mob.ai.passive.LookAround;
import net.minecraft.game.entity.mob.ai.passive.LookAt;
import net.minecraft.game.entity.mob.ai.passive.Swim;
import net.minecraft.game.entity.mob.ai.passive.WanderAround;
import net.minecraft.game.entity.mob.follower.Decoy;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.util.math.MathHelper;

public class Ogre extends Monster {
    public Ogre(World world) {
        super(world);
        this.setSize(1.7F, 2.5F);
        this.setHealth(40);
        this.setType();
        this.attackStrength = 8;
        this.knockback = 1.2F;
        this.scoreValue = 220;
        this.moveSpeed = 0.23F;
        this.behaviors.addTask(0, new Swim(this));
        this.behaviors.addTask(1, new Ogre.SmashTerrain(this, 30));
        this.behaviors.addTask(2, new Attack(this, this.moveSpeed + this.moveSpeed / 2.0F, false, false));
        this.behaviors.addTask(4, new ChangeCourse(this, this.moveSpeed));
        this.behaviors.addTask(6, new WanderAround(this, this.moveSpeed, true));
        this.behaviors.addTask(7, new LookAt(this, Player.class, 8.0F));
        this.behaviors.addTask(7, new LookAround(this));
        this.detections.addTask(1, new Aggro(this, false));
        this.detections.addTask(2, new DetectEnemy(this, Decoy.class, 16.0F, 0, true));
        this.detections.addTask(3, new DetectEnemy(this, Player.class, 16.0F, 0, true));
    }

    @Override
    public void setType() {
        switch (this.type) {
            case 0:
                this.texture = "/mob/ogre/ogre_ice.png";
                break;
            case 2:
                this.texture = "/mob/ogre/ogre_lava.png";
                break;
            default:
                this.texture = "/mob/ogre/ogre.png";
        }
    }

    @Override
    public void setTypeOnSpawn() {
        int x = MathHelper.floor_double(this.posX);
        int z = MathHelper.floor_double(this.posZ);
        Biome biome = this.world.getBiomeAt(x, z);
        this.type = biome.getTemperature();
        this.setType();
    }

    @Override
    public boolean canSpawnHere() {
        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_double(this.boundingBox.minY);
        int z = MathHelper.floor_double(this.posZ);
        return this.posY <= this.world.getSeaLevel() - this.world.getSeaLevel() / 4 && !this.world.isBlockExposed(x, y, z) && super.canSpawnHere();
    }

    @Override
    public int getSpawnID() {
        return 43;
    }

    @Override
    public int specialDrop() {
        return ItemList.eye.id;
    }

    @Override
    public final String getSound() {
        return "mob.ogre";
    }

    @Override
    protected final String getHurtSound() {
        return "mob.ogrehurt";
    }

    @Override
    protected final String getDeathSound() {
        return "mob.ogredeath";
    }

    @Override
    public void playStepSound() {
        this.world.playEntitySound(this, "mob.golemstep", 0.6F, 1.0F);
    }

    @Override
    public float getSoundVolume() {
        return 0.4F;
    }

    @Override
    public void knockBack(Entity entity, double force, double dist) {
    }

    public class SmashTerrain extends Action {
        private Ogre ogre;
        private int collidedTime;
        int maxCollideTime;

        public SmashTerrain(Ogre ogre, int max) {
            this.ogre = ogre;
            this.maxCollideTime = max;
        }

        @Override
        public void tick() {
            if (this.collidedTime > this.maxCollideTime) {
                this.collidedTime = 0;
            }

            if (this.ogre.collidedHorizontally) {
                this.collidedTime++;
                if (this.ogre.attackTarget != null && this.ogre.onGround && this.collidedTime == this.maxCollideTime) {
                    this.ogre.attackFrames = 10;
                    this.ogre.world.setEntityState(this.ogre, (byte)4);
                    int x = MathHelper.floor_double(this.ogre.posX);
                    int y = MathHelper.floor_double(this.ogre.posY + 1.5);
                    int z = MathHelper.floor_double(this.ogre.posZ);
                    this.ogre.world.createExplosion(this.ogre, x, y + 1, z, 3.0F);
                }
            }
        }

        @Override
        public boolean canExecute() {
            return this.ogre.collidedHorizontally && this.ogre.attackTarget != null;
        }
    }
}
