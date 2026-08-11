package net.minecraft.game.entity.mob.monster;

import com.mojang.nbt.tag.special.CompoundTag;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.ai.hostile.Aggro;
import net.minecraft.game.entity.mob.ai.hostile.Attack;
import net.minecraft.game.entity.mob.ai.hostile.ChangeCourse;
import net.minecraft.game.entity.mob.ai.hostile.DetectEnemy;
import net.minecraft.game.entity.mob.ai.passive.LookAround;
import net.minecraft.game.entity.mob.ai.passive.LookAt;
import net.minecraft.game.entity.mob.ai.passive.Swim;
import net.minecraft.game.entity.mob.ai.passive.WanderAround;
import net.minecraft.game.entity.mob.follower.Decoy;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.potion.EffectList;
import net.minecraft.game.item.tools.Axe;
import net.minecraft.game.world.World;

public class Treant extends Monster {
    public Treant(World world) {
        super(world);
        this.setSize(1.2F, 4.0F);
        this.texture = "/mob/treant.png";
        this.attackStrength = 6;
        this.moveSpeed = 0.25F;
        this.scoreValue = 100;
        this.behaviors.addTask(0, new Swim(this));
        this.behaviors.addTask(2, new Attack(this, this.moveSpeed, false, EffectList.poison.id, 150, 2, false));
        this.behaviors.addTask(4, new ChangeCourse(this, this.moveSpeed));
        this.behaviors.addTask(6, new WanderAround(this, this.moveSpeed, true));
        this.behaviors.addTask(7, new LookAt(this, Player.class, 8.0F));
        this.behaviors.addTask(7, new LookAround(this));
        this.detections.addTask(1, new Aggro(this, false));
        this.detections.addTask(2, new DetectEnemy(this, Decoy.class, 16.0F, 0, true));
        this.detections.addTask(3, new DetectEnemy(this, Player.class, 16.0F, 0, true));
    }

    @Override
    public double getYOffset() {
        return -0.3;
    }

    @Override
    public int getSpawnID() {
        return 52;
    }

    @Override
    public float getSoundVolume() {
        return 0.4F;
    }

    @Override
    public boolean damageEntity(DamageSource src, int amount) {
        if (src.getEntity() instanceof Player
            && ((Player)src.getEntity()).inventory.getCurrentItem() != null
            && ((Player)src.getEntity()).inventory.getCurrentItem().getItem() instanceof Axe) {
            amount *= 2;
        }

        return super.damageEntity(src, amount);
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        tag.setBoolean("Boss", this.boss);
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        this.boss = tag.getBoolean("Boss");
    }

    @Override
    public int getDrop() {
        return BlockList.wood.id;
    }

    @Override
    public final String getSound() {
        return "mob.treant";
    }

    @Override
    protected final String getHurtSound() {
        return "mob.treanthurt";
    }

    @Override
    protected final String getDeathSound() {
        return "mob.treantdeath";
    }
}
