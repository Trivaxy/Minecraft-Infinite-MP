package net.minecraft.game.entity.mob.animal.flying;

import com.mojang.nbt.tag.special.CompoundTag;
import java.util.List;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.hostile.Aggro;
import net.minecraft.game.entity.mob.ai.hostile.Attack;
import net.minecraft.game.entity.mob.ai.hostile.DetectEnemy;
import net.minecraft.game.entity.mob.ai.passive.LookAround;
import net.minecraft.game.entity.mob.ai.passive.LookAt;
import net.minecraft.game.entity.mob.ai.passive.Swim;
import net.minecraft.game.entity.mob.ai.passive.WanderAround;
import net.minecraft.game.entity.mob.follower.Decoy;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.physics.Vector;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.random.RandomPositionGenerator;

public class Penguin extends FlyingMob {
    public boolean attacking = false;
    public int angerLevel = 0;

    public Penguin(World world) {
        super(world);
        this.setSize(0.6F, 1.0F);
        this.texture = "/mob/penguin.png";
        this.moveSpeed = 0.32F;
        this.scoreValue = 50;
        this.behaviors.addTask(0, new Swim(this));
        this.behaviors.addTask(2, new Penguin.PenguinAttack(this, this.moveSpeed + this.moveSpeed / 2.0F, false, false));
        this.behaviors.addTask(6, new WanderAround(this, this.moveSpeed, true));
        this.behaviors.addTask(7, new LookAt(this, Player.class, 8.0F));
        this.behaviors.addTask(7, new LookAround(this));
        this.detections.addTask(1, new Penguin.RevengeAttack(this, true));
        this.detections.addTask(2, new Penguin.DetectPlayer(this, Decoy.class, 16.0F, 0, true));
        this.detections.addTask(3, new Penguin.DetectPlayer(this, Player.class, 16.0F, 0, true));
    }

    @Override
    public void mobTick() {
        super.mobTick();
        if (this.angerLevel > 0) {
            this.angerLevel--;
        }
    }

    @Override
    public int getSpawnID() {
        return 29;
    }

    @Override
    public final boolean isInWater() {
        return this.world.handleWaterMovement(this.boundingBox, MaterialList.water, this);
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        tag.setShort("Anger", (short)this.angerLevel);
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        this.angerLevel = tag.getShort("Anger");
    }

    @Override
    public String getSound() {
        return "mob.penguin";
    }

    @Override
    protected String getHurtSound() {
        return "mob.penguinhurt";
    }

    @Override
    protected String getDeathSound() {
        return "mob.penguinhurt";
    }

    @Override
    public void playStepSound() {
        this.world.playEntitySound(this, "mob.chickenstep", 0.15F, 1.0F);
    }

    @Override
    public float getSoundVolume() {
        return 0.2F;
    }

    @Override
    public int specialDrop() {
        return ItemList.beak.id;
    }

    @Override
    public float getEyeHeight() {
        return this.height * 0.8F;
    }

    @Override
    public float getPathWeight(int xCoord, int yCoord, int zCoord) {
        return this.world.getBlockId(xCoord, yCoord - 1, zCoord) != BlockList.grass.id
                && this.world.getBlockId(xCoord, yCoord - 1, zCoord) != BlockList.snowBlock.id
            ? this.world.getBrightness(xCoord, yCoord, zCoord) - 0.5F
            : 10.0F;
    }

    @Override
    public boolean canSpawnHere() {
        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_double(this.boundingBox.minY);
        int z = MathHelper.floor_double(this.posZ);
        return (this.world.getBlockId(x, y - 1, z) == BlockList.grass.id || this.world.getBlockId(x, y - 1, z) == BlockList.snowBlock.id)
            && this.world.getBlockBrightness(x, y, z) > 8
            && this.getPathWeight(x, y, z) >= 0.0F
            && this.world.isClear(this.boundingBox)
            && this.world.getBoundingBoxes(this, this.boundingBox).size() == 0
            && !this.world.containsLiquid(this.boundingBox);
    }

    @Override
    public boolean damageEntity(DamageSource src, int amount) {
        Entity target = src.getEntity();
        if (target instanceof Player) {
            List<Entity> entities = this.world.getEntities(this, this.boundingBox.expand(32.0, 32.0, 32.0));

            for (int i = 0; i < entities.size(); i++) {
                Entity peggu = entities.get(i);
                if (peggu instanceof Penguin) {
                    ((Penguin)peggu).angerLevel = 400 + this.rand.nextInt(400);
                }
            }

            this.angerLevel = 400 + this.rand.nextInt(400);
        }

        return super.damageEntity(src, amount);
    }

    private class DetectPlayer extends DetectEnemy {
        private Penguin peggu;

        public DetectPlayer(Mob target, Class<?> targetClass, float dist, int chance, boolean chase) {
            super(target, targetClass, dist, chance, chase);
            this.peggu = (Penguin)target;
        }

        @Override
        public boolean canExecute() {
            return this.peggu.angerLevel > 0 && super.canExecute();
        }
    }

    private class PenguinAttack extends Attack {
        Penguin peggu;

        private PenguinAttack(Mob src, float spd, boolean priority, boolean sprint) {
            super(src, spd, priority, sprint);
            this.peggu = (Penguin)src;
        }

        @Override
        public void start() {
            super.start();
            this.peggu.attacking = true;
        }

        @Override
        public void stop() {
            this.target = null;
            this.peggu.attacking = false;
        }

        @Override
        protected void attack() {
            super.attack();
            Vector hostLocation = RandomPositionGenerator.findRandomTargetAwayFrom(
                this.attacker, 8, 1, Vector.createVector(this.target.posX, this.target.posY, this.target.posZ)
            );
            if (hostLocation != null) {
                this.attacker.attackTarget = null;
                this.attacker.navigator.clearPath();
                this.path = this.attacker.navigator.pathToLocation(hostLocation.xCoord, hostLocation.yCoord, hostLocation.zCoord);
                this.attacker.navigator.setPath(this.path, this.attacker.moveSpeed);
            }
        }
    }

    private class RevengeAttack extends Aggro {
        private RevengeAttack(Mob target, boolean agro) {
            super(target, agro);
        }

        @Override
        public boolean canExecute() {
            return super.canExecute() && this.src.navigator.noPath();
        }
    }
}
