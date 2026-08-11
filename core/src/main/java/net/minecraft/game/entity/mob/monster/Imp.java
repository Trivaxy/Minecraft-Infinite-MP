package net.minecraft.game.entity.mob.monster;

import com.mojang.nbt.tag.special.CompoundTag;
import net.minecraft.game.ParticleType;
import net.minecraft.game.entity.mob.CreatureType;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.ai.hostile.Aggro;
import net.minecraft.game.entity.mob.ai.hostile.DetectEnemy;
import net.minecraft.game.entity.mob.ai.hostile.RangedAttack;
import net.minecraft.game.entity.mob.ai.passive.LookAround;
import net.minecraft.game.entity.mob.ai.passive.LookAt;
import net.minecraft.game.entity.mob.ai.passive.Swim;
import net.minecraft.game.entity.mob.ai.passive.WanderAround;
import net.minecraft.game.entity.mob.follower.Decoy;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.world.World;

public class Imp extends Monster {
    public boolean tamed = false;

    public Imp(World world) {
        super(world);
        this.setSize(0.8F, 1.5F);
        this.texture = "/mob/imp.png";
        this.moveSpeed = 0.3F;
        this.scoreValue = 120;
        this.behaviors.addTask(1, new Swim(this));
        this.behaviors.addTask(2, new RangedAttack(this, this.moveSpeed, -2, 30));
        this.behaviors.addTask(3, new WanderAround(this, this.moveSpeed, true));
        this.behaviors.addTask(4, new LookAt(this, Player.class, 8.0F));
        this.behaviors.addTask(5, new LookAround(this));
        this.detections.addTask(1, new Aggro(this, false));
        this.detections.addTask(2, new DetectEnemy(this, Decoy.class, 16.0F, 0, true));
        this.detections.addTask(3, new DetectEnemy(this, Player.class, 16.0F, 0, true));
    }

    public Imp(World world, boolean b) {
        this(world);
        this.scoreValue = 10;
        this.tamed = b;
        this.setHostileAI();
    }

    @Override
    public CreatureType getCreatureType() {
        return CreatureType.FLAME;
    }

    @Override
    public void mobTick() {
        super.mobTick();
        this.fire = 0;
        if (this.isInWater()) {
            this.damageEntity(DamageSource.drown, 1);
        }

        this.world
            .spawnParticle(
                ParticleType.FLAME,
                this.posX + this.rand.nextDouble() * this.width * 2.0 - this.width,
                this.posY + this.rand.nextDouble() * this.height,
                this.posZ + this.rand.nextDouble() * this.width * 2.0 - this.width,
                0.0,
                0.0,
                0.0
            );
        this.world
            .spawnParticle(
                ParticleType.SMOKE,
                this.posX + this.rand.nextDouble() * this.width * 2.0 - this.width,
                this.posY + this.rand.nextDouble() * this.height,
                this.posZ + this.rand.nextDouble() * this.width * 2.0 - this.width,
                0.0,
                0.0,
                0.0
            );
    }

    @Override
    public boolean canBurn() {
        return false;
    }

    @Override
    public int getSpawnID() {
        return 56;
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

    @Override
    public final String getSound() {
        return "mob.imp";
    }

    @Override
    protected final String getHurtSound() {
        return "mob.imphurt";
    }

    @Override
    protected final String getDeathSound() {
        return "mob.imphurt";
    }

    @Override
    public int getDrop() {
        return ItemList.ash.id;
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
