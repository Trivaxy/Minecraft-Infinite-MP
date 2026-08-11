package net.minecraft.game.entity.mob.animal.flying;

import java.util.List;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.flying.FlyAtRandom;
import net.minecraft.game.entity.mob.ai.flying.LookAroundFlying;
import net.minecraft.game.entity.mob.ai.flying.LookAtOnGround;
import net.minecraft.game.entity.mob.ai.passive.LookAround;
import net.minecraft.game.entity.mob.ai.passive.Swim;
import net.minecraft.game.entity.mob.ai.passive.WanderAround;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public class Parrot extends FlyingMob {
    public Parrot(World world) {
        super(world);
        this.setSize(0.5F, 0.9F);
        this.setHealth(6);
        this.moveSpeed = 0.32F;
        this.type = (byte)this.rand.nextInt(5);
        this.setType();
        this.behaviors.addTask(1, new Swim(this));
        this.behaviors.addTask(2, new FlyAtRandom(this, 6.0F, 0.13F));
        this.behaviors.addTask(3, new LookAroundFlying(this, false));
        this.behaviors.addTask(4, new WanderAround(this, this.moveSpeed, true));
        this.behaviors.addTask(5, new LookAtOnGround(this, Player.class, 6.0F));
        this.behaviors.addTask(6, new LookAround(this));
    }

    @Override
    public void mobTick() {
        super.mobTick();
        if (this.navigator.noPath() && this.onGround && this.rand.nextInt(100) == 0) {
            this.flying = true;
            this.moveControl = this.flyMovement;
        }
    }

    @Override
    public void setType() {
        switch (this.type) {
            case 1:
                this.texture = "/mob/parrot/parrot_grey.png";
                break;
            case 2:
                this.texture = "/mob/parrot/parrot_red.png";
                break;
            case 3:
                this.texture = "/mob/parrot/parrot_yellow.png";
                break;
            case 4:
                this.texture = "/mob/parrot/parrot_blue.png";
                break;
            default:
                this.texture = "/mob/parrot/parrot_green.png";
        }
    }

    @Override
    public int getSpawnID() {
        return 24;
    }

    @Override
    public float getSoundVolume() {
        return 0.4F;
    }

    @Override
    public String getSound() {
        return "mob.birdgreen";
    }

    @Override
    protected String getHurtSound() {
        return "mob.birdgreen";
    }

    @Override
    protected String getDeathSound() {
        return "mob.birdgreen";
    }

    @Override
    public void playStepSound() {
        this.world.playEntitySound(this, "mob.chickenstep", 0.15F, 1.0F);
    }

    @Override
    public void playSound() {
        List<Entity> nearbyEntities = this.world.getEntities(this, this.boundingBox.expand(20.0, 8.0, 20.0));
        if (!nearbyEntities.isEmpty()) {
            Entity target = nearbyEntities.get(this.world.rand.nextInt(nearbyEntities.size()));
            if (target instanceof Mob) {
                Mob mob = (Mob)target;
                String sound = mob.getSound();
                if (sound != null) {
                    this.world.playEntitySound(this, sound, mob.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 2.0F);
                }
            } else {
                super.playSound();
            }
        } else {
            super.playSound();
        }
    }

    @Override
    public int getDrop() {
        return ItemList.feather.id;
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
    public double getYOffset() {
        return 0.3;
    }

    @Override
    public boolean interact(Player player) {
        this.mountEntity(player);
        return true;
    }
}
