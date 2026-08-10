package net.minecraft.game.entity.mob.animal;

import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.mob.CreatureType;
import net.minecraft.game.entity.mob.ai.hostile.Aggro;
import net.minecraft.game.entity.mob.ai.hostile.Attack;
import net.minecraft.game.entity.mob.ai.hostile.ChangeCourse;
import net.minecraft.game.entity.mob.ai.passive.LookAround;
import net.minecraft.game.entity.mob.ai.passive.LookAt;
import net.minecraft.game.entity.mob.ai.passive.Swim;
import net.minecraft.game.entity.mob.ai.passive.WanderAround;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public class Crab extends Animal {
    public Crab(World world) {
        super(world);
        this.texture = "/mob/crab.png";
        this.setSize(0.7F, 0.4F);
        this.setHealth(6);
        this.moveSpeed = 0.25F;
        this.scoreValue = 30;
        this.behaviors.addTask(0, new Swim(this));
        this.behaviors.addTask(2, new Attack(this, this.moveSpeed + this.moveSpeed / 2.0F, false, false));
        this.behaviors.addTask(4, new ChangeCourse(this, this.moveSpeed));
        this.behaviors.addTask(6, new WanderAround(this, this.moveSpeed, true));
        this.behaviors.addTask(7, new LookAt(this, Player.class, 8.0F));
        this.behaviors.addTask(7, new LookAround(this));
        this.detections.addTask(1, new Aggro(this, false));
    }

    @Override
    public int getSpawnID() {
        return 38;
    }

    @Override
    public CreatureType getCreatureType() {
        return CreatureType.ARTHROPOD;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public String getSound() {
        return null;
    }

    @Override
    protected final String getHurtSound() {
        return "mob.spider";
    }

    @Override
    protected String getDeathSound() {
        return "mob.silverfishdeath";
    }

    @Override
    public void playStepSound() {
        this.world.playEntitySound(this, "mob.crabstep", 0.15F, 1.0F);
    }

    @Override
    public float getSoundVolume() {
        return 0.6F;
    }

    @Override
    public int specialDrop() {
        return ItemList.claw.id;
    }

    @Override
    public boolean isInWater() {
        return this.world.handleWaterMovement(this.boundingBox, MaterialList.water, this);
    }

    @Override
    public float getPathWeight(int xCoord, int yCoord, int zCoord) {
        return this.world.getBlockId(xCoord, yCoord - 1, zCoord) == BlockList.sand.id ? 10.0F : this.world.getBrightness(xCoord, yCoord, zCoord) - 0.5F;
    }

    @Override
    public boolean canSpawnHere() {
        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_double(this.boundingBox.minY);
        int z = MathHelper.floor_double(this.posZ);
        return this.world.getBlockId(x, y - 1, z) == BlockList.sand.id
            && this.world.getBlockBrightness(x, y, z) > 8
            && this.getPathWeight(x, y, z) >= 0.0F
            && this.world.isClear(this.boundingBox)
            && this.world.getBoundingBoxes(this, this.boundingBox).size() == 0
            && !this.world.containsLiquid(this.boundingBox);
    }
}
