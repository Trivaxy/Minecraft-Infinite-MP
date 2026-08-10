package net.minecraft.game.entity.mob.monster;

import com.mojang.nbt.tag.special.CompoundTag;
import java.util.Iterator;
import java.util.List;
import net.minecraft.game.block.Block;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.Action;
import net.minecraft.game.entity.mob.ai.Task;
import net.minecraft.game.entity.mob.ai.hostile.Aggro;
import net.minecraft.game.entity.mob.ai.hostile.Attack;
import net.minecraft.game.entity.mob.ai.hostile.ChangeCourse;
import net.minecraft.game.entity.mob.ai.hostile.DetectEnemy;
import net.minecraft.game.entity.mob.ai.hostile.RangedAttack;
import net.minecraft.game.entity.mob.ai.passive.LookAround;
import net.minecraft.game.entity.mob.ai.passive.LookAt;
import net.minecraft.game.entity.mob.ai.passive.Swim;
import net.minecraft.game.entity.mob.ai.passive.WanderAround;
import net.minecraft.game.entity.mob.follower.Decoy;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.game.world.pathfinder.Path;
import net.minecraft.statistics.achievement.AchievementList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.random.WeightedRandom;

public class Pigman extends Monster {
    public int angerLevel = 0;
    public int randomSoundDelay = 0;
    public ItemStack item;
    public boolean isSwinging = false;
    public boolean isSwingingOffhand = false;
    public int swingProgressInt = 0;
    public int swingProgressIntOffhand = 0;
    public static Pigman.LootHolder[] loots = new Pigman.LootHolder[]{
        new Pigman.LootHolder(20, ItemList.leather),
        new Pigman.LootHolder(25, ItemList.silk),
        new Pigman.LootHolder(3, ItemList.recordPig),
        new Pigman.LootHolder(5, ItemList.scrap),
        new Pigman.LootHolder(5, ItemList.scroll),
        new Pigman.LootHolder(5, ItemList.nameTag),
        new Pigman.LootHolder(5, ItemList.pearl),
        new Pigman.LootHolder(10, ItemList.potion),
        new Pigman.LootHolder(10, ItemList.saddle),
        new Pigman.LootHolder(25, ItemList.extract),
        new Pigman.LootHolder(20, ItemList.windCharge),
        new Pigman.LootHolder(20, ItemList.gunpowder),
        new Pigman.LootHolder(30, ItemList.bark),
        new Pigman.LootHolder(15, ItemList.chainmail),
        new Pigman.LootHolder(10, ItemList.musketRound),
        new Pigman.LootHolder(10, ItemList.pouch),
        new Pigman.LootHolder(15, ItemList.arrowCalming),
        new Pigman.LootHolder(25, ItemList.arrow),
        new Pigman.LootHolder(10, ItemList.topaz)
    };

    public Pigman(World world) {
        super(world);
        this.texture = "/mob/pigman/pigman.png";
        this.moveSpeed = 0.25F;
        this.attackStrength = 5;
        this.scoreValue = 125;
        this.type = (byte)this.rand.nextInt(2);
        this.behaviors.addTask(0, new Swim(this));
        this.behaviors.addTask(1, new Pigman.DistractedByItem(this, 16.0F));
        this.behaviors.addTask(2, new Pigman.Barter(this));
        this.behaviors.addTask(5, new ChangeCourse(this, this.moveSpeed));
        this.behaviors.addTask(6, new WanderAround(this, this.moveSpeed, true));
        this.behaviors.addTask(7, new Pigman.LookAtIdle(this, Player.class, 8.0F));
        this.behaviors.addTask(7, new Pigman.LookAroundIdle(this));
        this.detections.addTask(1, new Aggro(this, true));
        this.detections.addTask(2, new Pigman.DetectPlayer(this, Decoy.class, 16.0F, 0, true));
        this.detections.addTask(3, new Pigman.DetectPlayer(this, Player.class, 16.0F, 0, true));
        this.setType();
    }

    @Override
    public void setType() {
        Iterator<Task> it = this.behaviors.tasks.iterator();

        while (it.hasNext()) {
            Task entry = it.next();
            if (entry.action instanceof RangedAttack || entry.action instanceof Pigman.AttackWithSword) {
                it.remove();
            }
        }

        if (this.type == 1) {
            this.behaviors.addTask(4, new RangedAttack(this, this.moveSpeed, 0, 50));
        } else {
            this.behaviors.addTask(4, new Pigman.AttackWithSword(this, this.moveSpeed + this.moveSpeed / 2.0F, false, false));
        }
    }

    @Override
    protected void fall(float dist) {
    }

    @Override
    public int getSpawnID() {
        return 63;
    }

    @Override
    public void mobTick() {
        super.mobTick();
        if (this.angerLevel > 0) {
            this.angerLevel--;
        }

        if (this.randomSoundDelay > 0 && --this.randomSoundDelay == 0) {
            this.world
                .playEntitySound(
                    this, "mob.zombiepig.zpigangry", this.getSoundVolume() * 2.0F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) * 1.8F
                );
        }

        if (this.isSwinging) {
            this.swingProgressInt++;
            if (this.swingProgressInt >= 8) {
                this.swingProgressInt = 0;
                this.isSwinging = false;
            }
        } else {
            this.swingProgressInt = 0;
        }

        if (this.isSwingingOffhand) {
            this.swingProgressIntOffhand++;
            if (this.swingProgressIntOffhand >= 8) {
                this.swingProgressIntOffhand = 0;
                this.isSwingingOffhand = false;
            }
        } else {
            this.swingProgressIntOffhand = 0;
        }

        this.swing = this.swingProgressInt / 8.0F;
        this.swingOffhand = this.swingProgressIntOffhand / 8.0F;
    }

    @Override
    public void onDeath(DamageSource entity) {
        super.onDeath(entity);
        if (this.item != null) {
            DroppedItem drop = new DroppedItem(this.world, this.posX, this.posY - 0.3F, this.posZ, this.item);
            drop.pickupDelay = 40;
            float angle = 0.1F;
            float offset = this.rand.nextFloat() * (float) Math.PI * 2.0F;
            angle = 0.3F;
            drop.motionX = -MathHelper.sin(this.yaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.pitch / 180.0F * (float) Math.PI) * angle;
            drop.motionZ = MathHelper.cos(this.yaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.pitch / 180.0F * (float) Math.PI) * angle;
            drop.motionY = -MathHelper.sin(this.pitch / 180.0F * (float) Math.PI) * angle + 0.1F;
            angle = 0.02F;
            angle *= this.rand.nextFloat();
            drop.motionX = drop.motionX + Math.cos(offset) * angle;
            drop.motionY = drop.motionY + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F;
            drop.motionZ = drop.motionZ + Math.sin(offset) * angle;
            this.world.spawnEntity(drop);
        }
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
        tag.setShort("Anger", (short)this.angerLevel);
        if (this.item != null) {
            tag.setCompoundTag("Item", this.item.writeToNBT(new CompoundTag()));
        }
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        this.angerLevel = tag.getShort("Anger");
        CompoundTag itemTag = tag.getCompoundTag("Item");
        if (itemTag.getShort("id") > 0) {
            this.item = new ItemStack(itemTag);
        }
    }

    public void swingItem() {
        this.swingProgressInt = -1;
        this.isSwinging = true;
    }

    public void swingOffhandItem() {
        this.swingProgressIntOffhand = -1;
        this.isSwingingOffhand = true;
    }

    @Override
    public boolean damageEntity(DamageSource src, int amount) {
        Entity attacker = src.getEntity();
        if (attacker instanceof Pigman) {
            return false;
        }

        if (attacker instanceof Player) {
            List<Entity> entities = this.world.getEntities(this, this.boundingBox.expand(32.0, 32.0, 32.0));

            for (int i = 0; i < entities.size(); i++) {
                Entity pigman = entities.get(i);
                if (pigman instanceof Pigman) {
                    ((Pigman)pigman).becomeAngryAt();
                }
            }

            this.becomeAngryAt();
        }

        return super.damageEntity(src, amount);
    }

    public void becomeAngryAt() {
        this.angerLevel = 400 + this.rand.nextInt(400);
        this.randomSoundDelay = this.rand.nextInt(40);
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
    public int getDrop() {
        return ItemList.porkRaw.id;
    }

    private class AttackWithSword extends Attack {
        private Pigman pigman;

        private AttackWithSword(Mob src, float spd, boolean priority, boolean sprint) {
            super(src, spd, priority, sprint);
            this.pigman = (Pigman)src;
        }

        @Override
        protected void attack() {
            if (this.attackTimer == 0) {
                this.pigman.swingItem();
            }

            super.attack();
        }
    }

    private class Barter extends Action {
        private Pigman pigman;
        private int standTime = 0;

        private Barter(Pigman pigman) {
            this.pigman = pigman;
            this.setMutexBits(1);
        }

        @Override
        public boolean canExecute() {
            return this.pigman.item != null;
        }

        @Override
        public boolean continueExecuting() {
            return this.standTime > 0;
        }

        @Override
        public void start() {
            this.standTime = 60;
        }

        @Override
        public void tick() {
            if (this.standTime > 0) {
                if (this.standTime == 1) {
                    Pigman.LootHolder randomItem = WeightedRandom.getRandomItem(this.pigman.rand, Pigman.loots);
                    int random = this.pigman.rand.nextInt(3);
                    if (ItemList.items[randomItem.itemId].maxStackSize == 1) {
                        random = 0;
                    }

                    if (randomItem.itemId == ItemList.scroll.id || randomItem.itemId == ItemList.scrap.id) {
                        this.pigman.dropItem(randomItem.itemId, 1, this.pigman.rand.nextInt(6), 1.0F);
                    } else if (randomItem.itemId == ItemList.potion.id) {
                        this.pigman.dropItem(randomItem.itemId, 1, this.pigman.rand.nextInt(19), 1.0F);
                    } else {
                        this.pigman.dropItem(randomItem.itemId, 1 + random);
                    }

                    this.pigman.swingOffhandItem();
                    this.pigman.item = null;
                    Player player = this.pigman.world.getClosestPlayer(this.pigman, 32.0);
                    if (player != null) {
                        player.triggerAchievement(AchievementList.barter);
                    }
                }

                this.standTime--;
                this.pigman
                    .lookControl
                    .setLookPosition(
                        this.pigman.posX, this.pigman.posY + this.pigman.getEyeHeight() + 45.0, this.pigman.posZ, 10.0F, this.pigman.getFaceSpeed()
                    );
            }
        }
    }

    private class DetectPlayer extends DetectEnemy {
        private Pigman pig;

        private DetectPlayer(Mob target, Class<?> targetClass, float dist, int chance, boolean chase) {
            super(target, targetClass, dist, chance, chase);
            this.pig = (Pigman)target;
        }

        @Override
        public boolean canExecute() {
            return this.pig.angerLevel > 0 && super.canExecute();
        }
    }

    private class DistractedByItem extends Action {
        private Pigman mob;
        private DroppedItem target;
        private float dist;

        private DistractedByItem(Pigman mob, float dist) {
            this.mob = mob;
            this.dist = dist;
            this.setMutexBits(1);
        }

        @Override
        public void start() {
            this.mob.navigator.clearPath();
        }

        @Override
        public boolean canExecute() {
            this.target = this.getClosestIron(this.mob);
            if (this.target != null && !this.target.isDead && this.mob.item == null) {
                Path path = this.mob.navigator.pathToLocation(this.target.posX, this.target.posY, this.target.posZ);
                return path != null;
            } else {
                return false;
            }
        }

        @Override
        public boolean continueExecuting() {
            return this.target != null && !this.target.isDead && this.mob.item == null;
        }

        @Override
        public void tick() {
            if (this.mob.navigator.noPath()) {
                this.mob.navigator.moveToLocation(this.target.posX, this.target.posY, this.target.posZ, this.mob.moveSpeed + this.mob.moveSpeed / 2.0F);
            }
        }

        private DroppedItem getClosestIron(Entity entity) {
            double maxDistSq = this.dist * this.dist;
            DroppedItem item = null;
            List<Entity> entities = this.mob.world.getEntities(this.mob, this.mob.boundingBox.expand(this.dist, this.dist, this.dist));

            for (int i = 0; i < entities.size(); i++) {
                Entity entityFromList = entities.get(i);
                if (entityFromList instanceof DroppedItem) {
                    DroppedItem droppedItem = (DroppedItem)entityFromList;
                    if (droppedItem.item.getItem() == ItemList.ingotIron) {
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

    private class LookAroundIdle extends LookAround {
        private Pigman pigman;

        public LookAroundIdle(Mob host) {
            super(host);
            this.pigman = (Pigman)host;
        }

        @Override
        public boolean canExecute() {
            return this.pigman.item == null && super.canExecute();
        }
    }

    private class LookAtIdle extends LookAt {
        private Pigman pigman;

        public LookAtIdle(Mob host, Class<?> class2, float f) {
            super(host, class2, f);
            this.pigman = (Pigman)host;
        }

        @Override
        public boolean canExecute() {
            return this.pigman.item == null && super.canExecute();
        }
    }

    private static class LootHolder extends WeightedRandom.Choice {
        public final int itemId;

        public LootHolder(int weight, Item item) {
            super(weight);
            this.itemId = item.id;
        }

        public LootHolder(int weight, Block cock) {
            super(weight);
            this.itemId = cock.id;
        }
    }
}
