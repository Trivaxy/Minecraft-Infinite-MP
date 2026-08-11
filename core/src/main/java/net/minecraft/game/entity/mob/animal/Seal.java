package net.minecraft.game.entity.mob.animal;

import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.mob.ai.flying.LookAroundFlying;
import net.minecraft.game.entity.mob.ai.flying.SwimAround;
import net.minecraft.game.entity.mob.ai.passive.LookAt;
import net.minecraft.game.entity.mob.ai.passive.Panic;
import net.minecraft.game.entity.mob.ai.passive.WanderAround;
import net.minecraft.game.entity.mob.ai.util.FlightControl;
import net.minecraft.game.entity.mob.ai.util.MoveControl;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public class Seal extends Animal {
    private MoveControl landMovement = new MoveControl(this);
    private FlightControl seaMovement = new FlightControl(this);

    public Seal(World world) {
        super(world);
        this.setSize(1.3F, 0.5F);
        this.produceFootsteps = false;
        this.moveSpeed = 0.12F;
        this.type = (byte)this.rand.nextInt(5);
        this.setType();
        this.behaviors.addTask(1, new Panic(this, this.moveSpeed * 2.0F));
        this.behaviors.addTask(3, new SwimAround(this, 6.0F, 0.1F));
        this.behaviors.addTask(4, new LookAroundFlying(this, false));
        this.behaviors.addTask(5, new WanderAround(this, this.moveSpeed, true));
        this.behaviors.addTask(6, new LookAt(this, Player.class, 6.0F));
    }

    @Override
    public void setType() {
        if (this.type == 1) {
            this.texture = "/mob/seal/seal_white.png";
        } else {
            this.texture = "/mob/seal/seal.png";
        }
    }

    @Override
    public void mobTick() {
        super.mobTick();
        if (!this.isInWater() && this.onGround) {
            this.moveControl = this.landMovement;
        } else {
            this.moveControl = this.seaMovement;
        }
    }

    @Override
    public int getSpawnID() {
        return 34;
    }

    @Override
    public float getPathWeight(int xCoord, int yCoord, int zCoord) {
        return this.world.getBlockId(xCoord, yCoord - 1, zCoord) != BlockList.snowBlock.id
                && this.world.getBlockId(xCoord, yCoord - 1, zCoord) != BlockList.silverSand.id
            ? this.world.getBrightness(xCoord, yCoord, zCoord) - 0.5F
            : 10.0F;
    }

    @Override
    public boolean canSpawnHere() {
        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_double(this.boundingBox.minY);
        int z = MathHelper.floor_double(this.posZ);
        return (this.world.getBlockId(x, y - 1, z) == BlockList.silverSand.id || this.world.getBlockId(x, y - 1, z) == BlockList.snowBlock.id)
            && this.world.getBlockBrightness(x, y, z) > 8
            && this.world.isClear(this.boundingBox)
            && this.world.getBoundingBoxes(this, this.boundingBox).size() == 0
            && !this.world.containsLiquid(this.boundingBox);
    }

    @Override
    public String getSound() {
        return "mob.seal";
    }

    @Override
    protected String getHurtSound() {
        return "mob.seal";
    }

    @Override
    protected String getDeathSound() {
        return "mob.seal";
    }

    @Override
    public float getSoundVolume() {
        return 0.4F;
    }

    @Override
    public int getDrop() {
        return ItemList.leather.id;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public boolean isInWater() {
        return this.world.handleWaterMovement(this.boundingBox, MaterialList.water, this);
    }

    private void moveSwimming(float x, float z) {
        float spdFactor = 0.91F;
        if (this.onGround) {
            spdFactor = 0.54600006F;
        }

        float spd = 0.16277136F / (spdFactor * spdFactor * spdFactor);
        this.moveFlying(x, z, this.onGround ? 0.1F * spd : 0.02F);
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= spdFactor;
        this.motionY *= spdFactor;
        this.motionZ *= spdFactor;
    }

    @Override
    public void moveEntityWithHeading(float x, float y) {
        if (this.isDead || this.health <= 0 || !this.isInWater()) {
            super.moveEntityWithHeading(x, y);
        } else if (this.isInWater()) {
            this.moveSwimming(x, y);
        } else if (this.isInLava()) {
            this.moveFlying(x, y, 0.02F);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.5;
            this.motionY *= 0.5;
            this.motionZ *= 0.5;
        } else {
            this.moveSwimming(x, y);
            this.motionY = (float)(this.motionY - 0.8);
        }
    }
}
