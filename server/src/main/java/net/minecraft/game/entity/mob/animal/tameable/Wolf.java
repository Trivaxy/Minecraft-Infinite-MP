package net.minecraft.game.entity.mob.animal.tameable;

import com.mojang.nbt.tag.special.CompoundTag;
import net.minecraft.game.ParticleType;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.arrow.Arrow;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.Action;
import net.minecraft.game.entity.mob.ai.hostile.Attack;
import net.minecraft.game.entity.mob.ai.hostile.ChooseTarget;
import net.minecraft.game.entity.mob.ai.hostile.DetectEnemy;
import net.minecraft.game.entity.mob.ai.hostile.LeapAt;
import net.minecraft.game.entity.mob.ai.passive.LookAround;
import net.minecraft.game.entity.mob.ai.passive.LookAt;
import net.minecraft.game.entity.mob.ai.passive.Swim;
import net.minecraft.game.entity.mob.ai.passive.WanderAround;
import net.minecraft.game.entity.mob.ai.tameable.AggroWild;
import net.minecraft.game.entity.mob.ai.tameable.ChooseWildTarget;
import net.minecraft.game.entity.mob.ai.tameable.FollowOwner;
import net.minecraft.game.entity.mob.animal.Deer;
import net.minecraft.game.entity.mob.animal.Sheep;
import net.minecraft.game.entity.mob.animal.flying.Ostrich;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.statistics.achievement.AchievementList;
import net.minecraft.util.math.MathHelper;

public class Wolf extends TameableMob {
    private String prefix;
    public boolean tiltHead = false;
    public boolean armored = false;
    private boolean playShakingAnimation;
    private boolean isShaking;
    private float shakeFactor;
    private float prevShakeFactor;
    private float shakeTime;
    private float prevShakeTime;
    public int color;

    public Wolf(World world) {
        super(world);
        this.setSize(0.85F, 0.85F);
        this.setHealth(8);
        this.moveSpeed = 0.3F;
        this.texture = "/mob/wolf/wolf.png";
        this.prefix = "wolf";
        this.tamedItem = ItemList.bone.id;
        this.behaviors.addTask(1, new Swim(this));
        this.behaviors.addTask(2, this.sitAI);
        this.behaviors.addTask(3, new LeapAt(this, 0.4F));
        this.behaviors.addTask(4, new Attack(this, this.moveSpeed, true, false));
        this.behaviors.addTask(5, new FollowOwner(this, this.moveSpeed, 10.0F, 2.0F));
        this.behaviors.addTask(7, new WanderAround(this, this.moveSpeed, true));
        this.behaviors.addTask(8, new Wolf.Beg(this, 8.0F));
        this.behaviors.addTask(9, new LookAt(this, Player.class, 8.0F));
        this.behaviors.addTask(9, new LookAround(this));
        this.detections.addTask(1, new Wolf.OwnerHurt(this));
        this.detections.addTask(2, new Wolf.OwnerAttacks(this));
        this.detections.addTask(3, new AggroWild(this, true));
        this.detections.addTask(4, new ChooseWildTarget(this, Sheep.class, 16.0F, 16, false));
        this.detections.addTask(4, new ChooseWildTarget(this, Deer.class, 16.0F, 16, false));
        this.detections.addTask(4, new ChooseWildTarget(this, Ostrich.class, 16.0F, 16, false));
        this.detections.addTask(5, new Wolf.AngryAt(this, Player.class, 16.0F, 0, true));
    }

    @Override
    public int getSpawnID() {
        return 10;
    }

    @Override
    public void setTypeOnSpawn() {
        int x = MathHelper.floor_double(this.posX);
        int z = MathHelper.floor_double(this.posZ);
        Biome biome = this.world.getBiomeAt(x, z);
        if (biome == BiomeList.taiga) {
            this.prefix = "wolf_snowy";
        } else if (biome == BiomeList.megaTaiga) {
            this.prefix = "wolf_black";
        } else if (biome == BiomeList.dryTaiga) {
            this.prefix = "wolf_chestnut";
        } else if (biome == BiomeList.savanna || biome == BiomeList.pampas) {
            this.prefix = "wolf_striped";
        } else if (biome == BiomeList.outback) {
            this.prefix = "wolf_spotted";
        } else if (biome == BiomeList.sparseJungle || biome == BiomeList.jungle) {
            this.prefix = "wolf_rusty";
        } else if (biome == BiomeList.woodlandHills) {
            this.prefix = "wolf_woods";
        } else {
            this.prefix = "wolf";
        }
    }

    @Override
    protected void init() {
        super.init();
        this.dataSync.addObject(18, this.health);
    }

    @Override
    public String getTexture() {
        return this.isTamed()
            ? "/mob/wolf/" + this.prefix + "_tame.png"
            : (this.isAngry() ? "/mob/wolf/" + this.prefix + "_angry.png" : "/mob/wolf/" + this.prefix + ".png");
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        tag.setBoolean("Angry", this.isAngry());
        tag.setBoolean("Armor", this.armored);
        tag.setString("Texture", this.prefix);
        tag.setInteger("ArmorColor", this.color);
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        this.setAngry(tag.getBoolean("Angry"));
        this.armored = tag.getBoolean("Armor");
        this.prefix = tag.getString("Texture");
        this.color = tag.getInteger("ArmorColor");
    }

    @Override
    public String getSound() {
        if (this.isAngry()) {
            return "mob.wolf.growl";
        }

        if (this.isTamed()) {
            if (this.dataSync.getObjectInt(18) < 10) {
                return "mob.wolf.whine";
            } else {
                return this.rand.nextInt(3) == 0 ? "mob.wolf.panting" : "mob.wolf.bark";
            }
        } else {
            return "mob.wolf.bark";
        }
    }

    @Override
    protected String getHurtSound() {
        return "mob.wolf.hurt";
    }

    @Override
    protected String getDeathSound() {
        return "mob.wolf.death";
    }

    @Override
    public float getSoundVolume() {
        return 0.4F;
    }

    @Override
    public void mobTick() {
        super.mobTick();
        if (!this.isMultiplayer && this.playShakingAnimation && !this.isShaking && this.navigator.noPath() && this.onGround) {
            this.isShaking = true;
            this.shakeTime = 0.0F;
            this.prevShakeTime = 0.0F;
            this.world.setEntityState(this, (byte)8);
        }

        this.dataSync.updateObject(18, this.health);
    }

    @Override
    public void onTick() {
        super.onTick();
        this.prevShakeFactor = this.shakeFactor;
        if (this.tiltHead) {
            this.shakeFactor = this.shakeFactor + (1.0F - this.shakeFactor) * 0.4F;
        } else {
            this.shakeFactor = this.shakeFactor + (0.0F - this.shakeFactor) * 0.4F;
        }

        if (this.inWater) {
            this.isShaking = false;
            this.playShakingAnimation = true;
            this.shakeTime = 0.0F;
            this.prevShakeTime = 0.0F;
        } else if (this.isShaking) {
            if (this.shakeTime == 0.0F) {
                this.world.playEntitySound(this, "mob.wolf.shake", this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            }

            this.prevShakeTime = this.shakeTime;
            this.shakeTime += 0.05F;
            if (this.prevShakeTime >= 2.0F) {
                this.playShakingAnimation = false;
                this.isShaking = false;
                this.prevShakeTime = 0.0F;
                this.shakeTime = 0.0F;
            }

            if (this.shakeTime > 0.4F) {
                int totalDroplets = (int)(MathHelper.sin((this.shakeTime - 0.4F) * (float) Math.PI) * 7.0F);

                for (int i = 0; i < totalDroplets; i++) {
                    float xOffset = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;
                    float zOffset = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;
                    this.world
                        .spawnParticle(
                            ParticleType.RAINDROP,
                            this.posX + xOffset,
                            this.boundingBox.minY + 0.8,
                            this.posZ + zOffset,
                            this.motionX,
                            this.motionY,
                            this.motionZ
                        );
                }
            }
        }
    }

    public boolean getWolfShaking() {
        return this.playShakingAnimation;
    }

    public float getShading(float factor) {
        return 0.75F + (this.prevShakeTime + (this.shakeTime - this.prevShakeTime) * factor) / 2.0F * 0.25F;
    }

    public float getShakeAngle(float shake, float offset) {
        float angle = (this.prevShakeTime + (this.shakeTime - this.prevShakeTime) * shake + offset) / 1.8F;
        if (angle < 0.0F) {
            angle = 0.0F;
        } else if (angle > 1.0F) {
            angle = 1.0F;
        }

        return MathHelper.sin(angle * (float) Math.PI) * MathHelper.sin(angle * (float) Math.PI * 11.0F) * 0.15F * (float) Math.PI;
    }

    public float getHeadTilt(float angle) {
        return (this.prevShakeFactor + (this.shakeFactor - this.prevShakeFactor) * angle) * 0.15F * (float) Math.PI;
    }

    @Override
    public float getEyeHeight() {
        return this.height * 0.8F;
    }

    @Override
    public boolean damageEntity(DamageSource src, int amount) {
        Entity attacker = src.getEntity();
        this.sitAI.setSitting(false);
        if (attacker != null && !(attacker instanceof Player) && !(attacker instanceof Arrow)) {
            amount = (amount + 1) / 2;
        }

        if (attacker != null && this.armored) {
            amount /= 2;
        }

        if (!this.isTamed() && attacker != null) {
            this.setAngry(true);
        }

        return super.damageEntity(src, amount);
    }

    @Override
    protected boolean validFood(Item food) {
        return food == ItemList.porkCooked
            || food == ItemList.porkRaw
            || food == ItemList.rottenFlesh
            || food == ItemList.muttonCooked
            || food == ItemList.muttonRaw
            || food == ItemList.rabbitCooked
            || food == ItemList.rabbitRaw;
    }

    @Override
    public boolean interact(Player player) {
        ItemStack heldItem = player.inventory.getCurrentItem();
        if (player.getUsing()) {
            heldItem = player.inventory.getOffHandItem();
        }

        if (!this.isTamed() && !this.isAngry()) {
            return this.tameMob(player, heldItem);
        }

        if (!this.healMob(player, heldItem)) {
            if (heldItem != null && ItemList.items[heldItem.itemID] == ItemList.wolfArmor) {
                if (player.getUsing()) {
                    player.inventory.setSlotContents(44, null);
                } else {
                    player.inventory.setSlotContents(player.inventory.currentSlot, null);
                }

                player.triggerAchievement(AchievementList.wolfArmor);
                this.armored = true;
                this.color = heldItem.getItem().getColor(heldItem);
                return true;
            }

            if (this.armored && player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == ItemList.shears) {
                if (!this.world.isOnline) {
                    this.armored = false;
                    ItemStack armor = new ItemStack(ItemList.wolfArmor.id, 1, 0);
                    armor.getItem().addColor(armor, this.color);
                    DroppedItem item = new DroppedItem(this.world, this.posX, this.posY, this.posZ, armor);
                    item.pickupDelay = 10;
                    this.world.spawnEntity(item);
                    item.motionY = item.motionY + this.rand.nextFloat() * 0.05F;
                    item.motionX = item.motionX + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F;
                    item.motionZ = item.motionZ + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F;
                    this.world.playSoundEffect(this.posX + 0.5, this.posY + 0.5, this.posZ + 0.5, "random.shear", 1.0F, this.rand.nextFloat() * 0.4F + 0.8F);
                }

                player.swingItem();
                player.getCurrentEquippedItem().damageItem(1, player);
                return true;
            }
        }

        return false;
    }

    @Override
    public void updateState(byte state) {
        if (state == 8) {
            this.isShaking = true;
            this.shakeTime = 0.0F;
            this.prevShakeTime = 0.0F;
        } else {
            super.updateState(state);
        }
    }

    public float setTailRotation() {
        return this.isAngry()
            ? 1.5393804F
            : (this.isTamed() ? (0.55F - (20 - this.dataSync.getObjectInt(18)) * 0.02F) * (float) Math.PI : (float) (Math.PI / 5));
    }

    public boolean isAngry() {
        return (this.dataSync.getObjectByte(16) & 2) != 0;
    }

    public void setAngry(boolean angy) {
        byte state = this.dataSync.getObjectByte(16);
        if (angy) {
            this.dataSync.updateObject(16, (byte)(state | 2));
        } else {
            this.dataSync.updateObject(16, (byte)(state & -3));
        }
    }

    private class AngryAt extends DetectEnemy {
        private Wolf wolf;

        public AngryAt(Mob target, Class<?> targetClass, float dist, int chance, boolean chase) {
            super(target, targetClass, dist, chance, chase);
            this.wolf = (Wolf)target;
        }

        @Override
        public boolean canExecute() {
            return super.canExecute() && this.wolf.isAngry();
        }

        @Override
        public boolean continueExecuting() {
            return super.continueExecuting() && this.wolf.isAngry();
        }
    }

    private class Beg extends Action {
        private Wolf wolf;
        private Player owner;
        private World world;
        private float distance;
        private int tilt;

        private Beg(Wolf wolf, float f) {
            this.wolf = wolf;
            this.world = wolf.world;
            this.distance = f;
            this.setMutexBits(2);
        }

        @Override
        public boolean canExecute() {
            this.owner = this.world.getClosestPlayer(this.wolf, this.distance);
            return this.owner == null ? false : this.tiltHead(this.owner);
        }

        @Override
        public boolean continueExecuting() {
            return !this.owner.isAlive()
                ? false
                : (this.wolf.getDistanceSqTo(this.owner) > this.distance * this.distance ? false : this.tilt > 0 && this.tiltHead(this.owner));
        }

        @Override
        public void start() {
            this.wolf.tiltHead = true;
            this.tilt = 40 + this.wolf.rand.nextInt(40);
        }

        @Override
        public void stop() {
            this.wolf.tiltHead = false;
            this.owner = null;
        }

        @Override
        public void tick() {
            this.wolf
                .lookControl
                .setLookPosition(this.owner.posX, this.owner.posY + this.owner.getEyeHeight(), this.owner.posZ, 10.0F, this.wolf.getFaceSpeed());
            this.tilt--;
        }

        private boolean tiltHead(Player player) {
            ItemStack heldItem = player.inventory.getCurrentItem();
            return heldItem == null ? false : (!this.wolf.isTamed() && heldItem.itemID == ItemList.bone.id ? true : this.wolf.validFood(heldItem.getItem()));
        }
    }

    private class OwnerAttacks extends ChooseTarget {
        private TameableMob source;
        private Mob target;

        private OwnerAttacks(TameableMob mob) {
            super(mob, 32.0F, false);
            this.source = mob;
            this.setMutexBits(1);
        }

        @Override
        public boolean canExecute() {
            if (!this.source.isTamed()) {
                return false;
            } else {
                Mob owner = this.source.getOwner();
                if (owner != null && (!(owner.attackTarget instanceof TameableMob) || !((TameableMob)owner.attackTarget).isTamed())) {
                    this.target = owner.attackTarget;
                    return this.foundValidTarget(this.target, false);
                } else {
                    return false;
                }
            }
        }

        @Override
        public void start() {
            this.src.attackTarget = this.target;
            super.start();
        }
    }

    private class OwnerHurt extends ChooseTarget {
        private TameableMob source;
        private Mob target;

        private OwnerHurt(TameableMob mob) {
            super(mob, 32.0F, false);
            this.source = mob;
            this.setMutexBits(1);
        }

        @Override
        public boolean canExecute() {
            if (!this.source.isTamed()) {
                return false;
            }

            Mob mob = this.source.getOwner();
            if (mob == null) {
                return false;
            }

            this.target = mob.getLastTarget();
            return this.foundValidTarget(this.target, false);
        }

        @Override
        public void start() {
            this.src.attackTarget = this.target;
            super.start();
        }
    }
}
