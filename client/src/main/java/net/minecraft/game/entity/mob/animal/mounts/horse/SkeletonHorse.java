package net.minecraft.game.entity.mob.animal.mounts.horse;

import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.mob.CreatureType;
import net.minecraft.game.entity.mob.ai.hostile.Aggro;
import net.minecraft.game.entity.mob.ai.hostile.Attack;
import net.minecraft.game.entity.mob.ai.hostile.DetectEnemy;
import net.minecraft.game.entity.mob.follower.Decoy;
import net.minecraft.game.entity.mob.monster.Monster;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public class SkeletonHorse extends Horse {
    public SkeletonHorse(World world) {
        super(world);
        this.tamedItem = BlockList.mushroomNylium.id;
        this.behaviors.addTask(3, new Attack(this, this.moveSpeed + this.moveSpeed / 2.0F, false, false));
    }

    @Override
    public void setType() {
        this.texture = "/mob/horse/horse_skeleton.png";
    }

    @Override
    public int getSpawnID() {
        return 41;
    }

    @Override
    public void updateAI() {
        if (this.rider != null && this.rider instanceof Monster) {
            if (this.rider.isDead) {
                this.navigator.clearPath();
                this.detections.tasks.clear();
                this.attackTarget = null;
            }

            this.AIOverride();
        } else {
            super.updateAI();
        }
    }

    @Override
    public boolean canBurn() {
        return false;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public CreatureType getCreatureType() {
        return CreatureType.UNDEAD;
    }

    @Override
    public int getDrop() {
        return ItemList.bone.id;
    }

    @Override
    public float getPathWeight(int xCoord, int yCoord, int zCoord) {
        return this.world.getBlockId(xCoord, yCoord - 1, zCoord) != BlockList.grass.id
                && this.world.getBlockId(xCoord, yCoord - 1, zCoord) != BlockList.slowSand.id
                && this.world.getBlockId(xCoord, yCoord - 1, zCoord) != BlockList.soulSoil.id
            ? this.world.getBrightness(xCoord, yCoord, zCoord) - 0.5F
            : 10.0F;
    }

    @Override
    public boolean canSpawnHere() {
        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_double(this.boundingBox.minY);
        int z = MathHelper.floor_double(this.posZ);
        return (
                this.world.getBlockId(x, y - 1, z) == BlockList.grass.id
                    || this.world.getBlockId(x, y - 1, z) == BlockList.slowSand.id
                    || this.world.getBlockId(x, y - 1, z) == BlockList.soulSoil.id
            )
            && this.world.isClear(this.boundingBox)
            && this.world.getBoundingBoxes(this, this.boundingBox).size() == 0
            && !this.world.containsLiquid(this.boundingBox);
    }

    @Override
    public boolean canBeSlowed() {
        return false;
    }

    @Override
    protected void fall(float dist) {
    }

    public void setHostile() {
        this.behaviors.tasks.remove(1);
        this.detections.addTask(1, new Aggro(this, false));
        this.detections.addTask(2, new DetectEnemy(this, Decoy.class, 16.0F, 0, true));
        this.detections.addTask(3, new DetectEnemy(this, Player.class, 16.0F, 0, true));
    }
}
