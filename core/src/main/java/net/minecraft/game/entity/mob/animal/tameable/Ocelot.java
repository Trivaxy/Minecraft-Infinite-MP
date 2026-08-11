package net.minecraft.game.entity.mob.animal.tameable;

import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.decoration.Bed;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.block.tileentity.container.ChestEntity;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.Action;
import net.minecraft.game.entity.mob.ai.hostile.LeapAt;
import net.minecraft.game.entity.mob.ai.passive.FleeFrom;
import net.minecraft.game.entity.mob.ai.passive.LookAround;
import net.minecraft.game.entity.mob.ai.passive.LookAt;
import net.minecraft.game.entity.mob.ai.passive.Swim;
import net.minecraft.game.entity.mob.ai.passive.WanderAround;
import net.minecraft.game.entity.mob.ai.tameable.ChooseWildTarget;
import net.minecraft.game.entity.mob.ai.tameable.FollowOwner;
import net.minecraft.game.entity.mob.animal.Bunny;
import net.minecraft.game.entity.mob.animal.flying.Chicken;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public class Ocelot extends TameableMob {
    private Ocelot.CautiousApproach aiTempt;

    public Ocelot(World world) {
        super(world);
        this.setSize(0.8F, 0.7F);
        this.setType();
        this.moveSpeed = 0.3F;
        this.tamedItem = ItemList.fishRaw.id;
        this.behaviors.addTask(1, new Swim(this));
        this.behaviors.addTask(2, this.sitAI);
        this.behaviors.addTask(3, this.aiTempt = new Ocelot.CautiousApproach(this, 0.18F, this.tamedItem, true));
        this.behaviors.addTask(4, new FleeFrom(this, Player.class, 16.0F, this.moveSpeed, this.moveSpeed + this.moveSpeed / 2.0F));
        this.behaviors.addTask(5, new FollowOwner(this, 0.3F, 10.0F, 5.0F));
        this.behaviors.addTask(6, new Ocelot.SitRandomly(this, 0.4F));
        this.behaviors.addTask(7, new LeapAt(this, 0.3F));
        this.behaviors.addTask(8, new Ocelot.PounceTarget(this));
        this.behaviors.addTask(9, new WanderAround(this, this.moveSpeed, true));
        this.behaviors.addTask(10, new LookAround(this));
        this.behaviors.addTask(11, new LookAt(this, Player.class, 10.0F));
        this.detections.addTask(1, new ChooseWildTarget(this, Chicken.class, 14.0F, 16, false));
        this.detections.addTask(1, new ChooseWildTarget(this, Bunny.class, 16.0F, 16, false));
    }

    @Override
    public boolean isInWater() {
        return this.world.handleWaterMovement(this.boundingBox, MaterialList.water, this);
    }

    @Override
    public void setType() {
        switch (this.type) {
            case 1:
                this.texture = "/mob/cat/cat_tuxedo.png";
                break;
            case 2:
                this.texture = "/mob/cat/cat_red.png";
                break;
            case 3:
                this.texture = "/mob/cat/cat_siamese.png";
                break;
            case 4:
                this.texture = "/mob/cat/cat_tabby.png";
                break;
            case 5:
                this.texture = "/mob/cat/cat_jellie.png";
                break;
            case 6:
                this.texture = "/mob/cat/cat_calico.png";
                break;
            case 7:
                this.texture = "/mob/cat/cat_persian.png";
                break;
            case 8:
                this.texture = "/mob/cat/cat_ragdoll.png";
                break;
            case 9:
                this.texture = "/mob/cat/cat_shorthair.png";
                break;
            case 10:
                this.texture = "/mob/cat/cat_white.png";
                break;
            case 11:
                this.texture = "/mob/cat/cat_black.png";
                break;
            default:
                this.texture = "/mob/cat/ozelot.png";
        }
    }

    @Override
    public int getSpawnID() {
        return 15;
    }

    @Override
    protected void fall(float dist) {
    }

    @Override
    public String getSound() {
        return this.isTamed() ? (this.rand.nextInt(4) == 0 ? "mob.cat.purreow" : "mob.cat.meow") : "";
    }

    @Override
    protected String getHurtSound() {
        return "mob.cat.hitt";
    }

    @Override
    protected String getDeathSound() {
        return "mob.cat.hitt";
    }

    @Override
    protected void onTamed(boolean success) {
        super.onTamed(success);
        if (success) {
            this.type = (byte)(this.rand.nextInt(11) + 1);
            this.setType();
        }
    }

    @Override
    public boolean interact(Player player) {
        ItemStack heldItem = player.inventory.getCurrentItem();
        if (player.getUsing()) {
            heldItem = player.inventory.getOffHandItem();
        }

        if (!this.isTamed()) {
            return this.aiTempt.isFleeing() ? this.tameMob(player, heldItem) : false;
        } else {
            return this.healMob(player, heldItem);
        }
    }

    @Override
    protected boolean validFood(Item food) {
        return food == ItemList.fishCooked || food == ItemList.fishRaw || food == ItemList.rottenFlesh;
    }

    @Override
    public float getPathWeight(int xCoord, int yCoord, int zCoord) {
        return this.world.getBlockId(xCoord, yCoord - 1, zCoord) != BlockList.grass.id
                && this.world.getBlockId(xCoord, yCoord - 1, zCoord) != BlockList.leaves.id
            ? this.world.getBrightness(xCoord, yCoord, zCoord) - 0.5F
            : 10.0F;
    }

    @Override
    public boolean canSpawnHere() {
        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_double(this.boundingBox.minY);
        int z = MathHelper.floor_double(this.posZ);
        return (this.world.getBlockId(x, y - 1, z) == BlockList.grass.id || this.world.getBlockId(x, y - 1, z) == BlockList.leaves.id)
            && this.world.getBlockBrightness(x, y, z) > 8
            && this.getPathWeight(x, y, z) >= 0.0F
            && this.world.isClear(this.boundingBox)
            && this.world.getBoundingBoxes(this, this.boundingBox).size() == 0
            && !this.world.containsLiquid(this.boundingBox);
    }

    private class CautiousApproach extends Action {
        private Mob host;
        private Player tempter;
        private float friction;
        private double tempterX;
        private double tempterY;
        private double tempterZ;
        private double tempterPitch;
        private double tempterYaw;
        private int temptDelay = 0;
        private int temptItem;
        private boolean startled;
        private boolean fleeing;

        private CautiousApproach(Mob hsot, float f, int i, boolean b) {
            this.host = hsot;
            this.friction = f;
            this.temptItem = i;
            this.startled = b;
            this.setMutexBits(3);
        }

        @Override
        public boolean canExecute() {
            if (this.temptDelay > 0) {
                this.temptDelay--;
                return false;
            }

            this.tempter = this.host.world.getClosestPlayer(this.host, 10.0);
            if (this.tempter == null) {
                return false;
            }

            ItemStack heldItem = this.tempter.getCurrentEquippedItem();
            return heldItem == null ? false : heldItem.itemID == this.temptItem;
        }

        @Override
        public boolean continueExecuting() {
            if (this.startled) {
                if (this.host.getDistanceSqTo(this.tempter) < 36.0) {
                    if (this.tempter.getDistanceSq(this.tempterX, this.tempterY, this.tempterZ) > 0.010000000000000002) {
                        return false;
                    }

                    if (Math.abs(this.tempter.pitch - this.tempterPitch) > 5.0 || Math.abs(this.tempter.yaw - this.tempterYaw) > 5.0) {
                        return false;
                    }
                } else {
                    this.tempterX = this.tempter.posX;
                    this.tempterY = this.tempter.posY;
                    this.tempterZ = this.tempter.posZ;
                }

                this.tempterPitch = this.tempter.pitch;
                this.tempterYaw = this.tempter.yaw;
            }

            return this.canExecute();
        }

        @Override
        public void start() {
            this.tempterX = this.tempter.posX;
            this.tempterY = this.tempter.posY;
            this.tempterZ = this.tempter.posZ;
            this.fleeing = true;
        }

        @Override
        public void stop() {
            this.tempter = null;
            this.host.navigator.clearPath();
            this.temptDelay = 100;
            this.fleeing = false;
        }

        @Override
        public void tick() {
            this.host.lookControl.faceEntity(this.tempter, 30.0F, this.host.getFaceSpeed());
            if (this.host.getDistanceSqTo(this.tempter) < 6.25) {
                this.host.navigator.clearPath();
            } else {
                this.host.navigator.moveToTarget(this.tempter, this.friction);
            }
        }

        public boolean isFleeing() {
            return this.fleeing;
        }
    }

    private class PounceTarget extends Action {
        Mob theEntity;
        Mob target;
        int attackCooldown = 0;

        private PounceTarget(Mob src) {
            this.theEntity = src;
            this.setMutexBits(3);
        }

        @Override
        public boolean canExecute() {
            Mob target = this.theEntity.attackTarget;
            if (target == null) {
                return false;
            }

            this.target = target;
            return true;
        }

        @Override
        public boolean continueExecuting() {
            return !this.target.isAlive()
                ? false
                : (this.theEntity.getDistanceSqTo(this.target) > 225.0 ? false : !this.theEntity.navigator.noPath() || this.canExecute());
        }

        @Override
        public void stop() {
            this.target = null;
            this.theEntity.navigator.clearPath();
        }

        @Override
        public void tick() {
            this.theEntity.lookControl.faceEntity(this.target, 30.0F, 30.0F);
            double maxDist = this.theEntity.width * 2.0F * this.theEntity.width * 2.0F;
            double dist = this.theEntity.getDistanceSq(this.target.posX, this.target.boundingBox.minY, this.target.posZ);
            float spd = this.theEntity.moveSpeed;
            if (dist > maxDist && dist < 16.0) {
                spd = this.theEntity.moveSpeed * 2.0F;
            } else if (dist < 225.0) {
                spd = this.theEntity.moveSpeed / 2.0F;
            }

            this.theEntity.navigator.moveToTarget(this.target, spd);
            this.attackCooldown = Math.max(this.attackCooldown - 1, 0);
            if (dist <= maxDist && this.attackCooldown <= 0) {
                this.attackCooldown = 20;
                this.target.damageEntity(DamageSource.mobDamage(this.theEntity), this.theEntity.attackStrength);
            }
        }
    }

    private class SitRandomly extends Action {
        private final Ocelot theOcelot;
        private final float friction;
        private int tick = 0;
        private int sitTime = 0;
        private int chance = 0;
        private int sitX = 0;
        private int sitY = 0;
        private int sitZ = 0;

        private SitRandomly(Ocelot ocelot, float f) {
            this.theOcelot = ocelot;
            this.friction = f;
            this.setMutexBits(5);
        }

        @Override
        public boolean canExecute() {
            return this.theOcelot.isTamed() && !this.theOcelot.isSitting() && this.theOcelot.rand.nextDouble() <= 0.0065F && this.findSitPos();
        }

        @Override
        public boolean continueExecuting() {
            return this.tick <= this.chance && this.sitTime <= 60 && this.canSitHere(this.theOcelot.world, this.sitX, this.sitY, this.sitZ);
        }

        @Override
        public void start() {
            this.theOcelot.navigator.moveToLocation(this.sitX + 0.5, this.sitY + 1, this.sitZ + 0.5, this.friction);
            this.tick = 0;
            this.sitTime = 0;
            this.chance = this.theOcelot.rand.nextInt(this.theOcelot.rand.nextInt(1200) + 1200) + 1200;
            this.theOcelot.sitAI.setSitting(false);
        }

        @Override
        public void stop() {
            this.theOcelot.setSitting(false);
        }

        @Override
        public void tick() {
            this.tick++;
            this.theOcelot.sitAI.setSitting(false);
            if (this.theOcelot.getDistanceSq(this.sitX, this.sitY + 1, this.sitZ) > 1.0) {
                this.theOcelot.setSitting(false);
                this.theOcelot.navigator.moveToLocation(this.sitX + 0.5, this.sitY + 1, this.sitZ + 0.5, this.friction);
                this.sitTime++;
            } else if (!this.theOcelot.isSitting()) {
                this.theOcelot.setSitting(true);
            } else {
                this.sitTime--;
            }
        }

        private boolean findSitPos() {
            int y = (int)this.theOcelot.posY;
            double maxDist = 2.147483647E9;

            for (int x = (int)this.theOcelot.posX - 8; x < this.theOcelot.posX + 8.0; x++) {
                for (int z = (int)this.theOcelot.posZ - 8; z < this.theOcelot.posZ + 8.0; z++) {
                    if (this.canSitHere(this.theOcelot.world, x, y, z) && this.theOcelot.world.isAir(x, y + 1, z)) {
                        double dist = this.theOcelot.getDistanceSq(x, y, z);
                        if (dist < maxDist) {
                            this.sitX = x;
                            this.sitY = y;
                            this.sitZ = z;
                            maxDist = dist;
                        }
                    }
                }
            }

            return maxDist < 2.147483647E9;
        }

        private boolean canSitHere(World world, int x, int y, int z) {
            int id = world.getBlockId(x, y, z);
            int metadata = world.getBlockMetadata(x, y, z);
            if (id == BlockList.chest.id) {
                ChestEntity chest = (ChestEntity)world.getTileEntity(x, y, z);
                if (chest.players < 1) {
                    return true;
                }
            } else {
                if (id == BlockList.stoneOvenActive.id) {
                    return true;
                }

                if (BlockList.blocks[id] instanceof Bed && !Bed.isFoot(metadata)) {
                    return true;
                }
            }

            return false;
        }
    }
}
