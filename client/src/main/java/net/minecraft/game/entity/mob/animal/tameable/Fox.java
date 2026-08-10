package net.minecraft.game.entity.mob.animal.tameable;

import com.mojang.nbt.tag.special.CompoundTag;
import java.util.List;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.Action;
import net.minecraft.game.entity.mob.ai.hostile.Attack;
import net.minecraft.game.entity.mob.ai.hostile.LeapAt;
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
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.util.math.MathHelper;

public class Fox extends TameableMob {
    public ItemStack heldItem;

    public Fox(World world) {
        super(world);
        this.tamedItem = ItemList.berry.id;
        this.moveSpeed = 0.3F;
        this.setSize(0.8F, 0.7F);
        this.setType();
        this.behaviors.addTask(1, new Swim(this));
        this.behaviors.addTask(2, this.sitAI);
        this.behaviors.addTask(2, new Fox.Sleep(this));
        this.behaviors.addTask(3, new LeapAt(this, 0.4F));
        this.behaviors.addTask(4, new Attack(this, this.moveSpeed, true, false));
        this.behaviors.addTask(5, new Fox.FindItem(this, 16.0F));
        this.behaviors.addTask(6, new FollowOwner(this, this.moveSpeed, 10.0F, 2.0F));
        this.behaviors.addTask(7, new WanderAround(this, this.moveSpeed, true));
        this.behaviors.addTask(8, new LookAt(this, Player.class, 8.0F));
        this.behaviors.addTask(9, new LookAround(this));
        this.detections.addTask(1, new ChooseWildTarget(this, Chicken.class, 16.0F, 16, false));
        this.detections.addTask(1, new ChooseWildTarget(this, Bunny.class, 16.0F, 16, false));
    }

    @Override
    public void setType() {
        switch (this.type) {
            case 1:
                this.texture = "/mob/fox/snow_fox.png";
                break;
            case 2:
                this.texture = "/mob/fox/desert_fox.png";
                break;
            default:
                this.texture = "/mob/fox/fox.png";
        }
    }

    @Override
    public void setTypeOnSpawn() {
        int x = MathHelper.floor_double(this.posX);
        int z = MathHelper.floor_double(this.posZ);
        Biome biome = this.world.getBiomeAt(x, z);
        if (biome.hasNoRain()) {
            this.type = 2;
        } else if (biome.hasSnow()) {
            this.type = 1;
        } else {
            this.type = 0;
        }

        this.setType();
    }

    @Override
    public int getSpawnID() {
        return 12;
    }

    @Override
    public boolean interact(Player player) {
        ItemStack heldItem = player.inventory.getCurrentItem();
        if (player.getUsing()) {
            heldItem = player.inventory.getOffHandItem();
        }

        if (!this.isTamed()) {
            return this.tameMob(player, heldItem);
        }

        if (this.heldItem != null) {
            if (!player.inventory.addItemToInventory(this.heldItem)) {
                DroppedItem item = new DroppedItem(this.world, this.posX, this.posY, this.posZ, this.heldItem);
                item.pickupDelay = 20;
                this.world.spawnEntity(item);
            }

            this.heldItem = null;
            return true;
        } else {
            return this.healMob(player, heldItem);
        }
    }

    @Override
    protected boolean validFood(Item food) {
        return food == ItemList.rabbitCooked || food == ItemList.rabbitRaw || food == ItemList.rottenFlesh;
    }

    @Override
    public void onCollideWith(Entity entity) {
        this.sleeping = false;
    }

    @Override
    public void onDeath(DamageSource entity) {
        super.onDeath(entity);
        if (this.heldItem != null) {
            DroppedItem drop = new DroppedItem(this.world, this.posX, this.posY - 0.3F, this.posZ, this.heldItem);
            drop.pickupDelay = 40;
            float offset = 0.1F;
            offset = 0.3F;
            drop.motionX = -MathHelper.sin(this.yaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.pitch / 180.0F * (float) Math.PI) * offset;
            drop.motionZ = MathHelper.cos(this.yaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.pitch / 180.0F * (float) Math.PI) * offset;
            drop.motionY = -MathHelper.sin(this.pitch / 180.0F * (float) Math.PI) * offset + 0.1F;
            offset = 0.02F;
            float angle = this.rand.nextFloat() * (float) Math.PI * 2.0F;
            offset *= this.rand.nextFloat();
            drop.motionX = drop.motionX + Math.cos(angle) * offset;
            drop.motionY = drop.motionY + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F;
            drop.motionZ = drop.motionZ + Math.sin(angle) * offset;
            this.world.spawnEntity(drop);
        }
    }

    @Override
    public boolean isInWater() {
        return this.world.handleWaterMovement(this.boundingBox, MaterialList.water, this);
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        tag.setBoolean("Sleeping", this.sleeping);
        if (this.heldItem != null) {
            tag.setCompoundTag("Item", this.heldItem.writeToNBT(new CompoundTag()));
        }
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        this.sleeping = tag.getBoolean("Sleeping");
        CompoundTag itemTag = tag.getCompoundTag("Item");
        if (itemTag.getShort("id") > 0) {
            this.heldItem = new ItemStack(itemTag);
        }
    }

    @Override
    public String getSound() {
        return this.sleeping && this.rand.nextInt(4) != 0 ? null : "mob.fox";
    }

    @Override
    protected String getHurtSound() {
        return "mob.foxhurt";
    }

    @Override
    protected String getDeathSound() {
        return "mob.foxdeath";
    }

    @Override
    public int getDrop() {
        return ItemList.leather.id;
    }

    private class FindItem extends Action {
        private Fox fox;
        private DroppedItem target;
        private float dist;

        private FindItem(Fox fox, float dist) {
            this.fox = fox;
            this.dist = dist;
            this.setMutexBits(1);
        }

        @Override
        public boolean canExecute() {
            this.target = this.getClosestItem(this.fox);
            return this.target != null && !this.target.isDead && this.fox.isTamed() && !this.fox.sitAI.getSitting() && this.fox.heldItem == null
                ? this.fox.navigator.pathToLocation(this.target.posX, this.target.posY, this.target.posZ) != null
                : false;
        }

        @Override
        public boolean continueExecuting() {
            return this.target != null && !this.target.isDead && this.fox.getDistanceSqTo(this.target) < this.dist * this.dist;
        }

        @Override
        public void tick() {
            if (this.fox.navigator.noPath()) {
                this.fox.navigator.moveToLocation(this.target.posX, this.target.posY, this.target.posZ, this.fox.moveSpeed);
            }
        }

        private DroppedItem getClosestItem(Entity entity) {
            double maxDistSq = this.dist * this.dist;
            DroppedItem item = null;
            List<Entity> entities = this.fox.world.getEntities(this.fox, this.fox.boundingBox.expand(this.dist, this.dist, this.dist));

            for (int i = 0; i < entities.size(); i++) {
                Entity entityFromList = entities.get(i);
                if (entityFromList instanceof DroppedItem) {
                    DroppedItem droppedItem = (DroppedItem)entityFromList;
                    double distToItem = droppedItem.getDistanceSq(entity.posX, entity.posY, entity.posZ);
                    if (distToItem < maxDistSq) {
                        item = droppedItem;
                    }
                }
            }

            return item;
        }
    }

    private class Sleep extends LookAround {
        Fox fox;

        private Sleep(Mob fox) {
            super(fox);
            this.fox = (Fox)fox;
        }

        @Override
        public boolean canExecute() {
            return this.host.navigator.noPath() && this.host.rand.nextFloat() < 0.002F && !this.host.isInWater() && !this.fox.isTamed() || this.host.sleeping;
        }

        @Override
        public boolean continueExecuting() {
            return this.host.sleeping && super.continueExecuting();
        }

        @Override
        public void start() {
            this.idleTime = 360 + this.host.rand.nextInt(1000);
            this.host.sleeping = true;
        }

        @Override
        public void stop() {
            this.host.sleeping = false;
        }

        @Override
        public void tick() {
            this.idleTime--;
        }
    }
}
