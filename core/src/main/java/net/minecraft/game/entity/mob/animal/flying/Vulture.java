package net.minecraft.game.entity.mob.animal.flying;

import java.util.List;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.Action;
import net.minecraft.game.entity.mob.ai.flying.FlyAtRandom;
import net.minecraft.game.entity.mob.ai.flying.FlyToTarget;
import net.minecraft.game.entity.mob.ai.flying.LookAroundFlying;
import net.minecraft.game.entity.mob.ai.flying.LookAtOnGround;
import net.minecraft.game.entity.mob.ai.hostile.Aggro;
import net.minecraft.game.entity.mob.ai.hostile.DetectEnemy;
import net.minecraft.game.entity.mob.ai.passive.LookAround;
import net.minecraft.game.entity.mob.ai.passive.Swim;
import net.minecraft.game.entity.mob.ai.passive.WanderAround;
import net.minecraft.game.entity.mob.ai.util.MoveControl;
import net.minecraft.game.entity.mob.animal.Animal;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.pathfinder.Path;
import net.minecraft.util.math.MathHelper;

public class Vulture extends FlyingMob {
    public Vulture(World world) {
        super(world);
        this.setSize(0.8F, 0.7F);
        this.setHealth(12);
        this.texture = "/mob/vulture.png";
        this.moveSpeed = 0.3F;
        this.scoreValue = 50;
        this.behaviors.addTask(1, new Swim(this));
        this.behaviors.addTask(2, new Vulture.DistractedByMeat(this, 16.0F));
        this.behaviors.addTask(3, new Vulture.AttackDamagedTarget(this, 0.1F));
        this.behaviors.addTask(4, new FlyAtRandom(this, 6.0F, 0.1F));
        this.behaviors.addTask(5, new LookAroundFlying(this, true));
        this.behaviors.addTask(6, new WanderAround(this, this.moveSpeed, true));
        this.behaviors.addTask(7, new LookAtOnGround(this, Player.class, 6.0F));
        this.behaviors.addTask(8, new LookAround(this));
        this.detections.addTask(1, new Aggro(this, false));
        this.detections.addTask(2, new Vulture.FindDamagedTarget(this, Player.class, 32.0F, 0, true));
        this.detections.addTask(3, new Vulture.FindDamagedTarget(this, Animal.class, 32.0F, 0, true));
    }

    @Override
    public void mobTick() {
        super.mobTick();
        if (this.navigator.noPath() && this.onGround && (this.rand.nextInt(200) == 0 || this.attackTarget != null)) {
            this.flying = true;
            this.moveControl = this.flyMovement;
        }
    }

    @Override
    public int getSpawnID() {
        return 25;
    }

    @Override
    public final boolean isInWater() {
        return this.world.handleWaterMovement(this.boundingBox.expand(0.0, -0.2F, 0.0), MaterialList.water, this);
    }

    @Override
    protected void fall(float dist) {
    }

    @Override
    public float getSoundVolume() {
        return 0.8F;
    }

    @Override
    public String getSound() {
        return "mob.vulture";
    }

    @Override
    protected String getHurtSound() {
        return "mob.vulture";
    }

    @Override
    protected String getDeathSound() {
        return "mob.vulture";
    }

    @Override
    public void playStepSound() {
        this.world.playEntitySound(this, "mob.chickenstep", 0.15F, 1.0F);
    }

    @Override
    public int getDrop() {
        return ItemList.feather.id;
    }

    @Override
    public int specialDrop() {
        return ItemList.leg.id;
    }

    @Override
    public float getPathWeight(int xCoord, int yCoord, int zCoord) {
        return this.world.getBlockId(xCoord, yCoord - 1, zCoord) != BlockList.sand.id
                && this.world.getBlockId(xCoord, yCoord - 1, zCoord) != BlockList.redSand.id
            ? this.world.getBrightness(xCoord, yCoord, zCoord) - 0.5F
            : 10.0F;
    }

    @Override
    public boolean canSpawnHere() {
        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_double(this.boundingBox.minY);
        int z = MathHelper.floor_double(this.posZ);
        return (this.world.getBlockId(x, y - 1, z) == BlockList.sand.id || this.world.getBlockId(x, y - 1, z) == BlockList.redSand.id)
            && this.world.getBlockBrightness(x, y, z) > 8
            && this.getPathWeight(x, y, z) >= 0.0F
            && this.world.isClear(this.boundingBox)
            && this.world.getBoundingBoxes(this, this.boundingBox).size() == 0
            && !this.world.containsLiquid(this.boundingBox);
    }

    @Override
    public void knockBack(Entity entity, double forceX, double forceZ) {
        if (!this.flying) {
            super.knockBack(entity, forceX, forceZ);
        }
    }

    private class AttackDamagedTarget extends FlyToTarget {
        private AttackDamagedTarget(Mob parent, float dist) {
            super(parent, dist);
        }

        @Override
        public boolean canExecute() {
            return this.mob.world.settings.getDifficulty() != 0
                && this.mob.attackTarget != null
                && this.mob.attackTarget.health < this.mob.attackTarget.maxHealth / 2
                && this.mob.flying;
        }

        @Override
        public boolean continueExecuting() {
            return this.mob.world.settings.getDifficulty() > 0 && this.mob.attackTarget != null && this.mob.flying;
        }
    }

    private class DistractedByMeat extends Action {
        private Mob mob;
        private DroppedItem target;
        private float dist;
        private double lastDistSq;
        private int stuckTicks = 0;

        private DistractedByMeat(Mob mob, float dist) {
            this.mob = mob;
            this.dist = dist;
            this.setMutexBits(1);
        }

        @Override
        public boolean canExecute() {
            this.target = this.getClosestMeat(this.mob);
            if (this.target == null || this.target.isDead) {
                return false;
            } else if (!this.mob.flying) {
                Path path = this.mob.navigator.pathToLocation(this.target.posX, this.target.posY, this.target.posZ);
                return path != null;
            } else {
                return !(this.mob.posY <= this.target.posY);
            }
        }

        @Override
        public boolean continueExecuting() {
            return this.target != null && !this.target.isDead && this.mob.getDistanceSqTo(this.target) < this.dist * this.dist;
        }

        @Override
        public void tick() {
            double currentDistSq = this.mob.getDistanceSqTo(this.target);
            if (currentDistSq >= this.lastDistSq - 0.01) {
                this.stuckTicks++;
            } else {
                this.stuckTicks = 0;
            }

            this.lastDistSq = currentDistSq;
            if (this.stuckTicks >= 20) {
                this.mob.flying = false;
                this.mob.moveControl = new MoveControl(this.mob);
            }

            if (this.mob.flying) {
                this.mob.moveControl.moveTo(this.target.posX, this.target.posY, this.target.posZ, 1.0F);
            } else if (this.mob.navigator.noPath()) {
                this.mob.navigator.moveToLocation(this.target.posX, this.target.posY, this.target.posZ, this.mob.moveSpeed);
            }
        }

        @Override
        public void start() {
            this.stuckTicks = 0;
            this.lastDistSq = this.mob.getDistanceSqTo(this.target);
            if (!this.mob.navigator.noPath()) {
                this.mob.navigator.clearPath();
            }
        }

        private DroppedItem getClosestMeat(Entity entity) {
            double maxDistSq = this.dist * this.dist;
            DroppedItem item = null;
            List<Entity> entities = this.mob.world.getEntities(this.mob, this.mob.boundingBox.expand(this.dist, this.dist, this.dist));

            for (int i = 0; i < entities.size(); i++) {
                Entity entityFromList = entities.get(i);
                if (entityFromList instanceof DroppedItem) {
                    DroppedItem droppedItem = (DroppedItem)entityFromList;
                    if (droppedItem.item.getItem() == ItemList.rottenFlesh) {
                        double distToItem = droppedItem.getDistanceSq(entity.posX, entity.posY, entity.posZ);
                        if (distToItem < maxDistSq) {
                            item = droppedItem;
                        }
                    }
                }
            }

            return item;
        }
    }

    private class FindDamagedTarget extends DetectEnemy {
        private FindDamagedTarget(Mob target, Class<?> targetClass, float dist, int chance, boolean chase) {
            super(target, targetClass, dist, chance, chase);
        }

        @Override
        protected boolean foundValidTarget(Mob target, boolean attackPlayer) {
            return target != null && target.health > target.maxHealth / 2 ? false : super.foundValidTarget(target, attackPlayer);
        }
    }
}
