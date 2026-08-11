package net.minecraft.game.entity.mob.monster.arachnid;

import com.mojang.nbt.tag.special.CompoundTag;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.mob.ai.hostile.Aggro;
import net.minecraft.game.entity.mob.ai.hostile.Attack;
import net.minecraft.game.entity.mob.ai.hostile.DetectEnemy;
import net.minecraft.game.entity.mob.ai.passive.Swim;
import net.minecraft.game.entity.mob.ai.passive.WanderAround;
import net.minecraft.game.entity.mob.follower.Decoy;
import net.minecraft.game.entity.mob.monster.Monster;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.world.World;

public class Termite extends Monster {
    public boolean tamed = false;

    public Termite(World ass) {
        super(ass);
        this.setHealth(8);
        this.setSize(0.5F, 0.4F);
        this.texture = "/mob/silverfish/termite.png";
        this.moveSpeed = 0.32F;
        this.attackStrength = 1;
        this.produceFootsteps = false;
        this.behaviors.addTask(0, new Swim(this));
        this.behaviors.addTask(2, new Attack(this, this.moveSpeed, false, false));
        this.behaviors.addTask(5, new WanderAround(this, this.moveSpeed, false));
        this.detections.addTask(1, new Aggro(this, false));
        this.detections.addTask(2, new DetectEnemy(this, Decoy.class, 16.0F, 0, true));
        this.detections.addTask(3, new DetectEnemy(this, Player.class, 16.0F, 0, true));
    }

    public Termite(World world, boolean b) {
        this(world);
        this.scoreValue = 10;
        this.tamed = b;
        this.setHostileAI();
    }

    @Override
    public int getSpawnID() {
        return 44;
    }

    @Override
    public String getSound() {
        return "mob.silverfish";
    }

    @Override
    protected String getHurtSound() {
        return "mob.silverfishhurt";
    }

    @Override
    protected String getDeathSound() {
        return "mob.silverfishdeath";
    }

    @Override
    public float getSoundVolume() {
        return 0.4F;
    }

    @Override
    public float getPathWeight(int x, int y, int z) {
        return this.world.getBlockId(x, y - 1, z) == BlockList.stone.id ? 10.0F : super.getPathWeight(x, y, z);
    }

    @Override
    public final boolean isInWater() {
        return this.world.handleWaterMovement(this.boundingBox, MaterialList.water, this);
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        tag.setBoolean("Tamed", this.tamed);
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        this.tamed = tag.getBoolean("Tamed");
        this.setHostileAI();
    }

    private void setHostileAI() {
        this.detections.tasks.clear();
        this.detections.addTask(1, new Aggro(this, false));
        this.detections.addTask(2, new DetectEnemy(this, Decoy.class, 16.0F, 0, true));
        if (this.tamed) {
            this.detections.addTask(3, new DetectEnemy(this, Monster.class, 16.0F, 0, true));
        } else {
            this.detections.addTask(3, new DetectEnemy(this, Player.class, 16.0F, 0, true));
        }
    }
}
