package net.minecraft.game.entity.mob.animal;

import net.minecraft.game.block.BlockList;
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
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.cold.ColdBiome;
import net.minecraft.game.world.biomes.temperate.Mountains;
import net.minecraft.game.world.biomes.warm.Jungle;
import net.minecraft.util.math.MathHelper;

public class Bear extends Animal {
    public Bear(World world) {
        super(world);
        this.setSize(1.1F, 1.4F);
        this.setHealth(30);
        this.moveSpeed = 0.23F;
        this.attackStrength = 6;
        this.scoreValue = 50;
        this.texture = "/mob/bear/bear.png";
        this.behaviors.addTask(0, new Swim(this));
        this.behaviors.addTask(2, new Attack(this, this.moveSpeed + this.moveSpeed / 2.0F, false, false));
        this.behaviors.addTask(4, new ChangeCourse(this, this.moveSpeed));
        this.behaviors.addTask(6, new WanderAround(this, this.moveSpeed, true));
        this.behaviors.addTask(7, new LookAt(this, Player.class, 8.0F));
        this.behaviors.addTask(7, new LookAround(this));
        this.detections.addTask(1, new Aggro(this, false));
    }

    @Override
    public void setType() {
        switch (this.type) {
            case 1:
                this.texture = "/mob/bear/bear_black.png";
                break;
            case 2:
                this.texture = "/mob/bear/polar_bear.png";
                break;
            case 3:
                this.texture = "/mob/bear/panda_bear.png";
                break;
            default:
                this.texture = "/mob/bear/bear.png";
        }
    }

    @Override
    public void setTypeOnSpawn() {
        int x = MathHelper.floor_double(this.posX);
        int z = MathHelper.floor_double(this.posZ);
        Biome biome = this.world.getBiomeAt(x, z);
        if (biome instanceof ColdBiome) {
            this.type = 2;
        } else if (biome instanceof Jungle) {
            this.type = 3;
        } else if (biome instanceof Mountains) {
            this.type = 1;
        }

        this.setType();
    }

    @Override
    public int getSpawnID() {
        return 27;
    }

    @Override
    public String getSound() {
        return "mob.bear";
    }

    @Override
    protected String getHurtSound() {
        return "mob.bearhurt";
    }

    @Override
    protected String getDeathSound() {
        return "mob.beardeath";
    }

    @Override
    public void playStepSound() {
        this.world.playEntitySound(this, "mob.cowstep", 0.15F, 1.0F);
    }

    @Override
    public float getSoundVolume() {
        return 0.8F;
    }

    @Override
    public int getDrop() {
        return ItemList.leather.id;
    }

    @Override
    public int specialDrop() {
        return ItemList.paw.id;
    }

    @Override
    public int getDropCount() {
        return this.rand.nextInt(3) + 1;
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
}
