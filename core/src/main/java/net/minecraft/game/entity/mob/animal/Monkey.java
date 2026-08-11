package net.minecraft.game.entity.mob.animal;

import com.mojang.nbt.tag.special.CompoundTag;
import java.util.List;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.hostile.Aggro;
import net.minecraft.game.entity.mob.ai.hostile.Attack;
import net.minecraft.game.entity.mob.ai.hostile.ChangeCourse;
import net.minecraft.game.entity.mob.ai.hostile.DetectEnemy;
import net.minecraft.game.entity.mob.ai.hostile.RangedAttack;
import net.minecraft.game.entity.mob.ai.passive.FleeFrom;
import net.minecraft.game.entity.mob.ai.passive.LookAround;
import net.minecraft.game.entity.mob.ai.passive.LookAt;
import net.minecraft.game.entity.mob.ai.passive.Swim;
import net.minecraft.game.entity.mob.ai.passive.WanderAround;
import net.minecraft.game.entity.mob.follower.Decoy;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public class Monkey extends Animal {
    public ItemStack item;
    public int angerLevel = 0;

    public Monkey(World world) {
        super(world);
        this.setSize(0.6F, 0.8F);
        this.setHealth(8);
        this.moveSpeed = 0.33F;
        this.scoreValue = 50;
        this.type = (byte)this.rand.nextInt(1000);
        this.setType();
        this.behaviors.addTask(1, new Swim(this));
        this.behaviors.addTask(2, new Monkey.FleeWithItem(this, Player.class, 16.0F, this.moveSpeed, this.moveSpeed));
        this.behaviors.addTask(3, new Monkey.StealItem(this, this.moveSpeed, false, false));
        this.behaviors.addTask(4, new RangedAttack(this, this.moveSpeed, -3, 20));
        this.behaviors.addTask(5, new ChangeCourse(this, this.moveSpeed));
        this.behaviors.addTask(6, new WanderAround(this, this.moveSpeed, true));
        this.behaviors.addTask(7, new LookAt(this, Player.class, 8.0F));
        this.behaviors.addTask(7, new LookAround(this));
        this.detections.addTask(1, new Aggro(this, true));
        this.detections.addTask(2, new Monkey.DetectArmedPlayer(this, Decoy.class, 16.0F, 0, true));
        this.detections.addTask(3, new Monkey.DetectArmedPlayer(this, Player.class, 16.0F, 0, true));
    }

    @Override
    public void setType() {
        if (this.type == 1) {
            this.texture = "/mob/monkey/duende.png";
        } else {
            this.texture = "/mob/monkey/monkey.png";
        }
    }

    @Override
    public int getSpawnID() {
        return 28;
    }

    @Override
    public void onDeath(DamageSource src) {
        super.onDeath(src);
        if (this.item != null) {
            DroppedItem drop = new DroppedItem(this.world, this.posX, this.posY - 0.3F, this.posZ, this.item);
            drop.pickupDelay = 40;
            float offset = 0.1F;
            float angle = this.rand.nextFloat() * (float) Math.PI * 2.0F;
            offset = 0.3F;
            drop.motionX = -MathHelper.sin(this.yaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.pitch / 180.0F * (float) Math.PI) * offset;
            drop.motionZ = MathHelper.cos(this.yaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.pitch / 180.0F * (float) Math.PI) * offset;
            drop.motionY = -MathHelper.sin(this.pitch / 180.0F * (float) Math.PI) * offset + 0.1F;
            offset = 0.02F;
            offset *= this.rand.nextFloat();
            drop.motionX = drop.motionX + Math.cos(angle) * offset;
            drop.motionY = drop.motionY + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F;
            drop.motionZ = drop.motionZ + Math.sin(angle) * offset;
            this.world.spawnEntity(drop);
        }
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

    @Override
    public String getSound() {
        return this.type == 1 ? "mob.duende" : "mob.monkey";
    }

    @Override
    protected String getHurtSound() {
        return this.type == 1 ? "mob.duendehurt" : "mob.monkeyhurt";
    }

    @Override
    protected String getDeathSound() {
        return this.type == 1 ? "mob.duendehurt" : "mob.monkeyhurt";
    }

    @Override
    public void playStepSound() {
        this.world.playEntitySound(this, "mob.rabbithop", 0.75F, 1.0F);
    }

    @Override
    public float getSoundVolume() {
        return 0.4F;
    }

    @Override
    public float getEyeHeight() {
        return this.height * 0.8F;
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

    @Override
    public boolean damageEntity(DamageSource src, int amount) {
        Entity target = src.getEntity();
        if (target instanceof Player) {
            List<Entity> entities = this.world.getEntities(this, this.boundingBox.expand(32.0, 32.0, 32.0));

            for (int i = 0; i < entities.size(); i++) {
                Entity monke = entities.get(i);
                if (monke instanceof Monkey) {
                    ((Monkey)monke).angerLevel = 400 + this.rand.nextInt(400);
                }
            }

            this.angerLevel = 400 + this.rand.nextInt(400);
        }

        return super.damageEntity(src, amount);
    }

    private class DetectArmedPlayer extends DetectEnemy {
        private Monkey monkey;

        private DetectArmedPlayer(Mob target, Class<?> targetClass, float dist, int chance, boolean chase) {
            super(target, targetClass, dist, chance, chase);
            this.monkey = (Monkey)target;
        }

        @Override
        public boolean canExecute() {
            return (this.monkey.item == null && this.monkey.rand.nextInt(500) == 0 && this.monkey.lastTarget == null || this.monkey.angerLevel > 0)
                && super.canExecute();
        }
    }

    private class FleeWithItem extends FleeFrom {
        private Monkey monkey;

        private FleeWithItem(Mob host, Class<?> class2, float r, float max, float min) {
            super(host, class2, r, max, min);
            this.monkey = (Monkey)host;
        }

        @Override
        public boolean canExecute() {
            return this.monkey.item != null && super.canExecute();
        }
    }

    private class StealItem extends Attack {
        private Monkey monkey;

        private StealItem(Mob src, float spd, boolean priority, boolean sprint) {
            super(src, spd, priority, sprint);
            this.monkey = (Monkey)src;
        }

        @Override
        public boolean canExecute() {
            return this.monkey.item == null && this.monkey.lastTarget == null && this.monkey.angerLevel == 0 && super.canExecute();
        }

        @Override
        protected void attack() {
            super.attack();
            if (this.target instanceof Player) {
                Player player = (Player)this.target;
                if (player.inventory.getCurrentItem() != null) {
                    this.monkey.item = player.inventory.getCurrentItem();
                    player.inventory.setSlotContents(player.inventory.currentSlot, null);
                    this.monkey
                        .world
                        .playEntitySound(this.monkey, "mob.chickenplop", 1.0F, (this.monkey.rand.nextFloat() - this.monkey.rand.nextFloat()) * 0.2F + 1.0F);
                }

                this.monkey.attackTarget = null;
            }
        }
    }
}
